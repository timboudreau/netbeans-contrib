/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase.ui.checkout;

import org.netbeans.modules.clearcase.FileStatusCache;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.DialogBoundsPreserver;
import org.netbeans.modules.versioning.util.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.Dialog;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;
import org.netbeans.modules.clearcase.FileInformation;
import org.netbeans.modules.clearcase.client.*;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Checks all files/folders in the context out, making them editable by the user.
 * 
 * @author Maros Sandor
 */
public class CheckoutAction extends AbstractAction {
    
    static final String RECENT_CHECKOUT_MESSAGES = "checkout.messages"; 

    private static int ALLOW_CHECKOUT = 
            FileInformation.STATUS_VERSIONED_UPTODATE | 
            FileInformation.STATUS_VERSIONED_HIJACKED;
    
    private final VCSContext    context;

    public CheckoutAction(String name, VCSContext context) {
        super(name);
        this.context = context;
    }

    @Override
    public boolean isEnabled() {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        Set<File> roots = context.getRootFiles();
        for (File file : roots) {
            if( (cache.getInfo(file).getStatus() & ALLOW_CHECKOUT) == 0 ) {
                return false;
            }                
        }
        return true;
    }
    
    public void actionPerformed(ActionEvent ev) {
        Set<File> roots = context.getFiles();
        performCheckout(roots.toArray(new File[roots.size()]), Utils.getContextDisplayName(context));
    }

    public static ClearcaseClient.CommandRunnable performCheckout(File[] files, String title) {        
        JButton checkoutButton = new JButton(); 
        CheckoutPanel panel = new CheckoutPanel();
        
        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(CheckoutAction.class, "CTL_CheckoutDialog_Title", title)); // NOI18N
        dd.setModal(true);        
        org.openide.awt.Mnemonics.setLocalizedText(checkoutButton, org.openide.util.NbBundle.getMessage(CheckoutAction.class, "CTL_CheckoutDialog_Checkout"));
        
        dd.setOptions(new Object[] {checkoutButton, DialogDescriptor.CANCEL_OPTION}); // NOI18N
        dd.setHelpCtx(new HelpCtx(CheckoutAction.class));
                
        panel.putClientProperty("contentTitle", title);  // NOI18N
        panel.putClientProperty("DialogDescriptor", dd); // NOI18N
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);        
        dialog.addWindowListener(new DialogBoundsPreserver(ClearcaseModuleConfig.getPreferences(), "checkout.dialog")); // NOI18N       
        dialog.pack();        
        dialog.setVisible(true);
        
        Object value = dd.getValue();
        if (value != checkoutButton) return null;
        
        String message = panel.taMessage.getText();
        boolean doReserved = panel.cbReserved.isSelected();

        Utils.insert(ClearcaseModuleConfig.getPreferences(), RECENT_CHECKOUT_MESSAGES, message, 20);
                
        ClearcaseClient.CommandRunnable cr = Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Checking out...",
                new CheckoutCommand(files, message, doReserved ? CheckoutCommand.Reserved.Reserved : CheckoutCommand.Reserved.Unreserved, 
                                    false, createNotificationListener(files), new OutputWindowNotificationListener())));        
        return cr;
    }
    
    private static NotificationListener createNotificationListener(final File ...files) {
        return new NotificationListener() {
            public void commandStarted()        { /* boring */ }
            public void outputText(String line) { /* boring */ }
            public void errorText(String line)  { /* boring */ }
            public void commandFinished() {     
                org.netbeans.modules.clearcase.util.Utils.afterCommandRefresh(files, false);                
            }
        };
    }
    
    /**
     * Interceptor entry point.
     * 
     * @param file file to checkout
     */
    public static void checkout(File file) {
        ClearcaseClient.CommandRunnable cr = Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Checking out...",
                new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Default, true, createNotificationListener(file))));
        cr.waitFinished();
    }

    private static final FileFilter checkoutFileFilter = new FileFilter() {
        public boolean accept(File pathname) {
            return true;
        }
    };
}
