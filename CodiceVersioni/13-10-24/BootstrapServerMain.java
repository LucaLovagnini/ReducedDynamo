import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
public class BootstrapServerMain {
	public static void main(String[] args) {
		BootstrapServer_Monitor BSM = new BootstrapServer_Monitor();
		try
			{LocateRegistry.createRegistry(10001);
			Registry r=LocateRegistry.getRegistry(10001);
			BootstrapServer_Implementation BS = new BootstrapServer_Implementation(10,BSM); 
			BootstrapServer_Interface stub =(BootstrapServer_Interface) 
					UnicastRemoteObject.exportObject(BS,0);
			r.rebind("BOOTSTRAP-SERVER", stub);
			LogManager_Implementation LogManager = new LogManager_Implementation();
			stub.setLogManager(LogManager);
			try {
				System.out.println("Indirizzo IP BootstrapServer:"+InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {e.printStackTrace();}
			}
		catch (RemoteException e) 
		{e.printStackTrace();}
	}

}
