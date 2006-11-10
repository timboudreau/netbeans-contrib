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

package org.netbeans.modules.rectangularedittools.actions;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class RectangularPasteAction extends AbstractRectangularAction {

    public String getName() {
        return NbBundle.getMessage(RectangularPasteAction.class, "CTL_RectangularPasteAction"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/rectangularedittools/actions/rectangularpaste.gif"; // NOI18N
    }

    protected boolean enable(Node[] activatedNodes) {
        boolean enabled = super.enable(activatedNodes);
        if (enabled) {
            // additonal check
            Clipboard clipboard = getExClipboard();
            if (clipboard == null) {
                return false;
            }
            try {
            enabled = (clipboard.getData(DataFlavor.stringFlavor) != null);
            } catch (IOException ex) {
                enabled = false;
            } catch (UnsupportedFlavorException ex) {
                enabled = false;
            }
        }
        return enabled;
    }

    protected boolean isReplacingAction() {
        return true;
    }

    protected boolean isCopyingAction() {
        return false;
    }

    protected boolean requiresSelection() {
        return false;
    }

    protected String getReplacementText() {
        // Get the contents from the current clipboard if it's text
        Clipboard cb = getExClipboard();
        try {
            return (String) cb.getData(DataFlavor.stringFlavor);
        } catch (IOException ex) {
        } catch (UnsupportedFlavorException ex) {
        }
        return null;
    }
}
