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

package org.netbeans.modules.jabber.remote;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * This interface represents a two-way packet stream between server and client.
 * Its API is generic to allow differrent implementation of the underlaying
 * communication vehicle, be it standard TCP socket, scrambled channel,
 * RTP multicast or a substream in an aplication-specific multiplex.
 * It may even be a local testing loopback.
 *
 * @author  nenik
 */
public interface PacketChannel extends Channel {
    public void writePacket(Packet packet) throws IOException;
    public Packet readPacket() throws IOException;
    
}
