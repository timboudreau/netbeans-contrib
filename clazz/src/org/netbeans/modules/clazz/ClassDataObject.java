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
import java.util.ResourceBundle;
import java.text.MessageFormat;
import javax.swing.JApplet;
import javax.swing.JButton;

import com.netbeans.ide.util.datatransfer.TransferFlavors;
import com.netbeans.ide.util.datatransfer.TransferableOwner;
import com.netbeans.ide.*;
import com.netbeans.ide.classloader.NbClassLoader;
import com.netbeans.ide.cookies.*;
import com.netbeans.ide.debugger.DebuggerInfo;
import com.netbeans.ide.execution.ExecInfo;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.loaders.*;
import com.netbeans.ide.explorer.propertysheet.PropertySheet;
import com.netbeans.ide.util.*;
import com.netbeans.ide.nodes.*;

import com.netbeans.developer.modules.applet.AppletDebuggerInfo;
import com.netbeans.developer.modules.applet.AppletExecInfo;
import com.netbeans.developer.modules.loaders.java.JavaDataObject;


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
public class ClassDataObject extends MultiDataObject
implements DebuggerCookie {
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

  // variables ...................................................................................

  transient protected InstanceSupport instanceSupport;

  transient protected ExecSupport execSupport;

  // constructors ...................................................................................

  /** Constructs a new ClassDataObject */
  public ClassDataObject (final FileObject fo, final ClassDataLoader loader)
  throws com.netbeans.ide.loaders.DataObjectExistsException {
    super (fo, loader);
    initCookies();
  }

  private void initCookies () {
    MultiDataObject.Entry pe = getPrimaryEntry();
    instanceSupport = new InstanceSupport.Origin(pe);
    execSupport = new ExecSupport(pe);
    // asociate cookies (can be slow, do it in non AWT thread)
    RequestProcessor.postRequest(
      new Runnable () {
        public void run () {
          doInitCookies();
        }
      }
    );
  }

  /** Actually performs the work of assigning cookies */
  private void doInitCookies () {
    boolean isExecutable = false;
    try {
      instanceSupport.instanceClass();
      isExecutable = instanceSupport.isExecutable();
    } catch (IOException ex) {
      System.out.println ("Chytam IOExc....");
      return;
    } catch (ClassNotFoundException ex) {
      System.out.println ("Chytam ClassNFExc...");
      return;
    }
    CookieSet cs = getCookieSet();
    cs.add(instanceSupport);
    if (isExecutable)
      cs.add(execSupport);
  }

  private void readObject (java.io.ObjectInputStream is)
  throws java.io.IOException, ClassNotFoundException {
    is.defaultReadObject();
    initCookies();
  }

  // DataObject implementation .............................................

  /** Help context for this object.
  * @return help context
  */
  public HelpCtx getHelpCtx () {
    return new HelpCtx ("com.netbeans.developer.docs.Users_Guide.usergd-using-div-12", "USERGD-USING-TABLE-2");
  }

  /** Getter for copy action.
  * @return true if the object can be copied
  */
  public boolean isCopyAllowed () {
    return isJavaBean ();
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
    if (newName == null) throw new IOException ();
    Object bean;
    try {
      bean = instanceSupport.instanceCreate();
    } catch (ClassNotFoundException ex) {
      throw new IOException (ex.toString ());
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

  /**
  * @return class data node
  */
  protected Node createNodeDelegate () {
    return new ClassDataNode (this);
  }


  // implementation of ExecCookie ..........................................

  /**
  * @return ExecInfo object for execution.
  */
  /*public ExecInfo getExecInfo () {
    try {
      if (isApplet()) return new AppletExecInfo(getPrimaryFile());
    } catch (NbClass.NbClassException e) {
    }
    // build exec info
    String[] args = JavaDataObject.parseParameters(getArgv());
    String className = getPrimaryFile().getPackageName('.');
    return new ExecInfo (className, args) {
      public boolean needsInternalExecution () {
        return getExecution ();
      }
    };
  }*/

  // implementation of ArgumentsCookie ...................................

  /** @return the String arguments to be passed to executed application
  * @deprecated
  */
  /*public String[] getArguments () {
    String[] args;
    StringTokenizer st = new StringTokenizer (getArgv ());
    args = new String[st.countTokens ()];
    int i = 0;
    while (st.hasMoreTokens ()) {
      args[i++] = st.nextToken ();
    }
    return args;
  }*/

  /** @param args the String arguments to be passed to executed application */
  /*public void setArguments (String[] args) {
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
  }*/

  /** @return true if the object can get parameters, false otherwise */
  /*public boolean getArgumentsSupported () {
    return true;
  }*/

  // implementation of DebuggerCookie ...................................

  /** @return The informations needed for debugging. */
  public DebuggerInfo getDebuggerInfo () {
    if (isApplet ()) return new AppletDebuggerInfo(getPrimaryFile());
    // build debugger info
    String[] args = execSupport.getArguments();
    String className = getPrimaryFile().getPackageName('.');
    return new DebuggerInfo (className, args);
  }

  /** @return true if debugging is allowed
  * (exec cookie is present)
  */
  public boolean isDebugAllowed() {
    return getCookie(ExecCookie.class) != null;
  }

  /**
  * Do nothing - source file unavailable.
  */
  public void resolveMethodLine (DebuggerCookie.LineResolver rl) {
  }

  // Properties implementation .....................................................................

  public boolean isInterface () {
    return instanceSupport.isInterface ();
  }

  public Class getSuperclass () throws IOException, ClassNotFoundException {
    return instanceSupport.instanceClass ().getSuperclass ();
  }

  public String getModifiers () throws IOException, ClassNotFoundException {
    return Modifier.toString (instanceSupport.instanceClass().getModifiers());
  }

  public String getClassName () {
    return instanceSupport.instanceName ();
  }

  public Class getBeanClass () throws IOException, ClassNotFoundException {
    return instanceSupport.instanceClass ();
  }

  public boolean isExecutable () {
    return instanceSupport.isExecutable ();
  }

  public boolean isJavaBean () {
    return instanceSupport.isJavaBean();
  }

  public boolean isApplet () {
    return instanceSupport.isApplet ();
  }

  /** Sets parameters of the class
  */
  void setParams (final String params) throws IOException {
    execSupport.setArguments(Utilities.parseParameters(params));
  }

  /** Returns parameters of the class
  */
  String getParams () {
    String[] parArray = execSupport.getArguments();
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < parArray.length; i++) {
      buf.append(parArray[i]);
      if ((i + 1) != parArray.length) buf.append(" ");
    }
    return buf.toString();
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
      // file with the same name exists - ask user what to do
      ResourceBundle bundle = NbBundle.getBundle(this);
      final String rewriteStr = bundle.getString("CTL_Rewrite");
      final String renameStr = bundle.getString("CTL_Rename");
      NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
        new MessageFormat(bundle.getString("MSG_SerExists")).
          format(new Object[] { name, f.getName() }));
      nd.setOptions(new Object[] { rewriteStr, renameStr,
                    NotifyDescriptor.CANCEL_OPTION });
      Object ret = TopManager.getDefault().notify(nd);

      if (NotifyDescriptor.CANCEL_OPTION.equals(ret)) return null;
      String retStr = ((JButton)ret).getText();
      if (rewriteStr.equals(retStr))
        destName = FileUtil.findFreeFileName (
                    f.getPrimaryFile(), destName, ext);
      if (renameStr.equals(retStr)) {
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


  // innerclasses ................................................................................

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
