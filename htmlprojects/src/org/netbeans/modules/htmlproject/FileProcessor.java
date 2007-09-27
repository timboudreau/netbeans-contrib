/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s): */
package org.netbeans.modules.htmlproject;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Runnable which scans for files to add to the children of a Kids;
 * Weakly references the Children object it will add these to; enables
 * removeNotify() to be called on a Kids instance if scanning was aborted
 * (if this were implemented on Kids, it could not be aborted because
 * the RequestProcessor would hold a reference to the Kids object). 
 *
 * @author Tim Boudreau
 */
final class FileProcessor implements Runnable {
    private final Reference kidsRef;
    private final boolean incremental;
    private final boolean sortByFolder;
    private final File dir;
    public FileProcessor(Kids kids, File dir, boolean incremental, boolean sortByFolder) {
        kidsRef = new WeakReference(kids);
        this.incremental = incremental;
        this.sortByFolder = sortByFolder;
        this.dir = dir;
    }
    
    private boolean cancelled;
    public void cancel() {
        cancelled = true;
        System.err.println("FileProcessor CANCEL");
    }
    
    private boolean isCancelled() {
        boolean result = cancelled || Thread.currentThread().isInterrupted();
        if (result) {
            System.err.println("CANCELLED");
        }
        return result;
    }
    
    Kids getKids() {
        return (Kids) kidsRef.get();
    }
    
    public void run() {
        File f = dir;
        List l = new ArrayList();
        try {
            if (isCancelled()) {
                l.clear();
                return;
            }
            searchFiles (f, l, incremental);
            if (isCancelled()) {
                l.clear();
            }
        } finally {
            Kids k = getKids();
            if (k != null) {
                k.doneScanning();
            }
            if (l.size() > 0) {
                k.appendKeys (l);
            }
        }
    }
    
    void appendKeys (List keys) {
        if (!isCancelled()) {
            Kids k = getKids();
            if (k == null) {
                cancel();
                return;
            }
            k.appendKeys(keys);
            keys.clear();
        }
    }
    

    private void searchFiles(File f, List l, boolean incremental) {
        if (isCancelled()) {
            l.clear();
            return;
        }
        if (cancelled) {
            return;
        }
        if (f.isDirectory()) {
            if (!sortByFolder) {
                File[] ff = f.listFiles();
                int sz = l.size();
                for (int i = 0; i < ff.length; i++) {
                    searchFiles (ff[i], l, incremental);
                    if (Thread.currentThread().isInterrupted()) {
                        System.err.println("interru8pt");
                        cancel();
                        return;
                    }
                }
                int nsz = l.size();
                if (sortByFolder && nsz != sz) {
                    l.add(f);
                }
                if (incremental && (nsz != 0 && nsz > sz)) {
                    appendKeys(l);
                    l.clear();
                }
            }  else {
                File[] ff = f.listFiles();
                boolean found = false;
                for (int i = 0; i < ff.length; i++) {
                    found |= Kids.isUsable(ff[i]);
                    if (ff[i].isDirectory()) {
                        searchFiles (ff[i], l, incremental);
                        if (Thread.currentThread().isInterrupted()) {
                            System.err.println("  interrupt");
                            cancel();
                            return;
                        }
                    }
                }
                if (found) {
                    l.add(f);
                }
            }
        }  else {
            if (!sortByFolder && Kids.isUsable(f)) { //NOI18N
                l.add (f);
            }
        }
    }

    static String getExt(File f) {
        String s = f.getName();
        int ix = s.lastIndexOf('.');
        if (ix != -1 && ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return "";
        }
    }

    static String getName (File f) {
        String s = f.getName();
        return s.substring(0, s.length() - getExt(f).length() - 1);
    }
}
