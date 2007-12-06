/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remotefs.ui;

import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.NodeAction;


public final class AddFTPSiteAction extends NodeAction{

        DataFolder dataFolder;
        private static AddFTPSiteAction instance;

        private AddFTPSiteAction() {
            putValue("noIconInMenu", Boolean.TRUE); // NOI18N
        }

        public static NodeAction getInstance() {
            if (instance == null) {
                instance = new AddFTPSiteAction();
            }
            
            return instance;
        }

        @Override
        protected boolean asynchronous() {
            return false;
        }

        @Override
        protected void performAction(Node[] activatedNodes) {
            if (activatedNodes.length == 1 && activatedNodes[0] instanceof RootNode) {
                CallableSystemAction action = NewFTPSiteWizardAction.getInstance();
                action.performAction();
            }
        }

        @Override
        protected boolean enable(Node[] arr) {
            if ((arr == null) || (arr.length == 0)) {
                return true;
            }
            if (arr.length == 1 && arr[0] instanceof RootNode) {
                return true;
            }
            return false;
        }

        @Override
        public String getName() {
            return NbBundle.getMessage(AddFTPSiteAction.class, "ACT_AddFTPSite"); // NOI18N
        }

        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx(AddFTPSiteAction.class);
        }

        @Override
        protected String iconResource() {
            return "lu/kaupthing/explorer/resources/globe-sextant-16x16.png";
        }

}
