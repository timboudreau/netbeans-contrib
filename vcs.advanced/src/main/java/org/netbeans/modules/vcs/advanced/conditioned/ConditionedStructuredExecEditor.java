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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExecEditor extends PropertyEditorSupport {

    private ConditionedString cexecString;
    private ConditionedObject cexecStructured;
    private VcsCommand cmd;
    private Map cproperties;
    
    /** Creates a new instance of StructuredExecEditor *
    public ConditionedStructuredExecEditor() {
        this(new ConditionedString("exec", new HashMap()));
    }
     */
    
    public ConditionedStructuredExecEditor(ConditionedString cexecString, VcsCommand cmd, Map cproperties) {
        this.cexecString = cexecString;
        this.cmd = cmd;
        this.cproperties = cproperties;
    }
    
    public String getAsText() {
        Map vbc;
        if (cexecStructured == null || ((vbc = cexecStructured.getValuesByConditions()).size() == 1 && vbc.values().iterator().next() == null)) {
            return cexecString.toString();
        } else {
            return cexecStructured.toString();
        }
    }
    
    public java.awt.Component getCustomEditor() {
        ConditionedStructuredExecPanel panel = new ConditionedStructuredExecPanel(cmd, cproperties);
        panel.setExecStringConditioned(cexecString);
        panel.setExecStructuredConditioned(cexecStructured);
        return panel;
    }
    
    public Object getValue() {
        //cmd.setProperty(VcsCommand.PROPERTY_EXEC, execString);
        return cexecStructured;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        // Unimplemented
    }
    
    public void setValue(Object value) {
        cexecStructured = (ConditionedObject) value;
        ConditionedProperty cproperty = (ConditionedProperty) cproperties.get(VcsCommand.PROPERTY_EXEC);
        Map valuesByConditions = cexecString.getValuesByConditions();
        ConditionedProperty newCProperty;
        Object varValue = null;
        if (valuesByConditions.size() == 1 && valuesByConditions.keySet().iterator().next() == null) {
            newCProperty = null;
            varValue = valuesByConditions.get(null);
        } else {
            if (cproperty != null) {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, cproperty.getCondition(), valuesByConditions);
            } else {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, null, valuesByConditions);
            }
        }
        if (newCProperty != null) {
            cproperties.put(VcsCommand.PROPERTY_EXEC, newCProperty);
        } else {
            cproperties.remove(VcsCommand.PROPERTY_EXEC);
            cmd.setProperty(VcsCommand.PROPERTY_EXEC, varValue);
        }
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
}
