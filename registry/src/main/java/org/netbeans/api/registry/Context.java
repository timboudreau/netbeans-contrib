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

package org.netbeans.api.registry;

import org.netbeans.modules.registry.ApiContextFactory;
import org.netbeans.modules.registry.OrderingSupport;
import org.netbeans.spi.registry.BasicContext;
import org.netbeans.spi.registry.MergedContextProvider;
import org.netbeans.spi.registry.ResettableContext;
import org.netbeans.spi.registry.SpiUtils;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This API class is representing a context which consists of a set of 
 * name-to-object bindings. It contains methods for examining and 
 * updating these bindings. The contexts are composed into
 * hierarchical collection which form configuration system. The 
 * configuration system allows applicatioRootns to store and retrieve
 * data. How data are stored is not specified by the Registry API.
 * It is up to provided implementation of Registry SPI to properly
 * document it and that documentation must be consulted.
 *
 * <p>The hierarchical collection of contexts is similar to folders
 * in a hierarchical file system. There exist a root context containing
 * all the contexts. The name of this context is <i>"/"</i>
 * and it is also absolute name of the context. The names 
 * of individual contexts cannot be empty, but does not have to 
 * be unique. However, absolute name of the context must be
 * unique. Children of the root context have absolute names of 
 * <i>"/" + context name</i>. All other contexts have absolute names of 
 * <i>parent's context absolute name + "/" + context name</i>.
 *
 * <p>The context can contain the subcontext and binding with the same name
 * and they will coexist.
 *
 * <p>The context has getters and setters for all primitive data types, 
 * for a few basic object types (eg. Color, URL, etc.) and for Object.
 * As was said above the storage format is specific per Registry SPI
 * implementation, but following is valid for all backends:
 * <ul>
 * <li><b>primitive data types:</b> the primitive data type is converted into corresponding 
 * object counterpart and the object is stored. Retrieval of the primitive 
 * value does the opposite: the object is retrieved and converted to primitive data
 * type. Each context implementation must support storage of these data types.</li>
 * <li><b>supported basic object types:</b> these object types in addition to
 * basic data types must be persistable by all context implementations.</li>
 * <li><b>any other Object:</b> it is up to specific Registry SPI implementation
 * to document what is supported and how in this area.</li>
 * </ul>
 *
 * <p>Getters do not throw exceptions. They accept default value parameter which
 * is returned if value does not exist or it could not be read. The exceptions are logged.
 *
 * <p>Putting null value removes the binding.
 *
 *<p>Rename of binding is not supported because no useful usecase was found.
 *
 * <p>All methods are properly synchronized and can be accessed from multiple
 * threads without any synchronization on the client side. During the modification is
 * whole hierarchy of contexts exclusively locked.
 *
 * <p>TBD: Names restrictions: length, valid characters, etc. 
 * The context name nor binding name cannot contain "/" character. 
 * 
 * @author  David Konecny
 */
public final class Context {
    
    /** SPI context to which this API context delegates most of the calls. */
    BasicContext delegate;
    
    /** Cache of the created API contexts. The key is is SPI context and
     * value is WeakReference to API context. */
    private static WeakHashMap contextCache = new WeakHashMap();
    
    // This should theoretically be in ApiContextFactoryImpl, but that would
    // mean that variable ApiContextFactory.DEFAULT would not initialized
    // before ApiContextFactoryImpl was loaded into VM what can be sometimes
    // too late. On the other hand Context class is loaded as one of the first classes.
    // That's why it is here.
    private static final Mutex.Privileged privilegedMutex = new Mutex.Privileged();
    private static final Mutex mutex = new Mutex (privilegedMutex);
    
    static {
        ApiContextFactory.DEFAULT = new ApiContextFactoryImpl();                
    }
    
    private static BasicContext defaultRootContext;
    
    private static final Logger log = Logger.getLogger(Context.class.getName());
    
    private Context(BasicContext delegate) {
        this.delegate = delegate;
    }
    
    ///////////////////////////
    // Context related methods:
    ///////////////////////////

