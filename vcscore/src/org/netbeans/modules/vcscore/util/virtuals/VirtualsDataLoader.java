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

package org.netbeans.modules.vcscore.util.virtuals;

import org.openide.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.UniFileLoader;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.actions.SystemAction;

import java.util.WeakHashMap;



/**
 * The loader of virtual files (files that are not locally present
 *  on the disk, but reside in the repository)
 * @author  Milos Kleint
 */
public class VirtualsDataLoader extends UniFileLoader {


    /** Create the loader.
    * Should <em>not</em> be used by subclasses.
    */
    public VirtualsDataLoader() {
        this("org.netbeans.modules.vcscore.util.virtuals.VirtualsDataObject"); // NOI18N
    }

    /** Create the loader from a subclass.
    * @param recognizedObject the class of data object
    *        recognized by the loader
    */
    public VirtualsDataLoader(String recognizedObject) {
        super(recognizedObject);
    }

    /** Create the loader from a subclass.
    * @param representationClass the class of data object
    *        recognized by the loader
    */
    public VirtualsDataLoader(Class representationClass) {
        super(representationClass);
    }

    protected MultiDataObject createMultiObject(FileObject fileObject) throws DataObjectExistsException, java.io.IOException {
        return new VirtualsDataObject(fileObject, this);
    }
    
    protected FileObject findPrimaryFile(FileObject fo) {
        if (fo.isFolder())
            return null;
        else
            return fo;
    }
    
    private SystemAction[] createDefaultActions() {
        return new SystemAction[] {
//            SystemAction.get(OpenAction.class),
            //SystemAction.get(CustomizeBeanAction.class),
//            SystemAction.get(RefreshRevisionsAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
//            SystemAction.get(NewAction.class),
//            SystemAction.get(DeleteAction.class),
//            SystemAction.get(RenameAction.class),
            //null,
            //SystemAction.get(SaveAsTemplateAction.class),
//            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class)
        };
    }

    private static SystemAction[] standardActions;
    
    protected SystemAction[] defaultActions() {
        if (standardActions != null)
            return standardActions;
        synchronized (VirtualsDataLoader.class) {
            if (standardActions == null) {
                standardActions = createDefaultActions();
            }
        }
        return standardActions;
    }

}
