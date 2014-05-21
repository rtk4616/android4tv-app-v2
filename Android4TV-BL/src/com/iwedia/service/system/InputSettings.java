package com.iwedia.service.system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.ISourceDeviceCallback;
import com.iwedia.comm.devices.SourceDevice;
import com.iwedia.comm.system.IInputSettings;

public class InputSettings extends IInputSettings.Stub {
    public static final boolean DEBUG = true;
    public static final String LOG_TAG = "InputSettings";
    private static Object lock = new Object();
    final static RemoteCallbackList<ISourceDeviceCallback> mSourceDeviceCallback = new RemoteCallbackList<ISourceDeviceCallback>();
    // XML Parsing Data
    private static final String INPUT_FILE = "/system/etc/input_config.xml";
    private static final String INPUT = "inputs";
    private static final String INPUT_TAG = "inputs";
    private static final String INPUT_TYPE_TAG = "type";
    private static final String INPUT_PORT_TAG = "port";
    private static final String INPUT_NAME_TAG = "name";
    // public static final String INPUT_SCHEME_TAG = "scheme";
    // SharedPreference
    private static final String LAST_INPUT_DEVICE_URI = "last_input_device_uri";
    private List<SourceDevice> deviceList = new ArrayList<SourceDevice>();

    public InputSettings() {
    }

    public static void callSwitchSourceDevice(SourceDevice device) {
        synchronized (lock) {
            int i = mSourceDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mSourceDeviceCallback.getBroadcastItem(i)
                            .switchSourceDevice(device);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mSourceDeviceCallback.unregister(mSourceDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mSourceDeviceCallback.finishBroadcast();
        }
    }

    public void scanAvailableDevices() throws RemoteException {
        if (deviceList.isEmpty()) {
            parseInputs();
        }
    }

    private List<SourceDevice> parseInputs() {
        SourceDevice device;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(INPUT_FILE));
            NodeList nodelist = doc.getElementsByTagName(INPUT_TAG);
            int size = nodelist.getLength();
            for (int i = 0; i < size; i++) {
                for (Node child = nodelist.item(i).getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child instanceof Element) {
                        Element element = (Element) child;
                        // if ("true".equals(child.getTextContent())) {
                        device = new SourceDevice(
                                element.getAttribute(INPUT_TYPE_TAG),
                                Integer.parseInt(element
                                        .getAttribute(INPUT_PORT_TAG)),
                                element.getAttribute(INPUT_NAME_TAG));
                        deviceList.add(device);
                        // }
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deviceList;
    }
}
