package Assets.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import Assets.*;

class ATable extends JTable implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2971805234825945852L;
	public ATable(DefaultTableModel model, AssetsWindow parent) {
		super(model);
		super.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		super.setColumnSelectionAllowed(false);
		super.setRowSelectionAllowed(true);
		super.setFillsViewportHeight(true);
		super.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		super.getSelectionModel().addListSelectionListener(this);
		parent_ = parent;
		model_ = model;
		if (assets_.size() == 0) {
			ArrayList<Fetcher> fetchers = FetcherFabric.Fetchers();
			for(Fetcher f : fetchers) {
				java.util.SortedSet<Asset> la = f.GetAssets();
				if(la != null)  { 
					for(Asset a : la)
						if (!a.isDeleted())
							assets_.add(a);
				}
				assetsSources_.add(f.GetSource());
				assetsTypes_.add(f.GetType());
			}
		}
		setComboBoxForSourceAndType();
		for (Asset a : assets_)
			model.addRow(a.getAll());
	}
	private void setComboBoxForSourceAndType() {
		class setForCombo extends JComboBox<String> {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			setForCombo(JTable table, SortedSet<String> values, int cNum) {
				super();
				super.setEditable(false);
				TableColumn column = table.getColumnModel().getColumn(cNum);
				for (String v:values) 
					this.addItem(v);
				column.setCellEditor(new DefaultCellEditor(this));
				super.setName(values.first());
				super.setSelectedItem(values.first());
			}
			/*@Override
			public void setSelectedIndex(int index) {
				super.setSelectedIndex(PreviouslySelected);
			}*/
			@Override
			public int getSelectedIndex() {
				PreviouslySelected = super.getSelectedIndex();
				return PreviouslySelected;
			}
			private int PreviouslySelected = 0;
		}	
		new setForCombo(this, assetsSources_, 2);
		new setForCombo(this, assetsTypes_, 3);
	}
	@Override
	public boolean isCellEditable(int row, int column) {
		if(editable_ && row==getRowCount()-1)
			return true;
		/*if(super.getColumnName(column).equals("Count"))
			return true;*/
		return false;
	}
	public void setEditable(boolean b) {
		editable_ = b;
	}
	public void valueChanged(ListSelectionEvent event) {
		//System.out.println("value Changed " + event);
		super.valueChanged(event);
		if (editable_)
			return;
		marked_ = super.getSelectedRows();
		if (marked_.length > 0) {
			parent_.enableDeleteButton(true);
		}
		else {
			parent_.enableDeleteButton(false);
			marked_ = null;
		}
	}
	public SortedSet<Asset> getSelected() {
		if (marked_ == null)
			return null;
		SortedSet<Asset> as = new TreeSet<>();
		for (Integer i : marked_)
			as.add(assets_.get(i));
		return as;
	}
	public void addAsset(Asset a) {assets_.add(a);}
	public void deleteSelectedAssets() {
		if (marked_ == null)
			return;
		for (int i = marked_.length-1; i >= 0; i--) {
			assets_.get(marked_[i]).delete();
			assets_.remove(marked_[i]);
			model_.removeRow(marked_[i]);
		}
	}
	public void clearSelected() { marked_ = null;}
	
	private boolean editable_ = false;
	private int[] marked_ = null;
	private /*static*/ ArrayList<Asset> assets_ = new ArrayList<>();
	private /*static*/ SortedSet<String> assetsSources_ = new TreeSet<>();
	private /*static*/ SortedSet<String> assetsTypes_ = new TreeSet<>();
	private AssetsWindow parent_;
	private DefaultTableModel model_;
}

public class AssetsWindow extends JDialog implements WindowListener{

