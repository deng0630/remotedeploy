package remotedeploy.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class processDialog extends org.eclipse.jface.dialogs.Dialog {

	public static String host = "....";

//	public static String host = "10.20.146.95";
//	public static String port = "22";
//	public static String user = "admin";
//	public static String passwd = "admin";
//	public static String desc = "/home/admin/test";
//	public static String source = "";
	
	
	
	private Label textHost;

	public processDialog(Shell parentShell) {
		super(parentShell);
		PropertiesUtil.init();
	}

	/**
	 * ����������ﹹ��Dialog�еĽ�������
	 */
	public Control createDialogArea(Composite parent) {
		getShell().setText("Զ�̲�����Ϣ����"); // ����Dialog�ı�ͷ

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.marginLeft = 10;
		parent.setLayout(layout);

		Label labelHost = new Label(parent, SWT.NORMAL);
		labelHost.setText("������");
		textHost = new Label(parent, SWT.LEFT);
		textHost.setSize(250, 25);
		textHost.setText(host); // ����text�е�����
		// textHost.setSize(100, 20);


		return parent;
	}

	/**
	 * Dialog�����ťʱִ�еķ���
	 */
	protected void buttonPressed(int buttonId) {
		// ����ǵ���OK��ť����ֵ���û������
		if (buttonId == IDialogConstants.OK_ID){
			host = textHost.getText();
			super.buttonPressed(buttonId);
		}else if(buttonId == IDialogConstants.CANCEL_ID){
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * ��������������Ըı䴰�ڵ�Ĭ��ʽ�� SWT.RESIZE�����ڿ����϶��߿�ı��С SWT.MAX�������ڿ������
	 */
	public int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
	}
	
	public void setTipText(String tip){
		this.textHost.setText(tip);
	}
}
