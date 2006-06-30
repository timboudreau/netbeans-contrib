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
package org.netbeans.modules.latex.refactoring.ui;

import java.io.IOException;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.xml.XMLUtil;

/**
 *
 * @author Jan Lahoda
 */
public class UseNode extends AbstractNode {
    
    private org.netbeans.modules.latex.model.command.Node node;
    
    private String htmlDisplayName;
            
    /** Creates a new instance of UseNode */
    public UseNode(Node node) {
        super(Children.LEAF);
        
        this.node = node;
        htmlDisplayName = computeHtmlDisplayName(node);
        setDisplayName(node.getFullText().toString());
        
        getCookieSet().add(new OpenCookieImpl());
    }
    
    private String computeHtmlDisplayName(Node node) {
        try {
            SourcePosition sp = node.getStartingPosition();
            SourcePosition ep = node.getEndingPosition();
            Document doc = Utilities.getDefault().openDocument(sp.getFile());
            int rowStartOffset = org.netbeans.editor.Utilities.getRowStart((BaseDocument) doc, sp.getOffsetValue()); //TODO
            int rowEndOffset = org.netbeans.editor.Utilities.getRowEnd((BaseDocument) doc, sp.getOffsetValue()); //TODO
            String line = doc.getText(rowStartOffset, rowEndOffset - rowStartOffset);
            int start = sp.getOffsetValue() - rowStartOffset;
            int end   = ep.getOffsetValue() <= rowEndOffset ? ep.getOffsetValue() - rowStartOffset : rowEndOffset - rowStartOffset;
            
            String prefix = line.substring(0, start);
            String text   = line.substring(start, end);
            String suffix = line.substring(end);
            
            return "<html>" + XMLUtil.toElementContent(prefix) + "<b>" + XMLUtil.toElementContent(text) + "</b>" + XMLUtil.toElementContent(suffix);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return node.getFullText().toString();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return node.getFullText().toString();
        }
    }
    
    @Override
    public String getHtmlDisplayName() {
        return htmlDisplayName;
    }
    
    @Override
    public Action getPreferredAction() {
        return OpenAction.get(OpenAction.class);
    }
    
    private final class OpenCookieImpl implements OpenCookie {
        
        public void open() {
            Utilities.getDefault().openPosition(node.getStartingPosition());
        }
        
    }
}
