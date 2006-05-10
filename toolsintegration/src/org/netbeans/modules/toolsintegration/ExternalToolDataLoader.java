package org.netbeans.modules.toolsintegration;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class ExternalToolDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-et";
    
    private static final long serialVersionUID = 1L;
    
    public ExternalToolDataLoader () {
        super ("org.netbeans.modules.toolsintegration.ExternalToolDataObject");
    }
    
    protected String defaultDisplayName () {
        return NbBundle.getMessage (ExternalToolDataLoader.class, "LBL_ExternalTool_loader_name");
    }
    
    protected void initialize () {
        super.initialize ();
        getExtensions ().addMimeType (REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject (FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ExternalToolDataObject (primaryFile, this);
    }
    
    protected String actionsContext () {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    
}
