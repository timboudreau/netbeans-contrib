/*
 * RunSetConsistencyValidatorAction.java
 * 
 * Created on Oct 4, 2007, 10:56:23 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.actions;

import org.netbeans.modules.bpel.model.api.BpelModel;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;
import org.ucla.netbeans.module.bpel.setconsistency.Util;
import org.ucla.netbeans.module.bpel.setconsistency.validator.SetConsistencyValidatorVisitor;

/**
 *
 * @author radval
 */
public class RunSetConsistencyValidatorAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        OutputWriter writer =  IOProvider.getDefault().getStdOut();
        writer.println("In RunSetConsistencyValidatorAction");
        
        DataObject bpelDataObject = activatedNodes[0].getCookie(DataObject.class);
            
            if(bpelDataObject != null) {
                writer.println("In bpel file "+ bpelDataObject.getName());
                BpelModel model = Util.getModel(bpelDataObject);
                
                if(model != null && model.getProcess() != null) {
                    SetConsistencyValidatorVisitor visitor = new SetConsistencyValidatorVisitor();
                    model.getProcess().accept(visitor);
                }
            }
        
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RunSetConsistencyValidatorAction.class, "RunSetConsistencyValidatorAction_DisplayName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
