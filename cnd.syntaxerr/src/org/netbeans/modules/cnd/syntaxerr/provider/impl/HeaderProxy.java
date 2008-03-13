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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmInclude;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.xref.CsmIncludeHierarchyResolver;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Vladimir Kvashin
 */
class HeaderProxy extends SourceProxy {

    private File fileToCompile;
    private File interestingFile;
    private String compilerOptions;
    private File compilerRunDirectory;
	    
    public HeaderProxy(DataObject dao, BaseDocument doc, File tmpDir) throws IOException {
	super(dao, doc, tmpDir);
    }
    
    @Override
    public File getFileToCompile() {
	return fileToCompile;
    }
    
    @Override
    public String getInterestingFileAbsoluteName() {
        return interestingFile.getAbsolutePath();
    }

    @Override
    public File getCompilerRunDirectory() {
	return compilerRunDirectory;
    }
    
    @Override
    public String getCompilerOptions() {
	return compilerOptions;
    }    
    
    @Override
    public void init() throws IOException, BadLocationException {
	
	tmpDir.mkdirs();
	
	TopIncludingFileAndDirective fad = getTopIncludingFile(dao);
	
	if( fad != null && fad.topFile != null ) {
            if( DebugUtils.TRACE ) System.err.printf("\t\tfound top file: %s", fad.topFile.getAbsolutePath());
	    
	    String includeDirectiveText = fad.includeDirectiveText;
            NativeFileItem topNativeItem = findTopNativeItem(fad.topFile.getAbsolutePath().toString(), fileItem);
	    if( topNativeItem  != null ) {
		String origPath = new File(fad.topFile.getAbsolutePath().toString()).getParent();
		compilerOptions = " -I " + tmpDir.getAbsolutePath() + " -I " + origPath + getCompilerOptions(topNativeItem);
	    } else {
		// TODO: be more smart when getting options: try get them from folder & project
		compilerOptions = super.getCompilerOptions();
	    }
	    
	    String subDir = ""; //"/CLucene";
	    {
		String nameExt = fo.getNameExt();
		if( includeDirectiveText != null && includeDirectiveText.endsWith(nameExt)  ) {	
		    String s = includeDirectiveText.substring(0, includeDirectiveText.length() - nameExt.length());
		    if( s.length() > 0 && ! s.startsWith("..") ) {
			subDir = "/" + s + "/";
		    }
		}
	    }
	    
	    if( DebugUtils.TRACE ) System.err.printf("\t\tincText: %s subdir: %s", includeDirectiveText, subDir);
	    
	    fileToCompile = new File(tmpDir, fad.topFile.getName().toString());
	    ErrorProviderUtils.copyFile(fad.topFile, fileToCompile);
	    
	    interestingFile = new File(tmpDir.getAbsolutePath() + subDir, fo.getNameExt());	
	    ErrorProviderUtils.WriteDocument(doc, interestingFile);
	    
        } else {
	    if( DebugUtils.TRACE ) System.err.printf("\t\ttop file not found");
	    // create a dummy source that includes this file
	    fileToCompile = File.createTempFile("tmp_source_", ".cpp", tmpDir);
	    PrintWriter writer = ErrorProviderUtils.createPrintWriter(fileToCompile);
	    writer.printf("#include \"%s\"%s", fo.getNameExt(), System.getProperty("line.separator"));
	    writer.close();	

	    // TODO: be more smart when getting options: try get them from folder & project
	    compilerOptions = super.getCompilerOptions();
	    
	    interestingFile = new File(tmpDir.getAbsolutePath(), fo.getNameExt());	
	    ErrorProviderUtils.WriteDocument(doc, interestingFile);
        }

	compilerRunDirectory = FileUtil.toFile(fo.getParent()); // the original header path
	
        
	
    }
    
    private static NativeFileItem findTopNativeItem(String topFile, NativeFileItem fileItem) {
        NativeProject topNativeProject = fileItem.getNativeProject();
        if( topNativeProject != null ) {
            try {
                return topNativeProject.findFileItem(new File(topFile).getCanonicalFile());
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static class TopIncludingFileAndDirective {
	public CsmFile topFile;
	public String includeDirectiveText;
    }
	    
    private TopIncludingFileAndDirective getTopIncludingFile(DataObject dao) {
	// TODO: change to getTopParentFiles() as soon as it is added to CsmIncludeHierarchyResolver
	// (now we have to stay 6.0 compliant)
	CsmFile header = CsmUtilities.getCsmFile(dao, false);
	if( header != null ) {
	    return getTopIncludingFile(header);
	}
	return null;
    }
    
    private TopIncludingFileAndDirective getTopIncludingFile(CsmFile header) {
        Collection<CsmFile> files = CsmIncludeHierarchyResolver.getDefault().getFiles(header);
	Set<CsmUID<CsmFile>> processedFiles = new HashSet<CsmUID<CsmFile>>();
	TopIncludingFileAndDirective fad = new TopIncludingFileAndDirective();
        for( CsmFile file : files ) {
	    processedFiles.add(file.getUID());
            if( file.isSourceFile() ) {
		fad.topFile = file;
		fad.includeDirectiveText = findInclude(file, header);
                return fad;
            }
	}
        for( CsmFile file : files ) {
	    CsmFile top = getTopIncludingFile(file, processedFiles);
	    if( top != null ) {
		fad.topFile = top;
		fad.includeDirectiveText = findInclude(file, header);
		return fad;
	    }
        }
	return null;
    }
	    
    private CsmFile getTopIncludingFile(CsmFile header,  Set<CsmUID<CsmFile>> processedFiles) {
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
                if( top.isSourceFile() ) {
                    return top;
                }
	    }
        }
	return null;
    }
	    
    private String findInclude(CsmFile file, CsmFile header) {
	if( file != null ) {
	    for( CsmInclude include : file.getIncludes() ) {
		if( header.equals(include.getIncludeFile() )) {
		    CharSequence cs = include.getIncludeName();
		    if (cs != null) {
			return cs.toString();
		    }
		}
	    }
	}
	return null;
    }

}
