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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.commands.CommandsTree;

import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedCommand;

/**
 * This class provides methods for maintaining commands with respect to conditions.
 *
 * @author  Martin Entlicher
 */
public final class ConditionedCommands extends Object {
    
    /** The tree of commands. The commands, that are affected by a condition,
     * carry just the name (the real command has to be found through conditions).
     */
    private CommandsTree commands;
    /** An array of conditions, that are defined for commands.
     * The keys are just command names, values are Condition[].
     * The condition contains just var items (no sub-conditions) with empty value
     * and equals compare value.
     * <br>
     * Thus <code>if="testVar1" unless="testVar2"</code> maps to condition:<br>
     * <code>
     * &lt;and&gt;<br>
     * &nbsp;&nbsp;&lt;not&gt; &lt;var name="testVar1" value=""/&gt; &lt;/not&gt;<br>
     * &nbsp;&nbsp;&lt;var name="testVar2" value=""/&gt;<br>
     * &lt;/and&gt;<br>
     * </code>
     *
    private Map conditionsByCommands;
    /** Commands defined when the condition is true. *
    private Map commandsByConditions;
     */
    
    /** A map of command names as keys and associated ConditionedCommand
     * instances as values. */
    private Map conditionedCommandsByName;
    
    /** The builder that can alter these conditioned commands */
    private ConditionedCommandsBuilder builder;
    
    /**
     * Creates a new instance of ConditionedCommands.
     * If the structure of any provided arguments will change later, the structure
     * of ConditionedCommands object will reflect this modification and it's
     * structure will also change.
     */
    ConditionedCommands(CommandsTree commands, Map conditionedCommandsByName,
                        ConditionedCommandsBuilder builder) {
        this.commands = commands;
        this.conditionedCommandsByName = conditionedCommandsByName;
        this.builder = builder;
    }
    
    /** Get the builder that can alter these conditioned commands. */
    public ConditionedCommandsBuilder getBuilder() {
        return builder;
    }
    
    /**
     * Get the tree of commands, that are defined
     * for the provided map of conditional variables.
     * @param conditionalVars Map of conditional variable names and their values.
     * @return The commands tree.
     */
    public CommandsTree getCommands(Map conditionalVars) {
        Collection obtainedCommandNames = new HashSet(); // We need to assure that we do not obtain some commands multiple times
        if (conditionedCommandsByName.size() == 0) {
            return commands;
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd != null) {
                cmd = getCommand(cmd, conditionalVars, obtainedCommandNames);
            }
            CommandsTree condCommands = new CommandsTree(cmd);
            getChildren(commands, condCommands, conditionalVars, obtainedCommandNames);
            return condCommands;
        }
    }
    
    private CommandSupport getCommand(CommandSupport cmd, Map conditionalVars,
                                      Collection obtainedCommandNames) {
        ConditionedCommand ccmd = (ConditionedCommand) conditionedCommandsByName.get(cmd.getName());
        if (ccmd == null) return cmd;
        else {
            if (obtainedCommandNames.contains(cmd.getName())) {
                return null;
            } else {
                obtainedCommandNames.add(cmd.getName());
                return ccmd.getCommand(conditionalVars);
            }
        }
    }
    
    private void getChildren(CommandsTree commands, CommandsTree condCommands,
                             Map conditionalVars, Collection obtainedCommandNames) {
        CommandsTree[] children = commands.children();
        for (int i = 0; i < children.length; i++) {
            CommandSupport cmd = children[i].getCommandSupport();
            if (cmd != null) {
                cmd = getCommand(cmd, conditionalVars, obtainedCommandNames);
                if (cmd == null) continue;
            }
            CommandsTree condChild = new CommandsTree(cmd);
            condCommands.add(condChild);
            if (children[i].hasChildren()) {
                getChildren(children[i],  condChild, conditionalVars, obtainedCommandNames);
            }
        }
    }
    
    /**
     * Get the tree of unconditioned commands, commands that depend on a condition
     * are represented just by a name.
     */
    public CommandsTree getCommands() {
        return commands;
    }
    
    /**
     * Get the map of conditioned commands by their names.
     */
    public ConditionedCommand getConditionedCommand(String name) {
        return (ConditionedCommand) conditionedCommandsByName.get(name);
    }
    
}
