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

package org.netbeans.jellytools.modules.corba.nodes;

import org.netbeans.jellytools.actions.CompileAction;
import org.netbeans.jellytools.actions.CopyAction;
import org.netbeans.jellytools.actions.CutAction;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.actions.PasteAction;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.actions.RenameAction;
import org.netbeans.jellytools.modules.corba.actions.ImplementationAction;
import org.netbeans.jellytools.modules.corba.actions.ParseAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

/** Node representing IDL source */
public class IDLNode extends Node {
    
    /** creates new IDLNode
     * @param treeOperator JTreeOperator tree
     * @param treePath String tree path */    
    public IDLNode(JTreeOperator treeOperator, String treePath) {
       super(treeOperator, treePath);
    }

    /** creates new IDLNode
     * @param parent parent Node
     * @param treeSubPath String tree path from parent node */    
    public IDLNode(Node parent, String treeSubPath) {
       super(parent, treeSubPath);
    }

    static final OpenAction openAction = new OpenAction();
    static final CompileAction compileAction = new CompileAction();
    static final ParseAction parseAction = new ParseAction ();
    static final ImplementationAction implementationAction = new ImplementationAction ();
    static final CutAction cutAction = new CutAction();
    static final CopyAction copyAction = new CopyAction();
    static final PasteAction pasteAction = new PasteAction();
    static final DeleteAction deleteAction = new DeleteAction();
    static final RenameAction renameAction = new RenameAction();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    /** performs OpenAction with this node */
    public void open() {
        openAction.perform(this);
    }

    /** performs CompileAction with this node */
    public void compile() {
        compileAction.perform(this);
    }

    /** performs ParseAction with this node */
    public void parse() {
        parseAction.perform(this);
    }

    /** performs ImplementationAction with this node */
    public void generateImplementation() {
        implementationAction.perform(this);
    }

    /** performs ImplementationAction with this node */
    public void updateImplementation() {
        implementationAction.perform(this);
    }

    /** performs ImplementationAction with this node */
    public void generateAndUpdateImplementation() {
        implementationAction.perform(this);
    }

    /** performs CutAction with this node */
    public void cut() {
        cutAction.perform(this);
    }

    /** performs CopyAction with this node */
    public void copy() {
        copyAction.perform(this);
    }

    /** performs PasteAction with this node */
    public void paste() {
        pasteAction.perform(this);
    }

    /** performs DeleteAction with this node */
    public void delete() {
        deleteAction.perform(this);
    }

    /** performs RenameAction with this node */
    public void rename() {
        renameAction.perform(this);
    }

    /** performs PropertiesAction with this node */
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
