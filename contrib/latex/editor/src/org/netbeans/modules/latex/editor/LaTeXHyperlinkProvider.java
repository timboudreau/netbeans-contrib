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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.awt.Toolkit;
import javax.swing.text.Document;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXHyperlinkProvider implements HyperlinkProvider {

    /**
     * Creates a new instance of LaTeXHyperlinkProvider 
     */
    public LaTeXHyperlinkProvider() {
    }
    
    public void performClickAction(Document doc, int offset) {
        int[] span = LaTeXGoToImpl.getDefault().getGoToNode(doc, offset, true);
        
        if (span == null) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public boolean isHyperlinkPoint(Document doc, int offset) {
        return getHyperlinkSpan(doc, offset) != null;
    }
    
    public int[] getHyperlinkSpan(Document doc, int offset) {
        return LaTeXGoToImpl.getDefault().getGoToNode(doc, offset, false);
    }

    public String getShortDescription(Document document, int i) {
        return null;
    }

}
