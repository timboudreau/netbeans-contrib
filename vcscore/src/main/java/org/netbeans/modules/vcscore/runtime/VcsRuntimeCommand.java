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

package org.netbeans.modules.vcscore.runtime;

import org.openide.actions.PropertiesAction;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.UserCommandTask;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedTask;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * The default implementation of RuntimeCommand. Gets the info that is needed by
 * RuntimeCommandNode from CommandsPool and VcsCommandExecutor
 * @author  Milos Kleint, Martin Entlicher
 */
public class VcsRuntimeCommand extends RuntimeCommand {
    
    //private Command cmd;
    private CommandTaskInfo taskInfo;
    private CommandTask task;
    private VcsCommandExecutor executor;
    private int state;
    
    /*
    public VcsRuntimeCommand(Command cmd) {
        this.cmd = cmd;
        this.taskInfo = null;
        state = RuntimeCommand.STATE_WAITING;
    }
     */
    
    public VcsRuntimeCommand(CommandTaskInfo taskInfo) {
        //this.cmd = null;
        this.taskInfo = taskInfo;
        task = taskInfo.getTask();
        if (task instanceof UserCommandTask) {
            executor = ((UserCommandTask) task).getExecutor();
        }
        state = RuntimeCommand.STATE_WAITING;
    }

    public String getName() {
        //if (cmd != null) return cmd.getName();
        return task.getName();
        //return executor.getCommand().getName();
    }    

    public String getDisplayName() {
        //if (cmd != null) return cmd.getDisplayName();
        String name = task.getDisplayName();
        if (name != null) {
            int i = name.indexOf('&');
            if (i >= 0) name = name.substring(0, i) + name.substring(i + 1);
        }
        return name;
        /*
        String displayName = executor.getCommand().getDisplayName();
        if (displayName != null) {
            displayName = Variables.expand(executor.getVariables(), displayName, false);
        }
        return displayName;
         */
    }
    
    public int getExitStatus() {
        if (taskInfo != null) return task.getExitStatus();
        else return 0;
        //return executor.getExitStatus();
    }
    
