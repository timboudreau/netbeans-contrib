/*
 * CSS.java
 *
 * Created on May 18, 2006, 1:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.netbeans.modules.languages.fold.DatabaseManager;
import org.netbeans.modules.languages.LibrarySupport;
import org.netbeans.modules.languages.parser.ASTNode;
import org.netbeans.modules.languages.parser.PTPath;
import org.netbeans.modules.languages.parser.SToken;
import org.openide.text.Line;

/**
 *
 * @author Jan Jancura
 */
public class HTML {
    
//    private static final String HTML40DOC = "modules/ext/html40.zip";
    private static final String HTML401 = "org/netbeans/modules/languages/html/HTML401.xml";
    
    
    public static Runnable hyperlink (PTPath path) {
        SToken t = (SToken) path.getLeaf ();
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

    public static boolean isDeprecatedTag (PTPath path) {
        SToken t = (SToken) path.getLeaf ();
        String tagName = t.getIdentifier ().toLowerCase ();
        return "true".equals (getLibrary ().getProperty ("TAG", tagName, "deprecated"));
//        Map tags = getTags ();
//        Map m = (Map) tags.get (tagName);
//        if (m == null) return false;
//        return "D".equals (m.get ("Depr."));
    }

    public static boolean isEndTagRequired (PTPath path) {
        SToken t = (SToken) path.getLeaf ();
        return isEndTagRequired (t.getIdentifier ().toLowerCase ());
    }

    static boolean isEndTagRequired (String tagName) {
        String v = getLibrary ().getProperty ("TAG", tagName, "endTag");
        return !"O".equals (v) && !"F".equals (v);
//        Map tags = getTags ();
//        Map m = (Map) tags.get (tagName);
//        if (m == null) return false;
//        return !"O".equals (m.get ("End Tag")) &&
//               !"F".equals (m.get ("End Tag"));
    }

//    private static List tags = null;
    
    public static List tags (PTPath path) {
        return getLibrary ().getItems ("TAG");
//        Map m = getTags ();
//        if (tags == null)
//            tags = new ArrayList (m.keySet ());
//        return tags;
    }

    private static List tagDescriptions = null;
    
    public static List tagDescriptions (PTPath path) {
        if (tagDescriptions == null) {
            List tags = getLibrary ().getItems ("TAG");
            tagDescriptions = new ArrayList (tags.size ());
            Iterator it = tags.iterator ();
            while (it.hasNext ()) {
                String tag = (String) it.next ();
                String description = getLibrary ().getProperty 
                    ("TAG", tag, "description");
                tagDescriptions.add (
                    "<html><b><font color=blue>" + tag.toUpperCase () + 
                    ": </font></b><font color=black> " + 
                    description + "</font></html>"
                );
            }
        }
        return tagDescriptions;
//        Map tags = getTags ();
//        if (tagDescriptions == null) {
//            tagDescriptions = new ArrayList (tags.size ());
//            Iterator it = tags.keySet ().iterator ();
//            while (it.hasNext ()) {
//                String name = (String) it.next ();
//                Map properties = (Map) tags.get (name);
//                String description = (String) properties.get ("Description");
//                tagDescriptions.add (
//                    "<html><b><font color=blue>" + name.toUpperCase () + 
//                    ": </font></b><font color=#aaaaaa> " + 
//                    description + "</font></html>"
//                );
//            }
//        } 
//        return tagDescriptions;
    }

//    private static List attributes = null;
    
    public static List attributes (PTPath path) {
        ASTNode n = (ASTNode) path.get (path.size () - 2);
        n = n.getParent ("startTag");
        List r = getLibrary ().getItems (n.getTokenTypeIdentifier ("html-element_name"));
        System.out.println("attributes " + r);
        return r;
//        Map m = getAttributes ();
//        if (attributes == null)
//            attributes = new ArrayList (m.keySet ());
//        return attributes;
    }

    //private static List attributeDescriptions = null;
    
    public static List attributeDescriptions (PTPath path) {
        ASTNode n = (ASTNode) path.get (path.size () - 2);
        n = n.getParent ("startTag");
        String tagName = n.getTokenTypeIdentifier ("html-element_name");
        List as = getLibrary ().getItems (tagName);
        List attributeDescriptions = new ArrayList (as.size ());
        Iterator it = as.iterator ();
        while (it.hasNext ()) {
            String tag = (String) it.next ();
            String description = getLibrary ().getProperty 
                (tagName, tag, "description");
            attributeDescriptions.add (
                "<html><b><font color=blue>" + tag.toUpperCase () + 
                ": </font></b><font color=black> " + 
                description + "</font></html>"
            );
        }
        System.out.println("attributeDescriptions " + attributeDescriptions);
        return attributeDescriptions;
//        Map attribs = getAttributes ();
//        if (attributeDescriptions == null) {
//            attributeDescriptions = new ArrayList (attribs.size ());
//            Iterator it = attribs.keySet ().iterator ();
//            while (it.hasNext ()) {
//                String name = (String) it.next ();
//                Map properties = (Map) attribs.get (name);
//                String description = (String) properties.get ("Comment");
//                attributeDescriptions.add (
//                    "<html><b><font color=blue>" + name.toUpperCase () + 
//                    ": </font></b><font color=#aaaaaa> " + 
//                    description + "</font></html>"
//                );
//            }
//        } 
//        return attributeDescriptions;
    }
    

    public static boolean isDeprecatedAttribute (PTPath path) {
        ASTNode n = (ASTNode) path.get (path.size () - 2);
        ASTNode nn = n.getParent ("startTag");
        String tagName = nn.getTokenTypeIdentifier ("html-element_name");
        SToken t = (SToken) path.getLeaf ();
        String attribName = n.getTokenTypeIdentifier ("html-attribute-name");
        if (attribName == null) return false;
        attribName = attribName.toLowerCase ();
        return "true".equals (getLibrary ().getProperty (tagName, attribName, "deprecated"));
//        Map tags = getAttributes ();
//        String tagName = t.getIdentifier ().toLowerCase ();
//        Map m = (Map) tags.get (tagName);
//        if (m == null) return false;
//        return "D".equals (m.get ("Depr."));
    }
    
    public static ASTNode process (PTPath path) {
        ASTNode n = (ASTNode) path.getRoot ();
        List l = new ArrayList ();
        resolve (n, new Stack (), l, true);
        return ASTNode.create (n.getMimeType (), n.getNT (), n.getRule (), n.getParent (), l, n.getOffset ());
    }
    
    
    // private methods .........................................................
    
    private static LibrarySupport library;
    
    private static LibrarySupport getLibrary () {
        if (library == null)
            library = LibrarySupport.create (HTML401);
        return library;
    }
    
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

//    private static Map tagsMap = null;
//    
//    private static Map getTags () {
//        if (tagsMap == null) {
//            ASTNode n = parseHTML ("index/elements.html");
//            if (n != null)
//                tagsMap = readTags (n);
//            else
//                tagsMap = new HashMap ();
//        }
//        return tagsMap;
//    }
//    
//    private static ASTNode parseHTML (String resourceName) {
//        long start = System.currentTimeMillis ();
//        try {
//            File f = InstalledFileLocator.getDefault().locate 
//                (HTML40DOC, null, false); //NoI18N
//            if (f == null) {
//                System.out.println("File " + HTML40DOC + " not found!");
//                return null;
//            }
//            InputStream in = null;
//            try {
//                URL url = f.toURL ();
//                url = FileUtil.getArchiveRoot (url);
//                url = new URL (url.toString () + resourceName);
//                in = url.openStream ();
//            } catch (MalformedURLException e){
//                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
//                return null;
//            }
//            InputStreamReader r = new InputStreamReader (in);
//            BufferedReader br = new BufferedReader (r);
//            StringBuilder sb = new StringBuilder ();
//            String ln = br.readLine ();
//            while (ln != null) {
//                sb.append (ln).append ('\n');
//                ln = br.readLine ();
//            }
//            Language l = LanguagesManager.getDefault ().getLanguage ("text/html2");
//            TokenInput ti = TokenInput.create (
//                l.getParser (), 
//                Input.create (sb.toString (), resourceName),
//                Collections.EMPTY_SET// l.getSkipTokenTypes ()
//            );
//            ASTNode node = l.getAnalyser ().read (ti, false);
//            List rl = new ArrayList ();
//            resolve (node, new Stack (), rl, false);
//            node = ASTNode.create (
//                node.getMimeType (), 
//                node.getNT (), 
//                node.getRule (), 
//                node.getParent (), 
//                rl, 
//                node.getOffset ()
//            );
//            in.close ();
//            System.out.println("parse " + resourceName + " " + (System.currentTimeMillis () - start));
//            return node;
//        } catch (Exception ex) {
//            System.out.println("parse-error " + resourceName + " " + (System.currentTimeMillis () - start));
//            ErrorManager.getDefault ().notify (ex);
//            return null;
//        }
//    }
//    
//    private static Map readTags (ASTNode node) {
//        Map result = new HashMap ();
//        List result2 = new ArrayList ();
//        node = getFirstTag (node, "html");
//        node = getFirstTag (node, "body");
//        node = getFirstTag (node, "table");
//        Iterator it = getTags (node, "tr").iterator ();
//        while (it.hasNext ()) {
//            ASTNode tr = (ASTNode) it.next ();
//            String name = null;
//            Map params = new HashMap ();
//            Iterator it2 = getTags (tr, "td").iterator ();
//            while (it2.hasNext ()) {
//                ASTNode td = (ASTNode) it2.next ();
//                String title = getAttributeValue (td, "title");
//                String url = getAttributeValue (
//                    td.getNode ("tag"),
//                    "href"
//                ), value = null;
//                if (url != null) {
//                    value = td.getNode ("tag").getNode ("etext").
//                        getTokenType ("html-text").getIdentifier ().toLowerCase ();
//                    params.put (title + "_URL", url);
//                } else
//                    value = td.getNode ("etext").getAsText ();
//                if (value.equals ("&nbsp;"))
//                    value = "";
//                params.put (title, value);
//                if (title.equals ("Name")) {
//                    name = value;
//                    result.put (name, params);
//                    result2.add (params);
//                }
//            }
//        }
//        try {
//            FileWriter fw = new FileWriter ("c:\\tags.xml");
//            it = result2.iterator ();
//            while (it.hasNext ()) {
//                Map params = (Map) it.next ();
//                String tagName = (String) params.get ("Name");
//                boolean deprecated = "D".equals (params.get ("Depr."));
//                String description = (String) params.get ("Description");
//                String endTag = (String) params.get ("End Tag");
//                fw.write ("\t<node key = \"" + tagName + "\"\tcontext = \"TAG\"\tdeprecated = \"" + deprecated + "\"\tendTag = \"" + endTag + "\"\tdescription = \"" + description + "\"/>\n");
//            }
//            fw.close ();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return result;
//    }
//    
//    private static Map attributesMap = null;
//    
//    private static Map getAttributes () {
//        if (attributesMap == null) {
//            ASTNode n = parseHTML ("index/attributes.html");
//            if (n != null)
//                attributesMap = readAttributes (n);
//            else
//                attributesMap = new HashMap ();
//        }
//        return attributesMap;
//    }
//    
//    private static Map readAttributes (ASTNode node) {
//        Map result = new HashMap ();
//        List result2 = new ArrayList ();
//        Set tags = getTags ().keySet();
//        node = getFirstTag (node, "html");
//        node = getFirstTag (node, "body");
//        node = getFirstTag (node, "table");
//        Iterator it = getTags (node, "tr").iterator ();
//        while (it.hasNext ()) {
//            ASTNode tr = (ASTNode) it.next ();
//            String name = null;
//            Map params = new HashMap ();
//            Iterator it2 = getTags (tr, "td").iterator ();
//            while (it2.hasNext ()) {
//                ASTNode td = (ASTNode) it2.next ();
//                String title = getAttributeValue (td, "title");
//                String url = getAttributeValue (
//                    td.getNode ("tag"),
//                    "href"
//                ), value = null;
//                if (!title.equals ("Related Elements")) {
//                    if (url != null) {
//                        value = td.getNode ("tag").getNode ("etext").
//                            getTokenType ("html-text").getIdentifier ().toLowerCase ();
//                        params.put (title + "_URL", url);
//                    } else
//                        value = td.getNode ("etext").getAsText ();
//                    if (value.equals ("&nbsp;"))
//                        value = "";
//                } else {
//                    List l = td.getChildren ();
//                    Set neg = null;
//                    Iterator ii = l.iterator ();
//                    while (ii.hasNext ()) {
//                        Object elem = ii.next ();
//                        if (!(elem instanceof ASTNode)) continue;
//                        if (!((ASTNode) elem).getNT ().equals ("tag")) continue;
//                        if (neg != null)
//                            neg.remove (((ASTNode) elem).getNode ("etext").getAsText ().toLowerCase ());
//                        else
//                        if (value == null) {
//                            value = ((ASTNode) elem).getNode ("etext").getAsText ().toLowerCase ();
//                            if (value.equals ("all elements"))
//                                neg = new HashSet (tags);
//                        } else
//                            value += "," + ((ASTNode) elem).getNode ("etext").getAsText ().toLowerCase ();
//                    }
//                    if (neg != null) {
//                        value = null;
//                        ii = neg.iterator ();
//                        while (ii.hasNext ()) {
//                            String t = (String) ii.next ();
//                            if (value == null)
//                                value = t;
//                            else
//                                value += "," + t;
//                        }
//                    }
//                }
//                params.put (title, value);
//                if (title.equals ("Name")) {
//                    name = value;
//                    result.put (name, params);
//                    result2.add (params);
//                }
//            }
//            //System.out.println(name + " : " + params);
//        }
//        
//        try {
//            FileWriter fw = new FileWriter ("c:\\attribs.xml");
//            it = result2.iterator ();
//            while (it.hasNext ()) {
//                Map params = (Map) it.next ();
//                String tagName = (String) params.get ("Name");
//                boolean deprecated = "D".equals (params.get ("Depr."));
//                String description = (String) params.get ("Comment");
//                String endTag = (String) params.get ("End Tag");
//                String context = (String) params.get ("Related Elements");
//                fw.write ("\t<node key = \"" + tagName + "\"\tcontext = \"" + context + "\"\tdeprecated = \"" + deprecated + "\"\tdescription = \"" + description + "\"/>\n");
//            }
//            fw.close ();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return result;
//    }
//    
//    private static String getAttributeValue (ASTNode node, String attributeName) {
//        if (node == null) return null;
//        List path = node.getNode ("startTag").findToken 
//            ("html-attribute-name", attributeName);
//        if (path == null) return null;
//        ASTNode attributes = (ASTNode) path.get (1);
//        List path2 = attributes.findToken ("html-attribute-value", null);
//        String value = ((SToken) path2.get (0)).getIdentifier ();
//        if (value.startsWith ("\"")) value = value.substring (1, value.length () - 1);
//        return value;
//    }
//    
//    private static List getTags (ASTNode node, String tagName) {
//        List result = new ArrayList ();
//        Iterator it = node.getChildren ().iterator ();
//        while (it.hasNext ()) {
//            Object e = it.next ();
//            if (e instanceof SToken) continue;
//            ASTNode n = (ASTNode) e;
//            if (n.getNT ().equals ("tag")) {
//                ASTNode startTag = n.getNode ("startTag");
//                String tn = startTag.getTokenTypeIdentifier ("html-element_name");
//                if (tn.equals (tagName)) result.add (n);
//            }
//        }
//        return result;
//    }
//    
//    private static ASTNode getFirstTag (ASTNode node, String tagName) {
//        Iterator it = node.getChildren ().iterator ();
//        while (it.hasNext ()) {
//            Object e = it.next ();
//            if (e instanceof SToken) continue;
//            ASTNode n = (ASTNode) e;
//            if (n.getNT ().equals ("tag")) {
//                ASTNode startTag = n.getNode ("startTag");
//                String tn = startTag.getTokenTypeIdentifier ("html-element_name");
//                if (tn.equals (tagName)) return n;
//            }
//        }
//        return null;
//    }
}

