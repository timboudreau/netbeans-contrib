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

package org.netbeans.spi.jackpot;

/**
 * Thrown when a rule recursively matches itself.
 */
public class RecursiveRuleException extends RuntimeException {
    private String script;
    private int lineNumber;

    /**
     * Creates a new instance of <code>RecursiveRuleException</code>.
     *
     * @param script the name of the rule file.
     * @param lineNumber the starting line number of the rule.
     */
    public RecursiveRuleException(String script, int lineNumber) {
        super(script + ":" + lineNumber + ": recursive rule detected");
        this.script = script;
        this.lineNumber = lineNumber;
    }
    
    /**
     * Returns the script.
     */
    public String getString() {
        return script;
    }
    
    /**
     * Returns the starting line number of the detected rule.
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
