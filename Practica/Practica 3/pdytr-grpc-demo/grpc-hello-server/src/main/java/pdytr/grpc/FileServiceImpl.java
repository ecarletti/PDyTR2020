package pdytr.grpc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {
	private BufferedOutputStream mBufferedOutputStream = null;

	@Override
	public StreamObserver<WriteRequest> writeFile(final StreamObserver<WriteResponse> responseObserver) {
		return new StreamObserver<WriteRequest>() {

			@Override
			public void onNext(WriteRequest request) {
				byte[] data = request.getData().toByteArray();
				int offset = request.getOffset();
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
				responseObserver.onNext(WriteResponse.newBuilder().setBytesWrite(300).build());
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
			int bytesReadServerFile = 0;
			ByteString byteString;
			@Override
			public void onNext(ReadRequest request) {
				
				try {
					String src = request.getSrc();
					int offset = request.getOffset();
					int amount = request.getAmount();
					byte[] buff = new byte[amount];
					System.out.println(offset);

					File fileServer = new File("store/" + src);
					RandomAccessFile file = new RandomAccessFile(fileServer, "r");

					file.seek(offset);
					bytesReadServerFile = file.read(buff, 0, amount);
					file.close();
					byteString = ByteString.copyFrom(buff);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable t) {

			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(ReadResponse.newBuilder().setBytesRead(bytesReadServerFile).setData(byteString).build());
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
