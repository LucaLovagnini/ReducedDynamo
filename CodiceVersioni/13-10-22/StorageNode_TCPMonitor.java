

public class StorageNode_TCPMonitor {
	int readers = 0; 
	int waitingW = 0; 
	int waitingR = 0;
	boolean writing = false;
	boolean readersturn = false;
	
	synchronized void StartRead() {
		while(writing ||(waitingW>0 && !readersturn)) 
			{++waitingR;
			try
				{wait();} 
			catch (InterruptedException e) 
				{};
			--waitingR;
			}
		++readers; 
	}
	
	synchronized void EndRead() {
		readers = readers - 1; 
		readersturn=false;
		if (readers == 0) 
			notifyAll();
	}
	synchronized public void StartWrite() {
		while ((readers>0 || writing) || (waitingR>0 && readersturn))
			{++waitingW;
			try
				{wait();} 
			catch (InterruptedException e)
				{}
			--waitingW;
			}
		writing = true;
	}
	synchronized void EndWrite() {
		writing = false; 
		readersturn=true;
		notifyAll();
	}
}
