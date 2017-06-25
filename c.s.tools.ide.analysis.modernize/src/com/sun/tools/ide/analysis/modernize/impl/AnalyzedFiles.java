/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s): Ilia Gromov
 */
package com.sun.tools.ide.analysis.modernize.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.cnd.api.model.CsmFile;

/**
 * This cache is designed to help Editor showing correct checks for included
 * files.
 *
 * @param <T>
 */
public class AnalyzedFiles {

    private final Map<CsmFile, Set<CsmFile>> hierarchyCache = new HashMap<CsmFile, Set<CsmFile>>();
//    private final Map<FileObject, Set<CsmErrorInfo>> diagnosticsCache = new HashMap<FileObject, Set<CsmErrorInfo>>();

//    public void cacheDiagnostics(FileObject fo, CsmErrorInfo info) {
//        Set<CsmErrorInfo> alreadyMapped = diagnosticsCache.get(fo);
//
//        if (alreadyMapped == null) {
//            alreadyMapped = new HashSet<CsmErrorInfo>();
//            diagnosticsCache.put(fo, alreadyMapped);
//        }
//
//        alreadyMapped.add(info);
//    }
    public void cacheHierarchy(CsmFile startFile, Collection<CsmFile> includedFiles) {
        Set<CsmFile> alreadyMapped = hierarchyCache.get(startFile);

        if (alreadyMapped == null) {
            alreadyMapped = new HashSet<CsmFile>();
            hierarchyCache.put(startFile, alreadyMapped);
        }

        alreadyMapped.addAll(includedFiles);
    }

//    public Set<CsmErrorInfo> getDiagnostics(FileObject fo) {
//        return diagnosticsCache.get(fo);
//    }

    public Collection<? extends CsmFile> getStartFiles(CsmFile includedFile) {
        Set<CsmFile> result = new HashSet<CsmFile>();
        for (Map.Entry<CsmFile, Set<CsmFile>> entry : hierarchyCache.entrySet()) {
            CsmFile key = entry.getKey();
            Set<CsmFile> value = entry.getValue();

            if (value.contains(includedFile)) {
                result.add(key);
            }
        }
        return result;
    }

    public boolean isStartFile(CsmFile file) {
        return hierarchyCache.containsKey(file);
    }

    public void clear() {
        hierarchyCache.clear();
    }

    public static AnalyzedFiles getDefault() {
        return HOLDER.INSTANCE;
    }

    private static class HOLDER {

        public static final AnalyzedFiles INSTANCE = new AnalyzedFiles();
    }
}
