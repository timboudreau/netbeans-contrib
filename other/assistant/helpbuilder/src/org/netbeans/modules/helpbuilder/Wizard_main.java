/*
 * Wizard.java
 *
 * Created on September 24, 2001, 3:44 PM
 */

package org.netbeans.modules.helpbuilder;

import java.awt.Dialog;
import javax.swing.SwingUtilities;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
/**
 *
 * @author  rg125988
 * @version 
 *
 */
public class Wizard_main {

    /** Creates new Wizard */
    public Wizard_main() {
        WizardDescriptor desc = new HelpBuilderWizardDescriptor();
        final Dialog dlg = DialogDisplayer.getDefault().createDialog (desc);
        try {
            SwingUtilities.invokeAndWait (new Runnable () {
                public void run () {
                    dlg.show ();
                    // If nonmodal, should also wait for it to be closed.
                }
            });
        } catch (Exception e) { // InterruptedException, InvocationTargetException
            ErrorManager.getDefault().notify (e);
            return;
        }
        if (desc.getValue () == WizardDescriptor.FINISH_OPTION) {
            System.out.println ("User finished the wizard"); // NOI18N
        } else {
            System.out.println ("User cancelled the wizard"); // NOI18N
        }
    }


    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        new Wizard_main();
    }

}
