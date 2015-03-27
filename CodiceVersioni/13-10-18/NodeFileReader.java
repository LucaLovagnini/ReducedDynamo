import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Hashtable;
public class NodeFileReader 
{	String file;
	Hashtable<Integer,String> data;
	public NodeFileReader(String file,Hashtable<Integer,String> data)
		{this.file=file;
		this.data=data;
		}
	public Hashtable<Integer,String> read ()
	{
	  try
		  {//Apro il file
		  FileInputStream fstream = new FileInputStream("C:/Users/luca-kun/Documents/textfile.txt");
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
			  	System.out.println("id="+data_id+" strLine="+strLine);
		  		}
		  //Close the input stream
		  in.close();
		  }
	  catch (Exception e)
	  		{System.err.println("Error: " + e.getMessage());}
	  return data;
	}
}