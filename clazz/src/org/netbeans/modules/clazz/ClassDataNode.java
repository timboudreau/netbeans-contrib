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
import java.io.*;

import com.netbeans.ide.util.datatransfer.TransferableOwner;
import com.netbeans.ide.classloader.NbClass;
import com.netbeans.ide.nodes.*;
import com.netbeans.ide.util.RequestProcessor;

/** Represents ClassDataObject
*
* @author Ales Novak, Ian Formanek, Jan Jancura
* @version 0.28, Apr 15, 1998
*/
class ClassDataNode extends com.netbeans.ide.loaders.DataContextNode {
  /** generated Serialized Version UID */
  static final long serialVersionUID = -1543899241509520203L;

  private static java.awt.Image icon;
  private static java.awt.Image icon32;
  private static java.awt.Image iconMain;
  private static java.awt.Image iconMain32;
  private static java.awt.Image iconError;
  private static java.awt.Image iconError32;
  private static java.awt.Image iconBean;
  private static java.awt.Image iconBean32;
  private static java.awt.Image iconBeanMain;
  private static java.awt.Image iconBeanMain32;

  /** Properties */
  private final static String PROP_ISINTERFACE      = "isInterface";
  private final static String PROP_SUPERCLASS       = "superclass";
  private final static String PROP_MODIFIERS        = "modifiers";
  private final static String PROP_CLASSNAME        = "className";
  private final static String PROP_SHOWDECLAREDONLY = "showDeclaredOnly";
  private final static String PROPERTY_SET_NAME     = "Property";
  private final static String EXECUTION_SET_NAME     = "Execution";
  private final static String PROP_FILE_PARAMS      = "fileParams";
  private final static String PROP_EXECUTION        = "execution";

  /** Bundle to obtain text information. */
  static java.util.ResourceBundle  bundle = com.netbeans.ide.util.NbBundle.getBundle (
                                                "com.netbeans.developer.modules.locales.LoadersClazzBundle");

  /** initializers */
  static {
    icon = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/class.gif"));
    icon32 = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/class32.gif"));
    iconMain = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/classMain.gif"));
    iconMain32 = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/classMain32.gif"));
    iconError = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/classError.gif"));
    iconError32 = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/classError32.gif"));

    iconBean = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/bean.gif"));
    iconBean32 = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/bean32.gif"));
    iconBeanMain = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/beanMain.gif"));
    iconBeanMain32 = java.awt.Toolkit.getDefaultToolkit ().getImage (
        Object.class.getResource ("/com.netbeans.developer.modules/resources/class/beanMain32.gif"));
  }

// ----------------------------------------------------------------------------------
// constructor

  /**
  * @param obj is a ClassDataObject that is to be represented
  */
  ClassDataNode(ClassDataObject obj) {
    super(obj);
    this.obj = obj;
  }

  private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
    is.defaultReadObject();
    currentIcon = icon;
    currentIcon32 = icon32;
  }

