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

package org.netbeans.modules.tasklist.usertasks.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 * Clickable Link
 * 
 * Initial source code from
 * http://www.java2s.com/Code/Java/Swing-Components/TreeTable.htm
 * 
 * @author tl 
 */
public class JLinkButton extends JButton {
    private static final String uiString = "LinkButtonUI";
    
    /** the text will always be underlined */
    public static final int ALWAYS_UNDERLINE = 0;
    
    /** the text will only be underlined on mouse over. */
    public static final int HOVER_UNDERLINE = 1;
    
    /** never underline the text. */
    public static final int NEVER_UNDERLINE = 2;

    /**
     * Registers default UI class for JLinkButton.
     */
    public static void registerUI() {
        UIManager.getDefaults().put("LinkButtonUI", "BasicLinkButtonUI");
    }
    
    private int linkBehavior;
    private Color linkColor;
    private Color colorPressed;
    private Color visitedLinkColor;
    private Color disabledLinkColor;
    private URL buttonURL;
    private boolean isLinkVisited;

    /**
     * Empty link. 
     */
    public JLinkButton() {
        this(null, null, null);
    }
    
    /**
     * Constructor.
     * 
     * @param action todo? 
     */
    public JLinkButton(Action action) {
        this();
        setAction(action);
    }
    
    /**
     * Constructor.
     * 
     * @param icon shows an icon. 
     */
    public JLinkButton(Icon icon) {
        this(null, icon, null);
    }
    
    /**
     * Constructor.
     * 
     * @param s text of the button 
     */
    public JLinkButton(String s) {
        this(s, null, null);
    }
    
    /**
     * Constructor.
     * 
     * @param url target URL 
     */
    public JLinkButton(URL url) {
        this(null, null, url);
    }
    
    /**
     * Constructor.
     * 
     * @param s text
     * @param url target url 
     */
    public JLinkButton(String s, URL url) {
        this(s, null, url);
    }
    
    /**
     * Constructor.
     * 
     * @param icon an icon
     * @param url target url 
     */
    public JLinkButton(Icon icon, URL url) {
        this(null, icon, url);
    }

    /**
     * Constructor.
     * 
     * @param text text of the button
     * @param icon an icon
     * @param url target url
     */ 
    public JLinkButton(String text, Icon icon, URL url) {
        super(text, icon);
        linkBehavior = ALWAYS_UNDERLINE;
        linkColor = Color.blue;
        colorPressed = Color.red;
        visitedLinkColor = new Color(128, 0, 128);
        if (text == null && url != null)
            setText(url.toExternalForm());
        setLinkURL(url);
        setCursor(Cursor.getPredefinedCursor(12));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setMargin(new Insets(0, 0, 0, 0));
    }
    
    public void updateUI() {
        setUI(BasicLinkButtonUI.createUI(this));
    }
    
    
    public String getUIClassID() {
        return "LinkButtonUI";
    }
    
    /**
     * Setups the text for the tooltip. 
     */
    private void setupToolTipText() {
        String tip = null;
        if (buttonURL != null)
            tip = buttonURL.toExternalForm();
        setToolTipText(tip);
    }
    
    /**
     * Sets the behaviour.
     * 
     * @param bnew one of the *_UNDERLINE constants from this class. 
     */
    public void setLinkBehavior(int bnew) {
        if (bnew != ALWAYS_UNDERLINE && bnew != HOVER_UNDERLINE
                && bnew != NEVER_UNDERLINE)
            throw new IllegalArgumentException("Not a legal LinkBehavior");

        int old = linkBehavior;
        linkBehavior = bnew;
        firePropertyChange("linkBehavior", old, bnew);
        repaint();
    }
    
    /**
     * Returns the current behaviour.
     * 
     * @return one of the *_UNDERLINE constants from this class. 
     */
    public int getLinkBehavior() {
        return linkBehavior;
    }
    
    /**
     * Sets the color of the link.
     * 
     * @param color new color 
     */
    public void setLinkColor(Color color) {
        Color colorOld = linkColor;
        linkColor = color;
        firePropertyChange("linkColor", colorOld, color);
        repaint();
    }

    /**
     * Returns the color of the link.
     * 
     * @return color 
     */
    public Color getLinkColor() {
        return linkColor;
    }
    
    /**
     * Sets the color of the link used when the mouse is over it. 
     * 
     * @param colorNew new color
     */
    public void setActiveLinkColor(Color colorNew) {
        Color colorOld = colorPressed;
        colorPressed = colorNew;
        firePropertyChange("activeLinkColor", colorOld, colorNew);
        repaint();
    }
    
    /**
     * Returns the color of the link when the mouse is over it.
     * 
     * @return color 
     */
    public Color getActiveLinkColor() {
        return colorPressed;
    }
    
    /**
     * Sets disabled color.
     * 
     * @param color new color 
     */
    public void setDisabledLinkColor(Color color) {
        Color colorOld = disabledLinkColor;
        disabledLinkColor = color;
        firePropertyChange("disabledLinkColor", colorOld, color);
        if (!isEnabled())
            repaint();
    }
    
