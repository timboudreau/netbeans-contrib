/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.latex.ui.preview;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.latex.model.IconsStorage.ChangeableIcon;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.ParagraphNode;
import org.netbeans.modules.latex.ui.IconsStorageImpl;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source.Priority;
import org.netbeans.napi.gsfret.source.support.CaretAwareSourceTaskFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class PreviewTask implements CancellableTask<CompilationInfo> {

    private PreviewTopComponent comp;

    public PreviewTask(PreviewTopComponent comp) {
        this.comp = comp;
    }
    
    public void cancel() {
    }

    public void run(CompilationInfo parameter) throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            LaTeXParserResult lpr = LaTeXParserResult.get(parameter);
            int pos = CaretAwareSourceTaskFactory.getLastPosition(parameter.getFileObject());

            Node n = lpr.getCommandUtilities().findNode(parameter.getDocument(), pos); //XXX: getDocument() == null;

            while (n != null && !(n instanceof ParagraphNode)) {
                n = n.getParent();
            }

            if (n == null) {
                return;
            }

            final ChangeableIcon i = IconsStorageImpl.getDefaultImpl().getIconForText(n.getFullText().toString());
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (comp != null) {
                        comp.setIcon(i);
                    }
                }
            });
        } finally {
            long endTime = System.currentTimeMillis();
            
            Logger.getLogger("TIMER").log(Level.FINE, "PreviewTask", new Object[] {parameter.getFileObject(), endTime - startTime});
        }
    }

    public static final class FactoryImpl extends CaretAwareSourceTaskFactory {

        public FactoryImpl() {
            super(Phase.RESOLVED, Priority.MIN);
        }

        @Override
        public synchronized List<FileObject> getFileObjects() {
            if (comp != null) {
                return super.getFileObjects();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        protected synchronized CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new PreviewTask(comp);
        }
        
        public static FactoryImpl getInstance() {
            return Lookup.getDefault().lookup(FactoryImpl.class);
        }
        
        private PreviewTopComponent comp;
        
        public synchronized void setComponent(PreviewTopComponent comp) {
            this.comp = comp;
            fileObjectsChanged();
        }
    }
        
}
