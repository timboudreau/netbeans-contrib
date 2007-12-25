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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandCollection;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.NamedAttributableWithArguments;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TextNode;
import org.netbeans.modules.latex.model.command.impl.ArgumentContainingNodeImpl;
import org.netbeans.modules.latex.model.command.impl.ArgumentNodeImpl;
import org.netbeans.modules.latex.model.command.impl.BlockNodeImpl;
import org.netbeans.modules.latex.model.command.impl.CommandNodeImpl;
import org.netbeans.modules.latex.model.command.impl.GroupNodeImpl;
import org.netbeans.modules.latex.model.command.impl.InputNodeImpl;
import org.netbeans.modules.latex.model.command.impl.MathNodeImpl;
import org.netbeans.modules.latex.model.command.impl.NBDocumentNodeImpl;
import org.netbeans.modules.latex.model.command.impl.NodeImpl;
import org.netbeans.modules.latex.model.command.impl.ParagraphNodeImpl;
import org.netbeans.modules.latex.model.command.impl.TextNodeImpl;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.Exceptions;

/** LaTeX command parser. Generates tree of commands. The model is in
 *  {@link org.netbeans.modules.latex.model.command}.
 *
 *  MISSING: error recovery (error annotations in the editor).:partially done.
 *
 * @author Jan Lahoda
 */
public final class CommandParser {
    
    private static final boolean includeDebug = Boolean.getBoolean("netbeans.latex.parser.include.debug");
    private static final boolean debug        = Boolean.getBoolean("netbeans.latex.parser.debug");
    
    /** Creates a new instance of LaTeXParser */
    public CommandParser() {
    }
    
    private Collection<ParseError>  errors;
    private Collection/*<String>*/ labels;
    
    private Map<Environment, FileObject> env2BeginText = new HashMap<Environment, FileObject>();
    private Map<Environment, FileObject> env2EndText   = new HashMap<Environment, FileObject>();
    
//    public synchronized DocumentNode parseAndLeaveLocked(LaTeXSourceImpl source, Collection coll, Collection/*<ParseError>*/ errors) throws IOException {
//        this.errors    = errors;
//        this.usedFiles = new ArrayList();
//        this.documents = coll;
//        this.mainFile  = (FileObject) source.getMainFile();
//        this.labels    = new HashSet/*<String>*/();
//        this.currentCommandDefiningNode = null;
//        
//        ParserInput input = openFile(mainFile);
//        
//        NBDocumentNodeImpl result = parseDocument(input, source);
//        
//        result.setFiles(usedFiles.toArray());
//        
//        closeFile(input);
//        
//        this.errors = null;
//        this.usedFiles = null;
//        this.documents = null;
//        this.mainFile = null;
//        this.labels   = null;
//        this.currentCommandDefiningNode = null;
//        
//        return result;
//    }

    /*public*/private synchronized DocumentNode parseImpl(FileObject mainFile, Collection documents, Collection<ParseError> errors) throws IOException {
        this.errors    = errors;
        this.documents = documents;
//        this.usedFiles = new ArrayList();
        this.mainFile  = mainFile;
        this.labels    = new HashSet/*<String>*/();
        this.currentCommandDefiningNode = null;
        
        ParserInput input = openFile(mainFile);
        
        NBDocumentNodeImpl result = parseDocument(input);
        
        result.addUsedFile(mainFile);
        
        closeFile(input);
        
        this.errors = null;
//        this.usedFiles = null;
        this.mainFile = null;
        this.documents = null;
        this.labels   = null;
        this.currentCommandDefiningNode = null;
        
        return result;
    }

    /*public*/private DocumentNode parse(FileObject mainFile) throws IOException {
        return parse(mainFile, new ArrayList());
    }
    
    /*public*/private DocumentNode parse(FileObject mainFile, Collection<ParseError> errors) throws IOException {
        return parse(mainFile, new HashSet(), errors);
    }
    
    public synchronized DocumentNode parse(FileObject mainFile, Collection documents, Collection<ParseError> errors) throws IOException {
//        Collection coll = new HashSet();
//        
//        try {
            DocumentNode result = parseImpl(mainFile, documents, errors);
            
            return result;
//        } finally {
//            Iterator it = coll.iterator();
//            
//            while (it.hasNext()) {
//                try {
//                    ((AbstractDocument )it.next()).readUnlock();
//                } catch (Throwable t) {
//                    ErrorManager.getDefault().notify(t);
//                }
//            }
//        }
    }
    
//    private Collection usedFiles;
    private Collection documents;
    private FileObject mainFile;
    private NodeImpl   currentCommandDefiningNode;
    
    private FileObject findFile(String file, String extensionsString) throws IOException {
        FileObject toOpen = (FileObject) Utilities.getDefault().getRelativeFileName(mainFile, file);
        
        if (toOpen != null) {
            return toOpen;
        }
        
        String[] extensions = extensionsString.split(":");
        
        for (int cntr = 0; cntr < extensions.length; cntr++) {
            toOpen = (FileObject) Utilities.getDefault().getRelativeFileName(mainFile, file + "." + extensions[cntr]);
            if (toOpen != null) {
                return toOpen;
            }
        }
        
        if (includeDebug) {
            System.err.println("file=" + file);
            System.err.println("extensionsString" + extensionsString);
            System.err.println("mainFile = " + mainFile );
            System.err.println("mainFile.getParent() = " + mainFile.getParent() );
            System.err.println("not found");
        }
        
        return null;
    }
    
