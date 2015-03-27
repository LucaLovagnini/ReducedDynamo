import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class FileWriterNode {
	public synchronized void write (Hashtable<Integer,String> dataTable,int nodeId)
		{try 
		 	{String content = "This is the content to write into file";
	
			File file = new File("C:/Users/luca-kun/Documents/dataTable.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			bw.write("Elementi presenti nel nodo con id="+nodeId/*+System.getProperty("line.separator")*/ );
			Enumeration <Integer> items = dataTable.keys();
			while(items.hasMoreElements())
				{int idobj=items.nextElement();
				System.out.println(dataTable.get(idobj));
				bw.write("id="+idobj+" data="+dataTable.get(idobj)/*+System.getProperty("line.separator")*/ );
				}
			bw.close();
			System.out.println("Done");
		 	} 
		catch (IOException e) 
			{e.printStackTrace();}
		}
}
