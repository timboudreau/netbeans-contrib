/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. 
 *
 * Portions Copyrighted 2006 Sun Microsystems, Inc.
 */
package org.netbeans.modules.manifesteditor;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class ManDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/x-manifest";
    
    private static final long serialVersionUID = 1L;
    
    public ManDataLoader() {
        super("org.netbeans.modules.manifesteditor.ManDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(ManDataLoader.class, "LBL_Man_loader_name");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ManDataObject(primaryFile, this);
    }
    
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
    
}
