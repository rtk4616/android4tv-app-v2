package com.iwedia.gui.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetProviderInfo;
import android.view.View;

import com.iwedia.gui.MainActivity;

/**
 * Widget Arranging Handler Class
 * 
 * @author Veljko Ilkic
 */
public class WidgetArrangingHandler {
    /** Grid size */
    public static final int NUMBER_OF_CELL_PER_ROW = 5;
    public static final int NUMBER_OF_ROWS = 3;
    /** Drop spot height| */
    public static final int DROP_SPOT_HEIGHT = 50;
    /** Drop spot coordinates */
    private int[] widgetDropSpotCoordinates = new int[4];
    /** Screen width and height */
    private int screenWidth, screenHeight;
    /** Cell size */
    private int cellWidth, cellHeight;
    private WidgetCell[] widgetCells = new WidgetCell[NUMBER_OF_CELL_PER_ROW
            * NUMBER_OF_ROWS];

    /** Constructor 1 */
    public WidgetArrangingHandler(Activity activity) {
        super();
        // Take screen height and width
        if (((MainActivity) activity).isFullHD()) {
            this.screenHeight = 1080;
        } else {
            this.screenHeight = 720;
        }
        this.screenWidth = MainActivity.screenWidth;
        // Calculate cell size
        calculateCellSize();
        // Init cell grid
        initCellGrid();
        // Calculate drop spot size
        calculateDropSpotSize();
    }

    /** Calculate cell's size */
    private void calculateCellSize() {
        cellHeight = (screenHeight - DROP_SPOT_HEIGHT) / NUMBER_OF_ROWS;
        cellWidth = screenWidth / NUMBER_OF_CELL_PER_ROW;
    }

    /** Init cell grid */
    public void initCellGrid() {
        for (int i = 0; i < NUMBER_OF_CELL_PER_ROW * NUMBER_OF_ROWS; i++) {
            widgetCells[i] = new WidgetCell(0, 0, i, false);
        }
    }

    /** Calculate drop spot size */
    private void calculateDropSpotSize() {
        // X left
        widgetDropSpotCoordinates[0] = 0;
        // X right
        widgetDropSpotCoordinates[1] = screenWidth;
        // Y top
        widgetDropSpotCoordinates[2] = screenHeight - DROP_SPOT_HEIGHT;
        // Y bottom
        widgetDropSpotCoordinates[3] = screenHeight;
    }

    /** Calculate how many cells is needed for widget */
    public int[] howManyCellsTake(AppWidgetProviderInfo widget) {
        // Return value
        int[] retVal = new int[2];
        // Get real widget size
        int widgetWidth = widget.minWidth;
        int widgetHeight = widget.minHeight;
        // Calculate number horizontally taken cells
        int horizontalCellsTaken = widgetWidth / cellWidth;
        // Calculate number vertically taken cells
        int verticalCellsTaken = widgetHeight / cellHeight;
        // Check for part of cell taken
        if (horizontalCellsTaken > 0) {
            if (widgetWidth % cellWidth > 0) {
                horizontalCellsTaken++;
            }
        }
        // Check for part of cell taken
        if (verticalCellsTaken > 0) {
            if (widgetHeight % cellHeight > 0) {
                verticalCellsTaken++;
            }
        }
        // If widget is smaller that one cell, set one cell taken
        if (horizontalCellsTaken == 0) {
            horizontalCellsTaken = 1;
        }
        // If widget is smaller that one cell, set one cell taken
        if (verticalCellsTaken == 0) {
            verticalCellsTaken = 1;
        }
        retVal[0] = horizontalCellsTaken;
        retVal[1] = verticalCellsTaken;
        return retVal;
    }

    /** Set X and Y position of widget */
    public boolean setXY(View widgetView, int[] cellsNeeded,
            WidgetObject widgetObject) {
        // Get id of first cell
        int id = findEmptyCell(cellsNeeded);
        // Set id of first taken cell
        widgetObject.setCellId(id);
        // Check for errors
        if (id != -1) {
            int widgetX = id % NUMBER_OF_CELL_PER_ROW;
            int widgetY = id / NUMBER_OF_CELL_PER_ROW;
            widgetView.setX(widgetX * cellWidth);
            widgetView.setY(widgetY * cellHeight);
            // Tell widget in what cell it is
            ((WidgetViewTagObject) widgetView.getTag()).setCellId(id);
            // Tell widget how many cells it takes
            ((WidgetViewTagObject) widgetView.getTag())
                    .setCellsTaken(cellsNeeded);
            return true;
        } else {
            // No empty cell
            return false;
        }
    }

