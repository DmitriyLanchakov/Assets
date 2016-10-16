package Assets;
import java.io.IOException;
import java.util.SortedSet;

public interface Fetcher {
	/**
	 * Interface to fetch an asset from an Internet site/Read from DB/synchronize and update
	 */
	/** adds asset to fetch
	 * @param asset asset 
	 */
	void add (Asset asset);
	/**
	 * Updates all assets associated  to the fetcher
	 */
	void update();
	/**
	 * Synchronizes assets with DB
	 * @throws Exception
	 */
	void sync() throws Exception;
	/**
	 * Update
	 * @param asset
	 * @return true if the asset has been updated successfully, false otherwise
	 * @throws IOException
	 * @throws Exception
	 */
	boolean update(Asset asset) throws IOException, Exception;
	/**
	 * Deletes an asset
	 * @param name name of the asset to be deleted
	 */
	void DeleteAsset(Asset name);
	/**
	 * Gets all assets associated with the fetcher
	 * @return set of assets
	 */
	SortedSet<Asset> GetAssets();
	/**
	 * Gets the source which the fetcher response for
	 * @return a string contains the source
	 */
	String GetSource();
	/**
	 * Gets the type of fetcher's assets
	 * @return type of fetcher's assets
	 */
	String GetType();
}