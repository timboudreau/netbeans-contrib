/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
import java.lang.reflect.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import javax.swing.JApplet;
import javax.swing.JButton;

import org.openide.*;
import org.openide.util.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.src.SourceElement;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.SourceElementFilter;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;

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
        MultiDataObject.Entry pe = getPrimaryEntry();
        execSupport = new ExecSupport(pe);
    }

    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        // only JavaBeans should offer `Customize Bean' action
        if (isJavaBean()) {
            cs.add(instanceSupport);
        }
        cs.add(execSupport);
    }

    // DataObject implementation .............................................

    /** Getter for copy action.
    * @return true if the object can be copied
    */
    public boolean isCopyAllowed () {
        boolean isSerializable = false;
        try {
            isSerializable = Serializable.class.isAssignableFrom(instanceSupport.instanceClass());
        } catch (Exception exc) {
            // don't allow copying if some error appeared
            // during serializability test
        }
        return isJavaBean () && isSerializable;
    }

    /** Class DO cannot be moved.
    * @return false
    */
    public boolean isMoveAllowed () {
        return false;
    }

    /** Class DO cannot be renamed.
    * @return false
    */
    public boolean isRenameAllowed () {
        return false;
    }

    /** Copies this object to a folder. The copy of the object is required to
    * be deletable and movable.
    *
    * @param f the folder to copy object to
    * @exception IOException if something went wrong
    * @return the new object
    */
    protected DataObject handleCopy (DataFolder f) throws IOException {
        String newName = existInFolder (f);
        Object bean;
        try {
            bean = instanceSupport.instanceCreate();
        } catch (ClassNotFoundException ex) {
            throw new IOException (ex.toString ());
        }
        if (bean == null) throw new IOException ();
        FileObject serFile = f.getPrimaryFile ().createData (newName, "ser"); // NOI18N
        FileLock lock = null;
        ObjectOutputStream oos = null;
        try {
            lock = serFile.lock ();
            oos = new ObjectOutputStream (serFile.getOutputStream (lock));
            oos.writeObject (bean);
        }
        finally {
            if (lock != null)
                lock.releaseLock ();
            if (oos != null)
                oos.close ();
        }
        return DataObject.find (serFile);
    }
    
    /**
    * @return class data node
    */
    protected Node createNodeDelegate () {
        return new CompiledDataNode (this);
    }

    // Properties implementation .....................................................................

    boolean isExecutable () {
        return instanceSupport == null ? false : instanceSupport.isExecutable ();
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
            String retStr = (String)TopManager.getDefault().notify(nd);
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

    /** The implementation of the source cookie.
    * Class data object cannot implement source cookie directly,
    * because it's optional (if there's no instance cookie, then also
    * no source cookie is supported)
    */
    private static final class SourceSupport extends Object
        implements SourceCookie {
        /** The class which acts as a source element data */
        private Class data;
        /** Reference to outer class */
        private ClassDataObject cdo;

        /** Creates source support with asociated class object */
        SourceSupport (Class data, ClassDataObject cdo) {
            this.data = data;
            this.cdo = cdo;
        }

        /** @return The source element for this class data object */
        public SourceElement getSource () {
            return new SourceElement(new SourceElementImpl(data, cdo));
        }

    } // the end of SourceSupport inner class

    /* PENDING - not reimpl yet
    static class BeanTransferableOwner extends TransferableOwner.Filter {

      String beanName;

      BeanTransferableOwner (
        TransferableOwner transferable,
        String beanName,
        Class beanClass
      ) {
        super (
          transferable,
          new DataFlavor[] {new TransferFlavors.BeanFlavor (beanClass)}
        );
        this.beanName = beanName;
      } */

    /** Creates transferable data for this flavor.
    */
    /*
    public Object getTransferData (DataFlavor flavor)
    throws UnsupportedFlavorException, IOException {
      if (isDataFlavorSupported(flavor)) {
        if (flavor instanceof TransferFlavors.BeanFlavor) return beanName;
        return super.getTransferData (flavor);
      }
      else {
        // not supported flavor
        throw new UnsupportedFlavorException (flavor);
      }
}
} */

}

