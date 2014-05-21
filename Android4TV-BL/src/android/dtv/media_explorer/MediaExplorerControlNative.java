package android.dtv.media_explorer;

public class MediaExplorerControlNative {
    /** Initialization by calling native methods. */
    public native int initNative();

    /** Deinitialization by calling native methods. */
    public native int deinitNative();

    /** Registering file handler by calling native methods. */
    public native int registerEntry(int fileHandlingType);

    /** Unregistering file handler by calling native methods. */
    public native int unregisterEntry(int fileHandlingType);

    /** Set current directory by calling native methods. */
    public native String setCurDir(String newCurrentDir);

    /** Get current directory by calling native methods. */
    public native String getCurDir();

    /**
     * Functions that implement getting resources from current directory are
     * getFirst and getNext,getFirst should be called first, and then getNext
     * while it returns empty string
     */
    public native String getFirst(String URI);

    /**
     * Functions that implement getting resources from current directory are
     * getFirst and getNext,getFirst should be called first, and then getNext
     * while it returns empty string
     */
    public native String getNext(String URI);

    /**
     * After getting URI to resource it is necessary to have more information
     * about it in order to invoke access to file data or to get some metadata
     * for resource. These extended information are available through
     * getFileProperty function:
     * 
     * @return string property or null on error
     */
    public native String getFileProperty(String URI, int ID);

    static {
        System.loadLibrary("media_explorer");
    }
}
