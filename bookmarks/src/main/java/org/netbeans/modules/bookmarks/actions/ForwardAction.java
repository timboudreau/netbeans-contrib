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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.bookmarks.actions;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.*;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;
import org.netbeans.api.bookmarks.*;

/**
 * Forward action is used to present the NavigationService in the toolbar.
 * It is enabled according to the state of the NavigationService, namely
 * when canNavigateForward is enabled. It registers a listener on
 * the NavigationService.
 * @author David Strupl
 */
public class ForwardAction extends AbstractAction implements ChangeListener, HelpCtx.Provider {
    
    /**
     * Keep a reference to the NavigationService singleton.
     */
    private static NavigationService navigationService = NavigationService.getDefault();
    
    /** Default construcotr. Attaches the listener.
     */
    public ForwardAction() {
        putValue(Action.NAME, getName());
        putValue("iconBase", "org/netbeans/modules/bookmarks/resources/forward.gif");
        navigationService.addChangeListener(
            WeakListeners.change(this, navigationService));
        setEnabled(navigationService.canNavigateForward());
    }
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(ForwardAction.class).getString("Forward");
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
    public void stateChanged(ChangeEvent evt) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setEnabled(navigationService.canNavigateForward());
            }
        });
    }
}
