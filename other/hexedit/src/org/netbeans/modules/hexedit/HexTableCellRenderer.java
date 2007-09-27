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
* The Original Software is NetBeans. The Initial Developer of the Original
* Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
* Microsystems, Inc. All Rights Reserved.
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