    private ParserInput openFile(String file) throws IOException {
        FileObject toOpen = findFile(file, "tex");
        
        if (toOpen == null)
            return null;
        else
            return openFile(toOpen);
    }
    
    private ParserInput openFile(FileObject file) throws IOException {
//        usedFiles.add(file);
        return new ParserInput(file, documents);
    }
    
    private void closeFile(ParserInput input) throws IOException {
        //nothing...
    }
    
    private boolean isParNode(Node node) {
        if (node instanceof CommandNode) {
            return ((CommandNode) node).getCommand().hasAttribute("par");
        }
        
        if (node instanceof BlockNode) {
            Environment env = ((BlockNode) node).getEnvironment();
            
            return env.hasAttribute("par");
        }
        
        return false;
    }
    
    private boolean isFreeTextEndNode(Node node) {
        return node.hasAttribute("free-text-end");
    }
    
    private TextNodeImpl parseTextNode(ParserInput input, TextNodeImpl node) throws IOException {
//        return parseGroup(input, node, false, false, false, true, true);
//        NodeImpl lastCommandDefiningNode = currentCommandDefiningNode;

        boolean useParagraphs = true;
        
        node.setStartingPosition(input.getPosition());
        
        TextNodeImpl lastParagraph = useParagraphs ? new ParagraphNodeImpl(node, currentCommandDefiningNode) : node;
        MathNodeImpl mathNode = null;
        
        if (useParagraphs) {
            lastParagraph.setStartingPosition(input.getPosition());
            node.addChild(lastParagraph);
        }
        
        while (input.hasNext()) {
            Token read = input.getToken();
            
            if (read.id() == TexTokenId.COMMAND) {
                SourcePosition previous = input.getPosition();
                NodeImpl cnode = parseCommand(mathNode != null ? mathNode : lastParagraph, input);
                
                if (isParNode(cnode) && useParagraphs) {
                    if (mathNode != null) {
                        mathNode.setEndingPosition(previous);
                        mathNode = null;
                    }
                    lastParagraph.setEndingPosition(previous);
                    lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                    lastParagraph.setStartingPosition(previous);
                    node.addChild(lastParagraph);
                }
                
                cnode.setParent(mathNode != null ? mathNode : lastParagraph);
                (mathNode != null ? mathNode : lastParagraph).addChild(cnode);
            } else {
                if (read.id() == TexTokenId.COMP_BRACKET_LEFT) {
                    GroupNodeImpl n = new GroupNodeImpl(mathNode != null ? mathNode : lastParagraph, currentCommandDefiningNode);
                    
                    (mathNode != null ? mathNode : lastParagraph).addChild(parseGroup(input, n, false, false, false, true, false));
                } else {
                    if (read.id() == TexTokenId.MATH) {
                        if (mathNode == null) {
                            mathNode = new MathNodeImpl(lastParagraph, currentCommandDefiningNode);
                            lastParagraph.addChild(mathNode);
                            mathNode.setStartingPosition(input.getPosition());
                            input.next();
                        } else {
                            input.next();
                            mathNode.setEndingPosition(input.getPosition());
                            mathNode = null;
                        }
                    } else {
                        if (read.id() == TexTokenId.PARAGRAPH_END && useParagraphs) {
                            if (mathNode != null) {
                                mathNode.setEndingPosition(input.getPosition());
                                mathNode = null;
                            }
                            lastParagraph.setEndingPosition(input.getPosition());
                            lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                            lastParagraph.setStartingPosition(input.getPosition());
                            node.addChild(lastParagraph);
                        }
                        
                        input.next();
                    }
                }
            }
            
        }
        
        node.setEndingPosition(input.getPosition());
        
        if (useParagraphs)
            lastParagraph.setEndingPosition(input.getPosition());
        
        if (mathNode != null) {
            mathNode.setEndingPosition(input.getPosition());
            mathNode = null;
        }
        
//        currentCommandDefiningNode = lastCommandDefiningNode;
        
        return node;
    }
    
    private NBDocumentNodeImpl parseDocument(ParserInput input) throws IOException {
        NBDocumentNodeImpl node = new NBDocumentNodeImpl();
        
        currentCommandDefiningNode = node;
        
        parseTextNode(input, node);
        
        return node;
    }
    
    private Token getTokenWithoutWhiteSpace(ParserInput input) throws IOException {
        while (input.hasNext() && ParserUtilities.isWhitespace(input.getToken()))
            input.next();
        
        return input.getToken();
    }

    private Command findCommand(Node parent, ParserInput input, CharSequence name) {
        if (currentCommandDefiningNode != null) {
            return currentCommandDefiningNode.getCommand(name.toString(), true);
        }
        
        return null;
    }

    private Environment findEnvironment(CharSequence name) {
        if (currentCommandDefiningNode != null) {
            return currentCommandDefiningNode.getEnvironment(name.toString(), true);
        }
        
        return null;
    }
    