    /**
     * Getter for default instance of registry.
     *
     * <p>Application specific documentation must be consulted to learn which
     * exact implementation will be returned by this method. For example for
     * NetBeans it can be implementation working on top of the root of
     * SystemFileSystem.
     *
     * @return default root context
     */    
    public static Context getDefault() {
        return Context.getApiContext(getRC());
    }


   /**
     * Returns context, that merges all its delegates. See JavaDoc overview for more details.
     * @param delegates array of contexts that should be merged  
     * @return merged context
     * @since 1.6 
     */
    public static Context merge (final Context[] delegates) {
        BasicContext mergedBasic = SpiUtils.merge(new MergedContextProvider() {
            public void addPropertyChangeListener(PropertyChangeListener listener) {
            }

            public void removePropertyChangeListener(PropertyChangeListener listener) {
            }

            public BasicContext[] getDelegates() {
                List basicDelegates = new ArrayList();
                for (int i = 0; i < delegates.length; i++) {
                    Context delegate = delegates[i];
                    basicDelegates.add(delegate.delegate);
                }

                return (BasicContext[])basicDelegates.toArray(new BasicContext[0]);
            }
        });
        return SpiUtils.createContext(mergedBasic);
    }

    /**
     * Name of the context.
     *
     * @return name of the context; cannot be null
     */    
    public String getContextName() {
        return delegate.getContextName();
    }
    
    /**
     * Gets root context.
     *
     * @return root context 
     */    
    public Context getRootContext() {
        return getApiContext(delegate.getRootContext());
    }
    
    /**
     * Getter for the absolute name of the context, eg. "/mymodule/mysettings".
     * It is full path from the root context.
     *
     * @return absolute name of this context; cannot be null
     */    
    public String getAbsoluteContextName() {
        BasicContext ctx = delegate;
        StringBuffer sb = new StringBuffer(ctx.getContextName());
        while (ctx.getParentContext() != null) {
            ctx = ctx.getParentContext();
            if (ctx.getContextName().equals("/")) {
                sb.insert(0, ctx.getContextName());
            } else {
                sb.insert(0, "/");
                sb.insert(0, ctx.getContextName());
            }
        }
        return sb.toString();
    }

