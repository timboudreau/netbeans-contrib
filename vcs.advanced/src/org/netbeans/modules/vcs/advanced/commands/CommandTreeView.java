/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.commands;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openide.explorer.view.*;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandTreeView extends BeanTreeView {

    private static final String DEFAULT_FOLDER = "/org/openide/loaders/defaultFolder.gif";
    private static final String DEFAULT_OPEN_FOLDER = "/org/openide/loaders/defaultFolderOpen.gif";

    private static final Image FOLDER_ICON = (Image) UIManager.get("Nb.Explorer.Folder.icon"); // NOI18N
    private static final Image OPEN_FOLDER_ICON = (Image) UIManager.get("Nb.Explorer.Folder.openedIcon"); // NOI18N

    private static final long serialVersionUID = -8586652914620083109L;
    
    /** Creates new CommandTreeView */
    public CommandTreeView() {
        DefaultTreeCellRenderer commandRenderer = new DefaultTreeCellRenderer();
        if (FOLDER_ICON != null) {
            commandRenderer.setClosedIcon(new ImageIcon(FOLDER_ICON));
        } else {
            commandRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource(DEFAULT_FOLDER)));
        }
        if (OPEN_FOLDER_ICON != null) {
            commandRenderer.setOpenIcon(new ImageIcon(OPEN_FOLDER_ICON));
        } else {
            commandRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource(DEFAULT_OPEN_FOLDER)));
        }
        tree.setCellRenderer(commandRenderer);
    }

}