    private void handleAddArgument(ArgumentContainingNodeImpl cni, int argIndex, ArgumentNodeImpl ani) throws IOException {
        cni.putArgument(argIndex, ani);
        
        Command.Param param = ani.getArgument();
        
        if (param.hasAttribute("use-file-argument")) {
            String   extensionsString = param.getAttribute("use-file-argument-extensions");
            String   separator        = param.getAttribute("files-separator");
            String   fileNames        = ani.getText().toString();
            
            String[] fileNamesArray = null;
            
            if (separator != null) {
                fileNamesArray = fileNames.split(separator);
            } else {
                fileNamesArray = new String[] {fileNames};
            }
                
            for (int cntr = 0; cntr < fileNamesArray.length; cntr++) {
                FileObject file = findFile(fileNamesArray[cntr], extensionsString);
                
                if (file != null) {
                    ((NBDocumentNodeImpl) ani.getDocumentNode()).addUsedFile(file);
                }
            }
        }
    }
    
    private ParseError createError(final String message, final SourcePosition pos) {
        return ParseError.create(ParseError.Severity.ERROR, "error.unknown", message, pos);
    }
    
    private void handleArguments(ArgumentContainingNodeImpl cni, ParserInput input) throws IOException {
        NamedAttributableWithArguments actual = cni.getArgumentsSpecification();
        int argumentCount = actual.getArgumentCount();
        int currentArgument = 0;
        SourcePosition endingPosition = input.getPosition();
        
        while (currentArgument < argumentCount && input.hasNext()) {
            if (actual.getArgument(currentArgument).getType() == Command.Param.SPECIAL) {
                Command.Param arg = actual.getArgument(currentArgument);
                ArgumentNodeImpl ani = new ArgumentNodeImpl(cni, true, currentCommandDefiningNode);
                
                ani.setArgument(arg);
                
                if ("verb".equals(arg.getAttribute("type"))) {
                    handleAddArgument(cni, currentArgument, parseVerbArgument(input,  ani));
                } else {
                    if ("verbatim-env".equals(arg.getAttribute("type"))) {
                        handleAddArgument(cni, currentArgument, parseVerbatimEnvArgument(input,  ani));
                    } else {
                        errors.add(createError("Unknown special argument (internal error).", input.getPosition()));
                    }
                }
                
                currentArgument++;
                continue;
            }
            
            SourcePosition absoluteStartPosition = input.getPosition(); //For free text arguments only.
            
            Token read = getTokenWithoutWhiteSpace(input);
            
            if (read == null) {
                errors.add(createError("Unexpected end of file.", input.getPosition()));
                break;
            }
            
            if (read.id() != TexTokenId.RECT_BRACKET_LEFT) {
                //TODO: this is not finished (it won't find the problem of mandatory agrument starting by '[').
                while (currentArgument < argumentCount && actual.getArgument(currentArgument).getType() == Command.Param.NONMANDATORY) {
                    ArgumentNodeImpl an = new ArgumentNodeImpl(cni, false, currentCommandDefiningNode);
                    
                    an.setArgument(actual.getArgument(currentArgument));
                    an.setStartingPosition(absoluteStartPosition);
                    an.setEndingPosition(absoluteStartPosition);
                    
                    handleAddArgument(cni, currentArgument, an);
                    
                    currentArgument++;
                }
                
                if (currentArgument >= argumentCount) {
                    break;
                }
            }
            
            boolean freeText = actual.getArgument(currentArgument).getType() == Command.Param.FREE;
            ArgumentNodeImpl ani = new ArgumentNodeImpl(cni, true, currentCommandDefiningNode);
            Command.Param param = actual.getArgument(currentArgument);
            
            ani.setArgument(param);
            
            
            if (!ParserUtilities.isOpeningBracket(read) && (actual.getArgument(currentArgument).getType() != Command.Param.FREE)) {
                if (read.id() == TexTokenId.COMMAND) {
                    ani.setStartingPosition(input.getPosition());
                    input.next();
                    ani.setEndingPosition(input.getPosition());

                    handleAddArgument(cni, currentArgument, ani);

                } else {
                    errors.add(createError("A non-free argument looking like a free argument", input.getPosition()));
                    break;
                }
            } else {
                if (param.hasAttribute(Command.Param.ATTR_NO_PARSE)) {
                    handleAddArgument(cni, currentArgument, (ArgumentNodeImpl) parseBalancedText(input, ani, true /*!!!!!!*/));
                } else {
                    handleAddArgument(cni, currentArgument, (ArgumentNodeImpl) parseGroup(input, ani, true, freeText, true /*!!!!!!*/, param.hasAttribute("contains-paragraph"), false));
                }
            }
            
            currentArgument++;
            
            endingPosition = input.getPosition();
        }
        
        cni.setEndingPosition(endingPosition);
    }
    
