/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks.actions;

import java.beans.*;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListener;
import org.openide.util.actions.Presenter;
import org.netbeans.api.bookmarks.*;

/**
 * Forward action is used to present the NavigationService in the toolbar.
 * It is enabled according to the state of the NavigationService, namely
 * when canNavigateForward is enabled. It registers a listener on
 * the NavigationService.
 * @author David Strupl
 */
public class ForwardAction extends AbstractAction implements PropertyChangeListener, HelpCtx.Provider {
    
    /**
     * Keep a reference to the NavigationService singleton.
     */
    private static NavigationService navigationService = NavigationService.getDefault();
    
    /** Default construcotr. Attaches the listener.
     */
    public ForwardAction() {
        putValue(Action.NAME, getName());
        putValue(Action.SMALL_ICON, getIcon());
        navigationService.addPropertyChangeListener(
            WeakListener.propertyChange(this, navigationService));
        setEnabled(navigationService.canNavigateForward());
    }
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(ForwardAction.class).getString("Forward");
    }
    
    /**
     * @returns icon for the action
     */
    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("org/netbeans/modules/bookmarks/resources/forward.gif"));
    }
    
    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ForwardAction.class);
    }    
    
    /**
     * Invokes the NavigationService.forward in the AWT event
     * queue thread.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                navigationService.forward();
            }
        });
    }
    
    /**
     * This class as PropertyChangeListener is attached
     * to the NavigationService. If the NavigationService is
     * changed we enable/disable this action.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setEnabled(navigationService.canNavigateForward());
            }
        });
    }
}
