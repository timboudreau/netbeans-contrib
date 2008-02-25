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
package org.netbeans.modules.erlang.platform.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.Element;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.Error;
import org.netbeans.modules.gsf.api.ParseEvent;
import org.netbeans.modules.gsf.api.ParseListener;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.PositionManager;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.Severity;
import org.netbeans.modules.gsf.api.SourceFileReader;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.ParseException;
import org.netbeans.modules.erlang.editing.Erlang;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.erlang.editing.semantic.ErlContext;
import org.netbeans.modules.erlang.editing.semantic.ErlangSemanticAnalyser;
import org.netbeans.modules.gsf.spi.DefaultError;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;


/**
 * Wrapper around Erlang language parser to parse a file into an AST.
 *
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public class ErlangLanguageParser implements Parser {

    private static Language language;

    private PositionManager positions = new ErlangPositionManager();

    private static InputOutput io = IOProvider.getDefault().getIO("Info", false);

    public ErlangLanguageParser() {
    }

    private static String asString(CharSequence sequence) {
        if (sequence instanceof String) {
            return (String) sequence;
        } else {
            return sequence.toString();
        }
    }

    /** Parse the given set of files, and notify the parse listener for each transition
     * (compilation results are attached to the events )
     */
    public void parseFiles(Parser.Job job) {
        ParseListener listener = job.listener;
        SourceFileReader reader = job.reader;

        for (ParserFile file : job.files) {
            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
            listener.started(beginEvent);

            if (language == null) {
                try {
                    language = LanguagesManager.get().getLanguage(Erlang.MIME_TYPE);
                } catch (LanguageDefinitionNotFoundException ex) {
                    listener.exception(ex);
                }
            }

            ParserResult result = null;
            InputStream is = null;
            try {
                FileObject fo = file.getFileObject();
                ASTNode astRoot = null;
                ErlContext rootCtx = null;

                ErlangSemanticAnalyser semanticAnalyser = ErlangSemanticAnalyser.getAnalyserForIndexing();

                /**
                 * check if this fo's astRoot has been parsed ready, since if it's
                 * already opened in editor, the astRoot may have been there
                 */
//                ErlangSemanticParser semanticParser = ErlangSemanticParser.getParser(fo);
//                if (semanticParser == null) {
//                    semanticParser = ErlangSemanticParser.getOneTimeUsingParser();
//                } else {
//                    /** this fo should have been opened in editor, we'll wait for until it's parsed */
//
//                    State state = semanticParser.getState();
//                    do {
//                        /** well, this thread maybe invoked before editor parser, so we wait for some time first */
//                        try {
//                            Thread.currentThread().sleep(20);
//                            state = semanticParser.getState();
//                        } catch (InterruptedException ex) {
//                            Exceptions.printStackTrace(ex);
//                        }
//                    } while (!(state == State.OK || state == State.ERROR));
//
//                    astRoot = semanticParser.getAstRoot();
//                    erlRoot = semanticParser.getErlRoot();
//                }
                /**
                 * @Notice: if this is called from a document changed event, the document
                 * content will be composited to a file object first, then call: Source.forFileObject(fo)
                 * @see org.netbeans.api.retouche.source.Source.EditorRegistryListener.caretUpdate ->
                 * @see org.netbeans.api.retouche.source.Source.forDocument(Docement doc) ->
                 * @see org.netbeans.api.retouche.source.Source.forFileObject(FileObject fileObject)
                 * Then last, in caretUpdate, will call resetState(false, false), which will request
                 * a new reparse task
                 */

                if (astRoot == null) {
                    /**
                     * We are lucky here since reader only open file when call reader.read(),
                     * don't worry about to open my input stream.
                     */                    
                    //CharSequence cs = reader.read(file);
                    //ASTNode node = ErlyBirdNode.parse(cs);
                    is = fo.getInputStream();
                    if (file.isPlatform()) {
                        io.getOut().println("File: " + fo.getPath());
                        io.getOut().print("Parsing: ");
                    }
                    long start = System.currentTimeMillis();
                    astRoot = language.parse(is);
                    /** 
                     * since any ast parsing will call Erlang#process, we need not to 
                     * ask semanticParser do its parse(...) again, just fetch erlRoot directly 
                     * please see Erlang.nbs#AST
                     */
                    rootCtx = semanticAnalyser.analyse(astRoot);
                    if (file.isPlatform()) io.getOut().println((System.currentTimeMillis() - start) + "ms");
                }

                /** We may in batching parsing, such as under indexing, so release it manually here*/
                semanticAnalyser = null;

                AstRootElement rootElement = new AstRootElement(fo, astRoot);
                result = new ErlangLanguageParserResult(this, file, rootElement, astRoot, rootCtx);
            } catch (IOException ex) {
                listener.exception(ex);
            } catch (ParseException ex) {
                listener.exception(ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, result);
            listener.finished(doneEvent);
        }
    }


    private void notifyError(ParseListener listener, ParserFile file, String key, Severity severity, String description, String details, int offset) {
        Error error =
            new DefaultError(key, description, details, file.getFileObject(),
                offset, offset, severity);
        listener.error(error);
    }

    public PositionManager getPositionManager() {
        return positions;
    }

    public SemanticAnalyzer getSemanticAnalysisTask() {
        return null;
    }

    public org.netbeans.modules.gsf.api.OccurrencesFinder getMarkOccurrencesTask(int caretPosition) {
        //        OccurrencesFinder finder = new OccurrencesFinder();
        //        finder.setCaretPosition(caretPosition);
        //        return finder;
        /** @TODO */
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ElementHandle createHandle(ParserResult result, final Element object) {
        return null;
//        Node root = AstUtilities.getRoot(result);
//
//        return new RubyElementHandle(root, object, result.getFile().getFileObject());
    }

    @SuppressWarnings(value = "unchecked")
    public static Element resolveHandle(CompilationInfo info, ElementHandle handle) {
        /** @TODO */
        return null;
        //        RubyElementHandle h = (RubyElementHandle)handle;
        //        Node oldRoot = h.root;
        //        Node oldNode;
        //
        //        if (h.object instanceof KeywordElement || h.object instanceof IndexedElement) {
        //            // Not tied to a tree
        //            return (T)h.object;
        //        }
        //
        //        if (h.object instanceof AstElement) {
        //            oldNode = ((AstElement)h.object).getNode(); // XXX Make it work for DefaultComObjects...
        //        } else {
        //            return null;
        //        }
        //
        //        Node newRoot = AstUtilities.getRoot(info);
        //
        //        // Find newNode
        //        Node newNode = find(oldRoot, oldNode, newRoot);
        //
        //        if (newNode != null) {
        //            Element co = AstElement.create(newNode);
        //
        //            return (T)co;
        //        }
        //
        //        return null;
    }

     private static class RubyElementHandle implements ElementHandle {
        private final Node root;
        private final Element object;
        private final FileObject fileObject;

        private RubyElementHandle(Node root, Element object, FileObject fileObject) {
            this.root = root;
            this.object = object;
            this.fileObject = fileObject;
        }

        public boolean signatureEquals(ElementHandle handle) {
            // XXX TODO
            return false;
        }

        public FileObject getFileObject() {
//            if (object instanceof IndexedElement) {
//                return ((IndexedElement)object).getFileObject();
//            }

            return fileObject;
        }
        
        public String getMimeType() {
            return Erlang.MIME_TYPE;
        }

        public String getName() {
            return object.getName();
        }

        public String getIn() {
            return object.getIn();
        }

        public ElementKind getKind() {
            return object.getKind();
        }

        public Set<Modifier> getModifiers() {
            return object.getModifiers();
        }
    }
}
