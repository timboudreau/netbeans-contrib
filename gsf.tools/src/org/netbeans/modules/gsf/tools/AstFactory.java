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
package org.netbeans.modules.gsf.tools;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.RescheduleListener;
import org.netbeans.api.retouche.source.Source;
import org.netbeans.api.retouche.source.support.EditorAwareSourceTaskFactory;
import org.netbeans.modules.gsf.browser.AstViewer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;


/**
 * Source factory for displaying the AST in the AST viewer when
 * it has been parsed
 *
 * @author Tor Norbye
 */
public class AstFactory extends EditorAwareSourceTaskFactory {
    /**
     * Creates a new instance of GsfHintsFactory
     */
    public AstFactory() {
        super(Phase.RESOLVED, Source.Priority.BELOW_NORMAL);
    }

    public CancellableTask<CompilationInfo> createTask(FileObject file) {
        return new AstProvider(file);
    }

    public final class AstProvider implements CancellableTask<CompilationInfo> {
        private FileObject file;
        private boolean cancel;

        private AstProvider(FileObject file) {
            this.file = file;
        }

        synchronized boolean isCanceled() {
            return cancel;
        }

        public synchronized void cancel() {
            cancel = true;
        }

        synchronized void resume() {
            cancel = false;
        }

        public void run(CompilationInfo info) {
            resume();

            final ParserResult result = info.getParserResult();
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        AstViewer viewer = AstViewer.findInstance();
                        if (viewer == null || !viewer.isShowing()) {
                            return;
                        }
                        
                        AstViewer.findInstance().refresh(file, result);
                    }
                });
        }
    }
}
