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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
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
