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

package org.netbeans.modules.tasklist.docscan;

import java.util.ArrayList;
import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.Task;

/**
 * PropertyEditor for task tags
 *
 * @author Tor Norbye
 */
public final class TaskTagEditor extends PropertyEditorSupport
implements ExPropertyEditor {
    
    public TaskTagEditor() {
    }


    /** sets new value */
    public void setAsText(String s) {
//        TaskTags tags = new TaskTags();
//        int i = 0;
//        int n = s.length();
//        ArrayList list = new ArrayList();
//        while (i < n) {
//            if (s.charAt(i++) != '[') {
//                return;
//            }
//            // Get token
//            StringBuffer token = new StringBuffer();
//            while (i < n) {
//                char c = s.charAt(i++);
//                boolean escaped = (c == '\\');
//                if (escaped) {
//                    if (i == n) {
//                        break;
//                    }
//                    c = s.charAt(i++);
//                }
//                if (!escaped && ((c == ',') || (c == ']'))) {
//                    break;
//                }
//                token.append(c);
//            }
//            StringBuffer priostr = new StringBuffer();
//            while (i < n) {
//                char c = s.charAt(i++);
//                boolean escaped = (c == '\\');
//                if (escaped) {
//                    if (i == n) {
//                        break;
//                    }
//                    c = s.charAt(i++);
//                }
//                if (!escaped && ((c == ',') || (c == ']'))) {
//                    break;
//                }
//                priostr.append(c);
//            }
//            String prioString = priostr.toString();
//            String[] prios = Task.getPriorityNames();
//            SuggestionPriority priority = SuggestionPriority.MEDIUM;
//            for (int j = 0; j < prios.length; j++) {
//                if (prios[j].equals(prioString)) {
//                    priority = Task.getPriority(j+1);
//                    break;
//                }
//            }
//            TaskTag tag = new TaskTag(token.toString(), priority);
//            list.add(tag);
//            if (i == n) {
//                break;
//            }
//            if (s.charAt(i) == ']') {
//                break;
//            }
//            if (s.charAt(i) == ',') {
//                i++;
//            } else {
//                break;
//            }
//        }
//        TaskTag[] tagArray = (TaskTag[])list.toArray(new TaskTag[list.size()]);
//        tags.setTags(tagArray);
//        setValue(tags);
    }

    public String getAsText() {
        Object val = getValue();
        if (val == null) {
            return "";
        } else {
            TaskTags tags = (TaskTags)val;
            return tags.getScanRegexp().pattern();
//            StringBuffer sb = new StringBuffer(500);
//            TaskTag[] tgs = tags.getTags();
//            String[] prios = Task.getPriorityNames();
//            for (int i = 0; i < tgs.length; i++) {
//                if (i > 0) {
//                    sb.append(',');
//                }
//                sb.append('[');
//
//                String s = tgs[i].getToken();
//                int n = s.length();
//                for (int j = 0; j < n; j++) {
//                    char c = s.charAt(j);
//                    // escape some metachars
//                    if ((c == ',') || (c == '\\') ||
//                        (c == '[') || (c == ']')) {
//                        sb.append('\\');
//                    }
//                    sb.append(c);
//                }
//                sb.append(',');
//                sb.append(prios[tgs[i].getPriority().intValue()-1]);
//                sb.append(']');
//            }
//            return sb.toString();
        }
    }


    public boolean supportsCustomEditor () {
        return true;
    }

    public Component getCustomEditor() {
        TaskTags d = (TaskTags)getValue();
        if (d == null) {
            d = new TaskTags();
            setValue(d);
        }
        return new TaskTagsPanel(d);
    }

    public void attachEnv(PropertyEnv env) {        
        FeatureDescriptor desc = env.getFeatureDescriptor();
        if (desc instanceof Node.Property){
            Node.Property prop = (Node.Property)desc;
            editable = prop.canWrite();
        }
    }

    // bugfix# 9219 added editable field and isEditable() "getter" to be used in StringCustomEditor    
    // TODO Tor - is the above relevant for our property editor?
    private boolean editable=true;   

    /** gets information if the text in editor should be editable or not */
    public boolean isEditable(){
        return editable;
    }
}
