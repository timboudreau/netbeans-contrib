/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

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
