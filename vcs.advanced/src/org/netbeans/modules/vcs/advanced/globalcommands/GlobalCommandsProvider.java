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

package org.netbeans.modules.vcs.advanced.globalcommands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.WeakListeners;

import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.FilesModificationSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.runtime.RuntimeFolderNode;

import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;

/**
 * Provider of global VCS commands.
 *
 * @author  Martin Entlicher
 */
public class GlobalCommandsProvider extends VcsCommandsProvider implements CommandsTree.Provider,
                                                                           PropertyChangeListener,
                                                                           FilesModificationSupport {
    
    private static GlobalCommandsProvider instance;
    
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private Map profilesByNames = new TreeMap();
    private Map commandSupportsByNames = new TreeMap();
    private String[] commandNames;
    private CommandsTree commands;
    private int numberOfFinishedCmdsToCollect = RuntimeFolderNode.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    private boolean runtimeCreated = false;
    private boolean expertMode = false;
    private boolean initialized = false;
    
    /** Creates a new instance of GlobalCommandsProvider - to be called only by the Lookup system.
     * Do not call directly!
     */
    public GlobalCommandsProvider() {
        instance = this;
    }
    
    private synchronized void initialize() {
        ProfilesFactory factory = ProfilesFactory.getDefault();
        String names[] = factory.getProfilesNames();
        for (int i = 0; i < names.length; i++) {
            Profile profile = factory.getProfile(names[i]);
            if (profile == null || !factory.isOSCompatibleProfile(names[i])) continue;
            // TODO when a profile is not OS-compatible, we should listen
            //      for changes of it. It may become OS-compatible later.
            profilesByNames.put(names[i], profile);
            profile.addPropertyChangeListener(this);
        }
        factory.addPropertyChangeListener(WeakListeners.propertyChange(this, factory));
        initialized = true;
    }
    
    /**
     * Get the instance of GlobalCommandsProvider.
     */
    public static final synchronized GlobalCommandsProvider getInstance() {
        if (instance == null) {
            instance = new GlobalCommandsProvider();
        }
        return instance;
    }
    
    public String getType() {
        // TODO: Register separate global providers per every profile and
        //       return a meaningful value here.
        return "Global Commands"; // NOI18N
    }
    
    /** Create a new VCS command of the given class type.
     * @return The command or <code>null</code> when the command of the given
     * class type does not exist.
     *
     */
    public Command createCommand(Class cmdClass) {
        return null;
    }
    
    /** Create a new VCS command of the given name.
     * @return The command or <code>null</code> when the command of the given
     * name does not exist.
     */
    public synchronized Command createCommand(String cmdName) {
        if (commands == null) collectCommands();
        CommandSupport support = (CommandSupport) commandSupportsByNames.get(cmdName);
        if (support != null) {
            return support.createCommand();
        } else {
            return null;
        }
    }
    
    /** Get the list of VCS command names.
     */
    public synchronized String[] getCommandNames() {
        if (commands == null) collectCommands();
        return commandNames;
    }
    
    /** Get the commands.
     * @return The root of the commands tree.
     */
    public synchronized CommandsTree getCommands() {
        if (commands == null) collectCommands();
        if (!runtimeCreated) {
            org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    new GlobalRuntimeCommandsProvider(); // Create new runtime commands provider, it will do itself what is necessary.
                }
            });
            runtimeCreated = true;
        }
        return commands;
    }
    
    private void collectCommands() {
        synchronized (this) {
            if (!initialized) {
                initialize();
            }
        }
        Profile[] profiles = (Profile[]) profilesByNames.values().toArray(new Profile[0]);
        commands = createCommandsFromProfiles(profiles);
        fillCommands(commands);
    }
    
    private static CommandsTree createCommandsFromProfiles(Profile[] profiles) {
        CommandsTree commands = new CommandsTree(null);
        //System.out.println("GlobalCommandsProvider.createCommandsFromProfiles()");
        for (int i = 0; i < profiles.length; i++) {
            GlobalExecutionContext globalContext = new GlobalExecutionContext(profiles[i]);
            //CommandsTree profileCommands = profiles[i].getGlobalCommands();
            CommandsTree profileCommands = globalContext.getCommands();
            CommandsTree[] children = profileCommands.children();
            for (int j = 0; j < children.length; j++) {
                commands.add(children[j]);
                //System.out.println("  add "+children[j]+
                //    ((children[j].getCommandSupport() != null) ?
                //        ", name = "+children[j].getCommandSupport().getName() :
                //        ", null support"));
            }
            /* Uncomment if commands from different profiles should be separated.
            if (children.length > 0) {
                //System.out.println("  had "+children.length+" children, adding separator.");
                commands.add(CommandsTree.EMPTY);
            }
             */
        }
        return commands;
    }
    
    private void fillCommands(CommandsTree commands) {
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            CommandSupport support = subCommands[i].getCommandSupport();
            if (support != null) {
                commandSupportsByNames.put(support.getName(), support);
            }
            if (subCommands[i].hasChildren()) fillCommands(subCommands[i]);
        }
        commandNames = (String[]) commandSupportsByNames.keySet().toArray(new String[commandSupportsByNames.size()]);
    }
    
    public int getNumberOfFinishedCmdsToCollect() {
        return numberOfFinishedCmdsToCollect;
    }
    
    public void setNumberOfFinishedCmdsToCollect(int numberOfFinishedCmdsToCollect) {
        this.numberOfFinishedCmdsToCollect = numberOfFinishedCmdsToCollect;
        firePropertyChange(RuntimeFolderNode.PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, null, null);
    }
    
    /**
     * Get the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public boolean isExpertMode() {
        return expertMode;
    }

    /**
     * Set the expert mode of this commands provider. If it's true, all commands
     * have the expert mode turned on by default;
     */
    public void setExpertMode(boolean expertMode) {
        this.expertMode = expertMode;
    }
        
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(l);
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /** This method gets called when a ProfilesFactory property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (ProfilesFactory.PROP_PROFILE_ADDED.equals(propertyName)) {
            synchronized (this) {
                String name = (String) evt.getNewValue();
                Profile profile = ProfilesFactory.getDefault().getProfile(name);
                if (profile == null || !ProfilesFactory.getDefault().isOSCompatibleProfile(name)) {
                    return ;
                }
                profilesByNames.put(name, profile);
                profile.addPropertyChangeListener(WeakListeners.propertyChange(this, profile));
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        } else if (ProfilesFactory.PROP_PROFILE_REMOVED.equals(propertyName)) {
            synchronized (this) {
                String name = (String) evt.getOldValue();
                Object old = profilesByNames.remove(name);
                if (old == null) return ;
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        } else if (Profile.PROP_GLOBAL_COMMANDS.equals(propertyName)) {
            synchronized (this) {
                collectCommands();
            }
            firePropertyChange(CommandsTree.Provider.PROP_COMMANDS, null, commands);
        }
    }
    
    private Collection filesStructureListeners;
    
    public final synchronized void addFilesStructureModificationListener(ChangeListener chl) {
        if (filesStructureListeners == null) {
            filesStructureListeners = new ArrayList();
        }
        filesStructureListeners.add(chl);
    }
    
    public final synchronized void removeFilesStructureModificationListener(ChangeListener chl) {
        if (filesStructureListeners != null) {
            filesStructureListeners.remove(chl);
            if (filesStructureListeners.size() == 0) {
                filesStructureListeners = null;
            }
        }
    }
    
    protected final void fireFilesStructureModified(File file) {
        java.util.List listeners;
        ChangeEvent che = null;
        synchronized (this) {
            if (filesStructureListeners != null) {
                che = new ChangeEvent(file);
                listeners = new ArrayList(filesStructureListeners);
            } else {
                listeners = Collections.EMPTY_LIST;
            }
        }
        for (Iterator it = listeners.iterator(); it.hasNext(); ) {
            ChangeListener l = (ChangeListener) it.next();
            l.stateChanged(che);
        }
    }

}
