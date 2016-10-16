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
		
	//Just update the assets data and exit if the programm start with -u
	if (args.length > 0 && args[0].equals("-u")) { 
		new Assets.Updater();
		return;
	}
	System.out.println("Start exeecution");

	//Create main window
	Thread th = new Thread(new Runnable() {
		public void run() {
			new Assets.GUI.MainWindow("Assets window");
		}
	});
	//Get available assets
	ArrayList<Fetcher> fetchers = FetcherFabric.Fetchers();
	for(Fetcher f : fetchers)
		try {
			f.sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	System.out.println("Stop exeecution");
	}
}
