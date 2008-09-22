/*
 * FDataObject.java
 *
 * Created on July 9, 2007, 4:41 PM
 *
 */
package org.netbeans.modules.fort.core.dataobject;
 
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;

/**
 * Represents one file in a group data object.
 */
public class FDataObject extends MultiDataObject {
    
    /**
     * Creates a new instance of FDataObject
     */
    public FDataObject(FileObject pf, FDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }
    
    protected Node createNodeDelegate() {
        return new FDataNode(this);
    }
    
    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
}