    private NodeImpl parseCommand(Node parent, ParserInput input) throws IOException {
        Token command = input.getToken();
        
        assert command.id() == TexTokenId.COMMAND;
        
        Command actual = findCommand(parent, input, command.text());
        
        if (actual == null) {
            SourcePosition end = new SourcePosition(input.getPosition().getDocument(), input.getPosition().getOffsetValue() + command.length());
            ParseError error = ParseError.create(ParseError.Severity.WARNING, "unknown.command", "Unknown command: " + command.text().toString(), input.getPosition(), end, command.text().toString());
            
            errors.add(error);
            actual = CommandNodeImpl.NULL_COMMAND;
        }
        
        CommandNodeImpl cni;
        
        if (actual.isInputLike()) {
            cni = new InputNodeImpl(parent, actual, currentCommandDefiningNode);
        } else {
            cni = new CommandNodeImpl(parent, actual, currentCommandDefiningNode);
        }
        
        cni.setStartingPosition(input.getPosition());
        
        input.next();
        
        handleArguments(cni, input);

        if ("\\documentclass".equals(cni.getCommand().getCommand())) {//TODO: this is quite obsolette way :-)
            CommandCollection coll = new CommandCollection();
            String documentClass = cni.getArgumentCount() == cni.getCommand().getArgumentCount() ? cni.getArgument(1).getText().toString() : "";
            
            ((NBDocumentNodeImpl) parent.getDocumentNode()).setDocumentClass(documentClass);
            
            coll.addDocumentClassContent(documentClass);
            cni.setCommandCollection(coll);
            currentCommandDefiningNode = cni;
        }

        if ("\\usepackage".equals(cni.getCommand().getCommand())) {//TODO: this is quite obsolette way :-)
            if (cni.getArgumentCount() == cni.getCommand().getArgumentCount()) {
                String collectionsSpecification = cni.getArgument(1).getText().toString();
                String[] collections = collectionsSpecification.split(",");
                CommandCollection coll = new CommandCollection();
                
                for (int cntr = 0; cntr < collections.length; cntr++) {
                    coll.addPackageContent(collections[cntr]);
                }
                
                cni.setCommandCollection(coll);
                currentCommandDefiningNode = cni;                
            }
        }
        
        if ("\\newcommand".equals(cni.getCommand().getCommand()) || "\\renewcommand".equals(cni.getCommand().getCommand())) {//TODO: this is quite obsolette way :-)
            if (cni.getArgumentCount() == 4) {//TODO: this is quite obsolette way :-)
                String name = ParserUtilities.getArgumentValue(cni.getArgument(0)).toString(); //!!!Check that it has exactly one argument of type command!
                Iterator argTokens = cni.getArgument(1).getDeepNodeTokens().iterator(); if (argTokens.hasNext()) argTokens.next();
                String argCountString = argTokens.hasNext() ? ((Token) argTokens.next()).text().toString() : ""; //!!!Check that it has exact one argument of type number!
//                System.err.println(cni.getCommand().getCommand() + "{" + name + "}[" + argCountString + "]{" + cni.getArgument(2).getText() + "}");
                boolean isFirstNonMandatory = cni.getArgument(2).isPresent();
                int argCount = 0;
                
                try {
                    argCount = "".equals(argCountString) ? 0 : Integer.parseInt(argCountString);
                
                    Command newCommand = new Command(name, argCount, isFirstNonMandatory);
                    
                    CommandCollection coll = new CommandCollection();
                    
                    coll.addCommand(newCommand);
                    cni.setCommandCollection(coll);
                    currentCommandDefiningNode = cni;
//                    System.err.println("coll=" + coll);
//                    System.err.println("cni.getCommandCollection()=" + cni.getCommandCollection());
                } catch (NumberFormatException e) {
                    errors.add(createError("The number of arguments should be a positive integer or zero.", cni.getArgument(1).getStartingPosition()));
                }
            } else {
                errors.add(createError("Incorrect number of arguments for \\newcommand. Expected 3, found " + cni.getArgumentCount() + ".", cni.getStartingPosition()));
            }
        }
        
        if (cni.getCommand().hasAttribute("#enviroment-defining-command")) {
            if (cni.getArgumentCount() == cni.getCommand().getArgumentCount()) {
                String envName = null;
                int    argNumber = 0;
                String nonmandatoryArgDefault = "";
                String envBeginText = null;
                String envEndText = null;
                
                for (int cntr = 0; cntr < cni.getArgumentCount(); cntr++) {
                    ArgumentNode an = cni.getArgument(cntr);
                    Command.Param param = an.getArgument();
                    
                    if (param.hasAttribute("#envname")) {
                        if (an.isPresent())
                            envName = ParserUtilities.getArgumentValue(an).toString();
                    }

                    if (param.hasAttribute("#argcountargument")) {
                        String argNumberValue = "0";
                        
                        if (an.isPresent())
                            argNumberValue = ParserUtilities.getArgumentValue(an).toString();
                        
                        try {
                            argNumber = Integer.parseInt(argNumberValue);
                        } catch (NumberFormatException e) {
                            errors.add(createError("The number of arguments is not a valid number:" + argNumberValue + "." , an.getStartingPosition()));
                        }
                    }

                    if (param.hasAttribute("#nonmandargvalueargument")) {
                        if (an.isPresent())
                            nonmandatoryArgDefault = ParserUtilities.getArgumentValue(an).toString();
                    }
                    
                    if (param.hasAttribute("#environment-begin-spec")) {
                        envBeginText = ParserUtilities.getArgumentValue(an).toString();
                    }
                    
                    if (param.hasAttribute("#environment-end-spec")) {
                        envEndText = ParserUtilities.getArgumentValue(an).toString();
                    }
                }
                
                Environment newEnvironment = new Environment(envName, argNumber);
                
                CommandCollection coll = new CommandCollection();
                
                coll.addEnvironment(newEnvironment);
                cni.setCommandCollection(coll);
                currentCommandDefiningNode = cni;
                
                if (envBeginText != null) {
                    FileObject file = createUnique((NBDocumentNodeImpl) cni.getDocumentNode(), "env-begin-" + envName);
                    
                    dumpText(file, envBeginText);
                    env2BeginText.put(newEnvironment, file);
                }
                
                if (envEndText != null) {
                    FileObject file = createUnique((NBDocumentNodeImpl) cni.getDocumentNode(), "env-end-" + envName);
                    
                    dumpText(file, envEndText);
                    env2EndText.put(newEnvironment, file);
                }
            }
        }

        if (cni.getCommand().isBeginLike()) {//TODO: this is quite obsolette way :-)
            return parseBlock(input, cni);
        }
        
        if (cni.getCommand().isLabelLike()) {//TODO: this is quite obsolette way :-)
            if (cni.getArgumentCount() != 1)//TODO: this is quite obsolette way :-)
                errors.add(createError("Incorrect number of arguments for \\label (like). Expected 1, found " + cni.getArgumentCount() + ".", cni.getStartingPosition()));
            else {
                String label = cni.getArgument(0).getText().toString();
                
                if (labels.contains(label)) {
                    errors.add(createError("Multiply defined label: " + label + ".", cni.getEndingPosition()));
                } else {
                    labels.add(label);
                }
            }
        }
        
//        if (cni.getCommand().isEndLike()) {
//            errors.add(createError("Too much \\ends: \\end{" + cni.getArgument(0).getText() + "}", cni.getStartingPosition()));
//        }
        
        if (cni.getCommand().isInputLike()) {//TODO: this is quite obsolette way :-)
            if (cni.getArgumentCount() != 1) {//TODO: this is quite obsolette way :-)
                errors.add(createError("For \\input or \\include is expected one parameter, found " + cni.getArgumentCount() + ".", cni.getStartingPosition()));
            } else {
                String fileName = cni.getArgument(0).getText().toString();
                
                try {
                    ParserInput newInput = openFile(fileName);
                    
                    if (newInput != null) {
                        ((InputNodeImpl) cni).setContent(parseTextNode(newInput, new TextNodeImpl(cni, currentCommandDefiningNode)));
                        
                        closeFile(newInput);
                    } else {
                        errors.add(createError("Included file \"" + fileName + "\" not found.", cni.getStartingPosition()));
                    }
                } catch (IOException e) {
                    errors.add(createError("Cannot include file \"" + fileName + "\" reason:" + e.getMessage() + ".", cni.getStartingPosition()));
                }
            }
        }
        
        for (int cntr = 0; cntr < cni.getArgumentCount(); cntr++) {
            ArgumentNode an = cni.getArgument(cntr);
            
            if (an.hasAttribute("new-counter")) {
                Command newCommand = new Command("\\the" + ParserUtilities.getArgumentValue(an), 0, false);

                CommandCollection coll;
                
                if (currentCommandDefiningNode == cni) {
                    coll = cni.getCommandCollection();
                } else {
                    coll = new CommandCollection();

                    cni.setCommandCollection(coll);
                    currentCommandDefiningNode = cni;
                }

                coll.addCommand(newCommand);
            }
        }
        
        return cni;
    }
    
