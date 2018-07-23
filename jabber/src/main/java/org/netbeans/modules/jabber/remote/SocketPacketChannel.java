/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the Jabber module.
 * The Initial Developer of the Original Software is Petr Nejedly
 * Portions created by Petr Nejedly are Copyright (c) 2004.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
