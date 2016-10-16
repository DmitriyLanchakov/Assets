package Assets.DB;

public interface InsertAssetCb {
	/**
	 * CallBack to be called on reading asset's description from DB
	 * @param name  Name of the asset
	 * @param secid Security ID of the asset
	 * @param count Count of available items
	 */
	public void setAsset(String name, String secid, int count);
}