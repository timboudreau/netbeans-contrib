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

/**
 * default implementation of RuntimeCommand. Gets the info that is needed by runtimenode from 
 *CommandsPool and vcsCommandExecutor
 * @author  Milos Kleint
 */
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.CommandsPool;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;



public class VcsRuntimeCommand extends RuntimeCommand {
    
    private VcsCommandExecutor executor;
    private CommandsPool pool;
    private int state;
    
    public VcsRuntimeCommand(VcsCommandExecutor executor, CommandsPool pool) {
        this.executor = executor;
        this.pool = pool;
        state = RuntimeCommand.STATE_WAITING;
    }

    public String getName() {
        return executor.getCommand().getName();
    }    

    public String getDisplayName() {
        return executor.getCommand().getDisplayName();
    }
    
    public int getExitStatus() {
        return executor.getExitStatus();
    }
    
    public void openCommandOutputDisplay() {
       pool.openCommandOutput(executor);
    }
    
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        createProperties(set);
        return sheet;
    }
    
    private void createProperties(final Sheet.Set set) {
        set.put(new PropertySupport.ReadOnly("name", String.class, g("CTL_Name"), g("HINT_Name")) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return executor.getCommand().getName();
                        }
                });
        set.put(new PropertySupport.ReadOnly("exec", String.class, g("CTL_Exec"), g("HINT_Exec")) {
                        public Object getValue() {
                            return executor.getExec();
                        }
                });
        set.put(new PropertySupport.ReadOnly("files", String.class, g("CTL_Files"), g("HINT_Files")) {
                        public Object getValue() {
                            String[] files = (String[]) executor.getFiles().toArray(new String[0]);
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].length() == 0) files[i] = ".";
                            }
                            return VcsUtilities.array2stringNl(files);
                        }
                });
        set.put(new PropertySupport.ReadOnly("status", String.class, g("CTL_Status"), g("HINT_Status")) {
                        public Object getValue() {
                            if (pool.isWaiting(executor)) return g("CTL_Status_Waiting");
                            if (pool.isRunning(executor)) {
                                if (state == STATE_KILLED_BUT_RUNNING) return g("CTL_Status_Killed_But_Running");
                                else return g("CTL_Status_Running");
                            }
                            return CommandsPool.getExitStatusString(executor.getExitStatus());
                        }
                });
        if (Boolean.getBoolean("netbeans.vcsdebug")) {
        set.put(new PropertySupport.ReadOnly("startTime", String.class, "Start Time", null) {
                        public Object getValue() {
                            long time = pool.getStartTime(executor);
                            if (time == 0) return null;
                            else return new java.util.Date(time).toLocaleString();
                        }
                });
        set.put(new PropertySupport.ReadOnly("finishTime", String.class, "Finish Time", null) {
                        public Object getValue() {
                            long time = pool.getFinishTime(executor);
                            if (time == 0) return null;
                            else return new java.util.Date(time).toLocaleString();
                        }
                });
        set.put(new PropertySupport.ReadOnly("executionTime", String.class, "Execution Time", null) {
                        public Object getValue() {
                            long time = pool.getExecutionTime(executor);
                            if (time == 0) return null;
                            else {
                                long allms = time;
                                int ms = (int) (time - (time/1000)*1000);
                                time /= 1000;
                                int s = (int) (time - (time/60)*60);
                                time /= 60;
                                int min = (int) (time - (time/60)*60);
                                time /= 60;
                                int h = (int) (time - (time/24)*24);
                                time /= 24;
                                int d = (int) time;
                                return allms+" ms = "+d+"d "+h+"h "+min+"min "+s+"s "+ms+"ms";
                            }
                        }
                });
        }
                
    }

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(VcsRuntimeCommand.class).getString(name);
    }

    public SystemAction[] getActions() {
        return new SystemAction[] {
            CommandOutputViewAction.getInstance(),
            KillRunningCommandAction.getInstance(),
            SystemAction.get(PropertiesAction.class)
        };
    }    
    
    public String getId() {
        Object obj = executor;
        return Integer.toString(obj.hashCode());
    }
    
    /**
     * If you returned the KillRunningCommandAction among the other actions in getActions() method,
     * this method will be called and should attempt  to stop the running command.
     */
    public void killCommand() {
        pool.kill(executor);
        state = STATE_KILLED_BUT_RUNNING;
        // to refresh the state we have to set it to the node.
        RuntimeSupport.getInstance().findRuntimeNode(this).setState(state);
    }
    
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public void notifyRemoved() {
        pool.removeFinishedCommand(executor);
    }

}
