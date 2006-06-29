/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
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
