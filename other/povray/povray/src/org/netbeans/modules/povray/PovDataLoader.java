/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PovDataLoader.java
 *
 * Created on February 16, 2005, 2:26 PM
 */

package org.netbeans.modules.povray;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

/**
 * This is the entire code that causes NetBeans to recognize .pov and .inc
 * files as belonging to POV-Ray.
 *
 * @author Timothy Boudreau
 */
public class PovDataLoader extends UniFileLoader {
    
    /** Creates a new instance of PovDataLoader */
    public PovDataLoader() {
        super ("org.netbeans.modules.PovRayDataObject"); //NOI18N
        ExtensionList list = new ExtensionList();
        list.addExtension("pov"); //NOI18N
        list.addExtension("inc"); //NOI18N
        setExtensions(list);
        setDisplayName(NbBundle.getMessage(PovDataLoader.class, "TYPE_Povray")); //NOI18N
    }  

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, java.io.IOException {
        return new PovRayDataObject (primaryFile, this);
    }
}
