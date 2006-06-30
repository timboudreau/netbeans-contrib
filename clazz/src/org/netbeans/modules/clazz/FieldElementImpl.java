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
import org.netbeans.jmi.javamodel.Field;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
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
    public FieldElementImpl (final org.netbeans.modules.classfile.Field data) {
        super(data);
    }
    
    FieldElementImpl(final Field data) {
        super(data);
    }
    
    private Field getField() {
        return (Field)data;
    }

    public void initializeData() {
        super.initializeData();
        getType();
    }
    
    /** Type of the variable.
    * @return the type
    */
    public Type getType () {
        if (type == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    type = org.openide.src.Type.VOID;
                } else {
                    type = Util.createType(getField().getType());
                }
                //XXX
                //type = Type.createFromClass(((org.netbeans.modules.classfile.Field)data).getType());
            } finally {
                repo.endTrans();
            }
        }
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
