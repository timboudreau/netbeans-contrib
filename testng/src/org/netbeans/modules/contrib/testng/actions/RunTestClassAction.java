/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.contrib.testng.actions;

import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.contrib.testng.suite.XMLSuiteHandler;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class RunTestClassAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        Lookup l = activatedNodes[0].getLookup();
        EditorCookie ec = l.lookup(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes.length > 0) {
                final int cursor = panes[0].getCaret().getDot();
                JavaSource js = JavaSource.forDocument(panes[0].getDocument());
                TestClassInfoTask task = new TestClassInfoTask(cursor);
                try {
                    js.runUserActionTask(task, true);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                DataObject dobj = l.lookup(DataObject.class);
                Project p = FileOwnerQuery.getOwner(dobj.getPrimaryFile());
                FileObject fo = p.getProjectDirectory();
                try {
                    fo = FileUtil.createFolder(fo, "build/generated/testng");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                File f = XMLSuiteHandler.createSuiteforMethod(
                        FileUtil.toFile(fo),
                        ProjectUtils.getInformation(p).getDisplayName(),
                        task.getPackageName(),
                        task.getClassName(),
                        null);
                try {
                    ActionUtils.runTarget(p.getProjectDirectory().getFileObject("build.xml"), new String[]{"run-testng"}, null);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(RunTestClassAction.class, "CTL_RunTestClassAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{DataObject.class, EditorCookie.class};
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

