import jade.core.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.nio.file.DirectoryStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public class FTPAgent extends Agent {
	private Location origen;

	private String method;
	private String sourcePath;
	private String destinationPath;
	private String files;

	private byte[] file = null;
	private long fileSize;
	private int currentSize = 0;

	public void setup() {
		Object[] args = getArguments();
		this.getopt(args);

		this.origen = here();

		try {
			ContainerID destination = new ContainerID("Main-Container", null);
			System.out.println("Migrando el agente a " + destination.getID());
			doMove(destination);
		} catch (Exception e) {
			System.out.println("No fue posible migrar el agente");
			System.out.println(e);
		}
	}

	protected void afterMove() {
		Location here = here();
		switch (this.method) {
			case "write":
				this.writeAfterMove(here);
				break;
			case "read":
				this.readAfterMove(here);
				break;
		}
	}

	private void writeAfterMove(Location here) {
		String currentDirectory = System.getProperty("user.dir");
		String storeDir = currentDirectory + "/store/" + this.destinationPath;
		if (!here.getName().equals(this.origen.getName())) {
			FTPCommand.write(storeDir, this.file, this.currentSize, this.fileSize);
			doMove(new ContainerID(this.origen.getName(), null));
		} else if (this.fileSize > this.currentSize) {
			this.file = FTPCommand.read(this.sourcePath, this.currentSize, this.fileSize);
			this.currentSize += this.file.length;
			System.out.printf("%d de %d bytes leidos\n", this.file.length, this.fileSize);
			doMove(new ContainerID("Main-Container", null));
		} else {
			System.out.println("El archivo " + this.sourcePath + " fue escrito en el directorio remoto: " + this.destinationPath);
		}
	}

	private void readAfterMove(Location here) {
		String currentDirectory = System.getProperty("user.dir");
		String storeDir = currentDirectory + "/store/" + this.sourcePath;
		if (!here.getName().equals(this.origen.getName())) {
			this.file = FTPCommand.read(storeDir, this.currentSize, this.fileSize);
			this.currentSize += this.file.length;
			System.out.printf("Leyendo %d bytes de %d\n", this.file.length, this.fileSize);
			doMove(new ContainerID(this.origen.getName(), null));
			return;
		}

		try {
			try {
				Files.write(Paths.get(this.destinationPath), this.file,StandardOpenOption.APPEND);
				System.out.printf("Escribiendo %d bytes de %d\n", this.file.length, this.fileSize);
			} catch (IOException e) {
				Files.createFile(Paths.get(this.destinationPath));
				Files.write(Paths.get(this.destinationPath), this.file,StandardOpenOption.APPEND);
			} finally {
				if (this.fileSize > this.currentSize) {
					System.out.println("Quedan bytes!");
					doMove(new ContainerID("Main-Container", null));
				} else {
					System.out.println("El archivo " + this.destinationPath + " fue escrito correctamente y cargado satisfactoriamente en " + this.destinationPath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getopt(Object[] args) {
		try {
			switch ((String) args[0]) {
			case "write":
			case "read":
				if (args.length != 3) {
					System.out.println("3 argumentos necesarios, comando(metodo), directorio local y directorio remoto");
					System.exit(1);
				}
				
				this.method          = (String) args[0];
				this.sourcePath      = (String) args[1];
				this.destinationPath = (String) args[2];

				if (this.method.equals("write")) {
					this.file = FTPCommand.read(this.destinationPath, this.currentSize, this.fileSize);
					this.currentSize += this.file.length;
					this.fileSize = Files.size(Paths.get(this.sourcePath));
				} else {
					String storeDir = "../store/" + this.sourcePath;
					this.fileSize = Files.size(Paths.get(storeDir));
				}
				break;

			default: 
				System.out.printf("Command %s unavailable\n", (String) args[0]);
				System.exit(1);
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
