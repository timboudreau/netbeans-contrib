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
        throw new SourceException();
    }

    /** PENDING - don't know how to implement...
    */
    public String getInitValue () {
        return ""; // NOI18N
    }

    /** Not supported. Throws SourceException.
    */
    public void setInitValue (String value) throws SourceException {
        throw new SourceException();
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

/*
* Log
*  7    src-jtulach1.6         1/13/00  David Simonek   i18n
*  6    src-jtulach1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  5    src-jtulach1.4         8/9/99   Ian Formanek    Generated Serial Version 
*       UID
*  4    src-jtulach1.3         6/9/99   Petr Hrebejk    Empty JavaDoc 
*       implementation added.
*  3    src-jtulach1.2         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  2    src-jtulach1.1         2/17/99  Petr Hamernik   serialization changed.
*  1    src-jtulach1.0         1/22/99  David Simonek   
* $
*/
