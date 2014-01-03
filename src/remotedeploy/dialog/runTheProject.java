package remotedeploy.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class runTheProject implements Runnable {

	private Connection conn;
	
	public runTheProject(){}
	
	public runTheProject(Connection conn){
		this.conn = conn;
	}
	
	@Override
	public void run() {
		try {
			Session session = conn.openSession();
			
			session.execCommand("cd /opt/CreateC6-405/bin \n chmod 075 dyx_run.sh \n ./dyx_run.sh");
			
			//显示执行命令后的信息
			InputStream stdout = new StreamGobbler(session.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				//System.out.println("remote: " + line);
				ConsoleFactory.printToConsole(new String(line.getBytes("utf-8")));
				Thread.sleep(20L);
			}
		} catch (IOException e) {
			ConsoleFactory.printToConsole("##############启动远程项目失败,需手动运行#############",SWT.COLOR_RED);
		} catch (InterruptedException e) {
			ConsoleFactory.printToConsole("##############启动远程项目失败,需手动运行#############",SWT.COLOR_RED);
		}
	}

}
