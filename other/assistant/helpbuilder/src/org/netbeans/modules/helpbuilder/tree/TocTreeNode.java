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
    
    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE toc\n PUBLIC \""+javax.help.TOCView.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/toc_1_0.dtd\">\n"+
    "\n<toc version=\"1.0\">\n";
    
    /**
     * XML element name
     */
    public static final String ELEMENT="tocitem";
    
    /**
     * Footer of xml document
     */
    public static final String FOOTER="</toc>";
    

    public TocTreeNode(TocTreeItem item){
        super(item);
    }

    /**
     * Exports node and its descendants to the xml file according toc.dtd.
     *
     * @param out The OutputStream
     */
    public void exportNode(OutputStreamWriter writer) throws IOException{
        TreeNode paren = getParent();        
        TocTreeItem item = (TocTreeItem)getUserObject();        
        writer.write(getOffset()+"<"+getXMLElement()+ " text=\""+item.getName()+"\" ");
        String target = item.getTarget();
        if((target != null)&&(target.length() > 0))
            writer.write("target=\""+target+"\" ");        
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
     * Returns the XML header string
     */
    public String getXMLHeader(){
        return this.HEADER;
    }
    /**
     * Returns the XML element string
     */
    public String getXMLElement(){
        return this.ELEMENT;
    }
        
    /**
     * Returns footer of XML doc
     */
    public String getFooter(){
        return FOOTER;
    }
    
    /**
     * Debugging code
     */
    private static final boolean debug = false;
    private static void debug(String msg) {
  	if (debug) {
  	    System.err.println("TocTreeNode: "+msg);
	}
    }
    
}
