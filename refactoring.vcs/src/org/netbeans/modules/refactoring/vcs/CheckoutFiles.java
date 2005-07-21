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
import java.util.Collection;
import javax.swing.Action;
import org.netbeans.modules.refactoring.spi.ProblemDetailsImplementation;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Jan Becicka
 */
public class CheckoutFiles implements ProblemDetailsImplementation {
    
    private Collection files;
    private static String CHECKOUT_OPTION;
    private static final String CANCEL_OPTION = "Cancel";
    /** Creates a new instance of CheckoutFiles */
    public CheckoutFiles(Collection files) {
        this.files=files;
    }
    
    public void showDetails(Action rerunRefactoringAction) {
        CHECKOUT_OPTION = "Checkout and " + rerunRefactoringAction.getValue(Action.NAME);
        org.openide.DialogDescriptor desc = new DialogDescriptor(new CheckoutPanel(files), "Update Files", true, new String[]{CHECKOUT_OPTION, CANCEL_OPTION}, CHECKOUT_OPTION, DialogDescriptor.DEFAULT_ALIGN, null, null);
        Object retval = DialogDisplayer.getDefault().notify(desc);
        if (retval == CHECKOUT_OPTION) {
            checkoutFiles();
            rerunRefactoring(rerunRefactoringAction);
        }
    }
    
    public String getDetailsHint() {
        return "Update Files";
    }

    private void checkoutFiles() {
    }
    
    private void rerunRefactoring(Action rerunRefactoringAction) {
        Runnable close = (Runnable) rerunRefactoringAction.getValue("doCloseParent");
        close.run();
        rerunRefactoringAction.actionPerformed(null);
    }
    
}
