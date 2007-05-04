/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this 
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.palette;

import java.util.ArrayList;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;


/**
 *
 * @author nithya
 */
public class OperatorChildren extends Index.ArrayChildren {

    private Category category;

    private String[][] items = new String[][]{
        {"0", "Mashup Operators", "org/netbeans/modules/edm/editor/resources/join_view.png", "Join"},
        {"1", "Mashup Operators", "org/netbeans/modules/edm/editor/resources/mashup.png", "Materialized View"}       
    };

    /**
     * 
     * @param Category 
     */
    public OperatorChildren(Category Category) {
        this.category = Category;
    }

    /**
     * 
     * @return childrenNodes List<Node>
     */
    protected java.util.List<Node> initCollection() {
        ArrayList childrenNodes = new ArrayList( items.length );
        for( int i=0; i<items.length; i++ ) {
            if( category.getName().equals( items[i][1] ) ) {
                Operator item = new Operator();
                item.setNumber(new Integer(items[i][0]));
                item.setCategory(items[i][1]);
                item.setImage(items[i][2]);
                item.setName(items[i][3]);
                childrenNodes.add(new OperatorNode(item));
            }
        }
        return childrenNodes;
    }

}