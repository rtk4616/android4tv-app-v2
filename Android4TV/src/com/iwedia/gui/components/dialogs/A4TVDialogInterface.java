package com.iwedia.gui.components.dialogs;

import java.util.ArrayList;

/**
 * Interface for functions that every dialog must have
 * 
 * @author Branimir Pavlovic
 */
public interface A4TVDialogInterface {
    /**
     * Function that load contents for dialogs, only used for settings dialogs
     * 
     * @param contentList
     *        List that holds TAG's of widgets to be added to dialog
     * @param contentListIDs
     *        List that holds ID's and strings for widgets
     * @param titleTextIDs
     *        List that holds ID's of icons for dialog title and one final
     *        string
     */
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs);

    /**
     * Function that puts content to dialog
     */
    public void fillDialog();

    /**
     * Function that set dialog attributes
     */
    public void setDialogAttributes();
}
