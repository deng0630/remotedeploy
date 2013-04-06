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

public class ConfigDialog extends Dialog {

	public static String host = "";
	public static String port = "";
	public static String user = "";
	public static String passwd = "";
	public static String desc = "";
	public static String source = "";
	
	
	private Text textHost;
	private Text textPort;
	private Text textUser;
	private Text textPasswd;
	private Text textSource;
	private Text textDesc;

	public ConfigDialog(Shell parentShell) {
		super(parentShell);
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
		textHost = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textHost.setText(host); // ����text�е�����
		// textHost.setSize(100, 20);

		Label labelPort = new Label(parent, SWT.NORMAL);
		labelPort.setText("�˿�");
		// labelPort.setSize(20, 20);
		textPort = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textPort.setText(port); // ����text�е�����
		// textPort.setSize(50, 20);

		Label labelUser = new Label(parent, SWT.NORMAL);
		labelUser.setText("�û���");
		// labelUser.setSize(20, 20);
		textUser = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textUser.setText(user); // ����text�е�����

		Label labelPasswd = new Label(parent, SWT.NORMAL);
		labelPasswd.setText("����");
		// labelPasswd.setSize(20, 20);
		textPasswd = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textPasswd.setText(passwd); // ����text�е�����

		Label labelSource = new Label(parent, SWT.NORMAL);
		labelSource.setText("ԴĿ¼");
		// labelSource.setSize(20, 20);
		textSource = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textSource.setText(SelectFileUtil.getSelectFile().getPath()); // ����text�е�����

		Label labelDest = new Label(parent, SWT.NORMAL);
		labelDest.setText("Ŀ��Ŀ¼");
		// labelDest.setSize(20, 20);
		textDesc = new Text(parent, SWT.BORDER); // ����һ��Text�ؼ�
		textDesc.setText(desc); // ����text�е�����

		return parent;
	}

	/**
	 * Dialog�����ťʱִ�еķ���
	 */
	protected void buttonPressed(int buttonId) {
		// ����ǵ���OK��ť����ֵ���û������
		if (buttonId == IDialogConstants.OK_ID){
			host = textHost.getText();
			port = textPort.getText();
			user = textUser.getText();
			passwd = textPasswd.getText();
			source = textSource.getText();
			desc = textDesc.getText();
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * ��������������Ըı䴰�ڵ�Ĭ��ʽ�� SWT.RESIZE�����ڿ����϶��߿�ı��С SWT.MAX�������ڿ������
	 */
	public int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
	}
}