    private FileObject createUnique(NBDocumentNodeImpl dni, String prefix) throws IOException {
        int count = (int) System.currentTimeMillis();
        
        count = count < 0 ? -count : count;
        
        FileSystem fs = dni.getTemporaryFS();
        FileObject file = fs.getRoot();
        
        while (file.getFileObject(prefix + "-" + count + ".tex") != null) {
            count++;
        }
        
        return file.createData(prefix + "-" + count, "tex");
    }
    
    private void dumpText(FileObject to, String text) throws IOException {
        FileLock lock = null;
        OutputStream out = null;
        
        try {
            lock = to.lock();
            out  = to.getOutputStream(lock);
            
            out.write(text.getBytes());
        } finally {
            out.close();
            lock.releaseLock();
        }
        
    }
    
    private ArgumentNodeImpl parseVerbArgument(ParserInput input, ArgumentNodeImpl anode) throws IOException {
        //TODO: this is incorrect. The \verb is in form: \verbDtestD, where D is a one character
        //delimiter. This code requires D to be one token (of length==1) which is not required by LaTeX.
        Token delimiter = input.getToken();
        String delimiterString = delimiter.text().toString();
        
        anode.setStartingPosition(input.getPosition());
        input.next();
        
        while (!delimiterString.equals(input.getToken().text().toString()) && input.hasNext()) {
            input.next();
        }
        
        anode.setEndingPosition(input.getPosition());
        
        if (input.hasNext())
            input.next();
        
        return anode;
    }
    
