package org.jbpm.designer.repository;

import java.util.Collection;
import java.util.Map;

/**
 * Repository is responsible for managing its components that are as follows:
 * <ul>
 *     <li>Asset - component that can be of any type and is stored in a custom location</li>
 * </ul>
 */
public interface Repository {

    /**
     * Retrieves all directories stored under <code>startAt</code> location.
     * NOTE: Directory should be always relative to the repository root
     * @param startAt - location where directories should be fetched from
     * @return - list of directories
     */
    Collection<String> listDirectories(String startAt);

    /**
     * Retrieves all directories stored under <code>startAt</code> location including all sub folders.
     * NOTE: Directory should be always relative to the repository root
     *
     * @param startAt - location where directories should be fetched from
     * @param filter - filter that allows to narrow the results
     * @return - list of assets found
     */
    Collection<Asset> listAssetsRecursively(String startAt, Filter filter);

    /**
     * Stores new directory in given location, in case of sub folders existence in the location
     * all sub folders are created as well.
     * @param location - location in the repository to be created
     * @return - returns identifier of the new directory
     */
    String storeDirectory(String location);

    /**
     * Examines repository if given directory exists in the repository
     * NOTE: Directory should be always relative to the repository root
     * @param directory - directory to check
     * @return - true if and only if given directory exists
     */
    boolean directoryExists(String directory);

    /**
     * Deletes directory from repository including its content
     * NOTE: Directory should be always relative to the repository root
     * @param directory - directory to be deleted
     * @param failIfNotEmpty - indicates if delete operation should fail in case given directory is not empty
     * @return
     */
    boolean deleteDirectory(String directory, boolean failIfNotEmpty);

    /**
     * Retrieves all assets stored in the given location.
     * NOTE: This will not load the actual content of the asset but only its meta data
     * @param location - location that assets should be collected from
     * @return - list of available assets
     */
    Collection<Asset> listAssets(String location);

    /**
     * Retrieves all assets stored in the given location.
     * NOTE: This will not load the actual content of the asset but only its meta data
     * @param location - location that assets should be collected from
     * @param filter - allows to defined filter criteria to fetch only assets of interest
     * @return - list of available assets
     */
    Collection<Asset> listAssets(String location, Filter filter);

    /**
     * Loads an asset given by the <code>assetUniqueId</code> including actual content of the asset.
     * @param assetUniqueId - unique identifier of the asset to load
     * @return return loaded asset including content
     * @throws AssetNotFoundException - throws in case of asset given by id does not exist
     */
    Asset loadAsset(String assetUniqueId) throws AssetNotFoundException;

    /**
     * Loads an asset given by the <code>path</code> including actual content of the asset.
     * @param path - complete path of the asset to load (relative to the repository root)
     * @return return loaded asset including content
     * @throws AssetNotFoundException - throws in case of asset given by id does not exist
     */
    Asset loadAssetFromPath(String path) throws AssetNotFoundException;

    /**
     * Stores given asset in the repository. <code>asset</code> need to have all meta data and content available
     * for the operation to successfully complete.
     * @param asset - asset to be stored
     * @return returns asset unique identifier that can be used to locate it
     */
    String storeAsset(Asset asset);

    /**
     * Deletes asset from repository identified by <code>assetUniqueId</code> if exists
     * @param assetUniqueId - unique identifier of the asset
     * @return return true if and only if operation completed successfully otherwise false
     */
    boolean deleteAsset(String assetUniqueId);

    /**
     * Deletes asset from repository given by the <code>path</code> if exists
     * @param path - complete path of the asset to delete
     * @return return true if and only if operation completed successfully otherwise false
     */
    boolean deleteAssetFromPath(String path);

    /**
     * Examines repository if asset given by the <code>assetUniqueId</code> exists
     * @param assetUniqueId - unique identifier of the asset
     * @return true if and only if asset exists otherwise false
     */
    boolean assetExists(String assetUniqueId);

}
