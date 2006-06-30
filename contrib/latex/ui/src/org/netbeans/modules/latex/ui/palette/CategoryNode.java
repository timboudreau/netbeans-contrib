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

package org.netbeans.modules.latex.ui.palette;

import java.util.Collections;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.ui.TexCloneableEditor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jan Lahoda
 */
public class CategoryNode extends AbstractNode {

    private String cathegory;

    /**
     * Creates a new instance of CategoryNode
     */
    public CategoryNode(String cathegory) {
        super(new CathegoryChildren(cathegory));
        this.cathegory = cathegory;
    }
    
    public String getDisplayName() {
        return IconsStorage.getDefault().getCathegoryDisplayName(cathegory);
    }
    
    private static final class CathegoryChildren extends Children.Keys {

        private String cathegory;
        
        public CathegoryChildren(String cathegory) {
            this.cathegory = cathegory;
        }
        
        protected void addNotify() {
            setKeys(IconsStorage.getDefault().getIconNamesForCathegory(cathegory));
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] {new IconNode((String) key)};
        }
        
    }
    
}
