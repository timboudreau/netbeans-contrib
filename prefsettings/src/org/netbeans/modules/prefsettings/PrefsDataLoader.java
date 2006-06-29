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
