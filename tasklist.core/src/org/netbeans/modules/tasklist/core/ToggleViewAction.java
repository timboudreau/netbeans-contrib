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
package org.netbeans.modules.tasklist.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.swing.SwingUtilities;
import org.netbeans.modules.tasklist.core.*;
import org.openide.awt.Actions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/** 
 * Shows/closes a view.
 *
 * @author Tim Lebedkov
 */
public abstract class ToggleViewAction extends BooleanStateAction implements
PropertyChangeListener {
    private boolean block;
    private String mode = "output"; // NOI18N
    private WeakReference activated;
    
    public void setBooleanState(final boolean value) {
        super.setBooleanState(value);
        if (block)
            return;
        block = true;
        // XXX is it realy called from not AWT thread?
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toggleView(value);
                block = false;
            }
        });
    }

    /**
     * Should return the view to be shown
     *
     * @return the view
     */
    protected abstract TopComponent getView();

    /**
     * Returns true if the view is visible (on the current workspace).
     * This method could be overriden to don't create the view if it is not
     * visible.
     *
     * @return true = the view is opened on the current workspace
     */
    protected boolean isViewOpened() {
        return getView().isOpened(WindowManager.getDefault().
            getCurrentWorkspace());
    }
    
    /**
     * Closes/opens the view
     *
     * @param visible true = open the view
     */
    private void toggleView(boolean visible) {
        if (visible == isViewOpened())
            return;

        TopComponent view = getView();
        Workspace workspace = WindowManager.getDefault().
            getCurrentWorkspace();
        if (!visible) {
            Mode mode = workspace.findMode(view);
            if (mode != null)
                this.mode = mode.getName();
            view.close();
            if (activated != null) {
                TopComponent act = (TopComponent) activated.get();
                if (act != null)
                    act.requestFocus();
            }
        } else if (visible) {
            TopComponent act = WindowManager.getDefault().
                getRegistry().getActivated();
            if (act == null)
                activated = null;
            else
                activated = new WeakReference(act);
            Mode mode  = workspace.findMode(this.mode);
            if (mode != null) {
                mode.dockInto(view);
            }
            view.open(workspace);
            view.requestVisible();
            view.requestFocus(); 
        }
    }

    protected void initialize() {
        super.initialize();
        WindowManager.getDefault().addPropertyChangeListener(this);
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(this);
        putProperty(PROP_BOOLEAN_STATE, Boolean.valueOf(isViewOpened()));
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void propertyChange(PropertyChangeEvent e) {
        String p = e.getPropertyName();
        if (p.equals(TopComponent.Registry.PROP_OPENED) ||
            p.equals(WindowManager.PROP_CURRENT_WORKSPACE)) {
            super.setBooleanState(isViewOpened());
        }
    }
}
