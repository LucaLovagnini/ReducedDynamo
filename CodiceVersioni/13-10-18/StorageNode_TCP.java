import java.net.*;
import java.util.*;
import java.io.*;


public class StorageNode_TCP extends Thread{
	ServerSocket TCPServiceSocket;
	String predIP,succIP,myIP;//rispettivamente l'indirizzo ip del mio predecessore, successore ed il mio indirizzo IP
	int predPort,succPort,myPort,myId;//rispettivamente la porta TCP del mio predecessore, del mio successore, la mia porta TCP e il mio identificatore
	int node_number;//questo parametro indica se questo oggetto fa riferimento al primo nodo creato oppure no
	Hashtable<Integer,String> dataTable;
	Thread chiamante;//il thread che richiama il metodo. Esso rimarrà in stato di join fino ad un nostro segnale di interrupt.
					//in questo modo garantiamo che esso non rilasci la lock acquisita fino al quando non siamo "operativi" come nodo
	public StorageNode_TCP
	(Thread chiamante,ServerSocket TCPServiceSocket,String predIP,String succIP,String fakeip,int predPort,int succPort,int myId,Hashtable<Integer,String> dataTable,int node_number)
		{this.chiamante=chiamante;
		this.TCPServiceSocket=TCPServiceSocket;
		this.predIP=predIP;
		this.succIP=succIP;
		this.predPort=predPort;
		this.succPort=succPort;
		this.myId=myId;
		this.node_number=node_number;
		this.dataTable=dataTable;
		//ottengo il mio indirizzo ip
		int end = fakeip.indexOf("UDPport=");
		this.myIP=fakeip.substring(0,end);
		int start = fakeip.indexOf("TCPport=");
		//ottengo la mia porta TCP
		this.myPort = Integer.parseInt(fakeip.substring(start+8, fakeip.length()));
		}
	public void run()
		{//se non sono il primo nodo contatto il mio predecessore
		if(node_number!=1)
			try
				{/*Socket socketPred = new Socket();
				socketPred.connect(new InetSocketAddress("LocalHost",predPort));*/
				//non c'è altro da fare con il predecessore!
				}
			catch (Exception e){e.printStackTrace();}
		//contatto il mio successore
		if(node_number!=1)
			try
				{Socket socketSucc = new Socket();
				socketSucc.connect(new InetSocketAddress("localhost",succPort));
				BufferedReader reader = new BufferedReader(new InputStreamReader(socketSucc.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketSucc.getOutputStream()));
				//comunico al mio successore il mio id, in modo che possa capire quali dati inviarmi
				writer.write("nuovo nodo");
				writer.newLine();
				writer.flush();
				writer.write(myId);
				writer.flush();
				String data;
				int id_data;
				do
					{id_data=reader.read();	
					data=reader.readLine();
					System.out.println("id_data="+id_data+" data="+data);
					if(id_data!=-1&&data!=null)
						dataTable.put(id_data, data);
					}
				while(id_data!=-1);
				writer.close();
				reader.close();
				}
			catch (Exception e){e.printStackTrace();}
		chiamante.interrupt();//ora che il collegamento TCP del nodo è operativo e la tabella aggiornata, posso dire al Thread chiamante di continuare
		//rimango in ascolto
		while(true)
			{try
				{//ricevo una richiesta da qualcuno
				Socket socket = TCPServiceSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//se mi contatta un nuovo nodo devo aprire anche uno stream in uscita
				if(reader.readLine().equals("nuovo nodo"))
					{BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					int id_node=reader.read();
					System.out.println("suo id="+id_node);
					System.out.println("mio id="+myId);
					Enumeration <Integer> items = dataTable.keys();
					while(items.hasMoreElements())
						{int id_data=items.nextElement();
						//Da considerare due casi in questo if:
						//Il primo è il caso in cui il nuovo nodo ha un identificatore più piccolo del mio (ovvero non è il nodo con identificatore più grande dell'anello)
						//Nell'altro il caso il nuovo nodo ha l'identificatore più grande del mio (ovvero è il nodo con l'identificatore più grande dell'anello)
						if((id_data<myId&&id_node<id_data)||(myId<id_node&&myId<id_data&&id_data<id_node))
							{String data = dataTable.get(id_data);
							writer.write(id_data);
							writer.write(data);
							writer.newLine();
							writer.flush();
							//cancello dalla mia tabella il dato che ho trasmesso al nuovo nodo (mio nuovo predecessore)
							dataTable.remove(id_data);
							}
						}
					//id_data=-1 significa che non ci sono altri dati 
					writer.write(-1);
					writer.flush();
					reader.close();
					writer.close();
					}
				//se mi contatta un client allora non necessito dello stream in uscita
				//in realtà a contattarmi non è direttamente il client, ma una socket TCP creta dal Thread che gestisce l'UDP del mio stesso nodo
				else
					{}
				}
			catch(Exception e)
				{e.printStackTrace();}
			
			}
		//se qualcuno mi contatta vuol dire che è il mio nuovo predecessore e (forse) gli devo passare della roba
		}
	}
