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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import java.util.Set;
import javax.lang.model.element.Modifier;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class ExtractMethodRefactoring extends AbstractRefactoring {

    public ExtractMethodRefactoring(Lookup source, TreePathHandle handle, int start, int end, String methodName, Set <Modifier> modifiers) {
        super (source);
        this.handle = handle;
        this.start = start;
        this.end = end;
        this.methodName = methodName;
        this.modifiers = modifiers;
    }
    
    public final int end;
    public final int start;
    public final TreePathHandle handle;
    public final String methodName;
    public final Set <Modifier> modifiers;
}
