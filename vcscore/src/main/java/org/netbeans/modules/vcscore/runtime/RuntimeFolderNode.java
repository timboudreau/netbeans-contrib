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
