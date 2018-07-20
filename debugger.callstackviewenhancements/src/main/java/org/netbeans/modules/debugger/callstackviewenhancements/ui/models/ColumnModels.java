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

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import java.beans.PropertyEditor;
import org.netbeans.api.debugger.Properties;
import org.netbeans.spi.viewmodel.ColumnModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ColumnModels {
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    private static class AbstractColumn extends ColumnModel {
        
        private String id;
        private String previousColumnId = null;
        private String nextColumnId = null;
        private String displayName;
        private String shortDescription;
        private Class type;
        private boolean defaultVisible;
        private PropertyEditor propertyEditor;
        
        Properties properties = Properties.getDefault().
                getProperties("debugger").getProperties("views");
        
        public AbstractColumn(String id, String displayName, String shortDescription,
                Class type) {
            this(id, displayName, shortDescription, type, true);
        }
        
        public AbstractColumn(String id, String displayName, String shortDescription,
                Class type, boolean defaultVisible) {
            this(id, null, null, displayName, shortDescription, type, defaultVisible);
        }
        
        public AbstractColumn(String id, String previousColumnId, String nextColumnId,
                String displayName, String shortDescription,
                Class type, boolean defaultVisible) {
            this(id, previousColumnId, nextColumnId, displayName, shortDescription,
                    type, defaultVisible, null);
        }
        
        public AbstractColumn(String id, String previousColumnId, String nextColumnId,
                String displayName, String shortDescription,
                Class type, boolean defaultVisible,
                PropertyEditor propertyEditor) {
            this.id = id;
            this.previousColumnId = previousColumnId;
            this.nextColumnId = nextColumnId;
            this.displayName = displayName;
            this.shortDescription = shortDescription;
            this.type = type;
            this.defaultVisible = defaultVisible;
            this.propertyEditor = propertyEditor;
        }
        
        public String getID() {
            return id;
        }
        
        public String getPreviuosColumnID() {
            return previousColumnId;
        }
        
        public String getNextColumnID() {
            return nextColumnId;
        }
        
        public String getDisplayName() {
            return NbBundle.getBundle(ColumnModels.class).getString(displayName);
        }
        
        public String getShortDescription() {
            return NbBundle.getBundle(ColumnModels.class).getString(shortDescription);
        }
        
        public Class getType() {
            return type;
        }
        
        /**
         * Set true if column is visible.
         *
         * @param visible set true if column is visible
         */
        public void setVisible(boolean visible) {
            properties.setBoolean(getID() + ".visible", visible);
        }
        
        /**
         * Set true if column should be sorted by default.
         *
         * @param sorted set true if column should be sorted by default
         */
        public void setSorted(boolean sorted) {
            properties.setBoolean(getID() + ".sorted", sorted);
        }
        
        /**
         * Set true if column should be sorted by default in descending order.
         *
         * @param sortedDescending set true if column should be
         *        sorted by default in descending order
         */
        public void setSortedDescending(boolean sortedDescending) {
            properties.setBoolean(
                    getID() + ".sortedDescending",
                    sortedDescending
                    );
        }
        
        /**
         * Should return current order number of this column.
         *
         * @return current order number of this column
         */
        public int getCurrentOrderNumber() {
            return properties.getInt(getID() + ".currentOrderNumber", -1);
        }
        
        /**
         * Is called when current order number of this column is changed.
         *
         * @param newOrderNumber new order number
         */
        public void setCurrentOrderNumber(int newOrderNumber) {
            properties.setInt(
                    getID() + ".currentOrderNumber",
                    newOrderNumber
                    );
        }
        
        /**
         * Return column width of this column.
         *
         * @return column width of this column
         */
        public int getColumnWidth() {
            return properties.getInt(getID() + ".columnWidth", 150);
        }
        
        /**
         * Is called when column width of this column is changed.
         *
         * @param newColumnWidth a new column width
         */
        public void setColumnWidth(int newColumnWidth) {
            properties.setInt(getID() + ".columnWidth", newColumnWidth);
        }
        
        /**
         * True if column should be visible by default.
         *
         * @return true if column should be visible by default
         */
        public boolean isVisible() {
            return properties.getBoolean(getID() + ".visible", defaultVisible);
        }
        
        /**
         * True if column should be sorted by default.
         *
         * @return true if column should be sorted by default
         */
        public boolean isSorted() {
            return properties.getBoolean(getID() + ".sorted", false);
        }
        
        /**
         * True if column should be sorted by default in descending order.
         *
         * @return true if column should be sorted by default in descending
         * order
         */
        public boolean isSortedDescending() {
            return properties.getBoolean(
                    getID() + ".sortedDescending",
                    false
                    );
        }
        
        /**
         * Returns {@link java.beans.PropertyEditor} to be used for
         * this column. Default implementation returns <code>null</code> -
         * means use default PropertyEditor.
         *
         * @return {@link java.beans.PropertyEditor} to be used for
         *         this column
         */
        public PropertyEditor getPropertyEditor() {
            return propertyEditor;
        }
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createCallStackModifiersColumn() {
        return new AbstractColumn(Constants.CALL_STACK_FRAME_MODIFIERS_COLUMN_ID,
                null,
                null,
                "CTL_CallStackView_Column_Modifiers_Name", // NOI18N
                "CTL_CallStackView_Column_Modifiers_Desc", // NOI18N
                String.class,
                true);
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createCallStackMethodSignatureColumn() {
        return new AbstractColumn(Constants.CALL_STACK_FRAME_METHOD_SIGNATURE_COLUMN_ID,
                null,
                null,
                "CTL_CallStackView_Column_Method_Signature_Name", // NOI18N
                "CTL_CallStackView_Column_Method_Signature_Desc", // NOI18N
                String.class,
                true);
    }
    
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createCallStackDeclaringClassColumn() {
        return new AbstractColumn(Constants.CALL_STACK_FRAME_DECLARING_CLASS_COLUMN_ID,
                null,
                null,
                "CTL_CallStackView_Column_Declaring_Class_Name", // NOI18N
                "CTL_CallStackView_Column_Declaring_Class_Desc", // NOI18N
                String.class,
                true);
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createCallStackThisClassColumn() {
        return new AbstractColumn(Constants.CALL_STACK_FRAME_THIS_CLASS_COLUMN_ID,
                null,
                null,
                "CTL_CallStackView_Column_This_Class_Name", // NOI18N
                "CTL_CallStackView_Column_This_Class_Desc", // NOI18N
                String.class,
                true);
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createCallStackLocationPathColumn() {
        return new AbstractColumn(Constants.CALL_STACK_FRAME_LOCATION_PATH_COLUMN_ID,
                null,
                null,
                "CTL_CallStackView_Column_Location_Path_Name",
                "CTL_CallStackView_Column_Location_Path_Desc",
                String.class,
                true);
    }
}
