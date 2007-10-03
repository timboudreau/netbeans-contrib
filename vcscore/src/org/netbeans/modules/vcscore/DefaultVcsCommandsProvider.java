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

package org.netbeans.modules.vcscore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;

/**
 * The default implementation of VcsCommandsProvider based on commands provided
 * by CommandsTree.
 *
 * @author  Martin Entlicher
 */
public class DefaultVcsCommandsProvider extends VcsCommandsProvider implements CommandsTree.Provider {
    
    private CommandsTree commands;
    private Map commandSupportsByNames;
    private Map commandSupportsByClasses;
    private String[] commandNames;
    private boolean expertMode;
    private String type = "Unknown"; // NOI18N
    private PropertyChangeSupport changeSupport;
    
    /** Creates a new instance of DefaultVcsCommandsProvider */
    public DefaultVcsCommandsProvider(CommandsTree commands) {
        changeSupport = new PropertyChangeSupport(this);
        setCommands(commands);
    }
    
    /**
     * Set the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public void setExpertMode(boolean expertMode) {
        this.expertMode = expertMode;
    }
    
    /**
     * Get the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public boolean isExpertMode() {
        return expertMode;
    }
    
    /**
     * Create a new VCS command of the given class type.
     * @return The command or <code>null</code> when the command of the given
     * class type does not exist.
     */
    public synchronized Command createCommand(Class cmdClass) {
        CommandSupport support = (CommandSupport) commandSupportsByClasses.get(cmdClass);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /** Create a new VCS command of the given name.
     * @return The command or <code>null</code> when the command of the given
     * name does not exist.
     *
     */
    public synchronized Command createCommand(String cmdName) {
        CommandSupport support = (CommandSupport) commandSupportsByNames.get(cmdName);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /**
     * Get the list of VCS command names.
     */
    public synchronized String[] getCommandNames() {
        return commandNames;
    }
    
    public synchronized CommandsTree getCommands() {
        return commands;
    }
    
    public void setCommands(CommandsTree commands) {
        Map commandSupportsByNames = new HashMap();
        Map commandSupportsByClasses = new HashMap();
        fillCommands(commands, commandSupportsByNames, commandSupportsByClasses);
        synchronized (this) {
            this.commands = commands;
            this.commandNames = (String[]) commandSupportsByNames.keySet().toArray(new String[0]);
            this.commandSupportsByNames = commandSupportsByNames;
            this.commandSupportsByClasses = commandSupportsByClasses;
        }
        changeSupport.firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
    }
    
    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("The VCS type must not be null!");
        }
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    private void fillCommands(CommandsTree commands, Map commandSupportsByNames,
                              Map commandSupportsByClasses) {
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            CommandSupport support = subCommands[i].getCommandSupport();
            if (support != null) {
                commandSupportsByNames.put(support.getName(), support);
                if (support instanceof UserCommandSupport) {
                    Class[] commandClasses = ((UserCommandSupport) support).getImplementedCommandClasses();
                    for (int k = 0; k < commandClasses.length; k++) {
                        commandSupportsByClasses.put(commandClasses[k], support);
                    }
                }
            }
            if (subCommands[i].hasChildren()) fillCommands(subCommands[i],
                                                           commandSupportsByNames,
                                                           commandSupportsByClasses);
        }
    }
    
    /** Add a property change listener to this provider.
     * The listener is called whenever the provided commands change.
     *
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    /** Remove the property change listener, that is attached to this provider.
     *
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
}
