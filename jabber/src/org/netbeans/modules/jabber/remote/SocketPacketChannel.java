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
