/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

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
import javax.swing.JApplet;

import com.netbeans.ide.util.datatransfer.TransferFlavors;
import com.netbeans.ide.util.datatransfer.TransferableOwner;
import com.netbeans.ide.*;
import com.netbeans.ide.classloader.NbClassLoader;
import com.netbeans.ide.classloader.NbClass;
import com.netbeans.ide.cookies.*;
import com.netbeans.ide.debugger.DebuggerInfo;
import com.netbeans.ide.execution.ExecInfo;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.loaders.UniFileDataObject;
import com.netbeans.ide.explorer.propertysheet.PropertySheet;
import com.netbeans.ide.util.*;
import com.netbeans.ide.nodes.*;

/* TODO:
  - check the showDeclaredOnly flag - it works different for
    variables/constructors than for methods (i.e. for variables/constructors
    the declaredOnly are not subset of notDecalredOnly
*/

/**
* DataObject which represents .class files.
*
* @author Jan Jancura, Ian Formanek, Petr Hamernik
* @version 0.45, May 17, 1998
*/
public class ClassDataObject extends UniFileDataObject
implements ExecCookie, CustomizeBeanCookie, DebuggerCookie, ArgumentsCookie {
  /** generated Serialized Version UID */
  static final long serialVersionUID = -7355104884002106137L;

  /** constant for applet */
  private static final int UNDECIDED = 0;

  /** constant for applet */
  private static final int APPLET = 1;

  /** constant for applet */
  private static final int APPLICATION = 2;

  /** Name of "arguments" property. */
  private final static String  PROP_ARGV = "Arguments";
  /** Name of "execution" property. */
  private final static String  PROP_EXECUTION = "Execution";

  /** bundle to obtain text information from */
  private static java.util.ResourceBundle bundle =
    com.netbeans.ide.util.NbBundle.getBundle ("com.netbeans.developer.modules.locales.LoadersClazzBundle");


  // variables ...................................................................................

  transient protected NbClass beanClass = null;


  // constructors ...................................................................................

  /** Constructs a new ClassDataObject */
  public ClassDataObject (FileObject fo)
  throws com.netbeans.ide.loaders.DataObjectExistsException {
    super (fo);
    beanClass = new NbClass (fo);
  }

  private void readObject (java.io.ObjectInputStream is)
  throws java.io.IOException, ClassNotFoundException {
    is.defaultReadObject();
    beanClass = new NbClass (getPrimaryFile ());
  }

  // DataObject implementation .....................................................................

  /** Help context for this object.
  * @return help context
  */
  public HelpCtx getHelpCtx () {
    return new HelpCtx ("com.netbeans.developer.docs.Users_Guide.usergd-using-div-12", "USERGD-USING-TABLE-2");
  }

  /** Getter for delete action.
  * @return true if the object can be deleted
  */
  public boolean isDeleteAllowed () {
    return !getPrimaryFile ().isReadOnly ();
  }

  /** Getter for copy action.
  * @return true if the object can be copied
  */
  public boolean isCopyAllowed () {
    try {
      return isJavaBean ();
    } catch (NbClass.NbClassException e) {
      return false;
    }
  }

  /** Getter for move action.
  * @return true if the object can be moved
  */
  public boolean isMoveAllowed () {
    return false;
  }

  /** Getter for rename action.
  * @return true if the object can be renamed
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
    if (newName == null) throw new IOException ();
    Object bean;
    try {
      bean = beanClass.getInstance ();
    } catch (NbClass.NbClassException e) {
      throw new IOException (e.toString ());
    }
    if (bean == null) throw new IOException ();
    FileObject serFile = f.getPrimaryFile ().createData (newName, "ser");
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

  /** Provides node that should represent this data object. When a node for representation
  * in a parent is requested by a call to getNode (parent) it is the exact copy of this node
  * with only parent changed. This implementation creates instance
  * <CODE>DataNode</CODE>.
  * <P>
  * This method is called only once.
  *
  * @return the node representation for this data object
  * @see DataNode
  */
  protected Node createNodeDelegate () {
    return new ClassDataNode (this);
  }


  // implementation of ExecCookie ..........................................

  /**
  * @return ExecInfo object for execution.
  */
  public ExecInfo getExecInfo () {
    try {
      if (isApplet()) return new com.netbeans.developer.modules.applet.AppletExecInfo(getPrimaryFile());
    } catch (NbClass.NbClassException e) {
    }
    String[] args = com.netbeans.developer.modules.loaders.java.JavaDataObject.parseParameters(getArgv());
    String className = getPrimaryFile().getPackageName('.');
    return new ExecInfo (className, args) {
      public boolean needsInternalExecution () {
        return getExecution ();
      }
    };
  }

  /**
  * @return true, if the object is in a right state to be
  *    executed, false if the execution cannot be performed at this moment
  */
  public boolean isExecAllowed () {
    try {
      return beanClass.isExecutable () || beanClass.isApplet ();
    } catch (NbClass.NbClassException e) {
      return false;
    }
  }


  // implementation of ArgumentsCookie ...................................

  /** @return the String arguments to be passed to executed application
  * @deprecated
  */
  public String[] getArguments () {
    String[] args;
    StringTokenizer st = new StringTokenizer (getArgv ());
    args = new String[st.countTokens ()];
    int i = 0;
    while (st.hasMoreTokens ()) {
      args[i++] = st.nextToken ();
    }
    return args;
  }

  /** @param args the String arguments to be passed to executed application */
  public void setArguments (String[] args) {
    StringBuffer sb = new StringBuffer ();
    for (int i = 0; i < args.length; i++) {
      sb.append (args[i]);
      if (i != args.length - 1) sb.append (" ");
    }
    try {
      setArgv (sb.toString ());
    } catch (PropertyVetoException e) {
      // arguments vetoed -> cannot do anything, ignore it
    }
    firePropertyChange ("arguments", null, null);
  }

  /** @return true if the object can get parameters, false otherwise */
  public boolean getArgumentsSupported () {
    return true;
  }

  // implementation of DebugCookie ..........................................

  /** @return The informations needed for debugging. */
  public DebuggerInfo getDebuggerInfo () {
    try {
      if (isApplet ()) return new com.netbeans.developer.modules.applet.AppletDebuggerInfo(getPrimaryFile());
    } catch (NbClass.NbClassException e) {
    }
    ExecInfo ei = getExecInfo();
    return new DebuggerInfo (ei.getClassName(), ei.getArguments());
  }

  /** @return The same value as isExecAllowed ().
  */
  public boolean isDebugAllowed() {
    return isExecAllowed ();
  }

  /**
  * Do nothing - source file unavailable.
  */
  public void resolveMethodLine (DebuggerCookie.LineResolver rl) {
  }


  // implementation of CustomizeBeanCookie ..................................

  /**
  * @return Retuns the bean to customize.
  */
  public Object getJavaBean () {
    Object bean = null;
    try {
      bean = beanClass.getInstance ();
    } catch (Exception e) {
      TopManager.getDefault().notify(
        new NotifyDescriptor.Exception(e,
                                       bundle.getString("EXC_Introspection"))
        );
    }

    return bean;
  }

  /**
  * Serialize JavaBean.
  * @param javaBean Bean to serialize.
  * @param toAnotherPlace If true => create new ser file.
  * @return true after succesfull serialization.
  */
  public boolean serializeJavaBean (Object javaBean, boolean toAnotherPlace) {

    FileObject parent, serFile = null;
    String name;

    if (toAnotherPlace) {
      // Create component for for file name input
      DataObject.InputPanel p = new DataObject.InputPanel ();

      DataSystem ds = new DataSystem (new DataFilter () {
        /** Does the data object should be displayed or not?
        * @param obj the data object
        * @return <CODE>true</CODE> if the object should be displayed,
        *    <CODE>false</CODE> otherwise
        */
        public boolean acceptDataObject (DataObject obj) {
          // accept only data folders but ignore read only roots of file systems
          return (
            obj instanceof DataFolder &&
            (
              !obj.getPrimaryFile ().isReadOnly () ||
              obj.getPrimaryFile ().getParent () != null
            )
          );
        }
      });

      try {
        // selects one folder from data systems
        DataFolder df;
        Node.Cookie scookie = TopManager.getDefault ().getNodeOperation ().select (
          bundle.getString ("CTL_SerializeAs"),
          bundle.getString ("CTL_SaveIn"),
          ds, new NodeAcceptor () {
            public boolean acceptNodes (Node[] nodes) {
              if ((nodes == null) || (nodes.length == 0))
                return false;
              Node.Cookie cookie = nodes[0].getCookie ();
              return
                nodes.length == 1 &&
                Cookies.isInstanceOf (cookie, DataFolder.class) &&
                !((DataFolder)(Cookies.getInstanceOf (cookie, DataFolder.class))).getPrimaryFile ().isReadOnly ();
            }
          }, p
        )[0].getCookie();
        // can't direct cast  - see compound cookie
        df = (DataFolder) Cookies.getInstanceOf(scookie, DataFolder.class);
        parent = df.getPrimaryFile ();
        name = p.getText ();
      } catch (com.netbeans.ide.util.UserCancelException ex) {
        return false;
      }
    } else {
      parent = getPrimaryFile ().getParent ();
      name = getPrimaryFile ().getName ();
    }

    ByteArrayOutputStream baos = null;
    ObjectOutputStream oos = null;
    OutputStream os = null;
    FileLock lock = null;
    try {
      oos = new java.io.ObjectOutputStream (baos = new ByteArrayOutputStream ());
      oos.writeObject (javaBean);
      if ((serFile = parent.getFileObject (name, "ser")) == null)
        serFile = parent.createData (name, "ser");
      if (serFile == null) return false;
      lock = serFile.lock ();
      oos.close ();
      baos.writeTo (os = serFile.getOutputStream (lock));
    } catch (Exception e) {
      TopManager.getDefault ().notify (
        new NotifyDescriptor.Exception (e, bundle.getString ("EXC_Serialization") + " " + 
          parent.getPackageName ('.') + '.' + name)
      );
      return false;
    }
    finally {
      if (lock != null)
        lock.releaseLock ();
      try {
        if (os != null)
          os.close ();
      } catch (Exception e) {
        TopManager.getDefault ().notify (
          new NotifyDescriptor.Exception (e, bundle.getString("EXC_Serialization") + " " + 
            serFile.getPackageName ('.'))
        );
        return false;
      }
    }
    return true;
  }

  /**
  * @return true if the customization is currently allowed.
  */
  public boolean isCustomizationAllowed () {
    try {
      return beanClass.isJavaBean ();
    } catch (NbClass.NbClassException e) {
      return false;
    }
  }


  // Properties implementation .....................................................................

  public void setParams (String params) throws java.beans.PropertyVetoException {
    setArgv(params);
  }

  public String getParams () {
    return getArgv();
  }

  public boolean isInterface () throws NbClass.NbClassException  {
    return beanClass.isInterface ();
  }

  public Class getSuperclass () throws NbClass.NbClassException  {
    return beanClass.getClazz ().getSuperclass ();
  }

  public String getModifiers () throws NbClass.NbClassException  {
    return Modifier.toString (beanClass.getClazz ().getModifiers());
  }

  public String getClassName () throws NbClass.NbClassException  {
    return beanClass.getClazz ().getName ();
  }

  public Class getBeanClass () throws NbClass.NbClassException {
    return beanClass.getClazz ();
  }

  public boolean getHasMainMethod () throws NbClass.NbClassException  {
    return beanClass.isExecutable ();
  }

  public boolean isJavaBean () throws NbClass.NbClassException  {
    return beanClass.isJavaBean();
  }

  /** @return true if the class is a descendant of the Applet or JApplet
  */
  public boolean isApplet () throws NbClass.NbClassException {
    return beanClass.isApplet ();
  }

  /**
  * Sets arguments for the class object.
  * It uses template attribute of primary file.
  *
  * @param argv arguments of class object
  * @exception PropertyVetoException if it is not possible to set the attribute.
  */
  public void setArgv(String argv) throws PropertyVetoException {
    try {
      FileObject fo = getPrimaryFile();
      FileLock lock = fo.lock();
      if (lock == null) throw new IOException ();
      getPrimaryFile().setAttribute(lock, PROP_ARGV, argv);
      lock.releaseLock();
    }
    catch (IOException e) {
      throw new PropertyVetoException(e.getMessage(),
        new PropertyChangeEvent(this, PROP_ARGV, "", argv));
    }
  }

  /**
  * Getter for argument option. Default value is ""
  *
  * @return arguments of class object
  */
  public String getArgv() {
    Object o = getPrimaryFile().getAttribute(PROP_ARGV);
    if (o instanceof String)
      return (String) o;
    else
      return "";
  }

  /**
  * Sets whether to use internal execution or not.
  *
  * @param internal true if the object should be executed by internal execution
  * @exception IOException on an error
  */
  void setExecution(boolean internal) throws IOException {
    FileObject fo = getPrimaryFile();
    FileLock lock = fo.lock();
    if (lock == null) throw new IOException ();
    getPrimaryFile().setAttribute(lock, PROP_EXECUTION, new Boolean (internal));
    lock.releaseLock();
  }

  /**
  * @return true if the object should be executed internally, false otherwise
  */
  boolean getExecution () {
    Object o = getPrimaryFile().getAttribute(PROP_EXECUTION);
    return o instanceof Boolean ? ((Boolean)o).booleanValue () : false;
  }


  // other methods ..............................................................................

  /** Check if in specific folder exists .ser fileobject with the same name.
  * If it exists user is asked for confirmation to rewrite, rename or cancel operation.
  * @param f destination folder
  * @return new Name of file in destination folder or null if operation should be
  *     canceled.
  */
  private String existInFolder(DataFolder f) {
    FileObject fo = getPrimaryFile();
    String name = fo.getName();
    String ext = "ser";
    String destName = fo.getName();

    if (f.getPrimaryFile().getFileObject(name, ext) != null) {
      int ret = TopManager.getDefault().getConfirmation().confirmRewriteObject(
        f.getName(), name);
      switch (ret) {
        case 0: destName = findFreeFileName (f, destName, ext);
                break;
        case 1: try {
                  FileObject dest = f.getPrimaryFile().getFileObject(name, ext);
                  FileLock lock = dest.lock();
                  dest.delete(lock);
                  lock.releaseLock();
                }
                catch (IOException e) {
                  return null;
                }
                break;
        case 2: return null;
      }
    }
    return destName;
  }


  // innerclasses ................................................................................

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
    }

    /** Creates transferable data for this flavor.
    */
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
  }
}

/*
 * Log
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
