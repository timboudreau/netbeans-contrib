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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.guiproject.build;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.Utilities;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

public final class OutputListenerImpl implements OutputListener {
    
    private FileObject file;
    private int line;
    private String message;
    
    public OutputListenerImpl(FileObject file, int line, String message) {
        this.file = file;
        this.line = line;
        this.message = message;
    }
    
    public void outputLineSelected(OutputEvent ev) {
        //next action:
        try {
            DataObject od = DataObject.find(file);
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            
            if (lc != null) {
                Line l = lc.getLineSet().getOriginal(line - 1);
                
                if (!l.isDeleted()) {
                    l.show(Line.SHOW_GOTO);
                    ErrorAnnotation.getInstance().attach(l, message);
                }
            }
        }  catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public void outputLineAction(OutputEvent ev) {
        //goto:
        try {
            DataObject od = DataObject.find(file);
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            
            if (lc != null) {
                Line l = lc.getLineSet().getOriginal(line - 1);
                
                if (!l.isDeleted()) {
                    l.show(Line.SHOW_GOTO);
                    ErrorAnnotation.getInstance().attach(l, message);
                }
            }
        }  catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public void outputLineCleared(OutputEvent ev) {
        ErrorAnnotation.getInstance().detach(null);
    }
    
    /** Implements Annotation */
    private static class ErrorAnnotation extends Annotation implements PropertyChangeListener {
        private static ErrorAnnotation instance;
        private Line currentLine;
        private String shortDescription = "";
        
        public static ErrorAnnotation getInstance() {
            if (instance == null) {
                instance = new ErrorAnnotation();
            }
            
            return instance;
        }
        
        /** Returns name of the file which describes the annotation type.
         * The file must be defined in module installation layer in the
         * directory "Editors/AnnotationTypes"
         * @return  name of the anotation type */
        public String getAnnotationType() {
            return "org-netbeans-modules-latex-compiler-error"; // NOI18N
        }
        
        /** Returns the tooltip text for this annotation.
         * @return  tooltip for this annotation */
        public String getShortDescription() {
            return shortDescription;
        }
        
        public void attach(Line line, String shortDescription) {
            this.shortDescription = shortDescription;
            if (currentLine != null) {
                detach(currentLine);
            }
            currentLine = line;
            super.attach(line);
            line.addPropertyChangeListener(this);
        }
        
        public void detach(Line line) {
            if (line == currentLine || line == null) {
                currentLine = null;
                Annotatable at = getAttachedAnnotatable();
                if (at != null) {
                    at.removePropertyChangeListener(this);
                }
                detach();
            }
        }
        
        public void propertyChange(PropertyChangeEvent ev) {
            if (Annotatable.PROP_TEXT.equals(ev.getPropertyName())) {
                detach(null);
            }
        }
    }
    
}
