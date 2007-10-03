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

package org.netbeans.modules.vcs.advanced.variables;

import java.awt.datatransfer.*;
import java.util.Collection;

import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.ErrorManager;

class ConditionCopySupport extends Object {

    public static DataFlavor CONDITION_COPY_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "CONDITION_COPY_FLAVOR"); // NOI18N

    public static DataFlavor CONDITION_CUT_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "CONDITION_CUT_FLAVOR"); // NOI18N

    public static DataFlavor VAR_COPY_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "VAR_COPY_FLAVOR"); // NOI18N

    public static DataFlavor VAR_CUT_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "VAR_CUT_FLAVOR"); // NOI18N

    static class ConditionDataFlavor extends DataFlavor {
        
        private static final long serialVersionUID = 2413511288102054407L;
        
        ConditionDataFlavor(Class representationClass, String name) {
            super(representationClass, name);
        }
    }

    public static class ConditionTransferable implements Transferable {
        private AbstractNode var;
        private DataFlavor[] flavors;

        ConditionTransferable(DataFlavor flavor, AbstractNode var) {
            this(new DataFlavor[] { flavor }, var);
        }

        ConditionTransferable(DataFlavor[] flavors, AbstractNode var) {
            this.flavors = flavors;
            this.var = var;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i] == flavor) { // comparison based on exact instances, as these are static in this node
                    return true;
                }
            }
            return false;
        }

        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, java.io.IOException {
            if (flavor instanceof ConditionDataFlavor) {
                return var;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /** Paste type for meta components.
     */
    public static class ConditionPaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public ConditionPaste(Transferable t,
                              AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(CONDITION_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        CONDITION_CUT_FLAVOR : CONDITION_COPY_FLAVOR);
            }
            catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            if (!fromCut) { // pasting copy of RADComponent
                copyConditions(sourceNode, targetNode);
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    sourceNode.destroy();
                    copyConditions(sourceNode, targetNode);
                    return null;
                }
                // return new copy flavor, as the first one was used already
                return new ConditionTransferable(CONDITION_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    /** Paste type for meta components.
     */
    public static class VarPaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public VarPaste(Transferable t,
                        AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(VAR_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        VAR_CUT_FLAVOR : VAR_COPY_FLAVOR);
            }
            catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            if (!fromCut) { // pasting copy of RADComponent
                copyVars(sourceNode, targetNode);
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    sourceNode.destroy();
                    copyVars(sourceNode, targetNode);
                    return null;
                }
                // return new copy flavor, as the first one was used already
                return new ConditionTransferable(VAR_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    private static void copyConditions(AbstractNode sourceNode, AbstractNode targetNode) {
        Condition c;
        if (sourceNode instanceof ConditionNode) {
            c = ((ConditionNode) sourceNode).getCondition();
        } else return ;
        if (c == null) return ;
        c = (Condition) c.clone();
        Node newNode = new ConditionNode(c);
        Collection conditionNames = ConditionNode.getAllConditionsNames(targetNode);
        c.setName(VcsUtilities.createUniqueName(c.getName(), conditionNames));
        
        targetNode.getChildren().add(new Node[] { newNode });
    }

    private static void copyVars(AbstractNode sourceNode, AbstractNode targetNode) {
        Condition.Var var;
        if (sourceNode instanceof ConditionNode.VariableNode) {
            var = ((ConditionNode.VariableNode) sourceNode).getVar();
        } else return ;
        if (var == null) return ;
        var = (Condition.Var) var.clone();
        Node newNode = new ConditionNode.VariableNode(((ConditionNode.VariableNode) sourceNode).getEnclosingCondition(), var);
        //Collection conditionNames = ConditionNode.getAllConditionsNames(targetNode);
        //c.setName(VcsUtilities.createUniqueName(c.getName(), conditionNames));
        
        targetNode.getChildren().add(new Node[] { newNode });
    }

}
