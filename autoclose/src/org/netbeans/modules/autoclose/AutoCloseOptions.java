/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.autoclose;

import java.util.Collections;
import java.util.Map;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Options for something or other.
 *
 * @author Jan Lahoda
 */
public class AutoCloseOptions extends SystemOption {
    
    private static final long serialVersionUID = 1L;
    
    public static final String PROP_MAX_OPENED_FILES = "maxOpenedFiles";
    public static final String PROP_AUTO_CLOSE_ENABLED = "autoCloseEnabled";
    public static final String PROP_TIMESTAMP_MAP = "timestampMap";
    
    // No constructor please!
    
    protected void initialize() {
        super.initialize();
        // If you have more complex default values which might require
        // other parts of the module to already be installed, do not
        // put them here; e.g. make the getter return them as a
        // default if getProperty returns null. (The class might be
        // initialized partway through module installation.)
        setMaxOpenedFiles(15);
        setAutoCloseEnabled(true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(AutoCloseOptions.class, "LBL_settings");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you provide context help then use:
        // return new HelpCtx(NBLaTeXSourceFactoryStorageSettings.class);
    }
    
    /** Default instance of this system option, for the convenience of associated classes. */
    public static AutoCloseOptions getDefault() {
        return (AutoCloseOptions)findObject(AutoCloseOptions.class, true);
    }
    
    public boolean isAutoCloseEnabled() {
        return ((Boolean) getProperty(PROP_AUTO_CLOSE_ENABLED)).booleanValue();
    }
    
    public void setAutoCloseEnabled(boolean value) {
        putProperty(PROP_AUTO_CLOSE_ENABLED, Boolean.valueOf(value));
    }
    
    public int getMaxOpenedFiles() {
        return ((Integer) getProperty(PROP_MAX_OPENED_FILES)).intValue();
    }
    
    public void setMaxOpenedFiles(int value) {
        // Automatically fires property changes if needed etc.:
        putProperty(PROP_MAX_OPENED_FILES, new Integer(value), true);
        // If you need to start some service, or do something else with
        // an external effect, you should not use putProperty(...): keep
        // the data as a private static member, and manually modify that
        // variable and use firePropertyChange(...). Because if putProperty(...)
        // is used, getters and setters will be skipped during project save
        // and restore, which may cause problems.
    }
    
    public Map getTimestampMap() {
        Map result = (Map) getProperty(PROP_TIMESTAMP_MAP);
        
        if (result == null) {
            return Collections.EMPTY_MAP;
        }
        
        return result;
    }
    
    public void setTimestampMap(Map m) {
        putProperty(PROP_TIMESTAMP_MAP, m);
    }
    
}
