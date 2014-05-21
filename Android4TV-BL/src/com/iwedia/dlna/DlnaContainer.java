package com.iwedia.dlna;

/**
 * DLNA container class. It is DLNA representation of the folder.
 * 
 * @author maksovic
 */
public class DlnaContainer extends DlnaObject {
    /**
     * Parent folder.
     */
    private DlnaContainer parent;
    /**
     * Number of DLNA object in this container.
     */
    private int childCount;
    /**
     * Native resources handle.
     */
    private long nativeHandle;

    /**
     * Default Constructor.
     */
    public DlnaContainer() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param id
     *        Container ID.
     * @param friendlyName
     *        Container friendly name.
     */
    public DlnaContainer(String id, String friendlyName, String parentID) {
        super(id, friendlyName, parentID);
    }

    /**
     * Sets container parent.
     * 
     * @return Parent directory. If <code>null</code>, then this is server root
     *         directory.
     */
    public DlnaContainer getParent() {
        return parent;
    }

    /**
     * Sets parent directory. For internal usage only.
     * 
     * @param parent
     *        Parent container.
     */
    void setParent(DlnaContainer parent) {
        this.parent = parent;
    }

    /**
     * Returns number of items in this folder.
     * 
     * @return Child count.
     */
    public int getChildCount() {
        return childCount;
    }

    /**
     * Sets child count.
     * 
     * @param childCount
     *        Number to set.
     */
    void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    /**
     * Sets native directory handle.
     * 
     * @param nativeHandle
     *        native handle.
     */
    void setNativeHandle(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    /**
     * Gets native directory handle.
     * 
     * @return native handle.
     */
    long getNativeHandle() {
        return nativeHandle;
    }
}
