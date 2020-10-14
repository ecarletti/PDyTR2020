/*
 * Client.java
 * a) Looks up for the remote object
 * b) "Makes" the RMI
 */

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;

public class FTClient {
        static Boolean verboseMode = true;
        static Boolean isSetBytesFlag = false;
        static Boolean listView = false;
        static Integer bytes = 0;
        static Integer initialPosition = 0;

        static String src = "";
        static String dest = "";
        static String host = "";

        // funcion para leer
        public static void read(IfaceFTServer remote) {
                try {
                        byte endFile = 0;
                        int position = initialPosition;

                        while (endFile != 1) {
                                byte[] fileContent = remote.read(src, position, 1024);

                                endFile = fileContent[fileContent.length - 1];
                                fileContent = Arrays.copyOf(fileContent, fileContent.length - 1);
                                position += fileContent.length;

                                try {
                                        Files.write(Paths.get(dest), fileContent, StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                        Files.createFile(Paths.get(dest));
                                        Files.write(Paths.get(dest), fileContent, StandardOpenOption.APPEND);
                                }
                        }

                        System.out.println("El archivo fue leído correctamente");
                } catch (RemoteException e) {
                        System.err.println("Error de Conexión!");
                        e.printStackTrace();
                } catch (IOException e) {
                        System.err.println("Error con los archivos locales");
                        e.printStackTrace();
                } catch (Exception e) {
                        System.err.println("Excepción general!");
                        e.printStackTrace();
                }
        }

        // funcion para escribir
        public static void write(IfaceFTServer remote) {
                try {
                        RandomAccessFile file = new RandomAccessFile(Paths.get(src).toString(), "r");
                        FileDescriptor fd = file.getFD();
                        FileInputStream fis = new FileInputStream(fd);
                        byte[] partialContent = new byte[1024];

                        int bytesReaded;

                        StandardOpenOption openOption = StandardOpenOption.WRITE;

                        file.seek(initialPosition);

                        while (fis.available() > 0) {
                                bytesReaded = fis.read(partialContent, 0, Math.min(1024, fis.available()));
                                remote.write(dest, partialContent, bytesReaded, openOption);
                                openOption = StandardOpenOption.APPEND;
                        }

                        fis.close();
                        System.out.println("El archivo fue escrito correctamente");
                } catch (RemoteException e) {
                        System.err.println("Error de Conexión!");
                        e.printStackTrace();
                } catch (IOException e) {
                        System.err.println("Error con los archivos locales");
                        e.printStackTrace();
                } catch (Exception e) {
                        System.err.println("Excepción general!");
                        e.printStackTrace();
                }
        }

        // funcion para listar
        public static void list(IfaceFTServer remote) {
                try {
                        String listReturn = remote.list(src, listView);
                        System.out.println(listReturn);
                } catch (RemoteException e) {
                        System.err.println("Error de Conexión!");
                        e.printStackTrace();
                } catch (Exception e) {
                        System.err.println("Excepción general!");
                        e.printStackTrace();
                }
        }

        public static void main(String[] argv) {
                long startTime, endTime;

                /* Look for hostname and msg length in the command line */
                if (argv.length < 1) {
                        System.out.printf("Utilización: cliente\n"
                                        + "\t- write or add --src <local> --dest <remote>: Agrega un archivo de <local> a <remote> \n"
                                        + "\t- read or get --dest <local> --src <remote>: Almacene un archivo de <local> en <remote>\n"
                                        + "\t- ls or list --src <remote/directory>: List files from <remote/directory>\n"
                                        + "\t- rw or readwrite --src <local> --dest <remote>: Almacena un archivo de <local> a <remote> y realiza una copia de seguridad en el servidor \n");
                        System.exit(1);
                }

                String command = argv[0];
                // inicia (linea 240)  public static void initParams(String[] argv)
                initParams(argv);

                /*
                 * Instead of reporting '--verbose' and '--brief' as they are encountered, we
                 * report the final status resulting from them.
                 */
                if (verboseMode)
                        System.out.println("Verbose mode activated");

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
                        if (verboseMode)
                                System.err.println("Advertencia: --dest fue setteado a tmp1");

                        dest = "tmp1";
                }
                // setea localhost como host por default localhost
                if (host.isEmpty())
                        host = "localhost";

                try {
                        String rname = "//" + host + ":" + Registry.REGISTRY_PORT + "/remote";
                        IfaceFTServer remote = (IfaceFTServer) Naming.lookup(rname);

                        switch (command) {
                        case "read":
                        case "get":
                                startTime = System.nanoTime();
                                read(remote);
                                endTime = System.nanoTime();

                                if (verboseMode)
                                        System.out.printf("Tomó: %d ms\n", (endTime - startTime) / 1000000);
                                break;

                        case "write":
                        case "add":
                                startTime = System.nanoTime();
                                write(remote);
                                endTime = System.nanoTime();

                                if (verboseMode)
                                        System.out.printf("Tomó: %d ms\n", (endTime - startTime) / 1000000);
                                break;

                        case "list":
                        case "ls":
                                startTime = System.nanoTime();
                                list(remote);
                                endTime = System.nanoTime();

                                if (verboseMode)
                                        System.out.printf("Tomó: %d ms\n", (endTime - startTime) / 1000000);
                                break;

                        case "rw":
                        case "readwrite":
                                startTime = System.nanoTime();
                                read(remote);
                                dest = "backup-" + dest;
                                write(remote);
                                endTime = System.nanoTime();

                                if (verboseMode)
                                        System.out.printf("Took: %d ms\n", (endTime - startTime) / 1000000);
                                break;

                        case "time":
                                startTime = System.nanoTime();
                                remote.time();
                                endTime = System.nanoTime();

                                System.out.println(endTime - startTime);
                                break;

                        case "timeout":
                                if (verboseMode)
                                        System.out.println("Ejecutando el comando: timeout...");

                                Boolean ret = remote.timeout();

                                if (ret)
                                        System.out.println("Completado!");
                                else
                                        System.out.println("Timeout excedido");

                                break;

                        default:
                                System.err.println("Comando no disponible");
                                break;
                        }
                } catch (Exception e) {
                        System.err.println("Excepción general!");
                        e.printStackTrace();
                }
        }

