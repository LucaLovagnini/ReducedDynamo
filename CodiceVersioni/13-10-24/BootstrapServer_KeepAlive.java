import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class BootstrapServer_KeepAlive extends Thread {
	int k;
	ArrayList<String> storageNodeList;
	BootstrapServer_Implementation BS;
	public BootstrapServer_KeepAlive(int k,BootstrapServer_Implementation BS)
		{this.setDaemon(true);
		this.k=k;
		this.BS=BS;
		Thread.currentThread().run();
		} 
	public void setList (ArrayList<String> storageNodeList)
		{this.storageNodeList=storageNodeList;}
	public void run()
		{try
			{while(true)
				{}
			}
		catch(Exception e){e.printStackTrace();}
		}
}
