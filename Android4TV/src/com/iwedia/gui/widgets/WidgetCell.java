package com.iwedia.gui.widgets;

import java.io.Serializable;

/**
 * Widget cell object
 * 
 * @author Veljko Ilkic
 */
public class WidgetCell implements Serializable {
    private static final long serialVersionUID = 1L;
    /** X and Y of top corner of cell */
    private int xTopCorner, yTopCorner;
    /** Cell id */
    private int id;
    /** Taken flag */
    private boolean taken;

    /** Constructor 1 */
    public WidgetCell(int xTop, int yTop, int id, boolean taken) {
        super();
        this.xTopCorner = xTop;
        this.yTopCorner = yTop;
        this.id = id;
        this.taken = taken;
    }

    @Override
    public String toString() {
        return "WidgetCell [xTopCorner=" + xTopCorner + ", yTopCorner="
                + yTopCorner + ", id=" + id + ", taken=" + taken + "]";
    }

    // ///////////////////////////////////////////
    // Getters and setters
    // ///////////////////////////////////////////
    public int getxTopCorner() {
        return xTopCorner;
    }

    public void setxTopCorner(int xTopCorner) {
        this.xTopCorner = xTopCorner;
    }

    public int getyTopCorner() {
        return yTopCorner;
    }

    public void setyTopCorner(int yTopCorner) {
        this.yTopCorner = yTopCorner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
