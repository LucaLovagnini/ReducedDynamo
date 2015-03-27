import java.io.*;
import java.net.*;

public class StorageNode_UDP extends Thread {
	public void run ()
		{try
			{byte [] data = new byte[200];
			DatagramSocket ds = new DatagramSocket(6500);
			
			}
		catch (Exception e){e.printStackTrace();}
		}
}
