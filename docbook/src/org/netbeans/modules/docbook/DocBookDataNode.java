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

package org.netbeans.modules.docbook;

import java.awt.Image;
import java.beans.BeanInfo;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.Utilities;

public class DocBookDataNode extends DataNode {
    
    public DocBookDataNode(DocBookDataObject obj) {
        super(obj, Children.LEAF);
    }
    
    public Image getIcon(int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage("org/netbeans/modules/docbook/docbook.png", true);
        } else {
            return null;
        }
    }
    
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    
}
