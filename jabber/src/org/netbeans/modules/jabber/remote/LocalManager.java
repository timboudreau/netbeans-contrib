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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Vector;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A Localmanager manages a dynamic set of windows, provides wrapped graphics
 * for them and keeps the visual content of them. Also notifies any registered
 * client about window set changes, window size changes and content changes.
 *
 * @author  nenik
 */
public class LocalManager {
    
    private static LocalManager INSTANCE = new LocalManager();

    /** A flag marking that the UI is watched.
     * If the UI is not watched, all of the painting operations
     * go the shortest path to the device, but if the flag is set, painting
     * is redirected to have a local copy of the UI.
     */
    private boolean watched;

    int lastport = 8888;
    // map of all active acceptors. Key and value types are acceptor-dependent
    private Map acceptors = new HashMap();
    
    public int startSocketAcceptor(byte[] cookie, boolean ctrl) throws IOException {
        int port = lastport++;
        Object key = new Integer(port);
        SocketBasedAcceptor acceptor = new SocketBasedAcceptor(this, port, cookie, ctrl);
        acceptors.put(key, acceptor);
        return port;
    }
    
    public void stopSocketAcceptor(int port) {
        Object key = new Integer(port);
        SocketBasedAcceptor acceptor = (SocketBasedAcceptor)acceptors.get(key);
        acceptor.stop();
        // stop all clients
    }
    
    private Object clientsLock = new Object();
    private Set clients = new HashSet();
    
    public void attachClient(PacketChannel channel, boolean ctrl) {
        // already authenticated, create a client entry and send conf
        
        ClientEntry entry = new ClientEntry(this, channel.toString(), channel, ctrl);
        
        synchronized (clientsLock) {
            boolean first = false;
            if (clients.isEmpty()) first = true;
            clients.add(entry);
            
            for (Iterator it = infos.values().iterator(); it.hasNext();) {
                WindowInfo info = (WindowInfo)it.next();
                
                // send conf
                Packet p = Packet.createWindowAddedPacket(info.getId(), info.getParent(), info.getBounds(), info.getFlags());
                entry.enqueuePacket(p);
                
                if (!first) { // send actual content
                    Packet cnt = createImagePacket(info, null);
                    entry.enqueuePacket(cnt);
                }
            }
            if (first) firstClientConnected();
        }
    }
    
    /**
     *   content-changed: command=4, len=var, payload:
     *     4B window id
     *     1B encoding = 1; 
     *    2B xpos
     *    2B ypos
     *    2B width
     *    2B height
     *    width*height pixel[3bitR|3bitG|2bitB]
     */

    private Packet createImagePacket(WindowInfo info, Rectangle rect) {
        if (rect == null) {
            rect = new Rectangle(0, 0, info.getBounds().width, info.getBounds().height);
        }
        
        int id = info.getId();        
        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;
        
        int size = 5 + 8 + w*h;
        byte[] data = new byte[size+5];
        int offset = 0;

        // size
        data[offset++] = (byte)(size);
        data[offset++] = (byte)(size >> 8);
        data[offset++] = (byte)(size >> 16);
        data[offset++] = (byte)(size >> 24);
        
        // command
        data[offset++] = Packet.COMMAND_CONTENT_CHANGED;
        
        // window id
        data[offset++] = (byte)(id);
        data[offset++] = (byte)(id >> 8);
        data[offset++] = (byte)(id >> 16);
        data[offset++] = (byte)(id >> 24);

        // encoding
        data[offset++] = 1;
        
        // x
        data[offset++] = (byte)(x % 256);
        data[offset++] = (byte)(x / 256);
        
        // y
        data[offset++] = (byte)(y % 256);
        data[offset++] = (byte)(y / 256);
        
        // w
        data[offset++] = (byte)(w % 256);
        data[offset++] = (byte)(w / 256);
        
        // h
        data[offset++] = (byte)(h % 256);
        data[offset++] = (byte)(h / 256);

        int[] rgb = new int[w*h];
        info.content.getRGB(x, y, w, h, rgb, 0, w);
        
        for (int i=0; i<w*h; i++) {
            int pixel = rgb[i];
		
            data[offset++] = (byte)(
		    ((pixel >> 16) & 0xE0) |
                    ((pixel >> (8+3)) & 0x1C) |
		    ((pixel >> (3+3)) & 0x03));

        }
        
        return Packet.createFromData(data);
    }
    
