package remotedeploy.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SshCopyUtil {
	
	private static SCPClient client;
	private static SFTPv3Client sftpClient;
	
	static processDialog tipDialog;
	
	public static String executeCommand(Shell shell) {
		if (ConfigDialog.host == null || ConfigDialog.user == null || ConfigDialog.passwd == null
				|| ConfigDialog.source == null || ConfigDialog.desc == null) {
			ConsoleFactory.printToConsole("Can't execute SCP command. Please check \"hostname\" \"username\" and \"password\"");
			return "check your config properties,every field must not be null";
		}
		try {
			final Connection conn = new Connection(ConfigDialog.host);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(ConfigDialog.user, ConfigDialog.passwd);
			if (isAuthenticated == false) {
				ConsoleFactory.printToConsole("Authenticated false!!!");
				return "Authenticated false,check your config is right or not";
			}
			client = new SCPClient(conn);
			sftpClient = new SFTPv3Client(conn);  
			
			if(ConfigDialog.initWithCopy.equals("1")){
				ConsoleFactory.printToConsole("##############initialing......####################");
				
				try{
					delRemote(ConfigDialog.desc,conn);
				}catch(Exception e){
					return "got IOException  rmdir"+"\n Message:"+e.getMessage()+" \n course:"+e.getCause();
				}
				
				try{
					sftpClient.mkdir(ConfigDialog.desc, 0755);
				}catch(Exception e){
					return "got IOException  mkdir"+"\n Message:"+e.getMessage()+" \n course:"+e.getCause();
				}
				
				copy(new File(ConfigDialog.source));
			}
			if(ConfigDialog.runAfterWatch.equals("1")){
				new Thread(new runTheProject(conn)).start();
			}
				
			
			
		} catch (IOException ex) {
			return "got IOException"+"\n Message:"+ex.getMessage()+" \n course:"+ex.getCause();
		} catch(Exception ex){
			return "got exception"+"\n Message:"+ex.getMessage()+" \n course:"+ex.getCause();
		}
		ConsoleFactory.clean();
		ConsoleFactory.printToConsole("##############started new remote watcher####################3");
		return "success";
	}
	
	public static void runTheProject(Connection conn){
		
	}
	
	public static void copy(File source){
		
		if(source.isFile() ){		//文件
			if(source.length() < 1024*1024){
				put(source.getPath());
			}
		}else{						//文件夹
			try {
				String remoteDir = ConfigDialog.desc + source.getPath().substring(ConfigDialog.source.length()).replace(File.separator, "/");
				sftpClient.mkdir(remoteDir, 0755);
			} catch (IOException e) {
			} 
			File[] files = source.listFiles();
			if(files != null && files.length > 0){
				for(File file: files){
					copy(file);
				}
			}
		}
	}

	public static void put(String sourceFile){
		try {
			if(new File(sourceFile).isDirectory()){
				try {
					String remoteDir = ConfigDialog.desc + sourceFile.substring(ConfigDialog.source.length()).replace(File.separator, "/");
					sftpClient.mkdir(remoteDir, 0755);
					copy(new File(sourceFile));
				} catch (IOException e) {
//					e.printStackTrace();
				} 
				return ;
			}
			String pathSym = sourceFile.substring(ConfigDialog.source.length()).replace(File.separator, "/");
			String remoteFile = ConfigDialog.desc + pathSym;
			if(remoteFile.length() == ConfigDialog.desc.length()){
				if(getFileMD5String(new File(sourceFile)) != getFileMD5String(new File(remoteFile)))
					client.put(sourceFile, remoteFile, ConfigDialog.filemode);
				return;
			}
			
			File lFile = new File(sourceFile);
			SFTPv3FileAttributes attrs = null;
			try {
				attrs = sftpClient.stat(remoteFile);
			} catch (Exception e) {
			}
			if(attrs == null || attrs.size!=lFile.length()){
				if(lFile.exists()){
					client.put(sourceFile, remoteFile.substring(0, remoteFile.lastIndexOf("/")), ConfigDialog.filemode);
				}
			}
				
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void delRemote(String remotePath,Connection conn){
		try {
			Session session = conn.openSession();
			
			session.execCommand("rm -rf ".concat(remotePath));
			
			//显示执行命令后的信息
			InputStream stdout = new StreamGobbler(session.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				ConsoleFactory.printToConsole("远程服务器返回信息:" + line);
			}
			
			//获得退出状态
			ConsoleFactory.printToConsole("ExitCode: " + session.getExitStatus());
			session.close();
		} catch (IOException e) {
			
			//如果删除失败则进行递归删除
			try {
				SFTPv3FileAttributes attrs = null;
				attrs = sftpClient.stat(remotePath);
				if(!attrs.isDirectory()){
					sftpClient.rm(remotePath);
				}
				else{
					Vector<SFTPv3DirectoryEntry> files = new Vector<SFTPv3DirectoryEntry>();
						files = sftpClient.ls(remotePath);
					for (SFTPv3DirectoryEntry sd : files) {
						if(!sd.filename.replace(".", "").equals("")){
							delRemote(remotePath+"/"+sd.filename,conn);
						}
					}
						sftpClient.rmdir(remotePath);
				}
			} catch (IOException e1) { }
		}
		
		
		
	}

	public static boolean createRemoteDirIfNotExist(String remoteDir) throws IOException{
		try {
			SFTPv3FileAttributes attrP = null;
			
			try {
				attrP = sftpClient.stat(remoteDir);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			if (attrP == null) {
				try {
					String parentDir = remoteDir.substring(0,remoteDir.lastIndexOf("/"));
					createRemoteDirIfNotExist(parentDir);
					sftpClient.mkdir(remoteDir, 074);
					return true;
				} catch (Exception e) {
					System.out.println("create remote dir faild");
					return false;
				}
			}
		} catch (Exception e) {
		}
		return true;
	}
	
	/**
	* 默认的密码字符串组合，apache校验下载的文件的正确性用的就是默认的这个组合
	*/
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
	protected static MessageDigest messagedigest = null;
	static{
	   try{
	    messagedigest = MessageDigest.getInstance("MD5");
	   }catch(NoSuchAlgorithmException nsaex){
	    ConsoleFactory.printToConsole("md5 initial fail");
	    nsaex.printStackTrace();
	   }
	}

	/**
	* 适用于上G大的文件
	* @param file
	* @return
	* @throws IOException
	*/
	public static String getFileMD5String(File file) throws IOException {
	   FileInputStream in = new FileInputStream(file);
	   FileChannel ch = in.getChannel();
	   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
	   messagedigest.update(byteBuffer);
	   return bufferToHex(messagedigest.digest());
	}

	public static String getMD5String(String s) {
	   return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
	   messagedigest.update(bytes);
	   return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
	   return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
	   StringBuffer stringbuffer = new StringBuffer(2 * n);
	   int k = m + n;
	   for (int l = m; l < k; l++) {
	    appendHexPair(bytes[l], stringbuffer);
	   }
	   return stringbuffer.toString();
	}


	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
	   char c0 = hexDigits[(bt & 0xf0) >> 4];
	   char c1 = hexDigits[bt & 0xf];
	   stringbuffer.append(c0);
	   stringbuffer.append(c1);
	}

	public static boolean checkPassword(String password, String md5PwdStr) {
	   String s = getMD5String(password);
	   return s.equals(md5PwdStr);
	}
}
