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
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2008.
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
package org.netbeans.modules.latex.editor.listing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.DelegatedParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ListingDelegatedParser extends DelegatedParser {
    
    private Map<FileObject, Set<ListingStructuralElement>> elements;
    
    /** Creates a new instance of SectionDelegatedParser */
    public ListingDelegatedParser() {
    }
    
    private Set<ListingStructuralElement> getElements(FileObject file) {
        if (elements == null) {
            elements = new HashMap<FileObject, Set<ListingStructuralElement>>();
        }
        
        Set<ListingStructuralElement> result = elements.get(file);
        
        if (result == null) {
            elements.put(file, result = new HashSet<ListingStructuralElement>());
        }
        
        return result;
    }
    
    @Override
    public StructuralElement getElement(Node node) {
        Set<ListingStructuralElement> elements = getElements((FileObject) node.getStartingPosition().getFile());
        
        //Only for case that some malicious module marked some Command with our attributes ;-(.
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            Environment env = bnode.getEnvironment();
            
            if (env.hasAttribute("listing")) {
                ListingStructuralElement el = new ListingStructuralElement(bnode);
                
                elements.add(el);
                
                return el;
            } else
                return null;
        } else
            return null;
    }
    
    @Override
    public String[] getSupportedAttributes() {
        return new String[] {
            "listing",
        };
    }
    
    @Override
    public StructuralElement updateElement(Node node, StructuralElement element) {
        if (!(element instanceof ListingStructuralElement))
            throw new IllegalStateException("");
        
        getElements((FileObject) node.getStartingPosition().getFile()).add((ListingStructuralElement) element);
        
        ((ListingStructuralElement) element).fireNameChanged();
        return element;
    }
    
    @Override
    public Object getKey(Node node) {
        if (node instanceof BlockNode) {
            BlockNode bnode = (BlockNode) node;
            Environment env = bnode.getEnvironment();
            
            if (env.hasAttribute("listing"))
                return new ListingKey(bnode.getClass(), bnode.getStartingPosition(), bnode.getEndingPosition());
            else
                return null;
        } else
            return null;
    }

    @Override
    public void parsingFinished() {
        this.elements = null;
    }
    
    @Override
    public void reset() {
    }
    
    private static final class ListingKey {
        private Class nodeClass;
        private SourcePosition start;
        private SourcePosition end;
//        private String name;
        
        public ListingKey(Class nodeClass, SourcePosition start, SourcePosition end/*, String name*/) {
            this.nodeClass = nodeClass;
            this.start = start;
            this.end = end;
//            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (!getClass().equals(o.getClass()))
                return false;
            
            ListingKey key = (ListingKey) o;
            
            if (!nodeClass.equals(key.nodeClass))
                return false;
            
            if (!start.equals(key.start))
                return false;
            
            if (!end.equals(key.end))
                return false;

            return true;
        }
        
        @Override
        public int hashCode() {
            return 1; //just for testing!!!!
        }
    }

}
