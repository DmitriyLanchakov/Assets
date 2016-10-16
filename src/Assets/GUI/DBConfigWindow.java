package Assets.GUI;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.Document;

import Assets.DB.DBConnector;

public class DBConfigWindow extends JDialog implements ActionListener  {
	private static final long serialVersionUID = -6865512180545109624L;
	
	/**
	 * Class to show GUI to configure DB connection
	 * @param parent
	 */
	public DBConfigWindow(JFrame parent) {
		super(parent, "Config DataBase", Dialog.ModalityType.DOCUMENT_MODAL);
		this.setLayout(new BorderLayout());
		
		JLabel userLabel = new JLabel("User Name");
		JLabel passwordLabel = new JLabel("Password");
		JLabel hostLabel = new JLabel("Host");
		JLabel portLabel = new JLabel("Port");
		
		_userText = new JFormattedTextField();
		_userText.setColumns(15);
		_passwordText = new JPasswordField();
		_passwordText.setColumns(15);
		_hostText = new JFormattedTextField("localhost");
		_hostText.setColumns(15);
		_portText = new JFormattedTextField(3306);
		_portText.setColumns(15);
		
		setSize(300,150);
		
		userLabel.setLabelFor(_userText);
		passwordLabel.setLabelFor(_passwordText);
		hostLabel.setLabelFor(_hostText);
		portLabel.setLabelFor(_portText);
		
		
		JPanel labelPanel = new JPanel(new GridLayout(0,1));
		labelPanel.add(userLabel);
		labelPanel.add(passwordLabel);
		labelPanel.add(hostLabel);
		labelPanel.add(portLabel);
		
		JPanel textPanel = new JPanel(new GridLayout(0,1));
		textPanel.add(_userText);
		textPanel.add(_passwordText);
		textPanel.add(_hostText);
		textPanel.add(_portText);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1,0));
		JButton button = new JButton(_OK);
		button.addActionListener(this);
		buttonPanel.add(button);
		button = new JButton(_CANCEL);
		button.addActionListener(this);
		buttonPanel.add(button);
	
		add(labelPanel, BorderLayout.CENTER);
		add(textPanel, BorderLayout.LINE_END);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		add(buttonPanel, BorderLayout.PAGE_END);
		this.doLayout();
		this.setVisible(true);
	}
	private	JFormattedTextField _userText;
	private JPasswordField _passwordText;
	private JFormattedTextField _hostText;
	private JFormattedTextField _portText;
	private static final String _OK = "OK";
	private static final String _CANCEL = "Cancel";
	/**
	 * Invoked on Action event
	 * @param e an event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals(_OK)) {
			String user = _userText.getText();
			String pass = new String(_passwordText.getPassword());
			String host = _hostText.getText();
			Object portS = _portText.getValue();
			Integer port = Integer.valueOf(portS.toString());
			DBConnector.ConfigConnector(user, pass, host, port);
			System.out.println("user=" + user + " password=" + pass+ " host=" + host + ":" + port + " portS=" + portS) ;
			
		}
		this.dispose();
	}
}
