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

package org.netbeans.spi.registry;

import org.netbeans.api.registry.ContextException;
import org.netbeans.api.registry.ContextListener;

import java.util.Collection;

/**
 * This interface represents a basic context which consists of a set of
 * name-to-object bindings. It contains methods for examining and
 * updating these bindings. The contexts are composed into
 * hierarchical collection which form configuration system. The
 * configuration system allows applications to store and retrieve
 * data.
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
 * <p>All of the methods that modify context data are permitted to 
 * store changes asynchronously; they may fire change events
 * and return immediatelly and propagate changes to backing store lazily.
 *
 * <p>At minimum the implementation must support persistence of
 * all primitive data types and basic object types for which there
 * are getters and setters in {@link org.netbeans.api.registry.Context} class.
 * It is also recommended to provide a way for storage of other Object types
 * (for example by using Java Serialization or other mechanism for 
 * persistence of objects). If provided it must be properly documented 
 * and explained to clients.
 *
 * <p>The context can contain the subcontext and binding with the same name
 * and they will coexist without problems.
 *
 * <p>TBD: Names restrictions: length, valid characters, etc. 
 * The context name nor binding name cannot contain "/" character. 
 * 
 * <p>See also {@link ResettableContext} which is
 * extensions of the basic context and {@link org.netbeans.api.registry.Context}
 * which is API for bindings manipulation.
 *
 * @author  David Konecny
 */
public interface BasicContext {
    
    ///////////////////////////
    // BasicContext related methods:
    ///////////////////////////

    /**
     * Gets root context. Method #
     *
     * @return root context
     * @since 1.7 
     */    
    BasicContext getRootContext();
    
    /**
     * Name of the context.
     *
     * @return name of the context
     */    
    String getContextName();
    
    /**
     * Retrieve direct subcontext of the given name.
     *
     * @param subcontextName subcontext name to retrieve; cannot be null
     * @return Context or null if subcontext does not exist
     */    
    BasicContext getSubcontext(String subcontextName);
    
    /**
     * Retrieve parent context.
     *
     * @return parent context or null in case of root context
     */    
    BasicContext getParentContext();
    
    /** 
     * Create subcontext of the given name.
     *
     * @param subcontextName valid name of nonexisting subcontext
     * @return created context
     * @throws ContextException thrown when subcontext cannot be create or
     *         context with this name already exist
     */    
    BasicContext createSubcontext(String subcontextName) throws ContextException;
    
    /**
     * Destroy subcontext of the given name. Destroying context deletes
     * also all its data recursively, ie. all bindings, attributes 
     * and its subcontexts.
     *
     * @param subcontextName name of existing subcontext
     * @throws ContextException thrown when subcontext cannot be deleted or
     *         context with this name does not exist
     */    
    void destroySubcontext(String subcontextName) throws ContextException;


    ///////////////////////////////    
    // Context enumeration methods:
    ///////////////////////////////    

    /**
     * Retrieve names of all subcontexts of this context.
     *
     * @return collection of Strings, ie. names of all subcontexts in the context;
     * cannot be null
     */    
    Collection/*<String>*/ getSubcontextNames();

    /**
     * Get names of all bindings in this context.
     *
     * @return collection of Strings, ie. names of all bindings in the context;
     * cannot be null
     */    
    Collection/*<String>*/ getBindingNames();

    /**
     * Get names of all attributes in this context.
     *
     * @return collection of Strings, ie. names of all attribute in the context;
     * cannot be null
     */    
    Collection/*<String>*/ getAttributeNames(String bindingName);

    
    ///////////////////////////////    
    // Bindings related methods:
    ///////////////////////////////    

    /**
     * Retrieve named object from the context.
     *
     * @param bindingName the name of the object to lookup; cannot be empty
     * @return found object or null when no binding exist
     * @throws ContextException thrown when object cannot be recreated
     */    
    Object lookupObject(String bindingName) throws ContextException;

    /** 
     * Binds a name to an object and store the object in context.
     * Binding is deleted by passing null as object value.
     *
     * @param bindingName the name to bind; cannot be empty
     * @param object the object to bind;  null is allowed and means
     *        deletion of the binding
     * @throws ContextException thrown when object cannot be bound
     */    
    void bindObject(String bindingName, Object object) throws ContextException;


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
     * @return value of the attribute or null if attribute does not exist
     * @throws ContextException thrown when attribute cannot be read
     */    
    String getAttribute(String bindingName, String attributeName) throws ContextException;
    
    /**
     * Modify value of the attribute.  Attributes can be specified
     * on binding or context. Attribute is deleted by passing null as attribute value.
     *
     * @param bindingName name of the binding for binding attribute
     *   or null for context attribute
     * @param attributeName name of the attribute to modify; cannot be null
     * @param value new value of the attribute; null is allowed and means
     *        deletion of attribute
     * @throws ContextException thrown when object cannot be stored
     */    
    void setAttribute(String bindingName, String attributeName, String value) throws ContextException;

    
    //////////////////////////////
    // Listeners related methods:
    //////////////////////////////

    /**
     * Add listener for receiving events about
     * context and all its descendant subcontext changes.
     *
     * The listener must be notified about added/removed subcontext, 
     * added/modified/removed binding and added/modified/removed
     * context or binding attribute in this context and all its subcontexts.
     *
     * @param listener listener to add
     */    
    void addContextListener(ContextListener listener);
    
    /**
     * Remove listener for receiving events about
     * context and all its descedndant subcontexts changes.
     *
     * @param listener listener to remove
     */    
    void removeContextListener(ContextListener listener);

}
