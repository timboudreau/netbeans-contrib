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
