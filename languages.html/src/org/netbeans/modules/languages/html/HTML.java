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

package org.netbeans.modules.languages.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.languages.Cookie;
import org.netbeans.api.languages.SyntaxCookie;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.SToken;
import org.netbeans.api.languages.LibrarySupport;
import org.netbeans.api.languages.support.CompletionSupport;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;


/**
 *
 * @author Jan Jancura
 */
public class HTML {
    
//    private static final String HTML40DOC = "modules/ext/html40.zip";
    private static final String HTML401 = "org/netbeans/modules/languages/html/HTML401.xml";
    
    
    // tag completion ..........................................................
    
    public static String complete (Cookie cookie) {
        TokenSequence ts = cookie.getTokenSequence ();
        ts.moveNext ();
        Token t = ts.token ();
        if (t == null) return null;
        String identifier = t.text ().toString ();
        if (!identifier.equals (">")) return null;
        do {
            if (!ts.movePrevious ()) return null;
        } while (!t.id ().name ().equals ("html_element_name"));
        String et = getLibrary ().getProperty ("TAG", identifier, "endTag");
        if (!ts.movePrevious ()) return null;
        if (!ts.token ().text ().toString ().equals ("<")) return null;
        return "</" + identifier + ">";
    }
    
    
    // indent ..................................................................
    
