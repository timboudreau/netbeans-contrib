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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.clearcase.client.mockup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.netbeans.modules.versioning.util.Utils;

/**
 *
 * @author Tomas Stupka
 */
class Repository {
    
    private static Repository instance;    
    
    private Map<File, Map<File, FileEntry>> map = new HashMap<File, Map<File, FileEntry>>();    
            
    static Repository getInstance() {
        if(instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    void add(File file, boolean checkout) {
        File parent = file.getParentFile();
        Map<File, FileEntry> entries = map.get(parent);
        if (entries == null || entries.get(file) == null) {
            CleartoolMockup.LOG.warning("No entry for to be checkedin file " + file);
        }
        ci(file);
        if(checkout) {
            co(file, true);
        }
    }
    
    void ci(File file) {
        try {
            File parent = file.getParentFile();
            Map<File, FileEntry> entries = map.get(parent);
            if (entries == null) {
                entries = new HashMap<File, FileEntry>();
                map.put(parent, entries);
            }
            FileEntry fe = entries.get(file);
            if (fe == null) {
                fe = new FileEntry(file);
                entries.put(file, fe);
            }
            fe.setCheckedout(false);
            fe.setReserved(false);
            fe.setVersion(fe.getVersion() + 1);
            if(file.isFile()) {
                File data = File.createTempFile("clearcase-", ".data");
                data.deleteOnExit();
                fe.getVersions().add(data);
                Utils.copyStreamsCloseAll(new FileOutputStream(data), new FileInputStream(file));
            }
        } catch (IOException ex) {
            CleartoolMockup.LOG.log(Level.WARNING, null, ex);
        }
    }

    void co(File file, boolean reserved) {
        File parent = file.getParentFile();
        Map<File, FileEntry> entries = map.get(parent);
        if(entries == null) {
            CleartoolMockup.LOG.warning("No entry for to be checkedout file " + file);
        }
        FileEntry fe = entries.get(file);
        if(fe == null) {
            CleartoolMockup.LOG.warning("No entry for to be checkedout file " + file);
        }
        fe.setCheckedout(true);
        fe.setReserved(reserved);
    }

    FileEntry getEntry(File file) {
        File parent = file.getParentFile();
        Map<File, FileEntry> entries = map.get(parent);
        if(entries == null) {
            return null;
        }
        return entries.get(file);        
    }

    void removeEntry(File file) {
        File parent = file.getParentFile();
        Map<File, FileEntry> entries = map.get(parent);
        if(entries == null) {
            return;
        }
        entries.remove(file);
    }

    void unco(File file) {
        File parent = file.getParentFile();
        Map<File, FileEntry> entries = map.get(parent);
        if(entries == null) {
            CleartoolMockup.LOG.warning("No entry for to be uncheckedout file " + file);
        }
        FileEntry fe = entries.get(file);
        if(fe == null) {
            CleartoolMockup.LOG.warning("No entry for to be uncheckedout file " + file);
        }
        fe.setCheckedout(false);
        fe.setReserved(false);        
    }
}
