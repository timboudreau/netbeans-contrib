/*
 * DisconnectCtxCookie.java
 *
 * Created on April 7, 2001, 10:47 AM
 */

package org.netbeans.modules.jndi.utils;

import org.openide.nodes.Node;
/**
 *
 * @author  root
 * @version 
 */
public interface DisconnectCtxCookie extends Node.Cookie {

    public void disconnect ();
}

