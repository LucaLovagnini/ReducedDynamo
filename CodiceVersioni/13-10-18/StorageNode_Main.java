import java.util.concurrent.*;
public class StorageNode_Main {

	public static void main(String[] args) {
		scannerporte scanner = new scannerporte();
		StorageNode task = new StorageNode(scanner);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0;i<2;i++)
			exec.execute(task);
		exec.shutdown();
	}

}
