import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class BootstrapServer_KeepAlive extends Thread {
	int k,network;
	ArrayList<String> storageNodeList;
	ArrayList<String> gi‡Visti;
	List<DatagramPacket> dpList;
	BootstrapServer_Implementation BS;
	BootstrapServer_Monitor BSM;
	DatagramSocket ds;
	public BootstrapServer_KeepAlive(BootstrapServer_Implementation BS,BootstrapServer_Monitor BSM,int network)
		{this.setDaemon(true);
		storageNodeList= new ArrayList();
		gi‡Visti= new ArrayList();
		dpList = new ArrayList();
		this.BSM=BSM;
		this.BS=BS;
		this.network=network;
		try
			{ds=new DatagramSocket(10000);}//apro una socket UDP in ingresso
		catch(SocketException e){e.printStackTrace();}
		} 
	public void run()
		{try
			{ds.setSoTimeout(5000);//timeout di ascolto di 15 secondi
			int giro=0;
			while(true)
				{System.out.println("ciclo numero "+giro+":");
				giro++;
				try
					{while(true)
						{byte []data = new byte[200];
						DatagramPacket dp = new DatagramPacket (data,data.length);
						//ogni pacchetto ricevuto viene aggiunto ad una lista
						ds.receive(dp);
						dpList.add(dp);
						ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData(),0,dp.getLength());
						DataInputStream din = new DataInputStream(bin);
						System.out.println(din.readUTF()+" RICEVUTO!");
						}
					}
				catch(SocketTimeoutException e){System.out.println("Timeout!");}//Timeout!
				catch(Exception e){e.printStackTrace();}
				boolean trovato,daAggiornare=false;
				ByteArrayOutputStream bout=new ByteArrayOutputStream( );
				DataOutputStream dos= new DataOutputStream(bout);
				storageNodeList=BS.getStorageNodeList();//prendo dal BootStrapServer la lista aggiornata
				System.out.println("Stampa aggiornata della tabella:");
				for(int i=0;i<storageNodeList.size();i++)
					System.out.println(storageNodeList.get(i));
				for(int i=0;i<storageNodeList.size();i++)
					{System.out.println("Cerco di trovare "+storageNodeList.get(i));
					trovato=false;
					for(int j=0;j<dpList.size();j++)
						{ByteArrayInputStream bin = new ByteArrayInputStream(dpList.get(j).getData(),0,dpList.get(j).getLength());
						DataInputStream din = new DataInputStream(bin);
						String senderMessage = din.readUTF();
						//ricavo il numero della porta della socket UDP del StorageeNode_KeepAlive in questione
						int senderPort = 
								Integer.parseInt(senderMessage.substring(0, senderMessage.indexOf(" ip=")));
						senderMessage = senderMessage.substring(senderMessage.indexOf(" ip=")+4);
						if(senderMessage.equals(storageNodeList.get(i)))
							{trovato=true;
							dpList.remove(j);
							InetAddress ia = InetAddress.getLocalHost();
							if(network==1)//versione di rete
								ia = InetAddress.getByName(storageNodeList.get(i).substring(0, storageNodeList.get(i).indexOf(" UDPport=")));
							dos.writeUTF("ok");//avviso il nodo che ho ricevuto il suo pacchetto
							byte [ ] data=bout.toByteArray();
							DatagramPacket dp = new DatagramPacket(data,data.length,ia, senderPort);
							ds.send(dp);
							bout.reset();
							System.out.println(storageNodeList.get(i).substring(0, storageNodeList.get(i).indexOf(" UDPport="))+"inviato!");
							break;
							}
						din.close();
						}
					if(!trovato)
						{//il nodo in posizione i non ha risposto
						//non posso fare direttamente una remove altrimenti la dimensione della lista si altererebbe
						storageNodeList.set(i, "to delete");
						daAggiornare=true;//ho modificato la lista dei nodi di boostrap: devo aggiornare quella del Bootstrap Server
						}
					}
				//ora posso cancellare gli elementi dalla lista che non hanno risposto
				while(storageNodeList.contains("to delete"))
					{for(int i=0;i<storageNodeList.size();i++)
						if(storageNodeList.get(i).equals("to delete"))
							storageNodeList.remove(i);
					}
				System.out.println("Nuova storageNodeList:");
				for(int i=0;i<storageNodeList.size();i++)
					System.out.println(storageNodeList.get(i));
				//se ho modificato la lista, cerco di accedere alla lista (ho la priorit‡ sia sui nuovi nodi che sui client)
				if(daAggiornare)
					{BSM.start_keep_alive();
					BS.setStorageNodeList(storageNodeList);
					BSM.end_keep_alive();
					}
				//elimino tutti gli elementi rimanenti in dpList (possibili risposte perse dei vecchi bootstrap node)
				for(int i=dpList.size()-1;i>=0;i--)
					dpList.remove(i);
				}
			}
		catch(Exception e){e.printStackTrace();}
		}
}
