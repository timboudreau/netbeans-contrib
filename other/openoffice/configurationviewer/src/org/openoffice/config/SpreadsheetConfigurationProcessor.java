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

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.table.XCell;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;

/**
 * Shows configuration data in a spreadsheet.
 *
 * @author S. Aubrecht
 */
public class SpreadsheetConfigurationProcessor implements ConfigurationProcessor {
    
    private XSpreadsheetDocument doc;
    private String rootName;
    private XSpreadsheet sheet;
    private int currentRow = 1;
    private boolean hasValues = false;
    
    /** Creates a new instance of SpreadsheetConfigurationProcessor */
    public SpreadsheetConfigurationProcessor( XSpreadsheetDocument doc, String rootName ) {
        this.doc = doc;
        this.rootName = rootName;
    }

    public void processValueElement( String configPath, Object userValue, boolean isUserDefaultValue, 
            Object sharedValue, boolean isSharedDefaultValue ) {
        hasValues = true;
        if( null == sheet ) {
            try {
                sheet = createSpreadsheet( doc, rootName, (short)0 );
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        setCellValue( 0, currentRow, configPath );
        
        String strSharedValue = convertToString( sharedValue );
        setCellValue( 1, currentRow, strSharedValue );

        String strUserValue = convertToString( userValue );
        if( strUserValue.compareTo( strSharedValue ) != 0 ) {
            setCellValue( 2, currentRow, strUserValue );
            makeCellBold( 2, currentRow );
        }
        
        currentRow++;
    }

    public void processStructuralElement( String configPath, XInterface userNode, XInterface sharedNode ) {
    }
    
    public void format() {
        if( !hasValues ) {
            return;
        }
        setCellValue( 0, 0, "Configuration Path" );
        setCellValue( 1, 0, "Shared Value" );
        setCellValue( 2, 0, "User Value (if different)" );
        makeCellBold( 0, 0 );
        makeCellBold( 1, 0 );
        makeCellBold( 2, 0 );
        
        com.sun.star.table.XColumnRowRange xCRRange = (com.sun.star.table.XColumnRowRange)
            UnoRuntime.queryInterface( com.sun.star.table.XColumnRowRange.class, sheet );
        com.sun.star.table.XTableColumns xColumns = xCRRange.getColumns();

        try {
            Object aColumnObj = xColumns.getByIndex( 0 );
            XPropertySet xPropSet = (com.sun.star.beans.XPropertySet)
                UnoRuntime.queryInterface( com.sun.star.beans.XPropertySet.class, aColumnObj );
            xPropSet.setPropertyValue( "OptimalWidth", Boolean.TRUE );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    

    protected void setCellValue( int col, int row, String value ) {
        try {
            XCell cell = sheet.getCellByPosition( col, row );
            cell.setFormula( value );
        } catch (com.sun.star.lang.IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }
    
    protected void makeCellBold( int col, int row ) {
        try {
            XCell cell = sheet.getCellByPosition( col, row );
            XPropertySet cellProps = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, cell);
            try {
                cellProps.setPropertyValue( "CharWeight", new Float( 150 ) );
            } catch (UnknownPropertyException ex) {
                ex.printStackTrace();
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (WrappedTargetException ex) {
                ex.printStackTrace();
            }
        } catch (com.sun.star.lang.IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }
    
    protected String convertToString( Object val ) {
        if( null == val )
            return "";
        try {
            if( AnyConverter.isVoid( val ) )
                return "<null>";
            if( val instanceof int[] ) {
                StringBuffer buffer = new StringBuffer();
                buffer.append( '[' );
                final int [] arr = (int [])val;
                for( int i=0; i<arr.length; i++ ) {
                    buffer.append( arr[i] );
                    if( i < arr.length-1 )
                        buffer.append(',');
                }
                buffer.append( ']' );
                return buffer.toString();
            }
            if( AnyConverter.isArray( val ) ) {
                StringBuffer buffer = new StringBuffer();
                buffer.append( '[' );
                final Object [] arr = (Object [])val;
                for( int i=0; i<arr.length; i++ ) {
                    buffer.append( convertToString( arr[i] ) );
                    if( i < arr.length-1 )
                        buffer.append(',');
                }
                buffer.append( ']' );
                return buffer.toString();
            }
            if( val instanceof Boolean ) {
                return ((Boolean)val).booleanValue() ? "<true>" : "<false>";
            }
        } catch( ClassCastException ccE ) {
            ccE.printStackTrace();
        }
        return val.toString();
    }

    /** Inserts a new empty spreadsheet with the specified name.
     * @param aName  The name of the new sheet.
     * @param nIndex  The insertion index.
     * @return  The XSpreadsheet interface of the new sheet. */
    protected XSpreadsheet createSpreadsheet( XSpreadsheetDocument doc, String aName, short nIndex ) throws Exception {
        // Collection of sheets
        XSpreadsheets xSheets = doc.getSheets();
        XSpreadsheet xSheet = null;
        xSheets.insertNewByName( aName, nIndex );
        return (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class, xSheets.getByName( aName ));
    }
}