    private void firstClientConnected() {
        // watch the UI
        watched = true;
        
        for (Iterator it = infos.keySet().iterator(); it.hasNext(); ) {
            ((Window)it.next()).repaint();
        }
    }
    
    public void detachClient(ClientEntry ce) {
        synchronized (clientsLock) {
            clients.remove(ce);
            if (clients.isEmpty()) lastClientDetached();
        }
        
    }
    
    private void lastClientDetached() {
        watched = false;
        
        // XXX
    }
    
    // called when new window is added. Distributes info to all clients
    private void clientsWindowAdded(WindowInfo info) {
        synchronized (clientsLock) {
            if (clients.isEmpty()) return;
            Packet p = Packet.createWindowAddedPacket(info.getId(), info.getParent(), info.getBounds(), info.getFlags());
            clientsSendPacket(p);
        }
    }

    // called when a window is removed. Distributes info to all clients
    private void clientsWindowRemoved(WindowInfo info) {
        synchronized (clientsLock) {
            if (clients.isEmpty()) return;
            Packet p = Packet.createWindowRemovedPacket(info.getId());
            clientsSendPacket(p);
        }
    }

    // called when a window is resized. Distributes info to all clients
    private void clientsWindowResized(WindowInfo info) {
        synchronized (clientsLock) {
            if (clients.isEmpty()) return;
            Packet p = Packet.createWindowResizedPacket(info.getId(), info.getSize());
            clientsSendPacket(p);
        }
    }

    // called when a window is resized. Distributes info to all clients
    private void clientsWindowChanged(WindowInfo info, Rectangle rect) {
        synchronized (clientsLock) {
            if (clients.isEmpty()) return;

            Packet p = createImagePacket(info, rect);
            clientsSendPacket(p);
        }
    }

    // should be called under the clients lock
    private void clientsSendPacket(Packet packet) {
        for (Iterator it = clients.iterator(); it.hasNext(); ) {
            ClientEntry entry = (ClientEntry)it.next();
            entry.enqueuePacket(packet);
        }
    }
    
    
    /*
     * The manager keeps a pool of all connected clients and generally
     * tries to keep all the clients in sync, so it can compute the packets only once
     * and enqueue the same packet for all clients. This can get tricky for
     * clients with different supported encodings, but it is still manageable.
     */
    
    
    /** Map<Window,WindowInfo>*/
    // this man needs to keep the order to serve clients with parent windows
    // before subwindows
    private Map infos = new java.util.LinkedHashMap();

    Window getWindow(int id) {
        // XXX
        for (Iterator it = infos.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            
            if (((WindowInfo)entry.getValue()).getId() == id) return (Window)entry.getKey();
        }
        
        return null;
    }
    
    
    /** Creates a new instance of LocalManager */
    private LocalManager() {
    }

    /* package private methods: notification from the DisplayInterceptor*/
    void windowDisplayed(Window w) {
        if (!infos.containsKey(w)) {
            WindowInfo info = new WindowInfo(w);
            infos.put(w, info);
            clientsWindowAdded(info); // XXX?
        }
    }
    
    void windowHidden(Window w) {
        if (infos.containsKey(w)) {
            WindowInfo info = (WindowInfo)infos.remove(w);
            clientsWindowRemoved(info); // XXX?
        }
    }
    
    void windowResized(Window w) {
        if (infos.containsKey(w)) {
            WindowInfo info = (WindowInfo)infos.get(w);
            info.updateSize();
            clientsWindowResized(info); // XXX?
        }        
    }
    
    Graphics createGraphicsFor(Window w, Graphics orig) {
        if (!watched) return orig;
        
        WindowInfo info = (WindowInfo)infos.get(w);
        if (info == null) return orig; // not yet shown
        
        return info.createGraphics(orig);
    }
    
    public static LocalManager getDefault() {
        return INSTANCE;
    }
    
    
    private static int lastID = 0;
    private static synchronized int createID() {
        return lastID++;
    }
    

    private class WindowInfo {
        private int id = createID();
        private int parent; // or -1
        private Window window;
	private Rectangle bounds;
        private Dimension dim;
        private byte flags;
        BufferedImage content;
        
        private java.util.Timer timer = new java.util.Timer(true);

        private boolean modified;
        private int x1, y1, x2, y2;
        
        
        public int getId() {
            return id;
        }
        
        public int getParent() {
            return parent;
        }
        
