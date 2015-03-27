import java.util.concurrent.*;
public class StorageNode_Client_Main {

	public static void main(String[] args) {
		ScannerPorte scanner = new ScannerPorte();
		FileWriterNode writer = new FileWriterNode();
		StorageNode taskNode = new StorageNode(scanner,writer);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0;i<10;i++)
			exec.execute(taskNode);
		try
		{Thread.sleep(1000);}
		catch(Exception e){}
		for(int i=0;i<0;i++)
			{Client taskClient = new Client(scanner);
			exec.execute(taskClient);
			}
		exec.shutdown();
		
	}

}
