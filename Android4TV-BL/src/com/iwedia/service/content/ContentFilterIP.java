package com.iwedia.service.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.SharedPreferences.Editor;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.storage.ControllerType;
import com.iwedia.service.system.SystemControl;

/**
 * Content filter used to manage MW IP services and IP Contents created from
 * URL. {@link IpContent};
 * 
 * @author Marko Zivanovic
 */
public class ContentFilterIP extends ContentFilter {
    private final String RADIUS_VECTOR_CHANNELS_FILE_NAME = "radius_vector_ip_channels.txt";
    private final String CONFIG_FILE_NAME_TARGET = "/data/data/com.iwedia.service/";
    private final String IP_CONFIG_FILE = "ip_config.txt";
    private final String DEUTSCHE_TELEKOM_CONFIG_FILE = "ip_service_list.txt";
    /**
     * Debug log tag.
     */
    private final String LOG_TAG = "ContentFilterIP";
    /**
     * Path of file that contains URL.
     */
    private static final String ConfigurationFilePath = "/sample-chan-config.cfg";
    /**
     * Reference of global list "recenltyWatchedList" in ContentListControl.
     * Used to add set InputContent to recently list of GUI application.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * This array list holds indexes of InputContents in recently list. (This
     * list is needed because recently list hold all kind of Contents).
     */
    private ArrayList<Integer> recentlyWatchedIndexes;
    /**
     * Index of currently active IP channel.
     */
    private int activeIndex;
    /**
     * Instance of ContentListControl class {@link ContentListControl}.
     */
    private ContentListControl contentListControl;
    /**
     * Parsed asets file ip_config.
     */
    private ArrayList<String[]> ipConfiguration;
    /**
     * String[0] = name; String[1] = url;
     */
    private ArrayList<String[]> radiusVectorConfig;
    /**
     * String[0] = name; String[1] = url;
     */
    private ArrayList<String[]> deutscheTelekom;
    /**
     * String[0] = name; String[1] = url;
     */
    private ArrayList<String[]> aluConfig;
    /**
     * String[0] = name; String[1] = url;
     */
    private ArrayList<String[]> mwConfig;
    /**
     * Applies on main display only
     */
    private int mDisplayId = PlaybackDestinationType.MAIN_LIVE;

