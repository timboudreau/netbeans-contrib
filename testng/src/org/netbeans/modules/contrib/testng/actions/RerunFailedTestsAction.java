/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.contrib.testng.actions;

import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.ProjectUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class RerunFailedTestsAction extends CookieAction {

    private FileObject projectHome;
    private FileObject failedTestsConfig;

    public RerunFailedTestsAction() {
        projectHome = null;
        failedTestsConfig = null;
    }
    
    protected void performAction(Node[] activatedNodes) {
        assert failedTestsConfig != null;
        Properties p = new Properties();
        p.put("testng.config", FileUtil.getRelativePath(projectHome, failedTestsConfig));
        try {
            ActionUtils.runTarget(projectHome.getFileObject("build.xml"), new String[]{"run-testng"}, p);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (super.enable(activatedNodes)) {
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            projectHome = p.getProjectDirectory();
            //XXX - should rather listen on a fileobject??
            FileUtil.refreshFor(FileUtil.toFile(projectHome));
            failedTestsConfig = projectHome.getFileObject("build/test/results/testng-failed.xml"); //NOI18N
            return ProjectUtilities.isAntProject(p)
                    && (failedTestsConfig != null && !failedTestsConfig.isVirtual() && failedTestsConfig.isValid());
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

