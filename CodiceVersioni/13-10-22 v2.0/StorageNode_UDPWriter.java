import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.net.*;
import java.util.*;


public class StorageNode_UDPWriter extends Thread{
	StorageNode_UDPMonitor UDPM;
	DatagramSocket UDPServiceSocket;
	List<DatagramPacket> list;
	StorageNode_UDP UDP;
	int socketPort;
	public StorageNode_UDPWriter(StorageNode_UDP UDP,StorageNode_UDPMonitor UDPM,DatagramSocket UDPServiceSocket,List<DatagramPacket> list,int socketPort)
		{this.UDP=UDP;//avere una copia dell'oggetto che mi ha creato serve per poter settare la socket se quella vecchia viene chiusa
		this.UDPM=UDPM;
		this.UDPServiceSocket=UDPServiceSocket;
		this.list=list;
		this.socketPort=socketPort;
		}
	public void run()
	{	while (true)
			{try
				{UDPM.StartWrite();
				UDPServiceSocket.setSoTimeout(1000);
				while(true)
						{byte [] data = new byte[200];
						DatagramPacket dp = new DatagramPacket (data,data.length);
						UDPServiceSocket.receive(dp);
						list.add(dp);//aggiungo alla lista il pacchetto inviato dal client
						}
					}
			catch (SocketTimeoutException e)
				{}
			catch(Exception e)
				{e.printStackTrace();}
			UDPM.EndWrite(); //rilascio la lock
			}
	}
}
