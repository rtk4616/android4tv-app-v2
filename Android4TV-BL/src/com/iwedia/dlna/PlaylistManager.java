package com.iwedia.dlna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;
import com.iwedia.service.IWEDIAService;

class DurationSorter implements Comparator<MultimediaContent> {
    public int compare(MultimediaContent first, MultimediaContent second) {
        if (first.getDuration() == second.getDuration()) {
            return 0;
        }
        return first.getDuration() < second.getDuration() ? -1 : 1;
    }
}

class ArtistSorter implements Comparator<MultimediaContent> {
    public int compare(MultimediaContent first, MultimediaContent second) {
        return first.getArtist().compareTo(second.getArtist());
    }
}

class TitleSorter implements Comparator<MultimediaContent> {
    public int compare(MultimediaContent first, MultimediaContent second) {
        return first.getTitle().compareTo(second.getTitle());
    }
}

public class PlaylistManager {
    private static final String TAG = "PlaylistManager";
    private static PlaylistManager instance;
    private File playlistPath;
    private Context mContext;
    private static final String KEY_ITEM_LIST = "itemList"; // parent node
    private static final String KEY_ITEM = "item"; // parent node
    private static final String KEY_TYPE = "type";
    private static final String KEY_ENTRIES = "entries";
    private static final String KEY_ID = "id";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_RESOLUTION = "resolution";
    private static final String KEY_SOURCE = "source";
    private String playlist_folder_path;
    private Document doc;
    private String playlist_name;
    private int next_id;
    private ArrayList<MultimediaContent> pl_items;

    public PlaylistManager() {
        mContext = IWEDIAService.getContext();
        playlist_folder_path = mContext.getFilesDir() + "/playlist/";
        playlistPath = new File(playlist_folder_path);
        playlistPath.mkdirs();
        doc = null;
        playlist_name = "";
        next_id = 0;
    }

    private Document getDomElement(String xml) {
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            document = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return document;
    }

