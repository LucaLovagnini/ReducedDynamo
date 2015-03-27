import java.rmi.*;
public interface LogManager_Interface extends Remote {
	public void notifyMe (String ip) throws RemoteException;

}