    private ArgumentNodeImpl parseVerbatimEnvArgument(ParserInput input, ArgumentNodeImpl anode) throws IOException {
        String delimiterString = anode.getAttribute("end-tag"); // NOI18N
        StringBuffer currentContent = new StringBuffer();
        
        anode.setStartingPosition(input.getPosition());
        
        while (input.hasNext()) {
            currentContent.append(input.next().text());
            if (currentContent.indexOf(delimiterString) != (-1))
                break;
        }
        
        input.goBack(delimiterString.length());
        
        anode.setEndingPosition(input.getPosition());
        
        return anode;
    }
    
    private boolean isOpeningBracket(Token read, boolean argument) {
        return (argument && ParserUtilities.isOpeningBracket(read)) || (!argument && read.id() == TexTokenId.COMP_BRACKET_LEFT);
    }
    
    private boolean isClosingBracket(Token read, boolean argument) {
        return (argument && ParserUtilities.isClosingBracket(read)) || (!argument && read.id() == TexTokenId.COMP_BRACKET_RIGHT);
    }
    
    private boolean isPAREnd(Token read, ParserInput input, Node parent) {
        if (read.id() == TexTokenId.COMMAND) {
            Command cmd = findCommand(parent, input, read.text());
            return cmd != null ? cmd.isPARLike() : false;
        } else
            return false;//read.id() == TexTokenId.PARAGRAPH_END;
    }
    
    private boolean isFreeTextEnd(Token read, ParserInput input, Node parent) {
        return    read.id() == TexTokenId.COMP_BRACKET_RIGHT
               /*|| read.id() == TexTokenId.COMP_BRACKET_LEFT*/
               /*|| read.id() == TexTokenId.PARAGRAPH_END*/
               || isPAREnd(read, input, parent);
    }
    
    private boolean isErrorRecoveryEnd(Token read, ParserInput input, Node parent) {
        return isPAREnd(read, input, parent);
    }

    private TextNode parseBalancedText(ParserInput input, TextNodeImpl node, boolean parErrorRecovery) throws IOException {
        Token read = input.getToken();
        NodeImpl lastCommandDefiningNode = currentCommandDefiningNode;
        
        assert isOpeningBracket(read, false);
        
        StringBuffer bracketStack = new StringBuffer();
        
        ParserUtilities.updateBracketStack(bracketStack, read);
        
        node.setStartingPosition(input.getPosition());
        
        while (bracketStack.length() != 0) {
            if (!input.hasNext()) {
                errors.add(createError("Unexpected end of the file.", input.getPosition()));
                
                break;
            }
            
            read = input.next();
            
            if (isOpeningBracket(read, false) || isClosingBracket(read, false))
                ParserUtilities.updateBracketStack(bracketStack, read);
        }
        
        if (input.hasNext())
            input.next();
        
        node.setEndingPosition(input.getPosition());
        
        currentCommandDefiningNode = lastCommandDefiningNode;
        
        return node;
    }

