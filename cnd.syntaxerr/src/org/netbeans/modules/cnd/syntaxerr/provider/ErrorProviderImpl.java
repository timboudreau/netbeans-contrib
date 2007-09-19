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
            String command = compiler + ' ' + tmpFile.getAbsolutePath();
            if( Flags.TRACE ) System.err.printf("Running %s\n", command);
            Process compilerProcess = Runtime.getRuntime().exec(command);
            InputStream stream = compilerProcess.getErrorStream();
            parseCompilerOutput(stream, result);
            stream.close();
            tmpFile.delete();
            return result;
        }
        return Collections.emptyList();
    }

    // FIXUP: a temporary implementation, just to try how it looks like
    private void parseCompilerOutput(InputStream stream, Collection<ErrorInfo> errors) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for( String line = reader.readLine(); line != null; line = reader.readLine() ) {
            parseCompilerOutputLine(line, errors);
        }
    }
    
    private void parseCompilerOutputLine(String line, Collection<ErrorInfo> errors) {
        if( Flags.TRACE ) System.err.printf("\t", line);
        String errWarn = ": error: ";
        int pos = line.indexOf(errWarn);
        if( pos > 0 ) {
            addErrorInfo(line, pos, pos + errWarn.length(), true, errors);
        }
        else {
            errWarn = ": warning: ";
            pos = line.indexOf(errWarn);
            if( pos > 0 ) {
                addErrorInfo(line, pos, pos + errWarn.length(), false, errors);
            }
        }
    }

    private void addErrorInfo(String line, int beforeErrPos, int afterErrPos, boolean error, Collection<ErrorInfo> errors) {
        int pos = line.indexOf(':');
        if( pos > 0 ) {
            String fileName = line.substring(0, pos);
            String strLineNo = line.substring(pos+1, beforeErrPos);
            int lineNo = Integer.parseInt(strLineNo);
            String message = line.substring(afterErrPos);
            if( Flags.TRACE ) System.err.printf("\t\tFILE: %s LINE: %8d message: %s\n", fileName, lineNo, message);
            errors.add(new ErrorInfoImpl(message, error, lineNo));
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
