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
import com.netbeans.ide.util.datatransfer.TransferableOwner;
import com.netbeans.ide.util.RequestProcessor;
import com.netbeans.ide.util.NbBundle;

/** Represents ClassDataObject
*
* @author Ales Novak, Ian Formanek, Jan Jancura, Dafe Simonek
*/
class ClassDataNode extends DataNode {
  /** generated Serialized Version UID */
  static final long serialVersionUID = -1543899241509520203L;

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

  /** Icon bases for icon manager */
  private final static String CLASS_BASE =
    "com/netbeans/developer/modules/resources/class/class";
  private final static String CLASS_MAIN_BASE =
    "com/netbeans/developer/modules/resources/class/classMain";
  private final static String ERROR_BASE =
    "com/netbeans/developer/modules/resources/class/classError";
  private final static String BEAN_BASE =
    "com/netbeans/developer/modules/resources/class/bean";
  private final static String BEAN_MAIN_BASE =
    "com/netbeans/developer/modules/resources/class/beanMain";

  /** a flag whether the children of this object are only items declared
  * by this class, or all items (incl. inherited)
  */
  private boolean showDeclaredOnly = true;  // [PENDING - get default value from somewhere ?]

  /** ClassDataObject that is represented */
  protected ClassDataObject obj;

  /** The flag indicating whether right icon has been already found */
  transient boolean iconResolved = false;

// ----------------------------------------------------------------------------------
// constructor

  /** @param obj is a ClassDataObject that is to be represented
  */
  ClassDataNode(final ClassDataObject obj) {
    super(obj, new ClassDataNodeChildren());
    this.obj = obj;
    initialize();
  }

  private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
    is.defaultReadObject();
    initialize();
  }

// ----------------------------------------------------------------------------------
// methods

  /** Returns icon base string which should be used for
  * icon inicialization. Subclasses can ovveride this method
  * to provide their own icon base string.
  */
  protected String initialIconBase () {
    return CLASS_BASE;
  }

  private void initialize () {
    setIconBase(initialIconBase());
    // try to find oout which icon should be used
    requestResolveIcons();
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

  /** Creates property set for this data object */
  protected Sheet createSheet () {
    Sheet s = super.createSheet();
    ResourceBundle bundle = NbBundle.getBundle(this);
    Sheet.Set ps = s.get(Sheet.PROPERTIES);
    ps.put(new PropertySupport.ReadWrite (
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
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_MODIFIERS,
             String.class,
             bundle.getString("PROP_className"),
             bundle.getString("HINT_className")
           ) {
             public Object getValue () throws InvocationTargetException {
               Object result = null;
               try {
                 result = obj.getClassName();
               } catch (IOException ex) {
               } catch (ClassNotFoundException ex) {
               }
               return result;
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_MODIFIERS,
             String.class,
             bundle.getString ("PROP_modifiers"),
             bundle.getString ("HINT_modifiers")
           ) {
             public Object getValue () throws InvocationTargetException {
               Object result = null;
               try {
                 result = obj.getModifiers();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return result;
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_SUPERCLASS,
             Class.class,
             bundle.getString ("PROP_superclass"),
             bundle.getString ("HINT_superclass")
           ) {
             public Object getValue () throws InvocationTargetException {
               Object result = null;
               try {
                 result = obj.getSuperclass();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return result;
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_SUPERCLASS,
             Boolean.TYPE,
             bundle.getString ("PROP_hasMainMethod"),
             bundle.getString ("HINT_hasMainMethod")
           ) {
             public Object getValue () throws InvocationTargetException {
               boolean result = false;
               try {
                 obj.getHasMainMethod();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return new Boolean (result);
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_ISINTERFACE,
             Boolean.TYPE,
             bundle.getString ("PROP_isInterface"),
             bundle.getString ("HINT_isInterface")
           ) {
             public Object getValue () throws InvocationTargetException {
               boolean result = false;
               try {
                 obj.isInterface();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return new Boolean (result);
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_SUPERCLASS,
             Boolean.TYPE,
             bundle.getString ("PROP_isApplet"),
             bundle.getString ("HINT_isApplet")
           ) {
             public Object getValue () throws InvocationTargetException {
               boolean result = false;
               try {
                 obj.isApplet();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return new Boolean (result);
             }
           });
    ps.put(new PropertySupport.ReadOnly (
             PROP_SUPERCLASS,
             Boolean.TYPE,
             bundle.getString ("PROP_isJavaBean"),
             bundle.getString ("HINT_isJavaBean")
           ) {
             public Object getValue () throws InvocationTargetException {
               boolean result = false;
               try {
                 obj.isJavaBean();
               } catch (ThreadDeath td) {
                 throw td;
               } catch (Throwable t) {
                 // ignore - return null
               }
               return new Boolean (result);
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

// ----------------------------------------------------------------------------------
// private methods

  /** Inspects the class and return an array of elements,
  * which will be udes as keys in children */
  Object[] inspectClass () {
    // PENDING - will be implemented when Element hierarchy
    // will be changed
    return new Object[0];
   /* Class clazz;
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
   */
  }

  /** Removes all children and creates a new ones
  * (used after switching showDeclaredOnly).
  */
  private void resetChildren () {
    Object[] newKeys;
    try {
      newKeys = inspectClass();
    } catch (Throwable t) {
      if (t instanceof ThreadDeath) throw (ThreadDeath)t;
      TopManager.getDefault().notifyException (t);
      return;
    };
    ((ClassDataNodeChildren)getChildren()).setMyKeys(newKeys);
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

  /** Find right icon for this node. */
  protected void resolveIcons () {
    ClassDataObject dataObj = (ClassDataObject)getDataObject();
    try {
      dataObj.getBeanClass (); // check exception
      if (dataObj.isJavaBean ()) {
        if (dataObj.getHasMainMethod ())
          setIconBase(BEAN_MAIN_BASE);
        else
          setIconBase(BEAN_BASE);
      } else
      if (dataObj.getHasMainMethod ())
        setIconBase(CLASS_MAIN_BASE);
      else
        setIconBase(CLASS_BASE);
    } catch (ThreadDeath td) {
      throw td;
    } catch (Throwable t) {
      setIconBase(ERROR_BASE);
    }
    iconResolved = true;
  }

  /** Special subnodes (children) for ClassDataNode */
  private static final class ClassDataNodeChildren extends Children.Keys {

    private static final Object NUMB_KEY = new Object();

    ClassDataNodeChildren () {
      super();
      // set key for numb node showing "now parsing" message
      setKeys(new Object[] { NUMB_KEY });
    }

    /** Overrides initNodes to resetChidren in separate thread too. */
    protected Node[] initNodes () {
      Node[] result = super.initNodes();
      SwingUtilities.invokeLater(new Runnable () {
        public void run () {
          ((ClassDataNode)ClassDataNodeChildren.this.getNode()).resetChildren();
        }
      });
      return result;
    }

    /** Creates nodes for given key.
    * @param key the key that is used
    * @return array of nodes representing the key
    */
    protected Node[] createNodes (final Object key) {
      // PENDING - return the node according to the type
      // of input key (constructor, method, variable etc...
      if (NUMB_KEY.equals(key)) {
        // PENDING return node showing "now parsing" message
        return new Node[0];
      }
      return new Node[0];
    }

    /** Accessor for ClassDataNode outer class */
    private void setMyKeys (final Object[] keys) {
      setKeys(keys);
    }

  } // end of CallStackChildren inner class


}

/*
 * Log
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
