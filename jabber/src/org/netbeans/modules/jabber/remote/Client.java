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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * An implementation of the remote display client
 *
 * @author  nenik
 */
public class Client {
    
    public static void createSocketClient(String addr, byte[] key) throws Exception {

        int off = addr.indexOf(':');
        String serv = addr.substring(0, off);
        int port = Integer.parseInt(addr.substring(off+1));
        
        Socket sock = new Socket(serv, port);
        SocketPacketChannel channel = new SocketPacketChannel(sock);

        new Client(channel, key);
    }
    
    void stopClient() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Iterator it = windows.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            it.remove();
            
            Surface impl = (Surface)entry.getValue();
            impl.dismiss();
        }
    }
    
    private PacketChannel channel;
    /** A Map<Integer,Surface> of all active windows. */
    private Map windows = new HashMap();
    
    /** Creates a new instance of Client */
    public Client(PacketChannel channel, byte[] key) throws IOException {
        this.channel = channel;
        byte[] enc = new byte[2];
        enc[0] = 0;
        enc[1] = 1;
        
        Packet auth = Packet.createAuthPacket(key, enc);
        channel.writePacket(auth);
        
        new Thread(new Receiver()).start();
    }

    private interface Surface {
        public Rectangle getBounds();
        public void show();
        public void doResize(Dimension dim);
        public void dismiss();
        public void setRGB(int x, int y, int w, int h, int[] rgb);

    }
    
    private class FrameImpl extends JFrame implements Surface {
        private BufferedImage bi;
        int id;

        public FrameImpl(int id, Rectangle bounds, byte flags) {
            this.id = id;
            bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            // XXX
            Graphics g = bi.getGraphics();
            g.setColor(Color.red);
            g.drawLine(0, 0, bounds.width, bounds.height);
            g.drawLine(bounds.width, 0, 0, bounds.height);
            // XXX
            
            setBounds(bounds);
            
            PerWindowListener lst = new PerWindowListener(id);
            addMouseListener(lst);
            addMouseMotionListener(lst);
            addKeyListener(lst);
        }
        
        public void paint(Graphics g) {
            g.drawImage(bi, 0, 0, null);
        }
        
        public void doResize(Dimension size) {
            BufferedImage bi2 = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            bi2.getGraphics().drawImage(bi, 0, 0, null);
            bi = bi2;
            setSize(size);
        }
        
        public void dismiss() {
            hide();
            dispose();
        }        
        
        public void setRGB(int x, int y, int w, int h, int[] rgb) {
            bi.setRGB(x, y, w, h, rgb, 0, w);
            repaint(x, y, w, h);
        }
    }

    private class DialogImpl extends javax.swing.JDialog implements Surface {
        private BufferedImage bi;
        int id;

        public DialogImpl(Frame owner, int id, Rectangle bounds, byte flags) {
            super(owner);
            init(id, bounds, flags);
        }

        public DialogImpl(Dialog owner, int id, Rectangle bounds, byte flags) {
            super(owner);
            init(id, bounds, flags);
        }
            
        private void init (int id, Rectangle bounds, byte flags) {
            this.id = id;
            bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            // XXX
            Graphics g = bi.getGraphics();
            g.setColor(Color.red);
            g.drawLine(0, 0, bounds.width, bounds.height);
            g.drawLine(bounds.width, 0, 0, bounds.height);
            // XXX
            
            setBounds(bounds);

            PerWindowListener lst = new PerWindowListener(id);
            addMouseListener(lst);
            addMouseMotionListener(lst);
            addKeyListener(lst);
        }
        
        public void paint(Graphics g) {
            g.drawImage(bi, 0, 0, null);
        }
        
        public void doResize(Dimension size) {
            BufferedImage bi2 = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            bi2.getGraphics().drawImage(bi, 0, 0, null);
            bi = bi2;
            setSize(size);
        }
        
        public void dismiss() {
            hide();
            dispose();
        }        
        
        public void setRGB(int x, int y, int w, int h, int[] rgb) {
            bi.setRGB(x, y, w, h, rgb, 0, w);
            repaint(x, y, w, h);
        }        
    }

    private class WindowImpl extends javax.swing.JWindow implements Surface {
        private BufferedImage bi;
        int id;
        
        public WindowImpl(Frame owner, int id, Rectangle bounds, byte flags) {
            super(owner);
            init(id, bounds, flags);
        }

        public WindowImpl(Window owner, int id, Rectangle bounds, byte flags) {
            super(owner);
            init(id, bounds, flags);
        }
            
        private void init (int id, Rectangle bounds, byte flags) {
            this.id = id;
            if ((flags & 1) == 1) setName("###overrideRedirect###");
            bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);

            Graphics g = bi.getGraphics();
            g.setColor(Color.red);
            g.drawLine(0, 0, bounds.width, bounds.height);
            g.drawLine(bounds.width, 0, 0, bounds.height);
            
            setBounds(bounds);

            PerWindowListener lst = new PerWindowListener(id);
            addMouseListener(lst);
            addMouseMotionListener(lst);
            addKeyListener(lst);
        }
        
        public void paint(Graphics g) {
            g.drawImage(bi, 0, 0, null);
        }
        
        public void doResize(Dimension size) {
            BufferedImage bi2 = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            bi2.getGraphics().drawImage(bi, 0, 0, null);
            bi = bi2;
            setSize(size);
        }
        
        public void dismiss() {
            hide();
            dispose();
        }
        
        public void setRGB(int x, int y, int w, int h, int[] rgb) {
            bi.setRGB(x, y, w, h, rgb, 0, w);
            repaint(x, y, w, h);
        }        
    }
    
    private void addWindow(int id, int parentId, Rectangle bounds, byte flags) {
        Surface parent = (Surface)windows.get(new Integer(parentId));
        if (parent != null) {
            bounds.x += parent.getBounds().x;
            bounds.y += parent.getBounds().y;            
        } else {
            bounds.x += 800;
        }       
        
        Surface impl;
        if ((flags & 2) != 0) { // frame
            impl = new FrameImpl(id, bounds, flags);
        } else if ((flags & 4) != 0) { // dialog
            if (parent instanceof Dialog) {
                impl = new DialogImpl((Dialog)parent, id, bounds, flags);
            } else {
                impl = new DialogImpl((Frame)parent, id, bounds, flags);
            }
        } else { // window
            if (parent instanceof Frame) {
                impl = new WindowImpl((Frame)parent, id, bounds, flags);
            } else {
                impl = new WindowImpl((Window)parent, id, bounds, flags);
            }
        }

        windows.put(new Integer(id), impl);
        impl.show();
    }
    
    private void removeWindow(int id) {
        Surface impl = (Surface)windows.remove(new Integer(id));
        impl.dismiss();
    }
    
    private void resizeWindow(int id, Dimension size) {
        Surface impl = (Surface)windows.get(new Integer(id));
        impl.doResize(size);
    }
    
    
    private void updateWindow(int id, byte[] data) {
        Surface impl = (Surface)windows.get(new Integer(id));
                
        int offset = 5 + 4;
        
        int encoding = data[offset++];

        int x = ((int)data[offset++]) & 0xFF | ((((int)data[offset++]) & 0xFF) << 8);
        int y = ((int)data[offset++]) & 0xFF | ((((int)data[offset++]) & 0xFF) << 8);
        int w = ((int)data[offset++]) & 0xFF | ((((int)data[offset++]) & 0xFF) << 8);
        int h = ((int)data[offset++]) & 0xFF | ((((int)data[offset++]) & 0xFF) << 8);

        if (w*h+5+5+8 != data.length) {
            System.err.println("Bad data received!!!");
            return;
        }

        int[] rgb = new int[w*h];
        for (int i=0; i<w*h; i++) {
	    int comp = (int)data[offset++];
	    int r = (comp <<(16+0)) & 0x00E00000;
            r = (r | r >> 3 | r >> 6) & 0x00FF0000;
            
	    int g = (comp << (8+3)) & 0x0000E000;
            g = (g | g >> 3 | g >> 6) & 0x0000FF00;
            
	    int b = (comp << (0+6)) & 0x000000C0;
            b = (b | b >> 2 | b >> 4 | b >> 6) & 0x000000FF;
	    rgb[i] = 0xFF000000 | r | g | b;
        }

        impl.setRGB(x, y, w, h, rgb);
    }
    
    private void sendMouseEvent(int id, MouseEvent e) {
        Packet p = Packet.createMouseEventPacket(id, e);
        try {
            channel.writePacket(p);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
    
    private void sendKeyEvent(int id, KeyEvent e) {
        Packet p = Packet.createKeyEventPacket(id, e);
        try {
            channel.writePacket(p);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
    
    private class Receiver implements Runnable {
        
        public void run() {
            for (;;) {
                Packet pck = null;
                try {
                    pck = channel.readPacket();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    SwingUtilities.invokeLater(new AWTWrapper(null));
                    return;
                }

                SwingUtilities.invokeLater(new AWTWrapper(pck));
            }
        }
        
        private class AWTWrapper implements Runnable {
            private Packet pck;
            AWTWrapper(Packet pck) {
                this.pck = pck;
            }
            
            public void run() {
                if (pck == null) { // shutdown
                    stopClient();
                    return;
                }
                switch (pck.getCommand()) {
                    case Packet.COMMAND_WINDOW_ADDED:
                        addWindow(pck.getWindowId(), pck.getParentId(), pck.getWindowRectangle(), pck.getWindowFlags());
                        break;

                    case Packet.COMMAND_WINDOW_REMOVED:
                        removeWindow(pck.getWindowId());
                        break;

                    case Packet.COMMAND_WINDOW_RESIZED:
                        resizeWindow(pck.getWindowId(), pck.getWindowDimension());
                        break;

                    case Packet.COMMAND_CONTENT_CHANGED:
                        updateWindow(pck.getWindowId(), pck.getBytes());
                        break;

                    default:
                        System.err.println("unknown incomming packet: " + pck.getCommand());
                }
                
            }
        }
        
    }
    
    private class PerWindowListener implements MouseListener, MouseMotionListener, KeyListener {
        int id;
        
        public PerWindowListener(int id) {
            this.id = id;
        }

        public void mouseClicked(MouseEvent e) {
            sendMouseEvent(id,e);
        }
        public void mousePressed(MouseEvent e) {
            sendMouseEvent(id,e);
        }
        public void mouseReleased(MouseEvent e) {
            sendMouseEvent(id,e);
        }
        public void mouseEntered(MouseEvent e) {
            sendMouseEvent(id,e);
        }
        public void mouseExited(MouseEvent e) {
            sendMouseEvent(id,e);
        }
        public void mouseDragged(MouseEvent e) {
            sendMouseEvent(id, e);
        }
        public void mouseMoved(MouseEvent e) {
            sendMouseEvent(id, e);
        }
        public void keyTyped(KeyEvent e) {
            sendKeyEvent(id, e);
        }
        public void keyPressed(KeyEvent e) {
            sendKeyEvent(id, e);
        }
        public void keyReleased(KeyEvent e) {
            sendKeyEvent(id, e);
        }
    }

}
