/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the Jabber module.
 * The Initial Developer of the Original Code is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.jabber;

/**
 * A configuration storage bean for a NetBeans Jabber client.
 *
 * @author  nenik 
 */
public class Settings {
    private String jid;
    private String password;
    private String resource;
    private long  timeout;
    
    /* states: 0 - off; 1 - on; 2 - last,off; 3 - last,on */
    private int autologin;
    
    
    public Settings() {
    }
    
    public String getUserJid() {
        return jid;
    }
    
    public void setUserJid(String jid) {
        this.jid = jid;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getAutologin() {
        return autologin;
    }
    
    public void setAutologin(int state) {
        this.autologin = state;
    }
    
}
