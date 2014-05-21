package com.iwedia.gui.multimedia;

/**
 * Navigation object for multimedia browsing
 * 
 * @author Veljko Ilkic
 */
public class MultimediaNavigationObject {
    /** Position to focus */
    private int position;
    /** Folder name */
    private String folderName = "";
    /** Page to show */
    private int page;

    /** Constructor 1 */
    public MultimediaNavigationObject(String folderName, int position, int page) {
        this.folderName = folderName;
        this.position = position;
        this.page = page;
    }

    @Override
    public String toString() {
        return "MultimediaNavigationObject [position=" + position
                + ", folderName=" + folderName + ", page=" + page + "]";
    }

    // /////////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////////
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
