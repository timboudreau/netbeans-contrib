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
import javax.swing.table.TableModel;

/**
 * Stores configuration data in a list collection.
 *
 * @author S. Aubrecht
 */
public class ListConfigurationProcessor implements ConfigurationProcessor {
    
    private int currentRow = 1;
    private ConfigValueList list;
    
    /** Creates a new instance of SpreadsheetConfigurationProcessor */
    public ListConfigurationProcessor( ConfigValueList list ) {
        this.list = list;
    }

    public void processValueElement( String configPath, Object userValue, boolean isUserDefaultValue, 
            Object sharedValue, boolean isSharedDefaultValue ) {
        Object shared = convert( sharedValue );

        Object user = convert( userValue );
        
        String nodeName = configPath.substring( list.getFullPath().length()+1 );
        
        list.add( new ConfigValue( configPath, nodeName, shared, user ) );
    }

    public void processStructuralElement( String configPath, XInterface userNode, XInterface sharedNode ) {
    }
    
    public void format() {
    }

    static Object convert( Object val ) {
        if( null == val )
            return null;
        try {
            if( AnyConverter.isVoid( val ) )
                return null;
            else if( val instanceof int[] ) {
                StringBuffer buffer = new StringBuffer();
                buffer.append( '[' );
                final int [] arr = (int [])val;
                if( arr.length == 0 )
                    return null;
                for( int i=0; i<arr.length; i++ ) {
                    buffer.append( arr[i] );
                    if( i < arr.length-1 )
                        buffer.append(',');
                }
                buffer.append( ']' );
                return buffer.toString();
            }
            else if( val instanceof long[] ) {
                StringBuffer buffer = new StringBuffer();
                buffer.append( '[' );
                final long [] arr = (long [])val;
                if( arr.length == 0 )
                    return null;
                for( int i=0; i<arr.length; i++ ) {
                    buffer.append( arr[i] );
                    if( i < arr.length-1 )
                        buffer.append(',');
                }
                buffer.append( ']' );
                return buffer.toString();
            }
            else if( AnyConverter.isArray( val ) ) {
                StringBuffer buffer = new StringBuffer();
                buffer.append( '[' );
                final Object [] arr = (Object [])val;
                if( arr.length == 0 )
                    return null;
                for( int i=0; i<arr.length; i++ ) {
                    buffer.append( convert( arr[i] ) );
                    if( i < arr.length-1 )
                        buffer.append(',');
                }
                buffer.append( ']' );
                return buffer.toString();
            }
            if( val instanceof Boolean ) {
                return val;
            }
        } catch( ClassCastException ccE ) {
            ccE.printStackTrace();
            return null;
        }
        return val;
    }

}
