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
import org.openide.util.HelpCtx;
import org.openide.actions.PropertiesAction;

/**
 * The folder node, which contains RuntimeCommandNode nodes.
 *
 * @author  Martin Entlicher
 */
public class RuntimeFolderNode extends AbstractNode {
    
    public static final String PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT = "numOfFinishedCmdsToCollect";

    private int numOfFinishedCmdsToCollect = RuntimeSupport.DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    
    /** Creates new RuntimeFolderNode */
    public RuntimeFolderNode(Children children) {
        super(children);
    }
    
    public void setNumOfFinishedCmdsToCollect(int numOfFinishedCmdsToCollect) {
        if (this.numOfFinishedCmdsToCollect != numOfFinishedCmdsToCollect) {
            Object oldValue = new Integer(this.numOfFinishedCmdsToCollect);
            this.numOfFinishedCmdsToCollect = numOfFinishedCmdsToCollect;
            firePropertyChange(PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, oldValue, new Integer(numOfFinishedCmdsToCollect));
        }
    }
    
    public int getNumOfFinishedCmdsToCollect() {
        return numOfFinishedCmdsToCollect;
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        createProperties(set);
        return sheet;
    }
    
    private void createProperties(final Sheet.Set set) {
        set.put(new PropertySupport.ReadWrite(PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, Integer.TYPE, g("CTL_numOfFinishedCmdsToCollect"), "") {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return new Integer(numOfFinishedCmdsToCollect);
                        }
                        public void setValue(Object value) {
                            if (!(value instanceof Integer)) throw new IllegalArgumentException("An Integer value expected");
                            Object oldValue = new Integer(numOfFinishedCmdsToCollect);
                            numOfFinishedCmdsToCollect = ((Integer) value).intValue();
                            firePropertyChange(PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, oldValue, value);
                        }
                });
    }

    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(RuntimeFolderNode.class).getString(name);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(RuntimeFolderNode.class);
    }
    
}
