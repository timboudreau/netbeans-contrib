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
 * License Header, wupith the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorInfo;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorProvider;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.loaders.DataObject;

/**
 * Highlights errors in a the particular file
 * @author Vladimir Kvashin
 */
/* package-local */
class FileHighliter implements DocumentListener {

    private DataObject dao;
    private BaseDocument doc;
    
    private boolean disregard = false;
    private boolean disposed = false;
    
    private static final Map<ErrorInfo.Severity, Severity> errorInfoSeverity2EditorSeverity;
    
    static {
        errorInfoSeverity2EditorSeverity = new EnumMap<ErrorInfo.Severity, Severity>(ErrorInfo.Severity.class);
        errorInfoSeverity2EditorSeverity.put(ErrorInfo.Severity.ERROR, Severity.ERROR);
        errorInfoSeverity2EditorSeverity.put(ErrorInfo.Severity.WARNING, Severity.WARNING);
    }
    
    public FileHighliter(DataObject dao, BaseDocument doc) {
        assert dao != null : "DataObject should not be null!"; // NOI18N
        this.dao = dao;
        this.doc = doc;
        if( DebugUtils.TRACE ) System.err.printf("FileHighliter.ctor %s\n", dao.getName());
    }

    /** 
     * Is called after the object has been created
     * and caller has registered it
     */
    public void init() {
        if( DebugUtils.TRACE ) System.err.printf("FileHighliter.init %s\n", dao.getName());
        doc.addDocumentListener(this);
        schedule();
    }
    
    /**
     * Is be called when removing this FileHighlighter
     * (upon document closure)
     */
    public void dispose() {
        if( DebugUtils.TRACE ) System.err.printf("FileHighliter.dispose %s\n", dao.getName());
        disregard = true;
        disposed = true;
        doc.removeDocumentListener(this);
        HintsController.setErrors(doc, FileHighliter.class.getName(), Collections.<ErrorDescription>emptyList());
    }
    
    /**
     * Callback, called by ErrorHighlighter.schedule().
     */
    public void updateAnnotations() {
        if( DebugUtils.TRACE ) System.err.printf("Running updateAnnotations() %s\n", dao.getName());
        if( disposed ) {
            if( DebugUtils.TRACE ) System.err.printf("Already disposed  %s\n", dao.getName());
            return;
        }
        //if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        disregard = false;
        Collection<ErrorInfo> errors = ErrorProvider.getDefault().getErrors(getDataObject(), getDocument());
        if( disregard || disposed ) {
            if( DebugUtils.TRACE ) System.err.printf("Disregarding results %s\n", dao.getName());
            //if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        }
        else {
            if( DebugUtils.TRACE ) System.err.printf("Setting annotations %s\n", dao.getName());
            //if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
            setAnnotations(errors);
        }
    }
    
    private void schedule() {
        ErrorHighlighter.instance().schedule(this);
        if( DebugUtils.TRACE ) System.err.printf("Schedulling updateAnnotations() %s\n", dao.getName());
        //if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        
    }

    /** Implements DocumentListener */
    public void changedUpdate(DocumentEvent e) {
        // do nothing - ondly attributs have changed
    }

    /** Implements DocumentListener */
    public void insertUpdate(DocumentEvent e) {
	documentChanged(e);
    }

    /** Implements DocumentListener */
    public void removeUpdate(DocumentEvent e) {
	documentChanged(e);
    }

    private void documentChanged(DocumentEvent e) {
	assert e.getDocument() == doc;
        disregard = true;
        schedule();
    }
    
    private void setAnnotations(Collection<ErrorInfo> errors) {
        List<ErrorDescription> descs = new LinkedList<ErrorDescription>();
        
        for (ErrorInfo info : errors) {
            descs.add(ErrorDescriptionFactory.createErrorDescription(errorInfoSeverity2EditorSeverity.get(info.getSeverity()), info.getMessage(), doc, info.getLineNumber()));
        }
        
        HintsController.setErrors(doc, FileHighliter.class.getName(), descs);
    }
    
    public boolean isMine(DataObject dao) {
        return this.dao.equals(dao);
    }
    
    public DataObject getDataObject() {
        return dao;
    }

    public BaseDocument getDocument() {
        return doc;
    }

//    /** Gets line count - mostly for debugging/tracing purposes */
//    private int getLineCount() {
//        CharSeq seq = getDocument().getText();
//        int cnt = 0;
//        for (int i = 0; i < seq.length(); i++) {
//            if( seq.charAt(i) == '\n' ) {
//                cnt++;
//            }
//        }
//        return cnt;
//    }
    
}
