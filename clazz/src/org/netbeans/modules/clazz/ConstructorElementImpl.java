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

import org.openide.src.*;

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

    private Object declaringClass;
    
    static final long serialVersionUID =5714347955571851240L;
    /** Default constructor, asocitates this object
    * with java reflection Constructor instance.
    */
    public ConstructorElementImpl (final org.netbeans.modules.classfile.Method data, Object declaringClass ) {
        super(data);
        this.declaringClass = declaringClass;
    }

    /** @return the array specifying the parameters
    */
    public MethodParameter[] getParameters () {
        if (parameters == null) {
            parameters = new Util.SignatureToType(((org.netbeans.modules.classfile.Method)data).getDescriptor()).getMethodParameters();
        }
        return parameters;
    }
    
    protected Identifier createName(Object data) {
	if (this instanceof MethodElementImpl) {
	    return super.createName(data);
	}    
        //if( 
	String n = ((org.netbeans.modules.classfile.ClassFile)declaringClass).getName().getExternalName();//(org.netbeans.modules.classfile.Field)data).getClass().getName();
	int lastDot = n.lastIndexOf('.'); // NOI18N
	return lastDot == -1 ? 
	    Identifier.create(n) :
	    Identifier.create(n.substring(lastDot + 1));
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
            org.netbeans.modules.classfile.CPClassInfo[] reflEx = ((org.netbeans.modules.classfile.Method)data).getExceptionClasses();
            // obtain via reflection
            //if (data instanceof org.netbeans.modules.classfile.Method)
            //reflEx = ((org.netbeans.modules.classfile.Method)data).getExceptionClasses();
            //XXX
            //else
            //    reflEx = ((Constructor)data).getExceptionTypes();
            exceptions = new Identifier[reflEx.length];
            // build our exception types
            for (int i = 0; i < reflEx.length; i++) {
                exceptions[i] = Identifier.create(reflEx[i].getName());
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

/*
* Log
*  10   src-jtulach1.9         1/13/00  David Simonek   i18n
*  9    src-jtulach1.8         11/27/99 Patrik Knakal   
*  8    src-jtulach1.7         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  7    src-jtulach1.6         6/9/99   Petr Hrebejk    Empty JavaDoc 
*       implementation added.
*  6    src-jtulach1.5         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  5    src-jtulach1.4         3/15/99  Petr Hamernik   
*  4    src-jtulach1.3         2/17/99  Petr Hamernik   serialization changed.
*  3    src-jtulach1.2         2/10/99  David Simonek   
*  2    src-jtulach1.1         2/3/99   David Simonek   
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
