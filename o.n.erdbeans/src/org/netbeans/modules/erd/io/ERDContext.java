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
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.modules.erd.wizard.WizardContext;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;


public class ERDContext {
    
    public enum DATASOURCETYPE {CONNECTION,SCHEMA};
    private DATASOURCETYPE datasourcetype;
    private ERDDataObject dataObject;
    private ERDDocument document;
    private String dataSourceUrl;
    private boolean isDBSchema;
    private SchemaElement schemaElement;
    private FileObject erdFileObject;
    private boolean defaultLayout;
    /** Creates a new instance of ERDContext */
    public ERDContext(ERDDataObject dataObject,ERDDocument document) {
      this.dataObject=dataObject;   
      this.document=document;
    }
    
        
    
    public ERDContext(FileObject fileObject,String dataSource,DATASOURCETYPE datasourcetype){
       
        this.erdFileObject=fileObject;
        this.dataSourceUrl=dataSource;
        this.defaultLayout=true;
        this.datasourcetype=datasourcetype;
        
    }
    
    
    
    public FileObject getFileObject(){
        if(dataObject==null)
            return erdFileObject;
        else
           return dataObject.getPrimaryFile();
    }
    
    public boolean defaultLayout(){
        return defaultLayout;
    }
    
    public SchemaElement getSchemaElement(){
        return schemaElement;
    }
    
    public void setSchemaElement(SchemaElement schemaElement){
        this.schemaElement=schemaElement;
    }
    
    
    public DATASOURCETYPE getDataSourceType(){
        return datasourcetype;
    }
    
    public String getDataSourceUrl(){
        return dataSourceUrl;
    }
    
    
    
    public ERDDocument getDocument(){
        return document;
    }
    
    public DataObject getDataObject(){
        return dataObject;
    }
    
    public void setDataSource(String dataSource,DATASOURCETYPE datasourcetype){
        this.datasourcetype=datasourcetype;
        this.dataSourceUrl=dataSource;
    }
    
   
    
    public boolean isDBSchema(){
        return isDBSchema;
    }
    
   
}
