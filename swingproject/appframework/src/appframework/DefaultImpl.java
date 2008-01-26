/*
 * DefaultImpl.java
 *
 * Created on March 13, 2006, 3:37 PM
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
class DefaultImpl implements AppImpl {
    
    /** Creates a new instance of DefaultImpl */
    public DefaultImpl() {
    }

    public void init() {
    }

    public Object getDefaultObject(Object scope, String key) {
        return null;
    }

    public Object getObject(Object scope, String key) {
        return null;
    }

    public Object putPersistent(Object scope, String key, Object value) {
        return null;
    }

    public Object putSession(Object scope, String key, Object value) {
        return null;
    }

    public String getString(Object scope, String key) {
        return null;
    }
}
