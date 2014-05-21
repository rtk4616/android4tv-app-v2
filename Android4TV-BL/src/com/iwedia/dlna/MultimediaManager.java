package com.iwedia.dlna;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.dtv.media_explorer.MediaExplorerControlNative;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dlna.dmc.service.DlnaDmcService;
import com.iwedia.dlna.dmr.service.DlnaDmrNativeService;
import com.iwedia.dlna.dms.service.DlnaNativeService;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.content.ContentFilter;
import com.iwedia.service.storage.ControllerType;

/**
 * This class represent manager for multimedia
 * 
 * @author Djordje Kovacevic
 */
public class MultimediaManager extends ContentFilter {
    private final String LOG_TAG = "MultimediaManager";
    final int MAX_RECENTLY_WATCHED_SIZE = 20;
    /** instance of multimedia */
    private static MultimediaManager instance = null;
    /** */
    private static final String LOCAL_ROOT = "local:///";
    private static final String DLNA_ROOT = "dlna:///";
    // private static final String PATH_TO_USB = "local:///mnt/media/";
    private static final String PATH_TO_USB = "local:///storage/usb/";
    private static final String PVR_ROOT = "PVR";
    private static final String SLASH = "/";
    private static final String DOT = ".";
    private static final String TWO_DOT = "..";
    private static final String EMPTY_STRING = "";
    private static final String USB = "USB";
    private static final String LOCAL = "LOCAL";
    private static final String DLNA = "DLNA";
    private static final String PVR = "PVR";
    private static final String PLAYLIST = "Playlists";
    private static final String PLAYLIST_ROOT = "Playlists://";
    private static final String DEFAULT = "DEFAULT";
    private static final String LOCAL_STORAGE = "Local Storage";
    private boolean isPVRRoot = false;
    private int isPlaylistRoot = 0;
    private static int folderLevel = 0;
    private static String oldPath = SLASH;
    private static int SAME_FOLDER = 66666;
    /** Constant for local file handling */
    public static final int FILE_LOCAL = 0;
    /** Constant for DLNA file handling */
    public static final int FILE_DLNA = 1;
    /** Returns dir for directory and file for file resource. */
    private static final int FILE_TYPE = 2;
    /** Returns extension of passed resource URI. */
    private static final int FILE_EXT = 3;
    /** Returns URL to passed resource URI. */
    private static final int FILE_URL = 4;
    DlnaContainer con = null;
    public static DlnaDMP mPlayerControl;
    public static DlnaNativeService nativeService = null;
    public static DlnaDmcService nativeDmcService = null;
    public static DlnaDmrNativeService nativeDmrService = null;
    private boolean isDlna = false;
    private boolean player = true;
    /** Global path for multimedia */
    private String globalPathForMultimedia = SLASH;
    /** Multimedia content */
    private MultimediaContent multimedia = new MultimediaContent(EMPTY_STRING,
            EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, -1,
            EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, 0);
    private MediaExplorerControlNative mediaControlNative = new MediaExplorerControlNative();
    /** List of multimedia folders or items in one multimedia screen */
    private ArrayList<MultimediaContent> multimediaList = new ArrayList<MultimediaContent>();
    /** List of recently accessed items */
    private ArrayList<MultimediaContent> recentlyAccessed;
    /** List of recently accessed folders */
    private ArrayList<MultimediaContent> recentlyAccessedFolders;
    /** List of favorite */
    private ArrayList<Content> favouriteList;
    public static String rootID;
    public static boolean isFavorite = false;
    private PlaylistManager playlistManager = new PlaylistManager();

