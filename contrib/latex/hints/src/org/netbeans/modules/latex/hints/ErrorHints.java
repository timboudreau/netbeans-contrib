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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
