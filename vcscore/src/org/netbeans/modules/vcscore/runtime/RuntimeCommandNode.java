/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;

import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The node of command in the runtime tab.
 * The command can be in the three states: waiting (for user data or for other commands),
 * running and done.
 *
 * @author  Martin Entlicher
 */
class RuntimeCommandNode extends AbstractNode {

    static final int STATE_WAITING = 0;
    static final int STATE_RUNNING = 1;
    static final int STATE_DONE = 2;
    static final int STATE_CANCELLED = 3;
    
    private VcsCommandExecutor executor;
    private CommandsPool cpool;
    private int state;
    
    
    /** Creates new RuntimeCommandNode */
    RuntimeCommandNode(VcsCommandExecutor vce, CommandsPool cpool) {
        super(Children.LEAF);
        this.executor = vce;
        this.cpool = cpool;
        setName(vce.getCommand().getName());
        String displayName = vce.getCommand().getDisplayName();
        if (displayName == null || displayName.length() == 0) displayName = vce.getCommand().getName();
        setDisplayName(displayName);
    }
    
    void setState(int state) {
        this.state = state;
    }
    
    VcsCommandExecutor getExecutor() {
        return executor;
    }
    
    CommandsPool getCommandsPool() {
        return cpool;
    }
    
    public SystemAction[] getActions() {
        return new SystemAction[] { CommandOutputViewAction.getInstance() , SystemAction.get(PropertiesAction.class) };
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        createProperties(set);
        return sheet;
    }
    
    private void createProperties(final Sheet.Set set) {
        set.put(new PropertySupport.ReadOnly("name", String.class, g("CTL_Name"), "") {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return executor.getCommand().getName();
                        }
                });
        set.put(new PropertySupport.ReadOnly("exec", String.class, g("CTL_Exec"), "") {
                        public Object getValue() {
                            return executor.getExec();
                        }
                });
        set.put(new PropertySupport.ReadOnly("files", String.class, g("CTL_Files"), "") {
                        public Object getValue() {
                            String[] files = (String[]) executor.getFiles().toArray(new String[0]);
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].length() == 0) files[i] = ".";
                            }
                            return VcsUtilities.array2stringNl(files);
                        }
                });
        set.put(new PropertySupport.ReadOnly("status", String.class, g("CTL_Status"), "") {
                        public Object getValue() {
                            if (cpool.isWaiting(executor)) return g("CTL_Status_Waiting");
                            if (cpool.isRunning(executor)) return g("CTL_Status_Running");
                            return CommandsPool.getExitStatusString(executor.getExitStatus());
                        }
                });
                
    }

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeCommandNode.class).getString(name);
    }

}
