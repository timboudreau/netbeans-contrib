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
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.*;
import java.io.*;

public class Coder {
    Pretty pretty;

    public Coder(PluginCompiler pc) { 
        this.pc = pc;
        pretty = new Pretty(pc.getWriter(), true) {
            public void visitIdent(JCIdent tree) {
                try {
                    Name n = tree.sym==null ? tree.name : tree.sym.name;
                    for(int i = metavars.length; --i>=0; ) {
                        if(metavars[i]==n) {
                            if(isList[i]<=0) print(metavals[i]);
                            else {
                                print("firstN(");
                                print(metavals[i]);
                                print(", len_");
                                print(String.valueOf(i));
                                print(")");
                            }
                            return;
                        }
                    }
                    super.visitIdent(tree);
                } catch (IOException e) {
                    error = e;
                }
            }
        };
    }
    
    public void generate(JCTree t) throws IOException {
	t.accept(pretty);
        if (error != null)
            throw error;
	pretty.print(";\n");
    }

    protected Name[] metavars;
    protected String[] metavals;
    int[] isList;
    IOException error;
    private PluginCompiler pc;
}

