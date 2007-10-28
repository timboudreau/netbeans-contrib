/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.fileopenserver;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

public final class StartFileOpenServerAction extends BooleanStateAction {

    @Override
    public void setBooleanState(boolean start) {
        super.setBooleanState(start);
        final FileOpenServer fileOpenServer = FileOpenServer.getFileOpenServer();
        if (fileOpenServer != null) {
            if (start) {
                fileOpenServer.startServer();
            } else {
                fileOpenServer.stopServer();
            }
        }
    }        
    
    @Override
    protected void initialize() {
        super.initialize();
        putProperty(PROP_BOOLEAN_STATE, FileOpenServer.getFileOpenServer().isStarted());
    }

    public String getName() {
        return NbBundle.getMessage(StartFileOpenServerAction.class, "CTL_StartFileOpenServerAction");
    }

    @Override
    protected String iconResource() {
        return "org/netbeans/modules/fileopenserver/resources/StartFileOpenServerIcon.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
