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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.impl;

import org.openide.text.Annotatable;
import org.openide.text.Annotation;

/**
 *
 * @author Jan Lahoda
 */
public final class ErrorAnnotation extends Annotation {

    private final String error;

    public ErrorAnnotation(String error) {
        this.error = error;
    }

    public String getAnnotationType() {
        return "org-netbeans-modules-latex-command-parser-error";
    }

    public String getShortDescription() {
        return error;
    }
    
}
