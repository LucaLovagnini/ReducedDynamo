import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Hashtable;
public class NodeFileReader 
{	String file;
	Hashtable<Integer,String> data = new Hashtable<Integer,String>();
	public NodeFileReader(String file)
		{this.file=file;
		}
	public Hashtable<Integer,String> read ()
	{
	  try
		  {//Apro il file
		  FileInputStream fstream = new FileInputStream("textfile.txt");
		  // Get the object of DataInputStream
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  int data_id;
		  //Read File Line By Line
		  while ((strLine = br.readLine()) != null)   {
			  	//Genero l'identificatore del dato tramite la SHA-1
			  	MessageDigest md = MessageDigest.getInstance("SHA1");
			  	md.update(strLine.getBytes());
			  	byte[] output = md.digest();
			  	ByteBuffer bb = ByteBuffer.wrap(output);
			  	data_id = Math.abs((int) bb.getLong()) % (2 ^ 125);
			  	data.put(data_id, strLine);
		  		}
		  //Chiudo lo stream
		  in.close();
		  }
	  catch (Exception e)
	  		{e.printStackTrace();}
	  return data;
	}
	public int getRandomIdData()
		{//Apro il file
		try
			{FileInputStream fstream = new FileInputStream("textfile.txt");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  int data_id,data_id_line,data_counter=0;
			  //Calcolo il numero di righe del file
			  while ((strLine = br.readLine()) != null)   {
				  	data_counter++;
			  		}
			  //Chiudo lo stream
			  in.close();
			  fstream.close();
			  //scelgo una riga a caso
			  double double_data_id_line=Math.random()*data_counter;
			  data_id_line=(int) double_data_id_line;
			  data_counter=0;
			  //riapro il file
			  fstream = new FileInputStream("textfile.txt");
			  in = new DataInputStream(fstream);
			  br = new BufferedReader(new InputStreamReader(in));
			  //vado a quella riga
			  while ((strLine = br.readLine()) != null&&data_counter<data_id_line)   {
				  	data_counter++;
			  		}
			//Genero l'identificatore del dato tramite la SHA-1
			  MessageDigest md = MessageDigest.getInstance("SHA1");
			  md.update(strLine.getBytes());
			  byte[] output = md.digest();
			  ByteBuffer bb = ByteBuffer.wrap(output);
			  data_id = Math.abs((int) bb.getLong()) % (2 ^ 125);
			  return data_id;
			}
		catch(Exception e){e.printStackTrace();}
		return 0;
		 }
}