        // INFO DE GET OPT GNU
        // https://www.gnu.org/software/gnuprologjava/api/gnu/getopt/Getopt.html
        // ejemplos > https://www.programcreek.com/java-api-examples/?class=gnu.getopt.Getopt&method=getopt
        public static void initParams(String[] argv) {
                int c;
                String arg;
                LongOpt[] longopts = new LongOpt[8];

                StringBuffer verboseModeSb = new StringBuffer("0");
                StringBuffer listViewSb = new StringBuffer("0");

                longopts[0] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, verboseModeSb, '1');
                longopts[1] = new LongOpt("brief", LongOpt.NO_ARGUMENT, verboseModeSb, '0');
                longopts[2] = new LongOpt("all", LongOpt.NO_ARGUMENT, listViewSb, '1');
                longopts[3] = new LongOpt("bytes", LongOpt.REQUIRED_ARGUMENT, null, 'a');
                longopts[4] = new LongOpt("pos", LongOpt.REQUIRED_ARGUMENT, null, 'p');
                longopts[5] = new LongOpt("host", LongOpt.REQUIRED_ARGUMENT, null, 'h');
                longopts[6] = new LongOpt("src", LongOpt.REQUIRED_ARGUMENT, null, 's');
                longopts[7] = new LongOpt("dest", LongOpt.REQUIRED_ARGUMENT, null, 'd');

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
                        case 'v':
                                verboseModeSb = new StringBuffer("1");
                                break;
                        case 'b':
                                verboseModeSb = new StringBuffer("0");
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
                verboseModeSb.trimToSize();

                listView = new Integer(listViewSb.toString()) != 0;
                verboseMode = new Integer(verboseModeSb.toString()) != 0;

                for (int i = g.getOptind() + 1; i < argv.length; i++) {
                        System.out.println("Non option argv element: " + argv[i] + "\n");
                }
        }
}