// ----------------------------------------------------------------------------------
// methods

  /** Finds an icon for this node - the icon is different for classObject
  * that have method main (i.e. they are executable).
  * @param type constants from <CODE>java.bean.BeanInfo</CODE>
  * @return icon to use to represent the bean
  * @see java.bean.BeanInfo
  */
  public Image getIcon (int type) {
    if (!iconResolved) {
      iconResolved = true;
      requestResolveIcons ();
    }

    if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16))
      return currentIcon;
    else
      return currentIcon32;
  }

  /** Finds an icon for this node. This icon should represent the node
  * when it is opened (when it can have children).
  *
  * @see java.bean.BeanInfo
  * @param type constants from <CODE>java.bean.BeanInfo</CODE>
  * @return icon to use to represent the bean when opened
  */
  public Image getOpenedIcon (int type) {
    return getIcon (type);
  }

  /** @return initial sub nodes */
  public Node[] createInitNodes() {
    try {
      return parseClass();
    } catch (Throwable t) {
      if (t instanceof ThreadDeath) throw (ThreadDeath)t;
      com.netbeans.ide.TopManager.getDefault().notifyException (t);
      return new Node [0];
    };
  }

  public void setParams (String params) throws java.beans.PropertyVetoException {
    ((ClassDataObject) getDataObject()).setArgv (params);
  }

  public String getParams () {
    return ((ClassDataObject) getDataObject()).getArgv ();
  }

  void setExecution (boolean i) throws IOException {
    ((ClassDataObject) getDataObject()).setExecution (i);
  }

  boolean getExecution () {
    return ((ClassDataObject) getDataObject()).getExecution ();
  }


  /** Creates property set for this data object */
  protected PropertySetArraySupport createProperties () {
    return super.createProperties().
      addProperties (
        PROPERTY_SET_NAME,
        null,
        null,
        new Node.Property[] {
          new PropertySupport.ReadWrite (
            PROP_SHOWDECLAREDONLY,
            Boolean.TYPE,
            bundle.getString ("PROP_showDeclaredOnly"),
            bundle.getString ("HINT_showDeclaredOnly")
          ) {
            public Object getValue () {
              return new Boolean (showDeclaredOnly);
            }

            public void setValue (Object val) throws IllegalArgumentException {
              if (!(val instanceof Boolean))
                throw new IllegalArgumentException();
              showDeclaredOnly = ((Boolean) val).booleanValue();
              resetChildren();
            }
          },
          new PropertySupport.ReadOnly (
            PROP_MODIFIERS,
            String.class,
            bundle.getString("PROP_className"),
            bundle.getString("HINT_className")
          ) {
            public Object getValue () throws InvocationTargetException {
              return obj.getClassName();
            }
          },
          new PropertySupport.ReadOnly (
            PROP_MODIFIERS,
            String.class,
            bundle.getString ("PROP_modifiers"),
            bundle.getString ("HINT_modifiers")
          ) {
            public Object getValue () throws InvocationTargetException {
              return obj.getModifiers ();
            }
          },
          new PropertySupport.ReadOnly (
            PROP_SUPERCLASS,
            Class.class,
            bundle.getString ("PROP_superclass"),
            bundle.getString ("HINT_superclass")
          ) {
            public Object getValue () throws InvocationTargetException {
              return obj.getSuperclass();
            }
          },
          new PropertySupport.ReadOnly (
            PROP_SUPERCLASS,
            Boolean.TYPE,
            bundle.getString ("PROP_hasMainMethod"),
            bundle.getString ("HINT_hasMainMethod")
          ) {
            public Object getValue () throws InvocationTargetException {
              return new Boolean (obj.getHasMainMethod ());
            }
          },
          new PropertySupport.ReadOnly (
            PROP_ISINTERFACE,
            Boolean.TYPE,
            bundle.getString ("PROP_isInterface"),
            bundle.getString ("HINT_isInterface")
          ) {
            public Object getValue () throws InvocationTargetException {
              return new Boolean (obj.isInterface ());
            }
          },
          new PropertySupport.ReadOnly (
            PROP_SUPERCLASS,
            Boolean.TYPE,
            bundle.getString ("PROP_isApplet"),
            bundle.getString ("HINT_isApplet")
          ) {
            public Object getValue () throws InvocationTargetException {
              return new Boolean (obj.isApplet ());
            }
          },
          new PropertySupport.ReadOnly (
            PROP_SUPERCLASS,
            Boolean.TYPE,
            bundle.getString ("PROP_isJavaBean"),
            bundle.getString ("HINT_isJavaBean")
          ) {
            public Object getValue () throws InvocationTargetException {
              return new Boolean (obj.isJavaBean ());
            }
          }
        }
      ).addProperties (
        EXECUTION_SET_NAME,
        bundle.getString ("PROP_executionSetName"),
        bundle.getString ("HINT_executionSetName"),
        new Node.Property[] {
          new PropertySupport.ReadWrite (
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
                catch(java.beans.PropertyVetoException e) {
                  throw new InvocationTargetException (e);
                }
              }
              else {
                throw new IllegalArgumentException();
              }
            }
          },
          new PropertySupport.ReadWrite (
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
          }
        }
      );
  }

  /** Called when an object is to be copied to clipboard.
  * @return the transferable object dedicated to represent the
  *    content of clipboard
  * @exception NodeAccessException is thrown when the
  *    operation cannot be performed
  */
  public TransferableOwner clipboardCopy () throws NodeAccessException {
    try {
      Class c = obj.getBeanClass ();
      return obj.new BeanTransferableOwner (
        super.clipboardCopy (),
        c.getName (),
        c
      );
    } catch (Exception e) {
      throw new NodeAccessException ();
    }
  }


