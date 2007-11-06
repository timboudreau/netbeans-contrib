/*
 * ConfigManager.java
 *
 * Created on 6. listopad 2007, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openoffice.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;

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
}
