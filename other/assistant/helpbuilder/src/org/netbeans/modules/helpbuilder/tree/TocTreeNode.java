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

package org.netbeans.modules.helpbuilder.tree;

import javax.swing.tree.*;
import java.beans.*;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/** 
 * @author Richard Gregor
 * @version   1.1
 */

public class TocTreeNode extends HelpTreeNode{

    public TocTreeNode(TocTreeItem item){
        super(item);
    }

    /**
     * Exports node and its descendants to the xml file according favorites.dtd.
     *
     * @param out The OutputStream
     */
    public void exportNode(OutputStreamWriter writer) throws IOException{
        TreeNode paren = getParent();        
        HelpTreeItem item = (HelpTreeItem)getUserObject();        
        writer.write(getOffset()+"<"+getXMLElement()+ " text=\""+item.getName()+"\" ");
    /*    String target = item.getTarget();
        if(target != null)
            writer.write("target=\""+target+"\" ");
        String url = item.getURLSpec();
        if(url != null)
            writer.write("url=\""+url+"\"");
        String hstitle = item.getHelpSetTitle();
        if(hstitle != null)
            writer.write(" hstitle=\""+hstitle+"\"");*/
        Enumeration chldn = children(); 
        if(chldn.equals(DefaultMutableTreeNode.EMPTY_ENUMERATION))
            writer.write("/>\n");
        else{ 
            writer.write(">\n");
            Enumeration offspring = children.elements();
            while(offspring.hasMoreElements()){
                HelpTreeNode off = (HelpTreeNode)offspring.nextElement();
                debug("offspring: "+off);
                off.exportNode(writer);
            }
            writer.write(getOffset()+"</"+ELEMENT+">\n");
        }        
     
    }   
 
        
    /**
     * Debugging code
     */
    private static final boolean debug = false;
    private static void debug(String msg) {
  	if (debug) {
  	    System.err.println("HelpTreeNode: "+msg);
	}
    }
    
}
