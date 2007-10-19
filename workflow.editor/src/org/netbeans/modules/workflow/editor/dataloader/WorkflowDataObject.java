package org.netbeans.modules.workflow.editor.dataloader;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

public class WorkflowDataObject extends MultiDataObject {

    public WorkflowDataObject(FileObject pf, WorkflowDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    protected @Override
    Node createNodeDelegate() {
        return new WorkflowDataNode(this, getLookup());
    }

    public @Override
    Lookup getLookup() {
        return getCookieSet().getLookup();
    }
}
