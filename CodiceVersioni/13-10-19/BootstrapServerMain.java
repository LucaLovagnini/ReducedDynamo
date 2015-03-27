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
			}
		catch (RemoteException e) 
		{System.out.println("Communication error " +e.toString());}
	}

}
