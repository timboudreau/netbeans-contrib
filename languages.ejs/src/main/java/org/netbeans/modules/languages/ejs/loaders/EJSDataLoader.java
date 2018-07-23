package org.netbeans.modules.languages.ejs.loaders;

import java.io.IOException;
import org.netbeans.modules.languages.ejs.lexer.api.EJSTokenId;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class EJSDataLoader extends UniFileLoader {
    
    private static final long serialVersionUID = 1L;
    
    public EJSDataLoader() {
        super("org.netbeans.modules.languages.ejs.loaders.EJSDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(EJSDataLoader.class, "LBL_EJS_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(EJSTokenId.MIME_TYPE);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new EJSDataObject(primaryFile, this);
    }
    
    protected String actionsContext() {
        return "Loaders/" + EJSTokenId.MIME_TYPE + "/Actions";
    }
    
}
