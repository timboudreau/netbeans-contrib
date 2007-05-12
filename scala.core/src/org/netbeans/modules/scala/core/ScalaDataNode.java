package org.netbeans.modules.scala.core;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

public class ScalaDataNode extends DataNode {
    
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/scala/core/resources/class.png";
    
    public ScalaDataNode(final ScalaDataObject obj) {
        super(obj, Children.LEAF);
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
}
