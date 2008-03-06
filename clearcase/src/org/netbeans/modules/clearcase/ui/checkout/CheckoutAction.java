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
import java.util.*;

import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;
import org.netbeans.modules.clearcase.FileInformation;
import org.netbeans.modules.clearcase.util.ClearcaseUtils;
import org.netbeans.modules.clearcase.client.*;
import org.netbeans.modules.clearcase.client.status.FileEntry;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Checks all files/folders in the context out, making them editable by the user.
 * 
 * @author Maros Sandor
 */
public class CheckoutAction extends AbstractAction {

    private static final int STATUS_DISABLED    = 0;
    private static final int STATUS_CHECKOUT    = 1;
    private static final int STATUS_UNCHECKOUT  = 2;
    
    static final String RECENT_CHECKOUT_MESSAGES = "checkout.messages"; 

    private static int ALLOW_CHECKOUT = FileInformation.STATUS_VERSIONED_UPTODATE | FileInformation.STATUS_VERSIONED_HIJACKED;
    private static int ALLOW_UNCO = FileInformation.STATUS_VERSIONED_CHECKEDOUT;
    
    private final VCSContext    context;
    private final int           status;

    public CheckoutAction(VCSContext context) {
        this.context = context;
        status = getActionStatus();
        putValue(Action.NAME, status == STATUS_UNCHECKOUT ? "Uncheckout..." : "Checkout...");
    }

    private int getActionStatus() {
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        int status = STATUS_DISABLED;
        Set<File> files = context.getFiles();
        for (File file : files) {
            if ((cache.getInfo(file).getStatus() & ALLOW_CHECKOUT) != 0) {
                if (status == STATUS_UNCHECKOUT) return STATUS_DISABLED;
                status = STATUS_CHECKOUT;
            }                
            if ((cache.getInfo(file).getStatus() & ALLOW_UNCO) != 0) {
                if (status == STATUS_CHECKOUT) return STATUS_DISABLED;
                status = STATUS_UNCHECKOUT;
            }
        }
        return status;
    }
    
    @Override
    public boolean isEnabled() {
        return status != STATUS_DISABLED;
    }
    
    public void actionPerformed(ActionEvent ev) {
        Set<File> roots = context.getFiles();
        switch (status) {
        case STATUS_CHECKOUT:
            performCheckout(roots.toArray(new File[roots.size()]), Utils.getContextDisplayName(context));
            break;
        case STATUS_UNCHECKOUT:
            performUncheckout(roots.toArray(new File[roots.size()]));
            break;
        }
    }
    
    private void performUncheckout(File [] files) {
        String contextTitle = Utils.getContextDisplayName(context);
        JButton unCheckoutButton = new JButton(); 
        UncheckoutPanel panel = new UncheckoutPanel();

        panel.cbKeep.setEnabled(false);
        for (File file : files) {
            if(file.isFile()) {
                panel.cbKeep.setEnabled(true);        
                break;
            }
        }
        
        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(CheckoutAction.class, "CTL_UncheckoutDialog_Title", contextTitle)); // NOI18N
        dd.setModal(true);
        dd.setMessageType(DialogDescriptor.WARNING_MESSAGE);
        Mnemonics.setLocalizedText(unCheckoutButton, NbBundle.getMessage(CheckoutAction.class, "CTL_UncheckoutDialog_Unheckout"));
        
        dd.setOptions(new Object[] {unCheckoutButton, DialogDescriptor.CANCEL_OPTION}); // NOI18N
        dd.setHelpCtx(new HelpCtx(CheckoutAction.class));
                
        panel.putClientProperty("contentTitle", contextTitle);  // NOI18N
        panel.putClientProperty("DialogDescriptor", dd); // NOI18N
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);        
        dialog.addWindowListener(new DialogBoundsPreserver(ClearcaseModuleConfig.getPreferences(), "uncheckout.dialog")); // NOI18N       
        dialog.pack();        
        dialog.setVisible(true);
        
        Object value = dd.getValue();
        if (value != unCheckoutButton) return;
        
        boolean keepFiles = panel.cbKeep.isSelected();
        
        Clearcase.getInstance().getClient().post(new ExecutionUnit(
                "Undoing Checkout...",
                new UnCheckoutCommand(files, keepFiles, createNotificationListener(files), new OutputWindowNotificationListener())));
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
                org.netbeans.modules.clearcase.util.Utils.afterCommandRefresh(files, false, false);                
            }
        };
    }

    /**
     * Checks out the file or directory depending on the user-selected strategy in Options.
     * In case the file is already writable or the directory is checked out, the method does nothing.
     * Interceptor entry point.
     * 
     * @param file file to checkout
     * @see org.netbeans.modules.clearcase.ClearcaseModuleConfig#getOnDemandCheckout()
     */
    public static void ensureMutable(File file) {
        ensureMutable(file, null);
    }   
    
    /**
     * Checks out the file or directory depending on the user-selected strategy in Options.
     * In case the file is already writable or the directory is checked out, the method does nothing.
     * Interceptor entry point.
     * 
     * @param file file to checkout
     * @param entry the given files {@link FileEntry}
     * @see org.netbeans.modules.clearcase.ClearcaseModuleConfig#getOnDemandCheckout()
     */
    public static void ensureMutable(File file, FileEntry entry) {
        if (file.isDirectory()) {
            if(entry == null) {
                entry = ClearcaseUtils.readEntry(file);                
            }
            if (entry == null || entry.isCheckedout() || entry.isViewPrivate()) {
                return;
            }
        } else {
            if (file.canWrite()) return;
        }

        ClearcaseModuleConfig.OnDemandCheckout odc = ClearcaseModuleConfig.getOnDemandCheckout();

        CheckoutCommand command;
        switch (odc) {
        case Disabled:
            // XXX let the user decide if he want's to checkout the file
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("On-demand checkouts are currently disabled. Visit IDE Options to change that."));
            return;
        case Reserved:
        case ReservedWithFallback:
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Reserved, true, createNotificationListener(file));
            break;
        case Unreserved:
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Unreserved, true, createNotificationListener(file));
            break;
        default:
            throw new IllegalStateException("Illegal Checkout type: " + odc);
        }
        
        ExecutionUnit eu = new ExecutionUnit("Checking out...", odc != ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback, command);
        ClearcaseClient.CommandRunnable cr = Clearcase.getInstance().getClient().post(eu);
        cr.waitFinished();
        
        if (command.hasFailed() && odc == ClearcaseModuleConfig.OnDemandCheckout.ReservedWithFallback) {
            command = new CheckoutCommand(new File [] { file }, null, CheckoutCommand.Reserved.Unreserved, true, 
                                          new OutputWindowNotificationListener(), createNotificationListener(file));
            eu = new ExecutionUnit("Checking out...", true, command);
            cr = Clearcase.getInstance().getClient().post(eu);
            cr.waitFinished();
        }
    }
}
