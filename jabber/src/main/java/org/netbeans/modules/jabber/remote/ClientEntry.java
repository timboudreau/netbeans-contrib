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

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * This class represents one client registered for UI notifications.
 * The entry manages a queue of packets to be sent to a client and possibly
 * manages a queue of incomming events from the client.
 *
 * The queue is also able to throw away pending packets in case the client
 * can't keep the pace, yet maintain correct state at the client.
 *
 * @author  nenik
 */
class ClientEntry {
    
    private LocalManager manager;
    private PacketChannel channel;
    private boolean ctrl;
    
    // the following data is valid only under the queue's lock
    private LinkedList queue = new LinkedList();
    private boolean shutdown;
    private int fullSize;
    private int backlogSize;
    
    private Thread sender;
    private Thread receiver;
    
    
    /** Creates a new instance of ClientEntry based on given PacketChannel
     * after successfull authentization.
     */
    ClientEntry(LocalManager manager, String clientDesc, PacketChannel channel, boolean ctrl) {
        this.manager = manager;
        this.channel = channel;
        this.ctrl = ctrl;
        sender = new Thread(new Sender(), clientDesc + "-Sender");
        receiver = new Thread(new Receiver(), clientDesc + "-Receiver");
        sender.start();
        receiver.start();
    }
    
    
    void enqueuePacket(Packet packet) {
        // TODO: compute stats and discard overflow packets
        synchronized (queue) {
            queue.addLast(packet);
            queue.notifyAll();
        }
    }
    
    private void error(IOException ioe) {
        shutdown = true;
        try {
            channel.close();
        } catch (IOException e) {
        }
        
        sender.interrupt();
        receiver.interrupt();
        
        manager.detachClient(this);
    }
    
    
    private class Sender implements Runnable {
        public void run() {
            for(;;) {
                Packet pack = null;
                synchronized(queue) {
                    try {
                        if (queue.isEmpty()) queue.wait();
                    } catch (InterruptedException e) {}

                    if (shutdown) break;
                    if (queue.size() == 0) continue; // keep waiting
                    
                    pack = (Packet)queue.removeFirst();
                }

                // dispatch the packet if possible
                try {
                    channel.writePacket(pack);
                } catch (IOException ioe) {
                    error(ioe); // process the exception, will shutdown
                }
            }
        }
    }
    
    private class Receiver implements Runnable {
        
        public void run() {
            for (;;) {
                Packet pck = null;
                try {
                    pck = channel.readPacket();
                } catch (IOException ioe) {
                    error(ioe); // process the exception, will shutdown
                    break;
                }
                // XXX - process incomming packets from VIP clients
                if (!ctrl) continue; // log attempt
                switch (pck.getCommand()) {
                    case Packet.COMMAND_MOUSE_EVENT:
                        int id = pck.getWindowId();
                        Window win = manager.getWindow(id);
                        
                        if (win != null) {
                            MouseEvent evt = pck.getMouseEvent(win);
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(evt);
                        }
                        break;
                        
                    case Packet.COMMAND_KEY_EVENT:
                        id = pck.getWindowId();
                        win = manager.getWindow(id);
                        
                        if (win != null) {
                            KeyEvent evt = pck.getKeyEvent(win);
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(evt);
                        }
                        break;
                }
            }
        }
    }
}
