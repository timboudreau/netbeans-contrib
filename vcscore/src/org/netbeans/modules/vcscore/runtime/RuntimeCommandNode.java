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

import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

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
    
    private VcsCommandExecutor executor;
    private int state;
    
    /** Creates new RuntimeCommandNode */
    RuntimeCommandNode(VcsCommandExecutor vce) {
        super(Children.LEAF);
        this.executor = vce;
        setName(vce.getCommand().getName());
        setDisplayName(vce.getCommand().getDisplayName());
    }
    
    void setState(int state) {
        this.state = state;
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        createProperties(set);
        return sheet;
    }
    
    private void createProperties(final Sheet.Set set) {
        set.put(new PropertySupport.ReadOnly("exec", String.class, g("CTL_Exec"), "") {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return executor.getExec();
                        }
                });
    }

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeCommandNode.class).getString(name);
    }

}
