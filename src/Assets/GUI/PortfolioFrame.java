package Assets.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.swing.*;
import Assets.Asset;
import Assets.Engine.CalculatedAsset;
import Assets.Engine.Engine;

interface ParentNotify {
	void finish(ChoicePercentage cp);
}
class ChoicePercentage extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3418548838113949472L;
	ChoicePercentage(Asset[] assets, ParentNotify onFinish) {
		super();
		super.setLayout(new BorderLayout());
		super.add(new JLabel("Select percentages for the assets"), BorderLayout.NORTH);
		assets_ = assets;
		onFinish_ = onFinish;
		panel_ = new JPanel();
		panel_.setLayout(new GridLayout(assets.length+1, 3));
	
		panel_.add(new JLabel("Asset Name"));
		panel_.add(new JLabel("Min"));
		panel_.add(new JLabel("Max"));
		int max = 100 - assets.length;
		int val = max / assets.length;
		int min = assets.length + 1;
		Maxs_ = new JSlider[assets.length];
		Mins_ = new JSlider[assets.length];
		int n = 0;
		for(Asset a : assets_) {
			JLabel label = new JLabel(a.secid());
			
			panel_.add(label);
			Mins_[n] = setChangeListener(n, 1, 1, 1, max, false);
			panel_.add(Mins_[n]);
			int tmp = max-val;
			if (tmp<min) tmp = min;
			Maxs_[n] = setChangeListener(n, max-1, 1, 2, max, true);
			panel_.add(Maxs_[n]);
			n++;
		}
		JScrollPane pane = new JScrollPane(panel_);
		super.add(pane);
		JButton  button = new JButton("OK");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChoicePercentage.this.onFinish_.finish(ChoicePercentage.this);
			}
			
		});
		super.add(button, BorderLayout.SOUTH);
		super.setSize(panel_.getSize());
		super.setVisible(true);
	}
	public int[] getMins() {
		int[] mins = new int[Mins_.length];
		for (int i = 0; i < Mins_.length; i++)
			mins[i] = Mins_[i].getValue();
		return mins;
	}
	public int[] getMaxs() {
		int[] mins = new int[Maxs_.length];
		for (int i = 0; i < Maxs_.length; i++)
			mins[i] = Maxs_[i].getValue();
		return mins;
	}
	public Asset[] getAssets() {return assets_;}
	private JPanel panel_ = null;
	private JSlider[] Maxs_;
	private JSlider[] Mins_;
	private Asset[] assets_;
	private ParentNotify onFinish_; 
	private boolean underChanging_ = false;
	private JSlider setChangeListener(int n, int value, int extend, int min, int max, boolean isMax) {
		DefaultBoundedRangeModel model = new DefaultBoundedRangeModel( value, extend, min, max);
		model.setValueIsAdjusting(false);
		//class
		
		/*class Listener implements ChangeListener {
			Listener(int n, boolean isMax) {
				num = n;
				isMax_ = isMax;
			}
			@Override
			public void stateChanged(ChangeEvent e) {
				//System.out.println("stateChanged> " + e);
				if (ChoicePercentage.this.underChanging_) {
				} else {
					ChoicePercentage.this.underChanging_ = true;
					ChoicePercentage.this.underChanging_ = false;
				}
				
			}
			private int num;
			private boolean isMax_;
		}
		model.addChangeListener(new Listener(n, isMax));*/
		return new JSlider(model);
	}
}

