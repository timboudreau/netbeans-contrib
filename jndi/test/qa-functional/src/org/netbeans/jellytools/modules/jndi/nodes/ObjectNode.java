/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.modules.jndi.nodes;

import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.jndi.actions.CopyLookupCodeAction;
import org.netbeans.jellytools.nodes.Node;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import org.netbeans.jemmy.operators.JTreeOperator;

/** ObjectNode Class
 * @author dave */
public class ObjectNode extends Node {
    
    private static final Action copyLookupCodeAction = new CopyLookupCodeAction();
    private static final Action deleteAction = new DeleteAction();
    private static final Action propertiesAction = new PropertiesAction();

    /** creates new ObjectNode
     * @param tree JTreeOperator of tree
     * @param treePath String tree path */
    public ObjectNode(JTreeOperator tree, String treePath) {
        super(tree, treePath);
    }

    /** creates new ObjectNode
     * @param tree JTreeOperator of tree
     * @param treePath TreePath of node */
    public ObjectNode(JTreeOperator tree, TreePath treePath) {
        super(tree, treePath);
    }

    /** creates new ObjectNode
     * @param parent parent Node
     * @param treePath String tree path from parent Node */
    public ObjectNode(Node parent, String treePath) {
        super(parent, treePath);
    }

    /** tests popup menu items for presence */
    public void verifyPopup() {
        verifyPopup(new Action[]{
            copyLookupCodeAction,
            deleteAction,
            propertiesAction
        });
    }

    /** performs CopyLookupCodeAction with this node */
    public void copyLookupCode() {
        copyLookupCodeAction.perform(this);
    }

    /** performs DeleteAction with this node */
    public void delete() {
        deleteAction.perform(this);
    }

    /** performs PropertiesAction with this node */
    public void properties() {
        propertiesAction.perform(this);
    }
}
