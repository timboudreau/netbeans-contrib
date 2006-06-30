/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.runtime;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.actions.PropertiesAction;

/**
 * The folder node, which contains RuntimeCommandNode nodes.
 *
 * @author  Martin Entlicher
 */
public class RuntimeFolderNode extends AbstractNode {

    public static final String PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT = "numberOfFinishedCmdsToCollect"; // NOI18N
    public static final int DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT = 20;

    private int numOfFinishedCmdsToCollect = DEFAULT_NUM_OF_FINISHED_CMDS_TO_COLLECT;
    
    /** Creates new RuntimeFolderNode */
    public RuntimeFolderNode(Children children) {
        super(children);
        setShortDescription(NbBundle.getMessage(RuntimeFolderNode.class, "RuntimeFolderNode.Description"));
    }
    
    public void setNumOfFinishedCmdsToCollect(int numOfFinishedCmdsToCollect) {
        //System.out.println("RuntimeFolderNode\""+getDisplayName()+"\".setNumOfFinishedCmdsToCollect("+numOfFinishedCmdsToCollect+")");
        if (this.numOfFinishedCmdsToCollect != numOfFinishedCmdsToCollect) {
            Object oldValue = new Integer(this.numOfFinishedCmdsToCollect);
            this.numOfFinishedCmdsToCollect = numOfFinishedCmdsToCollect;
            firePropertyChange(PROPERTY_NUM_OF_FINISHED_CMDS_TO_COLLECT, oldValue, new Integer(numOfFinishedCmdsToCollect));
        }
    }
    
    public int getNumOfFinishedCmdsToCollect() {
        //System.out.println("RuntimeFolderNode\""+getDisplayName()+"\".getNumOfFinishedCmdsToCollect("+numOfFinishedCmdsToCollect+")");
        return numOfFinishedCmdsToCollect;
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
	Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put (set);
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
        return NbBundle.getMessage(RuntimeFolderNode.class, name);
    }

}
