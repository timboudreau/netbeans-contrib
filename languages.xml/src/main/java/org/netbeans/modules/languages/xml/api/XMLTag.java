/*
 * XMLRoot.java
 *
 * Created on March 31, 2007, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.xml.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;



/**
 *
 * @author Jan Jancura
 */
public class XMLTag extends XMLItem {
    
    private ASTNode node;
    
    XMLTag (ASTNode node) {
        this.node = node;
    }
    
    private String name;
    
    public String getName () {
        if (name == null) {
            ASTToken t = null;
            if (node.getNT ().equals ("tag"))
                t = node.getNode ("startTag").getTokenType ("element_name");
            else
                t = node.getTokenType ("element_name");
            if (t != null)
                name = t.getIdentifier ();
            else
                name = "";
        }
        return name;
    }
    
    private List<XMLAttribute> attributes;
    private Map<String, String> nameToValue;
    
    public List<XMLAttribute> getAttributes () {
        if (attributes == null) {
            attributes = new ArrayList<XMLAttribute> ();
            nameToValue = new HashMap<String, String> ();
            if (node.getNT ().equals ("tag"))
                findAttributes ((ASTNode) node.getNode ("startTag.attributes"));
            else
                findAttributes ((ASTNode) node.getNode ("attributes"));
        }
        return Collections.<XMLAttribute>unmodifiableList (attributes);
    }
    
    private void findAttributes (ASTNode n) {
        if (n == null) return;
        Iterator<ASTItem> it = n.getChildren ().iterator ();
        while(it.hasNext()) {
            ASTItem item =  it.next();
            if (item instanceof ASTToken) continue;
            ASTNode node = (ASTNode) item;
            if (!node.getNT ().equals ("attribute"))
                findAttributes (node);
            else {
                String name = node.getTokenTypeIdentifier ("attribute_name");
                String value = node.getTokenTypeIdentifier ("attribute_value");
                XMLAttribute attribute = new XMLAttribute (name, value);
                attributes.add (attribute);
                if (name != null)
                    nameToValue.put (name, attribute.getValue ());
            }
        }
    }
    
    public String getAttributeValue (String attributeName) {
        getAttributes ();
        return nameToValue.get (attributeName);
    }
    
    private List<XMLItem> items;
    private Map<String, List<XMLTag>> nameToTags;
    
    public List<XMLItem> getItems () {
        if (items == null) {
            items = new ArrayList<XMLItem> ();
            nameToTags = new HashMap<String, List<XMLTag>> ();
            Iterator<ASTItem> it = node.getChildren ().iterator ();
            while (it.hasNext ()) {
                ASTItem item = it.next ();
                if (item instanceof ASTToken) continue;
                ASTNode node = (ASTNode) item;
                if (node.getNT ().equals("startTag"))
                    continue;
                if (node.getNT ().equals("endTag"))
                    continue;
                if (node.getNT ().equals("etext"))
                    items.add (new XMLText (node.getAsText ()));
                else
                if (node.getNT ().equals ("tag") ||
                    node.getNT ().equals ("unpairedStartTag")
                ) {
                    XMLTag tag = new XMLTag ((ASTNode) item);
                    items.add (tag);
                    List<XMLTag> tags = nameToTags.get (tag.getName ());
                    if (tags == null) {
                        tags = new ArrayList<XMLTag> ();
                        nameToTags.put (tag.getName (), tags);
                    }
                    tags.add (tag);
                } else
                    System.out.println("!!! " + node.getNT ());
            }
        }
        return Collections.<XMLItem>unmodifiableList (items);
    }
    
    public List<XMLTag> getTags (String name) {
        getItems ();
        if (!nameToTags.containsKey (name)) 
            return Collections.<XMLTag>emptyList ();
        return Collections.<XMLTag>unmodifiableList (nameToTags.get (name));
    }
    
    private XMLTag startTag;
    
    public XMLTag getStartTag () {
        if (startTag == null) {
            startTag = new XMLTag (node.getNode("startTag"));
        }
        return startTag;
    }
    
    private XMLTag endTag;
    
    public XMLTag getEndTag () {
        if (endTag == null) {
            endTag = new XMLTag (node.getNode("endTag"));
        }
        return endTag;
    }
    
    public int getOffset () {
        return node.getOffset ();
    }
    
    public int getEndOffset () {
        return node.getEndOffset ();
    }
}

