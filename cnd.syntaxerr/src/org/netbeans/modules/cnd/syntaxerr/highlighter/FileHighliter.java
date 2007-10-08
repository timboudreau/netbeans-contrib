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
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.CharSeq;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorInfo;
import org.netbeans.modules.cnd.syntaxerr.provider.ErrorProvider;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.text.NbDocument;

/**
 * 
 * @author Vladimir Kvashin
 */
/* package-local */
class FileHighliter implements DocumentListener {

    private boolean upToDate = false;
    private boolean hasErrors = false;
    private boolean hasWarnings = false;
    
    private DataObject dao;
    private BaseDocument doc;
    
    private boolean disregard = false;
    private boolean disposed = false;
    
    /** A list of all annotations currently added to a fuke */
    private Collection<Annotation> annotations = new ArrayList<Annotation>();

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
        clearAnnotations();
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
        clearAnnotations();
        for (ErrorInfo info : errors) {
            attachAnnotation(info);
        }
    }
    
    private void clearAnnotations() {
        for (Annotation annotation : annotations) {
            NbDocument.removeAnnotation((StyledDocument) doc, annotation);
        }
        annotations.clear();
    }
    
    private final void attachAnnotation(final ErrorInfo info) {
        if (SwingUtilities.isEventDispatchThread()) {
            attachAnnotationImpl(info);
        } else {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        attachAnnotationImpl(info);
                    }
                }
            );
        }
    }
    
    private final void attachAnnotationImpl(ErrorInfo info) {
        final Annotation annotation = createAnnotation(info);
        int lineNo = info.getLineNumber()-1;
        //if( DebugUtils.TRACE ) System.err.printf("\tgetting original line for %d (of %d) \n", lineNo, getLineCount());
        LineCookie lc = dao.getLookup().lookup(LineCookie.class);
        final Line line = lc.getLineSet().getCurrent(lineNo);
        //if( DebugUtils.TRACE ) System.err.printf("\t\tgot %d \n", line.getLineNumber());
        annotation.attach(line);
        line.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev){
                String type = ev.getPropertyName();
                if (type.equals(Annotatable.PROP_TEXT)){
                    line.removePropertyChangeListener(this);
		    // annotation.detach() shows an exception if the line has been already deleted
		    int lineCount = NbDocument.findLineNumber((StyledDocument) doc, doc.getLength());
		    if( line.getLineNumber() < lineCount ) {
			annotation.detach();
		    }
                    annotations.remove(annotation);
                }
            }
        });
        annotations.add(annotation);
    }
    
    private Annotation createAnnotation(ErrorInfo info) {
        Annotation annotation = new AnnotationImpl(info);
        if (info.getSeverity() == ErrorInfo.Severity.ERROR) {
            hasErrors = true;
        } else if (info.getSeverity() == ErrorInfo.Severity.WARNING) {
            hasWarnings = true;
        }
        return annotation;
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
