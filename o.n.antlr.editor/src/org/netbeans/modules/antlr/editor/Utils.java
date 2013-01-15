/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.antlr.editor;

import java.io.PrintWriter;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.Tree;
import org.netbeans.modules.antlr.editor.gen.ANTLRv3Parser;
import org.netbeans.modules.csl.api.OffsetRange;

/**
 *
 * @author marekfukala
 */
public class Utils {
 
    /**
     * Returns a pointer to the start and end of the token image in the
     * underlaying stream. The token.getStopIndex() points to the last character
     * of the token which is a bit confusing.
     *
     * Use this method to get CommonToken's boundaries instead of using the
     * getStart/StopIndex methods.
     *
     * @return two members array - arr[0] is the start offset, arr[1] is the end
     * offset
     */
    public static OffsetRange getCommonTokenOffsetRange(CommonToken token) {
        if (token.getType() == CommonToken.EOF) {
            //"eof token" points at the end offset of the source, with zero length
            return new OffsetRange(token.getStartIndex(), token.getStopIndex());
        } else {
            return new OffsetRange(token.getStartIndex(), token.getStopIndex() + 1);
        }
    }
    
    
     public static void dumpTree(Tree node) {
        PrintWriter pw = new PrintWriter(System.out);
        dumpTree(node, pw);
        pw.flush();
    }

    public static void dumpTree(Tree node, PrintWriter pw) {
        dump(node, 0, pw);

    }

    private static void dump(Tree tree, int level, PrintWriter pw) {
        for (int i = 0; i < level; i++) {
            pw.print("    ");
        }
        pw.print(tree2String(tree));
        pw.println();
        for (int i = 0; i < tree.getChildCount(); i++) {
            Tree child = tree.getChild(i);
            dump(child, level + 1, pw);
        }
    }
    
    public  static String tree2String(Tree t) {        
        return String.format("type = %s, line = %s, fi = %s, ti = %s, text = %s", 
                ANTLRv3Parser.tokenNames[t.getType()], 
                t.getLine(), 
                t.getTokenStartIndex(), 
                t.getTokenStopIndex(), 
                t.getText());
    }
    
}
