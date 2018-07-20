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
 * Software is Nokia. Portions Copyright 2005 Nokia.
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

package org.netbeans.modules.tool.actions;

import org.netbeans.modules.tool.ExitDialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.cookies.SaveCookie;
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
        s = ExitDialog.getModifiedTCSet();
        setEnabled(!s.isEmpty());
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
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Saving failed.", exc); // NOI18N
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getBundle(ExitDialog.class).getString("EXC_Save"),
                    NotifyDescriptor.ERROR_MESSAGE
            );
            DialogDisplayer dd = DialogDisplayer.getDefault();
            dd.notify(nd);
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
