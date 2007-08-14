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
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class SpellcheckerOptionsPanelController extends OptionsPanelController {

    private SpellcheckerOptionsPanel comp;
    private boolean valid = true;

    /**
     * Creates a new instance of SpellcheckerOptionsPanelController
     */
    public SpellcheckerOptionsPanelController() {
    }

    public void update() {
        getComponentImpl().update();
    }

    public void applyChanges() {
        getComponentImpl().commit();
    }

    public void cancel() {
        getComponentImpl().update();
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isChanged() {
        return false;
    }
    
    void setValid(boolean valid) {
        this.valid = valid;
        pcs.firePropertyChange(PROP_VALID, null, valid);
    }

    private synchronized SpellcheckerOptionsPanel getComponentImpl() {
        if (comp == null) {
            comp = new SpellcheckerOptionsPanel(this);
            setValid(true);
        }
        
        return comp;
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return getComponentImpl();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
}
