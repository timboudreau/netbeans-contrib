/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model;
import java.io.File;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.NamedAttributableWithSubElements;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.openide.util.Lookup;

/**General Utilities (usefull mainly when used together with command and
 * structure models {@link org.netbeans.modules.latex.model.command.LaTeXSource}
 * and {@link org.netbeans.modules.latex.structural.Model}, respectivelly).
 *
 * Because these utilities define platform specific operations, it is necessary
 * to {@link getDefault()} an instance before each use.
 *
 * @author Jan Lahoda
 */
public abstract class Utilities {
    
    /** Instances are allowed only to implementors*/
    protected Utilities() {
    }
    
    /**Returns the most approriate Utilities implementation for the current
     * situation. This method should be used each time an instance of Utilities
     * is required, as it is generally possible to change the instance between
     * invokations of this method.
     *
     * @return an appropriate instance of Utilities
     */
    public static synchronized Utilities getDefault() {
        if (instance != null)
            return instance;
        
        return instance = (Utilities) Lookup.getDefault().lookup(Utilities.class);
    }
    private static Utilities instance = null;
    
    /**Compares whether file1 and file2 represent the same file.
     *
     * @param file1 the first file to compare
     * @param file2 the second file to compare
     * @return true if and only if file1 and file2 represent the same file
     *         false otherwise
     */
    public abstract boolean compareFiles(Object file1, Object file2);
    
    /**Returns file for which was the Document created.
     *
     * @param doc Document in question
     * @return source file of the doc Document.
     */
    public abstract Object getFile(Document doc);
    
    /**Open a Document for given file.
     *
     * @param obj source file
     * @return Document created for file obj
     * @throws IOException if reading of the file fails
     */
    public abstract Document openDocument(Object obj) throws IOException;
    
    public abstract Object[] getRelativeFileList(Object file, String relativePath) throws IOException;
    
    public abstract Object   getRelativeFileName(Object file, String relativeFile) throws IOException;
    
    /*This was originaly required for some tests, but it probably is not part of a good LaTeX API:
     */
//    public abstract Object   findFile(File file) throws IOException;
    
    public abstract List/*<String>*/ findRelativeFilesBegining(Object file, String prefix) throws IOException;
    
    /**Remove errors in the editor. All annotations and other visual clues
     * created by {@link #showErrors} for given {@link ParseError} should be removed.
     *
     * @param errors errors to show.
     */
    public abstract void removeErrors(Collection/*<ParseError>*/ errors);
    
    /**Show errors in the editor. Some kind of annotations are shown in the
     * editor for the errors. The must be removed using {@link #removeErrors}.
     *
     * @param errors errors to show.
     */
    public abstract void showErrors(Collection/*<ParseError>*/ errors);
    
    public abstract ParseError createError(String message, SourcePosition pos);
    
    /**Find all \label commands in the source. More preciselly, all arguments with
     * attribute <i>#label</i>.
     *
     * @param source source to search for labels.
     * @return list of all found {@link LabelInfo LabelInfos}
     */
    public abstract List/*<LabelInfo>*/ getLabels(LaTeXSource source);
    
    public abstract String getHumanReadableDescription(SourcePosition position);
    
    public abstract JEditorPane getLastActiveEditorPane();
    
    /**Returns LaTeXSource corresponding to a given Document.
     *
     * @param doc Document to find the LaTeXSource for.
     * @return LaTeXSource corresponding to the given Document.
     */
    public LaTeXSource getSource(Document doc) {
        Object file = getFile(doc);
        
        return LaTeXSource.get(file);
    }
    
    public ArgumentNode getArgumentWithAttribute(CommandNode node, String attribute) {
        for (int cntr = 0; cntr < node.getArgumentCount(); cntr++) {
            ArgumentNode anode = node.getArgument(cntr);
            
            if (anode.getArgument().hasAttribute(attribute))
                return anode;
        }
        
        return null;
    }
    
    private boolean hasAttribute(Node node, String attribute) {
        if (node instanceof CommandNode) {
            return ((CommandNode) node).getCommand().hasAttribute(attribute);
        }
        if (node instanceof BlockNode) {
            Environment env = ((BlockNode) node).getEnvironment();
            
            if (env != null)
                return env.hasAttribute(attribute);
        }
        
        return false;
    }
    
    public String findCaptionForNode(Node node) {
//        System.err.println("findCaptionForNode(" + node + ")");
        while (node != null && !hasAttribute(node, "#captionable"))
            node = node.getParent();
        
//        System.err.println("captionable: " + node);
        
        if (node == null)
            return null;
        
        CaptionHandlerImpl chi = new CaptionHandlerImpl(node);
        
        node.traverse(chi);
        
        return chi.getCaption();
    }
    
    private class CaptionHandlerImpl extends TraverseHandler {
        
        private String caption = null;
        private Node   forNode = null;
        
        public CaptionHandlerImpl(Node forNode) {
            this.forNode = forNode;
        }
        
        public void argumentEnd(ArgumentNode node) {
        }
        
        public boolean argumentStart(ArgumentNode node) {
            return false;
        }
        
        public void blockEnd(BlockNode node) {
        }
        
        public boolean blockStart(BlockNode node) {
            return false;
        }
        
        public void commandEnd(CommandNode node) {
        }
        
        public boolean commandStart(CommandNode node) {
//            System.err.println("commandStart(" + node + ")");
            if (node.getCommand().hasAttribute("#caption-command")) {
                ArgumentNode anode = getArgumentWithAttribute(node, "#caption");
                
                if (anode != null)
                    caption = anode.getText().toString();
//                System.err.println("caption = " + caption );
            }
                
            return false;
        }
        
        public String  getCaption() {
            return caption;
        }
        
    }
}
