package Assets.GUI;

import Assets.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MainWindow extends JFrame implements ActionListener {

	/**
	 * Class to show main window
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * Create window instance
	 * @param title the title of a window
	 */
	public MainWindow(String title) {
		super(title);
		this.init();
	}

	private void init() {
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		Container c_main = getContentPane();
		c_main.setLayout(new BorderLayout(1,0));
		//c_main.setLayout(new FlowLayout());
		JMenuBar m = new JMenuBar();
		JMenu m1 = new JMenu("Portfolio");
		JMenuItem m1_1 = new JMenuItem(portfolioCmd);
		m1_1.addActionListener(this);
		JMenuItem m1_2 = new JMenuItem(updateCmd);
		m1_2.addActionListener(this);
		JMenuItem m1_3 = new JMenuItem(syncCmd);
		m1_3.addActionListener(this);
		m1.add(m1_1);
		m1.add(m1_2);
		m1.add(m1_3);
		m.add(m1);
		JMenu m2 = new JMenu("Config");
		JMenuItem m2_1 = new JMenuItem(configDBCmd);
		m2_1.addActionListener(this);
		m2.add(m2_1);
		m.add(m2);
		JMenu m3 = new JMenu("Tab");
		JMenuItem m3_1 = new JMenuItem(addTabCmd);
		m3_1.addActionListener(this);
		JMenuItem m3_2 = new JMenuItem(removeTabCmd);
		m3_2.addActionListener(this);
		m3.add(m3_1);
		m3.add(m3_2);
		m.add(m3);
		
		TabPane_ = new JTabbedPane();
		TabPane_.addTab("Portfolio", new PortfolioFrame());
		c_main.add(m,/*FlowLayout.LEADING*/ BorderLayout.NORTH);
		TabPane_.setPreferredSize(new Dimension(this.getPreferredSize()));
		c_main.add(TabPane_, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Invoked on user selection event
	 * @param arg0 the event
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (PortfolioTable_ != null) {
			PortfolioTable_.setVisible(false);
			this.remove(PortfolioTable_);
			PortfolioTable_ = null;
		}
		String cmd = arg0.getActionCommand();
		if (cmd.equals(updateCmd)) {
			new Assets.Updater();	
			return;
		}
		else if (cmd.equals(portfolioCmd)) {		
			new AssetsWindow(this, true);
		}
		else if (cmd.equals(syncCmd)) {
			ArrayList<Fetcher> fetchers = FetcherFabric.Fetchers();
			for(Fetcher f : fetchers)
				try {
					f.sync();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		else if (cmd.equals(configDBCmd)) {
			new DBConfigWindow(this);
		}
		else if (cmd.equals(addTabCmd)) {
			TabPane_.addTab("Portfolio", new PortfolioFrame());
		}  else if (cmd.equals(removeTabCmd)) {
			int index = TabPane_.getSelectedIndex();
			if (index >= 0)
				TabPane_.remove(index);
		}
	}
	private static final String updateCmd = "Update";
	private static final String portfolioCmd = "Manage";
	private static final String syncCmd = "Sync with DB";
	private static final String addTabCmd = "Add new Tab";
	private static final String removeTabCmd = "Remove current Tab";
	private static final String configDBCmd = "Config DB"; 
	private JComponent PortfolioTable_ = null;
	private JTabbedPane TabPane_ = null;
}