    public void openCommandOutputDisplay(boolean gui) {
        if (task instanceof VcsDescribedTask) {
            VcsCommandVisualizer visualizer = ((VcsDescribedTask) task).getVisualizer(gui);
            if (visualizer != null) {
                if (visualizer.isOpened()) {
                    visualizer.requestFocus();
                } else {
                    visualizer.open(null);
                }
            }
            CommandOutputTopComponent.getInstance().open();
        }
        //pool.openCommandOutput(executor);
    }
    
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
	Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put (set);
        createProperties(set);
        return sheet;
    }
    
    private void createProperties(final Sheet.Set set) {
        set.put(new PropertySupport.ReadOnly("name", String.class, g("CTL_Name"), g("HINT_Name")) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return VcsRuntimeCommand.this.getName();
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
                            if (!task.isRunning() && !task.isFinished()) return g("CTL_Status_Waiting");
                            if (task.isRunning()) {
                                if (state == STATE_KILLED_BUT_RUNNING) return g("CTL_Status_Killed_But_Running");
                                else return g("CTL_Status_Running");
                            }
                            return CommandProcessor.getExitStatusString(executor.getExitStatus());
                        }
                });
        if (Boolean.getBoolean("netbeans.vcsdebug")) { // NOI18N
            set.put(new PropertySupport.ReadOnly("origin", String.class, "Task Trace", null) { // NOI18N
                        public Object getValue() {
                            StringBuffer sb = new StringBuffer();
                            try {
                                Class tclass = task.getClass();
                                while (tclass != CommandTask.class) {
                                    tclass = tclass.getSuperclass();
                                }
                                Method method = tclass.getDeclaredMethod("getOrigin", new Class[0]); // NOI18N
                                method.setAccessible(true);
                                Exception origin
                                        = (Exception) method.invoke(task, new Object[0]);
                                if (origin != null) {
                                    sb.append("Task origin:");  // NOI18N
                                    StackTraceElement[] el = origin.getStackTrace();
                                    for (int i = 0; i < el.length; i++) {
                                        StackTraceElement element = el[i];
                                        sb.append("\n  ").append(element.toString()); // NOI18N
                                    }
                                }

                                origin = taskInfo.getOrigin();
                                if (origin != null) {
                                    sb.append("\n\nTaskInfo origin:");  // NOI18N
                                    StackTraceElement[] el = origin.getStackTrace();
                                    for (int i = 0; i < el.length; i++) {
                                        StackTraceElement element = el[i];
                                        sb.append("\n  ").append(element.toString()); // NOI18N
                                    }
                                }

                            } catch (NoSuchMethodException e) {
                                sb.append("<unknown>");  // NOI18N
                            } catch (IllegalAccessException e) {
                                sb.append("<unknown>"); // NOI18N
                            } catch (InvocationTargetException e) {
                                sb.append("<unknown>"); // NOI18N
                            }
                            return sb.toString();
                        }
                });

            set.put(new PropertySupport.ReadOnly("startTime", String.class, "Start Time", null) {
                        public Object getValue() {
                            long time = taskInfo.getStartTime();
                            if (time == 0) return null;
                            else return new java.util.Date(time).toLocaleString();
                        }
                });
            set.put(new PropertySupport.ReadOnly("finishTime", String.class, "Finish Time", null) {
                        public Object getValue() {
                            long time = taskInfo.getFinishTime();
                            if (time == 0) return null;
                            else return new java.util.Date(time).toLocaleString();
                        }
                });
            set.put(new PropertySupport.ReadOnly("executionTime", String.class, "Execution Time", null) {
                        public Object getValue() {
                            long time = taskInfo.getExecutionTime();
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
                                if (allms > 1000) {
                                    return allms+" ms = "+((d > 0) ? d+"d " : "")
                                                         +((h > 0) ? h+"h " : "")
                                                         +((min>0 || h > 0) ? min+"min ":"")
                                                         +((s > 0 || min>0) ? s+"s " : "")
                                                         +ms+"ms";
                                } else {
                                    return allms+" ms";
                                }
                            }
                        }
                });
            set.put(new PropertySupport.ReadOnly("variables", String.class, "Variables", null) {
                    public Object getValue() {
                        if (executor != null) {
                            StringBuffer buff = new StringBuffer();
                            java.util.Map vars = executor.getVariables();
                            vars = new java.util.TreeMap(vars);
                            for (java.util.Iterator it = vars.keySet().iterator(); it.hasNext(); ) {
                                String name = (String) it.next();
                                String value = (String) vars.get(name);
                                buff.append(name);
                                buff.append("=");
                                buff.append(value);
                                buff.append("\n");
                            }
                            return buff.toString();
                        } else {
                            return null;
                        }
                    }
                });
        }
                
    }

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(VcsRuntimeCommand.class).getString(name);
    }

    public SystemAction[] getActions() {
        if (task instanceof VcsDescribedTask && ((VcsDescribedTask) task).hasGUIVisualizer()) {
            return new SystemAction[] {
                CommandOutputViewAction.getInstance(),
                CommandOutputTextViewAction.getInstance(),
                KillRunningCommandAction.getInstance(),
                SystemAction.get(PropertiesAction.class)
            };
        } else {
            return new SystemAction[] {
                CommandOutputTextViewAction.getInstance(),
                KillRunningCommandAction.getInstance(),
                SystemAction.get(PropertiesAction.class)
            };
        }
    }
    
    public SystemAction getDefaultAction() {
        if (task instanceof VcsDescribedTask && ((VcsDescribedTask) task).hasGUIVisualizer()) {
            return CommandOutputViewAction.getInstance();
        } else {
            return CommandOutputTextViewAction.getInstance();
        }
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
        task.stop();
        setState(STATE_KILLED_BUT_RUNNING);
    }
    
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        firePropertyChange(PROP_STATE, null, null);
    }
    
    /*
    public void notifyRemoved() {
        pool.removeFinishedCommand(executor);
    }
     */

}
