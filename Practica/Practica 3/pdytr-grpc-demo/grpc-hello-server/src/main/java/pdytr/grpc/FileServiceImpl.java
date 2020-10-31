package pdytr.grpc;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
						// timestamp for name of receive file
						String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
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
}
