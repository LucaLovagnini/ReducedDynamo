import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.rmi.ConnectException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;



public class Client implements Runnable{
	ScannerPorte scanner;
	int id_data;
	NodeFileReader reader = new NodeFileReader("");
	String bootstrapServerIp=null;
	public Client (ScannerPorte scanner)
		{this.scanner=scanner;
		id_data=reader.getRandomIdData();
		}
	public Client (ScannerPorte scanner , String bootstrapServerIp)
	{this.scanner=scanner;
	this.bootstrapServerIp=bootstrapServerIp;
	id_data=reader.getRandomIdData();
	}
	public void run()
		{String ipBN=null;
		String fakeip=null;
		DatagramSocket UDPServiceSocket=null;
		BootstrapServer_Interface serverObject=null;
		try
			{//scelgo casualmente un dato da cercare		
			//genero il mio indirizzo ip
			fakeip = String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256));
			if(bootstrapServerIp!=null)
				fakeip=InetAddress.getLocalHost().getHostAddress();
			//creo una Socket UDP con cui contattare il Nodo di Bootstrap
			UDPServiceSocket = scanner.UDPSocket();
			if(UDPServiceSocket==null)
				{System.out.println("Tentativo di creazione socket UDP fallito...");
				return;}
			int UDPport = UDPServiceSocket.getLocalPort();
			fakeip=fakeip+" UDPport="+UDPport;
			//mi connetto al server tramite RMI
			Remote RemoteObject;
			Registry r;
			if(bootstrapServerIp!=null)
				r= LocateRegistry.getRegistry(bootstrapServerIp,10001);
			else
				r= LocateRegistry.getRegistry(10001);
			
			serverObject = (BootstrapServer_Interface) r.lookup("BOOTSTRAP-SERVER");
			//Ottengo l'ip del Bootstrap Node a cui connettermi
			System.out.println("Il client "+Thread.currentThread()+" si mette in attesa.");			
			ipBN = serverObject.client_search();
			if(ipBN==null||ipBN.equals("-1"))
				{System.out.println("Attualmente non ci sono nodi attivi, ricerca fallita");
				UDPServiceSocket.close();
				return;
				}
			else
				{System.out.println("Il client "+Thread.currentThread()+" inizia la ricerca del dato "+id_data);
				//ottengo porta UDP del bootstrap
				int start=ipBN.indexOf("UDPport=");
				int end=ipBN.indexOf(" TCPport=");
				String sPort = ipBN.substring(start+8,end);
				int bootStrapNodeport=Integer.parseInt(sPort);
				//ed il suo indirizzo ip
				ipBN=ipBN.substring(0,start-1);
				//mi connetto al nodo di bootstrap
				ByteArrayOutputStream bout=new ByteArrayOutputStream( );
				DataOutputStream dos= new DataOutputStream(bout);
				//la stringa type indica che è un client a contattatare il bootstrap
				String type = "client";
				dos.writeUTF(type);
				dos.writeInt(id_data);
				//viene inviato anche il proprio indirizzo IP e la porta UDP per permettere di ricevere una risposta
				//si ricorda che la risposta proverrà direttamente dal nodo detentore del dato e non dal nodo di bootstrap
				dos.writeUTF(fakeip);
				byte [ ] data=bout.toByteArray();
				InetAddress ia=InetAddress.getByName("localhost");
				if(bootstrapServerIp!=null)//versione di rete
					ia=InetAddress.getByName(ipBN);
				DatagramPacket dp= new DatagramPacket(data,data.length,ia, bootStrapNodeport);
				//invio il pacchetto al nodo di bootstrap...
				UDPServiceSocket.send(dp);
				//...ed attendo la risposta da parte del nodo detentore del dato
				data = new byte[200];
				dp = new DatagramPacket (data,data.length);
				UDPServiceSocket.receive(dp);
				ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData(),0,dp.getLength());
				DataInputStream din = new DataInputStream(bin);
				String risposta = din.readUTF();
				System.out.println("Risultato ricerca per id_data="+id_data+" rispsota="+risposta);	//stampo il risultato della ricerca
				UDPServiceSocket.close();//chiudo la socket
				serverObject.end_client_search();//rilascio la lock acqusita
				}
			}
		catch(ConnectException e){
			System.out.println("Connessione al server fallita per il client "+Thread.currentThread());
			UDPServiceSocket.close();
			}
		catch (Exception e) {
			e.printStackTrace();
			UDPServiceSocket.close();
			} 
		}
}
