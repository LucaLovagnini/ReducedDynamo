import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;


public class StorageNode_KeepAlive extends Thread{
	String myIp;
	ScannerPorte scanner;
	String bootstrapServerIp;
	public StorageNode_KeepAlive(String myIp,String bootstrapServerIp,ScannerPorte scanner)
		{this.setDaemon(true);
		this.myIp=myIp;
		this.scanner=scanner;
		this.bootstrapServerIp=bootstrapServerIp;
		}
	public StorageNode_KeepAlive(String myIp,ScannerPorte scanner)
	{this.myIp=myIp;
	this.bootstrapServerIp=null;
	this.scanner=scanner;
	}
	public void run()
		{try
			{ByteArrayOutputStream bout=new ByteArrayOutputStream( );
			DataOutputStream dos= new DataOutputStream(bout);
			DatagramSocket ds = scanner.UDPSocket();
			ds.setSoTimeout(15000);//timeout di ascolto di 15 secondi
			DatagramPacket dp;
			while(true)
				{InetAddress ia;
				if(bootstrapServerIp!=null)	//versione di rete
					ia = InetAddress.getByName(bootstrapServerIp);
				else						//versione locale
					ia = InetAddress.getLocalHost();
				//oltre gli indirizzi del nodo a cui faccio riferimento, scrivo anche la porta della socket UDP a cui risoondere
				dos.writeUTF(ds.getLocalPort()+" ip="+myIp);
				byte [ ] data=bout.toByteArray();
				dp = new DatagramPacket(data,data.length,ia, 10000);
				//simulazione dell'uscita di un nodo
				/*int caso = (int)(Math.random()*5);
				if(caso==1&&ds.getLocalPort()!=1025)//1025 perchè così garantiamo (in questa macchina) che il primo nodo non esca
					return;*/
				ds.send(dp);
				bout.reset();
				ds.receive(dp);//mi metto in ascolto per quindici secondi
				ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData(),0,dp.getLength());
				DataInputStream din = new DataInputStream(bin);
				String s = din.readUTF();
				if(!s.equals("ok"))//errore
					break;
				}
			
			}
		catch(SocketTimeoutException e){System.out.println(myIp+" Nessuna risposta!");}
		catch(Exception e){e.printStackTrace();}
		}

}
