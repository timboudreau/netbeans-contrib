/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.awt.Color;
import java.awt.Image;
import org.netbeans.api.vcs.FileStatusInfo;

/**
 * The implementation of FileStatusInfo for command-line VCS filesystem.
 *
 * @author  Martin Entlicher
 */
public class CommandLineFileStatusInfo extends FileStatusInfo implements javax.swing.colorchooser.ColorSelectionModel {
    
    private String displayName;
    private String shortDisplayName;
    private Image icon;
    private FileStatusInfo repInfo;
    private boolean isShortDisplayed = false;
    private Color color;
    
    /**
     * Creates a new instance of CommandLineFileStatusInfo.
     * @param name The name of this status.
     * @param displayName The display name of this status.
     * @param shortDisplayName The short variant of the display name of this status.
     *                         It can be <code>null</code> when no short variant
     *                         exists.
     */
    public CommandLineFileStatusInfo(String name, String displayName,
                                     String shortDisplayName) {
        super(name);
        this.displayName = displayName;
        if (shortDisplayName == null) {
            this.shortDisplayName = displayName;
        } else {
            this.shortDisplayName = shortDisplayName;
        }
    }
    
    /**
     * Get the localized string representation of this status info. Used for
     * displaying purposes.
     * @return The localized status representation.
     */
    public String getDisplayName() {
        if (isShortDisplayed) {
            return shortDisplayName;
        } else {
            return displayName;
        }
    }
    
    /**
     * Get the icon for this status info.
     * @return The icon representing this status info or
     *         <code>null</code> when there is no icon.
     */
    public Image getIcon() {
        return icon;
    }
    
    /**
     * Set the icon for this status info.
     * @param icon The icon representing this status info or
     *             <code>null</code> when there is no icon.
     */
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    /**
     * Set whether the display name of this status should be the short variant
     * or not.
     * @param isShortDisplayed <code>true</code> for the short variant of display
     *                         name, <code>false</code> for the standard display
     *                         name.
     */
    public void setShortDisplayed(boolean isShortDisplayed) {
        this.isShortDisplayed = isShortDisplayed;
    }
    
    /**
     * Set the status info, that represents this status info.
     * @param repInfo The representing file status info. Can be <code>null</code>
     *                if no other status info is represented.
     */
    public void setRepresentingStatus(FileStatusInfo repInfo) {
        this.repInfo = repInfo;
    }
    
    /**
     * Tell, whether this file status information represents just the same kind
     * of status as another one. This method can be used by version control systems,
     * that have more status information types, then the pre-defined constants.
     * Use this to find out e.g. whether this status info represents one of the
     * pre-defined status info.
     * @return Whether this file status information represents just the same kind
     * of status as another one.
     */
    public boolean represents(FileStatusInfo info) {
        return equals(info) || (repInfo != null && repInfo.equals(info));
    }
    
    public Color getSelectedColor() {
        return color;
    }
    
    public void setSelectedColor(Color color) {
        this.color = color;
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener listener) {
        // Not used currently
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener listener) {
        // Not used currently
    }
    
}
