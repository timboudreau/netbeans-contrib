/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.structural.figtable;

import java.util.Collection;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class FigtableDelegatedParser extends DelegatedParser {
    
    /** Creates a new instance of SectionDelegatedParser */
    public FigtableDelegatedParser() {
    }
    
    private int getType(BlockNode bnode) {
        Environment env = bnode.getEnvironment();
        int type = FigtableStructuralElement.UNKNOWN;
        
        if (env.hasAttribute("#figure-environment"))
            type = FigtableStructuralElement.FIGURE;
        
        if (env.hasAttribute("#table-environment"))
            type = FigtableStructuralElement.TABLE;
        
        return type;
    }
    
    public StructuralElement getElement(Node node) {
        //Only for case that some malicious module marked some Command with our attributes ;-(.
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            int type = getType(bnode);
            
            if (type == FigtableStructuralElement.UNKNOWN)
                return null;
            
            return new FigtableStructuralElement((BlockNode) node, type);
        } else
            return null;
    }
    
    public String[] getSupportedAttributes() {
        return new String[] {
            "#table-environment",
            "#figure-environment",

        };
    }
    
    public StructuralElement updateElement(Node node, Collection/*<ParseError>*/ errors, StructuralElement element) {
        if (!(element instanceof FigtableStructuralElement))
            throw new IllegalStateException("");
        
        ((FigtableStructuralElement) element).fireNameChanged();
        return element;
    }
    
    public Object getKey(Node node) {
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            int type = getType(bnode);
            
            if (type == FigtableStructuralElement.UNKNOWN)
                return null;
            
            return new FigtableKey(type, bnode.getClass(), bnode.getStartingPosition(), bnode.getEndingPosition());
        } else
            return null;
    }

    private static class FigtableKey {
        private int type;
        private Class nodeClass;
        private SourcePosition start;
        private SourcePosition end;
//        private String name;
        
        public FigtableKey(int type, Class nodeClass, SourcePosition start, SourcePosition end/*, String name*/) {
            this.type = type;
            this.nodeClass = nodeClass;
            this.start = start;
            this.end = end;
//            this.name = name;
        }

        public boolean equals(Object o) {
            if (!getClass().equals(o.getClass()))
                return false;
            
            FigtableKey key = (FigtableKey) o;
            
            if (type != key.type)
                return false;
            
            if (!nodeClass.equals(key.nodeClass))
                return false;
            
            if (!start.equals(key.start))
                return false;
            
            if (!end.equals(key.end))
                return false;

            return true;
        }
        
        public int hashCode() {
            return 1; //just for testing!!!!
        }
    }

}
