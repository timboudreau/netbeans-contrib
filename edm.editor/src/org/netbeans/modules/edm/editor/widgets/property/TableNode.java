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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.edm.editor.widgets.property;

import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.impl.SourceTableImpl;

/**
 *
 * @author karthikeyan s
 */
public class TableNode extends AbstractNode {
    
    private SourceTableImpl dbTable;
    
    public TableNode(SourceTable obj) {
        super(Children.LEAF);
        dbTable = (SourceTableImpl) obj;
    }
    
    @Override
    public boolean canCopy() {
        return false;
    }
    
    @Override
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canCut() {
        return false;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
    
    /** Creates a property sheet. */
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.createPropertiesSet();
        try {
            PropertySupport.Reflection nameProp = new PropertySupport.Reflection(this.dbTable,
                    String.class, "getFullyQualifiedName", null);
            nameProp.setName("Table Name");
            set.put(nameProp);
            
            PropertySupport.Reflection aliasProp = new PropertySupport.Reflection(this.dbTable,
                    String.class, "getAliasName", "setAliasName");
            aliasProp.setName("Table Alias Name");
            set.put(aliasProp);            
            
            PropertySupport.Reflection conditionProp = new PropertySupport.Reflection(
                    this.dbTable.getParent().getConnectionDefinition(),
                    String.class, "getConnectionURL", null);
            conditionProp.setName("Connection URL");
            set.put(conditionProp);
            
            PropertySupport.Reflection distinctProp = new PropertySupport.Reflection(this.dbTable,
                    Boolean.class, "isSelectDistinct", "setSelectDistinct");
            distinctProp.setName("Is Distinct");
            set.put(distinctProp);              

            PropertySupport.Reflection extProp = new PropertySupport.Reflection(this.dbTable,
                    String.class, "getExtractionConditionText", "setExtractionConditionText");
//            extProp.setPropertyEditorClass(
//                    PropertyEditorManager.getPropertyEditor("EXTRACTION_CONDITION"));            
            extProp.setName("Filter Condition");
            set.put(extProp);                          
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        sheet.put(set);
        return sheet;
    }
}