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

package org.netbeans.modules.tasklist.core.editors;

import org.openide.util.NbBundle;

import java.beans.PropertyEditorSupport;

/**
 * Renderer for path/file:location values.
 *
 * @author Petr Kuzel
 */
public class LocationPropertyEditor extends PropertyEditorSupport {

    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        int y = gfx.getFontMetrics().getHeight() - gfx.getFontMetrics().getDescent();
        gfx.drawString(translate(getAsText()), box.x , box.y + y);
    }

    private String translate(String location) {
        if (location == null) return NbBundle.getMessage(LocationPropertyEditor.class, "unknown");
        int pathEnd = location.lastIndexOf('/');  // NOI18N
        if (pathEnd > 0 ) {
            String path = location.substring(0, pathEnd);
            String filePos = location.substring(pathEnd + 1);
            return filePos + "     (" + path + ")";   // NOI18N
        } else {
            return location;
        }
    }
}
