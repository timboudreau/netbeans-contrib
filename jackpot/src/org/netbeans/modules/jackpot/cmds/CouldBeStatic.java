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

package org.netbeans.modules.jackpot.cmds;

import org.netbeans.api.java.source.query.Query;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import javax.lang.model.element.*;
import java.util.Set;

public class CouldBeStatic extends Query<Void,Object> {
    
    @Override
    public Void visitMethod(MethodTree meth, Object p) {
	if (meth != null) {
            Set<Modifier> flags = meth.getModifiers().getFlags();
            if (!flags.contains(Modifier.ABSTRACT) && !flags.contains(Modifier.STATIC)) {
            ExecutableElement e = (ExecutableElement)getElement(meth);
            if(!overrides(e) && !isOverridden(e) && getInstanceReferenceCount(meth)==0)
                addResult(getElement(meth), "Could be Static");
            }
        }
        return null;
    }
    
    @Override
    public Void visitClass(ClassTree clazz, Object p) {
        if (getElement(clazz).getKind() == ElementKind.INTERFACE)
	    super.visitClass(clazz, p);
        return null;
    }

}
