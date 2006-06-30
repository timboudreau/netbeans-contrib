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
