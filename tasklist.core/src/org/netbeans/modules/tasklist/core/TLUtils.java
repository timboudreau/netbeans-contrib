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

import java.io.File;
import javax.swing.text.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.JEditorPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;

import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.URLMapper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** 
 * Various utility methods shared by the various tasklist related modules
 *
 * @author Tor Norbye 
 */
public final class TLUtils {
    private static Logger LOGGER = TLUtils.getLogger(TLUtils.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }

    /** Return the Line object for a particular line in a file
     */
    public static Line getLineByNumber(DataObject dobj, int lineno) {
        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno-1);
                    return l;
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            // line was at the end of file and is deleted now

        } catch (Exception e) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "getLineByNumber - file " + dobj + " and lineno=" + lineno); // NOI18N
            ErrorManager.getDefault().
                notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
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
        // steal DirectURLMapper from Javadoc module
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
    
    /**
     * Counts all children nodes of the given node recursively.
     *
     * @return node a node or null
     */
    public static int getChildrenCountRecursively(Node node) {
        if (node == null) return 0;
        
        Children children = node.getChildren();
        if(children.getNodesCount() == 0) return 0;

        int n = 0;
        Node[] nodes = children.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            n += getChildrenCountRecursively(nodes[i]) + 1;
        }
        return n;
    }
    
    /**
     * Gets a property descriptor for the specified property name.
     *
     * @param n a node
     * @param prop name of a property
     * @return found property or null
     */
    public static Node.Property getProperty(Node n, String prop) {
        Node.PropertySet[] propsets = n.getPropertySets();
        for (int j = 0; j < propsets.length; ++j) {
            Node.Property[] props = propsets[j].getProperties();
            for (int k = 0; k < props.length; ++k) {
                if (props[k].getName().equals(prop)) {
                    return props[k];
                }
            }
        }
        return null;
    }


    /**
     * Creates a simple logger for the specified class. 
     * Category of the logger will be equals to the class name.
     *
     * @param clazz the name of the class will be used for the logger's category
     * @return logger
     */
    public static Logger getLogger(Class clazz) {
        // eliminate duplications. There are two console handlers somehow,
        // the second one publishes also INFO messages
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        logger.addHandler(ch);
        logger.setLevel(Level.WARNING);
        return logger;
    }

    /** For debugging purposes only */
    public static void traceFocus(final Container c) {

        c.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                System.err.println("Component " + c + " gained focus.");
                Thread.dumpStack();
            }
            public void focusLost(FocusEvent e) {
                System.err.println("Component " + c + " lost focus.");
                Thread.dumpStack();
            }

        });
        Component[] cs = c.getComponents();
        if (cs != null) {
            for (int i = 0; i<cs.length; i++) {
                if (cs[i] instanceof Container) {
                    traceFocus((Container)cs[i]);
                }
            }
        }
    }


}
