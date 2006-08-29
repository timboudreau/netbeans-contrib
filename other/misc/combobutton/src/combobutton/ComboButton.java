/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package combobutton;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;

/**
 *
 * @author Tim Boudreau
 */
public class ComboButton extends JComboBox {
    private IconProvider iconProvider;
    
    public ComboButton() {
    }
        
    public interface IconProvider {
        /** Provides an icon to be displayed for the selected
         * object in the model
         * @param objectInModel an object in this ComboButton's 
         *   ComboBoxModel
         * @param index The index in the model, or -1 when
         *   rendering the combo box itself, not its popup
         */
        Icon getIcon (Object objectInModel, int index);
    }
    
    public void updateUI() {
        if (!(getUI() instanceof ComboButtonUI)) {
            setUI ((ComboBoxUI) ComboButtonUI.createUI(this));
        }
    }
    
    private boolean textVisible = false;
    /**
     * Set whether the selected item's text should be displayed
     * in the combo box itself (not the popup) or not.
     */ 
    public void setTextVisible (boolean val) {
        if (val != this.textVisible) {
            this.textVisible = val;
            firePropertyChange ("textVisible", !val, val); //NOI18N
        }
    }
    
    /** If true, the selected item's toString method will supply
     * text for the combo box.  If false, text will be visible in 
     * the popup but not in the combo box. */
    public boolean isTextVisible() {
        return textVisible;
    }
    
    /** Throws an UnsupportedOperationException */
    public void setEditable (boolean val) {
        throw new UnsupportedOperationException();
    }
    
    public IconProvider getIconProvider() {
        return iconProvider;
    }
    
    public void setIconProvider (IconProvider prov) {
        if (prov != getIconProvider()) {
            IconProvider old = prov;
            this.iconProvider = prov;
            repaint();
            firePropertyChange ("iconProvider", old, prov); //NOI18N
        }
    }
}
