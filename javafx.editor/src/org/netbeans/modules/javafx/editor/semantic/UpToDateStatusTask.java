/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx.editor.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.source.CancellableTask;
import org.netbeans.api.javafx.source.CompilationInfo;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
/**
 *
 * @author David Strupl
 */
class UpToDateStatusTask implements CancellableTask<CompilationInfo> {
    
    private static final Logger LOGGER = Logger.getLogger(UpToDateStatusTask.class.getName());
    private static final boolean LOGGABLE = LOGGER.isLoggable(Level.FINE);
    
    private FileObject file;
    private AtomicBoolean cancel = new AtomicBoolean();

    UpToDateStatusTask(FileObject file) {
        this.file = file;
    }
    
    private UpToDateStatusTask() {
    }
    
    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo info) {
        cancel.set(false);
        process(info);
    }

    private void process(CompilationInfo info) {
        try {
            DataObject od = DataObject.find(file);
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            Document doc = ec.getDocument();
            if (doc == null) {
                return;
            }

            List<Diagnostic> diag = info.getDiagnostics();
            
            ArrayList<ErrorDescription> c = new ArrayList<ErrorDescription>();
            
            for (Diagnostic d : diag) {
                c.add(ErrorDescriptionFactory.createErrorDescription(
                        Severity.ERROR, d.getMessage(Locale.getDefault()),
                        doc, (int)d.getLineNumber()));
            }
            // this is temporary for breh:
            if (!Boolean.getBoolean("org.netbeans.modules.javafx.editor.unstable.disable")) {
                HintsController.setErrors(doc, "semantic-highlighter", c);
            }
            
            UpToDateStatusProviderImpl p = UpToDateStatusProviderImpl.forDocument(doc);
            p.refresh(diag, UpToDateStatus.UP_TO_DATE_OK);
            
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void log(String s) {
        if (LOGGABLE) {
            LOGGER.fine(s);
        }
    }

}
