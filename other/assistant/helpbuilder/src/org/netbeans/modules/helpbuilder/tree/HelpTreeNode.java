/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