    /**
     * Default constructor.
     * 
     * @param manager
     *        ContentManager instance of global content manager to handle
     *        ContentFilters.
     * @param recenltyWatched
     *        Instance of global list that represent recently accessed content
     *        items.
     */
    public ContentFilterIP(ContentManager manager,
            ArrayList<Content> recenltyWatched) {
        try {
            contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        /**
         * Save reference of recentlyWatched list as local value.
         */
        this.recenltyWatched = recenltyWatched;
        this.FILTER_TYPE = com.iwedia.comm.enums.FilterType.IP_STREAM;
        radiusVectorConfig = new ArrayList<String[]>();
        ipConfiguration = new ArrayList<String[]>();
        aluConfig = new ArrayList<String[]>();
        mwConfig = new ArrayList<String[]>();
        deutscheTelekom = new ArrayList<String[]>();
        copyFile(IP_CONFIG_FILE);
        readFile(IP_CONFIG_FILE, ipConfiguration);
        for (int i = 0; i < ipConfiguration.size(); i++) {
            Log.e(LOG_TAG, "ipConfiguration:" + ipConfiguration.get(i)[1]);
        }
        if (ipConfiguration.get(0)[1].equals("true")) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "parsing RADIUS VECTOR channels");
            }
            initRadiusVectorFile();
        }
        if (ipConfiguration.get(1)[1].equals("true")) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "parsing ALU settings");
            }
            parseALUStrings();
        }
        if (ipConfiguration.get(2)[1].equals("true")) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "parsing MW ip service");
            }
            parseMWIpServices();
        }
        if (ipConfiguration.get(3)[1].equals("true")) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "init deutsche telekom");
            }
            initDeutscheTelekom();
        }
    }

    private void initDeutscheTelekom() {
        copyFile(DEUTSCHE_TELEKOM_CONFIG_FILE);
        readFile(DEUTSCHE_TELEKOM_CONFIG_FILE, deutscheTelekom);
    }

    private void initRadiusVectorFile() {
        copyFile(RADIUS_VECTOR_CHANNELS_FILE_NAME);
        readFile(RADIUS_VECTOR_CHANNELS_FILE_NAME, radiusVectorConfig);
    }

    private String[] aluNames = new String[] { "Alcatel1", "Alcatel2",
            "Alcatel3", "Alcatel4", "VQEC1", "VQEC2", "VQEC3", "VQEC4", "HLS1" };
    private String[] aluURLs = new String[] { "rtp://239.100.0.1:8433",
            "rtp://239.100.0.2:8433", "rtp://239.100.0.3:8433",
            "rtp://239.100.0.4:8433", "VQEC1", "VQEC2", "VQEC3", "VQEC4",
            "HLS1" };

    private void parseALUStrings() {
        String[] alu;
        for (int i = 0; i < 4; i++) {
            alu = new String[2];
            alu[0] = aluNames[i];
            alu[1] = aluURLs[i];
            aluConfig.add(alu);
        }
    }

    /**
     * Read data from configuration file and store them in local list
     */
    private void parseMWIpServices() {
        File file = new File(ConfigurationFilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] separatedStrings;
            separatedStrings = new String[2];
            Log.d(LOG_TAG, "String: " + separatedStrings);
            String[] port = new String[4];
            Log.d(LOG_TAG, "String: " + port);
            String[] ipTemp = new String[3];
            Log.d(LOG_TAG, "String: " + ipTemp);
            String[] ip = new String[2];
            Log.d(LOG_TAG, "String: " + ip);
            Boolean newService = false;
            String portTemp = "";
            String[] tmp = new String[2];
            Log.d(LOG_TAG, "String: " + tmp);
            try {
                while ((line = br.readLine()) != null) {
                    separatedStrings = new String[2];
                    Log.d(LOG_TAG, "String: " + separatedStrings);
                    separatedStrings = line.split("=");
                    if (separatedStrings[0].equals("s")) {
                        tmp[0] = separatedStrings[1];
                        newService = true;
                    }
                    if (separatedStrings[0].equals("m") && newService) {
                        port = separatedStrings[1].split(" ");
                        portTemp = port[1];
                    }
                    if (separatedStrings[0].equals("c") && newService) {
                        ipTemp = separatedStrings[1].split(" ");
                        ip = ipTemp[2].split("/");
                        tmp[1] = ("rtp://" + ip[0] + ":" + portTemp);
                        newService = false;
                        mwConfig.add(tmp);
                        tmp = new String[2];
                    }
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public Content getContent(int index) {
        return getContent(index, false);
    }

    @Override
    public Content getContentExtendedInfo() {
        try {
            Content cntActive = contentListControl.getActiveContent(mDisplayId);
            if (cntActive != null) {
                return getContent(cntActive.getIndex(), true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Content getContent(int index, boolean serviceInfo) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContent:" + index);
        }
        if (index < mwConfig.size()) {
            // /**
            // * MW IP Service
            // */
            // try {
            // return new IpContent(index, IWEDIAService.getInstance()
            // .getDtvManagerProxy().getServiceListControl(),
            // ServiceListIndex.IP_STREAMED, true);
            // } catch (RemoteException e) {
            // e.printStackTrace();
            // }
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "returning mwIpServices content:" + index);
            }
            IpContent iContent = new IpContent(mwConfig.get(index)[0],
                    mwConfig.get(index)[1], index);
            return iContent;
        } else if ((mwConfig.size() <= index)
                && (index < (aluConfig.size() + mwConfig.size()))) {
            /**
             * returning ALU IP Content
             */
            IpContent iContent = new IpContent(aluConfig.get(index
                    - mwConfig.size())[0], aluConfig.get(index
                    - mwConfig.size())[1], index);
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG,
                        "returning ALU content:" + +(index - mwConfig.size())
                                + iContent.toString());
            return iContent;
        } else if ((aluConfig.size() + mwConfig.size() <= index)
                && (index < aluConfig.size() + mwConfig.size()
                        + deutscheTelekom.size())) {
            /**
             * Returning Deutsche Telekom IP Content.
             */
            int position = index - mwConfig.size() - aluConfig.size();
            IpContent iContent = new IpContent(
                    deutscheTelekom.get(position)[0],
                    deutscheTelekom.get(position)[1], index);
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG, "returning Deutsche Telekom content:" + position
                        + iContent.toString());
            return iContent;
        } else {
            /**
             * Returning Radius Vector Content.
             */
            int position = index - mwConfig.size() - aluConfig.size()
                    - deutscheTelekom.size();
            IpContent iContent = new IpContent(
                    radiusVectorConfig.get(position)[0],
                    radiusVectorConfig.get(position)[1], index);
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG, "returning Radius Vector content:" + position
                        + iContent.toString());
            return iContent;
        }
    }

    @Override
    public int goContent(Content content, int displayId) {
        IpContent iContent = (IpContent) content;
        contentListControl.setActiveContent(content, displayId);
        activeIndex = content.getIndex();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "goContent:" + iContent.toString());
        }
        int decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getDecoderID(displayId);
        int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getIpRouteID(decoderID);
        Log.d(LOG_TAG, "goContent decoderID[" + decoderID + "] routeID["
                + routeID + "]");
        if (iContent.getUrl().length() == 0) {
            // Go MW IP service
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "zapDigit");
            }
            try {
                IWEDIAService
                        .getInstance()
                        .getDtvManagerProxy()
                        .getServiceControl()
                        .goServiceIndexFromServiceList(
                                ServiceListIndex.IP_STREAMED,
                                content.getIndexInMasterList(), routeID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Go FILE IP URL
        } else {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "zapURL");
            }
            if (iContent.getUrl().endsWith("m3u8")
                    && ipConfiguration.get(0)[1].equals("true")) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "zap radius vector:" + iContent.getUrl());
                }
                IActionCallback actionCallback = SystemControl
                        .getActionCallback();
                if (actionCallback != null) {
                    try {
                        contentListControl.setGuiVideoViewIPUri(true);
                        actionCallback.startUrl(iContent.getUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*
                     * try { contentListControl.getChannelsCallbackManager()
                     * .channelZapping(true); } catch(RemoteException e) {
                     * e.printStackTrace(); }
                     */
                }
            } else {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "iContent zapURL:" + iContent.toString());
                }
                try {
                    IWEDIAService.getInstance().getDtvManagerProxy()
                            .setCurrentLiveRoute(routeID);
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDIP());
                    IWEDIAService.getInstance().getDTVManager()
                            .getServiceControl()
                            .zapURL(routeID, iContent.getUrl());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InternalException e) {
                    e.printStackTrace();
                }
            }
            Editor edit = IWEDIAService.getInstance().getPreferenceManager()
                    .edit();
            edit.putBoolean("ip_last_watched", true);
            edit.putInt("ip_displayId", displayId);
            edit.putString("ip_url", iContent.getUrl());
            edit.commit();
        }
        return 1;
    }

    /**
     * Opens a content by given index, e.g. runs Android application, plays
     * Radio channel.
     */
    @Override
    public int goContentByIndex(int index, int displayId) {
        Content content = getContent(index - 1);
        goContent(content, displayId);
        return content.getIndex();
    }

    /**
     * Stops content playback.
     */
    @Override
    public int stopContent(int displayId) {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getServiceControl()
                    .stopService(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getIpRouteID(0));
        } catch (InternalException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getContentListSize() {
        int size = mwConfig.size() + aluConfig.size()
                + radiusVectorConfig.size() + deutscheTelekom.size();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentListSize IP:" + size);
        }
        return size;
        // return 0;
    }

    @Override
    public boolean removeContentFromFavorites(Content content) {
        for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
            if (favouriteList.get(i).equals(content)) {
                favouriteList.remove(i);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService.getInstance().getStorageManager()
                        .removeContentFromList("default", content);
                return true;
            }
        return false;
    }

    @Override
    public int getRecenltyWatchedListSize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRecentlyWatchedListSize");
        }
        recentlyWatchedIndexes = new ArrayList<Integer>();
        for (int i = 0; i < recenltyWatched.size(); i++)
            if (recenltyWatched.get(i).getFilterType() == FILTER_TYPE) {
                recentlyWatchedIndexes.add(i);
            }
        return recentlyWatchedIndexes.size();
    }

    @Override
    public Content getRecentlyWatchedItem(int index) {
        return recenltyWatched.get(recentlyWatchedIndexes.get(index));
    }

    @Override
    public int getServiceListIndex() {
        return ServiceListIndex.IP_STREAMED;
    }

    /**
     * Return enum {@link com.iwedia.comm.enums.FilterType} of this
     * ContentFilter.
     */
    @Override
    public int toInt() {
        return FILTER_TYPE;
    }

    /**
     * Copy configuration file.
     */
    private void copyFile(String filename) {
        String file = CONFIG_FILE_NAME_TARGET + filename;
        File fl = new File(file);
        if (!fl.exists()) {
            copyAssetToData(filename);
        }
    }

    /**
     * Copy configuration file from assets to data folder.
     * 
     * @param strFilename
     */
    private void copyAssetToData(String strFilename) {
        // Open your local db as the input stream
        try {
            InputStream myInput = IWEDIAService.getInstance().getAssets()
                    .open(strFilename);
            String outFileName = CONFIG_FILE_NAME_TARGET + strFilename;
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the configuration file with built-in application which will be
     * displayed in Content list.
     */
    private void readFile(String filename, ArrayList<String[]> arrayList) {
        File file = new File(CONFIG_FILE_NAME_TARGET + filename);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            String[] separated = new String[2];
            Log.d(LOG_TAG, "String: " + separated);
            try {
                while ((line = br.readLine()) != null) {
                    separated = line.split("#");
                    arrayList.add(separated);
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