public class PortfolioFrame extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5901477241771247766L;

	public PortfolioFrame() {
		super();
		addButton_ = new JButton(addAssetCmd_);
		addButton_.addActionListener(this);
		this.add(addButton_);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (addButton_ != null)
			return;
		if (engine_ != null) {
			paintGraph(g);
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if (cmd.equals(addAssetCmd_)) {
			AssetsWindow PortfolioTable_ = new AssetsWindow((JFrame) SwingUtilities.windowForComponent(this),false);
			SortedSet<Asset> assets = PortfolioTable_.getSelected();
			if (assets != null) {
				this.remove(addButton_);
				addButton_ = null;
				this.repaint();
				ChoicePercentage cp = new ChoicePercentage(assets.toArray(new Asset[0]), new ParentNotify() {

					@Override
					public void finish(ChoicePercentage cp) {
						
						try {
							PortfolioFrame.this.engine_ = new Engine(cp.getAssets(), cp.getMins(), cp.getMaxs());
							PortfolioFrame.this.remove(cp);
							PortfolioFrame.this.repaint();
						} catch (NumberFormatException e) {
							engine_ = null;
							e.printStackTrace();
						}
					}
				});
				add(cp);
				this.setSize(cp.getSize());
				this.repaint();
			}
		}
	}
	private void paintGraph(Graphics g) {
		final Color[] colors = {Color.CYAN, Color.RED, Color.GRAY, Color.MAGENTA,
				Color.ORANGE, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY,
				Color.BLUE, Color.LIGHT_GRAY}; 
		class DrawCoord {
			private static final int OFFSET = 10;
			DrawCoord(Graphics g, int height, int width, double maxH, double maxW) {
				offsetX = OFFSET;
				offsetY = height/2;
				scaleH = (height - offsetY) / maxH;
				scaleW = (width  - offsetX) / maxW;
				g_ = g;
				g.setColor(Color.BLACK);
				g.drawLine(offsetX, 0, offsetX, height);
				g.drawLine(0, offsetY, width -1 , offsetY);
				//g.setColor(Color.black);
				g.drawString("Date", width-50, height / 2);
				namePositionY = height - OFFSET;
				namePositionX = OFFSET * 2;
				nameFieldWidth = width - namePositionX;
			}
			public int X(double x) {
				double r = x * scaleW;
				return ((int)r + offsetX);
			}
			public int Y(double y) {
				double r = y * scaleH;
				return (offsetY - (int)r);
			}
			public void PrintOrdinaryAsset(String name, double percent, double ret, double sigma) {
				String param = name + " = " + percent + " % " + "(" + ret + "," + sigma + ")";
				int len = param.length();
				if (namePositionX + len * OFFSET > nameFieldWidth) {
					namePositionX = OFFSET * 2;
					namePositionY -= OFFSET * 2;
				}
				g_.drawString(param, namePositionX, namePositionY);
				namePositionX += len * 10; 
			}
			public void PrintResultAsset(String name, Color c, double ret, double sigma) {
				String param = name + " (" + ret + "," + sigma + ")";
				g_.setColor(c);
				namePositionY -= OFFSET * 2;
				namePositionX = OFFSET * 2;
				g_.drawString(param, namePositionX, namePositionY);
				namePositionY -= OFFSET * 2;
			}
			private double scaleH, scaleW;
			private int offsetX, offsetY, namePositionX, namePositionY, nameFieldWidth;
			private Graphics g_;
		}
		CalculatedAsset portfolio = engine_.getResult();
		int count = portfolio.getCount();
		DrawCoord coord =  new DrawCoord(g, this.getHeight(), this.getWidth(),
				portfolio.getMaxValue(), (double)count);
	
		int x = coord.X(0);
		int y = coord.Y(portfolio.getValueForNumber(0));
		g.setColor(Color.GREEN);
		for(int i=1; i< count; i++) {
			int x1 = coord.X(i);
			int y1 = coord.Y(portfolio.getValueForNumber(i));
			g.drawLine(x, y, x1, y1);
			y = y1;
			x = x1;
		}
		System.out.println("Values[" + portfolio.getDateForNumber(0) + "]=" + portfolio.getValueForNumber(0) + 
					" Values[" + portfolio.getDateForNumber(count) + "]=" + portfolio.getValueForNumber(count));
		Asset[] assets = portfolio.getAssets();
		double[] percents = portfolio.getPercents();
		for(int i=0; i < assets.length; i++) {
			g.setColor(colors[i % colors.length]);
			Asset a = assets[i];
			TreeMap<Date, Double> val = a.values();
			x = coord.X(0);
			double prev = val.get(portfolio.getDateForNumber(0));
			double scale = 1.0;
			y = coord.Y(1 * percents[i]);
			for (int n = 1; n < count; n++) {
				int x1 = coord.X(n);
				double cur = val.get(portfolio.getDateForNumber(n));
				scale *= cur / prev;
				int y1 = coord.Y(scale * percents[i]);
				g.drawLine(x, y, x1, y1);
				x = x1;
				y = y1;
				prev = cur;
			}
			coord.PrintOrdinaryAsset(a.secid(), percents[i] * 100,
					portfolio.getAverages()[i], portfolio.getSigmas()[i]);
			System.out.println("Values[" + portfolio.getDateForNumber(0) + "]=" + val.get(portfolio.getDateForNumber(0)) + 
					" Values[" + portfolio.getDateForNumber(count) + "]=" + val.get(portfolio.getDateForNumber(count)));
		
		}
		coord.PrintResultAsset("Result Portfolio", Color.GREEN,
				portfolio.getResultAverageReturn(), portfolio.getResultSigma());
	}
	private final String addAssetCmd_ = "Add Assets";
	private JButton addButton_ = null;
	private Engine engine_ = null;
}
