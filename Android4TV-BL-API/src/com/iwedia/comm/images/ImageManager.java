package com.iwedia.comm.images;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

public class ImageManager {

    private ArrayList<String[]> pathsMap;
    private String imageUrl;
    private String noImageUrl;

    private static ImageManager instance = null;

    private ImageManager() {
        imageUrl = "";
        noImageUrl = "";
        pathsMap = new ArrayList<String[]>();
        // noImageUrl = "channel_conf/channel_icons/no_image.png";
        noImageUrl = "-1";
        pathsMap = fillPathsMap();

    }

    private ArrayList<String[]> fillPathsMap() {
        File file = new File("channel_conf/channel_icons_paths/paths.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] separatedStrings;
            separatedStrings = new String[2];
            Log.d("ImageManager", "String: " + separatedStrings);

            try {
                while((line = br.readLine()) != null) {

                    separatedStrings = new String[2];
                    Log.d("ImageManager", "String: " + separatedStrings);
                    separatedStrings = line.split(":");

                    pathsMap.add(separatedStrings);
                }
            } finally {
                br.close();

            }

        } catch(IOException e) {
            Log.e("EXCEPTION", e.getMessage());
        }

        return pathsMap;

    }

    public static ImageManager getInstance() {
        if(instance == null) {
            instance = new ImageManager();
        }

        return instance;
    }

    public String getImageUrl(String serviceName) {
        imageUrl = noImageUrl;
        for(int i = 0; i < pathsMap.size(); i++) {
            int serviceLength = (pathsMap.get(i)[0]).length();
            if(serviceName.length() >= serviceLength) {
                if(serviceName.substring(0, serviceLength).equalsIgnoreCase(
                       pathsMap.get(i)[0])) {
                    imageUrl = pathsMap.get(i)[1];
                    break;
                }
            }
        }

        return imageUrl;
    }
}
