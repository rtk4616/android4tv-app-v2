package com.iwedia.gui.multimedia;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Global Fields For Multimedia
 * 
 * @author Milos Milanovic
 */
public interface MultimediaGlobal {
    /** Supported Extensions and Mimes */
    /** Video */
    public static final ArrayList<String> EXTENSIONS_VIDEO = new ArrayList<String>(
            Arrays.asList(
                    // Video
                    "3gp", "mp4", "avi", "mpg", "mpeg", "x-ms-wmv", "ts",
                    "tdx", "wmv", "mkv", "asf", "flv", "mkv", "ogm", "rm",
                    "mov", "video/3gp", "video/mp4", "video/avi", "video/mpeg",
                    "video/x-msvideo", "video/x-ms-wmv", "video/x-matroska",
                    "m4v", "mov", "divx"));
    /** Audio */
    public static final ArrayList<String> EXTENSIONS_AUDIO = new ArrayList<String>(
            Arrays.asList(
                    // Audio
                    "aiff", "aif", "wav", "mid", "mp3", "wma", "ogg", "mka",
                    "dts", "dtshd", "flac", "audio/wav", "audio/mid",
                    "audio/flac", "audio/mp3", "audio/mpeg", "aac", "m4a",
                    "m4b"));
    /** Image */
    public static final ArrayList<String> EXTENSIONS_IMAGE = new ArrayList<String>(
            Arrays.asList(
                    // Image
                    "jpg", "png", "jpeg", "bmp", "gif", "image/jpeg",
                    "image/png", "image/gif", "image/bmp"));
    /** Renderer */
    /** Renderer State */
    public static final int RENDERER_STATE_STOP = 0;
    public static final int RENDERER_STATE_PLAY = 1;
    public static final int RENDERER_STATE_PLAY_PIP = 2;
    public static final int RENDERER_STATE_PLAY_PAP = 3;
    /** Handle Renderer */
    public static final int RENDERER_STOP = 0;
    public static final int RENDERER_AUDIO = 1;
    public static final int RENDERER_IMAGE = 2;
    public static final int RENDERER_VIDEO = 3;
    /** Repeat States */
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
}
