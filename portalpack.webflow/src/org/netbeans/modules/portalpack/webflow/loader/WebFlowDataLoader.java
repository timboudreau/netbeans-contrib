/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.portalpack.webflow.loader;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class WebFlowDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/webflow2.0+xml";
    private static final long serialVersionUID = 1L;

    public WebFlowDataLoader() {
        super("org.netbeans.modules.portalpack.webflow.loader.WebFlowDataObject");
    }

    @Override
    protected String defaultDisplayName() {
        return NbBundle.getMessage(WebFlowDataLoader.class, "LBL_WebFlow_loader_name");
    }

    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new WebFlowDataObject(primaryFile, this);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
