/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.completion.latex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.latex.editor.AnalyseBib;
import org.netbeans.modules.latex.editor.ErrorManager;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.editor.Utilities;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.BiBRecordCompletionItem;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.CommandCompletionItem;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.DocClassCompletionItem;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.EnvironmentCompletionItem;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.LabelCompletionItem;
import org.netbeans.modules.latex.editor.completion.latex.TexCompletionItem.ValueCompletionItem;
import org.netbeans.modules.latex.model.LabelInfo;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.Command;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.CommandPackage;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.loaders.DataObject;


/**
 *
 * @author  Jan Lahoda
 */
public class TexCompletion implements CompletionProvider {
    
    protected static void getCommandsForPrefix(CompletionResultSet resultSet, LaTeXSource source, DataObject od, Position pos, String prefix, int start) throws BadLocationException {
        Object file = od.getPrimaryFile();
        try {
            SourcePosition spos = new SourcePosition(file, org.netbeans.modules.latex.model.Utilities.getDefault().openDocument(file), pos);
            List commandsList = source.getCommands(spos);
            
            if (commandsList != null) {
                Iterator commands = commandsList.iterator();
                
                while (commands.hasNext()) {
                    Command comm = (Command) commands.next();
                    String commandName = comm.getCommand();
                    
                    if (commandName.startsWith(prefix)) {
                        resultSet.addItem(new CommandCompletionItem(start, comm));
                    }
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private static boolean isCommand(TokenId token) {
        return token == TexLanguage.COMMAND;
    }
    
    private static ArgumentNode lookupArgument(LaTeXSource source, Document doc, int offset) {
        try {
            Node node = source.findNode(doc, offset);
            
            if (node instanceof ArgumentNode) {
                return (ArgumentNode) node;
            }
            
            return null;
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return null;
        }
    }
    
    public static String          preprocessList(String prefix) {
        int lastComma = prefix.lastIndexOf(',');
        
        if (lastComma != (-1)) {
            prefix = prefix.substring(lastComma + 1);
        }
        
        return prefix;
    }
    
    private static interface ArgumentCompletionHandler {
        public String[]               getArgumentTags();
        public String                 preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix);
        public void                   getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start);
    }
    
    private static class RefArgumentCompletionHandler implements ArgumentCompletionHandler {
        
        public String[] getArgumentTags() {
            return new String[] {"#ref"};
        }
        
        public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start) {
            Collection/*<LabelInfo>*/ labels = org.netbeans.modules.latex.model.Utilities.getDefault().getLabels(source);
            
            Iterator             labelsIter = labels.iterator();
            
            while (labelsIter.hasNext()) {
                LabelInfo info = (LabelInfo) labelsIter.next();
                
                if (!info.getLabel().startsWith(prefix))
                    continue;
                
                if (info.getCaption() == null || info.getCaption().length() == 0) {
                    set.addItem(new LabelCompletionItem(start, info.getLabel(), ""));
                } else {
                    set.addItem(new LabelCompletionItem(start, info.getLabel(), info.getCaption()));
                }
            }
        }
        
        public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
            return prefix;
        }
        
    }
    
    private static class CiteArgumentCompletionHandler implements ArgumentCompletionHandler {
        
        public String[] getArgumentTags() {
            return new String[] {"#cite"};
        }
        
        public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start) {
            List/*<BibRecord>*/ references = Utilities.getAllBibReferences(source);
            Iterator            referencesIter = references.iterator();
            
            while (referencesIter.hasNext()) {
                AnalyseBib.BibRecord record = (AnalyseBib.BibRecord) referencesIter.next();
                
                if (record.getRef().startsWith(prefix)) {
                    set.addItem(new BiBRecordCompletionItem(start, record));
                }
            }
        }
        
        public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
            return preprocessList(prefix);
        }
        
    }
    
    private static class DocumentClassArgumentCompletionHandler implements ArgumentCompletionHandler {
        
        public String[] getArgumentTags() {
            return new String[] {"#documentclass", "#package"};
        }
        
        public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start) {
            Collection names;
            
            if (node.getArgument().hasAttribute("#documentclass")) {
                names = CommandPackage.getKnownDocumentClasses();
            } else {
                names = CommandPackage.getKnownPackages();
            }
            
            for (Iterator iter = names.iterator(); iter.hasNext(); ) {
                String option = (String) iter.next();
                
                set.addItem(new DocClassCompletionItem(start, option));
            }
        }
        
        public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
            if (node.getArgument().hasAttribute("#package"))
                return preprocessList(prefix);
            else
                return prefix;
        }
        
    }
    
    private static class DocumentClassOptionsArgumentCompletionHandler implements ArgumentCompletionHandler {
        
        public String[] getArgumentTags() {
            return new String[] {"#documentclassoptions", "#packageoptions"};
        }
        
        public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start) {
            CommandNode cnode = node.getCommand();
            String      name  = null;
            
            if (cnode.getArgumentCount() != 2)
                return ;
            
            name = cnode.getArgument(1).getText().toString();
            
            Collection[] options;
            
            options = new Collection[] {
                CommandPackage.getCommandPackageForName(name).getOptions().keySet(),
            };
            
            for (int cntr = 0; cntr < options.length; cntr++) {
                Iterator            referencesIter = options[cntr].iterator();
                
                while (referencesIter.hasNext()) {
                    String option = (String) referencesIter.next();
                    
                    set.addItem(new ValueCompletionItem(start, option));
                }
            }
        }
        
        public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
            return preprocessList(prefix);
        }
        
    }
