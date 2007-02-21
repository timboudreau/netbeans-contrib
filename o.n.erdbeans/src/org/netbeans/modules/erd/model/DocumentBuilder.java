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


package org.netbeans.modules.erd.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.netbeans.modules.dbschema.ColumnElement;
import org.netbeans.modules.dbschema.DBIdentifier;
import org.netbeans.modules.dbschema.ForeignKeyElement;
import org.netbeans.modules.dbschema.KeyElement;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.dbschema.SchemaElementUtil;
import org.netbeans.modules.dbschema.TableElement;
import org.netbeans.modules.dbschema.UniqueKeyElement;
import org.netbeans.modules.dbschema.util.SQLTypeUtil;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.io.ERDContext;
import org.netbeans.modules.erd.io.ERDDataObject;
import org.netbeans.modules.erd.model.component.ColumnDescriptor;
import org.netbeans.modules.erd.model.component.TableDescriptor;
import org.netbeans.modules.erd.model.component.ConnectionDescriptor;
import org.netbeans.modules.erd.model.component.ConnectionDescriptor;
import org.openide.filesystems.FileObject;


public class DocumentBuilder {
    
    
    HashMap<String,ERDComponent> components=new HashMap<String,ERDComponent>();
    MyArrayList tables=new MyArrayList();
    
    ERDDocument document;
    public DocumentBuilder(ERDDocument document,SchemaElement se) {
        this.document=document;
        
        
        TableElement[] tes=se.getTables();
        for(int i=0;i<tes.length;i++){
            TableDescriptor table=null;
            if(!tables.contains(getName(tes[i]))){
               tables.add(tes[i]);
            }   
              
           // handleFK(tes[i]);
        }
    }
    
   private String getName(TableElement te){
       return te.getName().getName();
   }
    
    
   private void handleColumns(TableElement te){
       String tableName=getName(te);
       UniqueKeyElement pkey=te.getPrimaryKey();
       ForeignKeyElement[] fkey= te.getForeignKeys();
       
      
       ColumnElement[] columns=te.getColumns();
       for(int i=0;i<columns.length;i++){
          ColumnElement ce=columns[i];
          
           String columnName=ce.getName().getName();
           String columnType=SQLTypeUtil.getSqlTypeString(ce.getType());   
           String uniqueName=createUniqueName(tableName, columnName);
           String precision=handlePrecision(ce.getLength());
           ERDComponent column=document.createComponent(uniqueName,ColumnDescriptor.NAME );
           column.writeProperty(ColumnDescriptor.PROPERTY.TABLE,tableName);
           boolean isPK=isPK(ce,pkey);
           column.writeProperty(ColumnDescriptor.PROPERTY.IS_PK,Boolean.toString(isPK));
           column.writeProperty(ColumnDescriptor.PROPERTY.COLUMN_NAME,columnName);
           column.writeProperty(ColumnDescriptor.PROPERTY.SQL_TYPE,columnType);
           column.writeProperty(ColumnDescriptor.PROPERTY.PRECISION,precision);
           column.writeProperty(ColumnDescriptor.PROPERTY.IS_NULL, Boolean.toString(ce.isNullable()));
           column.writeProperty(ColumnDescriptor.PROPERTY.IS_FK,isFK(ce, fkey,isPK));
       }
   }
    
   
   private String handlePrecision(Integer precision){
       if(precision==null)
           return new String();
       else 
          return  precision.toString();
   }
  /* private void handleFK(TableElement te){
        
        ForeignKeyElement[] fk=te.getForeignKeys();
        for(int i=0;i<fk.length;i++){
          TableElement refTableElement=  fk[i].getReferencedTable();
          
          if(!tables.contains(getName(refTableElement))){
              tables.add(refTableElement);
          }
          String uniqueName=createUniqueName(getName(te), getName(refTableElement));
          ERDComponent connection=document.createComponent(uniqueName,ConnectionDescriptor.NAME);
          connection.writeProperty(ConnectionDescriptor.PROPERTY.SOURCE, getName(te));
          connection.writeProperty(ConnectionDescriptor.PROPERTY.TARGET, getName(refTableElement));
          
          
        }
        
        
    }*/
   
   
    public String createUniqueName(String part1,String part2){
        return part1+"@"+part2+Math.random();
    }
   
    private String isFK(ColumnElement ce,ForeignKeyElement[] keys,boolean isPK){
        for(int i=0;i<keys.length;i++){
           ColumnElement column= keys[i].getColumn(ce.getName());
           if(column!=null){
              TableElement childKey=keys[i].getDeclaringTable();
              TableElement parentKey=keys[i].getReferencedTable();
             // boolean compoundParentPK= parentKey.getPrimaryKey().getColumns().length>1 ? true : false;
              String uniqueName=createUniqueName(getName(childKey), getName(parentKey));
              ERDComponent connection=document.createComponent(uniqueName,ConnectionDescriptor.NAME);
              
              connection.writeProperty(ConnectionDescriptor.PROPERTY.SOURCE, getName(childKey));
              connection.writeProperty(ConnectionDescriptor.PROPERTY.TARGET, getName(parentKey));
              if(isPK)
                connection.writeProperty(ConnectionDescriptor.PROPERTY.RELATION, ConnectionDescriptor.ONE_ONE);
              else
               if(ce.isNullable())   
                 connection.writeProperty(ConnectionDescriptor.PROPERTY.RELATION, ConnectionDescriptor.ZERO_MANY);   
               else
                 connection.writeProperty(ConnectionDescriptor.PROPERTY.RELATION, ConnectionDescriptor.ONE_MANY);     
              return new Boolean(true).toString();
           }    
            
        }
        return  new Boolean(false).toString();
    }
    
    private boolean isPK(ColumnElement ce,UniqueKeyElement keys){
        //for(int i=0;i<keys.length;i++){
         //  String name= ce.getName().getName();  
        ColumnElement column=null;
           if(keys!=null)
            column= keys.getColumn(ce.getName());
        //   if(column!=null)
         //     name=column.getName().getName();
           
           if(column!=null && keys.getColumns().length==1)
               return  true;
              
            
       // }
        return false;
    }
   
   
    
    class MyArrayList {
         ArrayList<String> list=new ArrayList<String>();
        
         public void add(TableElement tableElement) {
          
          handleColumns(tableElement);
          document.createComponent(getName(tableElement), TableDescriptor.NAME);
          list.add(getName(tableElement));
       }
        
       
       public boolean contains(String value) {   
           return list.contains(value);
       }
    }
    
}
