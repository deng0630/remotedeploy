package remotedeploy.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileChangeWatcherThread  extends Thread{

	public static Map<String, Thread> pathThread = new ConcurrentHashMap<String,Thread>();
	
	private String path = null;
	
	FileChangeWatcher watch;
	
	private StringBuilder recPaths = new StringBuilder();
	
	public static void stop(String path){
		if(pathThread.containsKey(path)){
			ConsoleFactory.printToConsole("############## stop old remote watcher ##############");
			pathThread.get(path).interrupt();
			pathThread.remove(path);
		}
	}
	
	public static void start(String path){
		stop(path);
		FileChangeWatcherThread thread = new FileChangeWatcherThread(path);
		thread.start();
		pathThread.put(path, thread);
	}
	
	public FileChangeWatcherThread(String path){
		this.path = path;
		pathThread.put(path, this);
	}
	
	@Override
	public void run() {
		try {
			watch = new FileChangeWatcher();
			
			File[] sourceFile = new File(this.path).listFiles();
			for (File file : sourceFile) {
				addPath(file);
			}
			watch.handleEvents();
		} catch (IOException e) {
//			e.printStackTrace();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addPath(File file){
		if (file.isDirectory()) {
			for (File tfile : file.listFiles()) {
				addPath(tfile);
			}
			watch.addPath(file.getPath());
		}
	}

}