//
//        private static class InputArgumentCompletionHandler implements ArgumentCompletionHandler {
//
//            public String[] getArgumentTags() {
//                return new String[] {"#include"};
//            }
//
//            public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix) {
//                try {
//                    return org.netbeans.modules.latex.model.Utilities.getDefault().findRelativeFilesBegining(source.getMainFile(), prefix);
//                } catch (IOException e) {
//                    return Collections.EMPTY_LIST;
//                }
//            }
//
//            public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
//                return prefix;
//            }
//
//        }
//
    private static class EnvironmentArgumentCompletionHandler implements ArgumentCompletionHandler {
        
        public String[] getArgumentTags() {
            return new String[] {"#environmentname"};
        }
        
        public void getCompletionResult(CompletionResultSet set, LaTeXSource source, ArgumentNode node, String prefix, int start) {
            try {
                CommandNode cnode = node.getCommand();
                List environments = source.getEnvironments(cnode.getStartingPosition());
                Iterator            environmentsIter = environments.iterator();
                
                while (environmentsIter.hasNext()) {
                    Environment env = (Environment) environmentsIter.next();
                    
                    if (env.getName().startsWith(prefix))
                        set.addItem(new EnvironmentCompletionItem(start, env));
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        public String preprocessPrefix(LaTeXSource source, ArgumentNode node, String prefix) {
            return prefix;
        }
        
    }
    
    private static ArgumentCompletionHandler[] handlers = new ArgumentCompletionHandler[] {
        new RefArgumentCompletionHandler(),
        new CiteArgumentCompletionHandler(),
        new DocumentClassOptionsArgumentCompletionHandler(),
//            new InputArgumentCompletionHandler(),
        new EnvironmentArgumentCompletionHandler(),
        new DocumentClassArgumentCompletionHandler(),
    };
    
    
    
    
    private static void getSpecialCommandArguments(CompletionResultSet set, LaTeXSource source, Document doc, int offset, int start) throws BadLocationException {
        ArgumentNode argument = lookupArgument(source, doc, offset);
        
        if (argument != null) {
            String commandString = argument.getCommand().getCommand().getCommand();
            int    prefixLength = offset - argument.getStartingPosition().getOffsetValue();
            CharSequence argumentContent = argument.getText(); //jl: to be honest, I do not understand the stuff about argumentContent. getText returns only plain text, not commands!
            
            if (prefixLength < 0)
                return ;
            
            String ccPrefix = argumentContent.length() >= (prefixLength - 1) && prefixLength > 0 ? argumentContent.subSequence(0, prefixLength - 1).toString() : "";
            
            for (int cntr = 0; cntr < handlers.length; cntr++) {
                String[] attributes = handlers[cntr].getArgumentTags();
                
                for (int atr_cntr = 0; atr_cntr < attributes.length; atr_cntr++) {
                    if (argument.getArgument().hasAttribute(attributes[atr_cntr])) {
                        ccPrefix = handlers[cntr].preprocessPrefix(source, argument, ccPrefix);
                        
                        handlers[cntr].getCompletionResult(set, source, argument, ccPrefix, start);
                    }
                }
            }
            
            Command.Param param = argument.getArgument();
            
            for (Iterator i = param.getValues().iterator(); i.hasNext(); ) {
                String name = (String) i.next();
                
                if (name.startsWith(ccPrefix)) {
                    set.addItem(new ValueCompletionItem(start, name));
                }
            }
        }
    }
    
    private static boolean isArgument(LaTeXSource source, Document doc, int index) {
        return lookupArgument(source, doc, index) != null;
    }
    
    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == COMPLETION_QUERY_TYPE) {
            return new AsyncCompletionTask(new Query(), component);
        }
        
        return null;
    }
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }
    
    private static class Query extends AsyncCompletionQuery {
        
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            LaTeXSource.Lock lock = null;
            LaTeXSource      source = null;
            try {
                DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty); //TODO: this won't work in SA
                
                source = LaTeXSource.get(od.getPrimaryFile());
                
                lock = source.lock();
                
                int type;
                
                Token token = Utilities.getToken(doc, caretOffset);
                
                String caption      = null;
                int start = Utilities.getStartingOffset(doc, caretOffset);
                
                if (isCommand(token.getId())) {
                    Position pos = doc.createPosition(caretOffset);
                    String prefix = token.getText().subSequence(0, caretOffset - start + 1).toString();
                    
                    getCommandsForPrefix(resultSet, source, od, pos, prefix, start);
                }
                
                if (isArgument(source, doc, caretOffset)) {
                    getSpecialCommandArguments(resultSet, source, doc, caretOffset, start);
                }
                
                resultSet.finish();
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                if (lock != null) {
                    source.unlock(lock);
                }
            }
        }
        
        protected void filter(CompletionResultSet resultSet) {
            super.filter(resultSet);
        }
        
    }
}