    /**
     * Returns disabled color.
     * 
     * @return disabled color 
     */
    public Color getDisabledLinkColor() {
        return disabledLinkColor;
    }
    
    /**
     * Sets color for the visited state.
     * 
     * @param colorNew new color 
     */
    public void setVisitedLinkColor(Color colorNew) {
        Color colorOld = visitedLinkColor;
        visitedLinkColor = colorNew;
        firePropertyChange("visitedLinkColor", colorOld, colorNew);
        repaint();
    }
    
    /**
     * Returns color for the visited state.
     * 
     * @return color 
     */
    public Color getVisitedLinkColor() {
        return visitedLinkColor;
    }
    
    /**
     * Returns the URL.
     * 
     * @return URL 
     */
    public URL getLinkURL() {
        return buttonURL;
    }
    
    /**
     * Sets new URL.
     * 
     * @param url new URL 
     */
    public void setLinkURL(URL url) {
        URL urlOld = buttonURL;
        buttonURL = url;
        setupToolTipText();
        firePropertyChange("linkURL", urlOld, url);
        revalidate();
        repaint();
    }
    
    /**
     * Sets the visited flag.
     * 
     * @param flagNew new value for the visited flag 
     */
    public void setLinkVisited(boolean flagNew) {
        boolean flagOld = isLinkVisited;
        isLinkVisited = flagNew;
        firePropertyChange("linkVisited", flagOld, flagNew);
        repaint();
    }
    
    /**
     * Checks whether the link is visited.
     * 
     * @return true = visited. 
     */
    public boolean isLinkVisited() {
        return isLinkVisited;
    }
    
    protected String paramString() {
        String str;
        if (linkBehavior == ALWAYS_UNDERLINE)
            str = "ALWAYS_UNDERLINE";
        else if (linkBehavior == HOVER_UNDERLINE)
            str = "HOVER_UNDERLINE";
        else if (linkBehavior == NEVER_UNDERLINE)
            str = "NEVER_UNDERLINE";
        else
            str = "SYSTEM_DEFAULT";
        String colorStr = linkColor == null ? "" : linkColor.toString();
        String colorPressStr = colorPressed == null ? "" : colorPressed
                .toString();
        String disabledLinkColorStr = disabledLinkColor == null ? ""
                : disabledLinkColor.toString();
        String visitedLinkColorStr = visitedLinkColor == null ? ""
                : visitedLinkColor.toString();
        String buttonURLStr = buttonURL == null ? "" : buttonURL.toString();
        String isLinkVisitedStr = isLinkVisited ? "true" : "false";
        return super.paramString() + ",linkBehavior=" + str + ",linkURL="
                + buttonURLStr + ",linkColor=" + colorStr + ",activeLinkColor="
                + colorPressStr + ",disabledLinkColor=" + disabledLinkColorStr
                + ",visitedLinkColor=" + visitedLinkColorStr
                + ",linkvisitedString=" + isLinkVisitedStr;
    }
}

/**
 * Default UI delegate. 
 */
class BasicLinkButtonUI extends MetalButtonUI {
    private static final BasicLinkButtonUI ui = new BasicLinkButtonUI();

    /**
     * Returns the UI for the specified component.
     * 
     * @param jcomponent a JLinkButton
     * @return created UI 
     */
    public static ComponentUI createUI(JComponent jcomponent) {
        return ui;
    }
    
    protected void paintText(Graphics g, JComponent com, Rectangle rect,
            String s) {
        JLinkButton bn = (JLinkButton) com;
        ButtonModel bnModel = bn.getModel();
        Color color = bn.getForeground();
        Object obj = null;
        if (bnModel.isEnabled()) {
            if (bnModel.isPressed())
                bn.setForeground(bn.getActiveLinkColor());
            else if (bn.isLinkVisited())
                bn.setForeground(bn.getVisitedLinkColor());
            
            else
                bn.setForeground(bn.getLinkColor());
        } else {
            if (bn.getDisabledLinkColor() != null)
                bn.setForeground(bn.getDisabledLinkColor());
        }
        super.paintText(g, com, rect, s);
        int behaviour = bn.getLinkBehavior();
        boolean drawLine = false;
        if (behaviour == JLinkButton.HOVER_UNDERLINE) {
            if (bnModel.isRollover())
                drawLine = true;
        } else if (behaviour == JLinkButton.ALWAYS_UNDERLINE)
            drawLine = true;
        if (!drawLine)
            return;
        FontMetrics fm = g.getFontMetrics();
        int x = rect.x + getTextShiftOffset();
        int y = (rect.y + fm.getAscent() + fm.getDescent() + 
                getTextShiftOffset()) - 1;
        if (bnModel.isEnabled()) {
            g.setColor(bn.getForeground());
            g.drawLine(x, y, (x + rect.width) - 1, y);
        } else {
            g.setColor(bn.getBackground().brighter());
            g.drawLine(x, y, (x + rect.width) - 1, y);
        }
    }
}