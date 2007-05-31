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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
 */
package org.netbeans.modules.propertiestool;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows PropertiesTool component.
 * @author David Strupl
 */
public class PropertiesToolAction extends AbstractAction implements HelpCtx.Provider {
    /** Constructor is public because we will call it from the layer file. */
    public PropertiesToolAction() {
        super(NbBundle.getMessage(PropertiesToolAction.class, "CTL_PropertiesToolAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PropertiesToolTopComponent.ICON_PATH, true)));
    }
    
    /**
     * The action just opens the singleton tool.
     */
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PropertiesToolTopComponent.findInstance();
        win.open();
        win.requestActive();
    }

    /**
     * Implemented to provide a unique help context.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(PropertiesToolAction.class);
    }
}
