/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
