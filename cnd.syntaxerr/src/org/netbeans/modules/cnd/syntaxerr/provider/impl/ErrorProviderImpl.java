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

import org.netbeans.modules.cnd.syntaxerr.provider.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.xref.CsmIncludeHierarchyResolver;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeFileItemSet;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.openide.loaders.DataObject;

/*
 TODO: ensure correct work in the case of several users on the same machine
 TODO: ensure it doesn't crush/hang when there are no compilers, or no permissions, etc
 TODO: what if the task starts while the previous task isn't yet finished
 */

/**
 * ErrorProvider implementation
 * @author Vladimir Kvashin
 */
public class ErrorProviderImpl extends ErrorProvider {
    
    /**
     * ErrorProvider implementation.
     * It is supposed to be called in a separate thread
     */
    public Collection<ErrorInfo> getErrors(DataObject dao, BaseDocument doc) {
        try {
            return getErrorsImpl(dao, doc);
        }
        catch( IOException e ) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    
    private boolean isHeader(DataObject dao) {
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            for( NativeFileItem item : itemSet.getItems() ) {
		if( item.getLanguage() == NativeFileItem.Language.C_HEADER ) {
		    return true;   
		}
	    }
	}
	else {
	    String ext = dao.getPrimaryFile().getExt();
	    if( ext == null || ext.length () == 0 ) {
		return true;
	    }
	    if( ext.equals("h") || ext.equals("hpp") ) {
		return true;
	    }
	}
	return false;
    }
    
    private CsmFile getTopIncludingFile(DataObject dao) {
	// TODO: change to getTopParentFiles() as soon as it is added to CsmIncludeHierarchyResolver
	// (now we have to stay 6.0 compliant)
	CsmFile header = CsmUtilities.getCsmFile(dao, false);
	if( header != null ) {
	    return getTopIncludingFile(header, new HashSet<CsmUID<CsmFile>>());
	}
	return null;
    }
    
    private CsmFile getTopIncludingFile(CsmFile header, Set<CsmUID<CsmFile>> processedFiles) {
        Collection<CsmFile> files = CsmIncludeHierarchyResolver.getDefault().getFiles(header);
        for( CsmFile file : files ) {
            if( file.isSourceFile() ) {
                return file;
            }
        }
        for( CsmFile file : files ) {
	    CsmUID<CsmFile> uid = file.getUID();
            if( ! processedFiles.contains(uid) ) {
		processedFiles.add(uid);
                CsmFile top = getTopIncludingFile(file, processedFiles);
                if( file.isSourceFile() ) {
                    return file;
                }
	    }
        }
	return null;
    }
    

        
	    
    private Collection<ErrorInfo> getErrorsImpl(DataObject dao, BaseDocument doc) throws IOException, BadLocationException {
	//System.err.printf("File %s MIME type %s\n", dao.getPrimaryFile().getNameExt(), dao.getPrimaryFile().getMIMEType());
	FileProxy fileProxy;
        if( isHeader(dao) ) {
            return Collections.<ErrorInfo>emptyList();		    
	}
	else {
	    fileProxy = new DaoAndDocProxy(dao, doc);
	}
	return fileProxy == null ?   Collections.<ErrorInfo>emptyList() : getErrorsImpl(fileProxy);
    }
    
    private Collection<ErrorInfo> getErrorsImpl(FileProxy fileProxy) throws IOException, BadLocationException {
	
        CompilerInfo compilerInfo = fileProxy.getCompilerInfo();
        if( compilerInfo != null && compilerInfo.getPath() != null &&  compilerInfo.getPath().length() > 0 ) {
	    
            ErrorBag result = new ErrorBag();
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    
	    // ensure prefix is not less than 3, otherwise createTempFile throws
	    String prefix = fileProxy.getName();
	    switch( prefix.length() ) { 
	    // it can't be 0
	    case 1: prefix += "__"; break; // NOI18N
	    case 2: prefix += '_'; break;
	    default:
	    }

            File tmpFile = File.createTempFile(prefix, "." + fileProxy.getExt(), tmpDir); // NOI18N
            FileWriter writer = new FileWriter(tmpFile);
            fileProxy.write(writer);
            writer.write(System.getProperty("line.separator"));
            writer.close();
	    
            // TODO: set correct options
            String command = compilerInfo.getPath() + " -c -o /dev/null -I . " + fileProxy.getCompilerOptions() + ' ' + tmpFile.getAbsolutePath(); // NOI18N
            if( DebugUtils.SLEEP_ON_PARSE ) DebugUtils.sleep(3000);
            if( DebugUtils.TRACE ) System.err.printf("\n\nRUNNING %s\n", command);
            Process compilerProcess = Runtime.getRuntime().exec(command, null, fileProxy.getParent());
            InputStream stream = compilerProcess.getErrorStream();
            compilerInfo.getParser().parseCompilerOutput(stream, tmpFile.getAbsolutePath(), result);
//	    result = merge(result);
            stream.close();
            if( DebugUtils.CLEAN_TMP ) {
                tmpFile.delete();
            }
            if( DebugUtils.TRACE ) System.err.printf("DONE %s\n", command);
            return result.getResult();
        }
        return Collections.emptyList();
    }

    private String getOptions(DataObject dao) {
        // FIXUP: a temporary varyant that allows to get *something*
        // TODO: think over, what if there are several items?
        StringBuilder sb = new StringBuilder();
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            for( NativeFileItem item : itemSet.getItems() ) {
                for( String path : item.getUserIncludePaths() ) {
                    sb.append(" -I "); // NOI18N
                    sb.append(path);
                }
		for( String def : item.getUserMacroDefinitions() ) {
		    sb.append(" -D"); // NOI18N
		    sb.append(def);
		}
                break;
            }
            sb.append(' ');
        }
        return sb.toString();
    }


}
