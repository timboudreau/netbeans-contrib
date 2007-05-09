/*
 * ListDragListener.java
 * 
 * Created on Apr 16, 2007, 9:47:49 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JList;
import org.netbeans.modules.javanavigators.CellRenderer;

/**
 * Allows class members to be reordered by dragging when
 * in position-sort mode.
 *
 * @author Tim Boudreau
 */
class ListDragListener implements MouseListener, MouseMotionListener {
    final JList list;
    public ListDragListener(JList list) {
        this.list = list;
        list.addMouseListener (this);
        list.addMouseMotionListener (this);
    }
    
    private boolean enabled = true;
    protected Description draggingElement;
    void setEnabled (boolean val) {
        if (val != enabled) {
            enabled = val;
            draggingElement = null;
            lastDropRow = -1;
            list.repaint();
        }
    }
    
    boolean active(MouseEvent evt) {
        boolean result = enabled &&
                evt.getSource() == list &&
                (list.getModel() instanceof AsynchListModel)
                && list.getModel().getSize() > 1 &&
                ((GenerifiedListModel) list.getModel()).getComparator() 
                == Description.POSITION_COMPARATOR;
        if (result) {
            Description d = descriptionFor (evt);
            result &= d != null && d.fileObject != null && //fo may be null in unit tests
                    d.fileObject.isValid() && 
                    d.fileObject.canWrite();
        }
        return result;
    }

    private Description descriptionFor (MouseEvent evt) {
        Point p = evt.getPoint();
        int row = list.locationToIndex(p);
        if (row >= 0 && row < list.getModel().getSize()) {
            Object o = list.getModel().getElementAt(row);
            if (o instanceof Description) {
                return ((Description) o);
            }
        }
        return null;
    }

    public void mousePressed(MouseEvent evt) {
        if (active(evt)) {
            Description d = descriptionFor (evt);
            int row = list.locationToIndex (evt.getPoint());
            if (d != null) {
                dragStart (d, row);
            }
        }
    }
    
    private void dragStart (Description d, int row) {
        draggingElement = d;
        CellRenderer r = (CellRenderer) list.getCellRenderer();
//        r.setDraggingIndex(row);
    }

    public void mouseReleased(MouseEvent evt) {
        if (active(evt)) {
            Description d = descriptionFor (evt);
            if (d != null && draggingElement != null && draggingElement != d) {
                dragEnd (d, evt);
            } else if (draggingElement != null) {
                dragEnd (null, evt);
            }
        } else {
            dragEnd (null, evt);
        }
    }
    
    private void dragEnd (Description d, MouseEvent evt) {
        try {
            if (d != null) {
                repositionElementRelativeTo (d, above(evt));
            }
        } finally {
            draggingElement = null;
            setLastDropRow(-1, false);
            repaintCell (lastDropRow);
            repaintCell (list.getSelectedIndex());
            CellRenderer r = (CellRenderer) list.getCellRenderer();
            int ix = r.getDraggingIndex();
            r.setDraggingIndex(-1);
            repaintCell (ix);
            r.setDraggingIndex(-1);
        }
    }
    
    private boolean above (MouseEvent evt) {
        Point p = evt.getPoint();
        int row = list.locationToIndex(p);
        Rectangle cell = list.getCellBounds(row, row);
        return cell == null ? false : p.y < cell.y + (cell.getHeight() / 2);
    }
    
    protected void repositionElementRelativeTo (Description d, boolean above) {
        list.setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            new MemberRepositioner (draggingElement, d, above).go();
        } finally {
            list.setCursor (Cursor.getPredefinedCursor(
                    Cursor.DEFAULT_CURSOR));
        }
    }

    int lastDropRow = -1;
    public void mouseDragged(MouseEvent evt) {
        Point p = evt.getPoint();
        int row = list.locationToIndex(p);
        Description desc = descriptionFor (evt);
        if (desc != draggingElement && desc != null) {
            setLastDropRow (row, above(evt));
        } else {
            setLastDropRow (-1, above(evt));
        }
    }
    
    private void repaintCell (int ix) {
        if (ix  >= 0) {
            Rectangle cell = list.getCellBounds(ix, ix);
            if (cell != null) {
                list.repaint (cell.x, cell.y, cell.width, 
                        cell.height);
            }
        }
    }
    
    private void setLastDropRow (int val, boolean above) {
        int old = lastDropRow;
        lastDropRow = val;
        CellRenderer ren = (CellRenderer) list.getCellRenderer();
        ren.setDraggingIndex(val);
//        if (old != val) {
            ren.setDropFeedbackIndex (lastDropRow);
            
            ren.setBorderMode(above ? ren.BORDER_ABOVE : ren.BORDER_BELOW); //XXX calc rect pos
            
            repaintCell (lastDropRow);
            repaintCell (old);
//        }
        if (val == -1) {
            list.repaint();
            ren.setBorderMode(ren.BORDER_NONE);
            ren.setDropFeedbackIndex(-1);
        }
    }

    public void mouseMoved(MouseEvent evt) {}
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}
    public void mouseClicked(MouseEvent evt) {}
}
