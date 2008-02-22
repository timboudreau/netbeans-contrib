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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.MathNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source.Priority;
import org.netbeans.napi.gsfret.source.support.EditorAwareSourceTaskFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class HintsProcessor implements CancellableTask<CompilationInfo> {

    private AtomicBoolean cancel = new AtomicBoolean();
    
    public void cancel() {
    }

    public void run(final CompilationInfo info) throws Exception {
        List<ErrorDescription> hints = compute(info, providers, cancel);
        
        if (hints == null) {
            hints = Collections.<ErrorDescription>emptyList();
        }
        
        HintsController.setErrors(info.getFileObject(), HintsProcessor.class.getName(), hints);
    }
    
    static List<ErrorDescription> compute(final CompilationInfo info, final List<HintProvider> providers, final AtomicBoolean cancel) throws Exception {
        final Document doc = info.getDocument();

        if (doc == null) {
            return null;
        }

        final List<ErrorDescription> hints = new LinkedList<ErrorDescription>();
        LaTeXParserResult lpr = LaTeXParserResult.get(info);
        
        lpr.getDocument().traverse(new DefaultTraverseHandler() {
            @Override
            public boolean commandStart(CommandNode node) {
                if (cancel.get()) {
                    return false;
                }
                
                handleNode(info, providers, hints, node);
                
                return !cancel.get();
            }
            @Override
            public boolean argumentStart(ArgumentNode node) {
                if (cancel.get()) {
                    return false;
                }

                handleNode(info, providers, hints, node);
                
                return !cancel.get();
            }
            @Override
            public boolean blockStart(BlockNode node) {
                if (cancel.get()) {
                    return false;
                }

                handleNode(info, providers, hints, node);
                
                return !cancel.get();
            }
            @Override
            public boolean textStart(TextNode node) {
                if (cancel.get()) {
                    return false;
                }

                handleNode(info, providers, hints, node);
                
                return !cancel.get();
            }
            @Override
            public boolean mathStart(MathNode node) {
                if (cancel.get()) {
                    return false;
                }

                handleNode(info, providers, hints, node);
                
                return !cancel.get();
            }
        });
        
        return hints;
    }

    private static List<HintProvider> providers;
    
    static {
        providers = new LinkedList<HintProvider>();
        
        providers.add(new CheckCountersHint());
        providers.add(new UnknownCiteHint());
    }
        
    static void handleNode(CompilationInfo info, List<HintProvider> providers, List<ErrorDescription> hints, Node n) {
        for (HintProvider p : providers) {
            if (p.accept(info, n)) {
                try {
                    List<ErrorDescription> h = p.computeHints(info, n);

                    if (h != null) {
                        for (ErrorDescription ed : h) {
                            if (info.getFileObject().equals(ed.getFile())) {
                                hints.add(ed);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
    public static final class Factory extends EditorAwareSourceTaskFactory {

        public Factory() {
            super(Phase.RESOLVED, Priority.NORMAL);
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new HintsProcessor();
        }
        
    }
    
}
