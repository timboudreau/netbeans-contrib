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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
