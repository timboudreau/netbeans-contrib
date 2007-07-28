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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.hints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source.Priority;
import org.netbeans.api.retouche.source.support.EditorAwareSourceTaskFactory;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ErrorHints implements CancellableTask<CompilationInfo> {

    public void cancel() {
    }

    public void run(CompilationInfo parameter) throws Exception {
        LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
        
        Map<FileObject, List<ErrorDescription>> sortedErrors = sortErrors(lpr.getErrors());
        List<ErrorDescription> errors = sortedErrors.get(parameter.getFileObject());
        
        if (errors == null) errors = Collections.<ErrorDescription>emptyList();
        
        HintsController.setErrors(parameter.getFileObject(), ErrorHints.class.getName(), errors);
    }

    private Map<FileObject, List<ErrorDescription>> sortErrors(Collection<ErrorDescription> errors) {
        Map<FileObject, List<ErrorDescription>> result = new HashMap<FileObject, List<ErrorDescription>>();
        
        for (ErrorDescription err : errors) {
            List<ErrorDescription> errs = result.get(err.getFile());
            
            if (errs == null) {
                result.put(err.getFile(), errs = new ArrayList<ErrorDescription>());
            }
            
            errs.add(err);
        }
        
        return result;
    }
    
    public static final class Factory extends EditorAwareSourceTaskFactory {

        public Factory() {
            super(Phase.RESOLVED, Priority.NORMAL);
        }

        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new ErrorHints();
        }
        
    }
    
}
