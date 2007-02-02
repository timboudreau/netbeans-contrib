/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

