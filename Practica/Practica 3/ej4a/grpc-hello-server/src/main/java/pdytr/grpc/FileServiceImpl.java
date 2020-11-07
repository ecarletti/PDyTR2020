package pdytr.grpc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {
	private DataOutputStream mOutputStream = null;

	@Override
	public StreamObserver<WriteRequest> writeFile(final StreamObserver<WriteResponse> responseObserver) {
		return new StreamObserver<WriteRequest>() {

			@Override
			public void onNext(WriteRequest request) {
				byte[] data = request.getData().toByteArray();
				int offset = request.getOffset();
				String dest = request.getDest();
				try {
					if (mOutputStream == null) {
						// create directory store if not exists
						Files.createDirectories(Paths.get("store"));
						// store de receive file
						mOutputStream = new DataOutputStream(
								new FileOutputStream("store/" + dest));
					}
					mOutputStream.write(data, 0, offset);
					mOutputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable t) {
			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(WriteResponse.newBuilder().setBytesWrite(mOutputStream.size()).build());
				responseObserver.onCompleted();
				if (mOutputStream != null) {
					try {
						mOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						mOutputStream = null;
					}
				}
			}
		};
	}

  
  public void readFile(ReadRequest request, StreamObserver<ReadResponse> responseObserver){

		int bytesReadServerFile = 0;
		int offset,amount;
		String src;
		ReadResponse response;
		File fileServer;
		RandomAccessFile file; 
		ByteString byteString;

		try{
			// Parametros de ReadRequest mas inicio el buffer
			src = request.getSrc();
			offset = request.getOffset();
			amount = request.getAmount();
			byte[] buff = new byte[amount];
					
			// store > es la carpeta de almacenamiento del servidor
			fileServer = new File("store/" + src);
			file = new RandomAccessFile(fileServer, "r");

			// posicion en el archivo
			file.seek(offset);
			// read desde esa posicion
			bytesReadServerFile = file.read(buff, 0, amount);
			// cierro el archivo
			file.close();

			byteString = ByteString.copyFrom(buff);

			// Genero ReadResponse
			response = ReadResponse.newBuilder()
									.setBytesRead(bytesReadServerFile)
									.setData(byteString)
									.build();

			// StreamObserver<ReadResponse> responseObserver = onNext y onCompleted
			responseObserver.onNext(response); 
			responseObserver.onCompleted();

		}catch(Exception e){
			e.printStackTrace();
			}
	}

}
