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
import org.netbeans.spi.registry.BasicContext;

/**
 *
 * @author  David Konecny
 */

// this class is constructed and assigned to ApiContextFactory.DEFAULT
// variable in Context static initializer. see there for more.
final class ApiContextFactoryImpl extends ApiContextFactory {
    ApiContextFactoryImpl() {
    }

    public Context createContext(BasicContext ctx) {
        return Context.getApiContext(ctx);
    }

    public ContextException createContextException(BasicContext ctx, String str) {
        return new ContextException(createContext(ctx), str);
    }
    
    public SubcontextEvent createSubcontextEvent(BasicContext source, String subcontextName, int type) {
        return new SubcontextEvent(createContext(source), subcontextName, type);
    }
    
    public BindingEvent createBindingEvent(BasicContext source, String bindingName, int type) {
        return new BindingEvent(createContext(source), bindingName, type);
    }
    
    public AttributeEvent createAttributeEvent(BasicContext source, String bindingName, String attributeName, int type) {
        return new AttributeEvent(createContext(source), bindingName, attributeName, type);
    }


    public ObjectRef createObjectRef (BasicContext rootCtx, String ctxName, String bindingName) {
        return new ObjectRef(createContext(rootCtx), ctxName, bindingName);        
    }
    public ObjectRef createObjectRef (BasicContext ctx, String bindingName) {
        return new ObjectRef(createContext(ctx), bindingName);    
    }
    
    public BasicContext getBasicContext(Context ctx) {
        return ctx.delegate;
    }

}
