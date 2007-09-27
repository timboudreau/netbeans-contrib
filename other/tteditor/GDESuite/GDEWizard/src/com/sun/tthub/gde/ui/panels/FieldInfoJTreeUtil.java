
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gdelib.fields.ObjectArrayUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.DataTypeNature;
import com.sun.tthub.gdelib.fields.FieldDataEntryNature;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;
import javax.swing.tree.TreeNode;


/**
 *
 * @author Hareesh Ravindran
 */
public class FieldInfoJTreeUtil {
    
    /** Creates a new instance of FieldInfoJTreeUtil */
    public FieldInfoJTreeUtil() {}
    
    public static void paintFieldInfoNode(DefaultTreeModel model, 
                                MutableTreeNode parent,  FieldInfo info) {        
        FieldMetaData metaData = info.getFieldMetaData();
        FieldDisplayInfo displayInfo = info.getFieldDisplayInfo();
        DefaultMutableTreeNode rootNode = new 
                    DefaultMutableTreeNode(metaData.getFieldName());
        model.insertNodeInto(rootNode, parent, parent.getChildCount());

        model.insertNodeInto(new DefaultMutableTreeNode(
                metaData.getFieldDataType()), rootNode, rootNode.getChildCount());

        if(info instanceof TTValueFieldInfo) {
            paintPortletAttributeNode(model, rootNode, (TTValueFieldInfo) info);
        }

        DefaultMutableTreeNode displayParamsNode = new 
                    DefaultMutableTreeNode("Display Params:");
        model.insertNodeInto(displayParamsNode, 
                    rootNode, rootNode.getChildCount());                    

        paintBasicDisplayParams(model, displayParamsNode, info);

        if(displayInfo instanceof SelectionFieldDisplayInfo) {
            paintSelectionNode(model, displayParamsNode, info);
        } else  if(displayInfo instanceof ComplexEntryFieldDisplayInfo) {
            paintComplexNode(model, displayParamsNode, info);
        }
    }
    
    private static void paintSelectionNode(DefaultTreeModel model, 
                MutableTreeNode displayParamsNode, FieldInfo info) {
        SelectionFieldDisplayInfo displayInfo = 
                        (SelectionFieldDisplayInfo) info.getFieldDisplayInfo();
        FieldMetaData metaData = info.getFieldMetaData();
        
        DefaultMutableTreeNode selRootNode = 
                new DefaultMutableTreeNode("Selection Details");        
        DefaultMutableTreeNode selTypeNode = 
                new DefaultMutableTreeNode("Sel Type: " + (
                    displayInfo.getIsMultiSelect() ? 
                        "Multi Select" : "Single Select"));
        DefaultMutableTreeNode isSelReqNode = new
                DefaultMutableTreeNode("Is Mandatory: " + (
                displayInfo.getIsRequired() ? "Yes" : "No"));
        String selStr = "Sel Range: " + ObjectArrayUtil.getObjArrString(
                displayInfo.getSelectionRange(), metaData.getFieldDataType(), ',');
        DefaultMutableTreeNode selRangeNode = 
                new DefaultMutableTreeNode(selStr);
        String defSelStr = "Def. Selection: " + ObjectArrayUtil.getObjArrString(
                displayInfo.getDefaultSelection(), metaData.getFieldDataType(), ',');        
        DefaultMutableTreeNode defSelNode = 
                new DefaultMutableTreeNode(defSelStr);
        
        
        model.insertNodeInto(selRootNode, 
                displayParamsNode, displayParamsNode.getChildCount());
        model.insertNodeInto(selTypeNode, selRootNode, 0);
        model.insertNodeInto(isSelReqNode, selRootNode, 1);        
        model.insertNodeInto(selRangeNode, selRootNode, 2);        
        model.insertNodeInto(defSelNode, selRootNode, 3);        
    }

