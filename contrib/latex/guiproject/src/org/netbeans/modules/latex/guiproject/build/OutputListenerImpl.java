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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
