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
import java.util.List;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.Element;
import org.netbeans.api.gsf.ElementHandle;
import org.netbeans.api.gsf.Error;
import org.netbeans.api.gsf.ParseEvent;
import org.netbeans.api.gsf.ParseListener;
import org.netbeans.api.gsf.Parser;
import org.netbeans.api.gsf.ParserFile;
import org.netbeans.api.gsf.ParserResult;
import org.netbeans.api.gsf.PositionManager;
import org.netbeans.api.gsf.SemanticAnalyzer;
import org.netbeans.api.gsf.Severity;
import org.netbeans.api.gsf.SourceFileReader;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.ParseException;
import org.netbeans.modules.erlang.editing.semantic.ErlContext;
import org.netbeans.modules.erlang.editing.semantic.ErlangSemanticAnalyser;
import org.netbeans.spi.gsf.DefaultError;
import org.netbeans.spi.gsf.DefaultPosition;
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
    public void parseFiles(List<ParserFile> files, ParseListener listener, SourceFileReader reader) {
        for (ParserFile file : files) {
            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
            listener.started(beginEvent);

            if (language == null) {
                try {
                    language = LanguagesManager.get().getLanguage("text/x-erlang");
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
                    rootCtx = semanticAnalyser.getRootContext();
                    if (file.isPlatform()) io.getOut().println((System.currentTimeMillis() - start) + "ms");
                }

                /** We may in batching parsing, such as under indexing, so release it manually here*/
                semanticAnalyser = null;

                AstRootElement rootElement = new AstRootElement(fo, astRoot);
                result = new ErlangLanguageParserResult(file, rootElement, astRoot, rootCtx);
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
        Error error = new DefaultError(key, description, details, file.getFileObject(), new DefaultPosition(offset), new DefaultPosition(offset), severity);
        listener.error(error);
    }

    public PositionManager getPositionManager() {
        return positions;
    }

    public SemanticAnalyzer getSemanticAnalysisTask() {
        return null;
    }

    public org.netbeans.api.gsf.OccurrencesFinder getMarkOccurrencesTask(int caretPosition) {
        //        OccurrencesFinder finder = new OccurrencesFinder();
        //        finder.setCaretPosition(caretPosition);
        //        return finder;
        /** @TODO */
        return null;
    }

    @SuppressWarnings(value = "unchecked")
    public <T extends Element> ElementHandle<T> createHandle(CompilationInfo info, final T object) {
        /** @TODO */
        return null;
        //        if (object instanceof KeywordElement) {
        //            // Not tied to an AST - just pass it around
        //            return new RubyElementHandle(null, object);
        //        }
        //
        //        // TODO - check for Ruby
        //        if (object instanceof IndexedElement) {
        //            // Probably a function in a "foreign" file (not parsed from AST),
        //            // such as a signature returned from the index of the Ruby libraries.
        //            return new RubyElementHandle(null, object);
        //        }
        //
        //        if (!(object instanceof AstElement)) {
        //            return null;
        //        }
        //
        //        ParserResult result = info.getParserResult();
        //
        //        if (result == null) {
        //            return null;
        //        }
        //
        //        ParserResult.AstTreeNode ast = result.getAst();
        //
        //        if (ast == null) {
        //            return null;
        //        }
        //
        //        Node root = AstUtilities.getRoot(info);
        //
        //        return new RubyElementHandle(root, object);
    }

    @SuppressWarnings(value = "unchecked")
    public <T extends Element> T resolveHandle(CompilationInfo info, ElementHandle<T> handle) {
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

    private class RubyElementHandle<T extends Element> extends ElementHandle<T> {

        private Node root;
        private T object;

        private RubyElementHandle(Node root, T object) {
            this.root = root;
            this.object = object;
        }

        public boolean signatureEquals(ElementHandle handle) {
            // XXX TODO
            return false;
        }

        public FileObject getFileObject() {
            //            if (object instanceof IndexedElement) {
            //                return ((IndexedElement)object).getFileObject();
            //            }
            return null;
        }
    }
}
