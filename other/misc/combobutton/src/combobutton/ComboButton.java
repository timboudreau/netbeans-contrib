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
