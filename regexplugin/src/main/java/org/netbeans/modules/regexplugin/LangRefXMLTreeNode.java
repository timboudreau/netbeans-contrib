/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.regexplugin;

import javax.swing.tree.DefaultMutableTreeNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Angad
 */
public class LangRefXMLTreeNode extends DefaultMutableTreeNode {

	/**
	 * @param xmlNode
	 */
	public LangRefXMLTreeNode(Node xmlNode) {
            super(xmlNode);
            createChildren();
	}

	/**
	 * Creates visual child nodes + attributes
	 */
	protected void createChildren() {
/*		if (getXMLNode().hasAttributes()) {
			createAttributes();
		}
 */
		createChildNodes();
	}

	/**
	 * Creates children
	 */
	protected void createChildNodes() {
		Node xmlNode = getXMLNode();
		NodeList childXMLNodes = xmlNode.getChildNodes();
		for (int i = 0; i < childXMLNodes.getLength(); i++) {
                    String type = childXMLNodes.item(i).getNodeName();
                    if (type.compareTo("LangRefRoot") == 0 || type.compareTo("LangRefType") == 0 || type.compareTo("LangElement") == 0)
                    {
                         add(new LangRefXMLTreeNode(childXMLNodes.item(i)));
                    }
                    }
	}

	/**
	 * Creates attribute nodes
	 */
	protected void createAttributes() {
		Node xmlNode = getXMLNode();
		NamedNodeMap attributes = xmlNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			add(new LangRefXMLTreeNode(attributes.item(i)));
		}
	}

	public Node getXMLNode() {
		return (Node) getUserObject();
	}

    @Override
	public String toString() {
        Node xmlnode = getXMLNode();
        if (xmlnode.getNodeName().compareTo("LangRefRoot") == 0 || xmlnode.getNodeName().compareTo("LangRefType") == 0 )
        {
            if (xmlnode.hasAttributes())
            {
                if (xmlnode.getAttributes().getNamedItem("name") != null)
                    return xmlnode.getAttributes().getNamedItem("name").getTextContent();
            }
        }
        else if (xmlnode.getNodeName().compareTo("LangElement") == 0)
        {
            return xmlnode.getTextContent();
        }
        return "Unidentified Node";
    }
}