	private static final long serialVersionUID = 1L;
	public AssetsWindow(JFrame parent, boolean fromMain) {
		super(parent, "Manage Portfolio", Dialog.ModalityType.DOCUMENT_MODAL);
	
		table_ = new ATable(new DefaultTableModel(columnNames, 0), this);
		
		scrollPane_ = new JScrollPane(table_);
		this.setSize(new Dimension(400,300));
		this.setLocation(200, 200);

		JPanel panel = new JPanel();
		initButtons(panel, fromMain);
		this.add(scrollPane_,0);
		
		this.add(panel,BorderLayout.SOUTH);
		this.pack();
		this.setResizable(true);
		this.addWindowListener(this);
		this.setVisible(true);
	}
	private void initButtons(JPanel panel, boolean fromMain) {
		if (fromMain) {
			
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String cmd = arg0.getActionCommand();
					if(cmd.equals(addCmd))
						AssetsWindow.this.addAsset();
					else if(cmd.equals("Finish"))
						AssetsWindow.this.finishAsset();
					else if(cmd.equals(delCmd)) 
						AssetsWindow.this.table_.deleteSelectedAssets();
					else if(cmd.equals("Exit"))
						AssetsWindow.this.onButtonPressed(false);
				}
			};
			cmdBtn_ = new JButton(addCmd);
			cmdBtn_.addActionListener(listener);
			delBtn_ = new JButton(delCmd);
			delBtn_.addActionListener(listener);
			delBtn_.setEnabled(false);
			JButton exit = new JButton("Exit");
			exit.addActionListener(listener);
			panel.add(cmdBtn_);
			panel.add(delBtn_);
			panel.add(exit);
		}  else {
			ActionListener OkCancellListener = new ActionListener()  {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String cmd = arg0.getActionCommand();
					if (cmd.equalsIgnoreCase("OK"))
						AssetsWindow.this.onButtonPressed(true);
					else if (cmd.equalsIgnoreCase("Cancel"))
						AssetsWindow.this.onButtonPressed(false);
				}
			};
			JButton OkBtn = new JButton("Ok");
			OkBtn.addActionListener(OkCancellListener);
			JButton CancelBtn =new JButton("Cancel"); 
			CancelBtn.addActionListener(OkCancellListener);
			panel.add(OkBtn);
			panel.add(CancelBtn);
		}
	}
	
	protected void onButtonPressed(boolean OkCancel) {
		// TODO Auto-generated method stub
		if (!OkCancel) //Cancel pressed
			table_.clearSelected();
		this.setVisible(false);
		this.dispose();
	}

	public void addAsset() {
		
		JViewport vp = scrollPane_.getViewport();
		vp.scrollRectToVisible(getBounds());
				
		DefaultTableModel model = (DefaultTableModel) table_.getModel();
		table_.setEditable(true);
		model.setRowCount(model.getRowCount()+1);
		table_.setEditingRow(model.getRowCount()-1);
		table_.setEditingColumn(0);
		vp.scrollRectToVisible(getBounds());
		cmdBtn_.setText(finishCmd);
	}
	
	public void finishAsset() {
		table_.setEditable(false);
		cmdBtn_.setText(addCmd);
		HashMap<String,String> values = new HashMap<>();
		try {
			if (table_.getCellEditor()!= null)
				table_.getCellEditor().stopCellEditing();
			for (int n = 0; n < columnNames.length;  n++) {
				String colmn = table_.getColumnName(n);
				Object obj = table_.getValueAt(table_.getRowCount()-1, n);
				/*if (obj == null) {
					if (table_.getCellEditor() != null)
						obj = ((JTextField)table_.getCellEditor()).getText();
				}*/
				values.put(colmn, obj.toString());
			}
			Fetcher f = FetcherFabric.get(values.get(Source),values.get(Type));
			Asset a = new Asset(values.get(Name),values.get(SCID));
			a.setCount(Integer.parseInt(values.get(Count)));
			f.add(a);
			table_.addAsset(a);
		}
		catch (Exception e) {
			System.out.println("Got exception> " + e + " wrong data will be removed" );
			e.printStackTrace();
			DefaultTableModel model = (DefaultTableModel) table_.getModel(); 
			model.removeRow(table_.getRowCount()-1);
			return;
		}
		for (String n : values.keySet()) {
			System.out.println("Colmn n " +  n + " has value " + values.get(n));
		}
	}
	public void enableDeleteButton(boolean  enable) {
		if (delBtn_ != null)
			delBtn_.setEnabled(enable);
	}
	static private final String Name = "Name";
	static private final String SCID = "SCID"; 
	static private final String Source = "Source";
	static private final String Type = "Type";
	static private final String Count = "Count";
	static private final String[] columnNames = {
	     Name,
	     SCID,
	     Source,
	     Type,
	     Count
	}; 
	private ATable table_ = null;
	private JButton cmdBtn_, delBtn_;
	private final String addCmd = "Add Asset";
	private final String delCmd = "Delete Asset(s)";
	private final String finishCmd = "Finish";
	private JScrollPane scrollPane_;
	/*@Override
	public void windowStateChanged(WindowEvent arg0) {
		System.out.println("WindowStateChanged> " + arg0);
	}*/

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.onButtonPressed(false);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public SortedSet<Asset> getSelected() {
		return table_.getSelected();
	}
}
