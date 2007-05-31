/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
