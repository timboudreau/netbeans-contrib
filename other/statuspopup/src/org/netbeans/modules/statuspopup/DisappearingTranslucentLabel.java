/*
 * DisappearingTranslucentLabel.java
 *
 * Created on September 24, 2000, 10:35 AM
 */

package org.netbeans.modules.statuspopup;

/**
 *
 * @author  Tim Boudreau
 * @version 0.1
 */

//import java.lang.Thread;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

class DisappearingTranslucentLabel extends DisappearingLabel {
    private static Insets insets = new java.awt.Insets(6,6,6,6);
    /** Creates new DisappearingTranslucentLabel */
    public DisappearingTranslucentLabel() {
        setOpaque(false);
        setBorder (BorderFactory.createEmptyBorder(10,10,10,10));
    }
    
    public void paint(final java.awt.Graphics graphics) {
        if (!getText().equals(EmptyString)) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics;
            g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.60f));
            g2.setColor (new java.awt.Color (25,15,2));
            g2.fillRect (0, 0, getWidth(), getHeight());
            g2.setColor (java.awt.Color.lightGray);
            g2.drawRect (2, 2, getWidth()-4, getHeight()-4);
            g2.drawRect (3, 3, getWidth()-6, getHeight() -6);
            g2.setColor (java.awt.Color.yellow);
            g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC, 1.0f));
            super.paint(graphics);
        }
    }
    /*
    //Only useful if we subclass JTextArea for word wrap
     
    public void processMouseEvent (MouseEvent me) {
        if (me.getID() == MouseEvent.MOUSE_PRESSED) {
            setVisible(false);
        }
        if (getParent() != null) {
            Container c = ((JComponent) getParent()).getTopLevelAncestor();
            Point p = me.getPoint();
            p = SwingUtilities.convertPoint(this, p, c);
            Component retarget = c.getComponentAt(p);
            
            if (retarget instanceof JRootPane) {
                Container contentPane = ((JRootPane) retarget).getContentPane();
                p = SwingUtilities.convertPoint(retarget, p, contentPane);
                if (contentPane == null) {
                    return;
                }
                retarget = contentPane.getComponentAt(p);
                if (retarget == null) {
                    return;
                }
            }
            
            MouseEvent nue = new MouseEvent (retarget, me.getID(), 
                me.getWhen(), me.getModifiersEx(), p.x, p.y, 
                me.getClickCount(), me.isPopupTrigger(), me.getButton());
            System.err.println("Retargeting event to " + retarget);
            retarget.dispatchEvent(nue);
        }
    }
     */
    
}
