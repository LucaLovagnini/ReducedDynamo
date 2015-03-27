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
		//se continuo tocca a me
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		//se sono il primo nodo della lista sicuramente vengo aggiunto
		if(storageNodeList.size()==0)
			{storageNodeList.add(ip);
			return "0";}
		else
			{//genero casualmente il nodo di bootstrap
			double di=Math.random()*(storageNodeList.size()-1);
			int i = (int) di;
			System.out.println("i="+i);
			String ipStorageNode = storageNodeList.get(i);
			//se ci sono meno di k nodi nella lista, allora il nuovo nodo viene aggiunto
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
	
	public String client_search() throws RemoteException
	{	//se supero questa istruzione significa che � il mio turno
		BSM.start_client_search();
		//decido qual'� il nodo di bootstrap a cui rivolgermi
		double di=Math.random()*(storageNodeList.size()-1);
		int i = (int) di;
		System.out.println("i="+i);
		String ipStorageNode="";
		if(storageNodeList.size()!=0)
			{ipStorageNode = storageNodeList.get(i);}
		else
			{System.out.println("Attualmente non ci sono nodi nell'anello, riprovare pi� tardi");}
		return ipStorageNode;
		
	}
	public void end_client_search() throws RemoteException
	{	BSM.end_client_search();
	}
}
