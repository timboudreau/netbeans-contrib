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
package org.netbeans.modules.groovy.editor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.groovy.editor.StructureAnalyzer.AnalysisResult;
import org.netbeans.modules.groovy.editor.elements.AstElement;
import org.netbeans.modules.groovy.editor.parser.GroovyParserResult;
import org.netbeans.modules.gsf.api.IndexDocument;
import org.netbeans.modules.gsf.api.IndexDocumentFactory;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 * @todo should index version follow some GSF rules?
 * 
 * @author Tor Norbye
 * @author Martin Adamek
 */
public class GroovyIndexer implements Indexer {

    static final String FIELD_CLASS = "class"; //NOI18N

    private static FileObject preindexedDb;

    public boolean isIndexable(ParserFile file) {
        String extension = file.getExtension();

        if (extension.equals("groovy")) {
            return true;
        }
        return false;
    }

    public List<IndexDocument> index(ParserResult result, IndexDocumentFactory factory) throws IOException {
        GroovyParserResult r = (GroovyParserResult) result;
        ASTNode root = AstUtilities.getRoot(r);

        if (root == null) {
            return null;
        }

        TreeAnalyzer analyzer = new TreeAnalyzer(r, factory);
        analyzer.analyze();
        
        return analyzer.getDocuments();
    }

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            return GroovyIndex.getPreindexUrl(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }
    }

    public String getIndexVersion() {
        return "0.1";
    }

    public String getIndexerName() {
        return "groovy";
    }

    public FileObject getPreindexedDb() {
        if (preindexedDb == null) {
            File preindexed = InstalledFileLocator.getDefault().locate(
                    "preindexed-groovy", "org.netbeans.modules.groovy.editor", false); // NOI18N
            if (preindexed == null || !preindexed.isDirectory()) {
                throw new RuntimeException("Can't locate preindexed directory. Installation might be damaged"); // NOI18N
            }
            preindexedDb = FileUtil.toFileObject(preindexed);
        }
        return preindexedDb;
    }
    
    public boolean acceptQueryPath(String url) {
        return url.indexOf("/ruby2/") == -1 && url.indexOf("/gems/") == -1 && url.indexOf("lib/ruby/") == -1; // NOI18N
    }

    private static class TreeAnalyzer {
        private final ParserFile file;
        private String url;
        private final GroovyParserResult result;
        private BaseDocument doc;
        private IndexDocumentFactory factory;
        private List<IndexDocument> documents = new ArrayList<IndexDocument>();
        
        private TreeAnalyzer(GroovyParserResult result, IndexDocumentFactory factory) {
            this.result = result;
            this.file = result.getFile();
            this.factory = factory;
        }

        List<IndexDocument> getDocuments() {
            return documents;
        }

        public void analyze() throws IOException {
            FileObject fo = file.getFileObject();
            if (result.getInfo() != null) {
                this.doc = NbUtilities.getBaseDocument(fo, true);
            } else {
                // openide.loaders/src/org/openide/text/DataEditorSupport.java
                // has an Env#inputStream method which posts a warning to the user
                // if the file is greater than 1Mb...
                //SG_ObjectIsTooBig=The file {1} seems to be too large ({2,choice,0#{2}b|1024#{3} Kb|1100000#{4} Mb|1100000000#{5} Gb}) to safely open. \n\
                //  Opening the file could cause OutOfMemoryError, which would make the IDE unusable. Do you really want to open it?
                // I don't want to try indexing these files... (you get an interactive
                // warning during indexing
                if (fo.getSize () > 1024 * 1024) {
                    return;
                }
                
                this.doc = NbUtilities.getBaseDocument(fo, true);
            }

            try {
                url = fo.getURL().toExternalForm();

                // Make relative URLs for urls in the libraries
                url = GroovyIndex.getPreindexUrl(url);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }

            AnalysisResult ar = result.getStructure();
            List<?extends AstElement> children = ar.getElements();

            if ((children == null) || (children.size() == 0)) {
                return;
            }
            
            IndexDocument document = factory.createDocument(40); // TODO - measure!
            documents.add(document);
            
//            System.out.println("### children: " + children);
            
            for (AstElement child : children) {
                switch (child.getKind()) {
                    case CLASS:
                        indexClass(child, document);
                        break;
                }
            }

        }

        private void indexClass(AstElement element, IndexDocument document) {
            final String name = element.getName();
            document.addPair(FIELD_CLASS, name, true);
        }

    }
    
    
}
