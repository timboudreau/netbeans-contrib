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

package org.netbeans.modules.jackpot.rules.parser;

import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.util.List;

class Rule {
    Rule next;
    JCTree pattern;
    JCTree replacement;
    JCTree suchthat;
    int startLine;
    Rule(JCTree p, JCTree r, JCTree s, int line) {
	pattern = p;
	replacement = r;
	suchthat = s;
        this.startLine = line;
    }
    boolean isValid() { return this != invalidRule; }
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(pattern);
	if(replacement!=null) {
	    sb.append("=>");
	    sb.append(replacement);
	}
	if(suchthat!=null) {
	    sb.append("::");
	    sb.append(suchthat);
	}
	return sb.toString().replace('\n',' ')/*replaceAll("\\n|  +"," ")*/;
    }
    
    private static class ErroneousRule extends JCTree.JCErroneous {
        ErroneousRule() {
            super(List.<JCTree>nil());
        }
    }
    public static final Rule invalidRule = 
        new Rule(new ErroneousRule(), null, null, -1);
}
