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

package org.netbeans.modules.registry;

import org.netbeans.api.registry.*;
import org.netbeans.spi.registry.BasicContext;

/**
 *
 * @author  David Konecny
 */
public abstract class ApiContextFactory {

    public static ApiContextFactory DEFAULT;

    // force loading of Context class. That will set DEFAULT varible.
    static {
        Class c = Context.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    abstract public Context createContext(BasicContext ctx);

    abstract public ContextException createContextException(BasicContext ctx, String str);
    
    abstract public SubcontextEvent createSubcontextEvent(BasicContext source, String subcontextName, int type);

    abstract public AttributeEvent createAttributeEvent(BasicContext source, String bindingName, String attributeName, int type);

    abstract public BindingEvent createBindingEvent(BasicContext source, String bindingName, int type);

    abstract public ObjectRef createObjectRef (BasicContext rootCtx, String ctxName, String bindingName);
    
    abstract public ObjectRef createObjectRef (BasicContext ctx, String bindingName); 
    
    abstract public BasicContext getBasicContext(Context ctx);    
}
