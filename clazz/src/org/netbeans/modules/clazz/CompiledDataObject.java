/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.applet.Applet;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.Iterator;
import javax.swing.JApplet;
import javax.swing.JButton;

import org.openide.*;
import org.openide.util.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.execution.Executor;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.src.SourceElement;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.SourceElementFilter;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;
import org.netbeans.modules.classfile.ClassFile;

/* TODO:
  - check the showDeclaredOnly flag - it works different for
    variables/constructors than for methods (i.e. for variables/constructors
    the declaredOnly are not subset of notDecalredOnly
*/

/**
* DataObject which represents .class files.
*
* @author Jan Jancura, Ian Formanek, Petr Hamernik, Dafe Simonek
*/
public class CompiledDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -7355104884002106137L;

    /** Name of arguments property. */
    private final static String  PROP_ARGV = "Arguments"; // NOI18N
    /** Name of execution property. */
    private final static String  PROP_EXECUTION = "Execution"; // NOI18N

    // variables ...................................................................................

    /** Support for executing the class */
    transient protected ExecSupport execSupport;

    // constructors ...................................................................................

    /** Constructs a new ClassDataObject */
    public CompiledDataObject(final FileObject fo,final ClassDataLoader loader) throws org.openide.loaders.DataObjectExistsException {
        super (fo, loader);
        initCookies();
    }
    
    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        // only JavaBeans should offer `Customize Bean' action
        cs.add(InstanceCookie.Origin.class, this);
        cs.add(ExecSupport.class, this);
    }
    
    protected ExecSupport createExecSupport() {
        if (execSupport != null)
            return execSupport;
        synchronized (this) {
            if (execSupport == null)
                execSupport = new ExecSupport(getPrimaryEntry());
        }
        return execSupport;
    }
    
    protected Node.Cookie createBeanInstanceSupport() {
	if (isJavaBean()) {
	    return createInstanceSupport();
	} else {
	    return null;
	}
    }
    
    public Node.Cookie createCookie(Class c) {
        if (ExecCookie.class.isAssignableFrom(c)) {
            return createExecSupport();
        } else if (InstanceCookie.class.isAssignableFrom(c)) {
	    return createBeanInstanceSupport();
	}
        return super.createCookie(c);
    }


    /**
    * @return class data node
    */
    protected Node createNodeDelegate () {
        return new CompiledDataNode (this);
    }

    // Properties implementation .....................................................................

    boolean isExecutable () {
        return createInstanceSupport().isExecutable ();
    }

    // other methods ..............................................................................

    /** Check if in specific folder exists .ser fileobject with the same name.
    * If it exists user is asked for confirmation to rewrite, rename or
    * cancel operation. Throws UserCancelException if user pressed cancel
    * button.
    * @param f destination folder
    * @return new Name of file in destination
    */
    protected String existInFolder(DataFolder f) throws UserCancelException {
        FileObject fo = getPrimaryFile();
        String name = fo.getName();
        String ext = "ser"; // NOI18N
        String destName = fo.getName();
        if (f.getPrimaryFile().getFileObject(name, ext) != null) {
            // file with the same name exists - ask user what to do
            ResourceBundle bundle = NbBundle.getBundle(ClassDataObject.class);
            String rewriteStr = bundle.getString("CTL_Rewrite");
            String renameStr = bundle.getString("CTL_Rename");
            String cancelStr = bundle.getString("CTL_Cancel");
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                                      new MessageFormat(bundle.getString("MSG_SerExists")).
                                      format(new Object[] { name, f.getName() }));
            nd.setOptions(new Object[] { rewriteStr, renameStr, cancelStr });
            String retStr = (String)DialogDisplayer.getDefault().notify(nd);
            if (cancelStr.equals(retStr)) // user cancelled the dialog
                throw new UserCancelException();
            if (renameStr.equals(retStr))
                destName = FileUtil.findFreeFileName (
                               f.getPrimaryFile(), destName, ext);
            if (rewriteStr.equals(retStr)) {
                try {
                    FileObject dest = f.getPrimaryFile().getFileObject(name, ext);
                    FileLock lock = dest.lock();
                    dest.delete(lock);
                    lock.releaseLock();
                }
                catch (IOException e) {
                    return null;
                }
            }
        }
        return destName;
    }

    // innerclasses .......................................................
    
    private static class ExecSupport extends org.openide.loaders.ExecSupport {
        ExecSupport(MultiDataObject.Entry en) {
            super(en);
        }
        
        /**
         * Iterates through Execution service type, looking for some exec
         * service from the java module.
         */
        protected Executor defaultExecutor() {
            Lookup.Result servs = Lookup.getDefault().lookup(new Lookup.Template(Executor.class));
            Iterator servsIt = servs.allInstances().iterator();

            while (servsIt.hasNext()) {
                Object o = servsIt.next();
                if (o.getClass().getName().startsWith(
                    "org.netbeans.modules.java.JavaProcessExecutor" // NOI18N
                    )) {
                    return (Executor)o;
                }
            }
            return super.defaultExecutor();
        }
    }
}