/*
 * Log
 *  29   Gandalf   1.28        1/20/00  David Simonek   #2119 bugfix
 *  28   Gandalf   1.27        1/18/00  David Simonek   Execution now correctly 
 *       disabled for ser data nodes
 *  27   Gandalf   1.26        1/13/00  David Simonek   i18n
 *  26   Gandalf   1.25        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  25   Gandalf   1.24        8/18/99  Jaroslav Tulach #2641
 *  24   Gandalf   1.23        7/25/99  Ian Formanek    Fixed bug #2745 - 
 *       Property "Class Name" of serialized prototypes displays the file name 
 *       rather than the name of the class that is serialized in it.
 *  23   Gandalf   1.22        7/16/99  Petr Jiricka    Fixed bug that classes 
 *       without main couldn't be executed
 *  22   Gandalf   1.21        7/9/99   Petr Hrebejk    Add/emove mehods made 
 *       synchronized
 *  21   Gandalf   1.20        6/28/99  Petr Hrebejk    Multiple node factories 
 *       added
 *  20   Gandalf   1.19        6/24/99  Jesse Glick     Gosh-honest HelpID's.
 *  19   Gandalf   1.18        6/22/99  Ian Formanek    employed DEFAULT_HELP
 *  18   Gandalf   1.17        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  17   Gandalf   1.16        4/8/99   David Simonek   obscure dialog bugfix 
 *       (#1411)
 *  16   Gandalf   1.15        4/2/99   Jan Jancura     ObjectBrowser support 
 *       II.
 *  15   Gandalf   1.14        4/1/99   Ian Formanek    Rollback to make it 
 *       compilable
 *  14   Gandalf   1.13        4/1/99   Jan Jancura     Object browser support
 *  13   Gandalf   1.12        3/26/99  Ian Formanek    
 *  12   Gandalf   1.11        3/26/99  Ian Formanek    Fixed use of obsoleted 
 *       NbBundle.getBundle (this)
 *  11   Gandalf   1.10        3/3/99   Jaroslav Tulach Uses ExecSupport to 
 *       provide DebuggerCookie
 *  10   Gandalf   1.9         2/5/99   David Simonek   
 *  9    Gandalf   1.8         2/3/99   David Simonek   
 *  8    Gandalf   1.7         2/1/99   David Simonek   
 *  7    Gandalf   1.6         1/26/99  David Simonek   util.Task used for 
 *       synchronization
 *  6    Gandalf   1.5         1/22/99  David Simonek   synchronization problems
 *       concerning getCookie repaired
 *  5    Gandalf   1.4         1/20/99  David Simonek   rework of class DO
 *  4    Gandalf   1.3         1/19/99  David Simonek   
 *  3    Gandalf   1.2         1/13/99  David Simonek   
 *  2    Gandalf   1.1         1/6/99   Ian Formanek    Reflecting change in 
 *       datasystem package
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 * Beta Change History:
 *  0    Tuborg    0.20        --/--/98 Jan Formanek    SWITCHED TO NODES
 *  0    Tuborg    0.21        --/--/98 Jan Formanek    bugfix
 *  0    Tuborg    0.22        --/--/98 Jan Formanek    added property showDeclaredOnly
 *  0    Tuborg    0.23        --/--/98 Jan Formanek    employed PropertySupport.ReadOnly, ...
 *  0    Tuborg    0.24        --/--/98 Petr Hamernik   initializing of subnodes improvements
 *  0    Tuborg    0.25        --/--/98 Jan Formanek    icon change
 *  0    Tuborg    0.26        --/--/98 Jan Formanek    checks if the class has the right main method in
 *  0    Tuborg    0.26        --/--/98 Jan Formanek    isExecutionAllowed
 *  0    Tuborg    0.27        --/--/98 Jan Formanek    different icon for classes with main() method
 *  0    Tuborg    0.28        --/--/98 Jan Formanek    NotSerializable bug fixed (BUG ID: 03210062)
 *  0    Tuborg    0.29        --/--/98 Jan Formanek    default is showDeclaredOnly
 *  0    Tuborg    0.31        --/--/98 Jan Jancura     default is showDeclaredOnly
 *  0    Tuborg    0.32        --/--/98 Jan Jancura     moved to propertySet
 *  0    Tuborg    0.34        --/--/98 Ales Novak      handler joined
 *  0    Tuborg    0.36        --/--/98 Jan Formanek    isExecAllowed from ExecCookie added, isExecutionAllowed removed
 *  0    Tuborg    0.36        --/--/98 Jan Formanek    CustomizeBeanCookie implementation
 *  0    Tuborg    0.37        --/--/98 Jan Jancura     exception in containsMain...
 *  0    Tuborg    0.38        --/--/98 Jaroslav Tulach defaultClipboardCut moved to ClassDataNode
 *  0    Tuborg    0.41        --/--/98 Jan Jancura     bugfix
 *  0    Tuborg    0.44        --/--/98 Ales Novak      applets support
 *  0    Tuborg    0.45        --/--/98 Jan Formanek    bugfix in isApplet () (thrown ClassFormatError if the class was in
 *  0    Tuborg    0.45        --/--/98 Jan Formanek    a wrong package)
 */
