
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

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.InvalidArgumentException;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldDataEntryNature;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.ObjectArrayUtil;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;
import com.sun.tthub.gdelib.fields.Selection;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author  Hareesh Ravindran
 */
public class SelectionAttrJPanel extends javax.swing.JPanel {
    
    private FieldInfo fieldInfo;
    
    /** Creates new form SelectionAttrJPanel */
    public SelectionAttrJPanel() {
        initComponents();
    }
    
    public void setFieldInfo(FieldInfo fieldInfo) {
        if(fieldInfo == null) {
            throw new InvalidArgumentException("The fieldInfo passed " +
                    "cannot be null.");
        }
        this.fieldInfo = fieldInfo;
        fillFieldDisplayInfo(fieldInfo);        
    }
    
    /**
     * Use this function to retrieve the modified field info at any time.
     */
    public FieldInfo getFieldInfo() {
        return this.fieldInfo;
    }
    
    public void fillFieldDisplayInfo(FieldInfo fieldInfo) {
        FieldMetaData metaData = fieldInfo.getFieldMetaData();
        
        SelectionFieldDisplayInfo attr = (SelectionFieldDisplayInfo)
                            fieldInfo.getFieldDisplayInfo();
        Object[] selList = attr.getSelectionRange();
        Object[] defSelection = attr.getDefaultSelection();
        
        String selListStr = ObjectArrayUtil.getObjArrString(selList, 
                    metaData.getFieldDataType(), ',');
        this.txtSelList.setText(selListStr);        
        
        // Set the selection Mode of the combo to single selection or multiple
        // selection.
        int selMode = (attr.getFieldDataEntryNature() == 
                FieldDataEntryNature.TYPE_SINGLE_SELECT) ? 
                    ListSelectionModel.SINGLE_SELECTION : 
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        this.lstDefaultSel.setSelectionMode(selMode);
        
        // Refill the default Selection List Box.
        this.lstDefaultSel.setListData(selList);
        lstDefaultSel.clearSelection(); // clear all the existing selections.
        
        Collection coll = new ArrayList();
        for(int i = 0; i < lstDefaultSel.getModel().getSize(); ++i) {
            Object obj = lstDefaultSel.getModel().getElementAt(i);
            if(isInDefSelList(defSelection, obj)) {
                coll.add(new Integer(i));
            }
        }
        int[] arr = new int[coll.size()];
        int n = 0;
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            arr[n++] = ((Integer)it.next()).intValue();
        }
        lstDefaultSel.setSelectedIndices(arr);
        
