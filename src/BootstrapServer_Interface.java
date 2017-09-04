import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BootstrapServer_Interface extends Remote {


		public String start_join_node(String ip) throws RemoteException;
		public void end_join_node(String ip) throws RemoteException;
		public String client_search()throws RemoteException;
		public void end_client_search()throws RemoteException;
		public void setLogManager(LogManager_Interface LogManager) throws RemoteException;


	}
