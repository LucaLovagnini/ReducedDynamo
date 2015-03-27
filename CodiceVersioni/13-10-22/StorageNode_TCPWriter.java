import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;


public class StorageNode_TCPWriter extends Thread{
	StorageNode_TCPMonitor TCPM;
	ServerSocket TCPServiceSocket;
	List<Socket> list;
	int socketPort;
	public StorageNode_TCPWriter(StorageNode_TCPMonitor TCPM,ServerSocket TCPServiceSocket,List<Socket> list,int socketPort)
		{this.TCPM=TCPM;
		this.TCPServiceSocket=TCPServiceSocket;
		this.list=list;
		this.socketPort=socketPort;
		}
	public void run()
	{
		while (true)
		{TCPM.StartWrite();
		try
			{TCPServiceSocket.setSoTimeout(1000);
			while(true)
				{	Socket s = TCPServiceSocket.accept();
					list.add(s);//aggiungo il nuovo client a quelli che devono essere serviti
					//System.out.println("serverReader aggiunta socket");
				}
			}
		catch(SocketTimeoutException e)
			{/*System.out.println("TCP timeout!");*/}
		catch(Exception e)
			{e.printStackTrace();}
		TCPM.EndWrite(); //rilascio la lock
		}
	}
}