    public static void paintComplexNode(DefaultTreeModel model, 
                MutableTreeNode nodeToAttach, FieldInfo info) {
        
        MutableTreeNode complexRootNode = 
                            new DefaultMutableTreeNode("Sub Fields");        
        if(nodeToAttach != null) {    
            model.insertNodeInto(complexRootNode, nodeToAttach, 
                        nodeToAttach.getChildCount());
        } else {
            model.setRoot(complexRootNode);
        }
        
        ComplexEntryFieldDisplayInfo fieldDisplayInfo = 
                (ComplexEntryFieldDisplayInfo) info.getFieldDisplayInfo();
        Map fieldInfoMap = fieldDisplayInfo.getFieldInfoMap();
        Collection coll = fieldInfoMap.entrySet();
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            FieldInfo innerFieldInfo = (FieldInfo) entry.getValue();
            paintFieldInfoNode(model, complexRootNode,  innerFieldInfo);            
        }                
    }
    
    public static void paintComplexNode(DefaultTreeModel model, 
                MutableTreeNode displayParamsNode, FieldInfo info, 
                boolean setAsRoot) {        
        

    }
    
    private static void paintBasicDisplayParams(DefaultTreeModel model,
                    MutableTreeNode displayParamsNode, FieldInfo info) {
        FieldMetaData metaData = info.getFieldMetaData();
        FieldDisplayInfo displayInfo = info.getFieldDisplayInfo();

        DefaultMutableTreeNode fieldDisplayNameNode = new 
                    DefaultMutableTreeNode("Display Name: " + 
                                displayInfo.getFieldDisplayName());        
        DefaultMutableTreeNode dataTypeNatureNode = new DefaultMutableTreeNode(
                "Datatype Nature: " + (metaData.getFieldDataTypeNature() == 
                DataTypeNature.NATURE_SIMPLE ? "Simple" : "Complex"));
        DefaultMutableTreeNode controlTypeNode = 
                new DefaultMutableTreeNode("UI Control Type: " + 
                                displayInfo.getUIComponentType());
        
        String str = "Data Entry Nature: ";
        if(displayInfo.getFieldDataEntryNature() == 
                FieldDataEntryNature.TYPE_ENTRY) {
            str += "Entry Type";
        } else if(displayInfo.getFieldDataEntryNature() == 
                FieldDataEntryNature.TYPE_SINGLE_SELECT) {
            str += "Single Select Type";
        } else {
            str += "Multi Select Type";
        }        
        DefaultMutableTreeNode dataEntryNatureNode = new 
                        DefaultMutableTreeNode(str);

        model.insertNodeInto(fieldDisplayNameNode, 
                    displayParamsNode, displayParamsNode.getChildCount());                                    
        model.insertNodeInto(dataTypeNatureNode, 
                    displayParamsNode, displayParamsNode.getChildCount());                            
        model.insertNodeInto(controlTypeNode, 
                    displayParamsNode, displayParamsNode.getChildCount());                            
        model.insertNodeInto(dataEntryNatureNode, 
                    displayParamsNode, displayParamsNode.getChildCount());                                            
    }
    
    private static void paintPortletAttributeNode(DefaultTreeModel model, 
                        MutableTreeNode rootNode, 
                        TTValueFieldInfo ttValueFieldInfo) {
            DefaultMutableTreeNode portletAttrNode = new 
                        DefaultMutableTreeNode("Portlet Appearance Params:");
            model.insertNodeInto(portletAttrNode, 
                        rootNode, rootNode.getChildCount());            
            DefaultMutableTreeNode isReqNode = new DefaultMutableTreeNode(
                    "Is Required in portlet? : " + 
                    (ttValueFieldInfo.getIsRequired() ? "Yes" : "No"));
            DefaultMutableTreeNode isSearchableNode = new DefaultMutableTreeNode(
                    "Is Searchable ? : " + 
                    (ttValueFieldInfo.getIsSearchable() ? "Yes" : "No"));                        
            DefaultMutableTreeNode includeInResultsNode = new DefaultMutableTreeNode(
                    "Include In Results ? : " + 
                    (ttValueFieldInfo.getIncludeInSearchResults() ? "Yes" : "No"));            
            
            model.insertNodeInto(isReqNode, portletAttrNode, 0);
            model.insertNodeInto(isSearchableNode, portletAttrNode, 1);            
            model.insertNodeInto(includeInResultsNode, portletAttrNode, 2);                    
    }        
}
