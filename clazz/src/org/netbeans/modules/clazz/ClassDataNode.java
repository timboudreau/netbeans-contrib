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

import java.awt.Image;
import java.awt.Toolkit;
import java.lang.reflect.*;
import java.util.Vector;
import java.util.ResourceBundle;
import java.io.*;
import javax.swing.SwingUtilities;

import com.netbeans.ide.nodes.*;
import com.netbeans.ide.TopManager;
import com.netbeans.ide.loaders.DataNode;
import com.netbeans.ide.util.datatransfer.ExTransferable;
import com.netbeans.ide.util.RequestProcessor;
import com.netbeans.ide.util.NbBundle;
import com.netbeans.ide.src.*;
import com.netbeans.ide.src.nodes.SourceChildren;
import com.netbeans.ide.src.nodes.DefaultFactory;
import com.netbeans.ide.cookies.SourceCookie;

/** Represents ClassDataObject
*
* @author Ales Novak, Ian Formanek, Jan Jancura, Dafe Simonek
*/
class ClassDataNode extends DataNode implements Runnable {
  /** generated Serialized Version UID */
  static final long serialVersionUID = -1543899241509520203L;

  /** Properties */
  private final static String PROP_CLASS_NAME = "className";
  private final static String PROP_IS_JAVA_BEAN = "isJavaBean";
  private final static String PROP_IS_APPLET = "isApplet";
  private final static String PROP_IS_EXECUTABLE = "isExecutable";
  private final static String PROP_FILE_PARAMS = "fileParams";
  private final static String PROP_EXECUTION = "execution";

  private final static String EXECUTION_SET_NAME     = "Execution";

  /** Icon bases for icon manager */
  private final static String CLASS_BASE =
    "/com/netbeans/developer/modules/loaders/clazz/resources/class";
  private final static String CLASS_MAIN_BASE =
    "/com/netbeans/developer/modules/loaders/clazz/resources/classMain";
  private final static String ERROR_BASE =
    "/com/netbeans/developer/modules/loaders/clazz/resources/classError";
  private final static String BEAN_BASE =
    "/com/netbeans/developer/modules/loaders/clazz/resources/bean";
  private final static String BEAN_MAIN_BASE =
    "/com/netbeans/developer/modules/loaders/clazz/resources/beanMain";

  /** a flag whether the children of this object are only items declared
  * by this class, or all items (incl. inherited)
  */
  private boolean showDeclaredOnly = true;  // [PENDING - get default value from somewhere ?]

  /** ClassDataObject that is represented */
  protected ClassDataObject obj;

  /** The flag indicating whether right icon has been already found */
  transient boolean iconResolved = false;

// -----------------------------------------------------------------------
// constructor

  /** @param obj is a ClassDataObject that is to be represented
  */
  ClassDataNode(final ClassDataObject obj) {
    super(obj, new SourceChildren(DefaultFactory.READ_ONLY));
    this.obj = obj;
    initialize();
  }

  /** Returns icon base string which should be used for
  * icon inicialization. Subclasses can ovveride this method
  * to provide their own icon base string.
  */
  protected String initialIconBase () {
    return CLASS_BASE;
  }

  private void initialize () {
    setIconBase(initialIconBase());
    // set right source element to our children
    SourceCookie sc =
      (SourceCookie)getDataObject().getCookie(SourceCookie.class);
    if (sc != null) {
      ((SourceChildren)getChildren()).setElement(sc.getSource());
    }
    // icons...
    RequestProcessor.postRequest(this, 200);
  }

  public void setParams (final String params) throws IOException {
    ((ClassDataObject) getDataObject()).setParams(params);
  }

  public String getParams () {
    return ((ClassDataObject) getDataObject()).getParams();
  }

  void setExecution (boolean i) throws IOException {
    ((ClassDataObject) getDataObject()).setExecution (i);
  }

  boolean getExecution () {
    return ((ClassDataObject) getDataObject()).getExecution ();
  }

