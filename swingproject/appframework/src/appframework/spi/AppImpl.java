/*
 * AppImpl.java
 *
 * Created on March 13, 2006, 3:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package appframework.spi;

/**
 *
 * @author TOSHIBA USER
 */
public interface AppImpl {
    void init();

    Object getDefaultObject(Object scope, String key);

    Object getObject(Object scope, String key);

    Object putPersistent(Object scope, String key, Object value);

    Object putSession(Object scope, String key, Object value);

    String getString(Object scope, String key);
    
}
