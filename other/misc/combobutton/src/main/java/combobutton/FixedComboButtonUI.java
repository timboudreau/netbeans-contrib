/*
 * FixedComboButtonUI.java
 *
 * Created on August 29, 2006, 8:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package combobutton;

import combobutton.ComboButton.IconProvider;
import combobutton.ComboButtonUI.CP;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.ComboPopup;

/**
 * UI Delegate for a Mozilla-style button which will have
 * @author Tim Boudreau
 */
class FixedComboButtonUI extends ComboButtonUI implements IconProvider, MouseListener, ActionListener {
    public FixedComboButtonUI(FixedComboButton box) {
        super (box);
    }
    
    public static ComponentUI createUI (JComponent jc) {
        return new FixedComboButtonUI ((FixedComboButton) jc);
    }
    
    public void installUI( JComponent c ) {
        super.installUI(c);
        box.setIconProvider(this);
        box.removeMouseListener(popupMouseListener);
        box.addMouseListener (this);
        //Workaround for the JDK's hacky workaround for a bug 4168483 which
        //is still unfixed since 1998...unfortunately we trade the popup not
        //working at all for showing the popup causing focus to go to null :-/
        box.setLightWeightPopupEnabled(false);
    }
    
    void itemSelected (Action action) {
        ActionEvent ae = new ActionEvent (box, ActionEvent.ACTION_PERFORMED, 
                (String) action.getValue(Action.ACTION_COMMAND_KEY));
        action.actionPerformed(ae);
        ((FixedComboButton)box).fireAction(action);
    }
    
    protected ComboPopup createPopup() {
        CP result = new CP (box);
        result.addPopupMenuListener(this);
        return result;
    }

    public Icon getIcon(Object objectInModel, int index) {
        return (Icon) 
                ((Action) objectInModel).getValue(Action.SMALL_ICON);
    }

    protected RendererButton createRendererButton() {
        return new DownArrowRendererButton();
    }

    protected void configureButtonModel (boolean isSelected, boolean isLeadSelection, boolean isComboBoxItself, boolean hasFocus, boolean isPopupVisible) {
        if (isComboBoxItself && armed) {
            hasFocus = true;
            isLeadSelection = true;
        }
        isSelected |= mousePressed && isComboBoxItself;
        isLeadSelection |= mousePressed && isComboBoxItself;
        hasFocus |= mousePressed && isComboBoxItself;
        isPopupVisible |= mousePressed && isComboBoxItself;
        super.configureButtonModel(isSelected, isLeadSelection, isComboBoxItself, hasFocus, isPopupVisible);
        ((DownArrowRendererButton) rendererButton).setComboButtonVisible (isComboBoxItself);
    }
    
    boolean shouldForwardEvent (MouseEvent e) {
        if (box.isPopupVisible()) {
            return true;
        }
        Rectangle bds = box.getBounds();
        bds.x = 0;
        bds.y = 0;
        rendererButton.setBounds (bds);
        return ((DownArrowRendererButton) rendererButton).isInDownArrowBounds(e.getPoint());
    }

    public void mouseClicked(MouseEvent e) {
        if (shouldForwardEvent(e)) {
            popupMouseListener.mouseClicked(e);
        } else {
            if (!e.isPopupTrigger() && e.getClickCount() == 1 && !box.isPopupVisible()) {
                Action action = ((FixedComboButton) box).getPrimaryAction();
                action.actionPerformed(new ActionEvent (box,
                        ActionEvent.ACTION_PERFORMED, (String) 
                        action.getValue(
                        Action.ACTION_COMMAND_KEY), e.getWhen(),
                        e.getModifiers()));
                ((FixedComboButton) box).fireAction(action);
            }
        }
    }

    boolean mousePressed;
    MouseEvent pressEvent;
    public void mousePressed(MouseEvent e) {
        if (shouldForwardEvent(e)) {
            popupMouseListener.mousePressed(e);
        } else {
            mousePressed = true;
            pressEvent = e;
            startTimer();
            box.repaint();
        }
    }
    
    private Timer timer;
    private void startTimer() {
        if (timer != null) {
            timer.restart();
        } else {
            timer = new Timer (350, this);
            timer.start();
        }
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
        if (shouldForwardEvent(e)) {
            popupMouseListener.mouseReleased(e);
        } else {
            mousePressed = false;
            pressEvent = null;
            stopTimer();
            box.repaint();
        }
    }

    boolean armed;
    public void mouseEntered(MouseEvent e) {
        armed = true;
        box.repaint();
    }

    public void mouseExited(MouseEvent e) {
        armed = false;
        box.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (pressEvent != null) {
            popupMouseListener.mousePressed(pressEvent);
            pressEvent = null;
            stopTimer();
        }
    }

    private static final class ArrowIcon implements Icon {
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int h = c.getHeight();
            int w = c.getWidth();
            Polygon p = new Polygon ( 
                new int[] { x, x + getIconWidth(), x + (getIconWidth() / 2)},
                new int[] { y, y, y + getIconHeight() },
                3
            );
            Graphics2D gg = (Graphics2D) g;
            gg.setColor (Color.BLACK);
            gg.draw (p);
            gg.fill (p);
        }

        public int getIconWidth() {
            return 9;
        }

        public int getIconHeight() {
            return 9;
        }
    }
    
    private static class DownArrowRendererButton extends RendererButton {
        private Icon icon = new ArrowIcon();
        int gap = 7;
        private boolean comboButtonVisible;
        public void paintComponent(Graphics g) {
            super.paintComponent (g);
            if (comboButtonVisible) {
                int left = getWidth() - (icon.getIconWidth() + gap);
                int top = (getHeight() / 2) - (icon.getIconHeight() / 2);
                Color c = UIManager.getColor ("controlShadow"); //NOI18N
                g.setColor (c);
                g.drawLine (left - 2, 1, left - 2, getHeight()- 2);
                g.setColor (UIManager.getColor("control")); //NOI18N
                g.drawLine (left - 1, 1, left - 1, getHeight()-2);
                g.setColor (c);
                g.drawLine (left, 1, left, getHeight() - 2);
                left += 2;
                
                icon.paintIcon(this, g, left, top);
            }
        }
        
        public boolean isInDownArrowBounds (Point p) {
            return p.x > getWidth() - (icon.getIconWidth() + gap);
        }
        
        public void setComboButtonVisible (boolean val) {
            this.comboButtonVisible = val;
        }
    }
}
