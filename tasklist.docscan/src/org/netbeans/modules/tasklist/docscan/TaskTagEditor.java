/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.util.ArrayList;
import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;

import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

import org.netbeans.api.tasklist.SuggestionPriority;
import org.netbeans.modules.tasklist.core.Task;

/**
 * PropertyEditor for task tags
 *
 * @author Tor Norbye
 */
public class TaskTagEditor extends PropertyEditorSupport 
implements ExPropertyEditor {
    
    public TaskTagEditor() {
    }


    /** sets new value */
    public void setAsText(String s) {
        TaskTags tags = new TaskTags();
        int i = 0;
        int n = s.length();
        ArrayList list = new ArrayList();
        while (i < n) {
            if (s.charAt(i++) != '[') {
                return;
            }
            // Get token
            StringBuffer token = new StringBuffer();
            while (i < n) {
                char c = s.charAt(i++);
                boolean escaped = (c == '\\');
                if (escaped) {
                    if (i == n) {
                        break;
                    }
                    c = s.charAt(i++);
                }
                if (!escaped && ((c == ',') || (c == ']'))) {
                    break;
                }
                token.append(c);
            }
            StringBuffer priostr = new StringBuffer();
            while (i < n) {
                char c = s.charAt(i++);
                boolean escaped = (c == '\\');
                if (escaped) {
                    if (i == n) {
                        break;
                    }
                    c = s.charAt(i++);
                }
                if (!escaped && ((c == ',') || (c == ']'))) {
                    break;
                }
                priostr.append(c);
            }
            String prioString = priostr.toString();
            String[] prios = Task.getPriorityNames();
            SuggestionPriority priority = SuggestionPriority.MEDIUM;
            for (int j = 0; j < prios.length; j++) {
                if (prios[j].equals(prioString)) {
                    priority = Task.getPriority(j+1);
                    break;
                }
            }
            TaskTag tag = new TaskTag(token.toString(), priority);
            list.add(tag);
            if (i == n) {
                break;
            }
            if (s.charAt(i) == ']') {
                break;
            }
            if (s.charAt(i) == ',') {
                i++;
            } else {
                break;
            }
        }
        TaskTag[] tagArray = (TaskTag[])list.toArray(new TaskTag[list.size()]);
        tags.setTags(tagArray);
        setValue(tags);
    }

    public String getAsText() {
        Object val = getValue();
        if (val == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer(500);
            TaskTags tags = (TaskTags)val;
            TaskTag[] tgs = tags.getTags();
            String[] prios = Task.getPriorityNames();
            for (int i = 0; i < tgs.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('[');

                String s = tgs[i].getToken();
                int n = s.length();
                for (int j = 0; j < n; j++) {
                    char c = s.charAt(j);
                    // escape some metachars
                    if ((c == ',') || (c == '\\') ||
                        (c == '[') || (c == ']')) {
                        sb.append('\\');
                    }
                    sb.append(c);
                }
                sb.append(',');
                sb.append(prios[tgs[i].getPriority().intValue()-1]);
                sb.append(']');
            }
            return sb.toString();
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
