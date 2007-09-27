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

package org.netbeans.modules.erd.io;

import com.sun.net.ssl.internal.ssl.Debug;
import java.awt.Point;
import org.netbeans.modules.dbschema.SchemaElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.dbschema.SchemaElementUtil;

import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.io.ERDContext;
import org.netbeans.modules.erd.model.DocumentBuilder;
import org.netbeans.modules.erd.model.ERDComponent;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.modules.erd.model.component.TableDescriptor;
import org.netbeans.modules.erd.wizard.CaptureERD;

/**
 * @author David Kaspar
 */
// TODO - check for design and document version
public class DocumentLoad {


    
    
    
    public static void load (final ERDContext context) {
       
        // todo performance   
        final Node rootNode = getRootNode (context.getDataObject().getPrimaryFile());
        
        
        for (Node node : getChildNode (rootNode)) {
           if(node.getNodeName().equals(DocumentSave.DATASOURCE_NODE)){
                   Node datasource_type=getChildByName(node, DocumentSave.SCHEMA);
               
                   if(datasource_type!=null)
                     context.setDataSource(getAttributeValue (datasource_type, DocumentSave.VALUE),ERDContext.DATASOURCETYPE.SCHEMA);
                   else
                     context.setDataSource(getAttributeValue (getChildByName(node, DocumentSave.CONNECTION), DocumentSave.VALUE),ERDContext.DATASOURCETYPE.CONNECTION);
               
           }   
        }
        final SchemaElement schemaElement=getSchemaElement(context);
        
        
        if(schemaElement==null)
            return ;
        
        context.getDocument().getTransactionManager ().writeAccess (new Runnable() {
            public void run () {
                loadDocumentCore (context,schemaElement,rootNode);
            }
        });
        
        
    }

    
    private static Node getChildByName(Node parent,String childName) {
              Node[] nodes=getChildNode(parent);
               for(Node node : nodes){
                   if(childName.equals(node.getNodeName()))
                       return node;
                          
               }
               return null;
    }
    
    private static void loadDocumentCore(ERDContext context,SchemaElement schemaElement,Node rootNode){
        ERDDocument document=context.getDocument(); 
        new DocumentBuilder(document,schemaElement);
        
        deserializeData (rootNode,document);
        document.markAllComponentsAsAffected();
        document.getDocumentInterface().discardAllEdits();
        
    }
    
    
   
    
    private static SchemaElement getSchemaElement(ERDContext context){
        SchemaElement se=null;
        if(context.getDataSourceType()==ERDContext.DATASOURCETYPE.SCHEMA){
         Project project=FileOwnerQuery.getOwner(context.getDataObject().getPrimaryFile());
         FileObject projectDir=project.getProjectDirectory();
         FileObject dbschema=projectDir.getFileObject(context.getDataSourceUrl());
         se=SchemaElementUtil.forName(dbschema);
        }
        else {
          
            CaptureERD capture=new CaptureERD(context);
            capture.createSchemaFromConnection();
            se=context.getSchemaElement();
        }
        return se;
    }
    
    
    private static void deserializeData (Node data, ERDDocument document) {
        for (Node node : getChildNode (data)) {
            if(DocumentSave.DEFAULT_LAYOUT.equals (node.getNodeName ())){
                document.setIsDefaultLayout(true);
                break;
                
            }    
            if (DocumentSave.TABLE.equals (node.getNodeName ())) {
                String tableId = getAttributeValue (node, DocumentSave.TABLE_ID);
                String x = new Integer(getAttributeValue (node, DocumentSave.X_NODE)).toString();
                String y = new Integer(getAttributeValue (node, DocumentSave.Y_NODE)).toString();
                ERDComponent  component=document.getComponentByID(tableId);
                component.writeProperty(TableDescriptor.PROPERTY.X, x);
                component.writeProperty(TableDescriptor.PROPERTY.Y, y);
     
            }
        }
    }
    
   
   

    private static Node[] getChildNode (Node node) {
        NodeList childNodes = node.getChildNodes ();
        Node[] nodes = new Node[childNodes != null ? childNodes.getLength () : 0];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = childNodes.item (i);
        return nodes;
    }
    
    private static Node getRootNode (FileObject fileObject) {
        Document document = null;
        if (fileObject != null) {
            FileLock lock = null;
            try {
                lock = fileObject.lock ();
                document = getXMLDocument (fileObject.getInputStream ());
            } catch (IOException e) {
                
            } finally {
                if (lock != null)
                    lock.releaseLock ();
            }
        }
        return document != null ? document.getFirstChild () : null;
    }

    private static Document getXMLDocument (InputStream is) throws IOException {
        Document doc = null;
        try {
            doc = XMLUtil.parse (new InputSource (is), false, false, new ErrorHandler () {
                public void error (SAXParseException e) throws SAXException {
                    throw new SAXException (e);
                }

                public void fatalError (SAXParseException e) throws SAXException {
                    throw new SAXException (e);
                }

                public void warning (SAXParseException e) {
                  
                }
            }, null);
        } catch (SAXException e) {
           
        } finally {
            try {
                is.close ();
            } catch (IOException e) {
            }
        }
        return doc;
    }

    private static String getAttributeValue (Node node, String attr) {
        try {
            if (node != null) {
                NamedNodeMap map = node.getAttributes ();
                if (map != null) {
                    node = map.getNamedItem (attr);
                    if (node != null)
                        return node.getNodeValue ();
                }
            }
        } catch (DOMException e) {
           
        }
        return null;
    }

    

}