    private String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    private String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public boolean createPlaylist(String playlistName, String playlistType) {
        String filename = playlistName + ".ml";
        List<PlaylistFile> playlists = getPlaylists();
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().contains(playlistName)) {
                return false;
            }
        }
        String data = "<ml version='1.0'>\n\t<playlist>"
                + playlistName
                + "</playlist>\n\t<type>"
                + playlistType
                + "</type>\n\t<entries>0</entries>\n\t<itemList>\n\t</itemList>\n</ml>";
        // String data = "<ml version='1.0'>\n\t<playlist>" + playlistName +
        // "</playlist>\n\t<type>" + playlistType +
        // "</type>\n\t<entries>0</entries>\n</ml>";
        try {
            File playlistFile = new File(playlist_folder_path, filename);
            if (!playlistFile.exists()) {
                playlistFile.createNewFile();
                FileWriter filewriter = new FileWriter(playlistFile);
                try {
                    BufferedWriter out = new BufferedWriter(filewriter);
                    try {
                        out.write(data);
                    } finally {
                        out.close();
                    }
                } finally {
                    filewriter.close();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean openPlaylist(String playlistName) {
        String filename = playlistName + ".ml";
        String xml;
        File path = new File(playlist_folder_path, filename);
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path)));
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
            } finally {
                bufferedReader.close();
            }
            xml = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            return false;
        }
        if (xml != null) {
            doc = this.getDomElement(xml);
            if (doc != null) {
                Node plType = doc.getElementsByTagName(KEY_TYPE).item(0);
                String type = this.getElementValue(plType);
                // Node item_list =
                // doc.getElementsByTagName(KEY_ITEM_LIST).item(0);
                // NodeList nl = item_list.getChildNodes();
                NodeList nl = doc.getElementsByTagName(KEY_ITEM);
                int lId;
                String lTitle;
                String lArtist = "";
                String lResolution = "";
                int lDuration = 0;
                String lSource;
                // item = new PlaylistItem();
                // playlist_items = new ArrayList();
                pl_items = new ArrayList();
                for (int i = 0; i < nl.getLength(); i++) {
                    // creating new HashMap
                    Element e = (Element) nl.item(i);
                    // Node e = nl.item(i);
                    lId = Integer.parseInt(this.getValue(e, KEY_ID));
                    next_id = lId + 1;
                    lTitle = this.getValue(e, KEY_TITLE);
                    if (type.equals("audio")) {
                        lArtist = this.getValue(e, KEY_ARTIST);
                    }
                    if (type.equals("audio") || type.equals("video"))
                        lDuration = Integer.parseInt(this.getValue(e,
                                KEY_DURATION));
                    if (type.equals("image")) {
                        lResolution = this.getValue(e, KEY_RESOLUTION);
                    }
                    lSource = this.getValue(e, KEY_SOURCE);
                    int index = lSource.lastIndexOf(".");
                    String extension = lSource.substring(index + 1,
                            lSource.length());
                    pl_items.add(new MultimediaContent(next_id, lTitle,
                            lArtist, lSource, lResolution, lDuration, extension));
                }
            }
        } else {
            return false;
        }
        playlist_name = playlistName;
        return true;
    }

    public boolean isItemInTheList(String URL) {
        NodeList nl = doc.getElementsByTagName(KEY_ITEM);
        String source;
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            Element e = (Element) nl.item(i);
            source = this.getValue(e, KEY_SOURCE);
            if (URL.equals(source)) {
                return true;
            }
        }
        return false;
    }

    public String getPlaylistType(String filename) {
        String xml;
        String type = "";
        File path = new File(playlist_folder_path, filename);
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path)));
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
            } finally {
                bufferedReader.close();
            }
            xml = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            return "";
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            return "";
        }
        if (xml != null) {
            doc = this.getDomElement(xml);
            if (doc != null) {
                Node plType = doc.getElementsByTagName(KEY_TYPE).item(0);
                type = this.getElementValue(plType);
            }
        }
        return type;
    }

    /* this method is used to add audio item to the playlist */
    public boolean addAudioItemToPlaylist(String playlistName, String artist,
            String title, int duration, String URI) {
        // if(!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return false;
        }
        // }
        if (this.doc != null) {
            if (isItemInTheList(URI) == true) {
                return false;
            }
        }
        try {
            // Element rootElement = doc.getDocumentElement();
            if (this.doc != null) {
                Node item_list = doc.getElementsByTagName(KEY_ITEM_LIST)
                        .item(0);
                // server elements
                Element new_item = doc.createElement("item");
                item_list.appendChild(new_item);
                Element eId = doc.createElement("id");
                eId.appendChild(doc.createTextNode(Integer.toString(next_id++)));
                new_item.appendChild(eId);
                Element eTitle = doc.createElement("title");
                eTitle.appendChild(doc.createTextNode(title));
                new_item.appendChild(eTitle);
                Element eArtist = doc.createElement("artist");
                eArtist.appendChild(doc.createTextNode(artist));
                new_item.appendChild(eArtist);
                Element eDuration = doc.createElement("duration");
                eDuration.appendChild(doc.createTextNode(Integer
                        .toString(duration)));
                new_item.appendChild(eDuration);
                Element eSource = doc.createElement("source");
                eSource.appendChild(doc.createTextNode(URI));
                new_item.appendChild(eSource);
                Node playlist_entries = doc.getElementsByTagName(KEY_ENTRIES)
                        .item(0);
                int numberOfItems = Integer.parseInt(this
                        .getElementValue(playlist_entries)) + 1;
                playlist_entries
                        .setTextContent(Integer.toString(numberOfItems));
                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory
                        .newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                File path = new File(playlist_folder_path, playlistName + ".ml");
                StreamResult result = new StreamResult(path);
                transformer.transform(source, result);
            }
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return true;
    }

    /* this method is used to add video item to the playlist */
    public boolean addVideoItemToPlaylist(String playlistName, String title,
            int duration, String URI) {
        // if(!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return false;
        }
        // }
        if (this.doc != null) {
            if (isItemInTheList(URI) == true) {
                return false;
            }
        }
        try {
            // Element rootElement = doc.getDocumentElement();
            if (this.doc != null) {
                Node item_list = doc.getElementsByTagName(KEY_ITEM_LIST)
                        .item(0);
                // server elements
                Element new_item = doc.createElement("item");
                item_list.appendChild(new_item);
                Element eId = doc.createElement("id");
                eId.appendChild(doc.createTextNode(Integer.toString(next_id++)));
                new_item.appendChild(eId);
                Element eTitle = doc.createElement("title");
                eTitle.appendChild(doc.createTextNode(title));
                new_item.appendChild(eTitle);
                Element eDuration = doc.createElement("duration");
                eDuration.appendChild(doc.createTextNode(Integer
                        .toString(duration)));
                new_item.appendChild(eDuration);
                Element eSource = doc.createElement("source");
                eSource.appendChild(doc.createTextNode(URI));
                new_item.appendChild(eSource);
                Node playlist_entries = doc.getElementsByTagName(KEY_ENTRIES)
                        .item(0);
                int numberOfItems = Integer.parseInt(this
                        .getElementValue(playlist_entries)) + 1;
                playlist_entries
                        .setTextContent(Integer.toString(numberOfItems));
                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory
                        .newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                File path = new File(playlist_folder_path, playlistName + ".ml");
                StreamResult result = new StreamResult(path);
                transformer.transform(source, result);
            }
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return true;
    }

    /* this method is used to add image item to the playlist */
    public boolean addImageItemToPlaylist(String playlistName, String title,
            String resolution, String URI) {
        // if(!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return false;
        }
        // }
        if (this.doc != null) {
            if (isItemInTheList(URI) == true) {
                return false;
            }
        }
        try {
            // Element rootElement = doc.getDocumentElement();
            if (this.doc != null) {
                Node item_list = doc.getElementsByTagName(KEY_ITEM_LIST)
                        .item(0);
                // server elements
                Element new_item = doc.createElement("item");
                item_list.appendChild(new_item);
                Element eId = doc.createElement("id");
                eId.appendChild(doc.createTextNode(Integer.toString(next_id++)));
                new_item.appendChild(eId);
                Element eTitle = doc.createElement("title");
                eTitle.appendChild(doc.createTextNode(title));
                new_item.appendChild(eTitle);
                Element eResolution = doc.createElement("resolution");
                eResolution.appendChild(doc.createTextNode(resolution));
                new_item.appendChild(eResolution);
                Element eSource = doc.createElement("source");
                eSource.appendChild(doc.createTextNode(URI));
                new_item.appendChild(eSource);
                Node playlist_entries = doc.getElementsByTagName(KEY_ENTRIES)
                        .item(0);
                int numberOfItems = Integer.parseInt(this
                        .getElementValue(playlist_entries)) + 1;
                playlist_entries
                        .setTextContent(Integer.toString(numberOfItems));
                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory
                        .newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                File path = new File(playlist_folder_path, playlistName + ".ml");
                StreamResult result = new StreamResult(path);
                transformer.transform(source, result);
            }
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return true;
    }

    public boolean removeItemFromPlaylist(String playlistName, String URI) {
        // if(!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return false;
        }
        // }
        if (this.doc != null) {
            NodeList nl = doc.getElementsByTagName(KEY_ITEM);
            Node item_list = doc.getElementsByTagName(KEY_ITEM_LIST).item(0);
            // NodeList nl = item_list.getChildNodes();
            String fileURI;
            for (int i = 0; i < nl.getLength(); i++) {
                // creating new HashMap
                Element e = (Element) nl.item(i);
                fileURI = this.getValue(e, KEY_SOURCE);
                if (fileURI.equals(URI)) {
                    item_list.removeChild(nl.item(i));
                }
            }
            try {
                DOMSource source = new DOMSource(doc);
                TransformerFactory transformerFactory = TransformerFactory
                        .newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                File path = new File(playlist_folder_path, playlistName + ".ml");
                StreamResult result = new StreamResult(path);
                transformer.transform(source, result);
            } catch (TransformerException tfe) {
                tfe.printStackTrace();
            }
        }
        return true;
    }

    public boolean sortPlaylist(String playlistName, String criteria) {
        // if(!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return false;
        }
        // }
        if (criteria.equals("artist")) {
            Collections.sort(pl_items, new ArtistSorter());
        } else if (criteria.equals("title")) {
            Collections.sort(pl_items, new TitleSorter());
        } else if (criteria.equals("duration")) {
            Collections.sort(pl_items, new DurationSorter());
        }
        return true;
    }

    public void clearPlaylist(String playlistName) {
        pl_items = null;
        doc = null;
        File path = new File(playlist_folder_path, playlistName + ".ml");
        String playlistType = getPlaylistType(playlistName + ".ml");
        boolean deleted = path.delete();
        if (deleted) {
            this.createPlaylist(playlistName, playlistType);
        }
    }

    public boolean deletePlaylist(String playlistName) {
        pl_items = null;
        doc = null;
        playlist_name = "";
        File path = new File(playlist_folder_path, playlistName + ".ml");
        return path.delete();
    }

    public List<PlaylistFile> getPlaylists() {
        File dir = new File(playlist_folder_path);
        File[] files = dir.listFiles();
        String type;
        ArrayList<PlaylistFile> playlistInfo = new ArrayList<PlaylistFile>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    String tempName = files[i].getName();
                    if (tempName.endsWith(".ml")) {
                        type = getPlaylistType(tempName);
                        playlistInfo.add(new PlaylistFile(tempName, type));
                    }
                }
            }
        }
        return playlistInfo;
    }

    public int getNumberOfPlaylists() {
        File dir = new File(playlist_folder_path);
        File[] files = dir.listFiles();
        int numberOfPlaylists = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    String tempName = files[i].getName();
                    if (tempName.endsWith(".ml")) {
                        numberOfPlaylists++;
                    }
                }
            }
        }
        return numberOfPlaylists;
    }

    public ArrayList<MultimediaContent> getPlaylistItems(String playlistName) {
        // if (!playlist_name.equals(playlistName)) {
        if (openPlaylist(playlistName) == false) {
            return null;
        }
        // }
        return pl_items;
    }

    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    public ArrayList<MultimediaContent> getPl_items() {
        return pl_items;
    }

    public Document getDoc() {
        return doc;
    }
}
