package com.iwedia.comm.content;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.IChannelsCallback;
import com.iwedia.dtv.service.ServiceDescriptor;


	/** This interface contains declaration of functions implemented in all ContentFilters and ContentListControl as controller of all ContentFilters.
	* 	{@link com.iwedia.service.ContentFilterAll},
	* 	{@link com.iwedia.service.ContentFilterApps},
	* 	{@link com.iwedia.service.ContentFilterData},
	* 	{@link com.iwedia.service.ContentFilterDVB_C},
	* 	{@link com.iwedia.service.ContentFilterDVB_S},
	* 	{@link com.iwedia.service.ContentFilterDVB_T},
	* 	{@link com.iwedia.service.ContentFilterInputs},
	* 	{@link com.iwedia.service.ContentFilterMultimedia},
	* 	{@link com.iwedia.service.ContentFilterPVRRecorded},
	* 	{@link com.iwedia.service.ContentFilterScheduled},
	* 	{@link com.iwedia.service.ContentFilterRadio},
	* 	{@link com.iwedia.service.ContentFilterWidgets},
	* 	{@link com.iwedia.service.ContentListControl}.
	*
	*	@author Marko Zivanovic
	*/
	interface IContentListControl{

		/**
		*Returns number of items in all list of currently active filter.
		*/
		int getContentListSize();

		/**
		*Returns number of visible items in all list of currently active filter.
		*/
		int getContentListSizeVisible();

		/**
		*	Used in ContentListControl to manage currently active filter
		* 	@param typeOfFIlter {@link com.iwedia.comm.enums.FilterType}.
		*/
		void setActiveFilter(int typeOfFilter);

		/**
		*Returns basic Content item information (name, index,...) in all list of currently active filter by given index.
		*/
		Content getContent(int index);

		/**
		*Returns basic visible Content item information (name, index,...) in all list of currently active filter by given index.
		*/
		Content getContentVisible(int index);

		/**
		*Returns Content item in all list of currently active filter by given index.
		*/
		Content getContentExtendedInfo();

				/**
		*Returns Content item in all list of currently active filter by given index.
		*/
		Content getContentExtendedInfoByIndex(int index);


		/**
		*Returns list of Content items in all list of currently active filter by given range.
		*/
		List<Content> getContentList(int startIndex, int endIndex);

		/**
		*	Adds given Content item to favorites of currently active filter.
		*		@return true if Content item was successfully added, else - item already in favorite list.
		*/
		boolean addContentToFavorites(in Content content);

		/**
		*	Removes given Content item from favorites of currently active filter.
		*		@return true if Content item was successfully removed, else - item does not exit in list.
		*/
		boolean removeContentFromFavorites(in Content content);

		/**
		*	Adds given Content item to favorite list.
		*		@return true if Content item was successfully added, else - item already in favorite list.
		*/
		boolean addContentToFavoriteList(int favListIndex, in Content content);

		/**
		*	Removes given Content item from favorite list.
		*		@return true if Content item was successfully removed, else - item does not exit in list.
		*/
		boolean removeContentFromFavoritesList(int favListIndex, in Content content);

		/**
		*	Removes all Contents from favorite list of currently active filter.
		*		@return true if Contents were successfully removed, otherwise false.
		*/
		boolean removeAllContentsFromFavorites(int filterType);

		/**
		*	Removes given Content item from recently list.
		*		@return true if Content item was successfully removed, else - item does not exit in list.
		*/
		boolean removeContentFromRecentlyList(in Content content);


		/**
		*Returns number of items in favorite list of currently active filter.
		*/
		int getFavoritesSize();

		/**
		*Returns Content item in favorite list of currently active filter by given index.
		*/
		Content getFavoriteItem(int index);

		/**
		*Returns number of items in recently watched list of currently active filter.
		*/
		int getRecenltyWatchedListSize();

		/**
		*Returns Content item in recently watched list of currently active filter.
		*/
		Content getRecentlyWatchedItem(int index);

		/**
		* Opens given content, e.g. runs Android application, plays Radio channel.
		*/
		int goContent(in Content content, int displayId);

		/**
		* Opens a content by given index, e.g. runs Android application, plays Radio channel.
		*/
		int goContentByIndex(int index, int displayId);

		/**
		* Opens previously played content.
		*/
		int togglePreviousContent(int displayId);

		/**
		* Stops current content playback.
		*/
		int stopContent(in Content content, int displayId);

		/**
		* Used in ContentFilterMultimedia.
		* Sets current browsing location by given path.
		*/
		void goPath(String path);

		/**
		* Starts last watched service.
		*/
		boolean startVideoPlayback();

		/**
		* Stops currently active video playback.
		*/
		void stopVideoPlayback();

		/**
		* Used in ContentFilterMultimedia.
		* Returns number of items in file path for current browsing location.
		*/
		int getPathSize();

		/**
		* Used in ContentFilterMultimedia.
		* Returns the item in file path for current browsing location by given index.
		*/
		Content getPath(int index);

		/**
		*	Used in ContentListControl to get currently active filter index
		* 	@return typeOfFIlter {@link com.iwedia.comm.enums.FilterType}.
		*/
		int getActiveFilterIndex();

		/**
		*	Used in ContentListControl to get currently active filter
		* 	@return typeOfFIlter {@link com.iwedia.comm.enums.FilterType}.
		*/
		IContentFilter getActiveContentFilter();

		/**
		 * Returns currently active MW service as Content item.
		 *
	 	* @return currently active content.
	 	*/
		Content getActiveContent(int displayId);

		/**
		* Sets currently active Content.
		*
		* @param activeContent
		*            new Content to be set.
		*/
		void setActiveContent(in Content newContent, int displayId);

		/**
	 	* Returns number of content items in ContentFilter by filter type.
	 	*/
		int getContentFilterListSize(int filterType);

		/**
	 	* Return index in ContentFilterAll list of given content.
		 */
		int getContentIndexInAllList(in Content content);

		/**
		* Registers IChannelsCallback for channelZapping(), nowNextChanged() and EpgEventsChanged() callback functions.
		*/
		void registerCallback(IChannelsCallback channelsCallback);

		/**
		*	Lock or unlock content. Returns true if content is locked, otherwise false.
		*
		*/
		boolean setContentLockStatus(in Content content, boolean status);

		/**
		* Returns content locked status. True if content is locked, otherwise false.
		*/
		boolean getContentLockedStatus(in Content content);

		/**
		* Sets new name to a content
		*/
		boolean renameContent(in Content content, String newName);

		/**
		* Returns service of MW MasterList by given index.
		*/
		ServiceDescriptor getServiceByIndexInMasterList(int serviceIndex, boolean getNowNext);

		Content getPreviousContent();

		/**
		* Returns content by index in MW master list.
		*/
		Content getContentByIndexInMasterList(int index);

		/**
		* Return IContentFilter instance of passed filter type.
		*/
		IContentFilter getContentFilter(int filterType);

		/**
		* Returns number of content filters (number of service lists).
		*/
		int getNumberOfServiceLists();

		/**
		* Returns name of content list.
		*/
		String getContentListName();

		/**
   		* Reinitialize content list.
   		*/
   		void reinitialize();

   		/**
   		* Refresh service lists in content list
   		*/
   		void refreshServiceLists();
	}