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
import java.util.Iterator;
import java.util.List;

import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;


/**
 *
 * @author Jan Jancura
 */
public class XMLRoot {
    
    private ASTNode root;
    /** Creates a new instance of XMLRoot */
    XMLRoot (ASTNode root) {
        this.root = root;
    }
    
    private List<XMLItem> items;
    public List<XMLItem> getItems () {
        if (items == null) {
            items = new ArrayList<XMLItem> ();
            Iterator<ASTItem> it = root.getChildren ().iterator ();
            while (it.hasNext ()) {
                ASTItem item = it.next ();
                if (item instanceof ASTToken) continue;
                ASTNode node = (ASTNode) item;
                if (node.getNT ().equals("etext"))
                    items.add (new XMLText (node.getAsText ()));
                else
                    items.add (new XMLTag ((ASTNode) item));
            }
        }
        return items;
    }
}




