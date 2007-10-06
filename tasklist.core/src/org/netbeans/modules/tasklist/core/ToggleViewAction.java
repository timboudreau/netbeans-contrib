/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
