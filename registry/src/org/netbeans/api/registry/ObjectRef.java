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



/**
 * Identification of the object bound in a context. Object is identified
 * by the context in which it is bound and by its binding name.
 *
 * <p>ObjectRef instance can be stored to Context by method
 * {@link Context#putObject}. There are two options for retrieval.
 * Calling {@link Context#getRef} method on bound ObjectRef instance returns
 * that instance. Calling {@link Context#getObject}, however, will
 * recursively dereference ObjectRef instance and return directly object the
 * ObjectRef points to. If ObjectRef is invalid or referenced binding does not
 * exist the ObjectRef instance is returned.
 *
 * @author  David Konecny
 */
public final class ObjectRef {

    private Context ctx;
    private String bindingName;
    private String ctxName;
    

    private ObjectRef(Context rootCtx, Context ctx, String ctxName, String bindingName) {
        this.bindingName = bindingName;
        
        this.ctx = ctx;
        this.ctxName = ctxName;
        
        if (this.ctx != null && this.ctxName == null) {
            this.ctxName = ctx.getAbsoluteContextName();
        }
        if (this.ctx == null && this.ctxName != null && rootCtx != null) {
            this.ctx = rootCtx.getSubcontext(ctxName.substring(1));
        }        
    }

    /**
     * Constructs a new instance of <tt>ObjectRef</tt>.
     * @param rootContext root context. See {@link Context#getRootContext}
     * @param absoluteContextName absolute name of context relative to root context. See {@link Context#getAbsoluteContextName}  
     * @param bindingName name of binding
     * @since 1.7 
     */ 
    public ObjectRef(Context rootContext, String absoluteContextName, String bindingName) {
        this (rootContext, null, absoluteContextName, bindingName);
    }

    /**
     * Constructs a new <tt>ObjectRef</tt>. 
     * @param context context  
     * @param bindingName name of binding
     * @since 1.7
     */ 
    public ObjectRef(Context context, String bindingName) {
        this (null, context, null, bindingName);
    }
    
    /**
     * Context in which the object is bound.
     *
     * @return context
     */        
    public Context getContext() {
        return ctx;
    }

    /**
     * Absolute context name of the context in which the 
     * object is bound.
     *
     */
    public String getContextAbsoluteName() {
        return ctxName;
    }

    /**
     * Binding name under which is this object bound.
     *
     * @return binding name
     */        
    public String getBindingName() {
        return bindingName;
    }

    /**
     * Getter for the object referenced by this instance.
     *
     * @return object or null if the object cannot be retrieved or is invalid
     */    
    public Object getObject() {
        if (isValid()) {
            return getContext().getObject(getBindingName(), null);
        } else {
            return null;
        }
    }

    /**
     * Is the object reference valid?
     *
     * @return true if context exists
     */    
    public boolean isValid() {
        return ctx != null;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ObjectRef)) {
            return false;
        }
        ObjectRef or = (ObjectRef)o;
        return (this.ctx == or.ctx && 
            this.bindingName.equals(or.bindingName) && 
            this.ctxName.equals(or.ctxName));
    }
    
    public int hashCode() {
        int result = 7;
        result = 31*result + ctx.hashCode();
        result = 31*result + bindingName.hashCode();
        result = 31*result + ctxName.hashCode();
        return result;
    }
    
    public String toString() {
        return "ObjectRef [context="+getContext()+", ctxName="+
            ctxName+", object="+getObject()+"] " + super.toString(); // NOI18N
    }
    
}
