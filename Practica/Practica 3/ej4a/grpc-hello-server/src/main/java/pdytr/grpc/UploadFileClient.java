package pdytr.grpc;

import io.grpc.*;
import java.io.*;
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

import java.io.DataOutputStream;
import java.io.FileOutputStream;


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
	private static FileOutputStream out;
	private static DataOutputStream stream;

	private final ManagedChannel mChannel;
	private final FileServiceGrpc.FileServiceStub mAsyncStub;
	private final FileServiceGrpc.FileServiceBlockingStub  mBlockingStub;

	public UploadFileClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				// Channels are secure by default (via SSL/TLS). For the example we disable TLS
				// to avoid
				// needing certificates.
				.usePlaintext(true).build());
	}

	UploadFileClient(ManagedChannel channel) {
		this.mChannel = channel;
		// Stub 
		mAsyncStub = FileServiceGrpc.newStub(channel);
		// Block Stub
		mBlockingStub = FileServiceGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public void writeStream(final String src, final String dest) {
		logger.info("tid: " + Thread.currentThread().getId() + ", Will try to writeFile");
		StreamObserver<WriteResponse> responseObserver = new StreamObserver<WriteResponse>() {

			@Override
			public void onNext(WriteResponse res) {
				System.out.println("------- RESPONSE: " + res.getBytesWrite() + " bytes write");
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
					WriteRequest req = WriteRequest.newBuilder().setDest(dest).setData(byteString).setOffset(size)
							.build();
					requestObserver.onNext(req);
				}
				bInputStream.close();
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

	public void readFile(final String src, final String dest) {
		logger.info("tid: " + Thread.currentThread().getId() + ", Will try to readFile");
		
		try{
		// destino _ source
		// ej archivo cancion.mp4> src , dest: prueba, imprime: prueba_cancion.mp4
			// dest vacio = tmp1 puesto en gnu.
			File file = new File(dest + "_" + src);
			file.createNewFile();
			out = new FileOutputStream(file,true);
			stream = new DataOutputStream(out);
            int bufferSize = 512 * 1024; // 512k
			byte[] buffer = new byte[bufferSize];
			int totalBytesRead = 0;
			int endOfFile = 0;

			// mientras no termina el archivo creo request, obtengo responses y escribo lo dado por el servidor
			while(endOfFile != -1){
                //genero readRequest
				ReadRequest req = ReadRequest.newBuilder().setSrc(src).setOffset(totalBytesRead).setAmount(bufferSize).build();

                //genero respuesta del servidor y obtengo los parametros
                ReadResponse resp = mBlockingStub.readFile(req);
                buffer = resp.getData().toByteArray();
				int bytesRead = resp.getBytesRead();
				endOfFile = bytesRead;
				totalBytesRead += bytesRead;

				//write del archivo
				if (endOfFile != -1){
					stream.write(buffer, 0, bytesRead);
					stream.flush();
					out.flush();
				}

			}
			// close file y canal
            stream.close();
            out.close();
            mChannel.shutdownNow();

            System.out.println("Archivo " + src +" leido exitosamente! con un total de " + totalBytesRead + " bytes.");
        }catch(Exception e){
            e.printStackTrace();
        }
	}

	public static void initParams(String[] argv) {
		int c;
		String arg;
		LongOpt[] longopts = new LongOpt[8];

		longopts[0] = new LongOpt("host", LongOpt.REQUIRED_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("src", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[2] = new LongOpt("dest", LongOpt.REQUIRED_ARGUMENT, null, 'd');

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
			case 's':
				src = g.getOptarg();
				break;
			case 'd':
				dest = g.getOptarg();
				break;
			case 'h':
				host = g.getOptarg();
				break;
			default:
				System.exit(0);
			}
		}

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
					+ "\t- read or get --dest <local> --src <remote>: Almacene un archivo de <local> en <remote>\n");
					//+ "\t- ls or list --src <remote/directory>: List files from <remote/directory>\n"
					//+ "\t- rw or readwrite --src <local> --dest <remote>: Almacena un archivo de <local> a <remote> y realiza una copia de seguridad en el servidor \n");
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
				client.readFile(src, dest);
				endTime = System.nanoTime();

				System.out.printf("Tomo: %d ms\n", (endTime - startTime) / 1000000);
				break;

			case "write":
			case "add":
				startTime = System.nanoTime();
				client.writeStream(src, dest);
				endTime = System.nanoTime();

				System.out.printf("Tomo: %d ms\n", (endTime - startTime) / 1000000);
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
