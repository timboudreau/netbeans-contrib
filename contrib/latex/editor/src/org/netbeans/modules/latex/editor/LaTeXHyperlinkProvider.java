/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.awt.Toolkit;
import javax.swing.text.Document;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.Node;

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
        Node found = LaTeXGoToImpl.getDefault().getGoToNode(doc, offset, true);
        
        if (found == null) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public boolean isHyperlinkPoint(Document doc, int offset) {
        return getHyperlinkSpan(doc, offset) != null;
    }
    
    public int[] getHyperlinkSpan(Document doc, int offset) {
        Node node = LaTeXGoToImpl.getDefault().getGoToNode(doc, offset, false);
        
        if (node == null)
            return null;
        
        int[] result = new int[] {node.getStartingPosition().getOffsetValue(), node.getEndingPosition().getOffsetValue()};
        
        if (node instanceof ArgumentNode && ((ArgumentNode) node).getArgument().getType() != Command.Param.FREE) {
            result[0]++;
        }
        
        return result;
    }

}
