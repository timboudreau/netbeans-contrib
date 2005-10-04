/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.refactoring.vcs;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.Action;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.refactoring.spi.ProblemDetailsImplementation;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Becicka
 */
public class CheckoutFiles implements ProblemDetailsImplementation {
    
    private Collection files;
    private Command editCmd;
    private static String CHECKOUT_OPTION;
    private static final String CANCEL_OPTION = NbBundle.getMessage(CheckoutFiles.class, "LBL_Cancel");
    private Cancellable parent;
    /** Creates a new instance of CheckoutFiles */
    public CheckoutFiles(Collection files, Command editCmd) {
        this.setFiles(files);
        this.setEditCmd(editCmd);
    }
    
    public void showDetails(Action rerunRefactoringAction, Cancellable parent) {
        this.parent = parent;
        CHECKOUT_OPTION = MessageFormat.format(NbBundle.getMessage(CheckoutFiles.class, "LBL_Checkout_And_Rerun"), new Object[]{rerunRefactoringAction.getValue(Action.NAME)});
        DialogDescriptor desc = new DialogDescriptor(new CheckoutPanel(getFiles()), NbBundle.getMessage(CheckoutFiles.class, "LBL_Title_Update_Files"), true, new String[]{CHECKOUT_OPTION, CANCEL_OPTION}, CHECKOUT_OPTION, DialogDescriptor.DEFAULT_ALIGN, null, null);
        Object retval = DialogDisplayer.getDefault().notify(desc);
        if (retval == CHECKOUT_OPTION) {
            checkoutFiles();
            rerunRefactoring(rerunRefactoringAction);
        }
    }
    
    public String getDetailsHint() {
        return NbBundle.getMessage(CheckoutFiles.class, "LBL_Update_Files");
    }

    private void checkoutFiles() {
        CommandTask task = getEditCmd().execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void rerunRefactoring(Action rerunRefactoringAction) {
        parent.close();
        rerunRefactoringAction.actionPerformed(null);
    }

    void setFiles(Collection files) {
        this.files = files;
    }

    void setEditCmd(Command editCmd) {
        this.editCmd = editCmd;
    }

    public Collection getFiles() {
        return files;
    }

    public Command getEditCmd() {
        return editCmd;
    }
    
    
}
