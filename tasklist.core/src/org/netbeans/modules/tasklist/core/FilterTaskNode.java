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
final class FilterTaskNode extends FilterNode {
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
