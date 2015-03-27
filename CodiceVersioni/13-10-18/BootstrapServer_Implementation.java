import java.rmi.RemoteException;
import java.util.*;
import java.text.*;


public class BootstrapServer_Implementation implements BootstrapServer_Interface {

	ArrayList<String> storageNodeList;
	BootstrapServer_Monitor BSM;
	int k;
	
	public  BootstrapServer_Implementation (int k,BootstrapServer_Monitor BSM)
	{
		this.k=k;
		this.BSM=BSM;
		storageNodeList = new ArrayList<String>();
	}
	public String start_join_node(String ip) throws RemoteException
	{		
		try
		{BSM.start_join_node();
		Thread.sleep(3000);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		if(storageNodeList.size()==0)
			{storageNodeList.add(ip);
			return "0";}
		else
			{double di=Math.random()*(storageNodeList.size()-1);
			int i = (int) di;
			System.out.println("i="+i);
			String ipStorageNode = storageNodeList.get(i);
			if(storageNodeList.size()<k)
				storageNodeList.add(ip);
			return ipStorageNode;
			}
		}
		catch (Exception e)
			{e.printStackTrace();
			return "-1";
			}
	}
	public void end_join_node() throws RemoteException
	{	BSM.end_join_node();
	}
	
	public void client_search() throws RemoteException
	{
		
	}
}
