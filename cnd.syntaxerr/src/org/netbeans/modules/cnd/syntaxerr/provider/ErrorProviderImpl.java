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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeFileItemSet;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

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
    public Collection<ErrorInfo> getErrorsImpl(DataObject dao, BaseDocument doc) throws IOException, BadLocationException {
	
	// Fixup: since error highlighting does not work in headers, we'd better switch it off at all
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            for( NativeFileItem item : itemSet ) {
		if( item.getLanguage() == NativeFileItem.Language.C_HEADER ) {
		    return Collections.emptyList();
		}
	    }
	}
	
        String compiler = getCompiler(dao);
        if( compiler != null ) {
            Collection<ErrorInfo> result = new ArrayList<ErrorInfo>();
            FileObject fo = dao.getPrimaryFile();
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File tmpFile = File.createTempFile(fo.getName() + '_', "." + fo.getExt(), tmpDir); // NOI18N
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
            stream.close();
            if( DebugUtils.CLEAN_TMP ) {
                tmpFile.delete();
            }
            if( DebugUtils.TRACE ) System.err.printf("DONE %s\n", command);
            return result;
        }
        return Collections.emptyList();
    }
    
    private String getOptions(DataObject dao) {
        // FIXUP: a temporary varyant that allows to get *something*
        // TODO: think over, what if there are several items?
        StringBuilder sb = new StringBuilder();
        NativeFileItemSet itemSet = dao.getLookup().lookup(NativeFileItemSet.class);
        if( itemSet != null ) {
            for( NativeFileItem item : itemSet ) {
                for( String path : item.getUserIncludePaths() ) {
                    sb.append(" -I ");
                    sb.append(path);
                }
    //            for( String def : item.getUserMacroDefinitions() ) {
    //                sb.append(" -D ");
    //                sb.append(def);
    //            }
                break;
            }
            sb.append(' ');
        }
        return sb.toString();
    }

    // FIXUP: a temporary implementation, just to try how it looks like
    private void parseCompilerOutput(InputStream stream, String interestingFileName, Collection<ErrorInfo> errors) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for( String line = reader.readLine(); line != null; line = reader.readLine() ) {
            parseCompilerOutputLine(line, interestingFileName, errors);
        }
    }
    
    private void parseCompilerOutputLine(String line, String interestingFileName, Collection<ErrorInfo> errors) {
        if( DebugUtils.TRACE ) System.err.printf("\tPARSING: \t%s\n", line);
        findErrorOrWarning(line, ": error: ", true, interestingFileName, errors); // NOI18N
        findErrorOrWarning(line, ": warning: ", false, interestingFileName, errors); // NOI18N
    }
    
    private void findErrorOrWarning(String line, String keyword, boolean error, String interestingFileName, Collection<ErrorInfo> errors) {
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
                    errors.add(new ErrorInfoImpl(message, error, lineNum, colNum));
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
