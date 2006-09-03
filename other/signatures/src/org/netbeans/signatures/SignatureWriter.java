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

package org.netbeans.signatures;

import java.io.PrintWriter;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Writes out usages of a type signature.
 * @author Jesse Glick
 */
public final class SignatureWriter {
    
    private final PrintWriter w;
    private final String prefix;
    private final Elements elements;
    
    public SignatureWriter(PrintWriter w, String prefix, Elements elements) {
        this.w = w;
        this.prefix = prefix;
        this.elements = elements;
    }
    
    public void process(String clazz) {
        TypeElement type = elements.getTypeElement(clazz);
        if (type == null) {
            // Cannot find any such.
            return;
        }
        if (!type.getModifiers().contains(Modifier.PUBLIC)) {
            // Package-private class.
            return;
        }
        Name name = type.getQualifiedName();
        if (name.toString().length() == 0) {
            // Anonymous class.
            return;
        }
        emit("Class _ = " + name + ".class;");
        return;
    }
    
    private void emit(String text) {
        w.println(prefix + "{" + text + "}");
    }
    
}
