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

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;

/**
 * A persistent wrapper around a XMPPConnection that allows late login,
 * relogin after lost connection and so on.
 *
 * @author  nenik
 */
public class Connection {
    private Manager manager;
    private XMPPConnection connection;
    
    /** Creates a new instance of Connection */
    Connection(Manager man) {
        man = manager;
    }
    
    void connect(String server) throws XMPPException {
        XMPPConnection con = new XMPPConnection(server);
        connection = con;
    }
    
    void disconnect() {
        connection.close();
        connection = null;
    }
    
    XMPPConnection getCurrentConnection() {
        return connection;
    }
    
    public Roster getRoster() {
        return (connection == null) ? null : connection.getRoster();
    }
    
    public void sendPacket(Packet packet) {
        if (connection == null) throw new IllegalStateException("Not connected.");
        connection.sendPacket(packet);
    }
}
