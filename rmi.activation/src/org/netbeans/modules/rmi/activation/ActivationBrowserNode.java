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

package org.netbeans.modules.rmi.activation;

import java.io.IOException;
import java.awt.Dialog;
import java.awt.event.*;
import org.netbeans.modules.rmi.activation.settings.RMIActivationSettings;

import org.openide.*;
import org.openide.actions.*;
import org.openide.util.datatransfer.NewType;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.rmi.activation.util.RefreshAction;
import org.netbeans.modules.rmi.activation.util.RefreshCookie;

/** A node with some children.
 *
 * @author mryzl, Jan Pokorsky
 */
public class ActivationBrowserNode extends AbstractNode implements RefreshCookie {

    public ActivationBrowserNode() {
        super (new ActivationBrowserChildren ());
        
        // set an icon
        setIconBase ("org/netbeans/modules/rmi/resources/activationBrowser"); // NOI18N
        
        // Set FeatureDescriptor stuff:
        setName (NbBundle.getBundle (ActivationBrowserNode.class).getString ("LBL_ACTIVATION_BROWSER")); // NOI18N
        setDisplayName (NbBundle.getBundle (ActivationBrowserNode.class).getString ("LBL_ACTIVATION_BROWSER")); // NOI18N
        setShortDescription (NbBundle.getBundle (ActivationBrowserNode.class).getString ("HINT_ACTIVATION_BROWSER")); // NOI18N
    }
    
    public Node.Cookie getCookie(Class clazz) {
        if (clazz.isInstance(this)) return this;
        else return super.getCookie(clazz);
    }
    
    // Create the popup menu:
    protected SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (NewAction.class),
            null,
            SystemAction.get (RefreshAction.class),
            null,
            SystemAction.get (PropertiesAction.class),
        };
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx (ActivationBrowserNode.class.getName());
    }
    
    protected ActivationBrowserChildren getActivationBrowserChildren () {
        return (ActivationBrowserChildren) getChildren ();
    }
    
    public void refresh() {
        getActivationBrowserChildren().scheduleRefreshTask(0);
    }
    
    /** Permit new subnodes to be created:
    */
    public NewType[] getNewTypes () {
        return new NewType[] { new ActivationType() };
    }
    
    class ActivationType extends NewType implements ActionListener {
        
        private Dialog dialog;
        private ActivationPanel panel;
        private DialogDescriptor dialogdescriptor;
        
        public String getName () {
            return NbBundle.getBundle (ActivationBrowserNode.class).getString ("LBL_NEW_Activation"); // NOI18N
        }
        
        public void create () throws IOException {
            // display dialog
            panel = new ActivationPanel();
            DialogDescriptor dialogDesc = new DialogDescriptor(
                panel,
                NbBundle.getBundle(ActivationBrowserNode.class).getString("LBL_NEW_Activation.Title"), // NOI18N
                false,
                this
            );
            // added help button - must change location of help !!!
            dialogDesc.setHelpCtx(new HelpCtx(ActivationType.class));
            dialogDesc.setClosingOptions(new Object[0]);
            dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
            dialog.show();
        }
        
        public void actionPerformed(final java.awt.event.ActionEvent ev) {
            if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                try {
                    ActivationSystemItem system = new ActivationSystemItem(panel.getHost(), panel.getPort());
                    RMIActivationSettings.getDefault().removeActivationSystemItem(system);
                    system.updateActivationItems();
                } catch (NumberFormatException ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getBundle(ActivationBrowserNode.class).getString("ERR_ActivationTypePort"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                    return;
                } catch (java.net.UnknownHostException ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getBundle(ActivationBrowserNode.class).getString("ERR_UnknownHost"), NotifyDescriptor.ERROR_MESSAGE)); // NOI18N
                    return;
                }
            }
            dialog.dispose();
            dialog = null;
        }
    }
}
