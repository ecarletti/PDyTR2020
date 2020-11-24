import jade.core.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.UnicastRemoteObject;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public class FTPCommand {
	public static byte[] read(String path, int currentSize, long fileSize) {
		try {
			int chunck = 1024;
			int noBytes = ((int) fileSize - currentSize) < chunck
				? (int) (fileSize - currentSize)
				: chunck;
			
			byte[] contents = new byte[noBytes];

			System.out.printf("Leyendo %d bytes de %d\n", noBytes, currentSize);

			InputStream in = new FileInputStream(path);
			in.skip(currentSize);
			in.read(contents, 0, noBytes);
			in.close();

			return contents;
		} catch(IOException e) {
			System.out.println(e);
			return new byte[0];
		}
	}

	public static int write(String path, byte[] data, int currentSize, long fileSize) {
		try {
			try {
				Files.write(Paths.get(path), data,StandardOpenOption.APPEND);
			} catch (IOException e) {
				Files.createFile(Paths.get(path));
				Files.write(Paths.get(path), data, StandardOpenOption.APPEND);
			}
			
			System.out.printf("Escribiendo %d de %d bytes\n", currentSize, fileSize);
			return data.length;
		} catch (IOException e) {
			System.out.println(e.toString());
			return -1;
		}
	}
}
