/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import org.netbeans.jmi.javamodel.CallableFeature;
import org.openide.src.*;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Parameter;

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
    
    /** @return the array specifying the parameters
    */
    public MethodParameter[] getParameters () {
        if (parameters == null) {
            Parameter[] pars = (Parameter[])getBehavioral().getParameters().toArray(new Parameter[0]);
            MethodParameter[] mp = new MethodParameter[pars.length];
            for (int i = 0; i < pars.length; i++) {
                Parameter p=pars[i];
                mp[i] = new MethodParameter(p.getName(), Util.createType(p.getType()), p.isFinal());
            }
            parameters = mp;
        }
        return parameters;
    }
    
    protected Identifier createName(Object data) {
	if (this instanceof MethodElementImpl) {
	    return super.createName(data);
	}    
        return Identifier.create(((JavaClass)(getBehavioral().getDeclaringClass())).getName());
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
            JavaClass[] reflEx = (JavaClass[])getBehavioral().getExceptions().toArray(new JavaClass[0]);
            exceptions = new Identifier[reflEx.length];
            // build our exception types
            for (int i = 0; i < reflEx.length; i++) {
                exceptions[i] = Identifier.create(Util.createClassName(reflEx[i].getName()));
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
