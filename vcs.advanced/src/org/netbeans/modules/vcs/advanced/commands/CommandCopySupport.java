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

package org.netbeans.modules.vcs.advanced.commands;

import java.awt.datatransfer.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedPropertiesCommand;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;

class CommandCopySupport extends Object {

    public static DataFlavor COMMAND_COPY_FLAVOR = new CommandDataFlavor(
        CommandNode.class,
        "COMMAND_COPY_FLAVOR"); // NOI18N

    public static DataFlavor COMMAND_CUT_FLAVOR = new CommandDataFlavor(
        CommandNode.class,
        "COMMAND_COPY_FLAVOR"); // NOI18N

    static class CommandDataFlavor extends DataFlavor {
        
        private static final long serialVersionUID = 6305496575494482601L;
        
        CommandDataFlavor(Class representationClass, String name) {
            super(representationClass, name);
        }
    }

    // -----------

    public static class CommandTransferable implements Transferable {
        private CommandNode cmd;
        private DataFlavor[] flavors;

        CommandTransferable(DataFlavor flavor, CommandNode cmd) {
            this(new DataFlavor[] { flavor }, cmd);
        }

        CommandTransferable(DataFlavor[] flavors, CommandNode cmd) {
            this.flavors = flavors;
            this.cmd = cmd;
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
        throws UnsupportedFlavorException, java.io.IOException
        {
            if (flavor instanceof CommandDataFlavor) {
                return cmd;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    // -----------

    /** Method for checking whether a component can be moved to a container
     * (the component should not be pasted to its own sub-container
     * or even to itself). *
    public static boolean canPasteCut(VcsCommand sourceCmd,
                                      FormModel targetForm,
                                      ComponentContainer targetContainer) {
        if (sourceComponent.getFormModel() != targetForm)
            return true;

        if (targetContainer == null
                || targetContainer == targetForm.getModelContainer())
            return targetForm.getModelContainer().getIndexOf(sourceComponent) < 0;

        RADComponent targetComponent = (RADComponent) targetContainer;

        return sourceComponent != targetComponent
               && sourceComponent.getParentComponent() != targetComponent
               && !sourceComponent.isParentComponent(targetComponent);
    } */

    // -----------
    
    /** Paste type for meta components.
     */
    public static class CommandPaste extends PasteType {
        private Transferable transferable;
        private CommandNode targetNode;
        //private FormModel targetForm;

        public CommandPaste(Transferable t,
                            CommandNode targetNode) {
                            //FormModel targetForm) {
            this.transferable = t;
            this.targetNode = targetNode;
            //this.targetForm = targetForm;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(COMMAND_CUT_FLAVOR);

            CommandNode sourceNode = null;
            try {
                sourceNode = (CommandNode)
                    transferable.getTransferData(fromCut ?
                        COMMAND_CUT_FLAVOR : COMMAND_COPY_FLAVOR);
            }
            catch (java.io.IOException e) { } // ignore - should not happen
            catch (UnsupportedFlavorException e) { } // ignore - should not happen

            //if (sourceCommand == null)
            //    return null;

            if (!fromCut) { // pasting copy of RADComponent
                copyCommands(sourceNode, targetNode);
                /*
                targetForm.getComponentCreator()
                    .copyComponent(sourceComponent, targetContainer);
                 */
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    //CommandNode newNode = new CommandNode(Children.LEAF, sourceCommand.getCommand());
                    sourceNode.destroy();
                    copyCommands(sourceNode, targetNode);
                    //targetNode.getChildren().add(new Node[] { newNode });
                    return null;
                }
                /*
                FormModel sourceForm = sourceComponent.getFormModel();
                if (sourceForm != targetForm) { // taken from another form
                    Node sourceNode = sourceComponent.getNodeReference();
                    // delete component in the source
                    if (sourceNode != null)
                        sourceNode.destroy();
                    else throw new IllegalStateException();

                    sourceComponent.initialize(targetForm);
                }
                else { // moving within the same form
                    if (!canPasteCut(sourceComponent, targetForm, targetContainer))
                        return transferable; // ignore paste

                    // remove source component from its parent
                    sourceForm.removeComponent(sourceComponent);
                }
                 */

                // return new copy flavor, as the first one was used already
                return new CommandTransferable(COMMAND_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    private static UserCommand copyVcsCommand(VcsCommand cmd) {
        if (cmd == null) return null;
        UserCommand newCmd = new UserCommand();
        newCmd.setName(cmd.getName());
        newCmd.setDisplayName(cmd.getDisplayName());
        String[] propNames = cmd.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            newCmd.setProperty(propNames[i], cmd.getProperty(propNames[i]));
        }
        return newCmd;
    }
    
    private static void copyCommands(CommandNode sourceNode, CommandNode targetNode) {
        VcsCommand scmd = sourceNode.getCommand();
        Condition mc = sourceNode.getMainCondition();
        Collection cproperties = sourceNode.getConditionedProperties();
        Children ch;
        if (Children.LEAF.equals(sourceNode.getChildren())) {
            ch = Children.LEAF;
        } else {
            ch = new Children.Array();
        }
        UserCommand cmd = copyVcsCommand(scmd);
        if (cmd != null) {
            cmd.setName(VcsUtilities.createUniqueName(cmd.getName(), targetNode.getAllCommandsNames()));
        }
        CommandNode newNode;
        if (cproperties != null) {
            //CommandsTree ct = new CommandsTree(new UserCommandSupport(cmd, null));
            //ConditionedCommandsBuilder ccbuilder = new ConditionedCommandsBuilder()
            ConditionedPropertiesCommand cpc = new ConditionedPropertiesCommand(new UserCommandSupport(cmd, null));
            for (Iterator it = cproperties.iterator(); it.hasNext(); ) {
                ConditionedProperty cp = (ConditionedProperty) it.next();
                Condition c = cp.getCondition();
                if (c != null) c = (Condition) c.clone();
                Map valuesByConditions = new IdentityHashMap();
                Map vbc = cp.getValuesByConditions();
                for (Iterator vbcIt = vbc.keySet().iterator(); vbcIt.hasNext(); ) {
                    Condition vc = (Condition) vbcIt.next();
                    Object value = vbc.get(vc);
                    if (vc != null) vc = (Condition) vc.clone();
                    if (value instanceof Cloneable) {
                        try {
                            Method cloneM = value.getClass().getMethod("clone",  new Class[0]);
                            value = cloneM.invoke(value, new Object[0]);
                        } catch (NoSuchMethodException nsmex) {
                        } catch (SecurityException sex) {
                        } catch (IllegalAccessException iaex) {
                        } catch (IllegalArgumentException iaex) {
                        } catch (InvocationTargetException itex) {
                        }
                    }
                    valuesByConditions.put(vc, value);
                }
                cp = new ConditionedProperty(cp.getName(), c, valuesByConditions);
                cpc.addConditionedProperty(cp);
            }
            if (mc != null) mc = (Condition) mc.clone();
            newNode = new CommandNode(ch, cmd, mc, cpc);
        } else {
            newNode = new CommandNode(ch, cmd);
        }
        targetNode.getChildren().add(new Node[] { newNode });
        Node[] sourceSubnodes = sourceNode.getChildren().getNodes();
        for (int i = 0; i < sourceSubnodes.length; i++) {
            copyCommands((CommandNode) sourceSubnodes[i], newNode);
        }
    }

}
