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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.editor.PrintContainer;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXPrintContainer implements PrintContainer {

    private StringBuffer sb;
    private boolean toClipboard;
    private boolean background = false;

    /** Creates a new instance of LaTeXPrintContainer */
    public LaTeXPrintContainer() {
    }
    
    public void begin(boolean toClipboard) {
        this.toClipboard = toClipboard;
        this.sb = new StringBuffer();
        
        if (!toClipboard) {
            sb.append("\\documentclass{article}\n");
            sb.append("\\usepackage{color}\n");
            sb.append("\\begin{document}\n");
            
            if (background)
                sb.append("\\fboxsep=0pt");
        }
    }
    
    public void add(char[] chars, Font font, Color foreColor, Color backColor) {
        sb.append("\\texttt{");
        if (font.isItalic()) {
            sb.append("\\textit{");
        }
        if (font.isBold()) {
            sb.append("\\textbf{");
        }
        
        if (background) {
            sb.append("\\colorbox[rgb]{");
            sb.append(backColor.getRed()/255.0);
            sb.append(",");
            sb.append(backColor.getGreen()/255.0);
            sb.append(",");
            sb.append(backColor.getBlue()/255.0);
            sb.append("}{");
        }
        sb.append("\\textcolor[rgb]{");
        sb.append(foreColor.getRed()/255.0);
        sb.append(",");
        sb.append(foreColor.getGreen()/255.0);
        sb.append(",");
        sb.append(foreColor.getBlue()/255.0);
        sb.append("}{");
        sb.append(escape(chars));
        sb.append("}");
        if (background)
            sb.append("}");
        if (font.isItalic()) {
            sb.append("}");
        }
        if (font.isBold()) {
            sb.append("}");
        }
        sb.append("}");
    }
    
    public void eol() {
        sb.append("\\\\\n");
    }
    
    public String end() {
        if (!toClipboard) {
            sb.append("\\end{document}");
        }
        
        return sb.toString();
    }
    
    public boolean initEmptyLines() {
        return false;
    }
    
    private String escape(char[] buff) {
        StringBuffer sbuff = new StringBuffer();
        
        for (int cntr = 0; cntr < buff.length; cntr++) {
            String esc = (String) charToEscape.get(new Character(buff[cntr]));
            
            if (esc == null)
                sbuff.append(buff[cntr]);
            else
                sbuff.append(esc);
        }
        
        return sbuff.toString();
    }
    
    private static final Map charToEscape;
    
    static {
        charToEscape = new HashMap();
        
        charToEscape.put(new Character('['), "[");
        charToEscape.put(new Character('{'), "\\{");
        charToEscape.put(new Character('}'), "\\}");
        charToEscape.put(new Character(']'), "]");
        charToEscape.put(new Character('#'), "\\#");
        charToEscape.put(new Character('%'), "\\%");
        charToEscape.put(new Character('$'), "\\$");
        charToEscape.put(new Character('&'), "\\&");
        charToEscape.put(new Character('_'), "\\_");
        charToEscape.put(new Character('~'), "$\\sim$");
        charToEscape.put(new Character('^'), "$\\land$");
        charToEscape.put(new Character('\\'), "$\\backslash$");
        charToEscape.put(new Character(' '), "\\ ");
    }
    
}
