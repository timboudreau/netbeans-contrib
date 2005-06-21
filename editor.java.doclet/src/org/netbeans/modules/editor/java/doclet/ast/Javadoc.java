/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author leon chiver
 */
public class Javadoc {
    
    private String header;
    
    private List/*<Tag>*/ tags;
    
    public Javadoc() {
    }
    
    public void addTag(Tag t) {
        if (tags == null) {
            tags = new ArrayList();
        }
        tags.add(t);
    }
    
    public List/*<Tag>*/ getTags() {
        return tags;
    }
    
    public Tag getTagAtLine(int line) {
        if (tags == null) {
            return null;
        }
        int sz = tags.size();
        for (int i = sz - 1; i >=0; i--) {
            Tag t = (Tag) tags.get(i);
            if (t.getBeginLine() <= line) {
                return t;
            }
        }
        return null;
    }
    
    public boolean containsTag(String name) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        int sz = tags.size();
        for (int i = 0; i < sz; i++) {
            Tag t = (Tag) tags.get(i);
            if (name.equals(t.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public List getTagsByName(String name) {
        if (tags == null || tags.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List l = new ArrayList();
        int sz = tags.size();
        for (int i = 0; i < sz; i++) {
            Tag t = (Tag) tags.get(i);
            if (name.equals(t.getName())) {
                l.add(t);
            }
        }
        return l;
    }
    
}
