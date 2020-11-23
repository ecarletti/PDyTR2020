import jade.core.*;
import jade.util.leap.Serializable;
import jade.wrapper.ContainerController;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class ReportAgent extends Agent
{
	private ArrayList<ContainerInfo> info = new ArrayList<ContainerInfo>();
	private ArrayList<String> containers = new ArrayList<String>();
	private Location origin;
	private long startTime;
	private int index;
	
	public void printAgentPresentation() {
		System.out.println("Hola, agente con nombre local " + getLocalName());
		System.out.println("Y nombre completo... " + getName());
	}

	public void setup() {
		this.origin = here();
                
		this.printAgentPresentation();
		System.out.println("Y en location: " + this.origin.getID());

		for (int i = 0; i < 10; i++) {
			String containerName = "Container-" + i;
                        
			if (!containerName.equals(origin.getName())) {
				createContainer(containerName);
				containers.add(containerName);
			}
		}
                
		containers.add(this.origin.getName());

		try {
			index = 0;

			ContainerID destination = new ContainerID(containers.get(index++), null);
			System.out.println("Migrando el agente a " + destination.getID());
			startTime = System.currentTimeMillis();
			doMove(destination);
		} catch (Exception e) {
			System.out.println("No fue posible migrar el agente");
		}
	}

	protected void afterMove() {
		Location actual = here();
                
		if (actual.getName().equals(origin.getName())) {
			this.originAction();
		} else {
			this.remoteAction(actual);
		}
	}

	private void remoteAction(Location actual) {
		long startContainerTime = System.currentTimeMillis();
		ContainerInfo currentContainerInfo = new ContainerInfo();
		this.printAgentPresentation();
		currentContainerInfo.setFreeMemory(java.lang.Runtime.getRuntime().freeMemory());
		currentContainerInfo.setName(actual.getName());
                
		try {
			ContainerID destination = new ContainerID(containers.get(index++), null);
			System.out.println("Migrando el agente a " + destination.getID());
			
			info.add(currentContainerInfo);
			
			long finishContainerTime = System.currentTimeMillis() - startContainerTime;
			currentContainerInfo.setProcessingTime(finishContainerTime);

			OperatingSystemMXBean processing = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			currentContainerInfo.setProcessLoad(processing.getProcessCpuLoad());

			doMove(destination);
		} catch (Exception e) {
			System.out.println("No fue posible migrar el agente");
			System.out.println(e);
		}
	}

	private void originAction() {
        System.out.println("--- --- --- --- --- --- ---");
        System.out.println("(a) Tiempo total: " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("--- --- --- --- --- --- ---");

		for (ContainerInfo container : info) System.out.println(container.getInfoAsString());
	}

    // wrapper Container Controller
    // https://jade.tilab.com/doc/api/index.html?jade/wrapper/ContainerController.html
	protected ContainerController createContainer(String name) {
		// Get the JADE runtime interface (singleton)
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, name);
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		// create a non-main agent container
		return runtime.createAgentContainer(profile);
	}

    // ContainerInfo print report
	public class ContainerInfo implements Serializable {
		private long freeMemory;
		private String name;
		private long processingTime;
		private double processLoad;

        // Getters & Setters
		public long getFreeMemory() {
			return freeMemory;
		}

		public void setFreeMemory(long freeMemory) {
			this.freeMemory = freeMemory;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getProcessingTime() {
			return processingTime;
		}

		public void setProcessingTime(long processingTime) {
			this.processingTime = processingTime;
		}

		public void setProcessLoad(double processLoad){
			this.processLoad = processLoad;
		}

		public double getProcessLoad() {
			return processLoad;
		}
                
		public String getInfoAsString() {
			String str = "";

			str += "Informacion del Container (d) Nombre:" + getName();
			str += "\n\t(a) Tiempo de procesamiento: " + getProcessingTime() + "ms";
			str += "\n\t(b) Carga de procesamiento: " + processLoad + "%";
            str += "\n\t(c) Memoria total disponible: " + (getFreeMemory() / 1024) / 1024 + "Mb";
            str += "\n\t--- --- --- --- --- --- ---";
                        
			return str;
		}
	}
}
