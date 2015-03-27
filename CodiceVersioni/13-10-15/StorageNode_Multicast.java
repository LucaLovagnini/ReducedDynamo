import java.net.*;
import java.util.Hashtable;
import java.io.*;

public class StorageNode_Multicast extends Thread{
	Hashtable<Integer,String> nodeTable = new Hashtable<Integer, String>();
	String fakeip;
	public StorageNode_Multicast (Hashtable<Integer,String> nodeTable,String fakeip)
	{
		this.nodeTable=nodeTable;
		this.fakeip=fakeip;
	}
	
	public void run()
		{try 
			{//il mio identificatore
			//spedisco a tutti i nodi che fanno parte del multicast:
			//il mio indirizzo ip
			//porta UDP+porta TCP
			//tutto questo è contenuto in fakeip
			String address = "224.0.0.1";
			InetAddress ia=InetAddress.getByName(address);
			MulticastSocket ms = new MulticastSocket(4000);
			ByteArrayOutputStream bout= new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream (bout);
			dout.writeUTF(fakeip);
			byte []data = bout.toByteArray( );
			DatagramPacket dp = new DatagramPacket(data,data.length,ia,4000);
			ms.send (dp);
			bout.reset();
			//entro a far parte del gruppo multicast
			ms.joinGroup (ia);
			while(true)
				{data=new byte[200];
				dp = new DatagramPacket (data,data.length);
				//qualcuno ha aggiornato la tabella
				//possiamo fare affidamente sul datainputstream dato che nel multicast girano solo string
				ms.receive(dp);
				ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData(),0,dp.getLength());
				DataInputStream ddis= new DataInputStream(bin);
				//ora fakeip fa riferimento all'oggetto ricevuto da un nuovo membro
				fakeip=ddis.readUTF();
				System.out.println(Thread.currentThread()+" ha ricevuto: "+fakeip);
				
				}
			}
		catch (IOException ex) {System.out.println("errore"); }
		}
}
