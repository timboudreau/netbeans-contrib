/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.ui;

import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author denis
 */
public class PropertyNode extends AbstractNode {

    public PropertyNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public PropertyNode(Children children) {
        super(children);
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesKey.gif");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesKey.gif");
    }
}
