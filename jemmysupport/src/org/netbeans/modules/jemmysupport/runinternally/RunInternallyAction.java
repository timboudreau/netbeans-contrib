/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.runinternally;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.jemmysupport.Utils;
import org.netbeans.spi.project.ActionProvider;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/** Used to run jemmy test internally in the same JVM as IDE.
 * @author Jiri.Skrivanek@sun.com
 */
public class RunInternallyAction extends NodeAction {

    private InputOutput io;
    private boolean started = false;

    /** Not to show icon in main menu. */
    public RunInternallyAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    /** method performing the action
     * @param nodes selected nodes
     */
    protected void performAction(Node[] nodes) {
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
                    }
                    catch (Exception ex) {
                        ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
                    }
                }
            });
            actionThread.start();
        } else {
            executeSelectedMainClass();
        }
    }
    
    /** action is enabled when a main class is selected and it is a class
     * in regular java project.
     * @param node selected nodes
     * @return true if a main class is selected
     */
    public boolean enable(Node[] node) {
        if(started) {
            return false;
        }
        Lookup context = Utilities.actionsGlobalContext();
        if(getSelectedMainClass(context) != null) {
            DataObject dObj = getSelectedDataObject(context);
            FileObject fObj = dObj.getPrimaryFile();
            FileBuiltQuery.Status status = FileBuiltQuery.getStatus(fObj);
            return status != null;
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
    protected boolean asynchronous() {
        return false;
    }
    
    /**************************************************************************/
    private void executeSelectedMainClass() {
        Lookup context = Utilities.actionsGlobalContext();
        DataObject dObj = getSelectedDataObject(context);
        FileObject fObj = dObj.getPrimaryFile();
        String classname = getSelectedMainClass(context);
        
        FileBuiltQuery.Status status = FileBuiltQuery.getStatus(fObj);
        if(!status.isBuilt()) {
            // if not built add listener to wait for the end of compilation
            CompileListener listener = new CompileListener();
            status.addChangeListener(listener);

            // try to compile
            // This cannot be called because it is not made public in manifest
            //Actions.compileSingle().actionPerformed(null);
            Project project = FileOwnerQuery.getOwner(fObj);
            ActionProvider ap = (ActionProvider)project.getLookup().lookup(ActionProvider.class );
            ap.invokeAction(ActionProvider.COMMAND_COMPILE_SINGLE, context);

            //wait until compilation finishes
            // TODO - possibly wait for status text (failed or finished)
            try {
                synchronized (compileLock) {
                    // wait max. 30 seconds
                    compileLock.wait(30000);
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                status.removeChangeListener(listener);
            }
            if(status.isBuilt()) {
                // finally if really built, execute it
                execute(fObj, classname);
            }
        } else {
            // it is built, so execute
            execute(fObj, classname);
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
        if(dObj == null) {
            return null;
        }
        FileObject fObj = dObj.getPrimaryFile();
        // following code taken from org.netbeans.modules.java.j2seproject.J2SEProjectUtil.hasMainMethod()
        JavaModel.getJavaRepository().beginTrans(false);
        try {
            JavaModel.setClassPath(fObj);
            Resource res = JavaModel.getResource(fObj);
            if(res == null) {
                return null;
            }
            if(!res.getMain().isEmpty()) {
                return ((JavaClass)res.getMain().get(0)).getName();
            }
        } finally {
            JavaModel.getJavaRepository ().endTrans ();
        }
        return null;
    }
    
    private Object compileLock = new Object();
    
    /** Listener to wait for compilation. */
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

    /** Gets IDE's system class loader, adds given classpath and invokes main
     * method of given class.
     * @throws BuildException when something's wrong
     */
    private void execute(FileObject fObj, String classname) {
        String displayName = classname.substring(classname.lastIndexOf('.')+1)+" (run-internally)";
        try {
            URL[] urls = classpathToURL(fObj);
            URLClassLoader testClassLoader = new Utils.TestClassLoader(urls, Utils.getSystemClassLoader());
            /*
            URL[] u = testClassLoader.getURLs();
            for(int i=0;i<u.length;i++) {
                System.out.println("URL="+u[i]);
            }
            System.out.println("CLASSLOADER="+systemClassloader);
             */
            try {
                redirectOutput(displayName, testClassLoader);
            } catch (Exception e) {
                // ignore exception (e.g. when jemmy not available)
            }
            Class classToRun = testClassLoader.loadClass(classname);
            Method method = classToRun.getDeclaredMethod("main", new Class[] {String[].class}); // NOI18N
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                    RunInternallyAction.class, "LBL_Running", displayName));
            started = true;
            enableAction(false);
            method.invoke(null, new Object[] {null});
        } catch (ClassNotFoundException cnfe) {
            // compilation probably failed and we ignore it because I don't know
            // how to check the result of compilation for workaround 43609
        } catch (Exception e) {
            if(e.getCause() != null) {
                e.getCause().printStackTrace(io.getErr());
            } else {
                e.printStackTrace(io.getErr());
            }
        } finally {
            started = false;
            enableAction(true);
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                    RunInternallyAction.class, "LBL_Finished", displayName));
        }
    }

    /** Get output tab and redirect jemmy outputs to that tab.
     * @param displayName display name of output tab
     * @param testClassLoader classloader to used
     */
    private void redirectOutput(String displayName, URLClassLoader testClassLoader) throws Exception {
        InputOutput ioNew = IOProvider.getDefault().getIO(displayName, false);
        if(io != null && io != ioNew) {
            // close previous tab if has different display name
            io.closeInputOutput();
        }
        io = ioNew;
        io.getOut().reset();
        io.getErr().reset();
        io.select();
        // Do the following using reflection and system classloader
        // JemmyProperties.setCurrentOutput(new TestOut(null, (PrintWriter)io.getOut(), (PrintWriter)io.getErr()));
        Class testOutClass = testClassLoader.loadClass("org.netbeans.jemmy.TestOut");  // NOI18N
        Constructor constr = testOutClass.getDeclaredConstructor(new Class[] {InputStream.class, PrintWriter.class, PrintWriter.class});
        Object testOut = constr.newInstance(new Object[] {null, (PrintWriter)io.getOut(), (PrintWriter)io.getErr()});

        Class jemmyPropClass = testClassLoader.loadClass("org.netbeans.jemmy.JemmyProperties");  //NOI18N
        Method setCurrentOutputMethod = jemmyPropClass.getDeclaredMethod("setCurrentOutput", new Class[] {testOutClass}); // NOI18N
        setCurrentOutputMethod.invoke(null, new Object[] {testOut});
    }

    /** Helper method to enable or disable this action.
     * @param state true or false
     */
    private void enableAction(final boolean state) {
        if(SwingUtilities.isEventDispatchThread()) {
            setEnabled(state);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setEnabled(state);
                }
            });
        }
    }
    
    /**
     * Finds execute classpath of given file object and converts it to an array of URLs.
     * @return array of URLs
     */
    public static URL[] classpathToURL(FileObject fObj) {
        ClassPath classpath = ClassPath.getClassPath(fObj, ClassPath.EXECUTE);
        //System.out.println("CLASSPATH="+classpath);
        ArrayList list = new ArrayList();
        for (Iterator it = classpath.entries().iterator(); it.hasNext();) {
            ClassPath.Entry entry = (ClassPath.Entry) it.next();
            //System.out.println("URL="+entry.getURL());
            list.add(entry.getURL());
        }
        return (URL[])list.toArray(new URL[list.size()]);
    }
}

