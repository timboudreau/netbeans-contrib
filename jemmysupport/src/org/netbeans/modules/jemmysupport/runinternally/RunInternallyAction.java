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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.jemmysupport.runinternally;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.apache.tools.ant.module.api.support.TargetLister;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.spi.project.ActionProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Used to run jemmy test internally in the same JVM as IDE.
 * @author Jiri.Skrivanek@sun.com
 */
public class RunInternallyAction extends NodeAction {
    
    private static final Logger LOGGER = Logger.getLogger(RunInternallyAction.class.getName());
    private final String scriptFilename = "build"+System.currentTimeMillis(); // NOI18N
    
    
    /** Not to show icon in main menu. */
    public RunInternallyAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    /** method performing the action
     * @param nodes selected nodes
     */
    protected void performAction(Node[] nodes) {
        // It can happen that selected class was modified but action status 
        // is changed only when selection changes.
        if(!enable(nodes)) {
            setEnabled(false);
            return;
        }
        // release lock (it may be locked from previous run)
        synchronized (compileLock) {
            try {
                compileLock.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(SwingUtilities.isEventDispatchThread()) {
            // do not block AWT thread
            Thread actionThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        executeSelectedMainClass();
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    }
                }
            });
            actionThread.start();
        } else {
            executeSelectedMainClass();
        }
    }
    
    /** Action is enabled when a main class is selected and it is a class
     * which can be compiled.
     * @param node selected nodes
     * @return true if a compilable main class is selected
     */
    public boolean enable(Node[] node) {
        Lookup context = Utilities.actionsGlobalContext();
        if(getSelectedMainClass(context) != null) {
            DataObject dObj = getSelectedDataObject(context);
            FileObject fObj = dObj.getPrimaryFile();
            FileBuiltQuery.Status status = FileBuiltQuery.getStatus(fObj);
            if(status != null) {
                return true;
            } else {
                return isNBQAFunctional(fObj);
            }
        }
        return false;
    }
    
    /** method returning name of the action
     * @return String name of the action
     */
    public String getName() {
        return NbBundle.getMessage(RunInternallyAction.class, "LBL_RunInternallyAction"); // NOI18N
    }
    
    /** method returning icon for the action
     * @return String path to action icon
     */
    @Override
    protected String iconResource() {
        return "org/netbeans/modules/jemmysupport/resources/runInternally.png"; // NOI18N
    }
    
    /** method returning action Help Context
     * @return action Help Context
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RunInternallyAction.class);
    }
    
    /** Always return false - no need to run asynchronously. */
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    /**************************************************************************/
    private void executeSelectedMainClass() {
        Lookup context = Utilities.actionsGlobalContext();
        DataObject dObj = getSelectedDataObject(context);
        FileObject fObj = dObj.getPrimaryFile();
        String classname = getSelectedMainClass(context);
        
        FileBuiltQuery.Status builtStatus = FileBuiltQuery.getStatus(fObj);
        if(builtStatus == null) {
            executeNBQAFunctional(fObj, classname);
        } else if(builtStatus.isBuilt()) {
            // it is built, so execute
            execute(fObj, classname);
        } else {
            // if not built add listener to wait for the end of compilation
            CompileListener listener = new CompileListener();
            builtStatus.addChangeListener(listener);
            
            String projectName = ProjectUtils.getInformation(FileOwnerQuery.getOwner(fObj)).getName();
            // "myproject (compile-single)"
            String outputTarget = MessageFormat.format(
                    NbBundle.getBundle("org.apache.tools.ant.module.run.Bundle").getString("TITLE_output_target"),
                    new Object[] {projectName, null, "compile-single"});  // NOI18N
            // "Build of myproject (compile-single) failed."
            String failedMessage = MessageFormat.format(
                    NbBundle.getBundle("org.apache.tools.ant.module.run.Bundle").getString("FMT_target_failed_status"),
                    new Object[] {outputTarget});
            
            StatusListener statusListener = new StatusListener(failedMessage);
            StatusDisplayer.getDefault().addChangeListener(statusListener);
            try {
                // try to compile
                // This cannot be called because it is not made public in manifest
                //Actions.compileSingle().actionPerformed(null);
                Project project = FileOwnerQuery.getOwner(fObj);
                ActionProvider ap = (ActionProvider)project.getLookup().lookup(ActionProvider.class );
                ap.invokeAction(ActionProvider.COMMAND_COMPILE_SINGLE, context);
                
                //wait until compilation finishes
                synchronized (compileLock) {
                    // wait max. 30 seconds but it should be released sooner from CompileListener
                    // if compilation succeeded or StatusListener if compilation failed
                    compileLock.wait(30000);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, null, e);
            } finally {
                builtStatus.removeChangeListener(listener);
                StatusDisplayer.getDefault().removeChangeListener(statusListener);
            }
            if(builtStatus.isBuilt()) {
                // finally if really built, execute it
                execute(fObj, classname);
            }
        }
    }
    
    /** Returns selected data object. We expect that only one is selected. If
     * more or none is selected then returns null.
     */
    private DataObject getSelectedDataObject(Lookup context) {
        Collection dataObjects = context.lookup(new Lookup.Template(DataObject.class)).allInstances();
        if(dataObjects != null && dataObjects.size() == 1) {
            // only one object has to be selected
            return (DataObject)dataObjects.iterator().next();
        }
        return null;
    }
    
    /** Returns fully qualified name of class to be executed. It returns null
     * if no suitable class is selected.
     */
    private String getSelectedMainClass(Lookup context) {
        DataObject dObj = getSelectedDataObject(context);
        if(dObj != null) {
            try {
                // inspired in org.netbeans.modules.java.j2seproject.J2SEProjectUtil.hasMainMethod()
                Collection<ElementHandle<TypeElement>> mainClasses = SourceUtils.getMainClasses(dObj.getPrimaryFile());
                if(!mainClasses.isEmpty()) {
                    return mainClasses.iterator().next().getQualifiedName();
                }
            } catch (IllegalArgumentException e) {
                // not a java source
            }
        }
        return null;
    }
    
    private Object compileLock = new Object();
    
    /** Listener to wait for compilation success. */
    class CompileListener implements ChangeListener {
        
        public void stateChanged(ChangeEvent evt) {
            synchronized (compileLock) {
                try {
                    compileLock.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /** Listener to wait for compilation failure. */
    class StatusListener implements ChangeListener {
        private String failedMessage;
        public StatusListener(String failedMessage) {
            this.failedMessage = failedMessage;
        }
        public synchronized void stateChanged(ChangeEvent evt) {
            if(StatusDisplayer.getDefault().getStatusText().equals(failedMessage)) {
                // release lock because compile failed
                synchronized (compileLock) {
                    try {
                        compileLock.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /** Returns build script with run-internally target. */
    private FileObject getScript() {
        File scriptFile = FileUtil.normalizeFile(new File(System.getProperty("java.io.tmpdir"), scriptFilename+".xml")); // NOI18N
        FileObject scriptFO = FileUtil.toFileObject(scriptFile);
        if(scriptFO == null || !scriptFO.isValid()) {
            try {
                FileOutputStream fos = new FileOutputStream(scriptFile);
                FileUtil.copy(this.getClass().getResourceAsStream("build.xml"), fos); // NOI18N
                fos.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
            scriptFO = FileUtil.toFileObject(scriptFile);
            scriptFile.deleteOnExit();
        }
        return scriptFO;
    }
    
    /** Returns EXECUTE classpath of given FileObject. */
    private String getClasspath(FileObject fObj) {
        ClassPath classpath = ClassPath.getClassPath(fObj, ClassPath.EXECUTE);
        StringBuffer result = new StringBuffer();
        for (Iterator it = classpath.entries().iterator(); it.hasNext();) {
            URL entryUrl = ((ClassPath.Entry)it.next()).getURL();
            if("jar".equals(entryUrl.getProtocol())) { // NOI18N
                entryUrl = FileUtil.getArchiveFile(entryUrl);
            }
            result.append(new File(URI.create(entryUrl.toExternalForm())).getAbsolutePath());
            if(it.hasNext()) {
                result.append(File.pathSeparatorChar);
            }
        }
        return result.toString();
    }

    /** Gets IDE's system class loader, adds given classpath and invokes main
     * method of given class. It uses RunInternallyTask.
     */
    private void execute(FileObject fObj, String classname) {
        try {
            String[] targets = {"run-internally"}; // NOI18N
            Properties props = new Properties();
            props.setProperty("run-internally.classname", classname); // NOI18N
            props.setProperty("run-internally.cp", getClasspath(fObj)); // NOI18N
            ExecutorTask task = ActionUtils.runTarget(getScript(), targets, props);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }
    
    /** Executes internal-execution target from test/build.xml. */
    private void executeNBQAFunctional(FileObject fObj, String classname) {
        try {
            String[] targets = {"internal-execution"}; // NOI18N
            Properties props = new Properties();
            props.setProperty("xtest.testtype", "qa-functional"); // NOI18N
            props.setProperty("classname", classname); // NOI18N
            ActionUtils.runTarget(findTestBuildXml(FileOwnerQuery.getOwner(fObj)), targets, props);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }
    
    /** Returns FileObject representing test/build.xml or null if it doesn't exist. */
    private static FileObject findTestBuildXml(Project project) {
        return project.getProjectDirectory().getFileObject("test/build.xml"); // NOI18N
    }
    
    /** Returns true if FileObject is under test/qa-functional/src and 
     * target internal-execution exists in test/build.xml.
     */
    private static boolean isNBQAFunctional(FileObject fo) {
        Project project = FileOwnerQuery.getOwner(fo);
        if(project instanceof NbModuleProject) {
            NbModuleProject nbProject = (NbModuleProject)project;
            String qaFunctionalPath = nbProject.evaluator().getProperty("test.qa-functional.src.dir"); //NOI18N
            FileObject qaFunctionalFo = nbProject.getHelper().resolveFileObject(qaFunctionalPath);
            // if FileObject under test/qa-functional/src
            if(qaFunctionalFo != null && FileUtil.isParentOf(qaFunctionalFo, fo)) {
                FileObject buildXmlFo = findTestBuildXml(project);
                return targetExists(buildXmlFo, "internal-execution"); //NOI18N
            }
        }
        return false;
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
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage(), ex);
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
}

