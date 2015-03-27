import java.util.concurrent.*;
public class StorageNode_Client_Main {

	public static void main(String[] args) {
		ScannerPorte scanner = new ScannerPorte();
		StorageNode taskNode = new StorageNode(scanner);
		Client taskClient = new Client (scanner,76);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0;i<20;i++)
			exec.execute(taskNode);
		try
		{Thread.sleep(10000);}
		catch(InterruptedException e){}
		System.out.println("Inizia il client!");
		exec.execute(taskClient);
		exec.shutdown();
	}

}