// ----------------------------------------------------------------------------------
// private methods

  Node[] parseClass () {
    Class clazz;
    try {
      clazz = obj.getBeanClass ();
    } catch (Exception e) {
      return new Node [0];
    }

    Vector nodes = new Vector();

    //variables
    Field [] v;
    if (showDeclaredOnly)
      v = clazz.getDeclaredFields ();
    else
      v = clazz.getFields ();
    int i, k = v.length;
    for (i = 0; i < k; i++)
      nodes.addElement (new com.netbeans.developer.modules.loaders.clazz.ClassItemNode.VariableItemNode(this, v[i]));

    //constructors
    Constructor [] c;
    if (showDeclaredOnly)
      c = clazz.getDeclaredConstructors ();
    else
      c = clazz.getConstructors ();
    k = c.length;
    for (i = 0; i < k; i++)
      nodes.addElement (new com.netbeans.developer.modules.loaders.clazz.ClassItemNode.ConstructorItemNode(this, c[i]));

    //methods
    Method [] o;
    if (showDeclaredOnly)
      o = clazz.getDeclaredMethods ();
    else
      o = clazz.getMethods ();
    k = o.length;
    for (i = 0; i < k; i++) {
      nodes.addElement (new com.netbeans.developer.modules.loaders.clazz.ClassItemNode.MethodItemNode(this, o[i]));
    }

    Node[] children = new Node[nodes.size()];
    nodes.copyInto(children);
    return children;
  }

  /** Removes all children and creates a new ones
  * (used after switching showDeclaredOnly).
  */
  private void resetChildren() {
    Node[] children = getSubNodes();
    for (int i=0; i< children.length; i++)
        remove(children[i]);
    Node[] newChildren = parseClass();
    for (int i=0; i< newChildren.length; i++) {
      add(newChildren[i]);
    }
  }

  private void requestResolveIcons () {
    RequestProcessor.postRequest (
      new Runnable () {
        public void run () {
          resolveIcons ();
        }
      }
    );
  }

  void resolveIcons () {
    try {
      ((ClassDataObject) getDataObject()).getBeanClass (); // check exception
      if (((ClassDataObject) getDataObject()).isJavaBean ()) {
        if (((ClassDataObject) getDataObject()).getHasMainMethod ()) {
          currentIcon = iconBeanMain;
          currentIcon32 = iconBeanMain32;
        } else {
          currentIcon = iconBean;
          currentIcon32 = iconBean32;
        }
      } else
      if (((ClassDataObject) getDataObject()).getHasMainMethod ()) {
        currentIcon = iconMain;
        currentIcon32 = iconMain32;
      } else
        return;
    } catch (NbClass.NbClassException ex) {
      currentIcon = iconError;
      currentIcon32 = iconError32;
    }
    iconResolved = true;
    fireIconChange (null, currentIcon);
  }


// ----------------------------------------------------------------------------------
// variables

  transient java.awt.Window window = null;

  /** a flag whether the children of this object are only items declared
  * by this class, or all items (incl. inherited)
  */
  private boolean showDeclaredOnly = true;  // [PENDING - get default value from somewhere ?]

  /** ClassDataObject that is represented */
  protected ClassDataObject obj;

  transient boolean iconResolved = false;

  /** Icons for class data objects. */
  protected transient java.awt.Image currentIcon = icon;
  protected transient java.awt.Image currentIcon32 = icon32;
}

/*
 * Log
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
