/*
 * CSS.java
 *
 * Created on May 18, 2006, 1:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.netbeans.modules.languages.Language;
import org.netbeans.modules.languages.LanguagesManager;
import org.netbeans.modules.languages.fold.DatabaseManager;
import org.netbeans.modules.languages.parser.ASTNode;
import org.netbeans.modules.languages.parser.Input;
import org.netbeans.modules.languages.parser.SToken;
import org.netbeans.modules.languages.parser.TokenInput;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.text.Line;

/**
 *
 * @author Jan Jancura
 */
public class HTML {
    
    private static final String HTML40DOC = "modules/ext/html40.zip";
    
    
    public static Runnable hyperlink (SToken t) {
        String s = t.getIdentifier ();
        s = s.substring (1, s.length () - 1).trim ();
        if (!s.endsWith (")")) return null;
        s = s.substring (0, s.length () - 1).trim ();
        if (!s.endsWith ("(")) return null;
        s = s.substring (0, s.length () - 1).trim ();
        final Line l = (Line) DatabaseManager.get (s);
        if (l != null)
            return new Runnable () {
                public void run () {
                    l.show (l.SHOW_SHOW);
                }
            };
        return null;
    }

    public static boolean isDeprecatedTag (SToken t) {
        Map tags = getTags ();
        String tagName = t.getIdentifier ().toLowerCase ();
        Map m = (Map) tags.get (tagName);
        if (m == null) return false;
        return "D".equals (m.get ("Depr."));
    }

    public static boolean isEndTagRequired (SToken t) {
        return isEndTagRequired (t.getIdentifier ().toLowerCase ());
    }

    static boolean isEndTagRequired (String tagName) {
        Map tags = getTags ();
        Map m = (Map) tags.get (tagName);
        if (m == null) return false;
        return !"O".equals (m.get ("End Tag")) &&
               !"F".equals (m.get ("End Tag"));
    }

    private static List tags = null;
    
    public static List tags (SToken t) {
        Map m = getTags ();
        if (tags == null)
            tags = new ArrayList (m.keySet ());
        return tags;
    }

    private static List tagDescriptions = null;
    
    public static List tagDescriptions (SToken t) {
        Map tags = getTags ();
        if (tagDescriptions == null) {
            tagDescriptions = new ArrayList (tags.size ());
            Iterator it = tags.keySet ().iterator ();
            while (it.hasNext ()) {
                String name = (String) it.next ();
                Map properties = (Map) tags.get (name);
                String description = (String) properties.get ("Description");
                tagDescriptions.add (
                    "<html><b><font color=blue>" + name.toUpperCase () + 
                    ": </font></b><font color=#aaaaaa> " + 
                    description + "</font></html>"
                );
            }
        } 
        return tagDescriptions;
    }

    private static List attributes = null;
    
    public static List attributes (SToken t) {
        Map m = getAttributes ();
        if (attributes == null)
            attributes = new ArrayList (m.keySet ());
        return attributes;
    }

    private static List attributeDescriptions = null;
    
    public static List attributeDescriptions (SToken t) {
        Map attribs = getAttributes ();
        if (attributeDescriptions == null) {
            attributeDescriptions = new ArrayList (attribs.size ());
            Iterator it = attribs.keySet ().iterator ();
            while (it.hasNext ()) {
                String name = (String) it.next ();
                Map properties = (Map) attribs.get (name);
                String description = (String) properties.get ("Comment");
                attributeDescriptions.add (
                    "<html><b><font color=blue>" + name.toUpperCase () + 
                    ": </font></b><font color=#aaaaaa> " + 
                    description + "</font></html>"
                );
            }
        } 
        return attributeDescriptions;
    }
    

    public static boolean isDeprecatedAttribute (SToken t) {
        Map tags = getAttributes ();
        String tagName = t.getIdentifier ().toLowerCase ();
        Map m = (Map) tags.get (tagName);
        if (m == null) return false;
        return "D".equals (m.get ("Depr."));
    }
    
    public static ASTNode process (ASTNode n) {
        List l = new ArrayList ();
        resolve (n, new Stack (), l, true);
        return ASTNode.create (n.getMimeType (), n.getNT (), n.getRule (), n.getParent (), l, n.getOffset ());
    }
    
    
    // private methods .........................................................
    
