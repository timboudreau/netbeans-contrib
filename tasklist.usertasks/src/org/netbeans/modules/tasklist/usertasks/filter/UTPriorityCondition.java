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

package org.netbeans.modules.tasklist.usertasks.filter;

import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.*;
import org.netbeans.modules.tasklist.filter.FilterCondition;
import org.netbeans.modules.tasklist.swing.checklist.CheckList;
import org.netbeans.modules.tasklist.swing.checklist.DefaultCheckListModel;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.NbBundle;

/**
 * "Priority is" - condition
 *
 * @author tl
 */
public class UTPriorityCondition extends FilterCondition {
    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static UTPriorityCondition[] createConditions() {
        return new UTPriorityCondition[] {
            new UTPriorityCondition()
        };
    };
    
    
    private static int NPRIORITIES = 5;
    
    // 5 = Number of different priorities
    protected boolean[] priorities = new boolean[NPRIORITIES];
    
    /**
     * Creates a new instance
     *
     * @param prop index of a property
     */
    public UTPriorityCondition() {
        Arrays.fill(priorities, true);
    }
    
    
    public UTPriorityCondition(final UTPriorityCondition rhs) {
        super(rhs);
        // copy priorities
        for (int i = 0; i < NPRIORITIES; i++) 
            this.priorities[i] = rhs.priorities[i];
    }
    
    public Object clone() {
        return new UTPriorityCondition(this);
    }
    
    public boolean isTrue(Object o1) {
        Integer k = (Integer) o1;
        return priorities[k - 1];
    }
    
    public JComponent createConstantComponent() {
        CheckList list = new CheckList(
                new DefaultCheckListModel(
                priorities, UserTask.getPriorityNames()));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                UIManager.getBorder("TextField.border"), // NOI18N
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
        panel.add(list, BorderLayout.CENTER);
        panel.setToolTipText(NbBundle.getMessage(UTPriorityCondition.class,
                "prio_desc"));
        
        list.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(UTPriorityCondition.class, 
                "LBL_PriorityCheckList"));
        list.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(UTPriorityCondition.class, 
                "LBL_PriorityCheckList"));
        
        return panel;
    }
    
    public void getConstantFrom(JComponent cmp) {
        // Nothing to do. The array of booleans will not be cloned in
        // DefaultCheckListModel
    }
    
    protected String getDisplayName() {
        return NbBundle.getMessage(UTPriorityCondition.class, "IsOneOf");
    }
    
    
    private static class Convertor extends FilterCondition.Convertor {
        private static final String ATTR_PRIORITIES = "priorities";
        
        public Convertor() { 
            super("PriorityCondition");
        }
        
        public static UTPriorityCondition.Convertor create() {
            return new UTPriorityCondition.Convertor();
        }
        
        protected Object readElement(org.w3c.dom.Element element) 
                throws java.io.IOException, java.lang.ClassNotFoundException {
            UTPriorityCondition cond = new UTPriorityCondition();
            super.readCondition(element, cond);
            
            String spriorities = element.getAttribute(ATTR_PRIORITIES);
            boolean bpriorities [] = new boolean[NPRIORITIES];
            for (int i = 0; i < NPRIORITIES; i++) 
                bpriorities[i] = (spriorities.charAt(i) == '+');
            cond.priorities = bpriorities;
            
            return cond;
        }
        
        // write methods for supported condition types
        protected void writeElement(org.w3c.dom.Document document, 
                org.w3c.dom.Element element, Object obj)
                throws java.io.IOException, org.w3c.dom.DOMException {
            UTPriorityCondition cond = (UTPriorityCondition)obj;
            super.writeCondition(document, element, cond);
            
            StringBuilder str = new StringBuilder(NPRIORITIES);
            for (int i = 0; i < NPRIORITIES; i++) 
                str.append(cond.priorities[i] ? '+' : '-');
            element.setAttribute(ATTR_PRIORITIES, str.toString());
        }
    }
    
    
}
