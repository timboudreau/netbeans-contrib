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

package org.openoffice.config;

import com.sun.star.beans.Property;
import com.sun.star.beans.XHierarchicalPropertySet;
import com.sun.star.beans.XHierarchicalPropertySetInfo;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XHierarchicalNameAccess;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;
import com.sun.star.util.XChangesBatch;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * 
 *
 * @author S. Aubrecht
 */
public class ConfigTableModel extends AbstractTableModel {
    
    private ArrayList<ConfigValue> values = new ArrayList<ConfigValue>();
    private ConfigManager manager;
    
    public ConfigTableModel( ConfigManager manager ) {
        this.manager = manager;
    }
    
    public int getRowCount() {
        return values.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if( rowIndex < 0 || rowIndex >= values.size() )
            return null;
        ConfigValue val = values.get( rowIndex );
        switch( columnIndex ) {
            case 0:
                return val.getDisplayName();
            case 1: 
                return val.getSharedValue();
            case 2: {
                Object shared = val.getSharedValue();
                Object user = val.getUserValue();
                if( null != shared && null != user && shared.toString().equals( user.toString() ) )
                    return null;
                return val.getUserValue();
            }
        }
        return null;
    }
    
//    void add( String configPath, Object sharedValue, Object userValue ) {
//        ConfigValue val = new ConfigValue( configPath, sharedValue, userValue );
//        values.add( val );
//    }
//    
    void add( ConfigValue val ) {
        values.add( val );
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if( rowIndex < 0 || rowIndex >= values.size() )
            return;
        ConfigValue cv = values.get( rowIndex );
        manager.updateValue( cv, aValue );
    }

    public String getColumnName(int column) {
        switch( column ) {
            case 0:
                return "Configuration Path";
            case 1:
                return "Shared Value";
            case 2:
                return "User Value";
        }
        return super.getColumnName(column);
    }
    
    List<? extends ConfigValue> getValueList() {
        return new ArrayList<ConfigValue>( values );
    }
}