    public static void indent (Cookie cookie) {
        TokenSequence ts = cookie.getTokenSequence ();
        Document doc = cookie.getDocument ();
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
        if (type.equals ("html_attribute_value") || 
            type.equals ("html_attribute_name") ||
            type.equals ("html_element_name") ||
            type.equals ("html_operator")
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
        indent (doc, cookie.getJTextComponent ().getCaret ().getDot (), indent);
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
    
//    public static Runnable hyperlink (PTPath path) {
//        SToken t = (SToken) path.getLeaf ();
//        String s = t.getIdentifier ();
//        s = s.substring (1, s.length () - 1).trim ();
//        if (!s.endsWith (")")) return null;
//        s = s.substring (0, s.length () - 1).trim ();
//        if (!s.endsWith ("(")) return null;
//        s = s.substring (0, s.length () - 1).trim ();
//        final Line l = (Line) DatabaseManager.get (s);
//        if (l != null)
//            return new Runnable () {
//                public void run () {
//                    l.show (l.SHOW_SHOW);
//                }
//            };
//        return null;
//    }

    
    // completion ..............................................................
    
    public static List tags (Cookie cookie) {
        if (cookie instanceof SyntaxCookie) return Collections.EMPTY_LIST;
        List tags = getLibrary ().getItems ("TAG");
        List items = new ArrayList (tags.size ());
        Iterator it = tags.iterator ();
        while (it.hasNext ()) {
            String tag = (String) it.next ();
            String description = getLibrary ().getProperty 
                ("TAG", tag, "description");
            items.add (CompletionSupport.createCompletionItem (
                tag,
                "<html><b><font color=blue>" + tag.toUpperCase () + 
                ": </font></b><font color=black> " + 
                description + "</font></html>"
            ));
        }
        return items;
    }

    public static List attributes (Cookie cookie) {
        if (cookie instanceof SyntaxCookie) return Collections.EMPTY_LIST;
        String tagName = tagName (cookie.getTokenSequence ());
        if (tagName == null) return Collections.EMPTY_LIST;
        List r = getLibrary ().getItems (tagName);
        if (r == null) return Collections.EMPTY_LIST;
        //S ystem.out.println("attributes " + r);
        List items = new ArrayList (r.size ());
        Iterator it = r.iterator ();
        while (it.hasNext ()) {
            String tag = (String) it.next ();
            String description = getLibrary ().getProperty 
                (tagName, tag, "description");
            items.add (CompletionSupport.createCompletionItem (
                tag,
                "<html><b><font color=blue>" + tag.toUpperCase () + 
                ": </font></b><font color=black> " + 
                description + "</font></html>"
            ));
        }
        //S ystem.out.println("attributeDescriptions " + attributeDescriptions);
        return items;
    }
    

    // marks ...................................................................
    
    public static boolean isDeprecatedAttribute (Cookie cookie) {
        TokenSequence ts = cookie.getTokenSequence ();
        String attribName = ts.token ().text ().toString ().toLowerCase ();
        String tagName = tagName (cookie.getTokenSequence ());
        if (tagName == null) return false;
        return "true".equals (getLibrary ().getProperty (tagName, attribName, "deprecated"));
    }

    public static boolean isDeprecatedTag (Cookie cookie) {
        Token t = cookie.getTokenSequence ().token ();
        String tagName = t.text ().toString ().toLowerCase ();
        return "true".equals (getLibrary ().getProperty ("TAG", tagName, "deprecated"));
    }

    public static boolean isEndTagRequired (Cookie cookie) {
        Token t = cookie.getTokenSequence ().token ();
        return isEndTagRequired (t.id ().name ().toLowerCase ());
    }

    static boolean isEndTagRequired (String tagName) {
        String v = getLibrary ().getProperty ("TAG", tagName, "endTag");
        return !"O".equals (v) && !"F".equals (v);
    }
    
    public static ASTNode process (SyntaxCookie cookie) {
        ASTNode n = (ASTNode) cookie.getPTPath ().getRoot ();
        List l = new ArrayList ();
        resolve (n, new Stack (), l, true);
        return ASTNode.create (n.getMimeType (), n.getNT (), n.getRule (), l, n.getOffset ());
    }
    
    
    // private methods .........................................................

    private static String tagName (TokenSequence ts) {
        while (!ts.token ().id ().name ().equals ("html_element_name"))
            if (!ts.movePrevious ()) break;
        if (!ts.token ().id ().name ().equals ("html_element_name")) 
            return null;
        return ts.token ().text ().toString ().toLowerCase ();
    }
    
    private static LibrarySupport library;
    
    private static LibrarySupport getLibrary () {
        if (library == null)
            library = LibrarySupport.create (HTML401);
        return library;
    }
    
    private static ASTNode clone (String mimeType, String nt, int rule, int offset, List children) {
        Iterator it = children.iterator ();
        List l = new ArrayList ();
        while (it.hasNext ()) {
            Object o = it.next ();
            if (o instanceof SToken)
                l.add (o);
            else
                l.add (clone ((ASTNode) o));
        }
        return ASTNode.create (mimeType, nt, rule, l, offset);
    }
    
    private static ASTNode clone (ASTNode n) {
        return clone (n.getMimeType (), n.getNT (), n.getRule (), n.getOffset (), n.getChildren ());
    }
    
    private static ASTNode clone (ASTNode n, String nt) {
        return clone (n.getMimeType (), nt, n.getRule (), n.getOffset (), n.getChildren ());
    }
    
    private static void resolve (ASTNode n, Stack s, List l, boolean findUnpairedTags) {
        Iterator it = n.getChildren ().iterator ();
        while (it.hasNext ()) {
            Object o = it.next ();
            if (o instanceof SToken) {
                l.add (o);
                continue;
            }
            ASTNode node = (ASTNode) o;
            if (node.getNT ().equals ("startTag")) {
                if (node.getTokenType ("html_end_element_end") != null) {
                    l.add (clone (node, "simpleTag"));
                } else {
                    String name = node.getTokenTypeIdentifier ("html_element_name");
                    if (name == null) 
                        name = "";
                    else
                        name = name.toLowerCase ();
                    s.add (name);
                    s.add (new Integer (l.size ()));
                    if (findUnpairedTags && isEndTagRequired (name))
                        l.add (clone (node, "unpairedStartTag"));
                    else
                        l.add (clone (node, "startTag"));
                }
                continue;
            } else
            if (node.getNT ().equals ("endTag")) {
                String name = node.getTokenTypeIdentifier ("html_element_name");
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
                        node.getRule (),
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

