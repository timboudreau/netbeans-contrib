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
