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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
import org.openide.util.actions.BooleanStateAction;
import org.openide.util.actions.Presenter;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Shows/closes a view.
 */
public abstract class ToggleViewAction extends BooleanStateAction implements
PropertyChangeListener, Presenter.Menu {

    private static final long serialVersionUID = 1;

    private boolean block;
    private String mode = "output"; // NOI18N
    private WeakReference activated;
    
    public javax.swing.JMenuItem getMenuPresenter() {
        return new Actions.MenuItem(this, true);
    }

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
        return getView().isOpened();
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
        WindowManager wm = WindowManager.getDefault();
        if (!visible) {
            Mode mode = wm.findMode(view);
            if (mode != null)
                this.mode = mode.getName();
            view.close();
            if (activated != null) {
                TopComponent act = (TopComponent) activated.get();
                if (act != null)
                    act.requestActive();
            }
        } else {
            TopComponent act = WindowManager.getDefault().
                getRegistry().getActivated();
            if (act == null)
                activated = null;
            else
                activated = new WeakReference(act);
            Mode mode  = wm.findMode(this.mode);
            if (mode != null) {
                mode.dockInto(view);
            }
            view.open();
            view.requestVisible();
            view.requestActive(); 
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
        if (p.equals(TopComponent.Registry.PROP_OPENED)) {
            super.setBooleanState(isViewOpened());
        }
    }
}
