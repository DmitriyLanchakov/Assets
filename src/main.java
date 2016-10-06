import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import Assets.Fetcher;
import Assets.FetcherFabric;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	//DBConnector.Get().createAssetsTable("ASSETS");
	if (args.length > 0 && args[0].equals("-u")) { 
		new Assets.Updater();
		return;
	}
	System.out.println("Start exeecution");
	//MainWindow  mw = new MainWindow("My window");
	Thread th = new Thread(new Runnable() {
		public void run() {
			new Assets.GUI.MainWindow("Assets window");
		}
	});
	ArrayList<Fetcher> fetchers = FetcherFabric.Fetchers();
	for(Fetcher f : fetchers)
		try {
			f.sync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//SwingUtilities.invokeLater(th);
	try {
		SwingUtilities.invokeAndWait(th);
		th.join();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	/*
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	System.out.println("Stop exeecution");
	}
}
