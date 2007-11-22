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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Utilities.java
 *
 * Created on March 8, 2007, 10:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.saw.palette;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Formatter;

/**
 *
 * @author root
 */
public class Utilities {
    
    /** Creates a new instance of TestDDPaletteUtilities */
    public Utilities() {
    }
    
    public static void insert(String s, JTextComponent target)
    throws BadLocationException{
        insert(s, target, true);
    }
    
    public static void insert(String s, JTextComponent target, boolean reformat)
    throws BadLocationException{
        if(s == null){
            s = "";
        }
        Document doc = target.getDocument();
        if(doc == null){
            return;
        }
       
        int start = insert(s,target,doc);
        
        if(reformat && start >= 0 && doc instanceof BaseDocument) {
            int end = start + s.length();
            Formatter f = ((BaseDocument)doc).getFormatter();
            f.reformatLock();        
            f.reformat((BaseDocument)doc,start,end);
            f.reformatUnlock();
        } 
    }

    private static int insert(String s, JTextComponent target, Document doc)
    throws BadLocationException{
        int start = -1;
        try{
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);
            
            //replace selected text by the inserted one
            start = caret.getDot();
            doc.insertString(start, s, null);
        }catch(BadLocationException ble){}
        return start;
    }
}
