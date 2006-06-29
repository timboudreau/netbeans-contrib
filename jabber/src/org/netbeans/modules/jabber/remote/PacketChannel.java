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
