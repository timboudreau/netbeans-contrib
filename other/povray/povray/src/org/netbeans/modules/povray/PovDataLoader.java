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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * PovDataLoader.java
 *
 * Created on February 16, 2005, 2:26 PM
 */

package org.netbeans.modules.povray;
import java.io.IOException;
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
publicclass PovDataLoader extends UniFileLoader {
    
    /** Creates a new instance of PovDataLoader */
    public PovDataLoader() {
        super ("org.netbeans.modules.povray.PovRayDataObject"); //NOI18N
        ExtensionList list = new ExtensionList();
        list.addExtension("pov"); //NOI18N
        list.addExtension("inc"); //NOI18N
        setExtensions(list);
        setDisplayName(NbBundle.getMessage(PovDataLoader.class, "TYPE_Povray")); //NOI18N
    }  

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new PovRayDataObject (primaryFile, this);
    }
}
