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

package org.netbeans.modules.portalpack.servers.core.ui;
import javax.swing.text.*;
import java.awt.Toolkit;

/**
 * This is a DocumentFilter implementation to restrict only numeric inputs to a JTextField.
 * @author Satyaranjan
 */

public class IntegerDocumentFilter extends DocumentFilter {
    
    
    int currentValue = 0;
    
    public IntegerDocumentFilter() {
    }
    
    public void insertString(DocumentFilter.FilterBypass fb,
            int offset, String string, AttributeSet attr)
            throws BadLocationException {
        
        if (string == null) {
            return;
        } else {
            replace(fb, offset, 0, string, attr);
        }
    }
    
    public void remove(DocumentFilter.FilterBypass fb,
            int offset, int length)
            throws BadLocationException {
        
        replace(fb, offset, length, "", null);
    }
    
    public void replace(DocumentFilter.FilterBypass fb,
            int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        
        Document doc = fb.getDocument();
        int currentLength = doc.getLength();
        String currentContent = doc.getText(0, currentLength);
        String before = currentContent.substring(0, offset);
        String after = currentContent.substring(
                length+offset, currentLength);
        String newValue = before +
                (text == null ? "" : text) + after;
        currentValue = checkInput(newValue, offset);
        fb.replace(offset, length, text, attrs);
    }
    
    private int checkInput(String proposedValue, int offset)
    throws BadLocationException {
        int newValue = 0;
        if (proposedValue.length() > 0) {
            try {
                newValue = Integer.parseInt(proposedValue);
            } catch (NumberFormatException e) {
                throw new BadLocationException(
                        proposedValue, offset);
            }
        }
        return newValue;
    }
}
