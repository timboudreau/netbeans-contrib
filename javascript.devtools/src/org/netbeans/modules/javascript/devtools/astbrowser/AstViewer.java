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
package org.netbeans.modules.javascript.devtools.astbrowser;

import com.oracle.nashorn.ir.FunctionNode;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
//import org.netbeans.api.gsf.CompilationInfo;
//import org.netbeans.api.gsf.Error;
//import org.netbeans.api.gsf.ParseEvent;
//import org.netbeans.api.gsf.ParseListener;
//import org.netbeans.api.gsf.Parser;
//import org.netbeans.api.gsf.ParserFile;
//import org.netbeans.api.gsf.ParserResult;
//import org.netbeans.api.gsf.SourceFileReader;

//import org.netbeans.modules.csl.core.Language;
//import org.netbeans.modules.csl.core.LanguageRegistry;
import org.netbeans.modules.editor.NbEditorDocument;
//import org.netbeans.modules.gsf.Language;
//import org.netbeans.modules.gsf.LanguageRegistry;
//import org.netbeans.modules.ruby.AstUtilities;
//import org.netbeans.spi.gsf.DefaultParserFile;


import org.netbeans.modules.javascript.devtools.astbrowser.TreeCreator;

import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/**
 * This class is based on
 *
 * @author Petr Pisl
 */
public class AstViewer extends TopComponent {
    private static final long serialVersionUID = 1L;
    private static AstViewer instance;

    /** path to the icon used by the component and its open action */

    private static final String PREFERRED_ID = "PHPAstViewer";
    private JTree tree;

    private boolean listen = true;
    private CaretListener caretListener;
    private JEditorPane lastPane;
    private FunctionNode lastResult;


    private Document highlightedDocument = null;
    private TreeCreator.TreeASTNodeAdapter highlighted = null;
    private JEditorPane highlightedEditor = null;
    private NbEditorDocument lastDocument = null;
    
    private BrowserButtons browserButtons;

    public static final String JAVASCRIPT_MIME_TYPE = "text/javascript"; // NOI18N
    
