/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.clazz;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.*;
import java.util.ResourceBundle;
import java.io.*;
import java.text.MessageFormat;

import org.openide.nodes.*;
import org.openide.loaders.DataNode;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.src.*;
import org.openide.src.nodes.SourceChildren;
import org.openide.cookies.SourceCookie;
import org.openide.ErrorManager;
import org.openide.util.RequestProcessor;

/** Represents ClassDataObject. Common base for CompiledDataNode (.class)
* and SerDataNode (.ser and other serialized extensions)
* @author Ales Novak, Ian Formanek, Jan Jancura, Dafe Simonek
*/
abstract class ClassDataNode extends DataNode 
    implements Runnable, PropertyChangeListener {
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

    private static final RequestProcessor iconResolver = new RequestProcessor("clazz icon resolver", 1); // NOI18N
    // -----------------------------------------------------------------------
    // constructor

    /** @param obj is a ClassDataObject that is to be represented
    */
    ClassDataNode(final ClassDataObject obj) {
        /* Changed for multiple factories
        super(obj, new SourceChildren(ClassElementNodeFactory.getInstance()));
        */
        this(obj, new LazySourceChildren( obj, ClassDataObject.getExplorerFactory()) );
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
    
    protected abstract void requestResolveIcon();

    private void initialize () {
        
        setIconBase(initialIconBase());
        /* Disable the icon resolution - for now.
         *
        // icons...
        RequestProcessor.postRequest(this, 200);
         */
    }
    
    /**
     * If the image is not known, returns the basic (classfile) one and
     * requests image resolution. After the image is known, it returns the
     * cached image.
     */
    public Image getIcon(int type) {
        ensureIconResolved();
        return super.getIcon(type);
    }
    
    public Image getOpenedIcon(int type) {
        ensureIconResolved();
        return super.getOpenedIcon(type);
    }
    
    private void ensureIconResolved() {
        if (iconResolved)
            return;
        iconResolver.post(new Runnable() {
            public void run() {
                requestResolveIcon();
            }
        });
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
                    WeakListeners.propertyChange(this, sc.getSource()));
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
        errorMsg = NbBundle.getMessage(ClassDataNode.class, "FMT_ErrorHint",
            errMsg
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
