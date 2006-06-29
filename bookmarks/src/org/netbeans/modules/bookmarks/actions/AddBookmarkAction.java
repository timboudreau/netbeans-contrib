/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openide.util.*;
import org.openide.windows.*;

import org.netbeans.api.bookmarks.*;
import org.netbeans.modules.bookmarks.*;

/**
 * An action for the main menu bar. It finds the activated
 * top component, creates a deafult bookmark and stores it
 * with the BookmarkService.
 * @author David Strupl
 */
public class AddBookmarkAction extends AbstractAction implements HelpCtx.Provider, PropertyChangeListener {
    
    /**
     * Default constructor.
     */
    public AddBookmarkAction() {
        putValue(Action.NAME, getName());
        String base = "org/netbeans/modules/bookmarks/resources/add.gif";
        putValue("iconBase", base);
        TopComponent.Registry reg = WindowManager.getDefault().getRegistry();
        reg.addPropertyChangeListener(
            WeakListeners.propertyChange(this, reg));
        TopComponent tc = reg.getActivated();
        setEnabled(tc != null);
    }
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(AddBookmarkAction.class).getString("AddBookmark");
    }
    
    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(AddBookmarkAction.class);
    }    
    
    /**
     * Main method for the action. Stores the created
     * bookmark.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        WindowManager wm = WindowManager.getDefault();
        TopComponent tc = wm.getRegistry().getActivated();
        if (tc == null) {
            return;
        }
        BookmarkService bs = BookmarkService.getDefault();
        bs.storeBookmark(bs.createDefaultBookmark(tc));
    }
    
    /**
     * We are registered with TopComponent.Registry as propertyChange
     * listener. When the activated top component is changed we
     * recompute the state of our navigation controls.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
            setEnabled(tc != null);
        }
    }

}
