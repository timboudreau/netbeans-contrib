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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeFileItemSet;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 * ErrorProvider implementation
 * @author Vladimir Kvashin
 */
// package-local
class ErrorProviderImpl extends ErrorProvider {

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

    // FIXUP: a temporary implementation
    private Collection<ErrorInfo> getErrorsImpl(DataObject dao, BaseDocument doc) throws IOException, BadLocationException {
	
	// Fixup: since error highlighting does not work in headers, we'd better switch it off at all
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            for( NativeFileItem item : itemSet.getItems() ) {
		if( item.getLanguage() == NativeFileItem.Language.C_HEADER ) {
		    return Collections.emptyList();
		}
	    }
	}
	
        String compiler = getCompiler(dao);
        if( compiler != null ) {
	    
            ErrorBag result = new ErrorBag();
            FileObject fo = dao.getPrimaryFile();
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    
	    // ensure prefix is not less than 3, otherwise createTempFile throws
	    String prefix = fo.getName();
	    switch( prefix.length() ) { 
	    // it can't be 0
	    case 1: prefix += "__"; break; // NOI18N
	    case 2: prefix += '_'; break;
	    default:
	    }

            File tmpFile = File.createTempFile(prefix, "." + fo.getExt(), tmpDir); // NOI18N
            FileWriter writer = new FileWriter(tmpFile);
            doc.write(writer, 0, doc.getLength());
            writer.write(System.getProperty("line.separator"));
            writer.close();
	    
            // TODO: set correct options
            String command = compiler + " -c -o /dev/null -I . " + getOptions(dao) + ' ' + tmpFile.getAbsolutePath(); // NOI18N
            if( DebugUtils.SLEEP_ON_PARSE ) DebugUtils.sleep(3000);
            if( DebugUtils.TRACE ) System.err.printf("\n\nRUNNING %s\n", command);
            Process compilerProcess = Runtime.getRuntime().exec(command, null, FileUtil.toFile(fo.getParent()));
            InputStream stream = compilerProcess.getErrorStream();
            parseCompilerOutput(stream, tmpFile.getAbsolutePath(), result);
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

    private static class ErrorBag {
	
	Map<Integer, ErrorInfoImpl> errors = new HashMap<Integer, ErrorInfoImpl>();
	Map<Integer, ErrorInfoImpl> warnings = new HashMap<Integer, ErrorInfoImpl>();
	
	public void add(String message, boolean error, int line, int column) {
	    ErrorInfoImpl info = new ErrorInfoImpl(message, error, line, column);
	    Map<Integer, ErrorInfoImpl> map = error ? errors : warnings;
	    ErrorInfoImpl existent = map.get(line);
	    if( existent == null ) {
		map.put(line, info);
	    }
	    else {
		existent.adsorb(info);
	    }
	}
	
	public Collection<ErrorInfo> getResult() {
	    Collection<ErrorInfo> result = new ArrayList<ErrorInfo>(errors.size() + warnings.size());
	    for( ErrorInfo info : errors.values() ) {
		result.add(info);
	    }
	    for( ErrorInfo info : warnings.values() ) {
		result.add(info);
	    }
	    return result;
	}
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

    // FIXUP: a temporary implementation, just to try how it looks like
    private void parseCompilerOutput(InputStream stream, String interestingFileName, ErrorBag errorBag) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for( String line = reader.readLine(); line != null; line = reader.readLine() ) {
            parseCompilerOutputLine(line, interestingFileName, errorBag);
        }
    }
    
    private void parseCompilerOutputLine(String line, String interestingFileName, ErrorBag errorBag) {
        if( DebugUtils.TRACE ) System.err.printf("\tPARSING: \t%s\n", line);
        findErrorOrWarning(line, ": error: ", true, interestingFileName, errorBag); // NOI18N
        findErrorOrWarning(line, ": warning: ", false, interestingFileName, errorBag); // NOI18N
    }
    
    private void findErrorOrWarning(String line, String keyword, boolean error, String interestingFileName, ErrorBag errorBag) {
        int pos = line.indexOf(keyword);
        if( pos > 0 ) {
            int beforeErrPos = pos;
            int afterErrPos = pos + keyword.length();
            int fileEndPos = line.indexOf(':');
            if( fileEndPos > 0 ) {
                String fileName = line.substring(0, fileEndPos);
                if( fileName.equals(interestingFileName)) {
                    String strPosition = line.substring(fileEndPos+1, beforeErrPos);
		    int lineNum;
		    int colNum = -1;
		    int colonPos = strPosition.indexOf(':');
		    if( colonPos < 0 ) {
                        lineNum = Integer.parseInt(strPosition);
		    }
		    else {
			lineNum = Integer.parseInt(strPosition.substring(0, colonPos));
			colNum = Integer.parseInt(strPosition.substring(colonPos+1));
		    }
                    String message = line.substring(afterErrPos);
                    if( DebugUtils.TRACE ) System.err.printf("\t\tFILE: %s LINE: %8d COL: %d MESSAGE: %s\n", fileName, lineNum, colNum, message);
		    errorBag.add(message, error, lineNum, colNum);
                }
            }
        }
    }

    // FIXUP: a temporary implementation
    private String getCompiler(DataObject dao) {
        String baseName = dao.getPrimaryFile().getMIMEType().endsWith("/x-c++") ? "g++" : "gcc"; // NOI18N
        String path = System.getenv("PATH"); // NOI18N
        for (StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator); tokenizer.hasMoreTokens();) {
            String pathElement = tokenizer.nextToken();
            File file = new File(pathElement, baseName);
            if( file.exists() ) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
    
}