    private /*GroupNode*/TextNodeImpl parseGroup(ParserInput input, /*GroupNodeImpl*/TextNodeImpl node, boolean argument, boolean freeText, boolean parErrorRecovery, boolean useParagraphs, boolean forEver) throws IOException {
        if (debug) {
            //TODO: put missing arguments into the log message:
	    System.err.println("parseGroup: start(" + input + ", " + node + ", " + argument + ", " + freeText + ", " + parErrorRecovery);
	}
	
        boolean shouldReadAtEnd = true;
        Token read = input.getToken();
        Token left = read;
        boolean atStart = true;
        boolean errorRecovery = false;
        boolean afterCommand = false;
        
        NodeImpl lastCommandDefiningNode = currentCommandDefiningNode;
        
        TextNodeImpl lastParagraph = useParagraphs ? new ParagraphNodeImpl(node, currentCommandDefiningNode) : node;
        MathNodeImpl mathNode = null;
        
        if (useParagraphs) {
            lastParagraph.setStartingPosition(input.getPosition());
            node.addChild(lastParagraph);
        }
        
        assert freeText || isOpeningBracket(read, argument) || forEver;
        
        node.setStartingPosition(input.getPosition());
        
        do {
	    if (debug) {
	        System.err.println("parseGroup: read=" + read);
	    }
	    
            if (!input.hasNext()) {
                
                errors.add(createError("Unexpected end of the file.", input.getPosition()));
                
                break;
            }
            
            if (!(atStart && freeText) && !afterCommand)
                read = input.next();
            else
                read = input.getToken();
            
            afterCommand = false;
            
            atStart = false;

            if (   (freeText && isFreeTextEnd(read, input, node))
                || (   errorRecovery =    parErrorRecovery
                                       && isErrorRecoveryEnd(input.getToken(), input, node))) {
                shouldReadAtEnd = false;
                
                break;
            }
            
            if (read.id() == TexTokenId.COMMAND) {
                SourcePosition previous = input.getPosition();
                NodeImpl cnode = parseCommand(mathNode == null ? lastParagraph : mathNode, input);
                
                if (isFreeTextEndNode(cnode) && freeText) {
                    break;
                }
                
                if (isParNode(cnode) && useParagraphs) {
                    if (mathNode != null) {
                        mathNode.setEndingPosition(previous);
                        mathNode = null;
                    }
                    lastParagraph.setEndingPosition(previous); //!!!!!
                    lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                    lastParagraph.setStartingPosition(previous);
                    node.addChild(lastParagraph);
                }
                
                cnode.setParent(mathNode == null ? lastParagraph : mathNode);
                (mathNode == null ? lastParagraph : mathNode).addChild(cnode);
                
                afterCommand = true;
            }
            
            if (read.id() == TexTokenId.COMP_BRACKET_LEFT) {
                GroupNodeImpl n = new GroupNodeImpl(mathNode == null ? lastParagraph : mathNode, currentCommandDefiningNode);

                (mathNode == null ? lastParagraph : mathNode).addChild(parseGroup(input, n, false, false, parErrorRecovery, /*?????*/useParagraphs, false));
                
                afterCommand = true;
            }
            
            if (read.id() == TexTokenId.PARAGRAPH_END && useParagraphs) {
                if (mathNode != null) {
                    mathNode.setEndingPosition(input.getPosition());
                    mathNode = null;
                }
                lastParagraph.setEndingPosition(input.getPosition()); //!!!!!
                lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                lastParagraph.setStartingPosition(input.getPosition());
                node.addChild(lastParagraph);
            }
            
            if (read.id() == TexTokenId.MATH) {
                if (mathNode == null) {
                    mathNode = new MathNodeImpl(lastParagraph, currentCommandDefiningNode);
                    lastParagraph.addChild(mathNode);
                    mathNode.setStartingPosition(input.getPosition());
                } else {
                    input.next();
                    afterCommand = true;
                    mathNode.setEndingPosition(input.getPosition());
                    mathNode = null;
                }
            }
        } while (!(   (freeText && isFreeTextEnd(input.getToken(), input, node.getParent())) 
                   || (!freeText && ParserUtilities.matches(left, input.getToken()))
                   || (errorRecovery = parErrorRecovery && isErrorRecoveryEnd(input.getToken(), input, node.getParent()))
                   || (forEver && !input.hasNext())
                   ));
        
	if (debug) {
	    System.err.println("parseGroup: after main loop: input.getToken()=" + input.getToken() + ";" + input.getPosition());
	}
	
        shouldReadAtEnd = shouldReadAtEnd && !errorRecovery && !(freeText && isPAREnd(input.getToken(), input, node.getParent())) && !(freeText && input.getToken().id() == TexTokenId.COMP_BRACKET_RIGHT);
        
        if (useParagraphs) {
            lastParagraph.setEndingPosition(input.getPosition());
        }
        
        if (mathNode != null) {
            mathNode.setEndingPosition(input.getPosition());
            mathNode = null;
        }
        
        //TODO:moved down, so the argument node contains also the last {. Is it correct also for other cases?
//      node.setEndingPosition(input.getPosition());
        
        if (shouldReadAtEnd && input.hasNext())
            input.next();
        
        //TODO: see above.
        node.setEndingPosition(input.getPosition());
        
        if (errorRecovery) {
            ParseError err = createError("Missing closing bracket added.", node.getEndingPosition());
            
            errors.add(err);
            
            if (debug) {
                System.err.println("parseGroup: error=" + err);
            }
        }
        
        if (!forEver)
            currentCommandDefiningNode = lastCommandDefiningNode;
        
	if (debug) {
	    System.err.println("parseGroup: end, input.getToken()=" + input.getToken());
	}
	
        return node;
    }
    
