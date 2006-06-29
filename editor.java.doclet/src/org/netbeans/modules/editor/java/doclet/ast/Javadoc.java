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
 * Software is Leon Chiver. All Rights Reserved.
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
