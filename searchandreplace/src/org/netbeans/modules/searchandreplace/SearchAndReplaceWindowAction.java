package org.netbeans.modules.searchandreplace;

import java.awt.Toolkit;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SearchAndReplaceWindowAction extends CallableSystemAction {
    public void performAction() {
        SearchPreview prev = SearchPreview.getLastSearchComponent();
        if (prev != null) {
            prev.open();
            prev.requestActive();
        } else {
            //A weakReference could disappear between isEnabled() and
            //actionPerformed, so handle it
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public boolean isEnabled() {
        return SearchPreview.getLastSearchComponent() != null;
    }
    
    public String getName() {
        return NbBundle.getMessage(SearchAndReplaceWindowAction.class,
                "CTL_SearchAndReplaceWindowAction"); //NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
