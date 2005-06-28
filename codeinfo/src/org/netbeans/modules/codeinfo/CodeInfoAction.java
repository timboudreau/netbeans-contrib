package org.netbeans.modules.codeinfo;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Action to display the code info component
 *
 * @author Tim Boudreau
 */
public class CodeInfoAction extends CallableSystemAction {
    public void performAction () {
        CodeInfoComponent nav = CodeInfoComponent.findDefault ();
        nav.open ();
        nav.requestActive ();
    }

    public String getName () {
        return NbBundle.getMessage ( CodeInfoAction.class, "LBL_Action" ); //NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/codeinfo/codeinfo.png"; //NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean asynchronous () {
        return false;
    }
}
