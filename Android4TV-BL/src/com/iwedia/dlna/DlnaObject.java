package com.iwedia.dlna;

/**
 * General DLNA object.
 * 
 * @author maksovic
 */
public class DlnaObject {
    /**
     * Object ID.
     */
    protected String id;
    /**
     * Object parentID.
     */
    protected String parentID;
    /**
     * Object friendly name.
     */
    protected String friendlyName;

    /**
     * Constructor.
     * 
     * @param id
     *        Container ID.
     * @param friendlyName
     *        Container friendly name.
     */
    public DlnaObject(String id, String friendlyName, String parentID) {
        if (id == null || friendlyName == null || parentID == null) {
            throw new IllegalArgumentException();
        }
        setID(id);
        setFriendlyName(friendlyName);
        setParentID(parentID);
    }

    public DlnaObject() {
        this.id = null;
        this.parentID = null;
        this.friendlyName = null;
        // TODO Auto-generated constructor stub
    }

    /**
     * Sets object ID.
     * 
     * @param id
     *        new id.
     */
    protected void setID(String id) {
        this.id = id;
    }

    /**
     * Sets object ID.
     * 
     * @param id
     *        new parentID.
     */
    protected void setParentID(String parentID) {
        this.parentID = parentID;
    }

    /**
     * Sets object friendly name.
     * 
     * @param id
     *        new friendly name.
     */
    protected void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Returns object ID.
     * 
     * @return Object ID.
     */
    public String getID() {
        return id;
    }

    /**
     * Returns object ID.
     * 
     * @return Object parentID.
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Returns object friendly name.
     * 
     * @return Object friendly name.
     */
    public String getFriendlyName() {
        return friendlyName;
    }
}
