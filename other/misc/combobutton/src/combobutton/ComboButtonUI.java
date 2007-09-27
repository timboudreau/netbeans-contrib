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
 */
package combobutton;

import combobutton.ComboButton.IconProvider;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * UI Delegate for a combo box button - a combo box which looks like a 
 * JButton.
 *
 * @author Tim Boudreau
 */
class ComboButtonUI extends BasicComboBoxUI implements ListCellRenderer, PopupMenuListener, KeyListener {
    RendererButton rendererButton;
    final ComboButton box;
    public ComboButtonUI(ComboButton box) {
        this.box = box;
    }
    
    protected JButton createArrowButton() {
        return null;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new ComboButtonUI((ComboButton) c);
    }
    
    public void installUI( JComponent c ) {
        rendererButton = createRendererButton();
        super.installUI(c);
        c.setBorder (BorderFactory.createEmptyBorder());
        box.removeKeyListener (superHandler);
    }
    
    protected RendererButton createRendererButton() {
         return new RendererButton();
    }
    
    public void uninstallUI( JComponent c ) {
        super.uninstallUI(c);
        c.removeKeyListener(this);
    }
    
    protected ListCellRenderer createRenderer() {
        return this;
    }
    
    private boolean isWindowsLAF() {
        return UIManager.getLookAndFeel().getID().indexOf("Windows") >= 0; //NOI18N
    }
    
    protected void configureButtonModel (boolean isSelected, boolean isLeadSelection, boolean isComboBoxItself, boolean hasFocus, boolean isPopupVisible) {
        if (isWindowsLAF()) {
            isPopupVisible |= isPendingPopup();
            boolean pressed = isComboBoxItself ? isPopupVisible : isSelected;
            rendererButton.getModel().setArmed(pressed);
            rendererButton.getModel().setRollover(pressed && isComboBoxItself);
            rendererButton.getModel().setPressed(pressed);
        } else {
            rendererButton.getModel().setArmed(isSelected && !isComboBoxItself);
            rendererButton.getModel().setPressed (isLeadSelection && !isComboBoxItself);
        }
        rendererButton.setHasFocus(isComboBoxItself && hasFocus);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = value == null || 
                (!box.isTextVisible() && index == -1) ? "" : value.toString(); //NOI18N
        
        rendererButton.setText (text);
        boolean isComboBoxItself = index == -1;
        configureButtonModel (isSelected, cellHasFocus, isComboBoxItself, box.hasFocus(), box.isPopupVisible());
        IconProvider provider = box.getIconProvider();
        if (provider != null) {
            rendererButton.setIcon (provider.getIcon (value, index));
        } else {
            rendererButton.setIcon(null);
        }
        return rendererButton;
    }
    
    protected void superInstallComponents() {
        super.installComponents();
    }
    
    protected void installComponents() {
        comboBox.add( currentValuePane );
    }
    
    protected Rectangle superRectangleForCurrentValue() {
        return super.rectangleForCurrentValue();
    }
    
    protected Rectangle rectangleForCurrentValue() {
        Rectangle result = box.getBounds();
        result.x = 0;
        result.y = 0;
        return result;
    }

//    public Dimension getPreferredSize(JComponent c) {
//        Dimension result = super.getPreferredSize(c);
//        if (!box.isTextVisible()) {
//            Object item = box.getSelectedItem();
//            
//            Component comp = 
//                    box.getRenderer().getListCellRendererComponent(
//                    null, item == null ? "     " : item, 0, false, false);
//            currentValuePane.add (comp);
//            result.setSize(comp.getPreferredSize());
//            currentValuePane.remove(comp);
//        }
//        return result;
//    }

    public void setPopupVisible(final JComboBox c, boolean v) {
        super.setPopupVisible(c, v);
        box.repaint();
    }
    
    protected ComboPopup createPopup() {
        CP result = new CP (box);
        result.addPopupMenuListener(this);
        return result;
    }
    
    boolean isPendingPopup() {
        boolean result = isPendingPopup;
        isPendingPopup = false;
        return result;
    }
    
