package pdytr.grpc;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class UploadFileServer {
	private static final Logger logger = Logger.getLogger(UploadFileServer.class.getName());
	private static final int PORT = 50051;

	private Server mServer;

	private void start() throws IOException {
		/* The port on which the mServer should run UploadFileServer */

		mServer = ServerBuilder.forPort(PORT).addService(new FileServiceImpl()).build().start();
		logger.info("Server started, listening on " + PORT);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its JVM shutdown
				// hook.
				System.err.println("*** shutting down gRPC mServer since JVM is shutting down");
				UploadFileServer.this.stop();
				System.err.println("*** mServer shut down");
			}
		});
	}

	private void stop() {
		if (mServer != null) {
			mServer.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (mServer != null) {
			mServer.awaitTermination();
		}
	}

	/**
	 * Main launches the mServer from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final UploadFileServer server = new UploadFileServer();
		server.start();
		server.blockUntilShutdown();
	}

}
