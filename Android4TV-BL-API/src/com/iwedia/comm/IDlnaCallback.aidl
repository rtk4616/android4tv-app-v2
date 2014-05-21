package com.iwedia.comm;

import com.iwedia.comm.content.Content;

/**
 * The DTV services installation related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface IDlnaCallback {
	/**
        *Notifies that DLNA playback is started.
        *@param uri - uri of file to play
        */
        void dlnaPlayRendererEvent(String uri, String friendlyName, String mime);
        /**
        * DLNA pause event.
        */
        void dlnaPauseRendererEvent();
        /**
        * DLNA resume event.
        */
        void dlnaResumeRendererEvent();
        /**
        * DLNA stop event.
        */
        void dlnaStopRendererEvent();
        /**
        * DLNA position event.
        */
        void dlnaPositionRendererEvent();

        /**
        *DLNA seek to.
        */
        void dlnaSeekToRendererEvent(int seekTo);
}