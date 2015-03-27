import java.util.concurrent.*;
public class StorageNode_Main {

	public static void main(String[] args) {
		StorageNode task = new StorageNode();
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0;i<10;i++)
			exec.execute(task);
		exec.shutdown();
	}

}