        // set the state of the 'is selection mandatory' checkbox.
        chkIsSelMandatory.setSelected(attr.getIsRequired());
    }
    
    private boolean isInDefSelList(Object[] defSelectionArr, Object obj) {
        for(int i = 0; i < defSelectionArr.length; ++i) {
            if(defSelectionArr[i].equals(obj))
                return true;
        }
        return false;
    }   
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlSelList = new javax.swing.JPanel();
        scrlPaneSelList = new javax.swing.JScrollPane();
        txtSelList = new javax.swing.JTextArea();
        btnDefineSelList = new javax.swing.JButton();
        pnlDefaultSel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstDefaultSel = new javax.swing.JList();
        chkIsSelMandatory = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlSelList.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selection List:"));
        txtSelList.setColumns(20);
        txtSelList.setRows(5);
        txtSelList.setName("txSelList");
        txtSelList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSelListFocusLost(evt);
            }
        });

        scrlPaneSelList.setViewportView(txtSelList);

        btnDefineSelList.setText("jButton1");

        org.jdesktop.layout.GroupLayout pnlSelListLayout = new org.jdesktop.layout.GroupLayout(pnlSelList);
        pnlSelList.setLayout(pnlSelListLayout);
        pnlSelListLayout.setHorizontalGroup(
            pnlSelListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSelListLayout.createSequentialGroup()
                .addContainerGap()
                .add(scrlPaneSelList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnDefineSelList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlSelListLayout.setVerticalGroup(
            pnlSelListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelListLayout.createSequentialGroup()
                .add(pnlSelListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelListLayout.createSequentialGroup()
                        .add(37, 37, 37)
                        .add(btnDefineSelList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlSelListLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlPaneSelList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDefaultSel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Default Selection:"));
        lstDefaultSel.setName("lstDefaultSel");
        lstDefaultSel.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstDefaultSelValueChanged(evt);
            }
        });

        jScrollPane2.setViewportView(lstDefaultSel);

        org.jdesktop.layout.GroupLayout pnlDefaultSelLayout = new org.jdesktop.layout.GroupLayout(pnlDefaultSel);
        pnlDefaultSel.setLayout(pnlDefaultSelLayout);
        pnlDefaultSelLayout.setHorizontalGroup(
            pnlDefaultSelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDefaultSelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlDefaultSelLayout.setVerticalGroup(
            pnlDefaultSelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDefaultSelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkIsSelMandatory.setText("is selection mandatory ?");
        chkIsSelMandatory.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkIsSelMandatory.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkIsSelMandatory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkIsSelMandatoryItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkIsSelMandatory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(pnlSelList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlDefaultSel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDefaultSel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlSelList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkIsSelMandatory)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkIsSelMandatoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkIsSelMandatoryItemStateChanged
        SelectionFieldDisplayInfo fieldDisplayInfo = 
                (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
        fieldDisplayInfo.setIsRequired(evt.getStateChange() == ItemEvent.SELECTED);
    }//GEN-LAST:event_chkIsSelMandatoryItemStateChanged

    private void lstDefaultSelValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstDefaultSelValueChanged
        Object[] objArr = lstDefaultSel.getSelectedValues();
        int firstIdx = evt.getFirstIndex();
        int lastIdx = evt.getLastIndex();
        SelectionFieldDisplayInfo fieldDisplayInfo = 
                (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
        /*choonyin 14 Nov 2006- change to define parameters type*/
        fieldDisplayInfo.setDefaultSelection(objArr);        
    
        //fieldDisplayInfo.setDefaultSelection((Selection[])objArr);        
    }//GEN-LAST:event_lstDefaultSelValueChanged

    private void txtSelListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSelListFocusLost
        if(evt.getID() != FocusEvent.FOCUS_LOST)
            return;
        try {
            Object[] objArr = parseString();
            SelectionFieldDisplayInfo displayInfo = 
                    (SelectionFieldDisplayInfo) fieldInfo.getFieldDisplayInfo();
           
           displayInfo.setSelectionRange(objArr);
           //displayInfo.setSelectionRange((Selection[])objArr);
           
            lstDefaultSel.clearSelection();
           lstDefaultSel.setListData(objArr);
        } catch(GDEException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                                "Parse Error", JOptionPane.ERROR_MESSAGE);
            txtSelList.setSelectionStart(0);
            txtSelList.setSelectionEnd(txtSelList.getText().length());
            txtSelList.grabFocus();            
        }
        
    }//GEN-LAST:event_txtSelListFocusLost

    private Object[] parseString() throws GDEException {
        String str = txtSelList.getText();
        FieldMetaData metaData = fieldInfo.getFieldMetaData();
        // split the string with respect to commas.
        String[] values = str.split(",");
        // Trim each element in the array.
        for(int i = 0; i < values.length; ++i) {
            values[i] = values[i].trim();
        }
        return convertToObjectArray(values, metaData.getFieldDataType());
    }
    
    private Object[] convertToObjectArray(String[] strArr, String dataType) 
                    throws GDEException {
        if(SimpleDataTypes.TYPE_BOOLEAN.equals(dataType) ||
                    SimpleDataTypes.TYPE_BOOLEAN_OBJ.equals(dataType)) {
            Boolean[] boolArr = new Boolean[strArr.length];
            for(int i = 0; i < strArr.length; ++i) {
                boolArr[i] = Boolean.valueOf(strArr[i]);
            }
            return boolArr;            
        }
        
        if(SimpleDataTypes.TYPE_LONG.equals(dataType) || 
                    SimpleDataTypes.TYPE_LONG_OBJ.equals(dataType)) {
            Long[] longArr = new Long[strArr.length];
            for (int i = 0; i < strArr.length; ++i) {
                try {
                    longArr[i] = Long.valueOf(strArr[i]);
                } catch(NumberFormatException ex) {
                    throw new GDEException("Failed to convert value '" + 
                            strArr[i] + "' to java.lang.Long. Enter integer " +
                            "values separated by commas.");
                }
            }
            return longArr;            
        }
        
        if(SimpleDataTypes.TYPE_INTEGER.equals(dataType) ||
                    SimpleDataTypes.TYPE_INTEGER_OBJ.equals(dataType)) {
            Integer[] integerArr = new Integer[strArr.length];
            for (int i = 0; i < strArr.length; ++i) {
                try {
                    integerArr[i] = Integer.valueOf(strArr[i]);
                } catch(NumberFormatException ex) {
                    throw new GDEException("Failed to convert value '" + 
                            strArr[i] + "' to java.lang.Integer. Enter integer " +
                            "values separated by commas.");
                }
            }
            return integerArr;            
        }
        
        if(SimpleDataTypes.TYPE_FLOAT.equals(dataType) ||
                    SimpleDataTypes.TYPE_FLOAT_OBJ.equals(dataType)) {
            Float[] floatArr = new Float[strArr.length];
            for (int i = 0; i < strArr.length; ++i) {
                try {
                    floatArr[i] = Float.valueOf(strArr[i]);
                } catch(NumberFormatException ex) {
                    throw new GDEException("Failed to convert value '" + 
                            strArr[i] + "' to java.lang.Float. Enter numeric" +
                            " values separated by commas.");
                }
            }
            return floatArr;            
        }
        
        if(SimpleDataTypes.TYPE_DOUBLE.equals(dataType) ||
                    SimpleDataTypes.TYPE_DOUBLE_OBJ.equals(dataType)) {
            Double[] dblArr = new Double[strArr.length];
            for (int i = 0; i < strArr.length; ++i) {
                try {
                    dblArr[i] = Double.valueOf(strArr[i]);
                } catch(NumberFormatException ex) {
                    throw new GDEException("Failed to convert value '" + 
                        strArr[i] + "' to java.lang.Double. Enter numeric " +
                            "values spearated by commas.");
                }
            }
            return dblArr;            
            
        }
        
        if(SimpleDataTypes.TYPE_DATE.equals(dataType)) {
            Date[] dtArr = new Date[strArr.length];
            for (int i = 0; i < strArr.length; ++i) {
                try {                    
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    dtArr[i] = format.parse(strArr[i]);
                } catch(ParseException ex) {
                    throw new GDEException("Failed to convert value '" + 
                            strArr[i] + "' to java.util.Date. Enter all the " +
                            "dates in 'dd/mm/yyyy' format separated by commas.");
                }
            }
            return dtArr;                        
        }
        
        if(SimpleDataTypes.TYPE_STRING.equals(dataType)) {
            return strArr;
        }
        
        throw new GDEException("The data type is not supported.");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnDefineSelList;
    public javax.swing.JCheckBox chkIsSelMandatory;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JList lstDefaultSel;
    public javax.swing.JPanel pnlDefaultSel;
    public javax.swing.JPanel pnlSelList;
    public javax.swing.JScrollPane scrlPaneSelList;
    public javax.swing.JTextArea txtSelList;
    // End of variables declaration//GEN-END:variables
    
}
