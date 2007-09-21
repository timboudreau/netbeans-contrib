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
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    
    private boolean changed = false;
    
    /** A list of all annotations currently added to a fuke */
    private Collection<Annotation> annotations = new ArrayList<Annotation>();

    public FileHighliter(DataObject dao, BaseDocument doc) {
        this.dao = dao;
        this.doc = doc;
        if( DebugUtils.TRACE ) System.err.printf("FileHighliter.ctor");
    }

    /** 
     * Is called after the object has been created
     * and caller has registered it
     */
    public void init() {
        doc.addDocumentListener(this);
        schedule();
    }
    
    /**
     * Is be called when removing this FileHighlighter
     * (upon document closure)
     */
    public void dispose() {
        doc.removeDocumentListener(this);
    }
    
    /**
     * Callback, called by ErrorHighlighter.schedule().
     */
    public void updateAnnotations() {
        if( DebugUtils.TRACE ) System.err.printf("Running updateAnnotations()\n");
        if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        changed = false;
        Collection<ErrorInfo> errors = ErrorProvider.getDefault().getErrors(getDataObject(), getDocument());
        if( changed ) {
            if( DebugUtils.TRACE ) System.err.printf("The file has been changed - disregarding results\n");
            if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        }
        else {
            if( DebugUtils.TRACE ) System.err.printf("Setting annotations\n");
            if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
            setAnnotations(errors);
        }
    }
    
    private void schedule() {
        ErrorHighlighter.instance().schedule(this);
        if( DebugUtils.TRACE ) System.err.printf("Schedulling updateAnnotations()\n");
        if( DebugUtils.TRACE ) System.err.printf("(line count = %d)\n", getLineCount());
        
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
        changed = true;
        schedule();
    }
    
    private void setAnnotations(Collection<ErrorInfo> errors) {
        for (Annotation annotation : annotations) {
            NbDocument.removeAnnotation((StyledDocument) doc, annotation);
        }
        annotations.clear();
        for (ErrorInfo info : errors) {
            attachAnnotation(info);
        }
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
                if ((type ==null) || type.equals(Annotatable.PROP_TEXT)){
                    line.removePropertyChangeListener(this);
                    annotation.detach();
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

    public DataObject getDataObject() {
        return dao;
    }

    public BaseDocument getDocument() {
        return doc;
    }

    /** Gets line count - mostly for debugging/tracing purposes */
    private int getLineCount() {
        CharSeq seq = getDocument().getText();
        int cnt = 0;
        for (int i = 0; i < seq.length(); i++) {
            if( seq.charAt(i) == '\n' ) {
                cnt++;
            }
        }
        return cnt;
    }
    
}
