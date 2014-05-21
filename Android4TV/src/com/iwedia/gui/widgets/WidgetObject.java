package com.iwedia.gui.widgets;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Widget object
 * 
 * @author Veljko Ilkic
 */
public class WidgetObject implements Serializable {
    private static final long serialVersionUID = -657193571248076416L;
    /** Widget package name */
    private String packageName;
    /** Widget class name */
    private String className;
    /** How many cells does widget took */
    private int[] howManyCellsTake;
    /** Id of first taken cell */
    private int cellId;

    /** Constructor 1 */
    public WidgetObject(String packageName, String className,
            int[] howManyCellsTake, int cellId) {
        super();
        this.packageName = packageName;
        this.className = className;
        this.howManyCellsTake = howManyCellsTake;
        this.cellId = cellId;
    }

    @Override
    public String toString() {
        return "WidgetObject [packageName=" + packageName + ", className="
                + className + ", howManyCellsTake="
                + Arrays.toString(howManyCellsTake) + ", cellId=" + cellId
                + "]";
    }

    // //////////////////////////////////
    // Getters and setters
    // //////////////////////////////////
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int[] getHowManyCellsTake() {
        return howManyCellsTake;
    }

    public void setHowManyCellsTake(int[] howManyCellsTake) {
        this.howManyCellsTake = howManyCellsTake;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }
}