    /**
     * Set coordinates to widget from drag controller
     */
    public void setXYFromDrag(View widgetView, int cellId,
            WidgetObject widgetObject) {
        widgetObject.setCellId(cellId);
        int widgetX = cellId % NUMBER_OF_CELL_PER_ROW;
        int widgetY = cellId / NUMBER_OF_CELL_PER_ROW;
        widgetView.setX(widgetX * cellWidth);
        widgetView.setY(widgetY * cellHeight);
    }

    /** Set coordinates after init time */
    public void setXYAfterInit(View widgetView, WidgetObject widgetObject) {
        int cellId = widgetObject.getCellId();
        int widgetX = cellId % NUMBER_OF_CELL_PER_ROW;
        int widgetY = cellId / NUMBER_OF_CELL_PER_ROW;
        widgetView.setX(widgetX * cellWidth);
        widgetView.setY(widgetY * cellHeight);
    }

    /**
     * Find empty cell
     */
    private int findEmptyCell(int[] cellsNeeded) {
        // /////////////////////////////
        // 1x1
        // /////////////////////////////
        if (cellsNeeded[0] * cellsNeeded[1] == 1) {
            // Find first empty cell
            for (int i = 0; i < NUMBER_OF_CELL_PER_ROW; i++) {
                int x = i;
                for (int j = 0; j < NUMBER_OF_ROWS; j++) {
                    if (!widgetCells[x].isTaken()) {
                        widgetCells[x].setTaken(true);
                        return widgetCells[x].getId();
                    }
                    x = x + NUMBER_OF_CELL_PER_ROW;
                }
            }
        }
        // ////////////////////////////////////////
        // 2 cells
        // ////////////////////////////////////////
        if (cellsNeeded[0] * cellsNeeded[1] == 2) {
            // /////////////////////////////
            // 2x1
            // /////////////////////////////
            if (cellsNeeded[0] == 2) {
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW - 1; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + 1].isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + 1].setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
            // /////////////////////////////
            // 1x2
            // /////////////////////////////
            else {
                // Find first empty cell
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS - 1; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                        .isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                    .setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
        }
        // ////////////////////////////////////////
        // 3 cells
        // ////////////////////////////////////////
        if (cellsNeeded[0] * cellsNeeded[1] == 3) {
            // /////////////////////////////
            // 3x1
            // /////////////////////////////
            if (cellsNeeded[0] == 3) {
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW - 2; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + 1].isTaken()
                                && !widgetCells[x + 2].isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + 1].setTaken(true);
                            widgetCells[x + 2].setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
            // /////////////////////////////
            // 1x3
            // /////////////////////////////
            else {
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS - 2; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                        .isTaken()
                                && !widgetCells[x + 2 * NUMBER_OF_CELL_PER_ROW]
                                        .isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                    .setTaken(true);
                            widgetCells[x + 2 * NUMBER_OF_CELL_PER_ROW]
                                    .setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
        }
        // ////////////////////////////////////////
        // 4 cells
        // ////////////////////////////////////////
        if (cellsNeeded[0] * cellsNeeded[1] == 4) {
            // /////////////////////////////
            // 4x1
            // /////////////////////////////
            if (cellsNeeded[0] == 4) {
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW - 3; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + 1].isTaken()
                                && !widgetCells[x + 2].isTaken()
                                && !widgetCells[x + 3].isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + 1].setTaken(true);
                            widgetCells[x + 2].setTaken(true);
                            widgetCells[x + 3].setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
            // ////////////////////////////////
            // 2x2
            // ////////////////////////////////
            if (cellsNeeded[0] == 2) {
                // Find first empty cell
                for (int i = 0; i < NUMBER_OF_CELL_PER_ROW - 1; i++) {
                    int x = i;
                    for (int j = 0; j < NUMBER_OF_ROWS - 1; j++) {
                        if (!widgetCells[x].isTaken()
                                && !widgetCells[x + 1].isTaken()
                                && !widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                        .isTaken()
                                && !widgetCells[x + NUMBER_OF_CELL_PER_ROW + 1]
                                        .isTaken()) {
                            widgetCells[x].setTaken(true);
                            widgetCells[x + 1].setTaken(true);
                            widgetCells[x + NUMBER_OF_CELL_PER_ROW]
                                    .setTaken(true);
                            widgetCells[x + NUMBER_OF_CELL_PER_ROW + 1]
                                    .setTaken(true);
                            return widgetCells[x].getId();
                        }
                        x = x + NUMBER_OF_CELL_PER_ROW;
                    }
                }
            }
        }
        // No empty cells
        return -1;
    }

    /** Deinit take cells */
    public void deinitTakenCells(int id, int[] cellsTaken) {
        // ////////////////////////////////////
        // 1x1
        // ////////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 1) {
            widgetCells[id].setTaken(false);
        }
        // /////////////////////////////////
        // 2 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 2) {
            // //////////////////////////////////
            // 2x1
            // //////////////////////////////////
            if (cellsTaken[0] == 2) {
                widgetCells[id].setTaken(false);
                widgetCells[id + 1].setTaken(false);
            }
            // //////////////////////////////////
            // 1x2
            // //////////////////////////////////
            if (cellsTaken[0] == 1) {
                widgetCells[id].setTaken(false);
                widgetCells[id + NUMBER_OF_CELL_PER_ROW].setTaken(false);
            }
        }
        // /////////////////////////////////
        // 3 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 3) {
            // //////////////////////////////////
            // 3x1
            // //////////////////////////////////
            if (cellsTaken[0] == 3) {
                widgetCells[id].setTaken(false);
                widgetCells[id + 1].setTaken(false);
                widgetCells[id + 2].setTaken(false);
            }
            // //////////////////////////////////
            // 1x3
            // //////////////////////////////////
            if (cellsTaken[0] == 1) {
                widgetCells[id].setTaken(false);
                widgetCells[id + NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[id + 2 * NUMBER_OF_CELL_PER_ROW].setTaken(false);
            }
        }
        // /////////////////////////////////
        // 4 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 4) {
            // //////////////////////////////////
            // 4x1
            // //////////////////////////////////
            if (cellsTaken[0] == 4) {
                widgetCells[id].setTaken(false);
                widgetCells[id + 1].setTaken(false);
                widgetCells[id + 2].setTaken(false);
                widgetCells[id + 3].setTaken(false);
            }
            // //////////////////////////////////
            // 2x2
            // //////////////////////////////////
            if (cellsTaken[0] == 2) {
                widgetCells[id].setTaken(false);
                widgetCells[id + 1].setTaken(false);
                widgetCells[id + NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[id + NUMBER_OF_CELL_PER_ROW + 1].setTaken(false);
            }
        }
    }

    /** Check what cell should be taken */
    public int checkWhatCellWidgetWantsToTake(int x, int y) {
        int horizontalCell = x / cellWidth;
        int verticalCell = y / cellHeight;
        int cellId = horizontalCell + NUMBER_OF_CELL_PER_ROW * verticalCell;
        return cellId;
    }

    /** Check neighbour cells */
    public boolean checkNeighbourCells(int newCellId, View widget) {
        // Get tag
        WidgetViewTagObject tag = (WidgetViewTagObject) widget.getTag();
        /** Id of first cell that was taken by widget */
        int oldCellId = tag.getCellId();
        /** How many cells widget takes */
        int[] cellsTaken = tag.getCellsTaken();
        // Check horizontal
        if ((newCellId % NUMBER_OF_CELL_PER_ROW) + cellsTaken[0] > NUMBER_OF_CELL_PER_ROW) {
            return false;
        }
        // Check vertical
        if (newCellId + (cellsTaken[1] - 1) * NUMBER_OF_CELL_PER_ROW > NUMBER_OF_CELL_PER_ROW
                * NUMBER_OF_ROWS - 1) {
            return false;
        }
        // /////////////////////////////
        // 1x1
        // /////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 1) {
            if (widgetCells[newCellId].isTaken()) {
                return false;
            }
        }
        // ////////////////////////////////////////
        // 2 cells
        // ////////////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 2) {
            // /////////////////////////////
            // 2x1
            // /////////////////////////////
            if (cellsTaken[0] == 2) {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + 1].isTaken()) {
                    // ///////////////////////////////////////////////////////////////
                    // Check if new position has take same cells as old position
                    // ///////////////////////////////////////////////////////////////
                    if ((newCellId + 1 == oldCellId)
                            || (newCellId == oldCellId + 1)) {
                        // Deinit taken cell and take new cells
                        refreshWidgetCellsState(oldCellId, newCellId,
                                cellsTaken);
                        // Refresh cell id in tag
                        tag.setCellId(newCellId);
                        return true;
                    } else {
                        // /////////////////////////////////////
                        // Cell is taken by other widget
                        // /////////////////////////////////////
                        return false;
                    }
                }
            }
            // /////////////////////////////
            // 1x2
            // /////////////////////////////
            else {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + NUMBER_OF_CELL_PER_ROW]
                                .isTaken()) {
                    // ///////////////////////////////////////////////////////////////
                    // Check if new position has take same cells as old position
                    // ///////////////////////////////////////////////////////////////
                    if ((newCellId + NUMBER_OF_CELL_PER_ROW == oldCellId)
                            || (newCellId == oldCellId + NUMBER_OF_CELL_PER_ROW)) {
                        // Deinit taken cell and take new cells
                        refreshWidgetCellsState(oldCellId, newCellId,
                                cellsTaken);
                        // Refresh cell id in tag
                        tag.setCellId(newCellId);
                        return true;
                    } else {
                        // /////////////////////////////////////
                        // Cell is taken by other widget
                        // /////////////////////////////////////
                        return false;
                    }
                }
            }
        }
        // /////////////////////////////////
        // 3 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 3) {
            // //////////////////////////////////
            // 3x1
            // //////////////////////////////////
            if (cellsTaken[0] == 3) {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + 1].isTaken()
                        || widgetCells[newCellId + 2].isTaken()) {
                    // ///////////////////////////////////////////////////////////////
                    // Check if new position has take same cells as old position
                    // ///////////////////////////////////////////////////////////////
                    if ((newCellId + 1 == oldCellId)
                            || (newCellId + 2 == oldCellId)) {
                        // Deinit taken cell and take new cells
                        refreshWidgetCellsState(oldCellId, newCellId,
                                cellsTaken);
                        // Refresh cell id in tag
                        tag.setCellId(newCellId);
                        return true;
                    } else {
                        // /////////////////////////////////////
                        // Cell is taken by other widget
                        // /////////////////////////////////////
                        return false;
                    }
                }
            }
            // //////////////////////////////////
            // 1x3
            // //////////////////////////////////
            if (cellsTaken[0] == 1) {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + 1 * NUMBER_OF_CELL_PER_ROW]
                                .isTaken()
                        || widgetCells[newCellId + 2 * NUMBER_OF_CELL_PER_ROW]
                                .isTaken()) {
                    return false;
                }
            }
        }
        // /////////////////////////////////
        // 4 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 4) {
            // //////////////////////////////////
            // 4x1
            // //////////////////////////////////
            if (cellsTaken[0] == 4) {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + 1].isTaken()
                        || widgetCells[newCellId + 2].isTaken()
                        || widgetCells[newCellId + 3].isTaken()) {
                    // ///////////////////////////////////////////////////////////////
                    // Check if new position has take same cells as old position
                    // ///////////////////////////////////////////////////////////////
                    if ((newCellId + 1 == oldCellId)
                            || (newCellId + 2 == oldCellId)
                            || (newCellId + 3 == oldCellId)) {
                        // Deinit taken cell and take new cells
                        refreshWidgetCellsState(oldCellId, newCellId,
                                cellsTaken);
                        // Refresh cell id in tag
                        tag.setCellId(newCellId);
                        return true;
                    } else {
                        // /////////////////////////////////////
                        // Cell is taken by other widget
                        // /////////////////////////////////////
                        return false;
                    }
                }
            }
            // //////////////////////////////////
            // 2x2
            // //////////////////////////////////
            if (cellsTaken[0] == 2) {
                if (widgetCells[newCellId].isTaken()
                        || widgetCells[newCellId + 1].isTaken()
                        || widgetCells[newCellId + 1 * NUMBER_OF_CELL_PER_ROW]
                                .isTaken()
                        || widgetCells[newCellId + 2 * NUMBER_OF_CELL_PER_ROW]
                                .isTaken()) {
                    // ///////////////////////////////////////////////////////////////
                    // Check if new position has take same cells as old position
                    // ///////////////////////////////////////////////////////////////
                    if ((newCellId + 1 == oldCellId)
                            || (newCellId == oldCellId + 1)
                            || (newCellId + NUMBER_OF_CELL_PER_ROW == oldCellId)
                            || (newCellId == oldCellId + NUMBER_OF_CELL_PER_ROW)) {
                        // Deinit taken cell and take new cells
                        refreshWidgetCellsState(oldCellId, newCellId,
                                cellsTaken);
                        // Refresh cell id in tag
                        tag.setCellId(newCellId);
                        return true;
                    } else {
                        // /////////////////////////////////////
                        // Cell is taken by other widget
                        // /////////////////////////////////////
                        return false;
                    }
                }
            }
        }
        // Deinit taken cell and take new cells
        refreshWidgetCellsState(oldCellId, newCellId, cellsTaken);
        // Refresh cell id in tag
        tag.setCellId(newCellId);
        return true;
    }

    /** Refresh widget cell state after drag success */
    private void refreshWidgetCellsState(int oldId, int newId, int[] cellsTaken) {
        // /////////////////////////////
        // 1x1
        // /////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 1) {
            widgetCells[oldId].setTaken(false);
            widgetCells[newId].setTaken(true);
        }
        // ////////////////////////////////////////
        // 2 cells
        // ////////////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 2) {
            // /////////////////////////////
            // 2x1
            // /////////////////////////////
            if (cellsTaken[0] == 2) {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + 1].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + 1].setTaken(true);
            }
            // /////////////////////////////
            // 1x2
            // /////////////////////////////
            else {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + NUMBER_OF_CELL_PER_ROW].setTaken(true);
            }
        }
        // /////////////////////////////////
        // 3 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 3) {
            // //////////////////////////////////
            // 3x1
            // //////////////////////////////////
            if (cellsTaken[0] == 3) {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + 1].setTaken(false);
                widgetCells[oldId + 2].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + 1].setTaken(true);
                widgetCells[newId + 2].setTaken(true);
            }
            // //////////////////////////////////
            // 1x3
            // //////////////////////////////////
            if (cellsTaken[0] == 1) {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + 1 * NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[oldId + 2 * NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + 1 * NUMBER_OF_CELL_PER_ROW].setTaken(true);
                widgetCells[newId + 2 * NUMBER_OF_CELL_PER_ROW].setTaken(true);
            }
        }
        // /////////////////////////////////
        // 4 Cells
        // /////////////////////////////////
        if (cellsTaken[0] * cellsTaken[1] == 4) {
            // //////////////////////////////////
            // 4x1
            // //////////////////////////////////
            if (cellsTaken[0] == 4) {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + 1].setTaken(false);
                widgetCells[oldId + 2].setTaken(false);
                widgetCells[oldId + 3].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + 1].setTaken(true);
                widgetCells[newId + 2].setTaken(true);
                widgetCells[newId + 3].setTaken(true);
            }
            // //////////////////////////////////
            // 2x2
            // //////////////////////////////////
            if (cellsTaken[0] == 2) {
                widgetCells[oldId].setTaken(false);
                widgetCells[oldId + 1].setTaken(false);
                widgetCells[oldId + 1 * NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[oldId + 2 * NUMBER_OF_CELL_PER_ROW].setTaken(false);
                widgetCells[newId].setTaken(true);
                widgetCells[newId + 1].setTaken(true);
                widgetCells[newId + 1 * NUMBER_OF_CELL_PER_ROW].setTaken(true);
                widgetCells[newId + 2 * NUMBER_OF_CELL_PER_ROW].setTaken(true);
            }
        }
    }

    /** Check widget remove */
    public boolean checkDeleteZone(int dropX, int dropY) {
        // /////////////////////////
        // Inside drop zone
        // /////////////////////////
        if (dropY > widgetDropSpotCoordinates[2]) {
            return true;
        }
        // //////////////////////////
        // Outside drop zone
        // //////////////////////////
        else {
            return false;
        }
    }

    // ///////////////////////////////////////
    // Getters and Setters
    // ///////////////////////////////////////
    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public WidgetCell[] getWidgetCells() {
        return widgetCells;
    }

    public void setWidgetCells(WidgetCell[] widgetCells) {
        this.widgetCells = widgetCells;
    }
}
