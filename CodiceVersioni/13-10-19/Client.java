import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;



public class Client implements Runnable{
	ScannerPorte scanner;
	int id_data;
	public Client (ScannerPorte scanner,int id_data)
		{this.scanner=scanner;
		this.id_data=id_data;
		}
	public void run()
		{try
			{//genero il mio indirizzo ip
			String fakeip = String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256)) + "."
					+ String.valueOf((int) (Math.random() * 256));
			//creo una Socket UDP con cui contattare il Nodo di Bootstrap
			DatagramSocket UDPServiceSocket = scanner.UDPSocket();
			if(UDPServiceSocket==null)
				{System.out.println("Tentativo di creazione socket UDP fallito...");
				return;}
			int UDPport = UDPServiceSocket.getLocalPort();
			fakeip=fakeip+" UDPport="+UDPport;
			//mi connetto al server tramite RMI
			Remote RemoteObject;
			BootstrapServer_Interface serverObject;
			Registry r = LocateRegistry.getRegistry(10001);
			RemoteObject = r.lookup("BOOTSTRAP-SERVER");
			serverObject = (BootstrapServer_Interface)RemoteObject;
			//Ottengo l'ip del Bootstrap Node a cui connettermi
			String ipBN = serverObject.client_search();
			System.out.println("Tocca al client!");
			if(ipBN==null)
				{System.out.println("Attualmente non ci sono nodi attivi, riprovare più tardi");	
				return;
				}
			else
				{//ottengo porta UDP del bootstrap
				int start=ipBN.indexOf("UDPport=");
				int end=ipBN.indexOf(" TCPport=");
				String sPort = ipBN.substring(start+8,end);
				int bootStrapNodeport=Integer.parseInt(sPort);
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
		catch (Exception e) { e.printStackTrace();} 
		}
}
