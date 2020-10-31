package pdytr.grpc;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.nio.file.StandardOpenOption;
import io.grpc.stub.StreamObserver;

public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {
	private int mStatus = 200;
	private String mMessage = "";
	private BufferedOutputStream mBufferedOutputStream = null;

	@Override
	public StreamObserver<WriteRequest> writeFile(final StreamObserver<WriteResponse> responseObserver) {
		return new StreamObserver<WriteRequest>() {
			int mmCount = 0;

			@Override
			public void onNext(WriteRequest request) {
				mmCount++;

				byte[] data = request.getData().toByteArray();
				long offset = request.getOffset();
				String src = request.getSrc();
				String dest = request.getDest();
				try {
					if (mBufferedOutputStream == null) {
						// create directory store if not exists
						Files.createDirectories(Paths.get("store"));
						// store de receive file
						mBufferedOutputStream = new BufferedOutputStream(
								new FileOutputStream("store/" + dest));
					}
					mBufferedOutputStream.write(data);
					mBufferedOutputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(WriteResponse.newBuilder().setStatus(mStatus).setMessage(mMessage).build());
				responseObserver.onCompleted();
				if (mBufferedOutputStream != null) {
					try {
						mBufferedOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						mBufferedOutputStream = null;
					}
				}
			}
		};
	}

	@Override
	public StreamObserver<ReadRequest> readFile(final StreamObserver<ReadResponse> responseObserver) {
		return new StreamObserver<ReadRequest>() {
			int mmCount = 0;

			@Override
			public void onNext(ReadRequest request) {
				mmCount++;

				byte[] data = request.getData().toByteArray();
				long offset = request.getOffset();
				String src = request.getSrc();
				String dest = request.getDest();

				try {
					if (mBufferedOutputStream == null) {
						// store de receive file
						mBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(dest));
					}
					mBufferedOutputStream.write(data);
					mBufferedOutputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// try {
				// 	int bufferSize = 8 * 1024;
				// 	byte[] buffer = new byte[bufferSize];

				// 	BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("store/" + src), bufferSize);

				// 	int bytesRead = 0;
				// 	while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
				// 		bytesRead = bufferedInputStream.read(buffer);
				// 		try {
				// 			Files.write(Paths.get(dest), buffer, StandardOpenOption.APPEND);
				// 		} catch (IOException e) {
				// 			Files.createFile(Paths.get(dest));
				// 			Files.write(Paths.get(dest), buffer, StandardOpenOption.APPEND);
				// 		}
				// 	}
				// 	bufferedInputStream.close();
				// 	System.out.println("El archivo fue leido correctamente");
				// } catch (IOException e) {
				// 	System.err.println("Error con los archivos locales");
				// 	e.printStackTrace();
				// } catch (Exception e) {
				// 	System.err.println("Excepcion general!");
				// 	e.printStackTrace();
				// }
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(ReadResponse.newBuilder().setStatus(mStatus).setMessage(mMessage).build());
				responseObserver.onCompleted();
				if (mBufferedOutputStream != null) {
					try {
						mBufferedOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						mBufferedOutputStream = null;
					}
				}
			}
		};
	}
}
