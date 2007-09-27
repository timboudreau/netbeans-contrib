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

package org.netbeans.modules.corba.idl.editor.indent;

/**
 *
 * @author  Tomas Zezula
 */

import java.io.Writer;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import org.openide.text.IndentEngine;
import org.openide.text.NbDocument;
import org.netbeans.modules.corba.idl.editor.coloring.IDLKit;

public class IDLIndentEngine extends IndentEngine {

    private static final long serialVersionUID = 5378217893629944112L;
    
    public static final char L_CPAR = '{';
    public static final char R_CPAR = '}';
    private static final char NL = '\n';
    private static final char SPACE = ' ';
    private static final char TAB ='\t';
    private static final char INDENT_CHAR = SPACE;
    private static final String NL_STR = "\n";
    private static final int DEFAULT_TAB_WIDTH = 4;
    
    
    private int indentTabWidth;
    
    class IDLFormater extends FilterWriter {
        private int startingIndent;
        private int actualIndentSpaceSize;
        private int actualIndent;
        private int indentChars;
        private int state;
        
        public IDLFormater (Document doc, int offset, Writer parent) {
            super (parent);
            this.actualIndentSpaceSize = IDLIndentEngine.this.indentTabWidth;
            this.state = 0;
            int lineNumber = NbDocument.findLineNumber ((StyledDocument)doc, offset);
            Element linesRoot = NbDocument.findLineRootElement((StyledDocument)doc);
            int state = 0;
            while (state == 0) {
                this.startingIndent = this.actualIndent = 0;
                lineNumber--;
                if (lineNumber < 0) {
                    break;
                }
                else {
                    try {
                        Element parentLine = linesRoot.getElement(lineNumber);
                        String parentLineValue = doc.getText(parentLine.getStartOffset(),parentLine.getEndOffset()-parentLine.getStartOffset());
                        for (int i=0; i< parentLineValue.length(); i++) {
                            char c = parentLineValue.charAt(i);
                            switch (state) {
                                case 0:     // In initial indent region
                                    switch (c) {
                                        case SPACE:
                                            this.startingIndent++;
                                            break;
                                        case TAB:
                                            this.startingIndent+=this.actualIndentSpaceSize;
                                            break;
                                        case L_CPAR:
                                            this.actualIndent++;
                                            state = 1;
                                            break;
                                        case R_CPAR:
                                            state = 1;
                                            break;
                                        default:
                                            state = 1;
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (c) {
                                        case L_CPAR:
                                            this.actualIndent++;
                                            break;
                                        case R_CPAR:
                                            this.actualIndent--;
                                            break;
                                    }
                                    break;
                            }
                        }
                    } catch (BadLocationException e) {}
                }
            }
            this.indentChars = Math.max (0,this.startingIndent + this.actualIndent * this.actualIndentSpaceSize);
        }
        
        public void write (char[] buff, int off, int len) throws IOException {
            for (int i=off; i<len; i++)
                this.write ((int)buff[i]);
        }
        
        public void write (char[] buff) throws IOException {
            this.write (buff,0,buff.length);
        }
        
        public void write (int c) throws IOException {
            switch (this.state) {
                case 0:
                    switch (c) {
                        case L_CPAR:
                            this.actualIndent++;
                            this.state = 1;
                            writeIndent();
                            break;
                        case R_CPAR:
                            this.actualIndent--;
                            this.state = 1;
                            this.indentChars-=this.actualIndentSpaceSize;
                            writeIndent();
                            break;
                        case NL:
                            this.indentChars = Math.max (0,this.startingIndent + this.actualIndentSpaceSize * this.actualIndent);
                            break;
                        case SPACE:
                        case TAB:
                            break;
                        default:
                            this.state = 1;
                            writeIndent();
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case L_CPAR:
                            this.actualIndent++;
                            break;
                        case R_CPAR:
                            this.actualIndent--;
                            break;
                        case NL:
                            this.indentChars = Math.max (0,this.startingIndent + this.actualIndentSpaceSize * this.actualIndent);
                            this.state = 0;
                            break;
                    }
                    break;
            }
            if (state==1 || (c!= TAB && c!=SPACE))
                super.write (c);
        }
            
        
        public void write (String str, int off, int len) throws IOException {
            for (int i=off; i<len; i++)
                write (str.charAt(i));
        }
        
        public void write (String str) throws IOException {
            this.write (str,0,str.length());
        }
        
        public int getIndentWidth () {
            return this.indentChars;
        }
        
        public String generateIndent (int width) {
            StringBuffer sb = new StringBuffer ();
            for (int i=0; i<width; i++)
                sb.append (INDENT_CHAR);
            return sb.toString();
        }
        
        private void writeIndent () throws IOException {
            if (this.indentChars > 0) {
                String s = this.generateIndent(this.indentChars);
                super.write (s,0,s.length());
            }
        }
        
   
    }

