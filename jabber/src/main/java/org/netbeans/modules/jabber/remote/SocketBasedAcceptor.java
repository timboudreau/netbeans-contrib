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
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A listener that accepts TCP connections and encapsulates them in
 * a SocketPacketChannel.
 *
 * @author  nenik
 */
class SocketBasedAcceptor {
    private byte[] key;
    private LocalManager man;
    private ServerSocket server;
    private Thread thread;
    boolean stopped;
    boolean ctrl;
    
    private Set halfOpen = new HashSet();
    
    /** Creates a new instance of SocketBasedAcceptor
     * @throws IOException when there is a problem opening a server socket.
     */
    public SocketBasedAcceptor(LocalManager man, int port, byte[] key, boolean ctrl) throws IOException {
        this.ctrl = ctrl;
        this.man = man;
        this.key = key;
        
        server = new ServerSocket(port);
        
        thread = new Thread(new Acceptor(), "SocketBasedAcceptor-" + port);
        thread.start();
    }
    
    
    public void stop() {
        stopped = true;
        thread.interrupt();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Iterator it = halfOpen.iterator(); it.hasNext(); ) {
            ((HalfOpen)it.next()).kill();
        }
    }
    
    private class Acceptor implements Runnable {
        public void run() {
            // keep waiting for new connections and create servers for them
            while (!stopped) {
                try {
                    Socket sock = server.accept();
                    SocketPacketChannel channel = new SocketPacketChannel(sock);
                    halfOpen.add(new HalfOpen(channel));
                } catch (Exception e) {
                    // XXX
                    e.printStackTrace();
                }
            }
        }
    }
    
    /*
     * wait for the auth packet, verify the key
     */
    private class HalfOpen extends Thread {
        PacketChannel channel;
        
        public HalfOpen(PacketChannel channel) {
            super(channel + "-Auth");
            this.channel = channel;
            
            start();
        }
        
        public void kill() {
            try {
                channel.close();
            } catch (IOException ioe) {
            }
            interrupt();
        }
        
        public void run() {
            Packet pack;
            try {
                pack = channel.readPacket();
            } catch (IOException ioe) {
                kill();
                return;
            }
            
            if (verify(pack)) {
                man.attachClient(channel, ctrl);
            } else {
                System.err.println("bad auth from " + channel.toString());
                kill();
            }
            // the thread will die in both cases
        }
        
        private boolean verify(Packet pack) {
            if (pack.getCommand() != Packet.COMMAND_AUTH) return false;
            byte[] data = pack.getBytes();
            for (int i=0; i<32; i++) {
                if (key[i] != data[i+5]) return false;
            }
            return true;
        }
    }

}
