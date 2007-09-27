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
package org.netbeans.modules.latex.loop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ElementHandle;
import org.netbeans.api.gsf.OccurrencesFinder;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.gsf.ParseEvent;
import org.netbeans.api.gsf.ParseListener;
import org.netbeans.api.gsf.Parser;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.PositionManager;
import org.netbeans.api.gsf.SemanticAnalyzer;
import org.netbeans.api.gsf.SourceFileReader;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.impl.CommandUtilitiesImpl;
import org.netbeans.modules.latex.model.command.parser.CommandParser;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.parser.StructuralParserImpl;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGSFParser implements Parser {

    private static final Map<FileObject, StructuralParserImpl> file2Root = new WeakHashMap<FileObject, StructuralParserImpl>();
    
    public LaTeXGSFParser() {
    }

    public void parseFiles(List<ParserFile> files, ParseListener listener, SourceFileReader reader) {
        assert files.size() == 1;
        
        try {
            List<ErrorDescription> errors = new LinkedList<ErrorDescription>();
            FileObject file = files.get(0).getFileObject();
            FileObject main = null;
            
            for (LaTeXSourceFactory f : Lookup.getDefault().lookupAll(LaTeXSourceFactory.class)) {
                if (f.supports(file)) {
                    main = f.findMainFile(file);
                    System.err.println("f=" + f + ", main=" + main);
                    if (main != null)
                        break;
                }
            }
            
            assert main != null;
            
            final DocumentNode dn = reparseImpl(main, errors);
            
            StructuralParserImpl p = file2Root.get(main);
            
            if (p == null) {
                file2Root.put(main, p = new StructuralParserImpl());
            }
            
            StructuralElement structuralRoot = p.parse(dn, errors);
            
            listener.finished(new ParseEvent(ParseEvent.Kind.PARSE, files.get(0), new LaTeXParserResult(files.get(0), main, dn, structuralRoot, new CommandUtilitiesImpl(dn), errors)));
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    public PositionManager getPositionManager() {
        return new PositionManager() {
            public OffsetRange getOffsetRange(Element file, Element object) {
                assert object instanceof Node;
                
                return new OffsetRange(((Node) object).getStartingPosition().getOffsetValue(), ((Node) object).getEndingPosition().getOffsetValue());
            }
        };
    }

    public SemanticAnalyzer getSemanticAnalysisTask() {
        return null;
    }

    public OccurrencesFinder getMarkOccurrencesTask(int caretPosition) {
        return null;
    }

    public <T extends Element> ElementHandle<T> createHandle(CompilationInfo info, T element) {
        return null;
    }

    public <T extends Element> T resolveHandle(CompilationInfo info, ElementHandle<T> handle) {
        return null;
    }

    private DocumentNode reparseImpl(FileObject main, List<ErrorDescription> errors) throws IOException {
//        try {
            long start = System.currentTimeMillis();
            
            Collection newDocuments = new HashSet();
            
            long startParsing = System.currentTimeMillis();
            
            DocumentNode document = new CommandParser().parse(main, newDocuments, errors);
            
            long endParsing = System.currentTimeMillis();
            
            //XXX:
//            synchronized (REPARSE_IMPL_LOCK) {
//                DocumentNode oldDocument         = document;
//                Set          oldDocumentHardRefs = getDocumentsHardRefs();
//                
//                Collection<ErrorDescription> errors = new ArrayList();
//                
//                if (documents == null)
//                    documents = new HashSet();
//                else
//                    documents.clear();
//                
//                Iterator it = newDocuments.iterator();
//                
//                while (it.hasNext()) {
//                    documents.add(new WeakReference(it.next()));
//                }
//                
//                fireNodesRemoved(Collections.singletonList(oldDocument));
//                fireNodesAdded(Collections.singletonList(document));
//                
//                Set          newDocumentHardRefs = getDocumentsHardRefs();
//                
//                resolveListeners(oldDocumentHardRefs, newDocumentHardRefs);
//                
//                setErrors(document, errors);
//                
//                synchronized (DOCUMENT_VERSION_LOCK) {
//                    documentVersion++;
//                }
//            }
            
            long end = System.currentTimeMillis();
            
//            if (reparseTimeDebug) {
//                TimesCollector.getDefault().reportTime((FileObject) getMainFile(), "latex-parse", "LaTeX Parse", (endParsing - startParsing));
//                TimesCollector.getDefault().reportTime((FileObject) getMainFile(), "latex-parse-complete", "LaTeX Parse Complete", (end - start));
//                System.err.println("Reparse done, main file=" + getMainFile() + ", parse time=" + (endParsing - startParsing) + "ms, complete time=" + (end - start) + "ms.");
//            }
            
//        } finally {
//            pcs.firePropertyChange("parsing", Boolean.TRUE, Boolean.FALSE);
//            if (isUpToDate())
//                setReparseStateDebug(REPARSE_DEBUG_ALL_VALID);
//        }
        
        return document;
    }
}