    private void doIncludeForBlock(Map<Environment, FileObject> env2File, Environment env, TextNodeImpl node, SourcePosition pos) {
        if (env2File.get(env) != null) {
            try {
                ParserInput newInput = openFile(env2File.get(env));
                
                if (newInput != null) {
                    node.addChild(parseTextNode(newInput, new TextNodeImpl(node, currentCommandDefiningNode)));
                    
                    closeFile(newInput);
                } else {
                    errors.add(createError("Cannot correctly start environment \"" + env.getName() + "\" (internal error).", pos));
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                errors.add(createError("Cannot correctly start environment \"" + env.getName() + "\" (internal error).", pos));
            }
        }
    }
    
    private BlockNodeImpl parseBlock(ParserInput input, CommandNodeImpl begin) throws IOException {
        NodeImpl lastCommandDefiningNode = currentCommandDefiningNode;
        
	ArgumentNode  anode = Utilities.getDefault().getArgumentWithAttribute(begin, "#environmentname");
        String        beginText = anode != null ? anode.getText().toString() : "";
        Environment   env = findEnvironment(beginText);
        
        if (env == null) {
            ParseError error = ParseError.create(ParseError.Severity.WARNING, "unknown.environment", "Unknown environment: " + beginText, anode.getStartingPosition(), anode.getEndingPosition(), beginText);
            errors.add(error);
            env = BlockNodeImpl.NULL_ENVIRONMENT;
        }
        
        BlockNodeImpl bni = new BlockNodeImpl(begin.getParent(), currentCommandDefiningNode, env);
        
        {
            CommandCollection coll = new CommandCollection();
            coll.addEnvironmentContent(env);
            bni.setCommandCollection(coll);
            currentCommandDefiningNode = bni;
        }
        
        bni.setBeginCommand(begin);
        
        handleArguments(bni, input);
        
        TextNodeImpl node = new TextNodeImpl(bni, currentCommandDefiningNode);
        
        bni.setContent(node);
        node.setStartingPosition(input.getPosition());

        doIncludeForBlock(env2BeginText, env, node, bni.getStartingPosition());
        
        boolean useParagraphs = true;
        
        TextNodeImpl lastParagraph = useParagraphs ? new ParagraphNodeImpl(node, currentCommandDefiningNode) : node;
        MathNodeImpl mathNode = null;

        if (useParagraphs) {
            lastParagraph.setStartingPosition(input.getPosition());
            node.addChild(lastParagraph);
        }
        
        SourcePosition endPosition = null;
        
        if (debug) {
            System.err.println("parseBlock: start environment name=" + beginText + ", input.getToken()=" + input.getToken());
        }
        
        while (input.hasNext()) {
            Token read = input.getToken();
            
            if (read.id() == TexTokenId.COMMAND) {
                endPosition = input.getPosition();
                
                NodeImpl bcnode = parseCommand(mathNode != null ? mathNode : lastParagraph, input);
                
                if (isParNode(bcnode) && useParagraphs) {
                    if (mathNode != null) {
                        mathNode.setEndingPosition(endPosition);
                        mathNode = null;
                    }
                    lastParagraph.setEndingPosition(endPosition);
                    lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                    lastParagraph.setStartingPosition(endPosition);
                    node.addChild(lastParagraph);
                }
                
                if (bcnode instanceof CommandNodeImpl) {
                     CommandNodeImpl cnode = (CommandNodeImpl) bcnode;
                     
                     if (cnode.getCommand().isEndLike()) {
                         String endText   = cnode.getArgumentCount() > 0 ? cnode.getArgument(0).getText().toString() : "";
                         
                         cnode.setParent(bni);
                         bni.setEndCommand(cnode); //!!!Test missing
                         
                         if (!beginText.equals(endText)) {
                             System.err.println("beginText= " + beginText);
                             System.err.println("endText = " + endText);
                             errors.add(createError("\\begin-\\end not matching. \\begin command: " + beginText + ", \\end command: " + endText + ".", cnode.getStartingPosition()));
                             //????
                             break;
                         }
                         
                         break;
                     }
                }
                
                endPosition = null;
                
                bcnode.setParent(mathNode != null ? mathNode : lastParagraph);
                (mathNode != null ? mathNode : lastParagraph).addChild(bcnode);
            } else {
                if (read.id() == TexTokenId.COMP_BRACKET_LEFT) {
                    GroupNodeImpl n = new GroupNodeImpl(mathNode != null ? mathNode : lastParagraph, currentCommandDefiningNode);
                    
                    (mathNode != null ? mathNode : lastParagraph).addChild(parseGroup(input, n, false, false, false, true, false));
                } else {
                    if (read.id() == TexTokenId.COMP_BRACKET_RIGHT) {
                        SourcePosition pos = input.getPosition();
                        
                        errors.add(createError("Missing \\end{" + beginText + "} command added.", pos));
                        break;
                    } else {
                        if (read.id() == TexTokenId.MATH) {
                            if (mathNode == null) {
                                mathNode = new MathNodeImpl(lastParagraph, currentCommandDefiningNode);
                                lastParagraph.addChild(mathNode);
                                mathNode.setStartingPosition(input.getPosition());
                                input.next();
                            } else {
                                input.next();
                                mathNode.setEndingPosition(input.getPosition());
                                mathNode = null;
                            }
                        } else {
                            if (read.id() == TexTokenId.PARAGRAPH_END && useParagraphs) {
                                if (mathNode != null) {
                                    mathNode.setEndingPosition(input.getPosition());
                                    mathNode = null;
                                }
                                lastParagraph.setEndingPosition(input.getPosition());
                                lastParagraph = new ParagraphNodeImpl(node, currentCommandDefiningNode);
                                lastParagraph.setStartingPosition(input.getPosition());
                                node.addChild(lastParagraph);
                            }
                            input.next();
                        }
                    }
                }
            }
            
        }
        
        if (endPosition == null) {
            endPosition = input.getPosition();
            bni.setEndingPosition(input.getPosition());
        }
        
        node.setEndingPosition(endPosition);
        
        if (useParagraphs) {
            lastParagraph.setEndingPosition(endPosition);
        }
        
        if (mathNode != null) {
            mathNode.setEndingPosition(endPosition);
            mathNode = null;
        }
        
//        //Safety workaround:
//        if (bni.getEndingPosition() == null) {
//            bni.setEndingPosition(input.getPosition());
//        }
        
        currentCommandDefiningNode = lastCommandDefiningNode;
        
        if (debug) {
            System.err.println("parseBlock: end environment name=" + beginText + ", input.getToken()=" + input.getToken());
        }
        
        return bni;
    }
   
}
