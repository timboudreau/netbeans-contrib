/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.scala.editing.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.ParserManager.State;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class ASTVisitor {

    private ASTNode ast = ASTNode.create(null, "Root", 0);
    private State state = State.NOT_PARSED;
    private boolean[] cancel = new boolean[]{false};

    public void visit(ASTNode entry) {
        if (state == State.PARSING) {
            return;
        }
        List<ASTItem> path = new ArrayList<ASTItem>();
        path.add(entry);
        visitRecursively(entry, path, xpath(path), 0);
    }

    protected void visitRecursively(ASTItem item, List<ASTItem> path, String xpath, int ordinal) {
        visitNote(path, xpath, ordinal, true);
        Map<String, Integer> nameToOrdinal = new HashMap<String, Integer>();
        for (ASTItem child : item.getChildren()) {
            if (cancel[0]) {
                return;
            }
            String name = nameOf(child);
            Integer ord = nameToOrdinal.get(name);
            ord = ord == null ? Integer.valueOf(0) : Integer.valueOf(ordinal++);            
            nameToOrdinal.put(name, ord);
            
            path.add(child);
            visitRecursively(child, path, xpath(path), ord);
            path.remove(path.size() - 1);
        }
        visitNote(path, xpath, ordinal, false);
    }

    private String xpath(List<ASTItem> path) {
        StringBuilder sb = new StringBuilder(256);
        for (ASTItem item : path) {
            sb.append(nameOf(item)).append(".");
        }
        return sb.length() > 1 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }
    
    private String nameOf(ASTItem item) {
        return item instanceof ASTToken ? ((ASTToken) item).getTypeName() : ((ASTNode) item).getNT();
    }
    
    abstract void visitNote(List<ASTItem> path, String xpath, int ordinal, boolean enter);
}
