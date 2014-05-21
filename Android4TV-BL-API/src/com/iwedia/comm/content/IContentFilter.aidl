package com.iwedia.comm.content;

import java.util.List;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;


/**
 * This interface holds declaration of functions implemented in all
 * ContentFilter classes of this package:
 * {@link com.iwedia.service.content.ContentFilterApps}
 * {@link com.iwedia.service.content.ContentFilterData}
 * {@link com.iwedia.service.content.ContentFilterCable}
 * {@link com.iwedia.service.content.ContentFilterSatellite}
 * {@link com.iwedia.service.content.ContentFilterTerrestrial}
 * {@link com.iwedia.service.content.ContentFilterIP}
 * {@link com.iwedia.service.content.ContentFilterRadio}
 * {@link com.iwedia.service.content.ContentFilterWidgets}
 * {@link com.iwedia.service.content.ContentFilterPVRRecorded}
 * {@link com.iwedia.service.content.ContentFilterPVRScheduled}
 * {@link com.iwedia.service.content.ContentFilterMultimedia}
 *
 *
 * @author Marko Zivanovic
 *
 */
interface IContentFilter {

	/**
	 * Called on MW scanFinished() callback. Used to reinitialize proper fields
	 * (e.g. recently watched list, favorite list, last watched channel, etc.)
	 */
	void reinitialize();

	/**
	 * Returns Content item basic information (name, index,...) of currently active ContentFilter.
	 *
	 * @param index
	 *            index of Content to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getContent(int index);

	/**
	 * Returns non Hidden Content item basic information (name, index,...) of currently active ContentFilter.
	 *
	 * @param index
	 *            index of Content to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getContentVisible(int index);


	/**
	 * Returns Content item extended information (now, next, frequency,...) of currently active ContentFilter.
	 *
	 * @param index
	 *            index of Content to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getContentExtendedInfo();

	/**
	 * Returns Content item extended information (now, next, frequency,...) of currently active ContentFilter.
	 *
	 * @param index
	 *            index of Content to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getContentExtendedInfoByIndex(int index);

	/**
	 * Returns list of Content item of currently active ContentFilter.
	 *
	 * @param startIndex
	 *            range start index.
	 * @param endIndex
	 *            range end index.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	List<Content> getContentList(int startIndex, int endIndex);

	/**
	 * Starts given Content, e.g. plays MW TV service, plays MW radio service,
	 * starts Android application, etc.
	 *
	 * @param content
	 *            Content to be started.{@link com.iwedia.comm.content.Content}
	 * @param displayId
	 *            Display ID (0 - main display, 1 - PiP/PaP)
	 * @return Return value is used only in ContentFilterMultimedia
	 *         {@link ContentFilterMultimedia}
	 */
	int goContent(in Content content, int displayId);

	/**
	 * Opens a content by given index, e.g. runs Android application, plays
	 * Radio channel.
	 */
	int goContentByIndex(int index, int displayId);

	/**
	 * Stops given Content.
	 *
	 * @param displayId
	 *            Display ID (0 - main display, 1 - PiP/PaP)
	 * @return Return value is used only in ContentFilterMultimedia
	 *         {@link ContentFilterMultimedia}
	 */
	int stopContent(int displayId);

	/**
	 * Returns a number of elements in "All" list of currently active filter.
	 * "All" list represent list of Content items of GUI ContentList dialog.
	 *
	 * @return size of content list.
	 */
	int getContentListSize();

	/**
	 * Returns a number of visible elements in "All" list of currently active filter.
	 * "All" list represent list of Content items of GUI ContentList dialog.
	 *
	 * @return size of content list.
	 */
	int getContentListSizeVisible();

	/**
	 * Used in {@link ContentFilterMultimedia} to go to absolute path.
	 *
	 * @param path
	 *            absolute path to be shown.
	 */
	void goPath(String path);

	// void createNewFavouriteList(String name);

	/**
	 * Adds given Content to favorites of currently active content filter.
	 *
	 * @param content
	 *            content to be added {@link com.iwedia.comm.content.Content}.
	 * @return true is Content is successfully added, otherwise false - content
	 *         is already in favorite list.
	 */
	boolean addContentToFavorites(in Content content);

