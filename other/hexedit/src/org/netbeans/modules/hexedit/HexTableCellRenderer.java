/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * HexTableCellRenderer.java
 *
 * Created on April 27, 2004, 8:14 PM
 */

package org.netbeans.modules.hexedit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 *
 * @author  Tim Boudreau
 */
class HexTableCellRenderer implements TableCellRenderer, ListCellRenderer {
    private static final DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
    
    private int highlightRow = -1;
    private int highlightCol = -1;
    
    /** Creates a new instance of HexTableCellRenderer */
    public HexTableCellRenderer() {
        ren.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    
    int setHighlightRow (int row) {
        int old = highlightRow;
        highlightRow = row;
        return old;
    }
    
    int setHighlightColumn (int col) {
        int old = highlightCol;
        highlightCol = col;
        return old;
    }
    
    int colCount = 1;
    public java.awt.Component getTableCellRendererComponent(JTable jTable, Object obj, boolean sel, boolean focus, int row, int col) {
        colCount = jTable.getColumnCount();
        
        Object tip = obj;
        
        obj = Util.convertToString (obj);
        
        ren.setBackground (jTable.getBackground());
        Component result = ren.getTableCellRendererComponent (jTable, obj, sel, focus, row, col);
        if (tip == HexTableModel.PARTIAL_VALUE) {
            result.setBackground (new Color (220, 220, 220));
            return result;
        } else if (tip == null) {
            result.setBackground (new Color (204,204,204));
            return result;
        }


        if (obj == null) {
            result.setBackground(Color.LIGHT_GRAY);
            return result;
        } else if (!sel ) {
            if (row == highlightRow) {
                if (col == highlightCol) {
                    result.setBackground (hlFocus);
                } else {
                    result.setBackground (hl);
                }
            } 
        } else if (sel) {
            if (row == highlightRow && col == highlightCol) {
                result.setBackground (hlFocus);
            }
        }
//        ((JComponent) result).setToolTipText(tip == null ? null : tip instanceof String ? (String) tip : tip.toString());
        
        return result;
    }
    

    private static final Color hl = new Color (223, 223, 223);
    private static final Color hlFocus = new Color (255,255,200);
    
    private EnhListRen lren = new EnhListRen();
    public Component getListCellRendererComponent(JList jList, Object val, int idx, boolean sel, boolean focus) {
        return lren.getListCellRendererComponent (jList, val, idx, sel, focus);
    }
    
    private class EnhListRen extends JLabel implements ListCellRenderer {
        int row = -1;
        int col = -1;
        private int charWidth = -1;

        
        public void paintComponent(Graphics g) {
            if (charWidth == -1) {
                calcDims(g);
            }
            Color old = g.getColor();
            Rectangle r = new Rectangle();
            r.width = getWidth();
            r.height = getHeight();
            r.x = 0;
            r.y = 0;
            g.setColor (selected ? lastList.getSelectionBackground() : isHlRow ? hl : getBackground());
            g.fillRect (r.x, r.y, r.width, r.height);
            if (isHlRow && highlightCol != -1 && getText() != null && getText().length() > 0) {
                
                int charCount = getText().length();
                int charsPerCol = charCount / colCount;
                r.x = highlightCol * charsPerCol * charWidth;
                
                r.width = charsPerCol * charWidth;
                r.height = getHeight();
                g.setColor (hlFocus);
                g.fillRect (r.x, r.y, r.width, r.height);
                g.setColor(old);
            }
            super.paintComponent(g);
        }
        
        private void calcDims(Graphics g) {
            FontMetrics fm = g.getFontMetrics (getFont());
            charWidth = fm.charWidth('J');

        }
        
        public Component getListCellRendererComponent(JList jList, Object val, int idx, boolean sel, boolean focus) {
            if (val instanceof String) {
                setText ((String) val);
            } else {
                setText ("");
            }
            setBackground (sel ? jList.getSelectionBackground() : jList.getBackground());
            setForeground (sel ? jList.getSelectionForeground() : jList.getForeground());
            setFont (jList.getFont());
            setSelected (sel);
            setHlRow (idx == highlightRow);
            lastList = jList;
            return this;
        }
        
        boolean selected = false;
        private void setSelected (boolean val) {
            selected = val;
        }
        private boolean isHlRow = false;
        private void setHlRow (boolean val) {
            isHlRow = val;
        }
        
        private JList lastList = null;
        
        //performance overrides
        
        public void firePropertyChange (String s, Object a, Object b) {
            //do nothing
        }
        
        public void validate() {
            //do nothing
        }
        
        public void revalidate() {
            //do nothing
        }
        
        public void repaint() {
            //do nothing
        }
        
        public void repaint (long tm, int x, int y, int w, int h) {
            
        }
        
    }
    
}
