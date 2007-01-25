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

package org.netbeans.modules.jackpot;

import java.io.IOException;
import java.util.ArrayList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 * The list of all defined QuerySets.
 */
public class QuerySetList {
    private static QuerySetList instance;
    private ArrayList<QuerySet> sets;
    
    public static QuerySetList instance() {
        if (instance == null)
            instance = new QuerySetList();
        return instance;
    }

    private QuerySetList() {
        loadQuerySets();
        FileObject dir = getQuerySetDirectory().getPrimaryFile();
        dir.addFileChangeListener(new FileChangeAdapter() {
            public void fileDataCreated(FileEvent fe) {
                loadQuerySets();
            }
            public void fileDeleted(FileEvent fe) {
                loadQuerySets();
            }
            public void fileRenamed(FileRenameEvent fe) {
                loadQuerySets();
            }
        });
    }

    /**
     * Returns the list of currently defined QuerySets.
     */
    public QuerySet[] getQuerySets() {
        return sets.toArray(new QuerySet[0]);
    }
    
    /**
     * Returns the QuerySet for a specified index.
     */
    public QuerySet getQuerySet(int index) {
        return sets.get(index);
    }
    
    /**
     * Returns the QuerySet by name.
     */
    public QuerySet getQuerySet(String name) {
        for (QuerySet qs : sets)
            if (qs.getName().equals(name))
                return qs;
        return null;
    }

    /**
     * Save QuerySet list to the System FS.
     */
    public void saveAll() {
        for (QuerySet qs : sets)
            try {
                qs.save();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
    }

    /**
     * Revert all sets back to their saved states, discarding all modifications.
     */
    public void restoreAll() {
        for (QuerySet qs : sets)
            try {
                qs.restore(qs.getFileObject());
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
    }
    
    public void add(QuerySet qs) {
        sets.add(qs);
    }
    
    public void delete(QuerySet qs) {
        sets.remove(qs);
    }
    
    static DataFolder getQuerySetDirectory() {
        FileObject dir = Repository.getDefault().getDefaultFileSystem().findResource("/Jackpot/QuerySets"); // NOI18N
        assert dir != null;
        return DataFolder.findFolder(dir);
    }

    private void loadQuerySets() {
        DataFolder dir = getQuerySetDirectory();
        sets = new ArrayList<QuerySet>();
        for (DataObject dao : dir.getChildren()) {
            FileObject fo = dao.getPrimaryFile();
            try {
                String fileName = dao.getName();
                String localeName = getLocalizedName(dao, dir);
                QuerySet qs = new QuerySet(fileName, localeName);
                Boolean b = (Boolean)fo.getAttribute("default");
                qs.isDefault = b != null && b.booleanValue();
                qs.restore(fo);
                sets.add(qs);
            } catch (IOException e) {
                ErrorManager.getDefault().log("failed reading " + fo.getPath() + ": " + e);
            }
        }
    }
    
    /**
     * Fetch the localized name using either a file or its parent's attributes.
     */
    private String getLocalizedName(DataObject dao, DataFolder parent) {
        FileObject fo = dao.getPrimaryFile();
        String name = fo.getName();
        String bundleName = (String)fo.getAttribute("SystemFileSystem.localizingBundle");
        if (bundleName == null)
            bundleName = (String)parent.getPrimaryFile().getAttribute("SystemFileSystem.localizingBundle");
        if (bundleName != null)
            name = NbBundle.getBundle(bundleName).getString(fo.getPath());
        return name;
    }
}
