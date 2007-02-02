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

package org.netbeans.modules.erd.model.component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.erd.graphics.ColumnWidget;
import org.netbeans.modules.erd.graphics.ERDScene;
import org.netbeans.modules.erd.model.ComponentDescriptor;
import org.netbeans.modules.erd.model.TypeID;

import org.openide.util.Utilities;

public class ColumnDescriptor extends ComponentDescriptor{
    
    public final static String NAME="COLUMN#COMPONENT";
    private static final Image FOREIGN_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_f.png"); // NOI18N
    private static final Image PRIMARY_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_p.png"); // NOI18N
    private static final Image PF_KEY_IMAGE = Utilities.loadImage ("org/netbeans/modules/erd/resources/key_pf.png"); // NOI18N
    
    
    public final static TypeID type=new TypeID(TypeID.TYPE.COMPONENT,NAME);
    
    public enum PROPERTY {TABLE,COLUMN_NAME,IS_PK,IS_FK,SQL_TYPE,PRECISION,IS_NULL};
    
    String id;
    String parent;
    boolean isPK;
    boolean isFK;
    /** Creates a new instance of ColumnComponent */
    public ColumnDescriptor() {
       
    
    }
    
        
    public String getType(){
        return NAME;
    }
    
   
    
    private String getPrecision(){
        String precision=getProperty(PROPERTY.PRECISION);
        if(precision.equals("")){
            return new String();
        }
        else {
            return "("+precision+")";
        }
    }
    private String createLabel() {
        String columnName=getProperty(PROPERTY.COLUMN_NAME);
        String precision=getProperty(PROPERTY.PRECISION);
        
        
        String sqlType=getProperty(PROPERTY.SQL_TYPE);
        
        String label=columnName+": "+sqlType+" "+getPrecision();
        return label;
    }
    
    public void presentComponent(ERDScene scene){
        String pinId=getId();
        String table=getProperty(PROPERTY.TABLE);
        String isFK=getProperty(PROPERTY.IS_FK);
        String isPK=getProperty(PROPERTY.IS_PK);
        
        
        
        List<Image> list=getImage(isFK,isPK);
        ((ColumnWidget) scene.addPin (table, pinId)).setProperties (createLabel(), list,columnType);
        
    }
    
    private ColumnWidget.CONSTRAINT_TYPE columnType=ColumnWidget.CONSTRAINT_TYPE.ORDINARY;
    
    private void setColumnType(ColumnWidget.CONSTRAINT_TYPE columnType){
        this.columnType=columnType;
    }
    private List<Image> getImage(String isFK,String isPK){
        List<Image> images=new ArrayList<Image>();
        boolean fk=Boolean.parseBoolean(isFK);
        boolean pk=Boolean.parseBoolean(isPK);
        if(fk && pk){
             images.add(PF_KEY_IMAGE);
             setColumnType(ColumnWidget.CONSTRAINT_TYPE.FP_KEY);
        }     
        else     
         if(fk){
            images.add(FOREIGN_KEY_IMAGE);
            setColumnType(ColumnWidget.CONSTRAINT_TYPE.FOREIGN_KEY);
         }
            else      
             if(pk){
                images.add(PRIMARY_KEY_IMAGE);
                setColumnType(ColumnWidget.CONSTRAINT_TYPE.PRIMARY_KEY);
             }
             
        return images;
        
           
    }
 
    
}