    /** Creates new IDLIndentEnginee */
    public IDLIndentEngine() {
        this.indentTabWidth = DEFAULT_TAB_WIDTH;
    }
    
    public void setTabWidth (int tabWidth) {
        int oldTabWidth = this.indentTabWidth;
        this.indentTabWidth = tabWidth;
        firePropertyChange ("tabWidth",new Integer(oldTabWidth), new Integer(this.indentTabWidth));
    }
    
    public int getTabWidth () {
        return this.indentTabWidth;
    }
    
    protected boolean acceptMimeType (String mimeType) {
        return (IDLKit.IDL_CONTENT_TYPE.equals (mimeType));
    }
    
    public Writer createWriter (Document doc, int offset, Writer writer) {
        if (doc instanceof StyledDocument)
            return new IDLFormater (doc, offset, writer);
        else
            return writer;
    }
    
    public int indentLine (Document doc, int off) {
        if (!(doc instanceof StyledDocument))
            return off;
        try {
            int currentLineNumber = NbDocument.findLineNumber ((StyledDocument)doc, off);
            Element linesRoot = NbDocument.findLineRootElement ((StyledDocument)doc);
            Element currentLine = linesRoot.getElement (currentLineNumber);
            String lineText = doc.getText (currentLine.getStartOffset(), off - currentLine.getStartOffset());
            char hotChar = lineText.charAt (lineText.length()-1);
            switch (hotChar) {
                case R_CPAR:
                    int i;
                    for (i=lineText.length()-2; i>=0; i--) {
                        if (lineText.charAt(i) != SPACE && lineText.charAt(i) != TAB)
                            break;
                    }
                    if (i<0) { // Start of line, reformat it
                        StringWriter writer = new StringWriter ();
                        IDLFormater formater = new IDLFormater (doc, off, writer);
                        int newWidth = Math.max (0,formater.getIndentWidth() - this.indentTabWidth);
                        int oldWidth = lineText.length()-1;
                        int delta = newWidth - oldWidth;
                        if (delta < 0) {
                            doc.remove (currentLine.getStartOffset(),-delta);
                            return off+delta;
                        }
                        else if (delta > 0) {
                            String fill = formater.generateIndent (delta);
                            doc.insertString (currentLine.getStartOffset(), fill, null);
                            return off+delta;
                        }
                        else
                            return off;
                    }
                    else { // Inside a line, do nothing with this
                        return off;
                    }
                default:
                    return off;
            }
        }catch (BadLocationException ble) {
            return off;
        }
    }
    
    public int indentNewLine (Document doc, int off) {
        if (!(doc instanceof StyledDocument))
            return off;
        try {
            doc.insertString (off++, NL_STR,null);
            StringWriter writer = new StringWriter ();
            IDLFormater formater = new IDLFormater (doc, off, writer);
            String fill = formater.generateIndent (formater.getIndentWidth());
            doc.insertString (off, fill, null);
            return off+fill.length();
        }catch (BadLocationException ble) {
            return off;
        }
    }

}
