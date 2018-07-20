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

import org.netbeans.api.registry.*;
import org.netbeans.modules.registry.ApiContextFactory;
import org.netbeans.modules.registry.mergedctx.RootContextImpl;

/**
 * This class contains helper static methods intended for use by SPI clients only;
 * normal API clients which do not implement SPI contexts will not need them.
 *
 * @author  David Konecny
 */
public final class SpiUtils {

    private SpiUtils() {
    }

    /**
     * Create API context for the given SPI context.
     *
     * @return instance of Context created for the given BasicContext
     */    
    public static Context createContext(BasicContext ctx) {
        return ApiContextFactory.DEFAULT.createContext(ctx);
    }
    
    /**
     * Create ContextException instance.
     *
     * @param ctx context; should be specified, but null is acceptable
     * @param str optional exception description
     * @return instance of ContextException
     */    
    public static ContextException createContextException(BasicContext ctx, String str) {
        return ApiContextFactory.DEFAULT.createContextException(ctx, str);
    }

    /**
     * Create SubcontextEvent instance.
     *
     * @param source context; cannot be null
     * @param subcontextName name of created or deleted subcontext; cannot be null
     * @param type type; see {@link org.netbeans.api.registry.SubcontextEvent} for concrete values
     * @return instance of SubcontextEvent
     */    
    public static SubcontextEvent createSubcontextEvent(BasicContext source, String subcontextName, int type) {
        return ApiContextFactory.DEFAULT.createSubcontextEvent(source, subcontextName, type);
    }
    
    /**
     * Create BindingEvent instance.
     *
     * @param source context; cannot be null
     * @param bindingName name of the affected binding; can be null if accurate information
     *   about change is not available
     * @param type type; see {@link org.netbeans.api.registry.BindingEvent} for concrete values
     * @return instance of BindingEvent
     */    
    public static BindingEvent createBindingEvent(BasicContext source, String bindingName, int type) {
        return ApiContextFactory.DEFAULT.createBindingEvent(source, bindingName, type);
    }
    
    /**
     * Create AttributeEvent instance.
     *
     * @param source context; cannot be null
     * @param bindingName name of the binding which attribute was changed
     *    or null for the context attribute
     * @param attributeName attribute name;  can be null if accurate information
     *   about change is not available
     * @param type type; see {@link org.netbeans.api.registry.AttributeEvent} for concrete values
     * @return instance of AttributeEvent
     */    
    public static AttributeEvent createAttributeEvent(BasicContext source, String bindingName, String attributeName, int type) {
        return ApiContextFactory.DEFAULT.createAttributeEvent(source, bindingName, attributeName, type);
    }
    
    /**
     * Creates new instance of <tt>ObjectRef</tt>.
     * @param rootContext root context. See {@link Context#getRootContext}
     * @param absoluteContextName absolute name of context relative to root context. See {@link Context#getAbsoluteContextName}
     * @param bindingName name of binding
     * @return new instance of ObjectRef
     * @since 1.7
     */ 
    public static ObjectRef createObjectRef (BasicContext rootContext, String absoluteContextName, String bindingName) {
        return ApiContextFactory.DEFAULT.createObjectRef(rootContext, absoluteContextName, bindingName);        
    }

    /**
     * Creates new instance of <tt>ObjectRef</tt>. 
     * @param context context 
     * @param bindingName name of binding
     * @return new instance of ObjectRef
     */     
    public static ObjectRef createObjectRef (BasicContext context, String bindingName) {
        return ApiContextFactory.DEFAULT.createObjectRef(context, bindingName);        
    }
        
    /**
      * Returns context that merges all its delegates. See JavaDoc overview for more details.      
      * @param mergeProvider provides delegates; see {@link MergedContextProvider}  
      * @return merged context
      * @since 1.6 
      */    
     public static BasicContext merge (MergedContextProvider mergeProvider) {
         return RootContextImpl.create(mergeProvider);
     }
}