    /**
     * Retrieve subcontext of the given name. The multi-level path
     * is accepted as subcontext name (eg. "sub1/sub2/mysub").
     *
     * @param subcontextName multi-level subcontext name to retrieve
     * @return Context or null if subcontext does not exist
     */    
    public Context getSubcontext(String subcontextName) {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            StringTokenizer tok = new StringTokenizer(subcontextName, "/"); // NOI18N
            BasicContext ctx = delegate;
            while (tok.hasMoreTokens() && ctx != null) {
                String name = tok.nextToken();
                ctx = ctx.getSubcontext(name);
            }
            return getApiContext(ctx);
        } finally {
            mp.exitReadAccess();
        }
    }
    
    /**
     * Retrieve parent context.
     *
     * @return parent context or null in case of root context
     */    
    public Context getParentContext() {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            return getApiContext(delegate.getParentContext());
        } finally {
            mp.exitReadAccess();
        }
    }
    
    /** 
     * Create subcontext of the given name. The multi-level path
     * is accepted as subcontext name (eg. "sub1/sub2/mysub"). All
     * intermediate subcontexts which do not exist will be created. If
     * subcontext already exist it is just retrieved.
     *
     * @param subcontextName multi-level subcontext name to create
     * @return created or retrieved context
     * @throws ContextException thrown when subcontext cannot be created
     */    
    public Context createSubcontext(String subcontextName) throws ContextException {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterWriteAccess();
            StringTokenizer tok = new StringTokenizer(subcontextName, "/"); // NOI18N
            BasicContext ctx = delegate;
            while (tok.hasMoreTokens()) {
                String name = tok.nextToken();
                BasicContext ctx2 = ctx.getSubcontext(name);
                if (ctx2 != null) {
                    ctx = ctx2;
                } else {
                    ctx = ctx.createSubcontext(name);
                }
            }
            return getApiContext(ctx);
        } finally {
            mp.exitWriteAccess();
        }
    }
    
    /**
     * Destroy subcontext of the given name. Destroying context deletes
     * also all its data recursively, ie. all bindings, attributes 
     * and its subcontexts. The multi-level path is accepted
     * (eg. "sub1/sub2/mysub").
     *
     * @param subcontextName multi-level name of existing subcontext
     * @throws ContextException thrown when subcontext cannot be deleted
     */    
    public void destroySubcontext(String subcontextName) throws ContextException {
        int index = subcontextName.lastIndexOf('/');
        BasicContext ctx = delegate;
        if (index != -1) {
            ctx = getSubcontext(subcontextName.substring(0, index)).delegate;
            subcontextName = subcontextName.substring(index+1);
        }
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterWriteAccess();
            ctx.destroySubcontext(subcontextName);
        } finally {
            mp.exitWriteAccess();
        }
    }        


    ///////////////////////////////    
    // Context enumeration methods:
    ///////////////////////////////    

    /**
     * Retrieve names of all subcontexts of this context.
     *
     * @return collection of Strings, ie. names of all subcontexts in the context;
     * cannot be null
     */
    public Collection/*<String>*/ getSubcontextNames() {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            return delegate.getSubcontextNames();
        } finally {
            mp.exitReadAccess();
        }
    }
    
    /**
     * Retrieve names of all bindings in this context.
     *
     * @return collection of Strings, ie. names of all bindings in the context;
     * cannot be null
     */    
    public Collection/*<String>*/ getBindingNames() {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            return delegate.getBindingNames();
        } finally {
            mp.exitReadAccess();
        }
    }

    /**
     * Retrieve names of all attributes in this context.
     *
     * @return collection of Strings, ie. names of all attribute in the context;
     * cannot be null
     */    
    public Collection/*<String>*/ getAttributeNames(String bindingName) {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            return delegate.getAttributeNames(bindingName);
        } finally {
            mp.exitReadAccess();
        }
    }

    /////////////////////////////////////
    // Ordered context enumerations methods:
    /////////////////////////////////////
    
    /** Store full order of the context content. The passed
     * list can contain only String instances which are names
     * of a context's binding or context's subcontext. The context's
     * subcontext name must be appended with "/" character to distinguish subcontext
     * name from binding name. For any other value the 
     * IllegalArgumentException will be thrown. 
     *
     * @param names full order of the objects in the context; see above for
     *   expected values in the list
     */    
    public void orderContext(List names) {
        StringBuffer value = new StringBuffer();
        Iterator it = names.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof String)) {
                throw new IllegalArgumentException("OrderContext: Passed list contains item which is not String - "+o);
            }
            String item = (String)o;
            // this checking might be ommited. the fullorder can contain invalid items
            // and they will be ignored. therefore I'm not doing this under write lock as
            // it should be and if there are perf or other problems the checkItem
            // method can be removed completely.
            checkItem(item);
            value.append(item);
            value.append(",");
        }
        if (value.length() > 0) {
            value.setLength(value.length()-1);
        }
        setAttribute(null, "fullorder", value.toString());
    }
    
    private void checkItem(String name) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length()-1);
            if (getSubcontext(name) == null) {
                throw new IllegalArgumentException("OrderContext: Passed list contains non-existing subcontext - "+name);
            }
        } else {
            if (!getBindingNames().contains(name)) {
                throw new IllegalArgumentException("OrderContext: Passed list contains non-existing binding - "+name);
            }
        }
    }

    /**
     * Method for listing items of the context ordered. The returned list
     * will contain instances of all direct subcontexts and all bound
     * objects in this context.
     *
     * <p>See Javadoc overview for how the context items are sorted.
     * If context content was ordered by {@link #orderContext} method
     * then the items will be listed in this order (order of items which were
     * not specified in the full order is undefined). 
     *
     * @return collection of ordered Objects, ie. both instances of bound
     *     Objects and Contexts representing subcontexts are returned.
     */    
    public List getOrderedObjects() {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            List ar = new ArrayList();
            Iterator it = OrderingSupport.DEFAULT.getOrderedNames(this).iterator();
            while (it.hasNext()) {
                String item = (String)it.next();
                if (item.endsWith("/")) {
                    Context ctx = getSubcontext(item.substring(0, item.length()-1));
                    if (ctx != null) {
                        ar.add(ctx);
                    }
                } else {
                    Object o = getObject(item, null);
                    if (o != null) {
                        ar.add(o);
                    }
                }
            }
            return ar;
        } finally {
            mp.exitReadAccess();
        }
    }
    
    /**
     * Method listing ordered names of the context items. The returned list
     * will contain names of all direct subcontexts and all bound
     * objects in this context. The subcontext names are appended
     * with "/" character. It can be used to distinguish subcontext name
     * and binding name when they are the same.
     *
     * <p>See the JavaDoc for {@link #getOrderedObjects} method for
     * more details about ordering itself.
     *
     * @return collection of Strings; contains binding names and subcontext
     *  names appended with "/" character
     *
     * @since 1.4
     */    
    public List getOrderedNames() {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            return OrderingSupport.DEFAULT.getOrderedNames(this);
        } finally {
            mp.exitReadAccess();
        }
    }
    
    
    ///////////////////////////////    
    // Bindings related methods:
    ///////////////////////////////    

    /**
     * Retrieve named object from the context.
     *
     * <p>If retrieved object is instance of {@link ObjectRef} it is recursively
     * dereferenced and the value of ObjectRef instance if returned instead.
     * If dereferenced value is null or ObjectRef is invalid then the ObjectRef
     * instance is returned. See {@link #getRef} if you need to retrieve 
     * ObjectRef directly.
     *
     * @param bindingName the name of the object to retrieve; cannot be empty
     * @param defaultValue default value returned in case the binding does not 
     *      exist.
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public Object getObject(String bindingName, Object defaultValue) {
        Object o = getObject(bindingName);
        
        if (o instanceof ObjectRef) {
            // it is ObjectRef -> dereference it:
            Object origin = o;
            while (o != null && o instanceof ObjectRef) {
                o = ((ObjectRef)o).getObject();
            }
            // if the resulted value is null then return ObjectRef:
            if (o == null) {
                o = origin;
            }
        }
        
        if (o == null) {
            return defaultValue;
        } else {
            return o;
        }
    }

    /**
     * Retrieve directly the bounded ObjectRef instance. The {@link #getObject}
     * does recursive traversal of ObjectRef and returns directly the referenced value.
     *
     * @param bindingName the name of the object to retrieve; cannot be empty
     * @return instance of the ObjectRef or null if bound object is not ObjectRef
     */    
    public ObjectRef getRef(String bindingName) {
        Object o = getObject(bindingName);
        
        if (o instanceof ObjectRef) {
            return (ObjectRef)o;
        } else {
            return null;
        }
    }
    
    private Object getObject(String bindingName) {
        Object o = null;
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterReadAccess();
            o = delegate.lookupObject(bindingName);
        } catch (ContextException ex) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                    NbBundle.getMessage(Context.class, "MSG_get_object", bindingName, getAbsoluteContextName()),
                    ex);
            }
        } finally {
            mp.exitReadAccess();
        }

        return o;
    }
    
    /**
     * Binds a name to an object and store the object in context. Use null
     * value to remove binding.
     *
     * <p>See class overview JavaDoc for more details about how
     * the objects are stored and which object types are supported.
     *
     * @param bindingName the name to bind; cannot be empty
     * @param value the object to bind; null is allowed and means
     *        deletion of the binding
     */    
    public void putObject(String bindingName, Object value) {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterWriteAccess();
            delegate.bindObject(bindingName, value);
        } catch (ContextException ex) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                    NbBundle.getMessage(Context.class, "MSG_put_object",
                        bindingName, getAbsoluteContextName()),
                ex);
            }
        } finally {
            mp.exitWriteAccess();
        }
    }

    
    /**
     * Retrieve String value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public String getString(String bindingName, String defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null || !(o instanceof String) ) {
            return defaultValue;
        } else {
            return (String)o;
        }
    }
    
    /**
     * Binds a name to the String and store it in the context. Use null
     * value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putString(String bindingName, String value) {
        putObject(bindingName, value);
    }
    
    /**
     * Retrieve integer value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public int getInt(String bindingName, int defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Integer) {
            return ((Integer)o).intValue();
        } else if (o instanceof String) {
            return Integer.parseInt((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This method converts the passed integer to Integer
     * object and binds it into context.
     * Use {@link #putObject} with null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putInt(String bindingName, int value) {
        putObject(bindingName, new Integer(value));
    }

    /**
     * Retrieve long value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public long getLong(String bindingName, long defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Long) {
            return ((Long)o).longValue();
        } else if (o instanceof String) {
            return Long.parseLong((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This method converts the passed long to Long
     * object and binds it into context.
     * Use {@link #putObject} with null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putLong(String bindingName, long value) {
        putObject(bindingName, new Long(value));
    }

    /**
     * Retrieve boolean value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public boolean getBoolean(String bindingName, boolean defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            return Boolean.valueOf((String)o).booleanValue();
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This method converts the passed boolean to Boolean
     * object and binds it into context.
     * Use {@link #putObject} with null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putBoolean(String bindingName, boolean value) {
        putObject(bindingName, Boolean.valueOf(value));
    }

    /**
     * Retrieve float value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public float getFloat(String bindingName, float defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Float) {
            return ((Float)o).floatValue();
        } else if (o instanceof String) {
            return Float.parseFloat((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This method converts the passed float to Float
     * object and binds it into context.
     * Use {@link #putObject} with null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putFloat(String bindingName, float value) {
        putObject(bindingName, new Float(value));
    }

    /**
     * Retrieve double value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public double getDouble(String bindingName, double defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Double) {
            return ((Double)o).doubleValue();
        } else if (o instanceof String) {
            return Double.parseDouble((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This method converts the passed double to Double
     * object and binds it into context.
     * Use {@link #putObject} with null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putDouble(String bindingName, double value) {
        putObject(bindingName, new Double(value));
    }

    /**
     * Retrieve font value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public Font getFont(String bindingName, Font defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Font) {
            return (Font)o;
        } else if (o instanceof String) {
            return Font.decode((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This is just convenient method. Its functionality is equal
     * to {@link #putObject}. Use null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putFont(String bindingName, Font value) {
        putObject(bindingName, value);
    }

    /**
     * Retrieve color value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public Color getColor(String bindingName, Color defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof Color) {
            return (Color)o;
        } else if (o instanceof String) {
            return Color.decode((String)o);
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This is just convenient method. Its functionality is equal
     * to {@link #putObject}. Use null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putColor(String bindingName, Color value) {
        putObject(bindingName, value);
    }

    /**
     * Retrieve URL value.
     *
     * @param bindingName binding name
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public URL getURL(String bindingName, URL defaultValue) {
        Object o = getObject(bindingName, null);
        if (o == null) {
            return defaultValue;
        } else if (o instanceof URL) {
            return (URL)o;
        } else if (o instanceof String) {
            try {
                return new URL((String)o);
            } catch (MalformedURLException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
    
    /**
     * This is just convenient method. Its functionality is equal
     * to {@link #putObject}. Use null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     */    
    public void putURL(String bindingName, URL value) {
        putObject(bindingName, value);
    }

    /**
     * Retrieve string array by reading stored string and splitting it by the
     * given separator into array of Strings.
     *
     * @param bindingName binding name
     * @param separator separtor character
     * @param defaultValue default value returned if this binding does not exist
     * @return retrieved value or defaultValue if this binding does not exist
     */    
    public String[] getStringArray(String bindingName, char separator, String[] defaultValue) {
        String value = getString(bindingName, null);
        if (value == null) {
            return defaultValue;
        }
        StringTokenizer tok = new StringTokenizer(value, Character.toString(separator));
        String sa[] = new String[tok.countTokens()];
        int index = 0;
        while (tok.hasMoreTokens()) {
            sa[index] = tok.nextToken();
            index++;
        }
        return sa;
    }
    
    /**
     * Store array of strings. The strings are compound into one String and 
     * separated by separator. The rest is same as in putString() method.
     * Use null value to remove binding.
     *
     * @param bindingName binding name
     * @param value value
     * @param separator separator character
     */    
    public void putStringArray(String bindingName, char separator, String[] value) {
        if (value == null) {
            putString(bindingName, null);
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<value.length; i++) {
            sb.append(value[i]);
            if (i+1<value.length) {
                sb.append(separator);
            }
        }
        putString(bindingName, sb.toString());
    }

    
    ////////////////////////////////////
    // Attributes related methods:
    ////////////////////////////////////

    /**
     * Retrieve value of the attribute. Attributes can be specified
     * on binding or context.
     *
     * @param bindingName name of the binding for binding attribute
     *   or null for context attribute
     * @param attributeName name of the attribute to retrieve; cannot be null
     * @param defaultValue default value returned when attribute does not exist
     * @return retrieved value or defaultValue if this attribute does not exist
     */    
    public String getAttribute(String bindingName, String attributeName, String defaultValue) {
        Mutex.Privileged mp = getMutexPrivileged();
        String value = null;
        try {
            mp.enterReadAccess();
            value = delegate.getAttribute(bindingName, attributeName);
        } catch (ContextException ex) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                    NbBundle.getMessage(Context.class, "MSG_get_attr", 
                    bindingName == null ? attributeName : bindingName+"\\"+attributeName,
                    getAbsoluteContextName()), 
                    ex);
            }
        } finally {
            mp.exitReadAccess();
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * Modify value of the attribute.  Attributes can be specified
     * on binding or context. Attribute is deleted by passing null as attribute value.
     *
     * @param bindingName name of the binding for binding attribute
     *   or null for context attribute
     * @param attributeName name of the attribute to modify; cannot be null
     * @param value new value of the attribute; null is allowed and means
     *        deletion of attribute
     */    
    public void setAttribute(String bindingName, String attributeName, String value) {
        Mutex.Privileged mp = getMutexPrivileged();
        try {
            mp.enterWriteAccess();
            delegate.setAttribute(bindingName, attributeName, value);
        } catch (ContextException ex) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                    NbBundle.getMessage(Context.class, "MSG_put_attr",
                    bindingName == null ? attributeName : bindingName+"\\"+attributeName,
                    getAbsoluteContextName()),
                    ex);
            }
        } finally {
            mp.exitWriteAccess();
        }
    }

    
    //////////////////////////////
    // Listeners related methods:
    //////////////////////////////

    /**
     * Add listener for receiving events about
     * context and all its descendant subcontexts changes.
     *
     * This listener will be notified about added/removed subcontext, 
     * added/modified/removed binding and added/modified/removed
     * context or binding attribute in this context and all its subcontexts.
     * The {@link ContextEvent#getContext} must be consulted to learn in which 
     * context the change really happened.
     *
     * @param l listener to add
     */    
    public synchronized void addContextListener(ContextListener l) {
        delegate.addContextListener(l);
    }
    
    /**
     * Remove listener for receiving events about
     * context and all its descendant subcontexts changes.
     *
     * @param l listener to remove
     */    
    public synchronized void removeContextListener(ContextListener l) {
        delegate.removeContextListener(l);
    }

    
    
    /////////////////////////////////
    // Resettability of the context:
    /////////////////////////////////
    
    
    /**
     * Exist a default value for this binding or context?
     *
     * @param bindingName the binding name or null for the context examination
     * @return true if there is a default; false in opposite case or when backend does
     * not support defaults at all.
     */    
    public boolean hasDefault(String bindingName) {
        if (delegate instanceof ResettableContext) {
            return ((ResettableContext)delegate).hasDefault(bindingName);
        }
        return false;
    }

    /**
     * Check whether the value of this binding or context is modified.
     * The return value is 
     * always true in case default
     * value does not exist, ie. {@link #hasDefault} is false.
     *
     * @param bindingName the binding name or null for the context
     * @return true if default value is modified; always returns true if 
     *         default value does not exist
     */    
    public boolean isModified(String bindingName) {
        if (delegate instanceof ResettableContext) {
            Mutex.Privileged mp = getMutexPrivileged();
            try {
                mp.enterReadAccess();
                return ((ResettableContext)delegate).isModified(bindingName);
            } finally {
                mp.exitReadAccess();
            }
        }
        return true;
    }

    /**
     * Revert modification of this binding or context. Will do something 
     * only if value is modified
     * (ie. {@link #isModified} returns true). If there is no default
     * value (ie. {@link #hasDefault} returns false) the revert operation
     * is identical to unbinding of object or destroying of content of context.
     *
     * @param bindingName the binding name or null for the context
     * @throws ContextException can throw exception if there were problems
     *         during removal of modified values
     */    
    public void revert(String bindingName) throws ContextException {
        Mutex.Privileged mp = getMutexPrivileged();
        if (delegate instanceof ResettableContext) {
            try {
                mp.enterWriteAccess();
                ((ResettableContext)delegate).revert(bindingName);
            } finally {
                mp.exitWriteAccess();
            }
            return;
        }
        // resettable is not implemented, but try at least to delete all the values.
        if (bindingName != null) {
            putObject(bindingName, null);
        } else {
            try {
                mp.enterWriteAccess();
                BasicContext ctx = delegate.getParentContext();
                String name = delegate.getContextName();
                ctx.destroySubcontext(name);
                ctx.createSubcontext(name);
            } finally {
                mp.exitWriteAccess();
            }
            //TODO:
            // - enumerate all bindings in this context and delete them
            // - enumerate all attributes in this context and delete them
            // - enumerate all subcontexts in this context and delete them
        }
    }
    
    /**
     * Shared Mutex on which all contexts are
     * synchronized. The API clients does not need to synchronize access to context.
     * But it can be convenient for example to do several changes under one lock.
     *
     * @return mutex instance
     */    
    public static synchronized Mutex getMutex() {
        // temporary solution until there will exist public: 
        return mutex;
    }
        
    public String toString() {
        return "Context: [absoluteName="+getAbsoluteContextName()+"] " + super.toString();
    }
    
    //////////////////////////////
    // Private implementation
    //////////////////////////////

    
    static synchronized Context getApiContext(BasicContext ctx) {
        if (ctx == null) {
            return null;
        }
        
        WeakReference weakRef = (WeakReference)contextCache.get(ctx);
        Context apiCtx = (weakRef != null) ? (Context)weakRef.get() : null;
        if (apiCtx == null) {
            apiCtx = new Context(ctx);
            contextCache.put(ctx, new WeakReference(apiCtx));
        }
        return apiCtx;
    }


    private Mutex.Privileged getMutexPrivileged() {
        return privilegedMutex;
    }

    
    private static synchronized BasicContext getRC() {
        if (defaultRootContext == null) {
            defaultRootContext = (BasicContext)Lookup.getDefault().lookup(BasicContext.class);
            if (defaultRootContext == null) {
                log.log(Level.SEVERE, 
                    "FATAL ERROR: RootContext was not found in the default lookup! "+ //NOI18N
                    "All Registry API operations will fail!! "+ //NOI18N
                    "CAUSE: Either the org-netbeans-core-registry-1-?.?.jar module does not exist "+ //NOI18N
                    "or its implementation dependency on org-netbeans-modules-registry-1-?.?.jar "+ //NOI18N
                    "could not be fulfilled and therefore it was not installed. "+ //NOI18N 
                    "SOLUTION: rebuild both modules together."); //NOI18N
            }
        }
        return defaultRootContext;
    }
    
}
