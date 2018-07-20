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

package org.netbeans.modules.languages.xml;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.languages.Context;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;


/**XML
 * @author Jan Jancura
 */
public class XML {

    
    
    // indent ..................................................................
    
    public static void indent (Context context) {
        TokenSequence ts = context.getTokenSequence ();
        Document doc = context.getDocument ();
        int indent;
        Token t;
        do {
            ts.movePrevious ();
            t = ts.token ();
        } while (t.text ().toString ().trim ().length () == 0);
        String text = t.text ().toString ();
        String type = t.id ().name ();
        int ln = NbDocument.findLineNumber ((StyledDocument) doc, ts.offset ());
        int start = NbDocument.findLineOffset ((StyledDocument) doc, ln);
        if (text.equals (">") || text.equals ("/>")) {
            do {
                if (!ts.movePrevious ()) break;
                t = ts.token ();
            } while (!t.text ().toString ().equals ("<"));
            indent = getIndent (ts, doc);
        } else
        if (type.equals ("attribute_value") || 
            type.equals ("attribute_name") ||
            type.equals ("element_name") ||
            type.equals ("operator")
        ) {
            do {
                if (!ts.movePrevious ()) break;
                t = ts.token ();
            } while (
                !t.text ().toString ().equals ("<") &&
                ts.offset () > start
            );
            if (t.text ().toString ().equals ("<"))
                indent = getIndent (ts, doc) + 4;
            else {
                ts.moveNext ();
                indent = getIndent (ts, doc);
            }
        } else
        if (text.equals ("<")) {
            indent = getIndent (ts, doc) + 4;
        } else
            indent = getIndent (ts, doc);
        indent (doc, context.getJTextComponent ().getCaret ().getDot (), indent);
    }
    
    private static int getIndent (TokenSequence ts, Document doc) {
        int ln = NbDocument.findLineNumber ((StyledDocument) doc, ts.offset ());
        int start = NbDocument.findLineOffset ((StyledDocument) doc, ln);
        ts.move (start);
        ts.moveNext ();
        if (ts.token ().text ().toString ().trim ().length () == 0)
            ts.moveNext ();
        return ts.offset () - start;
    }
    
    private static void indent (Document doc, int offset, int i) {
        StringBuilder sb = new StringBuilder ();
        while (i > 0) {
            sb.append (' ');i--;
        }
        try {
            doc.insertString (offset, sb.toString (), null);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault ().notify (ex);
        }
    }
    
    public static ASTNode process (SyntaxContext context) {
        ASTNode n = (ASTNode) context.getASTPath ().getRoot ();
        List l = new ArrayList ();
        resolve (n, new Stack (), l, true);
        return ASTNode.create (n.getMimeType (), n.getNT (), l, n.getOffset ());
    }

    
    // tag completion ..........................................................
    
    public static String complete (Context context) {
        TokenSequence ts = context.getTokenSequence ();
        ts.moveNext ();
        Token t = ts.token ();
        if (t == null) return null;
        String identifier = t.text ().toString ();
        if (!identifier.equals (">")) return null;
        do {
            if (!ts.movePrevious ()) return null;
        } while (!t.id ().name ().equals ("element_name"));
        if (!ts.movePrevious ()) return null;
        if (!ts.token ().text ().toString ().equals ("<")) return null;
        return "</" + identifier + ">";
    }    
    
    // private methods .........................................................

    private static String tagName (TokenSequence ts) {
        while (!ts.token ().id ().name ().equals ("element_name"))
            if (!ts.movePrevious ()) break;
        if (!ts.token ().id ().name ().equals ("element_name")) 
            return null;
        return ts.token ().text ().toString ().toLowerCase ();
    }
    
    private static ASTNode clone (String mimeType, String nt, int offset, List children) {
        Iterator it = children.iterator ();
        List l = new ArrayList ();
        while (it.hasNext ()) {
            Object o = it.next ();
            if (o instanceof ASTToken)
                l.add (clone ((ASTToken) o));
            else
                l.add (clone ((ASTNode) o));
        }
        return ASTNode.create (mimeType, nt, l, offset);
    }
    
    private static ASTNode clone (ASTNode n) {
        return clone (n.getMimeType (), n.getNT (), n.getOffset (), n.getChildren ());
    }
    
    private static ASTToken clone (ASTToken token) {
        List<ASTItem> children = new ArrayList ();
        Iterator<ASTItem> it = token.getChildren ().iterator ();
        while (it.hasNext ()) {
            ASTItem item = it.next ();
            if (item instanceof ASTNode)
                children.add (clone ((ASTNode) item));
            else
                children.add (clone ((ASTToken) item));
        }
        return ASTToken.create (
            token.getMimeType (),
            token.getType (),
            token.getIdentifier (),
            token.getOffset (),
            token.getLength (),
            children
        );
    }
    
    private static ASTNode clone (ASTNode n, String nt) {
        return clone (n.getMimeType (), nt, n.getOffset (), n.getChildren ());
    }
    
    private static void resolve (ASTNode n, Stack s, List l, boolean findUnpairedTags) {
        Iterator<ASTItem> it = n.getChildren ().iterator ();
        while (it.hasNext ()) {
            ASTItem item = it.next ();
            if (item instanceof ASTToken) {
                l.add (clone ((ASTToken) item));
                continue;
            }
            ASTNode node = (ASTNode) item;
            if (node.getNT ().equals ("startTag")) {
                if (node.getTokenType ("end_element_end") != null) {
                    l.add (clone (node, "simpleTag"));
                } else {
                    String name = node.getTokenTypeIdentifier ("element_name");
                    if (name == null) 
                        name = "";
                    else
                        name = name.toLowerCase ();
                    s.add (name);
                    s.add (new Integer (l.size ()));
                    if (findUnpairedTags)
                        l.add (clone (node, "unpairedStartTag"));
                    else
                        l.add (clone (node, "startTag"));
                }
                continue;
            } else
            if (node.getNT ().equals ("endTag")) {
                String name = node.getTokenTypeIdentifier ("element_name");
                if (name == null) 
                    name = "";
                else
                    name = name.toLowerCase ();
                int indexS = s.lastIndexOf (name);
                if (indexS >= 0) {
                    int indexL = ((Integer) s.get (indexS + 1)).intValue ();
                    List ll = l.subList (indexL, l.size ());
                    ll.set (0, clone ((ASTNode) ll.get (0), "startTag"));
                    List ll1 = new ArrayList (ll);
                    ll1.add (node);
                    ASTNode tag = clone (
                        node.getMimeType (),
                        "tag",
                        ((ASTNode) ll1.get (0)).getOffset (),
                        ll1
                    );
                    ll.clear ();
                    s.subList (indexS, s.size ()).clear ();
                    l.add (tag);
                } else
                    l.add (clone (node, "unpairedEndTag"));
                continue;
            } else
            if (node.getNT ().equals ("tags")) {
                resolve (node, s, l, findUnpairedTags);
                continue;
            }
            l.add (clone (node));
        }
    }
}

