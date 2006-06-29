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

/**
 * @author leon chiver
 */
public class Attribute {

    private String name;

    private String value;

    private int line;

    private int valueLine;

    private int valueBeginColumn;

    private int valueEndColumn;

    private int beginColumn;

    private int endColumn;

    public Attribute() {
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public int getLine() {
        return line;
    }
    
    public void setBeginColumn(int column) {
        this.beginColumn = column;
    }
    
    public int getBeginColumn() {
        return beginColumn;
    }
    
    public void setEndColumn(int column) {
        this.endColumn = column;
    }
    
    public int getEndColumn() {
        return endColumn;
    }
    
    public int getValueLine() {
        return valueLine;
    }
    
    public void setValueLine(int valueLine) {
        this.valueLine = valueLine;
    }
    
    public int getValueBeginColumn() {
        return valueBeginColumn;
    }
    
    public void setValueBeginColumn(int valueBeginColumn) {
        this.valueBeginColumn = valueBeginColumn;
    }

    public int getValueEndColumn() {
        return valueEndColumn;
    }
    
    public void setValueEndColumn(int valueEndColumn) {
        this.valueEndColumn = valueEndColumn;
    }
    
}
