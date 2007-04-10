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

package org.netbeans.modules.jackpot.rules;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.MapFormat;
import org.openide.util.NbBundle;

/**
 * RulesDataLoader: a DataLoader for Jackpot rules files.
 */
public class RulesDataLoader extends UniFileLoader {
    static final String MIME_TYPE = "text/x-rules"; // NOI18N

    /** Create a new rules file data loader. */
    public RulesDataLoader() {
        super("org.netbeans.modules.jackpot.RulesDataObject");
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(RulesDataLoader.class, "TYPE_JackpotRules");
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(MIME_TYPE);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile)
    throws DataObjectExistsException, IOException {
        return new RulesDataObject(primaryFile, this);
    }

    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new RulesFileEntry(obj, primaryFile);
    }
    
    protected String actionsContext() {
        return "Loaders/" + MIME_TYPE + "/Actions";
    }

    /**
     * FileEntry.Format subclass that expands template macros.
     */
    public static class RulesFileEntry extends FileEntry.Format {
        RulesFileEntry(MultiDataObject obj, FileObject file) {
            super(obj, file);
        }
        
        protected java.text.Format createFormat (FileObject target, String name, String ext) {
            HashMap map = new HashMap();
            Date now = new Date();

            map.put ("NAME", name); // NOI18N
            map.put ("DATE", DateFormat.getDateInstance (DateFormat.LONG).format (now)); // NOI18N
            map.put ("TIME", DateFormat.getTimeInstance (DateFormat.SHORT).format (now)); // NOI18N
            map.put ("USER", System.getProperty ("user.name")); // NOI18N

            MapFormat format = new MapFormat (map);
            format.setLeftBrace ("__"); // NOI18N
            format.setRightBrace ("__"); // NOI18N
            format.setExactMatch (false);
            return format;
        }
    }
}
