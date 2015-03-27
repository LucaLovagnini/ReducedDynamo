import java.net.InetAddress;
import java.net.MulticastSocket;


public class idController extends Thread{
	public int control(int id)
	{	try
			{String address = "224.0.0.1";
			InetAddress ia=InetAddress.getByName(address);
			MulticastSocket ms = new MulticastSocket(4000);
			}
		catch(Exception e){e.printStackTrace();}
	return -1;
	}
	public void run()
	{	try
			{String address = "224.0.0.1";
			InetAddress ia=InetAddress.getByName(address);
			MulticastSocket ms = new MulticastSocket(4000);
			}
		catch(Exception e){e.printStackTrace();}
		while(true)
			{}
	}
}
