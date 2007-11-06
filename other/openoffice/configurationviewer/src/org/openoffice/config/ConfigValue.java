/*
 * ConfigValue.java
 *
 * Created on 25. øíjen 2007, 12:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openoffice.config;

/**
 *
 * @author sa
 */
class ConfigValue {
    
    private String configPath;
    private Object userValue;
    private Object sharedValue;
    
    /** Creates a new instance of ConfigValue */
    public ConfigValue( String configPath, Object shared, Object user ) {
        this.configPath = configPath;
        this.sharedValue = shared;
        this.userValue = user;
    }

    public String getConfigPath() {
        return configPath;
    }

    public Object getUserValue() {
        return userValue;
    }

    public void setUserValue(Object userValue) {
        this.userValue = userValue;
    }

    public Object getSharedValue() {
        return sharedValue;
    }

    public void setSharedValue(Object sharedValue) {
        this.sharedValue = sharedValue;
    }
    
}
