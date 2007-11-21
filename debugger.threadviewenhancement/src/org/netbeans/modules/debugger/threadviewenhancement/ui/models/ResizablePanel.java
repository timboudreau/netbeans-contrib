/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.debugger.threadviewenhancement.ui.models;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class ResizablePanel extends JPanel {
    private int BORDER_WIDTH = 4;
    
    private Dimension borderDimension  = new Dimension(BORDER_WIDTH, BORDER_WIDTH);
    
    private static final Insets zi = new Insets(0,0,0,0);
    
    class WidthPanel extends JPanel {
        public Dimension getMinimumSize() {
            return borderDimension;
        }
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }
    } 
    private JPanel nwPanel    = new WidthPanel();
    private JPanel nPanel     = new WidthPanel();
    private JPanel nePanel    = new WidthPanel();
    private JPanel wPanel     = new WidthPanel();
    private JPanel ePanel     = new WidthPanel();
    private JPanel sePanel    = new WidthPanel();
    private JPanel sPanel     = new WidthPanel();
    private JPanel swPanel    = new WidthPanel();
    
    private JLabel titleLabel = new JLabel(" ");        

    /** Creates a new instance of ResizablePanel */
    public ResizablePanel(JComponent clientPane, String title) {
        setDoubleBuffered(true);
        setBorder(BorderFactory.createLineBorder(Color.black));
        
        titleLabel.setText(title);
        
        GridBagHelper gbh[] = {
//____________________________________________________________________________________________________________________________________
//                            |    |    |     |      |   |   |                            |                             |     |   |
//                  Component | col| row|     |      |wt |wt |        anchor              |            fill             |insts|pad|pad 
//                            |  x |  y |width|height| x | y |                            |                             |     | x | y
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(nwPanel
                              , 0  , 0  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(nPanel
                              , 1  , 0  , 1   , 1    , 1 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(nePanel
                              , 2  , 0  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(wPanel
                              , 0  , 1  , 1   , 2    , 0 , 1 ,GridBagConstraints.NORTHWEST,GridBagConstraints.VERTICAL  ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(titleLabel
                              , 1  , 1  , 1   , 1    , 1 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(ePanel
                              , 2  , 1  , 1   , 2    , 0 , 1 ,GridBagConstraints.NORTHWEST,GridBagConstraints.VERTICAL  ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(clientPane
                              , 1  , 2  , 1   , 1    , 1 , 1 ,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH      ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(swPanel
                              , 0  , 3  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(sPanel
                              , 1  , 3  , 1   , 1    , 1 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(sePanel
                              , 2  , 3  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      ,zi   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
};
	GridBagHelper.createGUI(this, gbh);
        
        moveResizeListener = new MoveResizeListener();
        
        nwPanel.addMouseListener(moveResizeListener);
        nwPanel.addMouseMotionListener(moveResizeListener);
        nwPanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        
        nPanel.addMouseListener(moveResizeListener);
        nPanel.addMouseMotionListener(moveResizeListener);
        nPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        
        nePanel.addMouseListener(moveResizeListener);
        nePanel.addMouseMotionListener(moveResizeListener);
        nePanel.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
        
        wPanel.addMouseListener(moveResizeListener);
        wPanel.addMouseMotionListener(moveResizeListener);
        wPanel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        
        ePanel.addMouseListener(moveResizeListener);
        ePanel.addMouseMotionListener(moveResizeListener);
        ePanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        
        swPanel.addMouseListener(moveResizeListener);
        swPanel.addMouseMotionListener(moveResizeListener);
        swPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
        
        sPanel.addMouseListener(moveResizeListener);
        sPanel.addMouseMotionListener(moveResizeListener);
        sPanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        
        sePanel.addMouseListener(moveResizeListener);
        sePanel.addMouseMotionListener(moveResizeListener);
        sePanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));

        titleLabel.addMouseListener(moveResizeListener);
        titleLabel.addMouseMotionListener(moveResizeListener);
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

     private class MoveResizeListener implements MouseListener, MouseMotionListener {
        private static final int RESIZE_NORTHWEST     = 1;
        private static final int RESIZE_NORTH         = 2;
        private static final int RESIZE_NORTHEAST     = 4;
        private static final int RESIZE_WEST          = 8;
        private static final int RESIZE_EAST          = 16;
        private static final int RESIZE_SOUTHWEST     = 32;
        private static final int RESIZE_SOUTH         = 64;
        private static final int RESIZE_SOUTHEAST     = 128;
        private static final int MOVE                 = 256;
        
        private int resizeDirection = 0;
        private int lastX = Integer.MIN_VALUE;
        private int lastY = Integer.MIN_VALUE;
        
        public void mouseReleased(java.awt.event.MouseEvent e) {
            Point point = e.getPoint();
            SwingUtilities.convertPointToScreen(point, (Component) e.getSource());
            handleDrag(point);
            resizeDirection = 0;
            lastX = Integer.MIN_VALUE;
            lastY = Integer.MIN_VALUE;
            final JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(ResizablePanel.this);
            dialog.setCursor(Cursor.getDefaultCursor());
        }
        
        public void mousePressed(java.awt.event.MouseEvent e) {
            Component component = (Component) e.getSource();
            Point point = e.getPoint();            
            SwingUtilities.convertPointToScreen(point, component);
            lastX = point.x;
            lastY = point.y;
            
            if (component == nwPanel) {
                resizeDirection = RESIZE_NORTHWEST;
            } else if (component == nPanel) {
                resizeDirection = RESIZE_NORTH;               
            } else if (component == nePanel) {
                resizeDirection = RESIZE_NORTHEAST;
            } else if (component == wPanel) {
                resizeDirection = RESIZE_WEST;
            } else if (component == ePanel) {
                resizeDirection = RESIZE_EAST;
            } else if (component == swPanel) {
                resizeDirection = RESIZE_SOUTHWEST;
            } else if (component == sPanel) {
                resizeDirection = RESIZE_SOUTH;
            } else if (component == sePanel) {
                resizeDirection = RESIZE_SOUTHEAST;
            } else if (component == titleLabel) {
                resizeDirection = MOVE;
            }
        }
        
        public void mouseMoved(java.awt.event.MouseEvent e) {
        }
        
        public void mouseExited(java.awt.event.MouseEvent e) {
        }
        
        public void mouseEntered(java.awt.event.MouseEvent e) {
        }
        
        public void mouseDragged(java.awt.event.MouseEvent e) {
            Point point = e.getPoint();
            SwingUtilities.convertPointToScreen(point, (Component) e.getSource());
            handleDrag(point);
        }
        
        public void mouseClicked(java.awt.event.MouseEvent e) {
        }
        
        private void handleDrag(Point p) {
            final JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(ResizablePanel.this);
            if (dialog == null) {
                return;
            }
            int x = p.x;
            int y = p.y;
            int deltaX = x - lastX;
            int deltaY = y - lastY;
            lastX = x;
            lastY = y;
            
            Rectangle windowBounds = dialog.getBounds();
            switch (resizeDirection) {
                case RESIZE_NORTHWEST:
                    windowBounds.x      += deltaX;
                    windowBounds.width  -= deltaX;
                    windowBounds.y      += deltaY;
                    windowBounds.height -= deltaY;
                    break;
                case RESIZE_NORTH:
                    windowBounds.y      += deltaY;
                    windowBounds.height -= deltaY;
                    break;
                case RESIZE_NORTHEAST:
                    windowBounds.width  += deltaX;
                    windowBounds.y      += deltaY;
                    windowBounds.height -= deltaY;
                    break;
                case RESIZE_WEST:
                    windowBounds.x      += deltaX;
                    windowBounds.width  -= deltaX;
                    break;
                case RESIZE_EAST:
                    windowBounds.width  += deltaX;
                    break;
                case RESIZE_SOUTHWEST:
                    windowBounds.x      += deltaX;
                    windowBounds.width  -= deltaX;
                    windowBounds.height += deltaY;
                    break;
                case RESIZE_SOUTH:
                    windowBounds.height += deltaY;
                    break;
                case RESIZE_SOUTHEAST:
                    windowBounds.width  += deltaX;
                    windowBounds.height += deltaY;
                    break;
                case MOVE:
                windowBounds.x  += deltaX;
                windowBounds.y  += deltaY;
                break;
            }
            
            dialog.setBounds(windowBounds);
            dialog.doLayout();
            ResizablePanel.this.revalidate();
        }
    }
    
    private MoveResizeListener moveResizeListener;
}