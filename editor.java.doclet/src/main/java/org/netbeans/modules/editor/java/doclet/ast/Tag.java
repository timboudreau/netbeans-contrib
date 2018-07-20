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
 * Software is Leon Chiver. All Rights Reserved.
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
