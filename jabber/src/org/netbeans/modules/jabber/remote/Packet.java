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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * An entity excapsulating a single packet to be sent (possibly) to multiple
 * clients. It covers whole wire protocol except image encoding.
 *
 * Wire protocol:
 * Wire protocol is asymetric with common packet format:
 * each packet consists of a header and optional data payload.
 * headers:
 *   4B frame size (x) LSB first
 *   1B command
 *   xB payload
 *
 * The protocol starts with handshake, then state packets are transferred.
 * Any endpoint may close the session using close-session packet
 * Handshake:
 *   client opens connection to server, sends auth packet
 *   server responds with auth-reply packet, sends window-added,content-changed
 *   event for all active windows
 *   
 * State packets:
 *   server is sending window-added, window-resized, content-changed,\
 *   and window-removed events. Server can also send mouse events and keystrokes
 *
 *   client (if allowed) is sending mouse-events, key-events, window-resize events
 *   and window-close events
 *
 * client->server:
 *   auth: command=0, len=x > 32, payload:
 *     32B key
 *     x-32B supported encodings, see below
 *
 *   mouse-event: command=5, len=29, payload:
 *     4B window id
 *     4B event id
 *     4B modifiers
 *     4B x
 *     4B y
 *     4B clickCount
 *     1B popupTrigger
 *     4B button
 *
 *   key-event: command=6, len=22, payload:
 *     4B window id
 *     4B event id
 *     4B modifiers
 *     4B keyCode
 *     2B keyChar
 *     4B keyLocation
 *
 *  window-resize: command=7, len=12, payload:
 *     4B window id
 *     4B width
 *     4B height
 *
 *  window-close: command=8, len=4, payload:
 *     4B window id
 *
 * Server->client 
 *   window-added: command=1, len=25, payload:
 *     4B window id
 *     4B parent id
 *     4B hint xpos
 *     4B hint ypos
 *     4B width
 *     4B height
 *     1B flags
 *   
 *   window-resized: command=2, len=12, payload:
 *     4B window id
 *     4B width
 *     4B height
 *
 *   window-removed: command=3, len=4, payload:
 *     4B window id
 *
 *   content-changed: command=4, len=var, payload:
 *     4B window id
 *     1B encoding
 *     x-1B encoding-specific data
 *
 *   also commands 5 and 6
 *
 * Encodings:
 *  0: plain RGB:
 *    2B xpos
 *    2B ypos
 *    2B width
 *    2B height
 *    width*height*3 Rbyte,Gbyte,Bbyte
 *
 * 1: 332reduced RGB:
 *    2B xpos
 *    2B ypos
 *    2B width
 *    2B height
 *    width*height pixel[3bitR|3bitG|2bitB]
 *
 *
 * @author  nenik
 */
class Packet {

    private static final int[] SIZES = new int[] {
        -1, 5+25, 5+12, 5+4, -1, 5+29, 5+22, 5+12, 5+4
    };
        
    
    public static final byte COMMAND_AUTH = 0;
    public static final byte COMMAND_WINDOW_ADDED = 1;
    public static final byte COMMAND_WINDOW_RESIZED = 2;
    public static final byte COMMAND_WINDOW_REMOVED = 3;
    public static final byte COMMAND_CONTENT_CHANGED = 4;
    public static final byte COMMAND_MOUSE_EVENT = 5;
    public static final byte COMMAND_KEY_EVENT = 6;
    public static final byte COMMAND_WINDOW_RESIZE = 7;
    public static final byte COMMAND_WINDOW_CLOSE = 8;
    
    private byte[] data;
    private int offset;

    public byte getCommand() {
        return data[4];
    }
    
    public int getPayloadLength() {
        assert offset == data.length; // packet fully filled

        return offset - 5;
    }
    
    byte[] getBytes() {
        assert offset == data.length; // packet fully filled
        
        return data;
    }
    