	/**
	 * Adds given Content to favorite list
	 *
	 * @param content
	 *            content to be added {@link com.iwedia.comm.content.Content}.
	 * @return true is Content is successfully added, otherwise false - content
	 *         is already in favorite list.
	 */
	boolean addContentToFavoriteList(in Content content);


	/**
	 * Removes given content from favorites of currently active content
	 * filter.
	 *
	 * @param content
	 *            Content to removed {@link com.iwedia.comm.content.Content}.
	 * @return true if Content if successfully removed, otherwise false -
	 *         content does not exist in favorite list.
	 */
	boolean removeContentFromFavorites(in Content content);

	/**
	 * Removes given content from favorite list
	 *
	 * @param content
	 *            Content to removed {@link com.iwedia.comm.content.Content}.
	 * @return true if Content if successfully removed, otherwise false -
	 *         content does not exist in favorite list.
	 */
	boolean removeContentFromFavoriteList(in Content content);

	/**
	 * Removes all contents from favorite list of currently active content
	 * filter.
	 *
	 * @param filterType
	 *            Contents from which filter type to be removed
	 * @return true if all contents are successfully removed, otherwise false
	 */
	boolean removeAllContentsFromFavorites(int filterType);

	/**
	 * Returns number of items in favorite list of currently active filter.
	 *
	 * @return number of items in favorite list.
	 */
	int getFavoritesSize();

	/**
	 * Returns Content item at given index of currently active favorite list.
	 *
	 * @param index
	 *            index of Content item to be returned.
	 * @return Content item {@link com.iwedia.comm.content.Content};
	 */
	Content getFavoriteItem(int index);

	/**
	 * Returns number of items in recently watched list size of currently active
	 * content filter.
	 *
	 * @return number of items in recently watched list.
	 *
	 */
	int getRecenltyWatchedListSize();

	/**
	 * Returns Content item at given index of recently watched list of currently
	 * active content list.
	 *
	 * @param index
	 *            idex of Content to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getRecentlyWatchedItem(int index);

	/**
	 * Used in ContentFilterMultimedia to return number of items in Path list.
	 * Path list represent history list (absolute path) while user is browsing
	 * Multimedia content. {@link ContentFilterMultimedia}
	 *
	 * @return
	 */
	int getPathSize();

	/**
	 * Used in ContentFilterMultimedia. Returns Content item at given index of
	 * absolute path {@link ContentFilterMultimedia}.
	 *
	 * @param index
	 *            index of Content item to be returned.
	 * @return Content {@link com.iwedia.comm.content.Content}.
	 */
	Content getPath(int index);

	/**
	 * Returns index of MW service list associated to ContentFilter, e.g.
	 * ContentFilterDVB_T has index of MW service list 2.
	 *
	 * @return
	 */
	int getServiceListIndex();

	/**
	 * Returns enum {@link com.iwedia.comm.enums.FilterType} associated to
	 * ContentFilter, e.g. ContentFilterDVB_T has associated filter type
	 * FilterType.DVB_T.
	 *
	 * @return
	 */
	int toInt();

	String getContentListName();

    /**
	 * Rename content
	 * @return*/
	int renameContent(in Content content, String newName);

	boolean createPlaylist(String playlistName, String playlistType);

	boolean openPlaylist(String playlistName);

	boolean addAudioItemToPlaylist(String playlistName, String artist, String title, int duration, String URI);

	boolean addVideoItemToPlaylist(String playlistName, String title, int duration, String URI);

	boolean addImageItemToPlaylist(String playlistName, String title, String resolution, String URI);

	void removeItemFromPlaylist(in Content content, String playlistName, String URI);

	boolean sortPlaylist(String playlistName, String criteria);

	void clearPlaylist(String playlistName);

	void deletePlaylist(in Content content, String playlistName);

	List<PlaylistFile> getPlaylists();

	List<MultimediaContent> getPlaylistItems(String playlistName);
}
