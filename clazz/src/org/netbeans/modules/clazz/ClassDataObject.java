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
public class ClassDataObject extends MultiDataObject implements ElementCookie {
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

  /** Support for working with class */
  transient protected InstanceSupport instanceSupport;
  /** Support for executing the class */
  transient protected ExecSupport execSupport;
  /** The flag for recognizing if cookies are initialized or not */
  transient private boolean cookiesInitialized = false;

  // constructors ...................................................................................

  /** Constructs a new ClassDataObject */
  public ClassDataObject (final FileObject fo, final ClassDataLoader loader)
  throws org.openide.loaders.DataObjectExistsException {
    super (fo, loader);
    MultiDataObject.Entry pe = getPrimaryEntry();
    instanceSupport = new InstanceSupport.Origin(pe);
    execSupport = new ExecSupport(pe);
  }

  /** Performs cookie initialization. */
  private void initCookies () {
    // asociate cookies
    //boolean isExecutable = false;
    Class ourClass = null;
    try {
      ourClass = instanceSupport.instanceClass();
      //isExecutable = instanceSupport.isExecutable();
    } catch (IOException ex) {
      return;
    } catch (ClassNotFoundException ex) {
      return;
    }
    CookieSet cs = getCookieSet();
    cs.add(instanceSupport);
    //if (isExecutable)
    cs.add(execSupport);
    cs.add(new SourceSupport(ourClass));
  }

  /** Overrides superclass getCookie.<P>
  * Lazily initialize cookies. (When they are requested for the first time)
  */
  public Node.Cookie getCookie (Class type) {
    if (!cookiesInitialized) {
      cookiesInitialized = true;
      initCookies();
    }
    return super.getCookie(type);
  }

  private void readObject (java.io.ObjectInputStream is)
  throws java.io.IOException, ClassNotFoundException {
    is.defaultReadObject();
    cookiesInitialized = false;
  }

  // DataObject implementation .............................................

  /** Help context for this object.
  * @return help context
  */
  public HelpCtx getHelpCtx () {
    return new HelpCtx (ClassDataObject.class);
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


  // implementation of ElementCookie ..........................................

  /**
   * Get the alternate node representation.
   * @return the node
   * @see org.openide.loaders.DataObject#getNodeDelegate
  */
  public Node getElementsParent () {
    
    /* Changed for multiple factories
    ClassElementNodeFactory cef = new ClassElementNodeFactory ();
    cef.setGenerateForTree (true);
    */
    ElementNodeFactory cef = getBrowserFactory();
    SourceChildren sourceChildren = new SourceChildren (cef);
    SourceElementFilter sourceElementFilter = new SourceElementFilter();
    sourceElementFilter.setAllClasses (true);
    sourceChildren.setFilter (sourceElementFilter);
    try {
      Class ourClass = instanceSupport.instanceClass();
      sourceChildren.setElement (new SourceElement(new SourceElementImpl(ourClass)));
    } catch (IOException ex) {
    } catch (ClassNotFoundException ex) {
    }

    AbstractNode alteranteParent = new AbstractNode (sourceChildren);
    CookieSet cs = alteranteParent.getCookieSet();
    cs.add (sourceChildren);

    return alteranteParent;
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
    try {
      return instanceSupport.instanceClass ().getName ();
    } catch (Exception e) {
      // ignore and return the instance name from the InstanceSupport, 
      // which is by default the file name of the original instance
    }
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
    getPrimaryFile().setAttribute(PROP_EXECUTION, new Boolean (internal));
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
  * If it exists user is asked for confirmation to rewrite, rename or
  * cancel operation. Throws UserCancelException if user pressed cancel
  * button.
  * @param f destination folder
  * @return new Name of file in destination
  */
  private String existInFolder(DataFolder f) throws UserCancelException {
    FileObject fo = getPrimaryFile();
    String name = fo.getName();
    String ext = "ser";
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

  // =============== The mechanism for regeisteing node factories ==============

  private static ArrayList explorerFactories = new ArrayList();
  private static ArrayList browserFactories = new ArrayList();

  static {
    explorerFactories.add( new ClassElementNodeFactory() );

    ClassElementNodeFactory cef = new ClassElementNodeFactory();
    cef.setGenerateForTree (true);

    browserFactories.add( cef ) ;
  }

  public static void addExplorerFilterFactory( FilterFactory factory ) {
    addFactory( explorerFactories, factory );
  }

  public static void removeExplorerFilterFactory( FilterFactory factory ) {
    removeFactory( explorerFactories, factory );
  }

  static ElementNodeFactory getExplorerFactory() {
    return (ElementNodeFactory)explorerFactories.get( explorerFactories.size() - 1); 
  }

  public static void addBrowserFilterFactory( FilterFactory factory ) { 
    addFactory( browserFactories, factory );
  }

  public static void removeBrowserFilterFactory( FilterFactory factory ) {
    removeFactory( browserFactories, factory );
  }

  static ElementNodeFactory getBrowserFactory() {
    return (ElementNodeFactory)browserFactories.get( browserFactories.size() - 1 ); 
  }

  private static synchronized void addFactory( List factories, FilterFactory factory ) {
    factory.attachTo( (ElementNodeFactory)factories.get( factories.size() - 1 ) );
    factories.add( factory );
  }

  private static synchronized void removeFactory( List factories, FilterFactory factory ) {
    int index = factories.indexOf( factory );

    if ( index <= 0 )
      return;
    else if ( index == factories.size() - 1 )
      factories.remove( index );
    else {
      ((FilterFactory)factories.get( index + 1 )).attachTo( (ElementNodeFactory)factories.get( index - 1 ) );
      factories.remove( index );
    }

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

    /** Creates source support with asociated class object */
    SourceSupport (final Class data) {
      this.data = data;
    }

    /** @return The source element for this class data object */
    public SourceElement getSource () {
      return new SourceElement(new SourceElementImpl(data));
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
