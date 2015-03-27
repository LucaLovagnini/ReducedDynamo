import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class StorageNode_UDP extends Thread {
	DatagramSocket ds;
	StorageNode_Multicast multicast;
	Hashtable<Integer,String> nodeTable;
	public StorageNode_UDP (DatagramSocket ds, StorageNode_Multicast multicast)
		{this.ds=ds;
		this.multicast=multicast;
		}
	public void run ()
		{try
			{while(true)
				{byte [] data = new byte[200];
				DatagramPacket dp = new DatagramPacket (data,data.length);
				//qualcuno mi ha cercato!
				ds.receive(dp);
				ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData(),0,dp.getLength());
				DataInputStream din = new DataInputStream(bin);
				//cerco di capire se chi mi ha contattato è un nuovo nodo oppure un client
				String type = din.readUTF();
				int start,end;
				//se mi ha contattato un nuovo nodo...
				if(type.equals("node"))
					{int file_id=din.readInt();
					String fakeipSender = din.readUTF();
					//ricavo la porta del nodo che mi ha contattato
					start=fakeipSender.indexOf("UDPport=");
					end=fakeipSender.indexOf(" TCPport=");
					int portSender = Integer.parseInt(fakeipSender.substring(start+8, end));
					//ottengo la tabella aggiornata dall'oggetto di multicast del mio nodo
					nodeTable=multicast.getTable();
					//calcolo il successore e il predecessore del nodo e ne ricavo gli indirizzi
					Enumeration <Integer> items = nodeTable.keys();
					//calcolo il massimo e il minimo della tabella...
					int min,max,actual;
					min=max=items.nextElement();
					while(items.hasMoreElements())
						{actual=items.nextElement();
						if(actual<min)
							min=actual;
						if(max<actual)
							max=actual;
						}
					//...e li assegno come valore di partenza per il calcolo del pred e del succ
					int pred=min,succ=max;
					items = nodeTable.keys();
					//prima devo calcolare il massimo
					while(items.hasMoreElements())
						{actual=items.nextElement();
						if(pred<actual&&actual<file_id)
							pred=actual;
						if(file_id<actual&&actual<succ)
							succ=actual;
						}
					//se max<file_id significa che il nuovo nodo ha l'id più grande di tutto l'anello
					//quindi il suo successore è il nodo con id più piccolo dell'anello
					if(max<file_id)
						{succ=min;}
					//viceversa, se file_id<min, il nuovo nodo è il nuovo primo elemento dell'anello
					//quindi il suo predecessore è il nodo con id più grande dell'anello
					if(file_id<min)
						{pred=max;}
					String predIP = "",succIP="",string;
					int predPort = 0,succPort=0;
					//estraggo dal database IP e porta TCP del successore
					string=nodeTable.get(succ);
					start=string.indexOf(" IP=");
					end=string.indexOf(" UDPport=");
					succIP=string.substring(start+4, end);
					start=string.indexOf(" TCPport=");
					succPort=Integer.parseInt(string.substring(start+9));
					//estraggo dal database IP e porta TCP del predecessore
					string=nodeTable.get(pred);
					start=string.indexOf(" IP=");
					end=string.indexOf(" UDPport=");
					predIP=string.substring(start+4, end);
					start=string.indexOf(" TCPport=");
					predPort=Integer.parseInt(string.substring(start+9));
					//invio i dati al nodo
					InetAddress ia=InetAddress.getByName("localhost");
					ByteArrayOutputStream bout = new ByteArrayOutputStream( );
					ObjectOutputStream out= new ObjectOutputStream(bout);
					out.writeObject(predIP);
					out.writeObject(succIP);
					out.writeObject(predPort);
					out.writeObject(succPort);
					out.writeObject(nodeTable);
					byte [] senddata = bout.toByteArray();
					dp= new DatagramPacket(senddata,senddata.length,ia, portSender);
					ds.send(dp);
					bout.reset();
					}
				//se mi ha contattato un client invece devo contattare (tramite il mio TCP) il nodo che detiene il dato cercato
				else
					{}
				}
			}
		catch (Exception e){e.printStackTrace();}
		}
}
