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

package org.netbeans.modules.javanavigators;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.awt.HtmlRenderer;

/**
 *
 * @author Tim
 */
public class CellRenderer implements ListCellRenderer {
    private final HtmlRenderer.Renderer htmlRenderer = 
            HtmlRenderer.createRenderer();
    
    public CellRenderer() {
    }
    
    public Component getListCellRendererComponent(JList arg0, Object arg1,
                                                  int arg2, boolean arg3,
                                                  boolean arg4) {
        Component result = htmlRenderer.getListCellRendererComponent(arg0, 
                arg1, arg2, arg3, arg4);
        if (arg1 instanceof Description) {
            Description d = (Description) arg1;
            htmlRenderer.setIcon(d.icon);
            ((JComponent)result).setToolTipText(d.javadoc);
        }
        return result;
    }

}
