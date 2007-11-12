/*
 * ConfigManager.java
 *
 * Created on 6. listopad 2007, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openoffice.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author sa
 */
public class ConfigManager {
    
    private ConfigurationAccess configAccess;
    private List<String> roots;
    private Map<String, TableModel> models = new HashMap<String, TableModel>();
    
    /** Creates a new instance of ConfigManager */
    public ConfigManager( List<String> roots, ConfigurationAccess configAccess ) {
        this.roots = roots;
        this.configAccess = configAccess;
    }
    
    public List<? extends String> getRoots() {
        return roots;
    }
    
    public TableModel getTableModel( String root, boolean forceRefresh ) {
        TableModel res = models.get( root );
        if( null == res || forceRefresh ) {
            res = createTableModel( root );
            models.put( root, res );
        }
        return res;
    }
    
    private TableModel createTableModel( String configRoot ) {
        TableConfigurationProcessor processor = new TableConfigurationProcessor();
        configAccess.browse( configRoot, processor );
        processor.format();
        return processor.getTableModel();
    }
    
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
                        ConfigValueList list = new ConfigValueList( configAccess, parentPath+childPath, childPath );
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
}
