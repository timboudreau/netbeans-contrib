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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author leon chiver
 */
public class Tag {

    private int beginLine;
    
    private int nameBeginColumn;

    private int nameEndColumn;
    
    private String name;

    private String text;
    
    private List/*<Attribute>*/ attributeList;
    
    public Tag() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public int getBeginLine() {
        return beginLine;
    }
    
    public void setBeginLine(int line) {
        this.beginLine = line;
    }
    
    public void addAttribute(Attribute attr) {
        if (attributeList == null) {
            attributeList = new ArrayList();
        }
        attributeList.add(attr);
    }
    
    public List/*<Attribute>*/ getAttributeList() {
        return attributeList;
    }
    
    public Set/*<String>*/ getTagNames() {
        if (attributeList == null || attributeList.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set s = new HashSet();
        int sz = attributeList.size();
        for (int i = 0; i < sz; i++) {
            Attribute a = (Attribute) attributeList.get(i);
            s.add(a.getName());
        }
        return s;
    }

    public Attribute getAttributeAtPosition(int line, int column) {
        if (attributeList != null) {
            int sz = attributeList.size();
            // Decrement, as completion is probably invoked at the end of the attribute list
            for (int i = sz - 1; i >= 0; i--) {
                Attribute attr = (Attribute) attributeList.get(i);
                if (attr.getLine() == line && attr.getBeginColumn() <= column && attr.getEndColumn() >= column) {
                    return attr;
                } 
            }
        }
        return null;
    }
    
    public Attribute getAttributeWithValueAtPosition(int line, int column) {
        if (attributeList != null) {
            int sz = attributeList.size();
            for (int i = sz - 1; i >= 0; i--) {
                Attribute attr = (Attribute) attributeList.get(i);
                if (attr.getValueLine() == line && attr.getValueBeginColumn() <= column && 
                        attr.getValueEndColumn() >= column) {
                    return attr;
                } 
            }
        }
        return null;
    }
    
    public void setNameBeginColumn(int column) {
        this.nameBeginColumn = column;
    }
    
    public int getNameBeginColumn() {
        return nameBeginColumn;
    }

    public void setNameEndColumn(int nameEndColumn) {
        this.nameEndColumn = nameEndColumn;
    }

    public int getNameEndColumn() {
        return nameEndColumn;
    }
}
