/*
 * PrintTreeAction.java
 *
 * Created on February 20, 2003, 4:14 PM
 */

package org.netbeans.modules.uidiagnostics;
import org.openide.util.actions.*;
import org.openide.util.*;
import javax.swing.Action;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
/** Action to print out the component tree.
 *
 * @author  Tim Boudreau
 */
public class PrintTreeAction extends BooleanStateAction {
    
    /** Creates a new instance of PrintTreeAction */
    public PrintTreeAction() {
    }
    
    public void actionPerformed () {
        if (!getBooleanState()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message (
                NbBundle.getMessage (PrintTreeAction.class, "MSG_ClickToPrintTree"));
            DialogDisplayer.getDefault().notify (nd);
            setBooleanState (true);
        } else {
            getInstance().disarm();
            setBooleanState (false);
        }
    }
    
    public boolean getBooleanState () {
        return getInstance().armed;
    }
    
    public void setBooleanState (boolean val) {
        if (val) {
            getInstance().arm();
        } else {
            getInstance().disarm();
        }
        super.setBooleanState (val);
    }
    
    private Decipherer instance = null;
    
    private Decipherer getInstance () {
        if (instance == null) instance = new Decipherer();
        return instance;
    }
    
    public String getName() {
        return NbBundle.getMessage(ConfigureFocusLoggingAction.class, "LBL_PrintAction");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/uidiagnostics/PrintTreeActionIcon.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
     protected void initialize() {
        super.initialize();
        putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ConfigureFocusLoggingAction.class, "HINT_PrintAction"));
     }    
    
}
