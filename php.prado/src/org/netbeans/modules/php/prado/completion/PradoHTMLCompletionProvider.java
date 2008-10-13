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
package org.netbeans.modules.php.prado.completion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.SourceModel;
import org.netbeans.modules.gsf.api.SourceModelFactory;
import org.netbeans.modules.php.editor.index.IndexedClass;
import org.netbeans.modules.php.editor.index.IndexedFunction;
import org.netbeans.modules.php.editor.index.PHPIndex;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.BodyDeclaration.Modifier;
import org.netbeans.modules.php.prado.PageLanguage;
import org.netbeans.modules.php.prado.lexer.LexerUtilities;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Pisl
 */
public class PradoHTMLCompletionProvider implements CompletionProvider {

    public PradoHTMLCompletionProvider() {

    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        AsyncCompletionTask task = null;
        if ((queryType & COMPLETION_QUERY_TYPE & COMPLETION_ALL_QUERY_TYPE) != 0) {
            task = new AsyncCompletionTask(new Query(), component);
        }
//        else if (queryType == DOCUMENTATION_QUERY_TYPE) {
//            task = new AsyncCompletionTask(new DocQuery(null), component);
//        }
        return task;
    }

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }

    static class Query extends AsyncCompletionQuery {

        private enum CC_CONTEXT {

            NONE, OPEN_TAG, ARGUMENT
        }

        @Override
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            //test whether we are just in text and eventually close the opened completion
            //this is handy after end tag autocompletion when user doesn't complete the
            //end tag and just types a text
            //test whether the user typed an ending quotation in the attribute value
            BaseDocument document = (BaseDocument) doc;
            String componentPrefix = PageLanguage.getComponentPrefix() + ":";   //NOI18N
            document.readLock();
            try {
                TokenSequence<HTMLTokenId> ts = LexerUtilities.getHTMLTokenSequence(doc, caretOffset);
                if (ts != null) {
                    ts.move(caretOffset);
                    // to be sure that ts has  a tokens
                    if (ts.movePrevious() || ts.moveNext()) {
                        Token token = ts.token();
                        while (ts.offset() + token.length() < caretOffset) {
                            if (!ts.moveNext()) {
                                resultSet.finish();
                                return;
                            }
                            token = ts.token();
                        }

                        String preText = "";    //NOI18N
                        CC_CONTEXT context = CC_CONTEXT.NONE;
                        int delta = 0;

                        FileObject fileObject = NbEditorUtilities.getFileObject(doc);
                        if ((caretOffset - ts.offset()) > 0) {
                            preText = token.text().subSequence(0, caretOffset - ts.offset()).toString();
                        }

                        if (token.id() == HTMLTokenId.TAG_OPEN) {
                            String comp = (preText.length() < componentPrefix.length() ? componentPrefix.substring(0, preText.length()) : componentPrefix);
                            if (preText.startsWith(comp)) {
                                context = CC_CONTEXT.OPEN_TAG;
                                delta = 0;
                            }
                        } else if (token.id() == HTMLTokenId.TEXT) {
                            if (preText.endsWith("<")) {    //NOI18N
                                preText = "";               //NOI18N
                                context = CC_CONTEXT.OPEN_TAG;
                                delta = 1;
                            }
                        } else if (token.id() == HTMLTokenId.WS || token.id() == HTMLTokenId.ARGUMENT) {
                            int offset = ts.offset();
                            Token origToken = token;
                            boolean hasPrevious = ts.movePrevious();
                            if (hasPrevious) {
                                token = ts.token();
                                // don't show arguments, if it's already defined
                                List<String> usedArgument = new ArrayList<String>();
                                while (hasPrevious && (token.id() == HTMLTokenId.WS || token.id() == HTMLTokenId.ARGUMENT || token.id() == HTMLTokenId.VALUE || token.id() == HTMLTokenId.OPERATOR)) {
                                    if (token.id() == HTMLTokenId.ARGUMENT) {
                                        usedArgument.add(token.text().toString());
                                    }
                                    if (ts.movePrevious()) {
                                        token = ts.token();
                                    } else {
                                        hasPrevious = false;
                                    }

                                }
                                if (token.id() == HTMLTokenId.TAG_OPEN) {
                                    String tag = token.text().toString();

                                    if (tag.startsWith(componentPrefix)) {
                                        List<String> arguments = getArguments(fileObject, tag.substring(componentPrefix.length()), preText.trim());
                                        for (String argument : arguments) {
                                            if (!usedArgument.contains(argument)) {
                                                resultSet.addItem(new PradoHTMLCompletionItem.Property(argument, caretOffset - preText.trim().length()));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (context == CC_CONTEXT.OPEN_TAG) {
                            String tagPrefix = (preText.length() < (componentPrefix.length()) ? "" : preText.substring(componentPrefix.length()));
                            List<String> components = getComponents(fileObject, tagPrefix);
                            if (preText.length() < componentPrefix.length()) {
                                resultSet.addItem(new PradoHTMLCompletionItem.Component(componentPrefix, ts.offset() + delta));
                            }
                            for (String component : components) {
                                if (component.startsWith(preText)) {
                                    resultSet.addItem(new PradoHTMLCompletionItem.Component(component, ts.offset() + delta));
                                }
                            }
                        }
                    }
                } 
            } finally {
                document.readUnlock();
            }

            resultSet.finish();
        }
    }

    private static class ComponentAnalyzer implements CancellableTask<CompilationInfo> {

        private final List<String> tags;
        private final ArrayList<String> knownTagNames = new ArrayList<String>();
        private final String prefix;

        public ComponentAnalyzer(List<String> result, String prefix) {
            this.tags = result;
            this.prefix = prefix;
        }

        public void cancel() {
        }

        public void run(CompilationInfo cInfo) throws Exception {
            String mimetype = PHPTokenId.languageInPHP().mimeType();
            PHPIndex index = PHPIndex.get(cInfo.getIndex(mimetype));
            PHPParseResult result = (PHPParseResult) cInfo.getEmbeddedResult(mimetype, 0);
            String usedPrefix = prefix;
            if (prefix == null || prefix.trim().length() == 0) {
                // TODO probably it should be looking through all classes not only the classes starting with T
                usedPrefix = "T";       // NOI18N
            }

            Collection<IndexedClass> classes = index.getClasses(result, "T", NameKind.PREFIX);
            // class name, superclass
            HashMap<String, String> superClassMap = new HashMap<String, String>();
            for (IndexedClass indexedClass : classes) {
                superClassMap.put(indexedClass.getName(), indexedClass.getSuperClass());
            }
            for (IndexedClass indexedClass : classes) {
                if (indexedClass.getName().startsWith(usedPrefix)) {
                    String superClass = indexedClass.getSuperClass();
                    boolean isComponent = false;
                    if (superClass != null) {
                        if (possibleSuperClasses.contains(superClass)) {
                            isComponent = true;
                        } else {
                            if (isComponent(indexedClass.getName(), superClassMap)) {
                                isComponent = true;
                                possibleSuperClasses.add(superClass);
                            }
                        }
                    }
                    if (isComponent && !knownTagNames.contains(indexedClass.getName())) {
                        knownTagNames.add(indexedClass.getName());
                        tags.add(PageLanguage.getComponentPrefix() + ":" + indexedClass.getName());
                    }
                }
            }

        }

        private boolean isComponent(String className, HashMap<String, String> superClassMap) {
            String superClass = superClassMap.get(className);
            if (superClass == null) {
                return false;
            }
            if (possibleSuperClasses.contains(superClass)) {
                return true;
            }
            return isComponent(superClass, superClassMap);
        }
    }

    private static List<String> possibleSuperClasses = null;
    
    private static List<String> getComponents(FileObject fileObject, String prefix) {
        List<String> knownComponents = new ArrayList<String>();
        possibleSuperClasses = new ArrayList<String>();
        possibleSuperClasses.add("TComponent");  //NOI18N
        SourceModel model = SourceModelFactory.getInstance().getModel(fileObject);
        try {
            model.runUserActionTask(new ComponentAnalyzer(knownComponents, prefix), true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return knownComponents;
    }

    private static List<String> getArguments(FileObject fileObject, String component, String prefix) {
        List<String> arguments = new ArrayList<String>();
        SourceModel model = SourceModelFactory.getInstance().getModel(fileObject);
        try {
            model.runUserActionTask(new ArgumentFinder(component, arguments, prefix), true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return arguments;
    }

    private static class ArgumentFinder implements CancellableTask<CompilationInfo> {

        private final List<String> arguments;
        private final String prefix;
        private final String component;

        public ArgumentFinder(String component, List<String> arguments, String prefix) {
            this.arguments = arguments;
            this.prefix = prefix;
            this.component = component;
        }

        public void cancel() {
        }

        public void run(CompilationInfo cInfo) throws Exception {
            arguments.addAll(CompletionUtils.getComponentProperties(cInfo, component, prefix));
        }
    }
}
