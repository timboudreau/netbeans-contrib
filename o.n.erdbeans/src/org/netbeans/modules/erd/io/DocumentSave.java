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
import java.beans.PropertyDescriptor;
import org.openide.xml.XMLUtil;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import java.util.HashSet;
import java.util.Collection;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.dbschema.TableElement;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.model.ERDComponent;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.modules.erd.model.component.TableDescriptor;

public class DocumentSave {

    public static final String XML_ROOT_NODE = "erd"; // NOI18N
    public static final String DATASOURCE_NODE="datasource"; // NOI18N
    public static final String TABLE_ID = "tableId"; // NOI18N
    public static final String DATASOURCE_TYPE= "type";
    public static final String VALUE= "value";
    public static final String SCHEMA= "schema";
    public static final String CONNECTION= "connection";
    public static final String VERSION="version";
    public static final String VERSION_1="1.0";
    public static final String DEFAULT_LAYOUT="default_layout";
    
     static final String TABLE = "table"; // NOI18N
     static final String X_NODE = "x"; // NOI18N
     static final String Y_NODE = "y"; // NOI18N


    
     
     public static Node serializeDataFromDocument (ERDContext context,Document xml) {
       
        
        Node node = xml.getFirstChild ();
        Collection<ERDComponent> components = context.getDocument().getAllComponents();
        for (ERDComponent component : components) {
            if (component.getType()==TableDescriptor.NAME) {
                
                
                        String x=component.readProperty(TableDescriptor.PROPERTY.X);
                        String y=component.readProperty(TableDescriptor.PROPERTY.Y);
                        Node data = xml.createElement (TABLE);
                        setAttribute (xml, data, TABLE_ID, component.getComponentID());
                        setAttribute (xml, data, X_NODE,x);
                        setAttribute (xml, data, Y_NODE,y);
                        node.appendChild (data);
                
            }
        }

        return null;
    }
    
    
   
    
    private static Node createDataSourceChildNode(Document xml,Node childNode,ERDContext context){
        Node datasource_type=null;
        if(context.getDataSourceType()==ERDContext.DATASOURCETYPE.SCHEMA){
           datasource_type=xml.createElement(SCHEMA);
           setAttribute(xml,datasource_type,VALUE,context.getDataSourceUrl());
        
        }else{
           datasource_type=xml.createElement(CONNECTION);
           setAttribute(xml,datasource_type,VALUE,context.getDataSourceUrl());
        }
        return datasource_type;  
    }
    
    
    public static void save (ERDContext context) {
       Document xml = XMLUtil.createDocument (XML_ROOT_NODE, null, null, null);// TODO - NS, DTD
        Node xmlRootNode = xml.getFirstChild ();
        setAttribute(xml,xmlRootNode,VERSION,VERSION_1);
        Node datasource=xml.createElement(DATASOURCE_NODE);
        Node datasource_type=createDataSourceChildNode(xml,datasource,context);
        datasource.appendChild(datasource_type);
        xmlRootNode.appendChild(datasource);
        
        Node data=null;
        if(context.defaultLayout())
            serializeDataWithDefaultLayout(context,xml);
        else       
          data = serializeDataFromDocument (context, xml);
         if (data != null)
           xml.getFirstChild ().appendChild (data);
        

        try {
            writeDocument (context.getFileObject(), xml);
        } catch (IOException e) {
            ErrorManager.getDefault ().notify (e);
        }
    }

    
    
    public  static Node serializeDataWithDefaultLayout(ERDContext context,Document xml){
        Node node = xml.getFirstChild ();
        Node data = xml.createElement (DEFAULT_LAYOUT);
                        
        node.appendChild (data);
        return null;
    }
    
    private static void writeDocument (FileObject file, Document doc) throws IOException {
        OutputStream os = null;
        FileLock lock = null;
        try {
            
            lock = file.lock ();
            
            os = file.getOutputStream (lock);
            XMLUtil.write (doc, os, "UTF-8"); // NOI18N
        } finally {
            if (os != null)
                try {
                    os.close ();
                } catch (IOException e) {
                    ErrorManager.getDefault ().notify (e);
                }
            if (lock != null)
                lock.releaseLock ();
        }
    }

    private static void setAttribute (Document xml, Node node, String name, String value) {
        NamedNodeMap map = node.getAttributes ();
        Attr attribute = xml.createAttribute (name);
        attribute.setValue (value);
        map.setNamedItem (attribute);
    }

}

