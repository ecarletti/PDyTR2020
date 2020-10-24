package pdytr.grpc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class UploadFileClient {
	private static final Logger logger = Logger.getLogger(UploadFileClient.class.getName());
	private static final int PORT = 50051;

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

	public void startStream(final String filepath) {
		logger.info("tid: " + Thread.currentThread().getId() + ", Will try to readFile");
		StreamObserver<ReadResponse> responseObserver = new StreamObserver<ReadResponse>() {

			@Override
			public void onNext(ReadResponse value) {
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

		StreamObserver<ReadRequest> requestObserver = mAsyncStub.readFile(responseObserver);

		try {
			File file = new File(filepath);
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
					ReadRequest req = ReadRequest.newBuilder().setName(filepath).setData(byteString).setOffset(size)
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

	public static void main(String[] args) throws Exception {
		UploadFileClient client = new UploadFileClient("localhost", PORT);
		try {
			client.startStream(args[0]);
			logger.info("Done with startStream");
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
