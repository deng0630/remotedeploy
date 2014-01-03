package remotedeploy.dialog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class FileChangeWatcher {

	private WatchService watcher;    

	private StringBuilder pathStrs = new StringBuilder();
	
	private String[] pathArray;

	private Field implField;
	
	public FileChangeWatcher() throws ClassNotFoundException, NoSuchFieldException, SecurityException{
		Class clazz  = Class.forName("sun.nio.fs.AbstractWatchKey"); 
		implField = clazz.getDeclaredField("dir");
		implField.setAccessible(true);
	}

	public void addPath(String path) {    
		pathStrs.append(path).append(",");
	}     

	public void handleEvents() throws Exception{   
		watcher = FileSystems.getDefault().newWatchService();
		if (pathStrs.length()> 0) {
			pathStrs.substring(0, pathStrs.length()-1);
		}
		pathArray = pathStrs.toString().split(",");
		for (int i = 0; i < pathArray.length; i++) {
			Paths.get(pathArray[i]).register(watcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
		}
		WatchKey watckKey;
		for(;;) {
			watckKey = watcher.take();
			String dir =  implField.get(watckKey).toString(); 
			
			List<WatchEvent<?>> events = watckKey.pollEvents();   
			for (WatchEvent event : events) {
				try {
					Path child = Paths.get(dir).resolve(event.context().toString());//���·��  
					String subDir = dir.substring(ConfigDialog.source.length());
					String remoteDir = ConfigDialog.desc + subDir.replace(File.separator, "/");
					
					SshCopyUtil.createRemoteDirIfNotExist(remoteDir);
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						SshCopyUtil.put(child.toFile().getPath());
						ConsoleFactory.printToConsole("modify:>>  " + subDir+"\\"+event.context().toString());
					}
					else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						SshCopyUtil.put(child.toFile().getPath());
						ConsoleFactory.printToConsole("modify:>>  " + subDir+"\\"+event.context().toString());
					} 
					else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						ConsoleFactory.printToConsole("Delete:>>  " + subDir+"\\"+event.context().toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			boolean valid = watckKey.reset();
			if(!valid) {
				ConsoleFactory.printToConsole("############remote FileWatcher Faild ################",SWT.COLOR_RED);
				//watckKey.reset();
				continue;
/*				ConsoleFactory.printToConsole("############remote FileWatcher Faild ################",SWT.COLOR_RED);
				ConsoleFactory.printToConsole("############retry to watch the folder ......################",SWT.COLOR_BLUE);
				watcher = FileSystems.getDefault().newWatchService();
				for (int i = 0; i < pathArray.length; i++) {
					Paths.get(pathArray[i]).register(watcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
				}
				valid = watckKey.reset(); 
				if(!valid){
					ConsoleFactory.printToConsole("############retry to watch the folder faild ################",SWT.COLOR_RED);
					break;
				}
				ConsoleFactory.printToConsole("############retry to watch the folder success################",SWT.COLOR_BLUE);*/
			}
		}       
	}  

	public static void main(String[] args) {

		//define a folder root
		Path myDir = Paths.get(ConfigDialog.source);       

		try {
			WatchService watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

			for(;;) {
				WatchKey watckKey = watcher.take();

				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						ConsoleFactory.printToConsole("Created: " + event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						ConsoleFactory.printToConsole("Delete: " + event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						ConsoleFactory.printToConsole("Modify: " + event.context().toString());
					}
				}

				boolean valid = watckKey.reset();
				if(!valid) {
					// do some log work
					break;
				}
			}   // outer for loop ends           

		} catch (Exception e) {
			ConsoleFactory.printToConsole("Error: " + e.toString());
		}
	}
}
