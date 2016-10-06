package Assets;
import java.io.IOException;
import java.util.SortedSet;

public interface Fetcher {
	void add (Asset asset);
	void update();
	void sync() throws Exception;
	boolean update(Asset asset) throws IOException, Exception;
	void DeleteAsset(Asset name);
	SortedSet<Asset> GetAssets();
	String GetSource();
	String GetType();
}