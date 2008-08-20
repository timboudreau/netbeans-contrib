/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

import com.sun.star.beans.XHierarchicalPropertySet;
import com.sun.star.container.XHierarchicalNameAccess;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;
import com.sun.star.util.XChangesBatch;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Converts OpenOffice configuration hierarchy into Swing's tree model. It can
 * also modify configuration data.
 * 
 * @author S. Aubrecht
 */
class ConfigManager {
    
    private ConfigurationAccess configAccess;
    private List<String> roots;
    
    /**
     * C'tor
     * @param roots Configuration roots which it is possible to read config values from.
     * @param configAccess 
     */
    public ConfigManager( List<String> roots, ConfigurationAccess configAccess ) {
        this.roots = roots;
        this.configAccess = configAccess;
    }
    
    /**
     * @return Tree root node for the whole available configuration hierarchy.
     */
    public TreeNode getConfigRootNode() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( "/org.openoffice" );
        addChildren( root );
        return root;
    }
    
    private void addChildren( DefaultMutableTreeNode parent ) {
        String parentPath = getPathFromRoot( parent )+'.';
        for( String cfgRoot : roots ) {
            if( cfgRoot.startsWith( parentPath ) ) {
                String childPath = cfgRoot.substring( parentPath.length() );
                if( childPath.indexOf( '.' ) > 0 ) {
                    childPath = childPath.substring( 0, childPath.indexOf('.' ) );
                }
                if( !childAlreadyExists( childPath, parent ) ) {
                    DefaultMutableTreeNode child = new DefaultMutableTreeNode( childPath );
                    parent.add( child );
                    addChildren( child );
                    if( child.isLeaf() ) {
                        ConfigValueList list = new ConfigValueList( parentPath+childPath, childPath );
                        child.setUserObject( list );
                    }
                }
            }
        }
    }
    
    private static String getPathFromRoot( DefaultMutableTreeNode node ) {
        Object[] path = node.getUserObjectPath();
        StringBuffer res = new StringBuffer();
        for( int i=0; i<path.length; i++ ) {
            res.append( path[i] );
            if( i < path.length-1 )
                res.append('.');
        }
        return res.toString();
    }
    
    private boolean childAlreadyExists( String childName, DefaultMutableTreeNode parent ) {
        for( Enumeration en = parent.children(); en.hasMoreElements(); ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)en.nextElement();
            if( childName.equals( child.getUserObject() ) ) {
                return true;
            }
        }
        return false;
    }
    
    public ConfigurationAccess getConfigAccess() {
        return configAccess;
    }
    
    /**
     * Changes the given configuration value in OpenOffice config storage.
     * 
     * @param cv Current path and value.
     * @param aValue New value
     */
    public void updateValue( ConfigValue cv, Object aValue ) {
        String fullPath = cv.getFullConfigPath().substring( 0, cv.getFullConfigPath().length()-cv.getDisplayName().length()-1 );
        try {
            XInterface updateView = getConfigAccess().createUpdateView( fullPath );
            
            XHierarchicalPropertySet props = (XHierarchicalPropertySet)UnoRuntime.queryInterface(XHierarchicalPropertySet.class, updateView);
            props.setHierarchicalPropertyValue(cv.getDisplayName(), aValue );

            // commit the changes
            XChangesBatch xUpdateControl = (XChangesBatch) UnoRuntime.queryInterface(XChangesBatch.class,updateView);
        
            xUpdateControl.commitChanges();

            // now clean up
            ((XComponent) UnoRuntime.queryInterface(XComponent.class, updateView)).dispose();
            
            //try re-reading the property value again
            XInterface userView = getConfigAccess().createConfigView( fullPath, true );
            XHierarchicalNameAccess nameAccess = (XHierarchicalNameAccess) UnoRuntime.queryInterface(XHierarchicalNameAccess.class,userView);
            Object newValue = nameAccess.getByHierarchicalName( cv.getDisplayName() );
            newValue = ListConfigurationProcessor.convert( newValue );
            cv.setUserValue( newValue );
        } catch (com.sun.star.uno.Exception ex) {
            JOptionPane.showMessageDialog( null, ex.getMessage() );
        }
    }
}
