/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.xtest.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.apache.tools.ant.module.api.support.TargetLister;
import org.netbeans.api.debugger.jpda.DebuggerStartException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.project.Project;
import org.openide.awt.Actions;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.actions.Presenter;
import org.openide.util.NbBundle;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.HtmlBrowser;
import org.openide.execution.ExecutorTask;

/**
 * Defines XTest actions for top level project logical node. Inspired by CVS
 * sub menu org.netbeans.modules.versioning.system.cvss.ui.actions.ProjectCvsMenuItem.
 *
 * <pre>
 * XTest >
 *  Clean
 *  --------------------------
 *  Build unit Tests
 *  Run unit Tests
 *  Debug unit Tests
 *  Measure unit Test Coverage
 *  --------------------------
 *  Build qa-functional Tests
 *  Run qa-functional Tests
 *  Debug qa-functional Tests
 *  Measure qa-functional Test Coverage
 * </pre>
 *
 * <p>The menu is available only for projects that contains some tests.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public final class XTestProjectMenuItem extends AbstractAction implements Presenter.Popup, ContextAwareAction {
    
    private static final Logger LOGGER = Logger.getLogger(XTestProjectMenuItem.class.getName());
    private static final String NAME = NbBundle.getBundle(XTestProjectMenuItem.class).getString("CTL_MenuItem_XTest");
    private Lookup context;

    /** Creates XTest sub menu. */
    public XTestProjectMenuItem() {
        this(null);
    }

    /** Creates XTest sub menu. 
     * @param actionContext context of action
     */
    public XTestProjectMenuItem(Lookup actionContext) {
        super(NAME);
        this.context = actionContext;
    }
    
    /** No action for sub menu holder. 
     * @param ignore action event
     */
    public void actionPerformed(ActionEvent ignore) {
        // empty
    }
    
    /** Returns created popup menu. 
     * @return created popup menu.
     */
    public JMenuItem getPopupPresenter() {
        return new XTestProjectMenuItems();
    }

    /** Creates action instance for provided context. 
     * @param actionContext action context
     */
    public Action createContextAwareInstance(Lookup actionContext) {
        return new XTestProjectMenuItem(actionContext);
    }

    /** Sub menu items. */
    class XTestProjectMenuItems extends JMenuItem implements DynamicMenuContent {
        
        public JComponent[] getMenuPresenters() {
            Action [] actions = actions();
            if(actions == null) {
                // hide sub menu
                return new JComponent[0];
            }
            final JMenu menu = new JMenu();
            Mnemonics.setLocalizedText(menu, NAME);
            for (int i = 0; i < actions.length; i++) {
                Action action = actions[i];
                if (action == null) {
                    menu.add(new JSeparator());
                } else {
                    JMenuItem item = new JMenuItem();
                    Actions.connect(item, actions[i], false);
                    menu.add(item);
                }
            }
            return new JComponent[] { menu };
        }
        
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return items;
        }
    }
    
    /** Returns array of actions for all test types in selected project. */
    private Action[] actions() {
        Project project = (Project)context.lookup(Project.class);
        ArrayList actions = new ArrayList();
        
        String[] testTypes = findTestTypes(project);
        if(testTypes.length > 0) {
            actions.add(createAction(NbBundle.getMessage(XTestProjectMenuItem.class, "CTL_MenuItem_Clean"),
                                     project, null, new String[] {"clean"}, false));
            actions.add(null);
        } else {
            // hide XTest submenu
            return null;
            // or
            //actions.add(createAction("Create XTest Infrastructure", project, "", new String[] {""}, false));
            // and open new file wizard Testing Tools|XTest Infrastructure (enable it always)
        }
        for(int i=0;i<testTypes.length;i++) {
            Properties props = new Properties();
            props.setProperty("xtest.testtype", testTypes[i]); // NOI18N
            Properties propsDebug = new Properties();
            propsDebug.setProperty("xtest.testtype", testTypes[i]); // NOI18N
            propsDebug.setProperty("xtest.debug.port", "8765"); // NOI18N
            // "Build "+testTypes[i]+" Tests"
            actions.add(createAction(NbBundle.getMessage(XTestProjectMenuItem.class, "CTL_MenuItem_BuildTests", testTypes[i]), // NOI18N
                                     project, props, new String[] {"buildtests"}, false)); // NOI18N
            // "Run "+testTypes[i]+" Tests"
            actions.add(createAction(NbBundle.getMessage(XTestProjectMenuItem.class, "CTL_MenuItem_RunTests", testTypes[i]), // NOI18N
                                     project, props, new String[] {"runtests"}, true)); // NOI18N
            // "Debug "+testTypes[i]+" Tests"
            actions.add(createAction(NbBundle.getMessage(XTestProjectMenuItem.class, "CTL_MenuItem_DebugTests", testTypes[i]), // NOI18N
                                     project, propsDebug, new String[] {"runtests"}, true)); // NOI18N
            if(targetExists(findTestBuildXml(project), "coverage")) { //NOI18N
                // "Measure "+testTypes[i]+" Tests Coverage"
                actions.add(createAction(NbBundle.getMessage(XTestProjectMenuItem.class, "CTL_MenuItem_MeasureCoverage", testTypes[i]), // NOI18N
                                         project, props, new String[] {"coverage"}, true)); // NOI18N
            }
            // add separator
            if(testTypes.length-1 > i) {
                actions.add(null);
            }
        }
        return (Action[])actions.toArray(new Action[actions.size()]);
    }
    
    private AbstractAction createAction(String displayName, final Project project, 
            final Properties props, final String[] targets, final boolean showResults) {
        
        return new AbstractAction(displayName) {
            /** Enabled only if one project is selected and test/build.xml exists. */
            public boolean isEnabled() {
                // enable only if one project is selected
                if(context.lookup(new Lookup.Template(Project.class)).allInstances().size() == 1) {
                    return findTestBuildXml(project) != null;
                }
                return false;
            }
            
            /** Run given target and show test results if requested. */
            public void actionPerformed(ActionEvent ignore) {
                try {
                    // start thread for attaching debugger when port is set
                    AttachRunnable attachRunnable = null;
                    if(props != null && props.getProperty("xtest.debug.port") != null) { // NOI18N
                        attachRunnable = new AttachRunnable();
                        Thread attachDebuggerThread = new Thread(attachRunnable);
                        attachDebuggerThread.start();
                    }
                    final AttachRunnable attachRunnableFinal = attachRunnable;
                    // run given target
                    ExecutorTask task = ActionUtils.runTarget(findTestBuildXml(project), targets, props);
                    task.addTaskListener(new TaskListener() {
                        public void taskFinished(Task task) {
                            if(((ExecutorTask)task).result() == 0 && showResults) {
                                if(targets[0].equals("coverage")) { //NOI18N
                                    showCoverageResults(project);
                                } else {
                                    showTestResults(project);
                                }
                            }
                            // stop thread for attaching debugger
                            if(attachRunnableFinal != null) {
                                LOGGER.fine("Setting attachRunnableFinal.finish=true"); // NOI18N
                                attachRunnableFinal.finish = true;
                            }
                        }
                    });
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        };
    }
    
    /** Returns true if target is available in build script. */
    private static boolean targetExists(FileObject buildXml, String targetName) {
        if(buildXml == null) {
            return false;
        }
        DataObject d;
        try {
            d = DataObject.find(buildXml);
        } catch (DataObjectNotFoundException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
        AntProjectCookie apc = (AntProjectCookie)d.getCookie(AntProjectCookie.class);
        Iterator iter;
        try {
            iter = TargetLister.getTargets(apc).iterator();
        } catch (IOException ex) {
            // something wrong in build.xml => target not found
            Logger.getAnonymousLogger().fine(ex.getMessage());
            return false;
        }
        while(iter.hasNext()) {
            if(((TargetLister.Target)iter.next()).getName().equals(targetName)) {
                return true;
            }
        }
        return false;
    }
    
    /** Returns FileObject representing test/build.xml or null if it doesn't exist. */
    private static FileObject findTestBuildXml(Project project) {
        return project.getProjectDirectory().getFileObject("test/build.xml"); // NOI18N
    }
    
    /** Opens test/results/index.html in browser of specified project.
     * @param project project to open test results for
     */
    private static void showTestResults(Project project) {
        showBrowser(project.getProjectDirectory().getFileObject("test/results/index.html")); // NOI18N
    }

    /** Opens test/coverage/coverage.html in browser.
     * @param project project to open test results for
     */
    private static void showCoverageResults(Project project) {
        showBrowser(project.getProjectDirectory().getFileObject("test/coverage/coverage.html")); // NOI18N
    }

    /** Opens location in browser.
     * @param resultsFO FileObject to be opened in browser
     */
    private static void showBrowser(FileObject resultsFO) {
        if(resultsFO != null) {
            try {
                HtmlBrowser.URLDisplayer.getDefault().showURL(resultsFO.getURL());
            } catch (FileStateInvalidException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /** Returns sorted array of available test types in given project. It searches
     * for pairs build-testtype.xml, cfg-testtype.xml and returns array
     * of available test types. */
    private static String[] findTestTypes(Project project) {
        FileObject testFO = project.getProjectDirectory().getFileObject("test"); // NOI18N
        if(testFO == null) {
            return new String[0];
        }
        FileObject[] fos = testFO.getChildren(); // NOI18N
        ArrayList testTypes = new ArrayList();
        for(int i=0;i<fos.length;i++) {
            if(fos[i].getExt().equalsIgnoreCase("xml") && fos[i].getName().matches("build-.*")) {  // NOI18N
                String testType = fos[i].getName().substring(fos[i].getName().indexOf('-')+1);
                if(project.getProjectDirectory().getFileObject("test/cfg-"+testType+".xml") != null) { // NOI18N
                    testTypes.add(testType);
                }
            }
        }
        String [] result = (String[])testTypes.toArray(new String[testTypes.size()]);
        Arrays.sort(result);
        return result;
    }
    
    /** Runnable used in createAction method to wait for debugger session. */
    private static class AttachRunnable implements Runnable, PropertyChangeListener {
        public boolean finish = false;
        
        public void run() {
            finish = false;
            try {
                JPDADebugger jpdaDebugger = null;
                while(!finish && jpdaDebugger == null) {
                    try {
                        LOGGER.fine("Creating ServerSocket"); // NOI18N
                        ServerSocket socket = new ServerSocket(8765);
                        socket.close();
                        LOGGER.fine("ServerSocket ready"); // NOI18N
                        Thread.sleep(500);
                    } catch (IOException ioe) {
                        // BindException: Address already in use/debugger started
                        try{
                            LOGGER.fine("ATTACHING"); // NOI18N
                            jpdaDebugger = JPDADebugger.attach("localhost", 8765, new Object[0]); // NOI18N
                            jpdaDebugger.addPropertyChangeListener(JPDADebugger.PROP_STATE, this);
                            LOGGER.fine("waiting for debugger to finish"); // NOI18N
                            synchronized(this) {
                                wait();
                            }
                            Thread.sleep(1000);
                            // debugger finished - continue in cycle, i.e.
                            // wait for another VM in case of unit tests
                            jpdaDebugger.removePropertyChangeListener(this);
                            jpdaDebugger = null;
                        } catch (DebuggerStartException e) {
                            LOGGER.log(Level.INFO, e.getMessage(), e);
                        }
                    }
                }
            } catch (InterruptedException ie) {
                // ignore
            }
            LOGGER.fine("EXITING attachRunnable"); // NOI18N
        }
        
        public void propertyChange(PropertyChangeEvent event) {
            if(Integer.valueOf(event.getNewValue().toString()).intValue() == JPDADebugger.STATE_DISCONNECTED) {
                LOGGER.fine("DISCONNECTED => notifying."); // NOI18N
                synchronized(this) {
                    notifyAll();
                }
            }
        }
    }
}
