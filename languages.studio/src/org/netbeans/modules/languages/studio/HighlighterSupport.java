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

package org.netbeans.modules.languages.studio;

import java.awt.Color;
import java.util.Iterator;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.netbeans.modules.languages.Highlighting;
import org.netbeans.modules.languages.parser.ASTNode;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.windows.TopComponent;


/**
 */
public class HighlighterSupport {
    
    private Color       color;
    private Document    highlightedDocument;
    private ASTNode     highlightedNode;
    private int         highlightedTokenOffset = -1;
    
    
    public HighlighterSupport (Color c) {
        color = c;
    }
    
    public void highlight (Document doc, ASTNode node) {
        removeHighlightIn ();
        Highlighting.getHighlighting (highlightedDocument = doc).highlight (
            highlightedNode = node, 
            getHighlightAS ()
        );
        refresh (doc, node.getOffset ());
    }
    
    public void highlight (Document doc, int tokenOffset) {
        removeHighlightIn ();
        Highlighting.getHighlighting (highlightedDocument = doc).highlight (
            highlightedTokenOffset = tokenOffset, 
            getHighlightAS ()
        );
        refresh (doc, tokenOffset);
    }
    
    private static void refresh (final Document doc, final int offset) {
        Iterator it = TopComponent.getRegistry ().getOpened ().iterator ();
        while (it.hasNext ()) {
            TopComponent tc = (TopComponent) it.next ();
            EditorCookie ec = (EditorCookie) tc.getLookup ().lookup (EditorCookie.class);
            if (ec == null) continue;
            JEditorPane[] eps = ec.getOpenedPanes ();
            int i, k = eps.length;
            for (i = 0; i < k; i++) {
                if (eps [i].getDocument () == doc) {
                    final JEditorPane ep = eps [i];
                    SwingUtilities.invokeLater (new Runnable () {
                        public void run () {
                            try {
                                ep.scrollRectToVisible (ep.modelToView (offset));
                            } catch (BadLocationException ex) {
                                ErrorManager.getDefault ().notify (ex);
                            }
                            ep.repaint ();
                        }
                    });
                    return;
                }
            }
        }
        return;
    }
    
    private static void refresh (Document doc) {
        Iterator it = TopComponent.getRegistry ().getOpened ().iterator ();
        while (it.hasNext ()) {
            TopComponent tc = (TopComponent) it.next ();
            EditorCookie ec = (EditorCookie) tc.getLookup ().lookup (EditorCookie.class);
            if (ec == null) continue;
            JEditorPane[] eps = ec.getOpenedPanes ();
            if (eps == null) continue;
            int i, k = eps.length;
            for (i = 0; i < k; i++) {
                if (eps [i].getDocument () == doc) {
                    final JEditorPane ep = eps [i];
                    SwingUtilities.invokeLater (new Runnable () {
                        public void run () {
                            ep.repaint ();
                        }
                    });
                    return;
                }
            }
            return;
        }
    }
    
    public void removeHighlight () {
        Document doc = highlightedDocument;
        removeHighlightIn ();
        if (doc != null)
            refresh (doc);
    }
    
    private void removeHighlightIn () {
        if (highlightedNode != null) {
            Highlighting.getHighlighting (highlightedDocument).removeHighlight 
                (highlightedNode);
            highlightedNode = null;
            highlightedDocument = null;
        }
        if (highlightedTokenOffset != -1) {
            Highlighting.getHighlighting (highlightedDocument).removeHighlight 
                (highlightedTokenOffset);
            highlightedTokenOffset = -1;
            highlightedDocument = null;
        }
    }
    
    private AttributeSet highlightAS = null;
    
    private AttributeSet getHighlightAS () {
        if (highlightAS == null) {
            SimpleAttributeSet as = new SimpleAttributeSet ();
            as.addAttribute (StyleConstants.Background, color);
            highlightAS = as;
        }
        return highlightAS;
    }
    
}
