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
package org.netbeans.modules.erlang.platform.index;

import java.util.List;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.PositionManager;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;


/**
 *
 * @author Caoyuan Deng
 */
public class ErlangPositionManager implements PositionManager {

    public ErlangPositionManager() {
    }

    public OffsetRange getOffsetRange(Element file, Element object) {
        if (object instanceof AstRootElement) {
            ASTNode target = ((AstRootElement)object).getNode();

            return new OffsetRange(target.getOffset(), target.getEndOffset());
        } else {
            throw new IllegalArgumentException((("Foreign element: " + object + " of type " +
                object) != null) ? object.getClass().getName() : "null");
        }
    }
    
        
    /**
     * Find the position closest to the given offset in the AST. Place the path from the leaf up to the path in the
     * passed in path list.
     * @todo Build up an AstPath instead!
     */
    @SuppressWarnings("unchecked")
    public static ASTItem findPathTo(ASTItem node, List<ASTItem> path, int offset) {
        ASTItem result = find(node, path, offset);
        path.add(node);

        return result;
    }

    @SuppressWarnings("unchecked")
    private static ASTItem find(ASTItem node, List<ASTItem> path, int offset) {
        int begin = node.getOffset();
        int end = node.getEndOffset();

        if ((offset >= begin) && (offset <= end)) {
            List<ASTItem> children = node.getChildren();

            for (ASTItem child : children) {
                ASTItem found = find(child, path, offset);

                if (found != null) {
                    path.add(child);

                    return found;
                }
            }

            return node;
        } else {
            List<ASTItem> children = node.getChildren();

            for (ASTItem child : children) {
                ASTItem found = find(child, path, offset);

                if (found != null) {
                    path.add(child);

                    return found;
                }
            }

            return null;
        }
    }

    /**
     * Find the path to the given node in the AST
     */
    @SuppressWarnings("unchecked")
    public static boolean find(ASTItem node, List<ASTItem> path, ASTNode target) {
        if (node == target) {
            return true;
        }

        List<ASTItem> children = node.getChildren();

        for (ASTItem child : children) {
            boolean found = find(child, path, target);

            if (found) {
                path.add(child);

                return found;
            }
        }

        return false;
    }

    public boolean isTranslatingSource() {
        return false;
    }

    public int getLexicalOffset(ParserResult result, int astOffset) {
        return astOffset;
    }

    public int getAstOffset(ParserResult result, int lexicalOffset) {
        return lexicalOffset;
    }

}