    /**
     * Call this method in ContentFilterMultimedia before use DLNA library
     */
    public void init() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "********************* INIT DLNA LIBRARY");
        }
        nativeDmcService = new DlnaDmcService();
        if (nativeDmcService.getDlnaService() != null) {
            try {
                Log.i(LOG_TAG, "New Service started ---------> Nikola ");
                nativeDmcService.getDlnaService().init("eth0");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                if (player) {
                    System.out.println("Start only player controller");
                    mPlayerControl = new DlnaDMP(nativeDmcService);
                } else {
                    System.out.println("Start only player controller");
                    mPlayerControl = new DlnaDMC(nativeDmcService);
                }
                mPlayerControl.start();
            } catch (DlnaException e) {
                e.printStackTrace();
            }
        }
        nativeService = new DlnaNativeService();
        if (nativeService.getDlnaService() != null) {
            try {
                nativeService.getDlnaService().init("eth0");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        nativeDmrService = new DlnaDmrNativeService();
        if (nativeDmrService.getDlnaService() != null) {
            try {
                nativeDmrService.getDlnaService().init("eth0");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static DlnaDmcService getNativeDmcSerivce() {
        return nativeDmcService;
    }

    public static DlnaNativeService getNativeService() {
        return nativeService;
    }

    public static DlnaDmrNativeService getNativeDmrService() {
        return nativeDmrService;
    }

    /**
     * Multimedia constructor who initialize recently accessed, recently
     * accessed folders and favorite list
     */
    public MultimediaManager() {
        recentlyAccessed = new ArrayList<MultimediaContent>();
        recentlyAccessedFolders = new ArrayList<MultimediaContent>();
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.FAVOURITE_LIST);
        this.favouriteList = IWEDIAService
                .getInstance()
                .getStorageManager()
                .getElementsInListByFilter(
                        IWEDIAService.getInstance()
                                .getMultimediaListTableName(),
                        FilterType.MULTIMEDIA);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "number of elements in favorite list:"
                    + favouriteList.size());
            for (int i = 0; i < this.favouriteList.size(); i++) {
                Log.e(LOG_TAG, "init:" + favouriteList.get(i).toString());
            }
        }
        isPlaylistRoot = 0;
    }

    /**
     * Add folders or items in multimedia list
     * 
     * @param path
     *        - Content path
     * @param containerId
     *        - Content id
     * @return 0 - if folder not empty
     *         <p>
     *         -1 - if folder empty
     */
    private int setCurDirService(String path, String containerId,
            MultimediaContent content) {
        // //////////////////////
        // items on first screen
        // //////////////////////
        if (path.equals(SLASH)) {
            listOnFirstScreen(SLASH);
        }
        // ////////////////////////////
        // items on second screen
        // ///////////////////////////
        else {
            if (path.startsWith(PLAYLIST)) {
                // multimediaList.clear();
                String[] split = path.split("[/]+");
                if (split.length == 1) {
                    isPlaylistRoot = 0;
                } else {
                    if (split.length == 2) {
                        isPlaylistRoot = 1;
                    }
                }
                // add playlists to list
                if (isPlaylistRoot == 0) {
                    int playlistSize = playlistManager.getNumberOfPlaylists();
                    if (playlistSize == 0) {
                        return -1;
                    }
                    multimediaList.clear();
                    globalPathForMultimedia = path;
                    ArrayList<PlaylistFile> playlists;
                    playlists = (ArrayList<PlaylistFile>) playlistManager
                            .getPlaylists();
                    for (int i = 0; i < playlistSize; i++) {
                        int index = playlists.get(i).getName().lastIndexOf(".");
                        String playlistName = playlists.get(i).getName()
                                .substring(0, index);
                        MultimediaContent mContent = new MultimediaContent(1,
                                playlistName, "dir", globalPathForMultimedia
                                        + SLASH + playlistName, "", "", "", "",
                                0, "", DEFAULT, playlistName, playlists.get(i)
                                        .getType());
                        multimediaList.add(mContent);
                    }
                    isPlaylistRoot++;
                }
                // add playlist item to list
                else {
                    try {
                        if (content != null) {
                            ArrayList<MultimediaContent> playlistItems = (ArrayList<MultimediaContent>) playlistManager
                                    .getPlaylistItems(content.getPlaylistName());
                            if (playlistItems != null) {
                                if (playlistItems.size() == 0) {
                                    return -1;
                                }
                                multimediaList.clear();
                                globalPathForMultimedia = path;
                                for (int i = 0; i < playlistItems.size(); i++) {
                                    MultimediaContent mContent = new MultimediaContent(
                                            playlistItems.get(i)
                                                    .getPlaylistID(),
                                            content.getPlaylistName(),
                                            "file",
                                            globalPathForMultimedia + SLASH
                                                    + content.getPlaylistName(),
                                            playlistItems.get(i).getTitle(),
                                            playlistItems.get(i).getArtist(),
                                            playlistItems.get(i).getFileURL(),
                                            "", 0, playlistItems.get(i)
                                                    .getExtension(), DEFAULT,
                                            playlistItems.get(i).getTitle(), "");
                                    multimediaList.add(mContent);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else
            // ///////////////////////////
            // PVR items
            // ///////////////////////////
            if (path.startsWith(PVR_ROOT)) {
                if (IWEDIAService.DEBUG) {
                    Log.e("pvr", "pvr");
                }
                isPVRRoot = true;
                multimediaList.clear();
                try {
                    int size = IWEDIAService.getInstance().getDtvManagerProxy()
                            .getPvrControl().updateMediaList();
                    if (IWEDIAService.DEBUG) {
                        Log.e("pvr size", "size" + size);
                    }
                    MediaInfo mediaInfo = new MediaInfo();
                    Log.d("PVR", "initial mediaInfo: " + mediaInfo);
                    for (int i = 0; i < size; i++) {
                        mediaInfo = IWEDIAService.getInstance()
                                .getDtvManagerProxy().getPvrControl()
                                .getMediaInfo(i);
                        MultimediaContent mContent = new MultimediaContent(
                                mediaInfo.getTitle(),
                                mediaInfo.getDescription(),
                                String.valueOf(mediaInfo.getDuration()),
                                DEFAULT, mediaInfo.getStartTime(), i, "file",
                                "pvrfile");
                        mContent.setIncomplete(mediaInfo.isIncomplete());
                        multimediaList.add(mContent);
                        if (IWEDIAService.DEBUG)
                            Log.e("PVR", "pvr"
                                    + multimediaList.get(i).getFileURL());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // ////////////////////////////
                // LOCAL, USB items
                // Filelib library
                // ////////////////////////////
                if (path.startsWith(LOCAL_ROOT)
                        || (isDlna == false && path.equals(TWO_DOT))) {
                    // ////////////////////////////////////////////////////////
                    // if some folder in multimedia empty, stay on that screen
                    // /////////////////////////////////////////////////////////
                    if (setCurDir(path) == null) {
                        setCurDir(TWO_DOT);
                        return -1;
                    }
                    if (getFirst("") == null) {
                        setCurDir(TWO_DOT);
                        return -1;
                    }
                    // ///////////////////////////////////
                    // if some folder in multimedia have only dot and two dot,
                    // stay on that screen
                    // ///////////////////////////////////
                    if (folderWithDotAndTwoDot() == true) {
                        return -1;
                    }
                    path = getCurDir();
                    multimediaList.clear();
                    // /////////////////////////////////////
                    // return list of all LOCAL, USB items
                    // ////////////////////////////////////
                    listDirectoryForLocal(DEFAULT, path);
                }
                // ///////////////////////////
                // DLNA items
                // DLNA library
                // ///////////////////////////
                else {
                    isDlna = true;
                    multimediaList.clear();
                    // ///////////////////////////////
                    // return list of all DLNA items
                    // ///////////////////////////////
                    boolean emptyFolder;
                    try {
                        emptyFolder = listDirectory(DEFAULT, path, containerId);
                    } catch (Exception exception) {
                        emptyFolder = false;
                    }
                    if (emptyFolder == false) {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Put folders or items of LOCAL in multimediaList
     * 
     * @param multimediaType
     *        - set to DEFAULT and use for image icon
     * @param globalPath
     *        - Content path
     * @return true - If folders in LOCAL contains some item or folder
     *         <p>
     *         false - If folders in LOCAL empty
     */
    private boolean listDirectoryForLocal(String multimediaType,
            String globalPath) {
        String nextInDir;
        String firstInDir;
        globalPathForMultimedia = globalPath;
        String slash = "";
        if (globalPath.endsWith("///")) {
            slash = "";
        } else if (!globalPath.endsWith(SLASH)) {
            slash = SLASH;
        }
        // ////////////////////////
        // Passing through all items
        // and add it in multimediaList
        // ////////////////////////
        Log.e("DLNA GLOBAL PATH", globalPath + "");
        firstInDir = getFirst("");
        if (firstInDir != null) {
            if (!firstInDir.equals(DOT) && !firstInDir.equals(TWO_DOT)) {
                multimedia = new MultimediaContent(firstInDir, getFileProperty(
                        firstInDir, FILE_URL), getFileProperty(firstInDir,
                        FILE_EXT).toLowerCase(), getFileProperty(firstInDir,
                        FILE_TYPE), multimediaType, -1, globalPath + slash
                        + firstInDir, "", "", EMPTY_STRING, 0);
                if (IWEDIAService.DEBUG) {
                    Log.e("first", globalPath + slash + firstInDir);
                }
                multimediaList.add(multimedia);
            }
            do {
                nextInDir = getNext(getCurDir());
                if (nextInDir != null) {
                    if (!nextInDir.equals(DOT) && !nextInDir.equals(TWO_DOT)) {
                        multimedia = new MultimediaContent(nextInDir,
                                getFileProperty(nextInDir, FILE_URL),
                                getFileProperty(nextInDir, FILE_EXT)
                                        .toLowerCase(), getFileProperty(
                                        nextInDir, FILE_TYPE), multimediaType,
                                -1, globalPath + slash + nextInDir, "", "",
                                EMPTY_STRING, 0);
                        if (IWEDIAService.DEBUG) {
                            Log.e("next", globalPath + slash + nextInDir);
                        }
                        multimediaList.add(multimedia);
                    }
                }
            } while (nextInDir != null);
        }
        return false;
    }

    /**
     * @return true - if folder have only dot and two dot false - if folder
     *         haven't dot and two dot
     */
    private boolean folderWithDotAndTwoDot() {
        String nextInDir;
        String firstInDir;
        firstInDir = getFirst("");
        if (firstInDir != null) {
            nextInDir = getNext(getCurDir());
            if (firstInDir.equals(DOT) && nextInDir.equals(TWO_DOT)) {
                nextInDir = getNext(getCurDir());
                if (nextInDir == null) {
                    setCurDir(TWO_DOT);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add all dlna servers, local and usb folder in multimedia list
     * <p>
     * and show on first screen in multimedia
     * 
     * @param newString
     *        - root("/")
     */
    private void listOnFirstScreen(String newString) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "listOnFirstScreen:" + "setCurDir before");
        }
        setCurDir(SLASH);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "listOnFirstScreen:" + "setCurDir after");
        }
        multimediaList.clear();
        multimedia = new MultimediaContent(LOCAL_STORAGE, getFileProperty(
                newString, FILE_URL), getFileProperty(newString, FILE_EXT),
                getFileProperty(newString, FILE_TYPE), LOCAL, -1, LOCAL_ROOT,
                "", "", EMPTY_STRING, 0);
        multimediaList.add(multimedia);
        multimedia = new MultimediaContent(USB, getFileProperty(newString,
                FILE_URL), getFileProperty(newString, FILE_EXT),
                getFileProperty(newString, FILE_TYPE), USB, -1, PATH_TO_USB,
                "", "", EMPTY_STRING, 0);
        multimediaList.add(multimedia);
        multimedia = new MultimediaContent(PVR, "", "", "root", PVR, -1,
                PVR_ROOT, "", "", EMPTY_STRING, 0);
        multimediaList.add(multimedia);
        multimedia = new MultimediaContent(0, PLAYLIST, "root", PLAYLIST_ROOT,
                "", "", "", "", 0, "", PLAYLIST, PLAYLIST, "");
        multimediaList.add(multimedia);
        // ///////////////////////////
        // Number of DLNA servers
        // //////////////////////////
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "before get server count");
        }
        int numberOfDlna = 0;
        String localIp = getLocalIpAddress();
        if (localIp != null) {
            try {
                numberOfDlna = mPlayerControl.getServerCount();
            } catch (DlnaException e1) {
                e1.printStackTrace();
            }
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "after get server count: nb:" + numberOfDlna);
        }
        // ///////////////////////////////////////////////////////////////
        // Passing through all DLNA servers and put it in multimediaList
        // //////////////////////////////////////////////////////////////
        DlnaContainer dir = null;
        for (int j = 0; j < numberOfDlna; j++) {
            DlnaDMS newMServer = null;
            try {
                newMServer = mPlayerControl.getServer(j);
            } catch (DlnaException e) {
                e.printStackTrace();
            }
            if (newMServer != null) {
                dir = newMServer.getRoot();
            }
            if (dir != null) {
                String name = dir.getFriendlyName();
                if (name != null) {
                    if (!name.equals(DOT) && !name.equals(TWO_DOT)) {
                        multimedia = new MultimediaContent(name, "", "", "",
                                DLNA, -1, DLNA_ROOT + name, dir.getID(),
                                dir.getFriendlyName(), dir.getID(), 0);
                        multimediaList.add(multimedia);
                    }
                }
            }
        }
        globalPathForMultimedia = SLASH;
        setCurDir(SLASH);
    }

    /**
     * Put folders or items of DLNA in multimediaList
     * 
     * @param multimediaType
     *        - set to DEFAULT and use for image icon
     * @param globalPath
     *        - Content path
     * @param containerId
     *        - Content id
     * @return true - If folders in DLNA server contains some item or folder
     *         <p>
     *         false - If folders in DLNA server empty
     */
    private boolean listDirectory(String multimediaType, String globalPath,
            String containerId) {
        String slash = "";
        // DlnaContainer con = null;
        try {
            if (isFavorite) {
                mPlayerControl.openContainer(rootID);
            }
            con = mPlayerControl.openContainer(containerId);
            if (IWEDIAService.DEBUG) {
                Log.e("count", "" + con.getChildCount());
            }
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        // ///////////////////////////////////////////////////////
        // If folders in DLNA server contains some item or folder
        // ///////////////////////////////////////////////////////
        if (con.getChildCount() != 0) {
            globalPathForMultimedia = globalPath;
            if (!globalPath.endsWith(SLASH)) {
                slash = SLASH;
            }
            DlnaObject[] ret = new DlnaObject[con.getChildCount()];
            // /////////////////////////////
            // Passing through all items
            // and add it in multimediaList
            // /////////////////////////////
            Log.d(LOG_TAG, "ret.length: " + ret.length);
            for (int i = 0; i < ret.length; i++) {
                try {
                    ret[i] = mPlayerControl.readContainer(con);
                    Log.d(LOG_TAG, "LOG 2 ----------------> ");
                } catch (DlnaException e) {
                    e.printStackTrace();
                }
                Log.i("before DlnaContainer", "name:" + multimedia.getName());
                // ///////////////////////////
                // If DlnaObject folder
                // ///////////////////////////
                Log.d(LOG_TAG, "TS Name : " + ret[i].friendlyName);
                if (ret[i] instanceof DlnaContainer) {
                    Log.d(LOG_TAG, "Folder za TS");
                    DlnaContainer dlnaContainer = (DlnaContainer) ret[i];
                    String name = dlnaContainer.getFriendlyName();
                    if (name != null) {
                        if (!name.equals(DOT) && !name.equals(TWO_DOT)) {
                            multimedia = new MultimediaContent(name, "", "",
                                    "dir", multimediaType, -1, globalPath
                                            + slash + name,
                                    dlnaContainer.getID(), name, rootID, 0);
                            multimediaList.add(multimedia);
                        }
                    }
                    Log.i("DlnaContainer", "multimedia.getExtension:"
                            + multimedia.getExtension());
                    Log.i("DlnaContainer", "multimedia.getDlnaName:"
                            + multimedia.getDlnaName());
                    Log.i("DlnaContainer", "multimedia.getImageType:"
                            + multimedia.getImageType());
                    Log.i("DlnaContainer",
                            "multimedia.getMime:" + multimedia.getMime());
                } else {
                    // ///////////////////////////
                    // If DlnaObject picture, audio or video item
                    // ///////////////////////////
                    if (ret[i] instanceof DlnaPictureItem) {
                        Log.d(LOG_TAG, "DlnaPictureItem za TS");
                        DlnaPictureItem item = (DlnaPictureItem) ret[i];
                        String name = item.getFriendlyName();
                        String ext = item.getMime();
                        String extension = getExtensionFromItem(ext);
                        if (name != null) {
                            multimedia = new MultimediaContent(name,
                                    item.getURI(), extension, "file",
                                    multimediaType, -1, globalPath + slash
                                            + name, item.getID(), name, rootID,
                                    0);
                            multimedia.setMime(ext);
                            multimediaList.add(multimedia);
                        }
                        if (IWEDIAService.DEBUG) {
                            Log.e("mimepicture",
                                    "mimepicture:" + item.getMime());
                            Log.e("uriimage", "uriimage:" + item.getURI());
                            Log.e("profileImage",
                                    "profileImage: " + item.getProfile());
                        }
                    } else {
                        if (ret[i] instanceof DlnaAudioItem) {
                            Log.d(LOG_TAG, "DlnaAudioItem za TS");
                            DlnaAudioItem item = (DlnaAudioItem) ret[i];
                            String name = item.getFriendlyName();
                            String ext = item.getMime();
                            String extension = getExtensionFromItem(ext);
                            if (name != null) {
                                if (!name.equals(DOT) && !name.equals(TWO_DOT)) {
                                    multimedia = new MultimediaContent(name,
                                            item.getURI(), extension, "file",
                                            multimediaType, -1, globalPath
                                                    + slash + name,
                                            item.getID(), name, rootID, 0);
                                    multimedia.setMime(ext);
                                    multimediaList.add(multimedia);
                                }
                            }
                            if (IWEDIAService.DEBUG) {
                                Log.e("mimeaudio",
                                        "mimeaudio:" + item.getMime());
                                Log.e("uriaudio", "uriaudio:" + item.getURI());
                                Log.e("profileAudio",
                                        "profileAudio: " + item.getProfile());
                            }
                        } else {
                            if (ret[i] instanceof DlnaVideoItem) {
                                Log.d(LOG_TAG, "DlnaVideoItem za TS");
                                DlnaVideoItem item = (DlnaVideoItem) ret[i];
                                String name = item.getFriendlyName();
                                String ext = item.getMime();
                                String extension = getExtensionFromItem(ext);
                                if (name != null) {
                                    if (!name.equals(DOT)
                                            && !name.equals(TWO_DOT)) {
                                        multimedia = new MultimediaContent(
                                                name, item.getURI(), extension,
                                                "file", multimediaType, -1,
                                                globalPath + slash + name,
                                                item.getID(), name, rootID, 0);
                                        multimedia.setMime(ext);
                                        multimediaList.add(multimedia);
                                    }
                                }
                                if (IWEDIAService.DEBUG) {
                                    Log.e("mimevideo",
                                            "mimevideo:" + item.getMime());
                                    Log.e("urivideo",
                                            "urivideo:" + item.getURI());
                                    Log.e("profileVideo", "profileVideo: "
                                            + item.getProfile());
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @param ext
     *        - example: ext is type - image/jpeg
     * @return parse and return extension (jpeg)
     */
    private String getExtensionFromItem(String ext) {
        int index = ext.indexOf(SLASH);
        String extension = EMPTY_STRING;
        if (index != -1) {
            extension = ext.substring(index + 1, ext.length());
        }
        return extension;
    }

    /**
     * @return instance of Multimedia
     */
    public static MultimediaManager getInstante() {
        if (instance == null) {
            instance = new MultimediaManager();
        }
        return instance;
    }

    @Override
    public Content getContent(int index) {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "getContent:" + index + "name:"
                    + multimediaList.get(index).getName());
        return multimediaList.get(index);
    }

    @Override
    public List<Content> getContentList(int startIndex, int endIndex) {
        return null;
    }

    @Override
    public int goContent(Content mContent, int displayId) {
        MultimediaContent content = (MultimediaContent) mContent;
        rootID = content.getRootID();
        if (content.isFavorite() == 1) {
            isFavorite = true;
        } else {
            isFavorite = false;
        }
        // try {
        if (content.getFilterType() == FilterType.PVR_RECORDED)
        // if (isPVRRoot)
        {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "1");
            }
            try {
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .getPvrControl()
                        .startPlayback(displayId, content.getIndex());
            } catch (Exception e) {
                return 1;
            }
            addItemToRecentlyWatchedList(content);
            return -1;
        } else
        // ////////////////////////////////
        // NOT PVR
        // /////////////////////////////
        {
            // ///////////////////////////
            // File type not NULL
            // ///////////////////////////
            if (content.getType() != null) {
                if (content.getType().equals("file")) {
                    if (content.getExtension().equals("wav")
                            || content.getExtension().equals("mid")
                            || content.getExtension().equals("mp3")
                            || content.getExtension().equals("mpeg")
                            || content.getExtension().equals("audio/wav")
                            || content.getExtension().equals("audio/mid")
                            || content.getExtension().equals("audio/mp3")
                            || content.getExtension().equals("audio/mpeg")) {
                        addItemToRecentlyWatchedList(content);
                        if (IWEDIAService.DEBUG) {
                            Log.e(LOG_TAG, "2");
                        }
                        // /////////////////////////
                        // If url exists
                        // /////////////////////////
                        return -1;
                    } else if (content.getExtension().equals("3gp")
                            || content.getExtension().equals("mp4")
                            || content.getExtension().equals("avi")
                            || content.getExtension().equals("mpg")
                            || content.getExtension().equals("mpeg")
                            || content.getExtension().equals("mkv")
                            || content.getExtension().equals("x-ms-wmv")
                            || content.getExtension().equals("video/3gp")
                            || content.getExtension().equals("video/mp4")
                            || content.getExtension().equals("video/avi")
                            || content.getExtension().equals("video/mpg")
                            || content.getExtension().equals("video/mpeg")
                            || content.getExtension().equals("video/x-ms-wmv")
                            || content.getExtension()
                                    .equals("video/x-matroska")
                            || content.getExtension().equals("video/x-msvideo")) {
                        if (IWEDIAService.DEBUG) {
                            Log.e(LOG_TAG, "3");
                        }
                        addItemToRecentlyWatchedList(content);
                        return -1;
                    } else if (content.getExtension().equals("jpg")
                            || content.getExtension().equals("bmp")
                            || content.getExtension().equals("png")
                            || content.getExtension().equals("jpeg")) {
                        // IMAGE
                        addItemToRecentlyWatchedList(content);
                        return -1;
                    } else {
                        return 1;
                    }
                    // }
                } else {
                    String globalPath = content.getAbsolutePath();
                    if (IWEDIAService.DEBUG) {
                        Log.e("globalPath", "globalPath" + globalPath);
                        Log.e("oldPath", "oldPath" + oldPath);
                    }
                    if (globalPath.equals(oldPath)
                            || oldPath.equals(globalPath + SLASH)) {
                        return SAME_FOLDER;
                    }
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "*****************************:"
                                + content.getAbsolutePath());
                        Log.e(LOG_TAG, "*****************************:"
                                + content.getName());
                        Log.e(LOG_TAG, "*****************************:"
                                + content.getDlnaName());
                        Log.e(LOG_TAG, "*****************************:"
                                + content.getId());
                    }
                    int returnValue;
                    if (content.getAbsolutePath().startsWith(DLNA_ROOT)) {
                        if (content.getId() != null) {
                            returnValue = setCurDirService(globalPath,
                                    content.getId(), null);
                        } else {
                            return 0;
                        }
                    } else if (content.getAbsolutePath().startsWith(
                            PLAYLIST_ROOT)) {
                        returnValue = setCurDirService(globalPath, "", content);
                    } else {
                        returnValue = setCurDirService(globalPath, "", null);
                    }
                    // ////////////////////////////////////
                    // Calculate folder level difference
                    // ///////////////////////////////////
                    if (globalPath.equals(PVR)) {
                        folderLevel = 1;
                        if (IWEDIAService.DEBUG) {
                            Log.e("pvr putanja", "pvr putanja" + globalPath);
                        }
                    } else {
                        if (IWEDIAService.DEBUG) {
                            Log.e("globalPath", "globalPath:" + globalPath);
                            Log.e("oldPath", "oldPath:" + oldPath);
                        }
                        if (content.getAbsolutePath().startsWith(LOCAL_ROOT)) {
                            folderLevel = calculateFolderLevelDifference(
                                    getCurDir(), oldPath);
                        } else {
                            folderLevel = calculateFolderLevelDifference(
                                    globalPath
                                    /* getCurDir() */, oldPath);
                        }
                    }
                    if (IWEDIAService.DEBUG) {
                        Log.e("folderLevel", "" + folderLevel);
                        Log.e("returnValue", "" + returnValue);
                    }
                    if (returnValue != -1 && folderLevel != 0) {
                        if (content.getAbsolutePath().startsWith(LOCAL_ROOT)) {
                            oldPath = getCurDir();
                        } else {
                            oldPath = globalPath;
                        }
                        if (IWEDIAService.DEBUG) {
                            Log.e("ENTERED USLOV", "ENTERED");
                        }
                        recentlyAccessedFolders.clear();
                        if (IWEDIAService.DEBUG) {
                            Log.e("GLOBAL PATH", globalPath);
                        }
                        String[] split;
                        if (globalPath.equals(PVR)) {
                            split = globalPath.split("[/]+");
                        } else {
                            if (content.getAbsolutePath()
                                    .startsWith(LOCAL_ROOT)) {
                                split = getCurDir().split("[/]+");
                            } else {
                                split = /* getCurDir() */globalPath
                                        .split("[/]+");
                            }
                        }
                        int lenght;
                        if (IWEDIAService.DEBUG) {
                            Log.e("split", split[0]);
                        }
                        // ///////////////////////
                        // For DLNA
                        // //////////////////////
                        if (split[0].equals("dlna:")) {
                            lenght = split.length - 1;
                        }
                        // /////////////////////
                        // Other
                        // ////////////////////
                        else {
                            lenght = split.length;
                        }
                        if (IWEDIAService.DEBUG) {
                            Log.e("LENGHT", lenght + "");
                        }
                        // ///////////////////////////////////
                        // example: local:///mnt/usb
                        // return 3 path and add path to recently accessed
                        // folder
                        // path are: local:/// ; local:///mnt ; local:///mnt/usb
                        // ///////////////////////////////////
                        DlnaContainer conFilePath = con;
                        for (int i = 0; i < lenght; i++) {
                            String name = "";
                            String type = "DEFAULT";
                            StringBuffer pathBuffer = new StringBuffer();
                            // ///////////////////////
                            // First element
                            // ///////////////////////
                            if (i == 0) {
                                if (split[i].equals("local:")) {
                                    name = LOCAL_STORAGE;
                                    pathBuffer.append(split[i] + "///");
                                    type = LOCAL;
                                }
                                if (split[i].equals("dlna:")) {
                                    name = split[1];
                                    pathBuffer.append(split[0] + "///"
                                            + split[1]);
                                    type = DLNA;
                                }
                                // ////////////////////////////
                                if (split[i].equals("Playlists:")) {
                                    name = PLAYLIST;
                                    pathBuffer.append(split[0] + "//");
                                    type = PLAYLIST;
                                }
                                // ////////////////////////////
                            }
                            // /////////////////////////
                            // Others
                            // ////////////////////////
                            else {
                                // /////////////////////
                                // Local
                                // ////////////////////
                                if (split[0].equals("local:")
                                        || split[0].equals("Playlists:")) {
                                    name = split[i];
                                    for (int j = 0; j <= i; j++) {
                                        // /////////////////////
                                        // First element
                                        // ///////////////////////
                                        if (j == 0) {
                                            pathBuffer.append(split[j] + "///");
                                        }
                                        // /////////////////////
                                        // Others
                                        // /////////////////////
                                        else {
                                            // ////////////////////
                                            // Last element
                                            // ////////////////////
                                            if (j == i) {
                                                pathBuffer.append(split[j]);
                                            }
                                            // //////////////////
                                            // Middle element
                                            // /////////////////
                                            else {
                                                pathBuffer.append(split[j]
                                                        + "/");
                                            }
                                        }
                                    }
                                }
                                // ///////////////////////////
                                // DLNA
                                // //////////////////////////
                                if (split[0].equals("dlna:")) {
                                    name = split[i + 1];
                                    for (int j = 0; j <= i; j++) {
                                        if (IWEDIAService.DEBUG) {
                                            Log.e("J", j + "");
                                        }
                                        // /////////////////////
                                        // First elements
                                        // ////////////////////
                                        if (j == 0) {
                                            pathBuffer.append(split[j] + "///"
                                                    + split[j + 1] + "/");
                                        }
                                        // ////////////////////
                                        // Others
                                        // ////////////////////
                                        else {
                                            // ////////////////////
                                            // Last element
                                            // ////////////////////
                                            if (j == i) {
                                                pathBuffer.append(split[j + 1]);
                                            }
                                            // //////////////////
                                            // Middle element
                                            // /////////////////
                                            else {
                                                pathBuffer.append(split[j + 1]
                                                        + "/");
                                            }
                                        }
                                    }
                                }
                            }
                            if (IWEDIAService.DEBUG)
                                Log.e("Apppp", "PATH: " + pathBuffer.toString()
                                        + " " + i);
                            MultimediaContent multimediaContent;
                            if (content.getAbsolutePath()
                                    .startsWith(LOCAL_ROOT)
                                    || content.getAbsolutePath().startsWith(
                                            PVR_ROOT)) {
                                multimediaContent = new MultimediaContent(name,
                                        "", "", "dir", type, -1,
                                        pathBuffer.toString(), "", "",
                                        EMPTY_STRING, 0);
                            } else if (content.getAbsolutePath().startsWith(
                                    PLAYLIST_ROOT)) {
                                multimediaContent = new MultimediaContent(1,
                                        name, "dir", pathBuffer.toString(), "",
                                        "", "", "", 0, "", type, name, "");
                                String playlistName = name + ".ml";
                                String playlistType = playlistManager
                                        .getPlaylistType(playlistName);
                                multimediaContent.setPlaylistType(playlistType);
                            } else {
                                multimediaContent = new MultimediaContent(name,
                                        "", "", "dir", type, -1,
                                        pathBuffer.toString(),
                                        conFilePath.getID(), name, rootID, 0);
                                try {
                                    conFilePath = mPlayerControl
                                            .openContainer(conFilePath
                                                    .getParentID());
                                } catch (DlnaException e) {
                                    e.printStackTrace();
                                }
                            }
                            addItemToRecentlyWatchedFolderList(multimediaContent);
                            if (IWEDIAService.DEBUG) {
                                Log.e("split", "" + split[i] + " " + i);
                            }
                        }
                        String tmpId;
                        int size = lenght / 2;
                        for (int k = 0; k < size; k++) {
                            tmpId = recentlyAccessedFolders.get(k).getId();
                            recentlyAccessedFolders.get(k).setId(
                                    recentlyAccessedFolders.get(lenght - k - 1)
                                            .getId());
                            recentlyAccessedFolders.get(lenght - k - 1).setId(
                                    tmpId);
                        }
                        if (IWEDIAService.DEBUG) {
                            Log.e("RETURNING FOLDER LEVEL", folderLevel + "");
                        }
                        return folderLevel;
                    }
                    // /////////////////////////////
                    // Same folder or empty folder
                    // /////////////////////////////
                    else {
                        return 0;
                    }
                }
            } else {
                // //////////////////////////
                // FILE TYPE NULL
                // ///////////////////////////
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "file type null?");
                }
                return 0;
            }
        }
    }

    /**
     * Calculate folder level difference
     * 
     * @param newPath
     *        - content path
     * @param oldPath
     *        - old global path
     * @return folder level difference
     */
    private int calculateFolderLevelDifference(String newPath, String oldPath) {
        if (IWEDIAService.DEBUG) {
            Log.e("FOLDER LEVEL NEW", newPath);
        }
        if (IWEDIAService.DEBUG) {
            Log.e("FOLDER LEVEL OLD", oldPath);
        }
        String[] newPathSplit = newPath.split("[/]+");
        String[] oldPathSplit = oldPath.split("[/]+");
        int levelDifference;
        // ///////////////////
        // DLNA
        // ///////////////////
        if (newPathSplit[0].equals("dlna:")) {
            // /////////////////////////
            // Entering in server for the first time
            // //////////////////////////////////////
            if (oldPath.equals(SLASH)) {
                levelDifference = newPathSplit.length - oldPathSplit.length - 1;
            }
            // ////////////////////////////////
            // Every other time
            // ///////////////////////////////
            else {
                levelDifference = newPathSplit.length - oldPathSplit.length;
            }
        } else {
            // //////////////////////////
            // LOCAL
            // //////////////////////////
            if (newPathSplit[0].equals("local:")) {
                // /////////////////////////////
                // USB
                // ////////////////////////////
                if (newPath.startsWith(PATH_TO_USB)) {
                    // /////////////////////////////////
                    // ENTERING USB FROM FIRST SCREEN
                    // /////////////////////////////////
                    if (oldPath.equals(SLASH)) {
                        levelDifference = newPathSplit.length
                                - oldPathSplit.length - 2;
                    }
                    // ///////////////////////////////////
                    // ENTERING USB FROM LOCAL
                    // //////////////////////////////////
                    else {
                        levelDifference = newPathSplit.length
                                - oldPathSplit.length;
                    }
                }
                // ///////////////////////////
                // STANDARD LOCAL
                // //////////////////////////
                else {
                    levelDifference = newPathSplit.length - oldPathSplit.length;
                }
            } else {
                levelDifference = newPathSplit.length - oldPathSplit.length;
            }
        }
        return levelDifference;
    }

    private void addItemToRecentlyWatchedFolderList(Content mContent) {
        MultimediaContent content = (MultimediaContent) mContent;
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "adding item to recently:" + content.getId());
        }
        recentlyAccessedFolders.add(content);
        for (int i = 0; i < recentlyAccessedFolders.size(); i++) {
            recentlyAccessedFolders.get(i).getId();
        }
        if (recentlyAccessedFolders.size() == 10) {
            recentlyAccessedFolders.remove(0);
        }
    }

    private void addItemToRecentlyWatchedList(Content mContent) {
        MultimediaContent content = (MultimediaContent) mContent;
        recentlyAccessed.add(0, content);
        if (content.getFilterType() == FilterType.PVR_RECORDED)
        // if(isPVRRoot == true)
        {
            for (int i = 1; i < recentlyAccessed.size(); i++) {
                if (recentlyAccessed.get(i).getName().equals(content.getName())
                        && recentlyAccessed.get(i).getDescription()
                                .equals(content.getDescription())
                        && recentlyAccessed.get(i).getDurationTime()
                                .equals(content.getDurationTime())
                        && recentlyAccessed.get(i).getTimeDate()
                                .equals(content.getTimeDate())) {
                    recentlyAccessed.remove(i);
                }
            }
        } else {
            for (int i = 1; i < recentlyAccessed.size(); i++) {
                if (recentlyAccessed.get(i).getName().equals(content.getName())
                        && recentlyAccessed.get(i).getExtension()
                                .equals(content.getExtension())
                        && recentlyAccessed.get(i).getType()
                                .equals(content.getType())
                        && recentlyAccessed.get(i).getFileURL()
                                .equals(content.getFileURL())
                        && recentlyAccessed.get(i).getImageType()
                                .equals(content.getImageType())) {
                    recentlyAccessed.remove(i);
                }
            }
        }
        if (recentlyAccessed.size() == MAX_RECENTLY_WATCHED_SIZE) {
            recentlyAccessed.remove(MAX_RECENTLY_WATCHED_SIZE - 1);
        }
    }

    @Override
    public void goPath(String path) {
        Log.e(LOG_TAG, "goPath - entered");
        if (isPVRRoot) {
            isPVRRoot = false;
            // setCurDirService(SLASH, null);
        }
        if (IWEDIAService.DEBUG) {
            Log.e("go path", path);
        }
        try {
            if (path.equals(SLASH)) {
                isDlna = false;
                if (isPlaylistRoot != 0) {
                    isPlaylistRoot--;
                }
                setCurDirService(path, null, null);
            } else {
                // //////////////
                // If DLNA
                // //////////////
                if (isDlna == true && path.equals(TWO_DOT)) {
                    if (IWEDIAService.DEBUG)
                        Log.e("before globalPathForMultimedia",
                                "globalPathForMultimedia:"
                                        + globalPathForMultimedia);
                    int index = globalPathForMultimedia.lastIndexOf(SLASH);
                    String globalPath = globalPathForMultimedia.substring(0,
                            index);
                    if (IWEDIAService.DEBUG)
                        Log.e("after globalPathForMultimedia",
                                "globalPathForMultimedia:" + globalPath);
                    try {
                        con = mPlayerControl.openContainer(con.getParentID());
                        if (IWEDIAService.DEBUG) {
                            Log.e("con..", "con.." + con.getFriendlyName());
                        }
                    } catch (DlnaException e) {
                        e.printStackTrace();
                    }
                    setCurDirService(globalPath, con.getID(), null);
                } else {
                    if (isPlaylistRoot != 0) {
                        isPlaylistRoot--;
                        int index = globalPathForMultimedia.lastIndexOf(SLASH);
                        path = globalPathForMultimedia.substring(0, index);
                    }
                    // //////////////////////
                    // If LOCAL OR USB
                    // /////////////////////
                    if (IWEDIAService.DEBUG) {
                        Log.e("path", "path: " + path);
                    }
                    setCurDirService(path, null, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // oldPath = getCurDir();
        oldPath = globalPathForMultimedia;
        if (recentlyAccessedFolders.size() != 0) {
            recentlyAccessedFolders.remove(recentlyAccessedFolders.size() - 1);
        }
    }

    @Override
    public int getContentListSize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "content list size:" + multimediaList.size());
        }
        return multimediaList.size();
    }

    @Override
    public boolean addContentToFavorites(Content mContent) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "addContentToFavouriteList");
        }
        MultimediaContent content = (MultimediaContent) mContent;
        content.setFavorite(1);
        if (content.getFilterType() == FilterType.PVR_RECORDED) {
            for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
                if ((favouriteList.get(i).getName().equals(content.getName()))
                        && ((MultimediaContent) favouriteList.get(i))
                                .getTimeDate().equals(content.getTimeDate())
                        && ((MultimediaContent) favouriteList.get(i))
                                .getDescription().equals(
                                        content.getDescription())
                        && ((MultimediaContent) favouriteList.get(i))
                                .getDurationTime().equals(
                                        content.getDurationTime())) {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "favourite item already in list");
                    }
                    return false;
                }
        }
        // ///////////////////////////////
        // Others
        // ///////////////////////////////
        else {
            for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
                if ((favouriteList.get(i).getName().equals(content.getName()))
                        && ((MultimediaContent) favouriteList.get(i))
                                .getFileURL().equals(content.getFileURL())
                        && ((MultimediaContent) favouriteList.get(i))
                                .getAbsolutePath().equals(
                                        content.getAbsolutePath())) {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "favourite item already in list");
                    }
                    return false;
                }
        }
        favouriteList.add(content);
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.FAVOURITE_LIST);
        IWEDIAService
                .getInstance()
                .getStorageManager()
                .addContentToList(
                        IWEDIAService.getInstance()
                                .getMultimediaListTableName(), content);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "favourite item added to list");
        }
        return true;
    }

    @Override
    public boolean removeContentFromFavorites(Content mContent) {
        MultimediaContent content = (MultimediaContent) mContent;
        if (content.getFilterType() == FilterType.PVR_RECORDED) {
            return removeContentFromPvrList(content);
        } else {
            for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
                if ((favouriteList.get(i).getName().equals(content.getName()))
                        && ((MultimediaContent) favouriteList.get(i))
                                .getFileURL().equals(content.getFileURL())) {
                    favouriteList.remove(i);
                    IWEDIAService.getInstance().getStorageManager()
                            .setActiveController(ControllerType.FAVOURITE_LIST);
                    IWEDIAService
                            .getInstance()
                            .getStorageManager()
                            .removeContentFromList(
                                    IWEDIAService.getInstance()
                                            .getMultimediaListTableName(),
                                    content);
                    return true;
                }
            return false;
        }
    }

    /** Remove pvr file from local pvr list */
    private boolean removeContentFromPvrList(Content content) {
        MultimediaContent mContent = (MultimediaContent) content;
        // Remove from recently
        for (int i = recentlyAccessed.size() - 1; i >= 0; i--) {
            if ((recentlyAccessed.get(i).getName().equals(mContent.getName()))
                    && recentlyAccessed.get(i).getTimeDate()
                            .equals(mContent.getTimeDate())
                    && recentlyAccessed.get(i).getDescription()
                            .equals(mContent.getDescription())
                    && recentlyAccessed.get(i).getDurationTime()
                            .equals(mContent.getDurationTime())) {
                recentlyAccessed.remove(i);
            }
        }
        // Remove from favorite
        for (int i = favouriteList.size() - 1; i >= 0; i--) {
            if ((favouriteList.get(i).getName().equals(mContent.getName()))
                    && ((MultimediaContent) favouriteList.get(i)).getTimeDate()
                            .equals(mContent.getTimeDate())
                    && ((MultimediaContent) favouriteList.get(i))
                            .getDescription().equals(mContent.getDescription())
                    && ((MultimediaContent) favouriteList.get(i))
                            .getDurationTime().equals(
                                    mContent.getDurationTime())) {
                favouriteList.remove(i);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getMultimediaListTableName(), content);
            }
        }
        for (int i = multimediaList.size() - 1; i >= 0; i--) {
            if ((multimediaList.get(i).getName().equals(mContent.getName()))
                    && multimediaList.get(i).getTimeDate()
                            .equals(mContent.getTimeDate())
                    && multimediaList.get(i).getDescription()
                            .equals(mContent.getDescription())
                    && multimediaList.get(i).getDurationTime()
                            .equals(mContent.getDurationTime())) {
                multimediaList.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeAllContentsFromFavorites(int filterType) {
        // Remove from recently
        for (int i = recentlyAccessed.size() - 1; i >= 0; i--) {
            if (recentlyAccessed.get(i).getFilterType() == filterType) {
                recentlyAccessed.remove(i);
            }
        }
        // Remove from favorite
        for (int i = favouriteList.size() - 1; i >= 0; i--) {
            if (favouriteList.get(i).getFilterType() == filterType) {
                favouriteList.remove(i);
            }
        }
        for (int i = multimediaList.size() - 1; i >= 0; i--) {
            if (multimediaList.get(i).getFilterType() == filterType) {
                multimediaList.remove(i);
            }
        }
        return true;
    }

    @Override
    public int getFavoritesSize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "SIZE OF FAVOURITE LIST:" + favouriteList.size());
        }
        return favouriteList.size();
    }

    @Override
    public Content getFavoriteItem(int index) {
        return favouriteList.get(index);
    }

    @Override
    public int getRecenltyWatchedListSize() {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "getRecenltyWatchedListSize:" + recentlyAccessed.size());
        return recentlyAccessed.size();
    }

    @Override
    public Content getRecentlyWatchedItem(int index) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRecentlyWatchedItem:" + index);
        }
        return recentlyAccessed.get(index);
    }

    @Override
    public int getPathSize() {
        return recentlyAccessedFolders.size();
    }

    @Override
    public Content getPath(int index) {
        return recentlyAccessedFolders.get(index);
    }

    // ////////////////////////////////////////////////
    // Methods who call native method in Filelib library
    // /////////////////////////////////////////////////
    public int initNative() {
        return mediaControlNative.initNative();
    }

    /** Deinitialization by calling native methods. */
    public int deinitNative() {
        return mediaControlNative.deinitNative();
    }

    /**
     * Registering file handler by calling native methods. Initialization of
     * filelib SDK should be done in two steps. First step is to initialize
     * filelib SDK resources and second step is to initialize and register
     * filelib handlers.
     * 
     * @return return 1 on success or 0 on error.
     */
    public int registerEntry(int fileHandlingType) {
        return mediaControlNative.registerEntry(fileHandlingType);
    }

    /**
     * Unregistering file handler by calling native methods.
     * 
     * @return return 1 on success or 0 on error.
     */
    public int unregisterEntry(int fileHandlingType) {
        return mediaControlNative.unregisterEntry(fileHandlingType);
    }

    /**
     * Set current directory by calling native methods.
     * 
     * @return Function return new current directory on success or NULL on
     *         error.
     */
    public String setCurDir(String newCurrentDir) {
        return mediaControlNative.setCurDir(newCurrentDir);
    }

    /**
     * Get current directory by calling native methods.
     * 
     * @return Function will return address on filled URI buffer on success or
     *         NULL on error.
     */
    public String getCurDir() {
        return mediaControlNative.getCurDir();
    }

    /**
     * Functions that implement getting resources from current directory are
     * getFirst and getNext,getFirst should be called first, and then getNext
     * while it returns null string
     */
    public String getFirst(String URI) {
        return mediaControlNative.getFirst(URI);
    }

    /**
     * Functions that implement getting resources from current directory are
     * getFirst and getNext,getFirst should be called first, and then getNext
     * while it returns null string
     * 
     * @return null in case of error
     */
    public String getNext(String URI) {
        return mediaControlNative.getNext(URI);
    }

    /**
     * After getting URI to resource it is necessary to have more information
     * about it in order to invoke access to file data or to get some metadata
     * for resource. These extended information are available through
     * getFileProperty function:
     * 
     * @return string property or null on error
     */
    public String getFileProperty(String URI, int ID) {
        return mediaControlNative.getFileProperty(URI, ID);
    }

    @Override
    public int toInt() {
        return -1;
    }

    private Enumeration<NetworkInterface> en;
    private Enumeration<InetAddress> enumIpAddr;
    private NetworkInterface intf;
    private InetAddress inetAddress;

    public String getLocalIpAddress() {
        try {
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                intf = en.nextElement();
                enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLinkLocalAddress()) {
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Error", ex.toString());
        }
        return null;
    }

    @Override
    public Content getContentExtendedInfo() {
        return null;
    }

    public void mediaEjected() {
        int size = favouriteList.size();
        for (int i = size - 1; i >= 0; i--) {
            if (favouriteList.get(i).getFilterType() == FilterType.PVR_RECORDED) {
                favouriteList.remove(i);
            }
        }
    }

    @Override
    public Content getContentExtendedInfoByIndex(int index) {
        return null;
    }

    @Override
    public void deletePlaylist(Content content, String playlistName) {
        playlistManager.deletePlaylist(playlistName);
        MultimediaContent mContent = (MultimediaContent) content;
        // Remove from recently
        for (int i = recentlyAccessed.size() - 1; i >= 0; i--) {
            if ((recentlyAccessed.get(i).getPlaylistName().equals(mContent
                    .getPlaylistName()))
                    && recentlyAccessed.get(i).getName()
                            .equals(mContent.getName())) {
                recentlyAccessed.remove(i);
            }
        }
        // Remove from favorite
        for (int i = favouriteList.size() - 1; i >= 0; i--) {
            if ((favouriteList.get(i).getName().equals(mContent.getName()))
                    && ((MultimediaContent) favouriteList.get(i))
                            .getPlaylistName().equals(
                                    mContent.getPlaylistName())) {
                favouriteList.remove(i);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getMultimediaListTableName(), content);
            }
        }
        for (int i = multimediaList.size() - 1; i >= 0; i--) {
            if ((multimediaList.get(i).getPlaylistName().equals(mContent
                    .getPlaylistName()))
                    && multimediaList.get(i).getName()
                            .equals(mContent.getName())) {
                multimediaList.remove(i);
            }
        }
    }

    @Override
    public void clearPlaylist(String playlistName) {
        playlistManager.clearPlaylist(playlistName);
        // Remove from recently
        for (int i = recentlyAccessed.size() - 1; i >= 0; i--) {
            recentlyAccessed.remove(i);
        }
        // Remove from favorite
        for (int i = favouriteList.size() - 1; i >= 0; i--) {
            favouriteList.remove(i);
        }
        for (int i = multimediaList.size() - 1; i >= 0; i--) {
            multimediaList.remove(i);
        }
    }

    @Override
    public void removeItemFromPlaylist(Content content, String playlistName,
            String URI) {
        playlistManager.removeItemFromPlaylist(playlistName, URI);
        MultimediaContent mContent = (MultimediaContent) content;
        // Remove from recently
        for (int i = recentlyAccessed.size() - 1; i >= 0; i--) {
            if ((recentlyAccessed.get(i).getPlaylistName().equals(mContent
                    .getPlaylistName()))
                    && recentlyAccessed.get(i).getName()
                            .equals(mContent.getName())) {
                recentlyAccessed.remove(i);
            }
        }
        // Remove from favorite
        for (int i = favouriteList.size() - 1; i >= 0; i--) {
            if ((favouriteList.get(i).getName().equals(mContent.getName()))
                    && ((MultimediaContent) favouriteList.get(i))
                            .getPlaylistName().equals(
                                    mContent.getPlaylistName())) {
                favouriteList.remove(i);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getMultimediaListTableName(), content);
            }
        }
        for (int i = multimediaList.size() - 1; i >= 0; i--) {
            if ((multimediaList.get(i).getPlaylistName().equals(mContent
                    .getPlaylistName()))
                    && multimediaList.get(i).getName()
                            .equals(mContent.getName())) {
                multimediaList.remove(i);
            }
        }
    }

    public boolean sortPlaylist(String playlistName, String criteria) {
        playlistManager.sortPlaylist(playlistName, criteria);
        ArrayList<MultimediaContent> playlistItems = playlistManager
                .getPl_items();
        multimediaList.clear();
        for (int i = 0; i < playlistItems.size(); i++) {
            MultimediaContent mContent = new MultimediaContent(playlistItems
                    .get(i).getPlaylistID(), playlistName, "file",
                    globalPathForMultimedia + SLASH + playlistName,
                    playlistItems.get(i).getTitle(), playlistItems.get(i)
                            .getArtist(), playlistItems.get(i).getFileURL(),
                    "", 0, playlistItems.get(i).getExtension(), DEFAULT,
                    playlistItems.get(i).getTitle(), "");
            multimediaList.add(mContent);
        }
        return true;
    }

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }
}
