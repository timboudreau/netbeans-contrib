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
