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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.tool.actions;

import org.netbeans.modules.tool.ExitDialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.cookies.SaveCookie;
import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 * Save all ation implementation that walks through the
 * open top components looking for SaveCookie.
 *
 * @author David Strupl
 */
public class SaveAllAction extends CallableSystemAction {
    
    /** Keep strong reference to it. */
    private PropertyChangeListener pcl;
    
    public void performAction() {
        Set s = ExitDialog.getModifiedTCSet();
        if (s.isEmpty()) {
            return;
        }
        for (Iterator it = s.iterator(); it.hasNext(); ) {
            TopComponent tc = (TopComponent)it.next();
            save(tc);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(SaveAllAction.class, "LBL_SaveAll");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tool/actions/saveAll.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx (org.netbeans.modules.tool.actions.SaveAllAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected void initialize () {
        super.initialize ();
        // false by default
        putProperty (PROP_ENABLED, Boolean.FALSE);
        
        pcl = new PChangeListener();
        // listen to the changes
        TopComponent.getRegistry().addPropertyChangeListener(
            (PropertyChangeListener)WeakListeners.propertyChange(pcl, TopComponent.getRegistry()));
    }
    
    /** Tries to save given top component using its save cookie.
     * Notifies user if excetions appear.
     */
    private void save(TopComponent toc) {
        try {
            Node []activatedNodes = toc.getActivatedNodes();
            if (activatedNodes != null) {
                for (int i=0;i<activatedNodes.length;i++) {
                    SaveCookie sc = (SaveCookie)activatedNodes[i].getCookie(SaveCookie.class);
                    if (sc != null) {
                        sc.save();
                    }
                }
            }
        } catch (java.io.IOException exc) {
            ErrorManager em = ErrorManager.getDefault();
            Throwable t = em.annotate(
                exc, NbBundle.getBundle(ExitDialog.class).getString("EXC_Save")
            );
            em.notify(ErrorManager.EXCEPTION, t);
        }
    }
    /* Listens to the chnages in list of modified data objects
    * and enables / disables this action appropriately */
    final class PChangeListener implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent evt) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Set s = ExitDialog.getModifiedTCSet();
                    setEnabled(!s.isEmpty());
                }
            });
        }
    } // end of PChangeListener inner class
    
}
