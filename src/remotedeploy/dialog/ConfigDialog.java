package remotedeploy.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConfigDialog extends Dialog {

	public static String host = "";
	public static String port = "22";
	public static String user = "";
	public static String passwd = "";
	public static String desc = "";
	public static String source = "";
	public static String filemode="0644";
	public static String initWithCopy="0";
	public static String runAfterWatch="1";

//	public static String host = "10.20.146.95";
//	public static String port = "22";
//	public static String user = "admin";
//	public static String passwd = "admin";
//	public static String desc = "/home/admin/test";
//	public static String source = "";
	
	
	
	private Text textHost;
	private Text textPort;
	private Text textUser;
	private Text textPasswd;
	private Text textSource;
	private Text textDesc;
	private Text textMode;
	private Button withCopy;
	private Button runProject;

	public ConfigDialog(Shell parentShell) {
		super(parentShell);
		PropertiesUtil.init();
	}

	/**
     * 在这个方法里构建Dialog中的界面内容
     */
	public Control createDialogArea(Composite parent) {
		getShell().setText("remote config"); // 设置Dialog的标头

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.marginLeft = 10;
        parent.setLayout(layout);

        Label labelHost = new Label(parent, SWT.NORMAL);
        labelHost.setText("host");
        textHost = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textHost.setText(host); // 设置text中的内容
        // textHost.setSize(100, 20);

        Label labelPort = new Label(parent, SWT.NORMAL);
        labelPort.setText("port");
        // labelPort.setSize(20, 20);
        textPort = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textPort.setText(port); // 设置text中的内容
        // textPort.setSize(50, 20);

        Label labelUser = new Label(parent, SWT.NORMAL);
        labelUser.setText("username");
        // labelUser.setSize(20, 20);
        textUser = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textUser.setText(user); // 设置text中的内容

        Label labelPasswd = new Label(parent, SWT.NORMAL);
        labelPasswd.setText("password");
        // labelPasswd.setSize(20, 20);
        textPasswd = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textPasswd.setText(passwd); // 设置text中的内容

        Label labelSource = new Label(parent, SWT.NORMAL);
        labelSource.setText("source folder");
        // labelSource.setSize(20, 20);
        textSource = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textSource.setText(SelectFileUtil.getSelectFile().getPath()); // 设置text中的内容

        Label labelDest = new Label(parent, SWT.NORMAL);
        labelDest.setText("destination folder");
        // labelDest.setSize(20, 20);
        textDesc = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textDesc.setText(desc); // 设置text中的内容
        
        Label labelMode = new Label(parent, SWT.NORMAL);
        labelMode.setText("file permissions");
        // labelDest.setSize(20, 20);
        textMode = new Text(parent, SWT.BORDER); // 设置一个Text控件
        textMode.setText(filemode); // 设置text中的内容

		withCopy = new Button(parent, SWT.CHECK);
		withCopy.setText("initial with copy operation?");
		
		runProject = new Button(parent, SWT.CHECK);
		runProject.setText("run the project after watched remote?");
		runProject.setSelection(true);
		
		return parent;
	}

	/**
     * Dialog点击按钮时执行的方法
     */
	protected void buttonPressed(int buttonId) {
		// 如果是点了OK按钮，则将值设置回类变量
		if (buttonId == IDialogConstants.OK_ID){
			host = textHost.getText();
			port = textPort.getText();
			user = textUser.getText();
			passwd = textPasswd.getText();
			source = textSource.getText();
			desc = textDesc.getText();
			filemode = textMode.getText();
			initWithCopy = withCopy.getSelection()?"1":"0";
			runAfterWatch= runProject.getSelection()?"1":"0";
			
			PropertiesUtil.save();
			super.buttonPressed(buttonId);
		}else if(buttonId == IDialogConstants.CANCEL_ID){
			getShell().close();
			super.buttonPressed(buttonId);
		}
	}

	/**
     * 重载这个方法可以改变窗口的默认式样 SWT.RESIZE：窗口可以拖动边框改变大小 SWT.MAX：　窗口可以最大化
     */
	public int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
	}
}
