/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
