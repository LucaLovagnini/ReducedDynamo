import java.rmi.RemoteException;

public class BootstrapServer_Monitor {
	
	int clients = 0; 
	int waitingNodes = 0; 
	int waitingClients = 0;
	boolean keepalive = false;
	boolean addingNodes = false;
	boolean clientsturn = false;
	
	synchronized public void start_join_node()
	{
		while ((clients>0 || addingNodes) || (waitingClients>0 && clientsturn)|| keepalive)
		{++waitingNodes;
		try
			{wait();} 
		catch (InterruptedException e)
			{}
		--waitingNodes;
		}
		addingNodes = true;
	}
	
	synchronized public void end_join_node()
	{		
		addingNodes = false; 
		clientsturn=true;
		notifyAll();
	}
	
	synchronized public void start_client_search()
	{
		while(addingNodes ||(waitingNodes>0 && !clientsturn)||keepalive) 
			{++waitingClients;
			try
				{wait();} 
			catch (InterruptedException e) 
				{};
			--waitingClients;
			}
		++clients; 
	}
	synchronized public void end_client_search()
	{
		clients = clients - 1; 
		clientsturn=false;
		if (clients == 0) 
			notifyAll();
	}
	
	synchronized public void start_keep_alive()
	{	keepalive=true;
		while(addingNodes||clients>0)
			{try
				{wait();} 
			catch (InterruptedException e) 
				{};		
			}
	}
	synchronized public void end_keep_alive()
	{	keepalive=false;
		notifyAll();
	}


}
