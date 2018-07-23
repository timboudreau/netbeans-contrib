package org.netbeans.modules.languages.ejs.loaders;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

public class EJSDataObject extends MultiDataObject
        implements Lookup.Provider {
    
    public EJSDataObject(FileObject pf,EJSDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        //CookieSet set = getCookieSet();
        //set.add(HtmlEditorSupport.class, this);
        //set.add(ViewSupport.class, this);
    }
    
    protected Node createNodeDelegate() {
        return new EJSDataNode(this, getLookup());
    }
    
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
}
