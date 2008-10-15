/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.contrib.testng.actions;

import java.io.IOException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.spi.TestConfig;
import org.netbeans.modules.contrib.testng.api.TestNGSupport;
import org.netbeans.modules.contrib.testng.spi.TestNGSupportImplementation.TestExecutor;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class RerunFailedTestsAction extends CookieAction {

    public RerunFailedTestsAction() {
    }
    
    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
        TestExecutor exec = TestNGSupport.findTestNGSupport(p).createExecutor(p);
        assert exec.hasFailedTests();
        TestConfig conf = new TestConfig(true, null, null, null);
        try {
            exec.execute(conf);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (super.enable(activatedNodes)) {
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if (TestNGSupport.isProjectSupported(p)) {
                return TestNGSupport.findTestNGSupport(p).createExecutor(p).hasFailedTests();
            }
        }
        return false;
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(RerunFailedTestsAction.class, "CTL_RerunFailedTestsAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