  /** Creates property set for this node */
  protected Sheet createSheet () {
    Sheet s = super.createSheet();
    ResourceBundle bundle = NbBundle.getBundle(this);
    Sheet.Set ps = s.get(Sheet.PROPERTIES);
    ps.put(new PropertySupport.ReadOnly (
             PROP_CLASS_NAME,
             String.class,
             bundle.getString("PROP_className"),
             bundle.getString("HINT_className")
           ) {
             public Object getValue () throws InvocationTargetException {
               return obj.getClassName();
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             ElementProperties.PROP_MODIFIERS,
             String.class,
             bundle.getString ("PROP_modifiers"),
             bundle.getString ("HINT_modifiers")
           ) {
             public Object getValue () throws InvocationTargetException {
               Object result = null;
               try {
                 result = obj.getModifiers();
               } catch (IOException ex) {
                 // ignore - return null
               } catch (ClassNotFoundException ex) {
                 // ignore - return null
               }
               return result;
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             ElementProperties.PROP_SUPERCLASS,
             Class.class,
             bundle.getString ("PROP_superclass"),
             bundle.getString ("HINT_superclass")
           ) {
             public Object getValue () throws InvocationTargetException {
               Object result = null;
               try {
                 result = obj.getSuperclass();
               } catch (IOException ex) {
                 // ignore - return null
               } catch (ClassNotFoundException ex) {
                 // ignore - return null
               }
               return result;
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_IS_EXECUTABLE,
             Boolean.TYPE,
             bundle.getString ("PROP_isExecutable"),
             bundle.getString ("HINT_isExecutable")
           ) {
             public Object getValue () throws InvocationTargetException {
               return new Boolean(obj.isExecutable());
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             ElementProperties.PROP_CLASS_OR_INTERFACE,
             Boolean.TYPE,
             bundle.getString ("PROP_isInterface"),
             bundle.getString ("HINT_isInterface")
           ) {
             public Object getValue () throws InvocationTargetException {
               return new Boolean (obj.isInterface());
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_IS_APPLET,
             Boolean.TYPE,
             bundle.getString ("PROP_isApplet"),
             bundle.getString ("HINT_isApplet")
           ) {
             public Object getValue () throws InvocationTargetException {
               return new Boolean (obj.isApplet());
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_IS_JAVA_BEAN,
             Boolean.TYPE,
             bundle.getString ("PROP_isJavaBean"),
             bundle.getString ("HINT_isJavaBean")
           ) {
             public Object getValue () throws InvocationTargetException {
               return new Boolean (obj.isJavaBean());
             }
           });
    // execution property set
    Sheet.Set exps = new Sheet.Set();
    exps.setName(EXECUTION_SET_NAME);
    exps.setDisplayName(bundle.getString ("PROP_executionSetName"));
    exps.setShortDescription(bundle.getString ("HINT_executionSetName"));
    s.put(exps);
    // fill it!
    exps.put(new PropertySupport.ReadWrite (
               PROP_FILE_PARAMS,
               String.class,
               bundle.getString("PROP_fileParams"),
               bundle.getString("HINT_fileParams")
             ) {
               public Object getValue() {
                 return getParams ();
               }
               public void setValue (Object val) throws InvocationTargetException {
                 if (val instanceof String) {
                   try {
                     setParams((String)val);
                   }
                   catch(IOException e) {
                     throw new InvocationTargetException (e);
                   }
                 }
                 else {
                   throw new IllegalArgumentException();
                 }
               }
             });
    exps.put(new PropertySupport.ReadWrite (
               PROP_EXECUTION,
               Boolean.TYPE,
               bundle.getString("PROP_execution"),
               bundle.getString("HINT_execution")
             ) {
               public Object getValue() {
                 return new Boolean (getExecution ());
               }
               public void setValue (Object val) throws InvocationTargetException {
                 try {
                   setExecution(((Boolean)val).booleanValue ());
                 } catch (IOException ex) {
                   throw new InvocationTargetException (ex);
                 }
               }
             });
    return s;
  }

  /** The implementation of the Runnable interface
  * (initialization tasks in separate thread)
  */
  public void run () {
    resolveIcons();
  }

// --------------------------------------------------------------------
// private methods

  /** Find right icon for this node. */
  protected void resolveIcons () {
    ClassDataObject dataObj = (ClassDataObject)getDataObject();
    try {
      dataObj.getBeanClass (); // check exception
      if (dataObj.isJavaBean ()) {
        if (dataObj.isExecutable ())
          setIconBase(BEAN_MAIN_BASE);
        else
          setIconBase(BEAN_BASE);
      } else
      if (dataObj.isExecutable ())
        setIconBase(CLASS_MAIN_BASE);
      else
        setIconBase(CLASS_BASE);
    } catch (IOException ex) {
      setIconBase(ERROR_BASE);
    } catch (ClassNotFoundException ex) {
      setIconBase(ERROR_BASE);
    }
    iconResolved = true;
  }
}

/*
 * Log
 *  15   Gandalf   1.14        3/22/99  Ian Formanek    Icons location fixed
 *  14   Gandalf   1.13        3/22/99  Ian Formanek    Icons moved from 
 *       modules/resources to this package
 *  13   Gandalf   1.12        3/16/99  Petr Hamernik   renaming static fields
 *  12   Gandalf   1.11        3/15/99  Petr Hamernik   
 *  11   Gandalf   1.10        2/25/99  Jaroslav Tulach Change of clipboard 
 *       management  
 *  10   Gandalf   1.9         2/15/99  David Simonek   
 *  9    Gandalf   1.8         2/9/99   David Simonek   
 *  8    Gandalf   1.7         2/9/99   David Simonek   little fixes - init in 
 *       separate thread
 *  7    Gandalf   1.6         2/3/99   David Simonek   
 *  6    Gandalf   1.5         2/1/99   David Simonek   
 *  5    Gandalf   1.4         1/20/99  David Simonek   rework of class DO
 *  4    Gandalf   1.3         1/19/99  David Simonek   
 *  3    Gandalf   1.2         1/13/99  David Simonek   
 *  2    Gandalf   1.1         1/6/99   Ian Formanek    Reflecting change in 
 *       datasystem package
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 * Beta Change History:
 *  0    Tuborg    0.11        --/--/98 Jan Formanek    slightly modified to reflect that PropertySheetView is no more a GUI component
 *  0    Tuborg    0.20        --/--/98 Jan Formanek    SWITCHED TO NODES
 *  0    Tuborg    0.21        --/--/98 Jan Formanek    reflecting changes in ButtonBar and CoronaDialog
 *  0    Tuborg    0.26        --/--/98 Jan Jancura     Serialization, localization, CoronaDialog...
 *  0    Tuborg    0.27        --/--/98 Jaroslav Tulach methods that cannot be overriden moved from ClassDataObject
 *  0    Tuborg    0.28        --/--/98 Jan Jancura     ClassException,
 */
