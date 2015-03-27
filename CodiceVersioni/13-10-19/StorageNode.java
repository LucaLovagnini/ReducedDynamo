
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
	ScannerPorte scanner;
	static int node_number=0;
	public StorageNode (ScannerPorte scanner)
		{this.scanner=scanner;
		}
	public void run ()
	{int file_id;
	String predIP="",succIP="";//questi due indirizzi ip appartengono rispettivamente al mio predecessore e il mio successore
	int predPort=0, succPort=0;//analoghi agli indirizzi a predIP e succIP, ma identificano le porte tcp da contattare
	Hashtable<Integer,String> nodeTable = new Hashtable<Integer, String>();
	Hashtable<Integer,String> dataTable = new Hashtable <Integer,String>();
	//genero il mio indirizzo ip
	String fakeip = String.valueOf((int) (Math.random() * 256)) + "."
	+ String.valueOf((int) (Math.random() * 256)) + "."
			+ String.valueOf((int) (Math.random() * 256)) + "."
			+ String.valueOf((int) (Math.random() * 256));
	//creo già le Socket d'ingresso su cui il nodo verrà contattato
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
		//Se continuo, allora ho ottenuto la lock dal BootstrapServerMain
		//Se sono il primo nodo leggo tutti i dati che saranno presenti nel sistema
		node_number++;
		if(node_number==1)
			{NodeFileReader reader = new NodeFileReader ("C:/Users/luca-kun/Documents/textfile.txt",dataTable);
			dataTable=reader.read();
			if(dataTable.isEmpty())
				System.out.println("Errore: tabella dati vuota");
			}
		//Genero il mio identificatore tramite la SHA-1
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(fakeip.getBytes());
		byte[] output = md.digest();
		ByteBuffer bb = ByteBuffer.wrap(output);
		file_id = Math.abs((int) bb.getLong()) % (2 ^ 125);
		//se sono il primo nodo 
		//altrimenti devo contattare il nodo di bootstrap

		if(!ipBN.equals("0"))
			{//ottengo porta UDP del bootstrap
			int start=ipBN.indexOf("UDPport=");
			int end=ipBN.indexOf(" TCPport=");
			String sPort = ipBN.substring(start+8,end);
			int bootStrapNodeport=Integer.parseInt(sPort);
			//creo il collegamento verso il bootstrap
			ByteArrayOutputStream bout=new ByteArrayOutputStream( );
			DataOutputStream dos= new DataOutputStream(bout);
			//la stringa type indica che è un nodo a contattatare il bootstrap
			String type = "node";
			dos.writeUTF(type);
			//viene inviato anche l'identificatore del proprio nodo
			//così facendo il nodo di bootstrap può calcolare il nostro succ. e pred.
			dos.writeInt(file_id);
			//vengono inviati anche i propri indirizzi per permettere di ricevere una risposta
			dos.writeUTF(fakeip);
			byte [ ] data=bout.toByteArray();
			InetAddress ia=InetAddress.getByName("localhost");
			DatagramPacket dp= new DatagramPacket(data,data.length,ia, bootStrapNodeport);
			//invio il pacchetto al nodo di bootstrap...
			UDPServiceSocket.send(dp);
			//...il quale mi risponderà con l'indirizzo ip + porta del predecessore e del successore...
			//data ora potrà contenere il pacchetto vero e proprio
			data = new byte[5000];
			dp= new DatagramPacket(data,data.length);
			UDPServiceSocket.receive(dp);
			ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData());
			bin = new ByteArrayInputStream(dp.getData());
			ObjectInputStream oin = new ObjectInputStream(bin);			
			predIP=(String) oin.readObject();
			succIP=(String) oin.readObject();
			predPort = (Integer) oin.readObject();
			succPort = (Integer) oin.readObject();
			//...e la tabella dei nodi presenti nella rete
			nodeTable = (Hashtable<Integer,String>) oin.readObject();
			}
		//aggiungo il mio identificatore alla tabella
		nodeTable.put(file_id, "id="+file_id+" IP="+fakeip);
		//creo il thread che si occupa del multicast (passandogli la tabella attualmente esistente)
		//l'oggetto che gestisce il multicast aggiorna la tabella ogni volta che si inserisce un nodo
		StorageNode_Multicast multicast = new StorageNode_Multicast(nodeTable,"id="+file_id+" IP="+fakeip);
		multicast.start();
		//creare il mio collegamento udp in ingresso (passandogli l'oggetto che gestisce il multicast)
		//l'oggetto del multicast serve per poter ottenere quando necessario la tabella aggiornata
		StorageNode_UDP UDP = new  StorageNode_UDP(UDPServiceSocket,multicast,scanner);
		UDP.start();
		//se non sono il primo nodo connettermi al mio successore e predecessore con tcp
		StorageNode_TCP TCP = new StorageNode_TCP(Thread.currentThread(),TCPServiceSocket,predIP,succIP,fakeip,predPort,succPort,file_id,dataTable,node_number,multicast,scanner);
		try
		{TCP.start();
		TCP.join();//leggere la descrizione dell'attributo "chiamante" nella classe StorageNode_TCP per approfondimenti su questo punto
		}
		catch(InterruptedException e){}
		//Aggiunta del nodo completata
		System.out.println(Thread.currentThread()+" aggiunto");
		serverObject.end_join_node();
		}
	catch (NoSuchAlgorithmException e) {e.printStackTrace();}
	catch (Exception e) { e.printStackTrace();} 
	}
}
