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

import java.lang.reflect.Field;

import org.openide.src.*;

/** The implementation of the field element for class objects.
*
* @author Dafe Simonek
*/
final class FieldElementImpl extends MemberElementImpl
    implements FieldElement.Impl {
    /** Cached type of this field */
    private Type type;

    /** One JavaDoc empty implementation for all objects */
    private static final ClassJavaDocImpl.Field FIELD_JAVADOC_IMPL = new ClassJavaDocImpl.Field();

    static final long serialVersionUID =-4800326520399939102L;
    /** Default constructor. Asociates with given
    * reflection Field data.
    */
    public FieldElementImpl (final Field data) {
        super(data);
    }

    /** Type of the variable.
    * @return the type
    */
    public Type getType () {
        if (type == null)
            type = Type.createFromClass(((Field)data).getType());
        return type;
    }

    /** Not supported. Throws SourceException.
    */
    public void setType (Type type) throws SourceException {
        throwReadOnlyException();
    }

    /** PENDING - don't know how to implement...
    */
    public String getInitValue () {
        return ""; // NOI18N
    }

    /** Not supported. Throws SourceException.
    */
    public void setInitValue (String value) throws SourceException {
        throwReadOnlyException();
    }

    /** @return java doc for the field
    */
    public JavaDoc.Field getJavaDoc () {
        return FIELD_JAVADOC_IMPL;
    }

    public Object readResolve() {
        return new FieldElement(this, null);
    }
}
