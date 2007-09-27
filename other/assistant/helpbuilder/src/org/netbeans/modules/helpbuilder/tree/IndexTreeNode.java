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
 * @author Richard Gregor
 * @version   1.1
 */

public class IndexTreeNode extends HelpTreeNode{

    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE toc\n PUBLIC \""+javax.help.IndexView.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/index_1_0.dtd\">\n"+
    "\n<index version=\"1.0\">\n";
    
    /**
     * XML element name
     */
    public static final String ELEMENT="indexitem";
    
    /**
     * Footer of xml document
     */
    public static final String FOOTER="</index>";

    public IndexTreeNode(IndexTreeItem item){
        super(item);
    }

    /**
     * Exports node and its descendants to the xml file according favorites.dtd.
     *
     * @param out The OutputStream
     */
    public void exportNode(OutputStreamWriter writer) throws IOException{
        TreeNode paren = getParent();        
        IndexTreeItem item = (IndexTreeItem)getUserObject();        
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
  	    System.err.println("IndexTreeNode: "+msg);
	}
    }
    
}
