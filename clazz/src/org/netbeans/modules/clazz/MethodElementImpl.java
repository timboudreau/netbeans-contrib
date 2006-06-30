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
import org.openide.src.*;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;

/** Implementation of method element for class objects.
*
* @author Dafe Simonek
*/
final class MethodElementImpl extends ConstructorElementImpl
    implements MethodElement.Impl {
    /** Return type of the method */
    private Type returnType;

    static final long serialVersionUID =7928961724192084484L;

    MethodElementImpl(final Method data) {
        super(data);
    }    
    
    Method getMethod() {
        return (Method)data;
    }

    public void initializeData() {
        super.initializeData();
        getReturn();
    }
    
    /** @return returns teh Type representing return type of this method.
    */
    public Type getReturn () {
        if (returnType == null) {
            MDRepository repo = JavaMetamodel.getManager().getDefaultRepository();
            repo.beginTrans(false);
            try {
                if (!isValid()) {
                    returnType = org.openide.src.Type.VOID;
                } else {
                    returnType = Util.createType(getMethod().getType());
                }
            } finally {
                repo.endTrans();
            }
        }
        return returnType;
    }

    /** Unsupported. Throws an Source exception. */
    public void setReturn (Type ret) throws SourceException {
        throwReadOnlyException();
    }

    public Object readResolve() {
        return new MethodElement(this, null);
    }

}