    /** Creates a new instance of Packet, fills the header */
    private Packet(byte[] content, byte command) {
        data = content;
        fillInteger(content.length-5).
        fillByte(command);
    }
    
    private Packet(byte command) {
        assert command >= COMMAND_AUTH && command <= COMMAND_WINDOW_CLOSE;
        assert SIZES[command] >= 5;
        
        int len = SIZES[command];
        data = new byte[len];
        fillInteger(len-5).
        fillByte(command);
    }
    
    private Packet(byte[] content) {
        data = content;
        offset = data.length;
    }
    
    private Packet fillInteger(int value) {
        data[offset++] = (byte)(value);
        data[offset++] = (byte)(value >> 8);
        data[offset++] = (byte)(value >> 16);
        data[offset++] = (byte)(value >> 24);
        return this;
    }

    private Packet fillChar(char value) {
        data[offset++] = (byte)(value);
        data[offset++] = (byte)(value >> 8);
        return this;
    }

    private Packet fillByte(byte value) {
        data[offset++] = value;
        return this;
    }

    private Packet fillBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, data, offset, bytes.length);
        offset += bytes.length;
        return this;
    }
    
    private int readInteger(int off) {
        return ((int)data[off++]) & 0xFF |
             ((((int)data[off++]) & 0xFF) << 8) |
             ((((int)data[off++]) & 0xFF) << 16) |
             ((((int)data[off]) & 0xFF) << 24);
    }

    private char readChar(int off) {
        return (char)(((char)data[off++]) & 0xFF |
             ((((char)data[off++]) & 0xFF) << 8));
    }
    /* client->server:
     *   content-changed: command=4, len=var, payload:
     *     1B encoding
     *     x-1B encoding-specific data
     *
     *   also commands 5 and 6
     */
    
    /**
     * Creates an auth packet from given key and set of encodings.
     * Format: Header(0), 32B key, nB encodings
     */
    public static Packet createAuthPacket(byte[] key, byte[] encodings) {
        if (key.length != 32) throw new IllegalArgumentException("Wrong key");
        if (encodings.length < 1) throw new IllegalArgumentException("no encodings specified");
        
        byte[] content = new byte[5+32+encodings.length];
        return new Packet(content, COMMAND_AUTH).
        fillBytes(key).
        fillBytes(encodings);
    }
    
    /**
     * Creates a window-added packet from given bounds.
     * Format: Header(1), 4B winID, 4B xpos, 4B ypos, 4B width, 4B height, 1B flags
     */
    public static Packet createWindowAddedPacket(int id, int parent, Rectangle bounds, byte flags) {
        return new Packet(COMMAND_WINDOW_ADDED).
        fillInteger(id).
        fillInteger(parent).
        fillInteger(bounds.x).
        fillInteger(bounds.y).
        fillInteger(bounds.width).
        fillInteger(bounds.height).
        fillByte(flags);
    }
    
    /** valid for all window based commands */
    public int getWindowId() {
        return readInteger(5);
    }
    
    /** valid for all window based commands */
    public int getParentId() {
        assert getCommand() == COMMAND_WINDOW_ADDED;

        return readInteger(9);
    }
    
    /** valid only for window-added packet*/
    public Rectangle getWindowRectangle() {
        assert getCommand() == COMMAND_WINDOW_ADDED;
        
        return new Rectangle(
            readInteger(13),
            readInteger(17),
            readInteger(21),
            readInteger(25)
        );
    }

    /** valid only for window-added packet*/
    public byte getWindowFlags() {
        assert getCommand() == COMMAND_WINDOW_ADDED;
        
        return data[29];
    }
    
    /**
     * Creates a window-reized packet from given dimension.
     * Format: Header(2), 4B winID, 4B width, 4B height
     */
    public static Packet createWindowResizedPacket(int id, Dimension size) {
        return new Packet(COMMAND_WINDOW_RESIZED).
        fillInteger(id).
        fillInteger(size.width).
        fillInteger(size.height);
    }

    /** valid only for window-resize and window-resized packets*/
    public Dimension getWindowDimension() {
        assert getCommand() == COMMAND_WINDOW_RESIZE || getCommand() == COMMAND_WINDOW_RESIZED;
        
        return new Dimension(
            readInteger(9),
            readInteger(13)
        );
    }

    /**
     * Creates a window-removed packet for given window id.
     * Format: Header(3), 4B winID
     */
    public static Packet createWindowRemovedPacket(int id) {
        return new Packet(COMMAND_WINDOW_REMOVED).
        fillInteger(id);
    }

    /*   content-changed: command=4, len=var, payload:
     *     4B window id
     *     1B encoding
     *     x-1B encoding-specific data
     */

    /**
     * Creates a mouse-event packet from given MouseEvent.
     * Format: Header(5), 4B winID, 4B evtID, 4B modifiers, 4B x, 4B y,
     *   4B clickCount, 1B popup trigger, 4B button
     */
    public static Packet createMouseEventPacket(int id, MouseEvent evt) {
        return new Packet(COMMAND_MOUSE_EVENT).
        fillInteger(id).
        fillInteger(evt.getID()).
        fillInteger(evt.getModifiers()).
        fillInteger(evt.getX()).
        fillInteger(evt.getY()).
        fillInteger(evt.getClickCount()).
        fillByte(evt.isPopupTrigger() ? (byte)1 : (byte)0).
        fillInteger(evt.getButton());
    }
    
    /** valid only for mouse-event packets*/
    public MouseEvent getMouseEvent(Component comp) {
        assert getCommand() == COMMAND_MOUSE_EVENT;

        return new MouseEvent(comp,
            readInteger(9),
            System.currentTimeMillis(),
            readInteger(13),
            readInteger(17), // x
            readInteger(21), // y
            readInteger(25), // clickCount
            data[29] != 0, // popupTrigger
            readInteger(30) // button
        );
    }
    
    /**
     * Creates a key-event packet from given KeyEvent.
     * Format: Header(6), 4B winID, 4B evtID, 4B modifiers, 4B code, 2B char,
     *   4B location
     */
    public static Packet createKeyEventPacket(int id, KeyEvent evt) {
        byte[] content = new byte[5+22];
        return new Packet(content, COMMAND_KEY_EVENT).
        fillInteger(id).
        fillInteger(evt.getID()).
        fillInteger(evt.getModifiers()).
        fillInteger(evt.getKeyCode()).
        fillChar(evt.getKeyChar()).
        fillInteger(evt.getKeyLocation());
    }

    /** valid only for key-event packets*/
    public KeyEvent getKeyEvent(Component comp) {
        assert getCommand() == COMMAND_KEY_EVENT;
//            final KeyEvent evt = new KeyEvent(comp, id, System.currentTimeMillis(),
//                    modifiers, keyCode, keyChar, keyLocation);

        return new KeyEvent(comp,
            readInteger(9), // id
            System.currentTimeMillis(),
            readInteger(13), // modifiers
            readInteger(17), // keyCode
            readChar(21), // keyChar
            readInteger(23) // keyLocation
        );
    }
    
     /**
     * Creates a window-reize request packet from given target dimension.
     * Format: Header(7), 4B winID, 4B width, 4B height
     */
    public static Packet createWindowResizePacket(int id, Dimension size) {
        return new Packet(COMMAND_WINDOW_RESIZE).
        fillInteger(id).
        fillInteger(size.width).
        fillInteger(size.height);
    }

    /*
     *  window-close: command=8, len=4, payload:
     *     4B window id
     *
    /**
     * Creates a window-close request packet for given id.
     * Format: Header(8), 4B winID
     */
    public static Packet createWindowClosePacket(int id) {
        return new Packet(COMMAND_WINDOW_RESIZED).
        fillInteger(id);
    }
    
    public static Packet createFromData(byte[] data) {
        return new Packet(data);
    }
}
