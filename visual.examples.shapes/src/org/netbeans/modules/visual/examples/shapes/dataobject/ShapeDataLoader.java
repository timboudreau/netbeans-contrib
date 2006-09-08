/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visual.examples.shapes.dataobject;

import java.io.IOException;
import org.openide.actions.OpenAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

public class ShapeDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-shape";
    
    private static final long serialVersionUID = 1L;
    
    public ShapeDataLoader() {
        super("org.netbeans.modules.visual.examples.shapes.ShapeDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(ShapeDataLoader.class, "LBL_shape_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ShapeDataObject(primaryFile, this);
    }
    
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    

    
}
