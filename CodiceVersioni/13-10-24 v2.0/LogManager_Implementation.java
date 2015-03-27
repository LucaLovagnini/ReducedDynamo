import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;


public class LogManager_Implementation extends UnicastRemoteObject implements LogManager_Interface {
	DateFormat dateFormat;
	Date date;
	FileWriter fw;
	BufferedWriter bw;
	
	public LogManager_Implementation( ) throws RemoteException { 
		super( ); 
		try
			{dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			date = new Date();
			fw = new FileWriter("LogManager.txt");
			bw = new BufferedWriter(fw);
			bw.write("LogManager");
			bw.newLine();
			bw.flush();
			System.out.println("Creazione LogManager completata");
			}
		catch(Exception e){e.printStackTrace();}
		}
	public void notifyMe (String ip) throws RemoteException
		{date=new Date();
		try
			{bw.write(date+" ip="+ip);
			bw.newLine();
			bw.flush();
			}
		catch(Exception e){e.printStackTrace();}
			
		}
}
