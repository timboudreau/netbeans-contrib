/*
 * FDataNode.java
 *
 * Created on July 9, 2007, 4:41 PM
 *
 */
package org.netbeans.modules.fortran;
 
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 * Node representing a fortran data object.
 */
public class FDataNode extends DataNode {
    
    private static final String IMAGE_ICON_BASE = "SET/PATH/TO/ICON/HERE";
    
    /**
     * Creates a new instance of FDataNode
     */
    public FDataNode(FDataObject obj) {
        super(obj, Children.LEAF);
//        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    

}
