package org.netbeans.modules.scala.core;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class ScalaDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-scala";
    
    private static final long serialVersionUID = 1L;
    
    public ScalaDataLoader() {
        super("org.netbeans.modules.scala.core.ScalaDataObject");
    }
    
    @Override
    protected String defaultDisplayName() {
        return NbBundle.getMessage(ScalaDataLoader.class, "LBL_Scala_loader_name");
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ScalaDataObject(primaryFile, this);
    }

//    @Override
//    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
//        System.err.println("MK> " + new Exception().getStackTrace()[0] + " called...." + ", " + System.currentTimeMillis());
//        return new ScalaFileEntry(obj, primaryFile);
//    }
    
    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    
//    class ScalaFileEntry extends FileEntry {
//        
//        public ScalaFileEntry(MultiDataObject obj, FileObject fo) {
//            super(obj, fo);
//            System.err.println("MK> " + new Exception().getStackTrace()[0] + " called...." + ", " + System.currentTimeMillis());
//            System.err.println("MK>   obj: \"" + obj + "\"");
//            System.err.println("MK>   fo: \"" + fo + "\"");
//        }
//        
//    }
    
}
