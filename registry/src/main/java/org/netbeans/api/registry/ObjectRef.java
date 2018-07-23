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
