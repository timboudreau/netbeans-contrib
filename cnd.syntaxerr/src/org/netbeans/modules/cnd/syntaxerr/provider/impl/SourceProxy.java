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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.syntaxerr.provider.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeFileItemSet;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 * 
 * @author Vladimir Kvashin
 */
class SourceProxy implements FileProxy {

    protected final DataObject dao;
    protected final BaseDocument doc;
    protected final File tmpDir;
    protected final FileObject fo;
    protected final NativeFileItem fileItem;

    public SourceProxy(DataObject dao, BaseDocument doc, File tmpDir) {
        this.dao = dao;
        this.doc = doc;
	this.tmpDir = tmpDir;		
        fo = dao.getPrimaryFile();
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            Collection<NativeFileItem> items = itemSet.getItems();
            fileItem = items.isEmpty() ? null : items.iterator().next();
        } else {
            fileItem = null;
        }
    }

    public CompilerInfo getCompilerInfo() {
        String path = MakeprojectUtils.getCompilerPath(dao, fileItem);
        if( isSun(dao, fileItem) ) {
            if( path == null || path.length() == 0 ) {
                String baseName = isCpp(dao, fileItem) ? "CC" : "cc"; // NOI18N
                path = ErrorProviderUtils.findInPath(baseName);
            }
            return new CompilerInfo(path, new SunParser());
        }
        else {
            if( path == null || path.length() == 0 ) {
                String baseName = isCpp(dao, fileItem) ? "g++" : "gcc"; // NOI18N
                path = ErrorProviderUtils.findInPath(baseName);
            }
            return new CompilerInfo(path, new GnuParser());
        }
    }
    
    public String getCompilerOptions() {
        return getCompilerOptions(fileItem);
    }
    
    protected static String getCompilerOptions(NativeFileItem item) {
        StringBuilder sb = new StringBuilder(" -I . ");
        String options = MakeprojectUtils.getCompilerOptions(item);
        if( options != null ) {
            sb.append(options);
        } else  if( item != null ) {
            for( String path : item.getUserIncludePaths() ) {
                sb.append(" -I "); // NOI18N
                sb.append(path);
            }
            for( String def : item.getUserMacroDefinitions() ) {
                sb.append(" -D"); // NOI18N
                sb.append(def);
            }
        }
        return sb.toString();
    }
 
    public File getCompilerRunDirectory() {
        return FileUtil.toFile(fo.getParent());
    }
    
    public File getFileToCompile() {
	return new File(tmpDir, fo.getNameExt());
    }
    
    public void init() throws IOException, BadLocationException {
	ErrorProviderUtils.WriteDocument(doc, getFileToCompile());
    }

    public String getInterestingFileAbsoluteName() {
        return getFileToCompile().getAbsolutePath();
    }
    
    private static boolean isSun(DataObject dao, NativeFileItem item) {
        if( item != null ) {
            for (String macro : item.getSystemMacroDefinitions()) {
                if (macro.startsWith("__SUNPRO_C")) {
                    return true;
                }
                else if (macro.startsWith("__SUNPRO_CC")) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isCpp(DataObject dao, NativeFileItem item) {
        return dao.getPrimaryFile().getMIMEType().endsWith("/x-c++");
    }

}
