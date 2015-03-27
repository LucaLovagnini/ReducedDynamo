import java.util.ArrayList;
import java.util.concurrent.*;

public class Join_Node implements Callable <String>{
	
		BootstrapServer_Monitor BSM;
		ArrayList storageNodeList = new ArrayList<String>();
			public Join_Node (BootstrapServer_Monitor BSM)
			{ this.BSM=BSM; 
			}
		
		public String call()
			{ 	try
					{BSM.start_join_node();
					//tocca a me
					}
				catch (Exception e)
					{e.printStackTrace();}
				BSM.end_join_node(); 
				return "";
			}

}
