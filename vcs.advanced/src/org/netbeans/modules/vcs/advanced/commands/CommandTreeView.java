/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.commands;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openide.explorer.view.*;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandTreeView extends BeanTreeView {

    private static final String DEFAULT_FOLDER = "/org/openide/resources/defaultFolder.gif";
    private static final String DEFAULT_OPEN_FOLDER = "/org/openide/resources/defaultFolderOpen.gif";

    private static final long serialVersionUID = -8586652914620083109L;
    
    /** Creates new CommandTreeView */
    public CommandTreeView() {
        DefaultTreeCellRenderer commandRenderer = new DefaultTreeCellRenderer();
        commandRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource(DEFAULT_FOLDER)));
        commandRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource(DEFAULT_OPEN_FOLDER)));
        tree.setCellRenderer(commandRenderer);
    }

}
