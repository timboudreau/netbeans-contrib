/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.contrib.testng.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.contrib.testng.spi.TestConfig;
import org.netbeans.modules.contrib.testng.api.TestNGSupport;
import org.netbeans.modules.contrib.testng.spi.TestNGSupportImplementation.TestExecutor;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class RunTestMethodAction extends CookieAction {

    private static final Logger LOGGER = Logger.getLogger(RunTestMethodAction.class.getName());

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (super.enable(activatedNodes)) {
            DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
            Project p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            return TestNGSupport.isProjectSupported(p);
        }
        return false;
    }

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
                if (task.getMethodName() == null) {
                    //TODO - cursor is outside of a method or a given method is not a test
                    //so let allow user to choose any available method within given class
                    //using some UI
                }
                DataObject dobj = l.lookup(DataObject.class);
                Project p = FileOwnerQuery.getOwner(dobj.getPrimaryFile());
                TestExecutor exec = TestNGSupport.findTestNGSupport(p).createExecutor(p);
                TestConfig conf = new TestConfig(dobj.getPrimaryFile(), task.getPackageName(), task.getClassName(), task.getMethodName());
                try {
                    exec.execute(conf);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(RunTestMethodAction.class, "CTL_RunTestMethodAction");
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

