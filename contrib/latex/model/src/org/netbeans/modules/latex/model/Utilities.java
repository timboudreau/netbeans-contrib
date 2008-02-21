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
package org.netbeans.modules.latex.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import org.netbeans.fpi.gsf.CancellableTask;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.modules.latex.model.command.Environment;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.command.TraverseHandler;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
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
        
        return instance = Lookup.getDefault().lookup(Utilities.class);
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
    
    public abstract void     openPosition(SourcePosition position);
    
    public abstract Object[] getRelativeFileList(Object file, String relativePath) throws IOException;
    
    public abstract Object   getRelativeFileName(Object file, String relativeFile) throws IOException;
    
    public abstract String   getFileShortName(Object file);
    
    /*This was originaly required for some tests, but it probably is not part of a good LaTeX API:
     */
//    public abstract Object   findFile(File file) throws IOException;
    
    public abstract List/*<String>*/ findRelativeFilesBegining(Object file, String prefix) throws IOException;
    
    /**Find all \label commands in the source. More preciselly, all arguments with
     * attribute <i>#label</i>.
     *
     * @param source source to search for labels.
     * @return list of all found {@link LabelInfo LabelInfos}
     */
    public abstract List<? extends LabelInfo> getLabels(LaTeXParserResult root);
    
    public abstract String getHumanReadableDescription(SourcePosition position);
    
    public abstract JEditorPane getLastActiveEditorPane();
    
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

    private List<? extends PublicationEntry> getReferences(Object file, String  bibFileName) throws IOException {
        Object      bibFile = getRelativeFileName(file, bibFileName);
        
        if (bibFile == null) {
            bibFile = org.netbeans.modules.latex.model.Utilities.getDefault().getRelativeFileName(file, bibFileName + ".bib");
        }
        
        if (bibFile == null)
            throw new IllegalArgumentException("BiBTeX file " + bibFileName + " for main source file " + file + " not found.");
        
        BiBTeXModel model   = BiBTeXModel.getModel(bibFile);
        List<PublicationEntry> result  = new ArrayList<PublicationEntry>();
        
        for (Iterator i = model.getEntries().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
            if (e instanceof PublicationEntry) {
                PublicationEntry pEntry = (PublicationEntry) e;
                
                result.add(pEntry);
            }
        }
        
        return result;
    }
    
    private Map<LaTeXParserResult, List<? extends PublicationEntry>> parse2PublicationsCache = new WeakHashMap<LaTeXParserResult, List<? extends PublicationEntry>>();
    
    public final List<? extends PublicationEntry> getAllBibReferences(final LaTeXParserResult lpr) {
        List<? extends PublicationEntry> possibleResult = parse2PublicationsCache.get(lpr);
        
        if (possibleResult != null) {
            return possibleResult;
        }
        
        final List<PublicationEntry> result = new LinkedList<PublicationEntry>();
        DocumentNode node = lpr.getDocument();

        node.traverse(new DefaultTraverseHandler() {
            public boolean commandStart(CommandNode node) {
                //XXX: use attribute instead of command name:
                if ("\\bibliography".equals(node.getCommand().getCommand())) {
                    String          bibFileNames = node.getArgument(0).getText().toString();
                    StringTokenizer divider      = new StringTokenizer(bibFileNames, ",");

                    while (divider.hasMoreTokens()) {
                        String bibFileName  = divider.nextToken();
                        Object file         = lpr.getMainFile();

                        try {
                            result.addAll(getDefault().getReferences(file, bibFileName));
                        } catch (IOException e) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        } catch (IllegalArgumentException e) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        }
                    }
                }

                return false;
            }
        });
        
        parse2PublicationsCache.put(lpr, result);
        
        return result;
    }
    
}
