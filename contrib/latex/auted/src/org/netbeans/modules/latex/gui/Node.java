/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Jan Lahoda
 */
public abstract class Node implements Serializable {
    
    private PropertyChangeSupport pcs;
    private String id;
    
    private static Map ids = new WeakHashMap();
    
    private static boolean isUsed(String id) {
        return ids.values().contains(id);
    }
    
    private static synchronized void addId(String id) {
        ids.put(id, id);
    }
    
    private static int idCount = 0;
    private static synchronized String getUniqueId() {
        int id = idCount++;
        String sid = null;
        
        while (isUsed(sid = "__SYSTEMID" + Integer.toString(id)))
            id = idCount++;
        
        addId(sid);
        return sid;
    }
    
    private NodeStorage storage;
    
    /** Creates a new instance of Node */
    public Node() {
        id = getUniqueId();
        pcs = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(property, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener l) {
        pcs.removePropertyChangeListener(property, l);
    }
    
    protected void firePropertyChange(String property, Object old, Object nue) {
        pcs.firePropertyChange(property, old, nue);
    }

    protected void firePropertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    public void setStorage(NodeStorage storage) {
        this.storage = storage;
    }
    
    public NodeStorage getStorage() {
        return storage;
    }
    
    protected final void redraw() {
        getStorage().redrawAll();
    }
    
    protected final void redrawMe() {
        Rectangle2D dim = getOuterDimension();
        
        redraw((int) dim.getX(), (int) dim.getY(), (int) dim.getWidth(), (int) dim.getHeight());
    }
    
    protected final void redraw(int x, int y, int w, int h) {
        getStorage().redrawAll(x, y, w, h);
    }
    
    public String getID() {
        return id;
    }
    
    public void setID(String id) {
        this.id = id;
        addId(id);
    }
    
    public abstract void draw(Graphics2D g);
    
    public abstract Rectangle2D getOuterDimension();
    
    protected String printDouble(double d) {
        NumberFormat n = NumberFormat.getNumberInstance(Locale.ENGLISH);
        
        n.setMaximumFractionDigits(2);
        return n.format(d);
    }
    public abstract void outputVaucansonSource(PrintWriter out);
    
    protected Action[] createPopupMenu() {
        return new Action[0];
    }
    
    private transient WeakReference menu;
    
    public synchronized Action[] getPopupMenu() {
        Action[] m;
        
        if (menu == null) {
            m = createPopupMenu();
            menu = new WeakReference(m);
        } else {
            m = (Action[] ) menu.get();
            
            if (m == null) {
                m = createPopupMenu();
                menu = new WeakReference(m);
            }
        }
        
        return m;
    }

    public abstract double distance(Point p);
    public abstract void remove();
    
    protected class RemoveAction extends AbstractAction {
        
        public RemoveAction() {
            super("Delete");
        }
        
        public void actionPerformed(ActionEvent e) {
            remove();
            Node.this.redraw();
        }
        
    }
    
    public boolean equalsNode(Node node) {
        return this.getClass().equals(node.getClass()) && getID().equals(node.getID());
    }
}