    private int popupCount = 0;
    private boolean isPendingPopup;
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        //Bizarrely, on both JDK 5 & 6, if repaint() is called just as
        //the popup is being shown, focus will be sent to null and
        //the popup will immediately disappear, but only the first
        //time the popup is ever shown.
        isPendingPopup = true;
        popupCount++;
        if (popupCount == 1) {
            //However, paintImmediately has no such problem, but 
            //the very first time the renderer button will not appear
            //pressed, 
            box.paintImmediately(0, 0, box.getWidth(), box.getHeight());
        } else {
            box.repaint();
        }
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        box.repaint();
        isPendingPopup = false;
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        boolean isArrow = code == KeyEvent.VK_UP || 
                code == KeyEvent.VK_DOWN;
        boolean isAccept = code == KeyEvent.VK_ENTER;
        boolean isTogglePopup = isAccept || code == KeyEvent.VK_SPACE;
        boolean isPopupVisible = popup.isVisible();
        if (code == KeyEvent.VK_ESCAPE) {
            setPopupVisible (box, false);
        } else if (isArrow && !isPopupVisible) {
            superHandler.keyPressed(e);
        } else {
            if (isArrow && isPopupVisible) {
                int max = popup.getList().getModel().getSize();
                if (max > 0) {
                    int ix = popup.getList().getSelectedIndex();
                    ix += code == KeyEvent.VK_UP ? -1 : 1;
                    if (ix < 0) {
                        ix = max - 1;
                    } else if (ix == max) {
                        ix = 0;
                    }
                    popup.getList().setSelectedIndex(ix);
                }
            } else if (isAccept && isPopupVisible) {
                box.setSelectedItem(popup.getList().getSelectedValue());
                setPopupVisible(box, false);
            } else if (isTogglePopup) {
                setPopupVisible (box, !box.isPopupVisible());
            } else {
                superHandler.keyPressed(e);
            }
        }
        if (e.getModifiersEx() == 0) {
            //Otherwise something sends focus to null for arrow keys and
            //such
            e.consume();
        }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        boolean isArrow = code == KeyEvent.VK_UP || 
                code == KeyEvent.VK_DOWN;
        boolean isAccept = code == KeyEvent.VK_ENTER;
        boolean isTogglePopup = isAccept || code == KeyEvent.VK_SPACE;
        boolean isPopupVisible = popup.isVisible();
        if (isArrow && !isPopupVisible) {
            int ix = box.getSelectedIndex();
            ix += code == KeyEvent.VK_UP ? -1 : 1;
            int max = box.getModel().getSize();
            if (max > 0) {
                if (ix < 0) {
                    ix = max - 1;
                } else if (ix >= max) {
                    ix = 0;
                }
                box.setSelectedIndex(ix);
            }
        }
        if (e.getModifiersEx() == 0) {
            //Otherwise something sends focus to null for arrow keys and
            //such
            e.consume();
        }
    }

    KeyListener superHandler;
    protected KeyListener createKeyListener() {
        superHandler = super.createKeyListener();
        return this;
    }
    
    static class RendererButton extends JToggleButton {
        public RendererButton () {
            setRolloverEnabled (true);
        }
        boolean hasFocus;
        public boolean hasFocus() {
            return hasFocus;
        }
        
        void setHasFocus (boolean val) {
            hasFocus = val;
        }
        public void validate() {}
        public void invalidate() {}
        public void revalidate() {}
        public void repaint() {}
        public void firePropertyChange (String nm, Object old, Object nue) {}
    }
    
    static final class CP extends BasicComboPopup {
        CP (JComboBox box) {
            super (box);
        }
        
        protected Rectangle computePopupBounds(int px,int py,int pw,int ph) {
            Dimension d = getList().getPreferredSize();
            Rectangle result = super.computePopupBounds(px, py, d.width, 
                    d.height);
            int dif = (comboBox.getWidth() - result.width) -
                    comboBox.getInsets().right;
            if (dif > 0) {
                result.x += dif;
            }
            Point loc = result.getLocation();
            SwingUtilities.convertPointToScreen(loc, comboBox);
            Rectangle screen = comboBox.getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds();
            Rectangle test = new Rectangle (loc.x, loc.y, result.width, result.height);
            if (!screen.contains(test)) {
                test = screen.intersection (test);
                result.width = test.width;
                result.height = test.height;
            }
            return result;
        }

        protected void configureScroller() {
            super.configureScroller();
            this.scroller.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            this.scroller.setHorizontalScrollBarPolicy (
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
    }
}
