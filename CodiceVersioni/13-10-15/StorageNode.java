
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class StorageNode implements Runnable {
	//Ogni elemento della Hashtable è formato dalla stringa=IP+spazio+porta e dall'id SHA-1
	int file_id;
	public void run ()
	{System.out.println(Thread.currentThread());
	//genero il mio indirizzo ip
	String fakeip = String.valueOf((int) (Math.random() * 256)) + "."
	+ String.valueOf((int) (Math.random() * 256)) + "."
			+ String.valueOf((int) (Math.random() * 256)) + "."
			+ String.valueOf((int) (Math.random() * 256));
	//creo già le Socket d'ingresso su cui il nodo verrà contattato
	scannerporte scanner = new scannerporte();
	//Socket UDP di servizio: serve per esser contattato quando si è un nodo di bootstrap
	DatagramSocket UDPServiceSocket = scanner.UDPSocket();
	if(UDPServiceSocket==null)
		{System.out.println("Tentativo di creazione socket UDP fallito...");
		return;}
	//Socket TCP di servizio: server per esser contattato da altri nodi
	ServerSocket TCPServiceSocket = scanner.TCPSocket();
	if(TCPServiceSocket==null)
		{System.out.println("Tentativo di creazione socket TCP fallito...");
		return;}
	int UDPport = UDPServiceSocket.getLocalPort();
	int TCPport = TCPServiceSocket.getLocalPort();
	fakeip=fakeip+" UDPport="+UDPport+" TCPport="+TCPport;
	try
		{//mi connetto al server tramite RMI
		Remote RemoteObject;
		BootstrapServer_Interface serverObject;
		Registry r = LocateRegistry.getRegistry(10001);
		RemoteObject = r.lookup("BOOTSTRAP-SERVER");
		serverObject = (BootstrapServer_Interface)RemoteObject;
		//Ottengo l'ip del Bootstrap Node a cui connettermi
		String ipBN = serverObject.start_join_node(fakeip);
		System.out.println("Risposta= "+ipBN);
		//Genero il mio identificatore tramite la SHA-1
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(fakeip.getBytes());
		byte[] output = md.digest();
		ByteBuffer bb = ByteBuffer.wrap(output);
		file_id = Math.abs((int) bb.getLong()) % (2 ^ 125);
		System.out.println("file_id="+file_id);
		//se sono il nodo iniziale
		/*if(ipBN.equals("0"))
			{
			
			}
		//altrimenti devo contattare il nodo di bootstrap
		else
			{//ottengo porta UDP del bootstrap
			int start=ipBN.indexOf("UDPport=");
			int end=ipBN.indexOf(" TCPport=");
			String sPort = ipBN.substring(start+8,end);
			int bootStrapNodeport=Integer.parseInt(sPort);
			System.out.println("port="+bootStrapNodeport);
			//creo il collegamento verso il bootstrap
			ByteArrayOutputStream bout=new ByteArrayOutputStream( );
			ObjectOutputStream oo= new ObjectOutputStream(bout);
			//la stringa type indica che è un nodo a contattatare il bootstrap
			String type = "node";
			oo.writeObject(type);
			//viene inviato anche l'identificatore del proprio nodo
			//così facendo il nodo di bootstrap può calcolare il nostro succ. e pred.
			oo.writeObject(file_id);
			byte [ ] data=bout.toByteArray();
			InetAddress ia=InetAddress.getByName("localhost");
			DatagramPacket dp= new DatagramPacket(data,data.length,ia, bootStrapNodeport);
			//invio il pacchetto al nodo di bootstrap...
			UDPServiceSocket.send(dp);
			data = new byte [516];
			dp= new DatagramPacket(data,data.length,ia, bootStrapNodeport);
			//...il quale mi risponderà con l'indirizzo ip + porta del predecessore e del successore...
			UDPServiceSocket.receive(dp);
			ByteArrayInputStream bin = new ByteArrayInputStream(data);
			ObjectInputStream oin = new ObjectInputStream(bin);
			String predIP=(String) oin.readObject();
			String succIP=(String) oin.readObject();
			int predPort = (Integer) oin.readObject();
			int succPort = (Integer) oin.readObject();
			//...e la tabella dei nodi presenti nella rete
			Hashtable<Integer,String> nodeTable = new Hashtable<Integer, String>();
			}
		*/
		Hashtable<Integer,String> nodeTable = new Hashtable<Integer, String>();
		//creo il thread che si occupa del multicast (passandogli la tabella attualmente esistente)
		//l'oggetto che gestisce il multicast aggiorna la tabella ogni volta che si inserisce un nodo
		StorageNode_Multicast multicast = new StorageNode_Multicast(nodeTable,"id="+file_id+" IP="+fakeip);
		multicast.start();
		//creare il mio collegamento udp in ingresso (passandogli l'oggetto che gestisce il multicast)
		
		//se client oppure un nuovo nodo contatta oggetto udp, quest'ultimo guarda tabella aggioranta
		//interpellando l'oggetto di multicast
		//se non sono il primo nodo connettermi al mio successore e predecessore con tcp
		
		//Aggiunta del nodo completata
		serverObject.end_join_node();
		}
	catch (NoSuchAlgorithmException e) {e.printStackTrace();}
	catch (Exception e) { e.printStackTrace();} 
	}
}
