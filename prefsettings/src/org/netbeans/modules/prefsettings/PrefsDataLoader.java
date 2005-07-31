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
package org.netbeans.modules.prefsettings;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Timothy Boudreau
 */
public final class PrefsDataLoader extends DataLoader {
    public PrefsDataLoader() {
        super ("org.netbeans.modules.prefsettings.PrefsDataObject"); //NOI18N
    }

    protected SystemAction[] defaultActions() {
        return new SystemAction[0];
    }
    
    private static final String EXT = "prefs"; //NOI18N
    protected DataObject handleFindDataObject(FileObject fo, DataLoader.RecognizedFiles recognized) throws IOException {
        if (EXT.equals(fo.getExt())) {
            if (fo.getFileSystem() == Repository.getDefault().getDefaultFileSystem()) {
                recognized.markRecognized(fo);
                return new PrefsDataObject(fo, this);
            }
        }
        return null;
    }
}
