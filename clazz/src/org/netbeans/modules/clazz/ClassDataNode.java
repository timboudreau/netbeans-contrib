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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.*;
import java.util.ResourceBundle;
import java.io.*;
import java.text.MessageFormat;

import javax.swing.SwingUtilities;

import org.openide.nodes.*;
import org.openide.loaders.DataNode;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openide.src.*;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.DefaultFactory;
import org.openide.cookies.SourceCookie;
import org.openide.ErrorManager;

/** Represents ClassDataObject. Common base for CompiledDataNode (.class)
* and SerDataNode (.ser and other serialized extensions)
* @author Ales Novak, Ian Formanek, Jan Jancura, Dafe Simonek
*/
abstract class ClassDataNode extends DataNode implements Runnable, PropertyChangeListener {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -1543899241509520203L;

    /** Properties */
    private final static String PROP_CLASS_NAME = "className"; // NOI18N
    private final static String PROP_IS_JAVA_BEAN = "isJavaBean"; // NOI18N
    private final static String PROP_IS_APPLET = "isApplet"; // NOI18N

    /** a flag whether the children of this object are only items declared
    * by this class, or all items (incl. inherited)
    */
    private boolean showDeclaredOnly = true;  // [PENDING - get default value from somewhere ?]

    /** The flag indicating whether right icon has been already found */
    transient boolean iconResolved = false;

    /** Holds error message shown in node tooltip */
    transient String errorMsg;
    
    transient boolean initialized;

    // -----------------------------------------------------------------------
    // constructor

    /** @param obj is a ClassDataObject that is to be represented
    */
    ClassDataNode(final ClassDataObject obj) {
        /* Changed for multiple factories
        super(obj, new SourceChildren(ClassElementNodeFactory.getInstance()));
        */
        this(obj, new SourceChildren( ClassDataObject.getExplorerFactory()) );
    }
    
    protected ClassDataNode(ClassDataObject obj, Children ch) {
        super(obj, ch);
        initialize();
    }
    
    protected SourceChildren getSourceChildren() {
        return (SourceChildren)getChildren();
    }
    
    /** Returns icon base string which should be used for
    * icon inicialization. Subclasses can ovveride this method
    * to provide their own icon base string.
    */
    protected abstract String initialIconBase ();
    
    protected abstract void resolveIcons();

    private void initialize () {
        SourceCookie sc =
            (SourceCookie)getDataObject().getCookie(SourceCookie.class);
        getSourceChildren().setElement(sc.getSource());
        setIconBase(initialIconBase());
        // icons...
        Util.getClassProcessor().post(this, 200);
    }

    /** Creates property set for this node */
    protected Sheet createSheet () {
        Sheet s = super.createSheet();
        ResourceBundle bundle = NbBundle.getBundle(ClassDataNode.class);
        final ClassDataObject obj=(ClassDataObject)getDataObject();
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
                   String.class,
                   bundle.getString ("PROP_superclass"),
                   bundle.getString ("HINT_superclass")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return obj.getSuperclass();
                   }
               });
        ps.put(new PropertySupport.ReadOnly (
                   ElementProperties.PROP_CLASS_OR_INTERFACE,
                   Boolean.TYPE,
                   bundle.getString ("PROP_isInterface"),
                   bundle.getString ("HINT_isInterface")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return obj.isInterface() ? Boolean.TRUE : Boolean.FALSE;
                   }
               });
        ps.put(new PropertySupport.ReadOnly (
                   PROP_IS_APPLET,
                   Boolean.TYPE,
                   bundle.getString ("PROP_isApplet"),
                   bundle.getString ("HINT_isApplet")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return obj.isApplet() ? Boolean.TRUE : Boolean.FALSE;
                   }
               });
        ps.put(new PropertySupport.ReadOnly (
                   PROP_IS_JAVA_BEAN,
                   Boolean.TYPE,
                   bundle.getString ("PROP_isJavaBean"),
                   bundle.getString ("HINT_isJavaBean")
               ) {
                   public Object getValue () throws InvocationTargetException {
                       return obj.isJavaBean() ? Boolean.TRUE : Boolean.FALSE;
                   }
               });
        return s;
    }

    /** The implementation of the Runnable interface
    * (initialization tasks in separate thread)
    */
    public void run () {
        // set right source element to our children
        SourceCookie sc =
            (SourceCookie)getDataObject().getCookie(SourceCookie.class);
        if (sc != null) {
            if (!initialized)
                sc.getSource().addPropertyChangeListener(
                    WeakListener.propertyChange(this, sc.getSource()));
            initialized = true;
        }
        resolveIcons();
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
        if (ElementProperties.PROP_STATUS.equals(ev.getPropertyName()))
            Util.getClassProcessor().post(this, 200);
    }

    // --------------------------------------------------------------------
    // private methods

    
    /** Sets error tooltip based on given exception message.
    * @param exc Exception which describes the failure.
    */
    protected void setErrorToolTip (Exception exc) {
        String errMsg = findErrorMessage(exc);
        errorMsg = MessageFormat.format(
            NbBundle.getBundle(ClassDataNode.class).getString("FMT_ErrorHint"),
            new Object[] { errMsg }
        );
        setShortDescription(errorMsg);
    }

    private String findErrorMessage(Throwable t) {
        if (t == null) {
            return null;
        }
        
        ErrorManager.Annotation[] ann = ErrorManager.getDefault().findAnnotations(t);
        if (ann == null)
            return t.getLocalizedMessage();
        for (int i = 0; i < ann.length; i++) {
            String normal = ann[i].getMessage();
            String localized = ann[i].getLocalizedMessage();
            if (localized != null)
                return localized;
            Throwable t2 = ann[i].getStackTrace();
            if (t2 == null)
                continue;
            localized = t2.getLocalizedMessage();
            if (localized != null) {
                if (!localized.equals(normal))
                    return localized;
            }
        }
        return t.getLocalizedMessage();
    }
}