    private static ASTNode create (ASTNode n, String nt) {
        return ASTNode.create (n.getMimeType (), nt, n.getRule (), n.getParent (), n.getChildren (), n.getOffset ());
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
                if (node.getTokenType ("html-end_element_end") != null) {
                    l.add (create (node, "simpleTag"));
                } else {
                    String name = node.getTokenTypeIdentifier ("html-element_name");
                    if (name == null) 
                        name = "";
                    else
                        name = name.toLowerCase ();
                    s.add (name);
                    s.add (new Integer (l.size ()));
                    if (findUnpairedTags && isEndTagRequired (name))
                        l.add (create (node, "unpairedStartTag"));
                    else
                        l.add (create (node, "startTag"));
                }
                continue;
            } else
            if (node.getNT ().equals ("endTag")) {
                String name = node.getTokenTypeIdentifier ("html-element_name");
                if (name == null) 
                    name = "";
                else
                    name = name.toLowerCase ();
                int indexS = s.lastIndexOf (name);
                if (indexS >= 0) {
                    int indexL = ((Integer) s.get (indexS + 1)).intValue ();
                    List ll = l.subList (indexL, l.size ());
                    ll.set (0, create ((ASTNode) ll.get (0), "startTag"));
                    List ll1 = new ArrayList (ll);
                    ll1.add (node);
                    ASTNode tag = ASTNode.create (
                        node.getMimeType (),
                        "tag",
                        node.getRule (),
                        node.getParent (),
                        ll1,
                        ((ASTNode) ll1.get (0)).getOffset ()
                    );
                    ll.clear ();
                    s.subList (indexS, s.size ()).clear ();
                    l.add (tag);
                } else
                    l.add (create (node, "unpairedEndTag"));
                continue;
            } else
            if (node.getNT ().equals ("tags")) {
                resolve (node, s, l, findUnpairedTags);
                continue;
            }
            l.add (node);
        }
    }

    private static Map tagsMap = null;
    
    private static Map getTags () {
        if (tagsMap == null) {
            ASTNode n = parseHTML ("index/elements.html");
            if (n != null)
                tagsMap = readTags (n);
            else
                tagsMap = new HashMap ();
        }
        return tagsMap;
    }
    
    private static ASTNode parseHTML (String resourceName) {
        long start = System.currentTimeMillis ();
        try {
            File f = InstalledFileLocator.getDefault().locate 
                (HTML40DOC, null, false); //NoI18N
            if (f == null) {
                System.out.println("File " + HTML40DOC + " not found!");
                return null;
            }
            InputStream in = null;
            try {
                URL url = f.toURL ();
                url = FileUtil.getArchiveRoot (url);
                url = new URL (url.toString () + resourceName);
                in = url.openStream ();
            } catch (MalformedURLException e){
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
                return null;
            }
            InputStreamReader r = new InputStreamReader (in);
            BufferedReader br = new BufferedReader (r);
            StringBuilder sb = new StringBuilder ();
            String ln = br.readLine ();
            while (ln != null) {
                sb.append (ln).append ('\n');
                ln = br.readLine ();
            }
            Language l = LanguagesManager.getDefault ().getLanguage ("text/html2");
            TokenInput ti = TokenInput.create (
                l.getParser (), 
                Input.create (sb.toString (), resourceName),
                l.getSkipTokenTypes ()
            );
            ASTNode node = l.getAnalyser ().read (ti, false);
            List rl = new ArrayList ();
            resolve (node, new Stack (), rl, false);
            node = ASTNode.create (
                node.getMimeType (), 
                node.getNT (), 
                node.getRule (), 
                node.getParent (), 
                rl, 
                node.getOffset ()
            );
            in.close ();
            System.out.println("parse " + resourceName + " " + (System.currentTimeMillis () - start));
            return node;
        } catch (Exception ex) {
            System.out.println("parse-error " + resourceName + " " + (System.currentTimeMillis () - start));
            ErrorManager.getDefault ().notify (ex);
            return null;
        }
    }
    
    private static Map readTags (ASTNode node) {
        Map result = new HashMap ();
        node = getFirstTag (node, "html");
        node = getFirstTag (node, "body");
        node = getFirstTag (node, "table");
        Iterator it = getTags (node, "tr").iterator ();
        while (it.hasNext ()) {
            ASTNode tr = (ASTNode) it.next ();
            String name = null;
            Map params = new HashMap ();
            Iterator it2 = getTags (tr, "td").iterator ();
            while (it2.hasNext ()) {
                ASTNode td = (ASTNode) it2.next ();
                String title = getAttributeValue (td, "title");
                String url = getAttributeValue (
                    td.getNode ("tag"),
                    "href"
                ), value = null;
                if (url != null) {
                    value = td.getNode ("tag").getNode ("etext").
                        getTokenType ("html-text").getIdentifier ().toLowerCase ();
                    params.put (title + "_URL", url);
                } else
                    value = td.getNode ("etext").getAsText ();
                if (value.equals ("&nbsp;"))
                    value = "";
                params.put (title, value);
                if (title.equals ("Name")) {
                    name = value;
                    result.put (name, params);
                }
            }
        }
        try {
            FileWriter fw = new FileWriter ("c:\\tags.xml");
            it = result.keySet ().iterator ();
            while (it.hasNext ()) {
                String tagName = (String) it.next ();
                Map params = (Map) result.get (tagName);
                boolean deprecated = "D".equals (params.get ("Depr."));
                String description = (String) params.get ("Description");
                String endTag = (String) params.get ("End Tag");
                fw.write ("\t<node key = \"" + tagName + "\"\tcontext = \"TAG\"\tdeprecated = \"" + deprecated + "\"\tendTag = \"" + endTag + "\"\tdescription = \"" + description + "\"/>\n");
            }
            fw.close ();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    private static Map attributesMap = null;
    
    private static Map getAttributes () {
        if (attributesMap == null) {
            ASTNode n = parseHTML ("index/attributes.html");
            if (n != null)
                attributesMap = readAttributes (n);
            else
                attributesMap = new HashMap ();
        }
        return attributesMap;
    }
    
    private static Map readAttributes (ASTNode node) {
        Map result = new HashMap ();
        node = getFirstTag (node, "html");
        node = getFirstTag (node, "body");
        node = getFirstTag (node, "table");
        Iterator it = getTags (node, "tr").iterator ();
        while (it.hasNext ()) {
            ASTNode tr = (ASTNode) it.next ();
            String name = null;
            Map params = new HashMap ();
            Iterator it2 = getTags (tr, "td").iterator ();
            while (it2.hasNext ()) {
                ASTNode td = (ASTNode) it2.next ();
                String title = getAttributeValue (td, "title");
                String url = getAttributeValue (
                    td.getNode ("tag"),
                    "href"
                ), value = null;
                if (!title.equals ("Related Elements")) {
                    if (url != null) {
                        value = td.getNode ("tag").getNode ("etext").
                            getTokenType ("html-text").getIdentifier ().toLowerCase ();
                        params.put (title + "_URL", url);
                    } else
                        value = td.getNode ("etext").getAsText ();
                    if (value.equals ("&nbsp;"))
                        value = "";
                } else {
                    List l = td.getChildren ();
                    Iterator ii = l.iterator ();
                    while (ii.hasNext ()) {
                        Object elem = ii.next ();
                        if (!(elem instanceof ASTNode)) continue;
                        if (!((ASTNode) elem).getNT ().equals ("tag")) continue;
                        if (value == null)
                            value = ((ASTNode) elem).getNode ("etext").getAsText ();
                        else
                            value += "," + ((ASTNode) elem).getNode ("etext").getAsText ();
                    }
                }
                params.put (title, value);
                if (title.equals ("Name")) {
                    name = value;
                    result.put (name, params);
                }
            }
            //System.out.println(name + " : " + params);
        }
        
        try {
            FileWriter fw = new FileWriter ("c:\\attribs.xml");
            it = result.keySet ().iterator ();
            while (it.hasNext ()) {
                String tagName = (String) it.next ();
                Map params = (Map) result.get (tagName);
                boolean deprecated = "D".equals (params.get ("Depr."));
                String description = (String) params.get ("Comment");
                String endTag = (String) params.get ("End Tag");
                String context = (String) params.get ("Related Elements");
                fw.write ("\t<node key = \"" + tagName + "\"\tcontext = \"" + context + "\"\tdeprecated = \"" + deprecated + "\"\tdescription = \"" + description + "\"/>\n");
            }
            fw.close ();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    private static String getAttributeValue (ASTNode node, String attributeName) {
        if (node == null) return null;
        List path = node.getNode ("startTag").findToken 
            ("html-attribute-name", attributeName);
        if (path == null) return null;
        ASTNode attributes = (ASTNode) path.get (1);
        List path2 = attributes.findToken ("html-attribute-value", null);
        String value = ((SToken) path2.get (0)).getIdentifier ();
        if (value.startsWith ("\"")) value = value.substring (1, value.length () - 1);
        return value;
    }
    
    private static List getTags (ASTNode node, String tagName) {
        List result = new ArrayList ();
        Iterator it = node.getChildren ().iterator ();
        while (it.hasNext ()) {
            Object e = it.next ();
            if (e instanceof SToken) continue;
            ASTNode n = (ASTNode) e;
            if (n.getNT ().equals ("tag")) {
                ASTNode startTag = n.getNode ("startTag");
                String tn = startTag.getTokenTypeIdentifier ("html-element_name");
                if (tn.equals (tagName)) result.add (n);
            }
        }
        return result;
    }
    
    private static ASTNode getFirstTag (ASTNode node, String tagName) {
        Iterator it = node.getChildren ().iterator ();
        while (it.hasNext ()) {
            Object e = it.next ();
            if (e instanceof SToken) continue;
            ASTNode n = (ASTNode) e;
            if (n.getNT ().equals ("tag")) {
                ASTNode startTag = n.getNode ("startTag");
                String tn = startTag.getTokenTypeIdentifier ("html-element_name");
                if (tn.equals (tagName)) return n;
            }
        }
        return null;
    }
}

