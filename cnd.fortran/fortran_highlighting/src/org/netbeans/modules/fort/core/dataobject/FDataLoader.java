/*
 * FDataLoader.java
 *
 * Created on July 9, 2007, 4:41 PM
 *
 */
package org.netbeans.modules.fort.core.dataobject;
 
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

/**
 * Support class for loader handling one file at a time
 */
public class FDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-f90";
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates the right data object for a given primary file.
     */
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new FDataObject(primaryFile, this);
    }
    
    /**
     * Creates a new instance of FDataLoader
     */
    public FDataLoader() {
        super("org.netbeans.modules.fortran.FDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(FDataLoader.class, "LBL_F_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    

    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    
}
