/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import javax.swing.text.*;

import java.net.URL;
import java.net.MalformedURLException;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.URLMapper;
import org.openide.filesystems.FileObject;

/** 
 * Various utility methods shared by the various tasklist related modules
 *
 ** TODO - use this method everywhere!!!
 *
 * @author Tor Norbye 
 */
public final class TLUtils {

    /** Return the Line object for a particular line in a file
     */
    public static Line getLineByNumber(DataObject dobj, int lineno) {
        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // XXX HACK
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno-1);
                    return l;
                }
            }
        } catch (Exception e) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "getLineByNumber - file " + dobj + " and lineno=" + lineno); // NOI18N
            ErrorManager.getDefault().
                notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
    }

    /** Replace the given symbol on the line with the new symbol - starting
        roughly at the given column (symbol should be at col or col+1)
        @param sb Buffer to write into
        @param text The text to be copied into the buffer, except for
            the substitution of symbol into newSymbol.
        @param pos Earliest possible starting position of the symbol
        @param symbol The symbol which may occur multiple times; we want
            each reference replaced (provided it's a java identifier - not
            a prefix or suffix of a larger identifier
        @param newSymbol The string to replace the old symbol
        @param bold If true, make the new symbol bold
        @param underlineBegin If -1, underline the newSymbol starting at
            this position, ending at underlineEnd.
        @param underlineEnd Only considererdd if underlineBegin != -1; 
            ending position for underlining started at underlineBegin.
    */
    public static void replaceSymbol(StringBuffer sb, String text, int pos, String symbol, 
                                     String newSymbol, boolean bold,
                                     int underlineBegin, int underlineEnd) {
        //System.out.println("replace('" + text + "', " + pos + ", '" + symbol + "', '" + newSymbol + "')");
        if (pos > 0) {
            // For some compilers, the position is off by 1 so make sure 
            // we catch the earliest possible match
            pos--;
        }
        int from = 0;
        int symLen = symbol.length();
        int texLen = text.length();
        while (true) {
            int n = text.indexOf(symbol, pos);
            if (n == -1) {
                break;
            }
            if ((n+symLen < texLen-1) &&
                Character.isJavaIdentifierPart(text.charAt(n+symLen))) {
                pos = n+symLen;
                continue;
            }
            
            for (int i = from; i < n; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (bold) {
                sb.append("<b>"); // NOI18N
            }
            if (underlineBegin != -1) {
                for (int i = 0; i < underlineBegin; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
                sb.append("<u>"); // NOI18N
                for (int i = underlineBegin; i < underlineEnd; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
                sb.append("</u>"); // NOI18N
                int nl = newSymbol.length();
                for (int i = underlineEnd; i < nl; i++) {
                    appendHTMLChar(sb, newSymbol.charAt(i));
                }
            } else {
                appendHTMLString(sb, newSymbol);
            }
            if (bold) {
                sb.append("</b>"); // NOI18N
            }
            pos = n+symLen;
            from = pos;
        }
        for (int i = from; i < texLen; i++) {
            appendHTMLChar(sb, text.charAt(i));
        }
    }

    /** Append a "window of text with the given line as the middle line.
     * It will escape HTML characters.
     * @param line The line we want to obtain a window for.
     * @param currText If non null, use this line instead of the
     *     text on the current line.
     */
    public static void appendSurroundingLine(StringBuffer sb, Line line, 
                                             int offset) {
        DataObject dobj = org.openide.text.DataEditorSupport.findDataObject (line);
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc == null) {
                return;
            }
            Line.Set ls = lc.getLineSet();
            if (ls == null) {
                return;
            }

            int lineno = line.getLineNumber();
            if (lineno+offset < 0) {
                // Trying to surround the first line - no "before" line
                return;
            }
            Line before = ls.getCurrent(lineno+offset);
            appendHTMLString(sb, before.getText());
        } catch (Exception e) {
            ErrorManager.getDefault().
                notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    /** Compute first difference position for two strings */
    public static int firstDiff(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        int n;
        if (n1 < n2) {
            n = n1;
        } else {
            n = n2;
        }
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /** Compute last difference position for two strings. Returns
        DISTANCE FROM THE END! */
    public static int lastDiff(String s1, String s2) {
        int n1 = s1.length()-1;
        int n2 = s2.length()-1;
        int i = 0;
        while ((n2 >= 0) && (n1 >= 0)) {
            if (s1.charAt(n1) != s2.charAt(n2)) {
                return i;
            }
            --n2;
            --n1;
            ++i;
        }
        return i;
    }    
    
    /** Append a character to a StringBuffer intended for HTML
        display - it will escape <, >, etc. such that the char is
        shown properly in HTML.
    */
    public static void appendHTMLChar(StringBuffer sb, char c) {
        // See also HTMLSupport.toHTML if you modify this
        switch (c) {
        case '<': sb.append("&lt;"); break; // NOI18N
        case '>': sb.append("&gt;"); break; // NOI18N
        case '&': sb.append("&amp;"); break; // NOI18N
        case '"': sb.append("&quot;"); break; // NOI18N
        case ' ': sb.append("&nbsp;"); break; // NOI18N
        case '\n': sb.append("<br>"); break; // NOI18N
        default: sb.append(c);
        }
    }

    /** Append a string to a StringBuffer intended for HTML
        display - it will escape <, >, etc. such that they are
        shown properly in HTML.
    */
    public static void appendHTMLString(StringBuffer sb, String s) {
        int n = s.length();
        for (int i = 0; i < n; i++) {
            appendHTMLChar(sb, s.charAt(i));
        }
    }

    /** Append the given string to the given string buffer,
     * underlining from a starting index to an ending index.
     * Also escape HTML characters.
     */
    public static void appendAttributed(StringBuffer sb,
                                        String text, 
                                        int begin, 
                                        int end,
                                        boolean underline,
                                        boolean bold) {
        if (begin != -1) {
            for (int i = 0; i < begin; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (underline) {
                sb.append("<u>"); // NOI18N
            }
            if (bold) {
                sb.append("<b>"); // NOI18N
            }
            for (int i = begin; i < end; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
            if (underline) {
                sb.append("</u>"); // NOI18N
            }
            if (bold) {
                sb.append("</b>"); // NOI18N
            }
            int nl = text.length();
            for (int i = end; i < nl; i++) {
                appendHTMLChar(sb, text.charAt(i));
            }
        } else {
            appendHTMLString(sb, text);
        }
    }

    public static Element getElement(Document d, Line line) {
	if (d == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "d was null");
            return null;
	}

        if (!(d instanceof StyledDocument)) {
            ErrorManager.getDefault().log(ErrorManager.USER, "Not a styleddocument");
            return null;
        }
            
        StyledDocument doc = (StyledDocument)d;
        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement ();
        }
        int lineNumber = line.getLineNumber();
        Element elm = e.getElement(lineNumber);
        return elm;
    }

    public static Document getDocument(Line line) {
        DataObject dao = org.openide.text.DataEditorSupport.findDataObject (line);
        if (!dao.isValid()) {
            //ErrorManager.getDefault().log(ErrorManager.USER, "dataobject was not null");
            return null;
        }
        return getDocument(dao);
    }

    public static Document getDocument(DataObject dao) {
	final EditorCookie edit = (EditorCookie)dao.getCookie(EditorCookie.class);
	if (edit == null) {
            //ErrorManager.getDefault().log(ErrorManager.USER, "no editor cookie!");
	    return null;
	}

        Document d = edit.getDocument(); // Does not block
        return d;
    }

    /** Remove a particular line. Make sure that the line begins with
     * a given prefix, just in case.
     * @param prefix A prefix that the line to be deleted must start with
     */
    public static boolean deleteLine(Line line, String prefix) {
        Document doc = getDocument(line);
        Element elm = getElement(doc, line);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            if (!text.startsWith(prefix)) {
                return false;
            }
            doc.remove(offset, endOffset-offset);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }

    /** Comment out a particular line (in a Java file). Make sure that
     * the line begins with a given prefix, just in case.
     * @param prefix A prefix that the line to be commented out must start with
     */
    public static boolean commentLine(Line line, String prefix) {
        Document doc = getDocument(line);
        Element elm = getElement(doc, line);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            if (!text.startsWith(prefix)) {
                return false;
            }
            doc.insertString(offset, "// ", null); // NOI18N
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }

    /** Given a file object, produce a URL suitable for inclusion
     * in a tasklist (e.g. it must be persistent, not based on some
     * currently assigned webserver port etc.) */
    public static String toURL(FileObject fo) {
        // Try to construct our own URL since 
        /*
        File file = FileUtil.toFile(fo);
        String filename;
        if (file == null) {
            URL url = URLMapper.findURL(fo, URLMapper.INTERNAL);
            filename = url.toExternalForm();
            // System.out.println("INTERNAL URL was " + filename);
            // url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
            // filename = url.toExternalForm();
            // System.out.println("EXTERNAL URL was " + filename);
            // url = URLMapper.findURL(fo, URLMapper.NETWORK);
            // filename = url.toExternalForm();
            // System.out.println("NETWORK URL was " + filename);
            return null;
        } else {
            filename = "file:" + file.getPath(); // NOI18N
        }
        return filename;
        */
        return URLMapper.findURL(fo, URLMapper.INTERNAL).toExternalForm();
    }

    /** Given a URL created by fileToURL, return a file object representing
     * the given file. Returns null if the URL can (no longer) be resolved.
     */
    public static FileObject[] fromURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
        return URLMapper.findFileObjects(url);
    }
}



