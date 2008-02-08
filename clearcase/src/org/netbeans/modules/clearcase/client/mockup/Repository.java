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
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Tomas Stupka
 */
class Repository {
    
    private static Repository instance;    
    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.clearcase");
    
    private Map<File, Set<FileEntry>> map = new HashMap<File, Set<FileEntry>>();
    
    static Repository getInstance() {
        if(instance == null) {
            instance = new Repository();
        }
        return instance;
    }
    
    void addEntry(FileEntry fe) {
        File parent = fe.getFile().getParentFile();
        Set<FileEntry> entries = map.get(parent);
        if(entries == null) {
            entries = new HashSet<FileEntry>();            
            map.put(parent, entries);
        }
        LOG.fine("Adding: " + fe);
        entries.add(fe);
    }

    FileEntry getEntry(File file) {
        File parent = file.getParentFile();
        Set<FileEntry> entries = map.get(parent);
        if(entries == null) {
            return null;
        }
        for (FileEntry fileEntry : entries) {
            if(fileEntry.getFile().equals(file)) {
                return fileEntry;
            }
        }
        return null;
    }
    
}