    private AstViewer() {
        initComponents();
        setLayout(new BorderLayout());
        tree = new JTree();
//        tree.setCellRenderer(new Renderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(new TreeCreator.TreeNodeCellRenderer());
        tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    if (!listen) {
                        return;
                    }

                    listen = false;
                    selectionChanged();
                    listen = true;
                }
            });
        add(new JScrollPane(tree), BorderLayout.CENTER);
        browserButtons = new BrowserButtons(this);
        add(browserButtons, BorderLayout.SOUTH);
        setName(NbBundle.getMessage(AstViewer.class, "CTL_AstViewer"));

        //setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    private void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                      .addGap(0, 300, Short.MAX_VALUE));
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized AstViewer getDefault() {
        if (instance == null) {
            instance = new AstViewer();
        }

        return instance;
    }

    /**
     * Obtain the AstViewer instance. Never call {@link #getDefault} directly!
     */
    public static synchronized AstViewer findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);

        if (win == null) {
            ErrorManager.getDefault()
                        .log(ErrorManager.WARNING,
                "Cannot find ASTBrowser component. It will not be located properly in the window system.");

            return getDefault();
        }

        if (win instanceof AstViewer) {
            return (AstViewer)win;
        }

        ErrorManager.getDefault()
                    .log(ErrorManager.WARNING,
            "There seem to be multiple components with the '" + PREFERRED_ID +
            "' ID. That is a potential source of errors and unexpected behavior.");

        return getDefault();
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public void componentOpened() {
    }

    public void componentShowing() {
        super.componentShowing();

        // TODO add custom code on component opening
        refresh();

        //
        //        if (listener == null) {
        //            listener = new Listener(this);
        //        }
    }

    public void componentHidden() {
        super.componentHidden();

        // TODO add custom code on component closing
        //        if (listener != null) {
        //            listener.remove();
        //            listener = null;
        //        }
    }

    public void componentClosed() {
        if (lastPane != null) {
            lastPane.removeCaretListener(caretListener);
            lastPane = null;
            lastDocument = null;
            org.openide.awt.StatusDisplayer.getDefault().setStatusText("");
        }
    }

    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    protected String preferredID() {
        return PREFERRED_ID;
    }

    /*public void refresh(FileObject fo, ParserResult result) {
        if ((result == null) || (result.getAst() == null)) {
            DefaultTreeModel model = new EmptyTreeModel();
            tree.setModel(model);
        } else {
            DefaultTreeModel model = new DefaultTreeModel(result.getAst());
            tree.setModel(model);
        }

        // Update caret listener
        DataObject dobj;

        try {
            dobj = DataObject.find(fo);
        } catch (DataObjectNotFoundException ex) {
            return;
        }

        EditorCookie editorCookie = (EditorCookie)dobj.getCookie(EditorCookie.class);

        if (editorCookie == null) {
            return;
        }

        JEditorPane[] panes = editorCookie.getOpenedPanes();

        if ((panes == null) || (panes.length == 0)) {
            return;
        }

        JEditorPane pane = panes[0];

        if (caretListener == null) {
            caretListener = new CListener();
        }

        if ((lastPane != null) && (lastPane != pane)) {
            lastPane.removeCaretListener(caretListener);
            lastPane = null;
            lastDocument = null;
        }

        if (lastPane == null) {
            pane.addCaretListener(caretListener);
            lastPane = pane;
            lastDocument = (NbEditorDocument)pane.getDocument();
        }

        lastResult = result;

        int pos = pane.getCaret().getDot();
        String mimeType = (String)pane.getDocument().getProperty("mimeType");
        Language l = LanguageRegistry.getInstance().getLanguageByMimeType(mimeType);
        pos = l.getParser().getPositionManager().getAstOffset(result, pos);
        showPosition(pos);
    }
*/
    protected void refresh() {
        Node[] ns = TopComponent.getRegistry().getActivatedNodes();

        if (ns.length != 1) {
            return;
        }

        DataObject dataObject = (DataObject)ns[0].getLookup().lookup(DataObject.class);
        EditorCookie editorCookie = (EditorCookie)ns[0].getLookup().lookup(EditorCookie.class);

        if (editorCookie == null) {
            return;
        }

        if (editorCookie.getOpenedPanes() == null) {
            return;
        }

        if (editorCookie.getOpenedPanes().length < 1) {
            return;
        }

        JEditorPane pane = editorCookie.getOpenedPanes()[0];

        if (caretListener == null) {
            caretListener = new CListener();
        }

        if ((lastPane != null) && (lastPane != pane)) {
            lastPane.removeCaretListener(caretListener);
            lastPane = null;
            lastDocument = null;
        }

        if (lastPane == null) {
            pane.addCaretListener(caretListener);
            lastPane = pane;
            lastDocument = (NbEditorDocument)pane.getDocument();
        }

        final Document doc = editorCookie.getDocument();

        if ((doc == null) || !(doc instanceof NbEditorDocument)) {
            return;
        }

        String mimeType = (String)doc.getProperty("mimeType");

        if (mimeType == null || !mimeType.equals(JAVASCRIPT_MIME_TYPE)) {
            DefaultTreeModel model = new EmptyTreeModel();
            tree.setModel(model);

            return;
        }
            
        try {
//            Language l = LanguageRegistry.getInstance().getLanguageByMimeType(mimeType);
//            if (l == null) {
//                DefaultTreeModel model = new EmptyTreeModel();
//                tree.setModel(model);
//
//                return;
//            }

            TreeNode astNode = null;

            DataObject dobj = (DataObject)doc.getProperty(doc.StreamDescriptionProperty);
            FileObject file = dobj.getPrimaryFile();

            //Iterable<? extends ComFile> iterable = l.getParser().parse(file, doc.getText(0, doc.getLength()), errorHandler);
            //ParserResult result =
            //    l.getParser().parseFiles(file, doc.getText(0, doc.getLength()), errorHandler);
            
            
            FunctionNode fn = parseNashron(doc.getText(0, doc.getLength()));
//            Parser parser = l.getParser();
//            
//            final ParserResult[] resultHolder = new ParserResult[1];
//            ParseListener listener =
//                new ParseListener() {
//                    public void started(ParseEvent e) {
//                    }
//
//                    public void error(org.netbeans.modules.gsf.api.Error e) {
//                    }
//
//                    public void exception(Exception e) {
//                    }
//
//                    public void finished(ParseEvent e) {
//                        // TODO - check state
//                        if (e.getKind() == ParseEvent.Kind.PARSE) {
//                            resultHolder[0] = e.getResult();
//                        }
//                    }
//                                 
//                };
//
//            List<ParserFile> sourceFiles = new ArrayList<ParserFile>(1);
//            sourceFiles.add(new DefaultParserFile(file, null, false));
//
//            SourceFileReader reader =
//                new SourceFileReader() {
//                    public CharSequence read(ParserFile fileObject)
//                        throws IOException {
//                        try {
//                            return doc.getText(0, doc.getLength());
//                        } catch (BadLocationException ex) {
//                            return "";
//                        }
//                    }
//                
//                public int getCaretOffset(ParserFile file) {
//                    return -1;
//                }
//            };
//            
//            parser.parseFiles(new Parser.Job (sourceFiles, listener, reader, null));
//
//            ParserResult result = resultHolder[0];
            lastResult = fn;
            
            astNode = fn == null ? null : (new TreeCreator()).createTree(fn, browserButtons.isDetailView());

            if (astNode == null) {
                return;
            }

            DefaultTreeModel model = new DefaultTreeModel(astNode);
            tree.setModel(model);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    private static final boolean SCRIPTING =
            Boolean.valueOf(System.getProperty("parsertest.scripting"));
    
    protected com.oracle.nashorn.ir.FunctionNode parseNashron(String text) {
        long start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.Source source = new com.oracle.nashorn.runtime.Source("test", text);
        long end = System.currentTimeMillis();
//        System.out.println("Nashron creating source: " + (end - start));
        start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.options.Options options = new com.oracle.nashorn.runtime.options.Options("nashorn");
        options.process(new String[]{
            "--parse-only=true", 
            //"--print-parse=true",    
            "--debug-lines=false",
            "--print-symbols=true", 
//            "--dump-ir-graph=true",
            "--print-symbols=true"});
        end = System.currentTimeMillis();
//        System.out.println("Nashron creating options: " + (end - start));
        //options._scripting = SCRIPTING;
        
        start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.ErrorManager errors = new IdeErrorManager();
        errors.setLimit(100);
        end = System.currentTimeMillis();
//        System.out.println("Nashron creating errors: " + (end - start));
        
        start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.Context contextN = new com.oracle.nashorn.runtime.Context(options, errors);
        com.oracle.nashorn.runtime.Context.setContext(contextN);
        //contextN.setGlobal(new com.oracle.nashorn.objects.Global(contextN));
        end = System.currentTimeMillis();
//        System.out.println("Nashron creating context: " + (end - start));
        start = System.currentTimeMillis();

        com.oracle.nashorn.codegen.Compiler compiler = new com.oracle.nashorn.codegen.Compiler(source, contextN);
        
        end = System.currentTimeMillis();
//        System.out.println("Nashron creating compiler: " + (end - start));
        start = System.currentTimeMillis();
        com.oracle.nashorn.parser.Parser parser = new com.oracle.nashorn.parser.Parser(compiler);
        com.oracle.nashorn.ir.FunctionNode node = parser.parse(com.oracle.nashorn.codegen.CompilerConstants.runScriptName);
        //node.accept(new com.oracle.nashorn.codegen.Lower(compiler));
        end = System.currentTimeMillis();
        System.out.println("Nashron parsing: " + (end - start) + "ms");
        System.out.println("Errors: " + errors.getNumErrors());
        System.out.println("Warnings: " + errors.getNumWarnings());
        
        
        return node;
    }
    
    private TreeNode findNode(TreeNode parent, int index) {
        if (parent instanceof TreeCreator.TreeASTNodeAdapter) {
            int begin = ((TreeCreator.TreeASTNodeAdapter)parent).getStartOffset();
            int end = ((TreeCreator.TreeASTNodeAdapter)parent).getEndOffset();

            if ((index >= begin) && (index <= end) && begin > 0) {
                TreeNode candidate = parent;

                for (int i = 0; i < parent.getChildCount(); i++) {
                    TreeNode child = parent.getChildAt(i);
                    TreeNode found = findNode(child, index);

                    if (found != null) {
                        return found;

                        //                            if (candidate == null) {
                        //                                candidate = found;
                        //                            } else {
                        //                                // See which one is better - further away.
                        //                                // Another fitness test could be the size of the range...
                        //                                int depth1 = 0;
                        //
                        //                                // See which one is better - further away.
                        //                                // Another fitness test could be the size of the range...
                        //                                int depth2 = 0;
                        //                                TreeNode n = candidate;
                        //
                        //                                while ((n != null) && (n != child)) {
                        //                                    n = n.getParent();
                        //                                    depth1++;
                        //                                }
                        //
                        //                                n = found;
                        //
                        //                                while ((n != null) && (n != child)) {
                        //                                    n = n.getParent();
                        //                                    depth2++;
                        //                                }
                        //
                        //                                if (depth2 < depth1) {
                        //                                    candidate = found;
                        //                                }
                        //                            }
                    }
                }

                return candidate;
            } else {
                TreeNode candidate = null;

                for (int i = 0; i < parent.getChildCount(); i++) {
                    TreeNode child = parent.getChildAt(i);
                    TreeNode found = findNode(child, index);

                    if (found != null) {
                        return found;
                    }
                }

                return null;
            }
        } else {
            return null;
        }
        //return null;
    }

    private void showPosition(int position) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
        TreeNode closest = findNode(root, position);
        List<TreeNode> path = new ArrayList<TreeNode>();

        while (closest != null) {
            path.add(0, closest);
            closest = closest.getParent();
        }
        
        if (path.size() == 0) {
            return;
        }

        try {
            TreePath treePath = new TreePath(path.toArray());
            listen = false;
            tree.setSelectionPath(treePath);
            tree.expandPath(treePath);
            tree.scrollPathToVisible(treePath);
            org.openide.awt.StatusDisplayer.getDefault()
                                           .setStatusText("Caret position : " + position);
            listen = true;
        } catch (Exception ex) {
            // XXX TODO debug
            ex.printStackTrace();
        }
    }

    private void selectionChanged() {
        System.out.println("selection changed");
        removeHighlight();

        if (!tree.hasFocus()) {
            return;
        }

        TreePath selPath = tree.getSelectionPath();

        if (selPath == null) {
            return;
        }

        //NavigatorNode node = (NavigatorNode) selPath.getLastPathComponent ();
       TreeCreator.TreeASTNodeAdapter node =
            (TreeCreator.TreeASTNodeAdapter)tree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }
        
       if (node.getStartOffset() == -1) {
           return;
       }
       
        highlighted = node;
        HighlightSections highlighter = (HighlightSections) lastDocument.getProperty(HighlightSections.class);
        highlightedDocument = lastDocument;
        
        highlighter.setSelectedNode(highlightedDocument, node);
        if (browserButtons.showLocationToken()) {
            highlighter.addLocationToken(lastResult);
        }
        lastPane.setCaretPosition(node.getStartOffset());
        System.out.println("carret possition nastaveno: " + node.getStartOffset());
        highlightedEditor = lastPane;
        highlightedEditor.repaint();
    }

    private void removeHighlight() {
        if (highlighted == null) {
            return;
        }
        HighlightSections highlighter = (HighlightSections) highlightedDocument.getProperty(HighlightSections.class);
        highlighter.setSelectedNode(highlightedDocument, null);
        highlightedEditor.repaint();
        highlighted = null;
        highlightedDocument = null;
        highlightedEditor = null;
    }

    class CListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            if (!listen) {
                return;
            }

            int position = e.getDot();
            //String mimeType = (String)lastPane.getDocument().getProperty("mimeType");
            //Language l = LanguageRegistry.getInstance().getLanguageByMimeType(mimeType);
            //position = l.getParser().getPositionManager().getAstOffset(lastResult, position);
            showPosition(position);
        }
    }

    private static class Renderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                hasFocus);
        }
    }

    static final class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return AstViewer.getDefault();
        }
    }

    
    private static TreeNode EMPTY_ROOT = new TreeNode() {
        public TreeNode getChildAt(int arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getChildCount() {
            return 0;
        }

        public TreeNode getParent() {
            return null;
        }

        public int getIndex(TreeNode arg0) {
            return -1;
        }

        public boolean getAllowsChildren() {
            return false;
        }

        public boolean isLeaf() {
            return true;
        }

        public Enumeration children() {
            return new Vector().elements();
        }
    };
    
    private class EmptyTreeModel extends DefaultTreeModel {
        
        EmptyTreeModel() {
            super(EMPTY_ROOT);
        }
        
    }
    
    protected void checkOffsets() {
        CheckOffsetIntegrityVisitor visitor = new CheckOffsetIntegrityVisitor();
        int offset = visitor.checkOffset(lastResult);
        System.out.println("offset: " + offset);
        if (offset > -1) {
            lastPane.setCaretPosition(offset);
        }
    }
    
    protected void runPerformanceTest(File file) {

        if (!file.isDirectory()) {
            System.out.println("Running performance test for: " + file.getAbsolutePath());
            Source source = Source.create(FileUtil.toFileObject(file));
            Snapshot snapshot = source.createSnapshot();
            long nTime = parseNashronPerformance(snapshot);
//            long rTime = parseRhinoPerformance(snapshot);
            System.out.println("Nashorn: " + nTime);
//            System.out.println("Rhino: " + rTime);
        } else {
            System.out.println("Running performance test for folder: " + file.getAbsolutePath());
            FileObject folder = FileUtil.toFileObject(file);
            Enumeration<? extends FileObject> en = folder.getChildren(true);
            int count = 0;
            long nTime = 0;
            long rTime = 0;
            while(en.hasMoreElements()) {
                FileObject fo = en.nextElement();
                if ("js".equals(fo.getExt())) {
                    count++;
                    Source source = Source.create(fo);
                    Snapshot snapshot = source.createSnapshot();
                    nTime = nTime + parseNashronPerformance(snapshot);
//                    rTime = rTime + parseRhinoPerformance(snapshot);
                }
            }
            System.out.println("Parsed " + count + " .js files:");
            System.out.println("Nashorn: " + nTime);
//            System.out.println("Rhino: " + rTime);
        }
    }
    
    
    protected long parseNashronPerformance(Snapshot snapshot) {
        long start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.Source source = new com.oracle.nashorn.runtime.Source("test", snapshot.getText().toString());
        start = System.currentTimeMillis();
        com.oracle.nashorn.runtime.options.Options options = new com.oracle.nashorn.runtime.options.Options("nashorn");
        options.process(new String[]{
            "--parse-only=true", 
            //"--print-parse=true",    
            "--debug-lines=false",
            "--print-symbols=true", 
            "--dump-ir-graph=true", 
            "--print-symbols=true"});

        com.oracle.nashorn.runtime.ErrorManager errors = new IdeErrorManager();
        errors.setLimit(100);
        com.oracle.nashorn.runtime.Context contextN = new com.oracle.nashorn.runtime.Context(options, errors);
        com.oracle.nashorn.runtime.Context.setContext(contextN);

        com.oracle.nashorn.codegen.Compiler compiler = new com.oracle.nashorn.codegen.Compiler(source, contextN);
        com.oracle.nashorn.parser.Parser parser = new com.oracle.nashorn.parser.Parser(compiler);
        com.oracle.nashorn.ir.FunctionNode node = parser.parse(com.oracle.nashorn.codegen.CompilerConstants.runScriptName);

        long end = System.currentTimeMillis();
        return end - start;
    }
    
//    protected long parseRhinoPerformance(Snapshot snapshot) {
//        long start = System.currentTimeMillis();
//        JsParser jsParser  = new JsParser();
//        JsParser.Context context = new JsParser.Context(snapshot, null);
//        jsParser.parseBuffer(context, JsParser.Sanitize.NEVER);
//        long end = System.currentTimeMillis();
//        return end - start;
//    }
}
