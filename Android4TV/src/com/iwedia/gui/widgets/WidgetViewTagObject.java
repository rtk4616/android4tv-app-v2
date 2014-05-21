package com.iwedia.gui.widgets;

/** Tag object for widget views */
public class WidgetViewTagObject {
    /** Id of real widget */
    int widgetId;
    /** Id of first taken cell */
    int cellId;
    /** Amount of taken cells */
    int[] cellsTaken = new int[2];

    /** Constructor 1 */
    public WidgetViewTagObject(int widgetId, int cellId, int[] cellsTaken) {
        super();
        this.widgetId = widgetId;
        this.cellId = cellId;
        this.cellsTaken = cellsTaken;
    }

    // /////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////
    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int[] getCellsTaken() {
        return cellsTaken;
    }

    public void setCellsTaken(int[] cellsTaken) {
        this.cellsTaken = cellsTaken;
    }
}
