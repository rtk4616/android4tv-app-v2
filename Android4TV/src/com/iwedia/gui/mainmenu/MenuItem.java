package com.iwedia.gui.mainmenu;

import com.iwedia.gui.MainActivity;

/**
 * Class that holds string values for icon name and description and the icon
 * itself
 * 
 * @author Branimir Pavlovic
 */
public class MenuItem {
    private String menuItemName, menuItemDescription;
    private int menuImage;
    private boolean enabled;

    public MenuItem(String menuItemName, String menuItemDescription, int image) {
        this.menuItemName = menuItemName;
        this.menuItemDescription = menuItemDescription;
        this.menuImage = image;
    }

    public MenuItem(MainActivity activity, String menuItemName,
            String menuItemDescription, int image) {
        this.menuItemName = menuItemName;
        this.menuItemDescription = menuItemDescription;
        this.menuImage = image;
    }

    // ///////////////////////////////
    // Getters and setters
    // ///////////////////////////////
    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemDescription() {
        return menuItemDescription;
    }

    public void setMenuItemDescription(String menuItemDescription) {
        this.menuItemDescription = menuItemDescription;
    }

    public int getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(int menuImage) {
        this.menuImage = menuImage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
