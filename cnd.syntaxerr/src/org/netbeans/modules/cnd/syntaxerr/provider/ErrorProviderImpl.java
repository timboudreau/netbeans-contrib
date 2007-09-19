/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
import org.netbeans.modules.cnd.syntaxerr.Flags;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

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
        String compiler = getCompiler(dao);
        if( compiler != null ) {
            Collection<ErrorInfo> result = new ArrayList<ErrorInfo>();
            FileObject fo = dao.getPrimaryFile();
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File tmpFile = File.createTempFile(fo.getName(), "." + fo.getExt(), tmpDir);
            doc.write(new FileWriter(tmpFile), 0, doc.getLength());
            // TODO: set correct options
            String command = compiler + " -c -o /dev/null -I . " + tmpFile.getAbsolutePath();
            if( Flags.TRACE ) System.err.printf("RUNNING %s\n", command);
            Process compilerProcess = Runtime.getRuntime().exec(command, null, FileUtil.toFile(fo.getParent()));
            InputStream stream = compilerProcess.getErrorStream();
            parseCompilerOutput(stream, tmpFile.getAbsolutePath(), result);
            stream.close();
            if( Flags.CLEAN_TMP ) {
                tmpFile.delete();
            }
            return result;
        }
        return Collections.emptyList();
    }

    // FIXUP: a temporary implementation, just to try how it looks like
    private void parseCompilerOutput(InputStream stream, String interestingFileName, Collection<ErrorInfo> errors) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for( String line = reader.readLine(); line != null; line = reader.readLine() ) {
            parseCompilerOutputLine(line, interestingFileName, errors);
        }
    }
    
    private void parseCompilerOutputLine(String line, String interestingFileName, Collection<ErrorInfo> errors) {
        if( Flags.TRACE ) System.err.printf("\tPARSING: \t%s\n", line);
        findErrorOrWarning(line, ": error: ", true, interestingFileName, errors);
        findErrorOrWarning(line, ": warning: ", true, interestingFileName, errors);
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
                    if( Flags.TRACE ) System.err.printf("\t\tFILE: %s LINE: %8d COL: %d message: %s\n", fileName, lineNum, colNum, message);
                    errors.add(new ErrorInfoImpl(message, error, lineNum, colNum));
                }
            }
        }
    }

    // FIXUP: a temporary implementation
    private String getCompiler(DataObject dao) {
        String baseName = dao.getPrimaryFile().getMIMEType().endsWith("/x-c++") ? "g++" : "gcc";
        String path = System.getenv("PATH");
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
