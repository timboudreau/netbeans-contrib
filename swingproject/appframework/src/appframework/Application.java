/*
 * Application.java
 *
 * Created on March 13, 2006, 3:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package appframework;

import appframework.spi.AppImpl;

/**
 *
 * @author TOSHIBA USER
 */
public final class Application {
    private static Application instance = null;
    Application(AppImpl impl) {
        this.impl = impl;
        register (this);
    }

    public static void initialize() {
        getDefault().getImpl().init();
    }

    private static AppImpl lookupDefaultImpl() {
        //XXX look up by class name from sysprp or metainf lookup, etc.
        return new DefaultImpl();
    }
    
    public Application() {
        this (lookupDefaultImpl());
    }
    
    private static final void register (Application app) {
        if (instance != null) {
            throw new Error();
        }
        instance = app;
    }
    
    public static final String APPLICATION_NAME = "appName";

    private AppImpl impl;
    
    private AppImpl getImpl() {
        return impl;
    }
    
    public static String getString (Object scope, String key) {
        return getDefault().getImpl().getString (scope, key);
    }
    
    public static Application getDefault() {
        if (instance == null) {
            instance = new Application (lookupDefaultImpl());
        }
        return instance;
    }
    
    public static Object getDefaultObject (Object scope, String key) {
        return getDefault().getImpl().getDefaultObject (scope, key);
    }
    
    public static Object getObject (Object scope, String key) {
        return getDefault().getImpl().getObject (scope, key);
    }
    
    public static Object putPersistent (Object scope, String key, Object value) {
        return getDefault().getImpl().putPersistent (scope, key, value);
    }
    
    public static Object putSession (Object scope, String key, Object value) {
        return getDefault().getImpl().putSession (scope, key, value);
    }
    
    
    
}
