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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A PacketChannel implementation over a TCP socket.
 *
 * @author  nenik
 */
public class SocketPacketChannel implements PacketChannel {

    private Socket socket;
    private OutputStream output;
    private InputStream input;

    private byte[] header = new byte[5];

    SocketPacketChannel(Socket socket) throws IOException {
 this.socket = socket;
        output = socket.getOutputStream();
        input = socket.getInputStream();
    }
    
    public void close() throws IOException {
        socket.close();
    }
    
    public boolean isOpen() {
        return !socket.isClosed();
    }
    
    /* only one thread can read from the channel */
    public Packet readPacket() throws java.io.IOException {
        byte[] hdr = header;
        readSafe(hdr, 0);
        int len =  ((int)hdr[0]) & 0xFF |
                 ((((int)hdr[1]) & 0xFF) << 8) |
                 ((((int)hdr[2]) & 0xFF) << 16) |
                 ((((int)hdr[3]) & 0xFF) << 24);
        
        byte[] full = new byte[len+5];
        System.arraycopy(hdr, 0,  full, 0, 5);
        readSafe(full, 5);
        
        return Packet.createFromData(full);
    }
    
    private void readSafe(byte[] target, int off) throws IOException {
        int len = target.length - off;
        while (len > 0) {
            int part = input.read(target, off, len);
            if (part < 0) throw new java.io.EOFException();
            off += part;
            len -= part;
        }
    }
    
    public void writePacket(Packet packet) throws java.io.IOException {
        byte[] content = packet.getBytes();
        output.write(content);
        output.flush();
    }
    
    public String toString() {
        return "SocketPacketChannel(" + socket.getInetAddress() + ")";
    }
    
}