        public Rectangle getBounds() {
            return bounds;
        }

        public Dimension getSize() {
            return dim;
        }
        
        public byte getFlags() {
            return flags;
        }

        public WindowInfo(Window w) {
            window = w;
            
            Window par = w.getOwner();
            WindowInfo parInfo = (WindowInfo)infos.get(par);
            parent = parInfo == null ? -1 : parInfo.getId();
            
            dim = w.getSize();
            bounds = w.getBounds();
            if (parent != -1) { // relative to parent
                bounds.x -= par.getBounds().x;
                bounds.y -= par.getBounds().y;
            }
            
            if (w instanceof Frame) flags |= 2;
            if (w instanceof Dialog) flags |= 4;
            if ("###overrideRedirect###".equals(w.getName())) flags |= 1;
        }
        
        public Graphics createGraphics(Graphics orig) {
            Graphics peer = checkImage().getGraphics();
            return new Wrapped(orig, peer, dim);
        }

        void updateSize() {
            dim = window.getSize();
            if (content != null) {
                BufferedImage n = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB); // XXX
                n.getGraphics().drawImage(content, 0, 0, null);
                content = n;
            }
        }
        
        
        private BufferedImage checkImage() {
            if (content == null) {
                content = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB); // XXX
            }
            return content;
        }
        
        
        synchronized void damaged(int x, int y, int width, int height) {
            if (modified) { // first damage after last refresh, extend the range
                if (x1 > x) x1 = x;
                if (y1 > y) y1 = y;
                if (x2 < x + width) x2 = x + width;
                if (y2 < y + height) y2 = y + height;
            } else { // first notification, set the initial range...
                modified = true;
                x1 = x;
                y1 = y;
                x2 = x + width;
                y2 = y + height;
                
                // ... and schedule the update
                timer.schedule(new java.util.TimerTask() {
                    public void run() {
                        modified = false;
                        if (x2 > dim.width) x2 = dim.width;
                        if (y2 > dim.height) y2 = dim.height;
                        clientsWindowChanged(WindowInfo.this, new Rectangle(x1, y1, (x2-x1), (y2-y1)));
                    }
                }, 100); // fire 100ms after first UI change
            }
        }

        /** Special graphics that keeps track of modifications */
        private class Wrapped extends Graphics {
            private Dimension full;
            private int tr_x = 0;
            private int tr_y = 0;

            private Graphics orig;
            private Graphics peer;
            public Wrapped(Graphics orig, Graphics peer, Dimension dim) {
                this.orig = orig;
                this.peer = peer;
                this.full = dim;
            }
            
            private void damage(int x, int y, int width, int height) {
                damage(); // for test
/*                // XXX clip with r
                if (width < 0) {
                    x += width;
                    width = -width;
                }
                
                if (height < 0) {
                    y += height;
                    height = -height;
                }

                Rectangle r = getClipBounds();
                
                damaged(x+tr_x, y+tr_y, width+1, height+1);
 */
            }


            /* unknown damage, use only clip bounds*/
            private void damage() {
                Rectangle r = getClipBounds();
                if (r != null) {
                    r.x += tr_x;
                    r.y += tr_y;
                } else {
                    r = new Rectangle(0, 0, full.width, full.height);
                }
                
                damaged(r.x, r.y, r.width, r.height);
            }
            
            /* a complete graphics implementation */
            public void clearRect(int x, int y, int width, int height) {
                peer.clearRect(x,y,width,height);
                damage(x, y, width, height);
                orig.clearRect(x,y,width,height);
            }

            public void clipRect(int x, int y, int width, int height) {
                peer.clipRect(x,y,width,height);
                orig.clipRect(x,y,width,height);
            }

            public void copyArea(int x, int y, int width, int height, int dx, int dy) {
                peer.copyArea(x,y,width,height, dx, dy);
                damage(x+dx, y+dy, width, height);
                orig.copyArea(x,y,width,height, dx, dy);
            }
        
            public Graphics create() {
                Wrapped wr = new Wrapped(orig.create(), peer.create(), full);
                wr.tr_x = tr_x;
                wr.tr_y = tr_y;
                return wr;
            }

            public void dispose() {
                peer.dispose();
                orig.dispose();
            }

            public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
                peer.drawArc(x,y, width, height,startAngle, arcAngle);
                damage(x, y, width+1, height+1);
                orig.drawArc(x,y, width, height,startAngle, arcAngle);
            }

            public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
                peer.drawImage(img, x, y, observer);
                damage(x, y, img.getWidth(null), img.getHeight(null));
                return orig.drawImage(img, x, y, observer);
            }

            public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
            	peer.drawImage(img, x, y, bgcolor, observer);
            	damage(x, y, img.getWidth(null), img.getHeight(null));
            	return orig.drawImage(img, x, y, bgcolor, observer);
            }

            public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
                peer.drawImage(img, x, y, width, height, observer);
            	damage(x, y, width, height);
                return orig.drawImage(img, x, y, width, height, observer);
            }

            public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
                peer.drawImage(img, x, y, width, height, bgcolor, observer);
            	damage(x, y, width, height);
                return orig.drawImage(img, x, y, width, height, bgcolor, observer);
            }

            public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
                peer.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
                damage(dx1, dy1, (dx2-dx1)+1, (dy2-dy1)+1);
                return orig.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
            }

            public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
                peer.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
                damage(dx1, dy1, (dx2-dx1)+1, (dy2-dy1)+1);
                return orig.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
            }

            public void drawLine(int x1, int y1, int x2, int y2) {
                peer.drawLine(x1,y1, x2, y2);
                damage(x1, y1, (x2-x1), (y2-y1));
                orig.drawLine(x1,y1, x2, y2);
            }

            public void drawOval(int x, int y, int width, int height) {
                peer.drawOval(x,y, width, height);
                damage(x, y, width, height);
                orig.drawOval(x,y, width, height);
            }

            public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
                peer.drawPolygon(xPoints, yPoints, nPoints);
                damage(); // don't know real damage
                orig.drawPolygon(xPoints, yPoints, nPoints);
            }

            public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
                peer.drawPolyline(xPoints, yPoints, nPoints);
                damage(); // don't know real damage
                orig.drawPolyline(xPoints, yPoints, nPoints);
            }

            public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
                peer.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
                damage(x, y, width, height);
                orig.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
            }


            public void drawString(String str, int x, int y) {
                peer.drawString(str, x, y);
                damage(); // TODO: too important to leave so
                orig.drawString(str, x, y);
            }


            public void drawString(AttributedCharacterIterator iterator, int x, int y) {
                peer.drawString(iterator, x, y);
                damage(); // probably rare enough
                orig.drawString(iterator, x, y);
            }


            public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
                peer.fillArc(x,y, width, height,startAngle, arcAngle);
                damage(x, y, width+1, height+1);
                orig.fillArc(x,y, width, height,startAngle, arcAngle);
            }

            public void fillOval(int x, int y, int width, int height) {
                peer.fillOval(x,y, width, height);
                damage(x, y, width, height);
                orig.fillOval(x,y, width, height);
            }

            public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
                peer.fillPolygon(xPoints, yPoints, nPoints);
                damage(); // don't know real damage
                orig.fillPolygon(xPoints, yPoints, nPoints);
            }

            public void fillRect(int x, int y, int width, int height) {
                peer.fillRect(x, y, width, height);
                damage(x, y, width, height);
                orig.fillRect(x, y, width, height);
            }

            public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
                peer.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
                damage(x, y, width, height);
                orig.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
            }
            
            public void setClip(Shape clip) {
                peer.setClip(clip);
                orig.setClip(clip);
            }

            public void setClip(int x, int y, int width, int height) {
                peer.setClip(x, y, width, height);
                orig.setClip(x, y, width, height);
            }
        
            public void setColor(Color c) {
                peer.setColor(c);
                orig.setColor(c);
            }

            public void setFont(Font font) {
                peer.setFont(font);
                orig.setFont(font);
            }
        
            public void setPaintMode() {
                peer.setPaintMode();
                orig.setPaintMode();
            }

            public void setXORMode(Color c1) {
                peer.setXORMode(c1);
                orig.setXORMode(c1);
            }
        
            public void translate(int x, int y) {
                tr_x += x;
                tr_y += y;
                peer.translate(x, y);
                orig.translate(x, y);
            }

            public Shape getClip() { return orig.getClip(); }
            public Rectangle getClipBounds() { return orig.getClipBounds(); }
            public Color getColor() { return orig.getColor(); }
            public Font getFont() { return orig.getFont(); }
            public FontMetrics getFontMetrics(Font f) { return orig.getFontMetrics(f); }
        }

    }
    
}
