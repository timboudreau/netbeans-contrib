/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.runinternally;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
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
import org.netbeans.spi.project.ActionProvider;
import org.openide.ErrorManager;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Used to run jemmy test internally in the same JVM as IDE.
 * @author Jiri.Skrivanek@sun.com
 */
public class RunInternallyAction extends NodeAction {
    
    /** method performing the action
     * @param nodes selected nodes
     */
    protected void performAction(Node[] nodes) {
        // release lock (it may be locked form previous run)
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
    
    /** action is enabled when a main class is selected
     * @param node selected nodes
     * @return true if a main class is selected
     */
    public boolean enable(Node[] node) {
        return getSelectedMainClass(Utilities.actionsGlobalContext()) != null;
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
        return "org/netbeans/modules/jemmysupport/runinternally/RunInternallyAction.gif"; // NOI18N
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
            // try to compile
            // this cannot be called because it is not made public in manifest
            //Actions.compileSingle().actionPerformed(null);
            Project project = FileOwnerQuery.getOwner(fObj);
            ActionProvider ap = (ActionProvider)project.getLookup().lookup(ActionProvider.class );
            ap.invokeAction(ActionProvider.COMMAND_COMPILE_SINGLE, context);
            
            if(isSourceOK(dObj)) {
                if(!status.isBuilt()) {
                    // if not built so far, wait until compilation finishes
                    CompileListener listener = new CompileListener();
                    try {
                        status.addChangeListener(listener);
                        synchronized (compileLock) {
                            // wait max. 20 seconds
                            compileLock.wait(20000);
                        }
                    } catch (Exception e) {
                        ErrorManager.getDefault().notify(e);
                    } finally {
                        status.removeChangeListener(listener);
                    }
                }
                if(status.isBuilt()) {
                    // finally if really built, execute it
                    execute(fObj, classname);
                }
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
        if(dObj ==null) {
            return null;
        }
        SourceCookie cookie = (SourceCookie)dObj.getCookie(SourceCookie.class);
        // from class org.netbeans.modules.java.j2seproject.ui.customizer.MainClassChooser
        if (cookie == null) {
            return null;
        }
        // check the main class
        SourceElement source = cookie.getSource();
        ClassElement[] classes = source.getClasses();
        for (int i = 0; i < classes.length; i++) {
            if (classes[i].hasMainMethod()) {
                return classes[i].getName().getFullName();
            }
        }
        return null;
    }
    
    /** Checks whether given data object is parsed and is OK. */
    private boolean isSourceOK(DataObject dObj) {
        SourceCookie cookie = (SourceCookie)dObj.getCookie(SourceCookie.class);
        SourceElement source = cookie.getSource();
        return source.getStatus() == source.STATUS_OK;
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
    public static void execute(FileObject fObj, String classname) {
        try {
            // find NetBeans SystemClassLoader in threads hierarchy
            ThreadGroup tg = Thread.currentThread().getThreadGroup();
            ClassLoader systemClassloader = Thread.currentThread().getContextClassLoader();
            while(!systemClassloader.getClass().getName().endsWith("SystemClassLoader")) { // NOI18N
                tg = tg.getParent();
                if(tg == null) {
                    ErrorManager.getDefault().notify(new Exception("NetBeans SystemClassLoader not found!"));
                }
                Thread[] list = new Thread[tg.activeCount()];
                tg.enumerate(list);
                systemClassloader = list[0].getContextClassLoader();
            }
            URL[] urls = classpathToURL(fObj);
            URLClassLoader testClassLoader = new TestClassLoader(urls, systemClassloader);
            /*
            URL[] u = testClassLoader.getURLs();
            for(int i=0;i<u.length;i++) {
                System.out.println("URL="+u[i]);
            }
            System.out.println("CLASSLOADER="+systemClassloader);
             */
            Class classToRun = testClassLoader.loadClass(classname);
            Method method = classToRun.getDeclaredMethod("main", new Class[] {String[].class}); // NOI18N
            method.invoke(null, new Object[] {null});
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
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
    
    /** Classloder with overriden getPermissions method because it doesn't
     * have sufficient permissions when run from IDE.
     */
    private static class TestClassLoader extends URLClassLoader {
        
        public TestClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        protected PermissionCollection getPermissions(CodeSource codesource) {
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            permissions.setReadOnly();
            
            return permissions;
        }
        
        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.netbeans.jemmy") || name.startsWith("org.netbeans.jellytools")) { // NOI18N
                //System.out.println("CLASSNAME="+name);
                // Do not proxy to parent!
                Class c = findLoadedClass(name);
                if (c != null) return c;
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } else {
                return super.loadClass(name, resolve);
            }
        }
    }
}

