/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.fortress.editing;

import com.sun.fortress.nodes.Node;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import org.netbeans.modules.fortress.editing.visitors.Scope;
import org.netbeans.modules.fortress.editing.visitors.Signature;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;

/**
 *  
 * @author Caoyuan Deng
 */
public class FortressSemanticAnalyzer implements SemanticAnalyzer {

    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public void run(CompilationInfo info) throws Exception {
        resume();

        if (isCancelled()) {
            return;
        }

        FortressParserResult result = AstUtilities.getParserResult(info);
        if (result == null) {
            return;
        }

        Node root = result.getRootNode();
        if (root == null) {
            return;
        }

        if (isCancelled()) {
            return;
        }

        Scope rootScope = result.getRootScope();
        
        Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<OffsetRange, Set<ColoringAttributes>>(100);
        visitScopeRecursively(info, rootScope, highlights);

        if (highlights.size() > 0) {
//            if (result.getTranslatedSource() != null) {
//                Map<OffsetRange, ColoringAttributes> translated = new HashMap<OffsetRange, ColoringAttributes>(2 * highlights.size());
//                for (Map.Entry<OffsetRange, ColoringAttributes> entry : highlights.entrySet()) {
//                    OffsetRange range = LexUtilities.getLexerOffsets(info, entry.getKey());
//                    if (range != OffsetRange.NONE) {
//                        translated.put(range, entry.getValue());
//                    }
//                }
//
//                highlights = translated;
//            }

            this.semanticHighlights = highlights;
        } else {
            this.semanticHighlights = null;
        }
    }

    private void visitScopeRecursively(CompilationInfo info, Scope scope, Map<OffsetRange, Set<ColoringAttributes>> highlights) {
        for (Signature definition : scope.getDefinitions()) {
            OffsetRange range = definition.getNameRange();
            switch (definition.getKind()) {
                case MODULE:
                    highlights.put(range, ColoringAttributes.CLASS_SET);
                    break;
                case CLASS:
                    highlights.put(range, ColoringAttributes.CLASS_SET);
                    break;
                case METHOD:
                    highlights.put(range, ColoringAttributes.METHOD_SET);
                    break;
                case FIELD:
                    highlights.put(range, ColoringAttributes.FIELD_SET);
                    break;
                default:
            }

        }

        for (Scope child : scope.getScopes()) {
            visitScopeRecursively(info, child, highlights);
        }
    }
}
