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
 * A class for Favorites node. This class forces none-folders to have children.
 *
 * @author Richard Gregor
 * @version   1.1
 */

public abstract class HelpTreeNode extends DefaultMutableTreeNode{
    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE favorites\n PUBLIC \""+//FavoritesView.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/favorites_1_0.dtd\">\n"+
    "\n<favorites version=\"1.0\">\n";
    
    /**
     * XML element name
     */
    public static final String ELEMENT="helpitem";
    /**
     * Footer of xml document
     */
    public static final String FOOTER="</help>";
    /**
     * HelpTreeItem userObject of this node
     */
    private HelpTreeItem item;   
    /**
     * Creates a HelpTreeNode for HelpTreeItem.
     *
     * @param item The HelpTreeItem
     */
    public HelpTreeNode(HelpTreeItem item){
        super(item);
        this.item = item;
    }    
    
    /**
     * Adds the child node.
     *
     * @param child The DefaultMutableTreeNode with HelpTreeItem as UserObject.
     */
    public void add(DefaultMutableTreeNode child) {
        super.add(child);
        HelpTreeItem childItem = (HelpTreeItem) child.getUserObject();
        HelpTreeItem oldParent = childItem.getParent();
        HelpTreeItem newParent = (HelpTreeItem) getUserObject();
        newParent.add(childItem);        
    }
    
    /**
     * Removes the child node.
     *
     * @param child Node to remove.
     */
    public void remove(DefaultMutableTreeNode child) {
        super.remove(child);
        HelpTreeItem childItem = (HelpTreeItem) ((HelpTreeNode) child).getUserObject();
        HelpTreeItem ParentItem = (HelpTreeItem) getUserObject();
        if (parent != null)
            ParentItem.remove(childItem);
    }

    /**
     * Returns the string representation of offset.
     */
    public String getOffset(){
        String parentOffset = null;
        String offset = null;
        
        HelpTreeNode parent = (HelpTreeNode)getParent();
        if(parent != null){
            parentOffset = parent.getOffset();
            offset = parentOffset + "  ";
        }else
            offset = "  ";
        
        return offset;
    }
    /**
     * Exports nodes descendants to the OutputStream
     *
     * @param out The OutputStream
     */
    public void export(OutputStream out) throws IOException{        
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer = exportHeader(out);
        //exportNode(writer);
        Enumeration chldn = children(); 
        if(!(chldn.equals(DefaultMutableTreeNode.EMPTY_ENUMERATION))){
            while(chldn.hasMoreElements()){
                HelpTreeNode node = (HelpTreeNode)chldn.nextElement();
                node.exportNode(writer);
            }
        }
        writer.write(getFooter());
        //out.close();
        writer.close();
    }

    /**
     * Exports node and its descendants to the xml file according favorites.dtd.
     *
     * @param out The OutputStream
     */
    public abstract void exportNode(OutputStreamWriter writer) throws IOException;
   
    /*{
        TreeNode paren = getParent();        
        HelpTreeItem item = (HelpTreeItem)getUserObject();        
        writer.write(getOffset()+"<"+getXMLElement()+ " text=\""+item.getName()+"\" ");
        String target = item.getTarget();
        if(target != null)
            writer.write("target=\""+target+"\" ");
        String url = item.getURLSpec();
        if(url != null)
            writer.write("url=\""+url+"\"");
        String hstitle = item.getHelpSetTitle();
        if(hstitle != null)
            writer.write(" hstitle=\""+hstitle+"\"");
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
        
     
    }*/
    
    /**
     * Exports header defined for this type of node to the OutputStream.
     *
     * @param out The OutputStream.
     */
    public OutputStreamWriter exportHeader(OutputStream out) throws IOException{
        //OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8")
        
        OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8");
        writer.write(getXMLHeader());
        return writer;
    }
    
    /**
     * Returns the XML header string
     */
    public String getXMLHeader(){
        return HEADER;
    }
    /**
     * Returns the XML element string
     */
    public String getXMLElement(){
        return ELEMENT;
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
  	    System.err.println("HelpTreeNode: "+msg);
	}
    }
    
}
