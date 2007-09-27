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
