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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.refactoring;

import org.netbeans.api.retouche.source.Source;
import org.netbeans.modules.refactoring.api.Problem;

/**
 *
 * @author Jan Lahoda
 */
public class Data {

    private Source source;
    private int caret;
    private String displayName;
    private Problem problem;
    private String originalName;

    public Data(Source source, int caret, String displayName, Problem problem, String originalName) {
        this(source, caret, displayName, problem);
        this.originalName = originalName;
    }
    
    public Data(Source source, int caret, String displayName, Problem problem) {
        this.source = source;
        this.caret = caret;
        this.displayName = displayName;
        this.problem = problem;
    }

    public int getCaret() {
        return caret;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Problem getProblem() {
        return problem;
    }

    public Source getSource() {
        return source;
    }

    public String getOriginalName() {
        return originalName;
    }
    
}
