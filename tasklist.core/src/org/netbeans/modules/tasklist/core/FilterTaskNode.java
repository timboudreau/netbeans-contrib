/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import java.awt.Image;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * Filter node for a Task
 *
 * @author Tor Norbye
 */
public class FilterTaskNode extends FilterNode {
    private boolean overrideIcon;

    public FilterTaskNode(Node n, org.openide.nodes.Children children,
    boolean overrideIcon) {
        super(n, children);
        this.overrideIcon = overrideIcon;
    }

    /** Override the icon? */
    public Image getIcon(int type) {
        if (overrideIcon) {
            return Utilities.loadImage(
                "org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
        } else {
            return super.getIcon(type);
        }
    }

    public Image getOpenedIcon(int type) {
        if (overrideIcon) {
            return Utilities.loadImage(
                "org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
        } else {
            return super.getOpenedIcon(type);
        }
    }
    
    public Node getOriginal() {
        return super.getOriginal();
    }
}
