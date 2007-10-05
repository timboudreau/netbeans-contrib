/*
 * EditSetConsistencyRule.java
 * 
 * Created on Oct 4, 2007, 10:56:41 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency.actions;

import org.netbeans.modules.bpel.model.api.BpelModel;
import org.netbeans.modules.bpel.model.spi.BpelModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

/**
 *
 * @author radval
 */
public class EditSetConsistencyRuleAction extends CookieAction {

    @Override
    protected int mode() {
        return CookieAction.MODE_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[] {DataObject.class};
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        OutputWriter writer =  IOProvider.getDefault().getStdOut();
        writer.println("In EditSetConsistencyRuleAction");
        if(activatedNodes.length > 0) {
            DataObject bpelDataObject = activatedNodes[0].getCookie(DataObject.class);
            
            if(bpelDataObject != null) {
                writer.println("In bpel file "+ bpelDataObject.getName());
                
                BpelModel model = getModel(bpelDataObject);
                
                if(model != null) {
                    writer.println("Got BPEL Model");
                }
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(EditSetConsistencyRuleAction.class, "EditSetConsistencyRuleAction_DisplayName");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    private BpelModel getModel(DataObject bpelDataObject) {
        ModelSource modelSource = Utilities.getModelSource(bpelDataObject
                .getPrimaryFile(), true);
        return getModelFactory().getModel(modelSource);
    }
    private BpelModelFactory getModelFactory() {
        BpelModelFactory factory = (BpelModelFactory) Lookup.getDefault()
                .lookup(BpelModelFactory.class);
        return factory;
    }
}
