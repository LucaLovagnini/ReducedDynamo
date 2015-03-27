import java.util.concurrent.*;
import java.util.*;
public class StorageNode_Client_Main {

	public static void main(String[] args) {
		int network=Integer.parseInt(args[0]);//se impostata ad 1 si utilizzerà la versione di rete, quella in locale altriemnti
		String bootstrapServerIp=null;
		Scanner sc = new Scanner(System.in);
		ScannerPorte scanner = new ScannerPorte();
		StorageNode taskNode;
		if(Integer.parseInt(args[0])==1)//versione di rete
			{System.out.println("Inserire l'indirizzo del BootstrapServer:");
			bootstrapServerIp=sc.nextLine();
			taskNode=new StorageNode(scanner,bootstrapServerIp);
			}
		else		//versione locale
			taskNode=new StorageNode(scanner);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0;i<20;i++)
			exec.execute(taskNode);
		try
		{Thread.sleep(1000);}
		catch(Exception e){e.printStackTrace();}
		for(int i=0;i<0;i++)
			{Client taskClient = new Client(scanner);
			exec.execute(taskClient);
			}
		exec.shutdown();
		
	}

}
