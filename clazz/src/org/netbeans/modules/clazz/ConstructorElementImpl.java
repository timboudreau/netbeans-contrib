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

package org.netbeans.modules.clazz;

import org.netbeans.api.mdr.MDRepository;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.openide.src.*;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;

/** Implementation of the constructor element for class objects.
* It's simple bridge to the java reflection Constructor, delegates
* all tasks to it.
*
* @author Dafe Simonek
*/
class ConstructorElementImpl extends MemberElementImpl
    implements ConstructorElement.Impl {

    /** The array of parameters of this constructor (or method)*/
    private MethodParameter[] parameters;
    /** The array of exceptions which can be thrown */
    private Identifier[] exceptions;
    /** One JavaDoc empty implementation for all objects */
    private static final ClassJavaDocImpl.Method METHOD_JAVADOC_IMPL = new ClassJavaDocImpl.Method();
    
    static final long serialVersionUID =5714347955571851240L;
    
    ConstructorElementImpl (final CallableFeature data ) {
        super(data);
    }

    CallableFeature getBehavioral() {
        return (CallableFeature)data;
    }
    
    public void initializeData() {
        super.initializeData();
        getExceptions();
        getParameters();
    }
    
    /** @return the array specifying the parameters
    */
    public MethodParameter[] getParameters () {
        if (parameters == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    parameters = new MethodParameter[0];
                } else {
                    Parameter[] pars = (Parameter[])getBehavioral().getParameters().toArray(new Parameter[0]);
                    MethodParameter[] mp = new MethodParameter[pars.length];
                    for (int i = 0; i < pars.length; i++) {
                        Parameter p=pars[i];
                        mp[i] = new MethodParameter(p.getName(), Util.createType(p.getType()), p.isFinal());
                    }
                    parameters = mp;
                }
            } finally {
                repo.endTrans();
            }
        }
        return parameters;
    }
    
    protected Identifier createName(Object data) {
	if (this instanceof MethodElementImpl) {
	    return super.createName(data);
	}
        MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
        repo.beginTrans(false);
        try {
            if (!isValid()) {
                return Identifier.create(""); // NOI18N
            }
            return Identifier.create(((JavaClass)(getBehavioral().getDeclaringClass())).getSimpleName());
        } finally {
            repo.endTrans();
        }
    }

    /** Unsupported, throws SourceException
    */
    public void setParameters (MethodParameter[] params) throws SourceException {
        throwReadOnlyException();
    }

    /** @return the array of the exceptions throwed by the method.
    */
    public Identifier[] getExceptions () {
        if (exceptions == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    return new Identifier[0];
                }
                JavaClass[] reflEx = (JavaClass[])getBehavioral().getExceptions().toArray(new JavaClass[0]);
                exceptions = new Identifier[reflEx.length];
                // build our exception types
                for (int i = 0; i < reflEx.length; i++) {
                    exceptions[i] = Identifier.create(Util.createClassName(reflEx[i].getName()));
                }
            } finally {
                repo.endTrans();
            }
        }
        return exceptions;
    }

    /** Unsupported, throws SourceException
    */
    public void setExceptions (Identifier[] exceptions) throws SourceException {
        throwReadOnlyException();
    }

    /** Unsupported, throws SourceException
    */
    public void setBody (String s) throws SourceException {
        throwReadOnlyException();
    }

    /** Unsupported, always return empty string.
    */
    public String getBody () {
        return ""; // NOI18N
    }

    /** Empty implementation
    * @return Empty JavaDoc implementation.
    */
    public JavaDoc.Method getJavaDoc () {
        return METHOD_JAVADOC_IMPL;
    }

    public Object readResolve() {
        return new ConstructorElement(this, null);
    }
}
