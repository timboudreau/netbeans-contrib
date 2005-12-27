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
 */
package org.netbeans.modules.latex.ui.options;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXOptionsPanelController extends OptionsPanelController {
    
    private LaTeXOptionsPanel comp;
    
    /** Creates a new instance of LaTeXOptionsPanelController */
    public LaTeXOptionsPanelController() {
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
        return true;
    }

    public boolean isChanged() {
        return false;
    }

    private synchronized LaTeXOptionsPanel getComponentImpl() {
        if (comp == null) {
            comp = new LaTeXOptionsPanel();
        }
        
        return comp;
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return getComponentImpl();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
    }
    
}
