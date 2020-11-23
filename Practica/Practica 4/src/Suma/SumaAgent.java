import java.nio.charset.Charset;
import java.nio.file.Files;

import jade.core.*;
import java.io.IOException;
import java.util.List;
import java.nio.file.Paths;

public class SumaAgent extends Agent
{
        private final String machine = "Main-Container";
        private Location origin = null;
        private Integer sum;
        private String filePath = "Suma/numeros";

        public void printAgentPresentation() {

            System.out.println("Hola, agente con nombre local " + getLocalName());
            System.out.println("Y nombre completo... " + getName());

        }

        public void setup()
        {
                Object[] args = getArguments();
                this.getopt(args);
                this.origin = here();
                this.printAgentPresentation();
		        System.out.println("Y en location: " + this.origin.getID());
                
                try {

                        ContainerID destination = new ContainerID(this.machine, null);
                        System.out.println("Migrando el agente a " + destination.getID());
                        doMove(destination);

                } catch (Exception e) {

                        System.out.println("No fue posible migrar el agente!");
                        System.out.println(e);

                }
        }

        protected void afterMove()
        {
                Location actual = here();

                if (!actual.getName().equals(this.origin.getName())) {

                        try {
                                List<String> numbers = Files.readAllLines(Paths.get(this.filePath), Charset.forName("utf8"));
                                int result = 0;
                                //System.out.println("numbers " + numbers);
                                for (String number: numbers) {
                                        result += Integer.parseInt(number);
                                }
                                
                                sum = result;
                                
                        } catch(NumberFormatException e) {
                                System.out.println("Solo soporta numeros");
                        } catch(IOException e) {
                                System.out.println("El archivo no existe");
                        } catch(Exception e) {
                                System.out.println("Error");
                                System.out.println(e.getMessage());
                        }

                        ContainerID destination = new ContainerID(this.origin.getName(), null);
                        doMove(destination);
                } else {
                        System.out.printf("Sum: %d\n", this.sum);
                }
        }

        private void getopt(Object[] args) {
                try {
                        if (args.length != 0) {
                                this.filePath = (String) args[0];           
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
}
