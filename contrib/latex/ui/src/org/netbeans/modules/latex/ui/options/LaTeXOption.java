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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Utilities;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXOption extends OptionsCategory {
    
    /** Creates a new instance of LaTeXOption */
    public LaTeXOption() {
    }

    public OptionsPanelController create() {
        return new LaTeXOptionsPanelController();
    }

    public String getCategoryName() {
        return "&LaTeX";
    }

    public String getTitle() {
        return "LaTeX";
    }
    
    private Icon i;
    
    public synchronized Icon getIcon() {
        if (i == null) {
            i = new ImageIcon(Utilities.loadImage("org/netbeans/modules/latex/ui/resources/option.png"));
        }
        
        return i;
    }
}
