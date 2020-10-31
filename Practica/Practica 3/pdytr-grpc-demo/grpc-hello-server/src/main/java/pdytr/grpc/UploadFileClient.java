package pdytr.grpc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class UploadFileClient {
	private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
	private static final int PORT = 50051;
	public static String src = "";
	public static String dest = "";
	public static String host = "";
	public static Boolean isSetBytesFlag = false;
	public static Boolean listView = false;
	public static Integer bytes = 0;
	public static Integer initialPosition = 0;

	private final ManagedChannel mChannel;
	private final FileServiceGrpc.FileServiceBlockingStub mBlockingStub;
	private final FileServiceGrpc.FileServiceStub mAsyncStub;

	public UploadFileClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				// Channels are secure by default (via SSL/TLS). For the example we disable TLS
				// to avoid
				// needing certificates.
				.usePlaintext(true).build());
	}

	UploadFileClient(ManagedChannel channel) {
		this.mChannel = channel;
		mBlockingStub = FileServiceGrpc.newBlockingStub(channel);
		mAsyncStub = FileServiceGrpc.newStub(channel);
	}

	public void shutdown() throws InterruptedException {
		mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void startStream(final String src, final String dest) {
		logger.info("tid: " + Thread.currentThread().getId() + ", Will try to writeFile");
		StreamObserver<WriteResponse> responseObserver = new StreamObserver<WriteResponse>() {

			@Override
			public void onNext(WriteResponse value) {
				logger.info("Client response onNext");
			}

			@Override
			public void onError(Throwable t) {
				logger.info("Client response onError");
			}

			@Override
			public void onCompleted() {
				logger.info("Client response onCompleted");
			}
		};

		StreamObserver<WriteRequest> requestObserver = mAsyncStub.writeFile(responseObserver);

		try {
			File file = new File(src);
			if (file.exists() == false) {
				logger.info("File does not exist");
				return;
			}
			try {
				BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));
				int bufferSize = 512 * 1024; // 512k
				byte[] buffer = new byte[bufferSize];
				int size = 0;
				while ((size = bInputStream.read(buffer)) > 0) {
					ByteString byteString = ByteString.copyFrom(buffer, 0, size);
					WriteRequest req = WriteRequest.newBuilder().setSrc(src).setDest(dest).setData(byteString).setOffset(size)
							.build();
					requestObserver.onNext(req);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			requestObserver.onError(e);
			throw e;
		}
		requestObserver.onCompleted();
	}

	public static void initParams(String[] argv) {
		int c;
		String arg;
		LongOpt[] longopts = new LongOpt[8];
		StringBuffer listViewSb = new StringBuffer("0");

		longopts[0] = new LongOpt("all", LongOpt.NO_ARGUMENT, listViewSb, '1');
		longopts[1] = new LongOpt("bytes", LongOpt.REQUIRED_ARGUMENT, null, 'a');
		longopts[2] = new LongOpt("pos", LongOpt.REQUIRED_ARGUMENT, null, 'p');
		longopts[3] = new LongOpt("host", LongOpt.REQUIRED_ARGUMENT, null, 'h');
		longopts[4] = new LongOpt("src", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[5] = new LongOpt("dest", LongOpt.REQUIRED_ARGUMENT, null, 'd');

		Getopt g = new Getopt("flags", argv, "vbls:d:h:a:p:", longopts);
		g.setOpterr(false);

		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 0:
				/* If this option set a flag, do nothing else now. */
				arg = g.getOptarg();
				if (new Integer(longopts[g.getLongind()].getFlag().toString()) != 0)
					break;

				System.out.println("option %s" + longopts[g.getLongind()].getName() + " with arg %s"
						+ ((arg != null) ? arg : "null"));
				break;
			case 'l':
				listViewSb = new StringBuffer("1");
				break;
			case 's':
				src = g.getOptarg();
				break;
			case 'd':
				dest = g.getOptarg();
				break;
			case 'h':
				host = g.getOptarg();
				break;
			case 'a':
				isSetBytesFlag = true;
				bytes = new Integer(g.getOptarg());
				break;
			case 'p':
				initialPosition = new Integer(g.getOptarg());
				break;
			case '?':
				/* getopt_long already printed an error message. */
				break;
			default:
				System.exit(0);
			}
		}

		listViewSb.trimToSize();

		listView = new Integer(listViewSb.toString()) != 0;

		for (int i = g.getOptind() + 1; i < argv.length; i++) {
			System.out.println("Non option argv element: " + argv[i] + "\n");
		}
	}

	public static void main(String[] argv) throws Exception {
		long startTime, endTime;

		/* Look for hostname and msg length in the command line */
		if (argv.length < 1) {
			System.out.printf("Utilizacion: cliente\n"
					+ "\t- write or add --src <local> --dest <remote>: Agrega un archivo de <local> a <remote> \n"
					+ "\t- read or get --dest <local> --src <remote>: Almacene un archivo de <local> en <remote>\n"
					+ "\t- ls or list --src <remote/directory>: List files from <remote/directory>\n"
					+ "\t- rw or readwrite --src <local> --dest <remote>: Almacena un archivo de <local> a <remote> y realiza una copia de seguridad en el servidor \n");
			System.exit(1);
		}

		String command = argv[0];
		initParams(argv);

		if (src.isEmpty()) {
			String[] commandsWithSrc = { "write", "add", "read", "get" };

			for (String commandWithSrc : commandsWithSrc) {
				if (command.equals(commandWithSrc)) {
					System.err.println("Error: Especifique una ruta --src");
					System.exit(1);
				}
			}
		}
		// el archivo destino se llama por default tmp1
		if (dest.isEmpty()) {
			System.err.println("Advertencia: --dest fue setteado a tmp1");

			dest = "tmp1";
		}
		// setea localhost como host por default localhost
		if (host.isEmpty())
			host = "localhost";
		UploadFileClient client = new UploadFileClient(host, PORT);
		try {
			switch (command) {
			case "read":
			case "get":
				startTime = System.nanoTime();
				// read(remote);
				endTime = System.nanoTime();

				System.out.printf("Tomo: %d ms\n", (endTime - startTime) / 1000000);
				break;

			case "write":
			case "add":
				startTime = System.nanoTime();
				client.startStream(src, dest);
				endTime = System.nanoTime();

				System.out.printf("Tomo: %d ms\n", (endTime - startTime) / 1000000);
				break;

			case "time":
				startTime = System.nanoTime();
				// remote.time();
				endTime = System.nanoTime();

				System.out.println(endTime - startTime);
				break;

			case "timeout":
				System.out.println("Ejecutando el comando: timeout...");

				// Boolean ret = remote.timeout();

				// if (ret)
				// System.out.println("Completado!");
				// else
				// System.out.println("Timeout excedido");

				break;

			default:
				System.err.println("Comando no disponible");
				break;
			}
		} catch (Exception e) {
			System.err.println("Excepcion general!");
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			client.shutdown();
		}
	}
}
