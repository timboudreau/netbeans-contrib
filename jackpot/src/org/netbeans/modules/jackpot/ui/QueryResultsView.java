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

package org.netbeans.modules.jackpot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectStreamException;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.java.source.engine.JackpotEngine;
import org.netbeans.api.java.source.query.Query;
import org.netbeans.api.java.source.query.QueryResult;
import org.netbeans.api.java.source.query.ResultTableModel;
import org.netbeans.api.java.source.query.SearchResult;
import org.netbeans.api.java.source.query.SourceSelection;
import org.netbeans.api.java.source.transform.Change;
import org.netbeans.api.java.source.transform.ChangeList;
import org.netbeans.api.java.source.transform.ChangeSet;
import org.netbeans.api.java.source.transform.TransformResult;
import org.netbeans.api.java.source.transform.Transformer;
import org.netbeans.api.java.source.transform.TransformerResult;
import org.netbeans.modules.jackpot.JackpotModule;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreeModel;
import org.netbeans.api.diff.Diff;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.text.NbDocument;

/**
 * Query Results window singleton.
 */
public class QueryResultsView extends TopComponent {
    private JTree tree;
    private JScrollPane scrollPane;
    private boolean hasTransformerResults;
    private JackpotSelectAnnotation currentSelection;
    private static Image png = Utilities.loadImage("org/netbeans/modules/jackpot/resources/QueryRefactor.png");
    private static ImageIcon icon = new ImageIcon(png);
    private static WeakReference<QueryResultsView> instance = null;
    
    /**
     * unique ID of <code>TopComponent</code> (singleton)
     */
    private static final String ID = "jackpot-query-results";
    
    private static final TreeModel emptyModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    
    public static synchronized QueryResultsView getInstance() {
        QueryResultsView view = null;
        
        if (EventQueue.isDispatchThread())
            view = getExistingWindow();
        else {
            final Object[] o = new Object[1];
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        o[0] = getExistingWindow();
                    }
                });
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
            view = (QueryResultsView)o[0];
        }
        if (view == null)
            view = getSingleton();
        return view;
    }
    
    private static QueryResultsView getExistingWindow() {
        return (QueryResultsView)WindowManager.getDefault().findTopComponent(ID);
    }
    
    /**
     * Accessor reserved for the windowing system only. The window
     * system calls this method to create an instance of this
     * <code>TopComponent</code> from a <code>.settings</code> file.
     * <p>
     * <em>This method should not be called anywhere except from the window
     * system's code. </em>
     *
     * @return  singleton - instance of this class
     */
    
    public static QueryResultsView getSingleton() {
        QueryResultsView view;
        
        if (instance == null ||
                (view = (QueryResultsView)instance.get()) == null) {
            view = new QueryResultsView();
            instance = new WeakReference<QueryResultsView>(view);
        }
        return view;
    }
    
    private QueryResultsView() {
        setName("Query Results");
        setDisplayName(NbBundle.getMessage(QueryResultsView.class, "LBL_QueryResults"));
        setIcon(png);
        getActionMap().put("jumpPrev",
                new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                decrementSelection();
            }
        });
        getActionMap().put("jumpNext",
                new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                incrementSelection();
            }
        });
        setLayout(new BorderLayout());
        currentSelection = new JackpotSelectAnnotation();
    }
    
    public void setResults(ResultTableModel[] results) {
        ResultNode root = new ResultNode(results, false, null);
        int totalResults = 0;
        int nchild = 0;
        for (int i = 0; i < results.length; i++) {
            int n = results[i].getResultCount();
            if (n > 0) {
                ResultNode child = createResultsTree(results[i]);
                root.insert(child, nchild++);
                totalResults += n;
            }
        }
        root.setLabel(makeResultsLabel(totalResults));
        setRoot(root, true);
    }
    
    public void setResults(ResultTableModel results) {
        ResultNode root = createResultsTree(results);
        root.setLabel(makeResultsLabel(results.getResultCount()));
        setRoot(root, true);
    }
    
    private String makeResultsLabel(int n) {
        ResourceBundle bundle = NbBundle.getBundle(QueryResultsView.class);
        if (n == 0)
            return bundle.getString("MSG_NoResults");
        ChoiceFormat cf = new ChoiceFormat(bundle.getString("FMT_Results"));
        String nodeStr = cf.format(n);
	return MessageFormat.format(bundle.getString("MSG_Results"), n, nodeStr);
    }
    
    private void setRoot(final ResultNode root, final boolean rootVisible) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                updateTree(root, rootVisible);
                updatePanel();
                requestActive();
            }
        });
    }
    
    private ResultNode createResultsTree(ResultTableModel results) {
        final ResultNode root = new ResultNode(results, false, icon);
        root.setLabel(makeHeading(results.getQuery()));
        int n = results.getResultCount();
        for (int i = 0; i < n; i++) {
            QueryResult qr = results.getResult(i);
            boolean isTransformer = qr instanceof TransformerResult;
            ResultNode child = new ResultNode(qr, isTransformer, null);
            root.insert(child, i);
            if (isTransformer) {
                hasTransformerResults = true;
                Change change = ((TransformerResult)qr).getChanges();
                if (change instanceof ChangeList) {
                    ChangeList chList = (ChangeList)change;
                    child.setLabel(chList.getRefactoringName());
                    int nchildren = 0;
                    for (Change ch : chList) {
                        ResultNode subChild = new ResultNode(ch, true, null);
                        child.insert(subChild, nchildren++);
                        Element element = qr.getSourceSelection().getElement();
                        subChild.setLabel(makeElementLabel(element));
                    }
                } else {
                    Element element = qr.getSourceSelection().getElement();
                    child.setLabel(makeElementLabel(element));
                }
            } else {
                Element element = qr.getSourceSelection().getElement();
                String label = makeElementLabel(element) + ": " + qr.getNote();
                child.setLabel(label);
            }
        }
        return root;
    }
    
    private void updateTree(final MutableTreeNode root, final boolean rootVisible) {
        // add panel with appropriate content
        tree = new JTree(root);
        tree.setRootVisible(rootVisible);
        tree.setCellRenderer(new ResultRenderer());
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    currentSelection.detach();
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        ResultNode node = (ResultNode) path.getLastPathComponent();
                        Object result = node.getUserObject();
                        if (result instanceof TransformerResult)
                            displayTransformResult((TransformerResult)result);
                        else if (result instanceof QueryResult)
                            displayCommandResult((QueryResult)result);
                    }
                }
            }
        });
        ResultNodeListener l = new ResultNodeListener(hasTransformerResults);
        tree.addMouseListener(l);
        tree.addKeyListener(l);
        tree.setToggleClickCount(0);
        Dimension d = ((ResultRenderer) tree.getCellRenderer()).getPreferredSize(hasTransformerResults);
        tree.setRowHeight(d.height);
        for (int i = tree.getRowCount() - 1; i >= 0; i--)
            tree.expandRow(i);
    }
    
    private void updatePanel() {
        scrollPane = new JScrollPane(tree);
        removeAll();
        add(scrollPane, BorderLayout.CENTER);
        if (hasTransformerResults) {
            String label = NbBundle.getBundle(QueryResultsView.class).getString("BTN_QueryResults_Apply");
            JButton applyButton = new JButton(label);
            applyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doRefactoring();
                }
            });
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.add(applyButton);
            add(panel, BorderLayout.SOUTH);
        }
        validate();
    }
    
    private String makeElementLabel(Element element) {
        String cls = null;
        String pkg = "";
        Element e = element;
        while (e != null) {
            if (e instanceof TypeElement) {
                String s = e.getSimpleName().toString();
                cls = cls == null ? s : s + '.' + cls;
            }
            if (e instanceof PackageElement) {
                pkg = "(" + ((PackageElement)e).getQualifiedName().toString() + ")";
                break;
            }
            e = e.getEnclosingElement();
        }
        String name = element.getSimpleName().toString();
        String fullname = cls != null ? cls + '.' + name : name;
        return pkg.length() > 0 ? fullname + ' ' + pkg : fullname;
    }
    
    private String makeHeading(Query query) {
        if (query == null) // shouldn't happen, but true for Finders
            return NbBundle.getBundle(QueryResultsView.class).getString("LBL_QueryResults");
        StringBuffer sb = new StringBuffer("<html>");
        sb.append(query.getQueryDescription());
        if (query instanceof Transformer) {
            sb.append("<font color=\"#a9a9a9\"> =&gt; ");
            sb.append(((Transformer)query).getRefactoringDescription());
            sb.append("</font>");
        }
        sb.append("</html>");
        return sb.toString();
    }
    
    private void doRefactoring() {
        ResultNode root = (ResultNode)tree.getModel().getRoot();
        if (anyNodesDeselected(root)) {
            JackpotEngine engine = JackpotModule.getInstance().getEngine();
            engine.undo(true);
            refactorNode(root, engine);
        }
        // Save changes and release engine instance.
        JackpotModule.getInstance().releaseEngine();
        // Release tree model
        setRoot(new ResultNode(null, false, null), false);
        close();
    }
    
    private void refactorNode(ResultNode node, JackpotEngine engine) {
        if (node.isSelected()) {
            // some changes need to be applied
            Object obj = node.getUserObject();
            if (obj instanceof ResultTableModel[]) {
                int n = node.getChildCount();
                for (int i = 0; i < n; i++)
                    refactorNode((ResultNode)node.getChildAt(i), engine);
            }
            else if (obj instanceof TransformResult) {
                TransformResult results = (TransformResult)obj;
                ChangeSet changes = results.getChangeSet();
                updateChangeSet(changes, node);
                engine.applyChanges(changes);
            }
            // ignore SearchResults since they don't contain changes
            else if (!(obj instanceof SearchResult))
                throw new AssertionError("unknown node type: " + obj.getClass().getName());
        }
    }
    
    /**
     * Returns true if any ResultNodes were unchecked, indicating that some of
     * the changes need to be backed out.
     */
    private boolean anyNodesDeselected(ResultNode node) {
        if (!node.isSelected())
            return true;
        int n = node.getChildCount();
        for (int i = 0; i < n; i++)
            if (anyNodesDeselected((ResultNode)node.getChildAt(i)))
                return true;
        return false;
    }
    
    private void updateChangeSet(ChangeSet changes, ResultNode node) {
        if (!node.isSelected())  {
            Object o = node.getUserObject();
            if (o instanceof TransformerResult) {
                TransformerResult tr = (TransformerResult)o;
                changes.remove(tr.getChanges());
            }
        } 
        int n = node.getChildCount();
        for (int i = 0; i < n; i++)
            updateChangeSet(changes, (ResultNode)node.getChildAt(i));
    }
    
    private void incrementSelection() {
        //FIXME:
    }
    
    private void decrementSelection() {
        //FIXME:
    }
    
    public void clear() {
        tree.setModel(emptyModel);
    }
    
    protected String preferredID() {
        return ID;
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_ALWAYS;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return getSingleton();
    }
    
    /**
     * Open transformer result in Diff window.
     */
    
    private void displayTransformResult(TransformerResult result) {
        Reader oldSource = null;
        Reader newSource = null;
        
        try {
            SourceSelection oldSrc = result.getOriginalSourceSelection();
            SourceSelection newSrc = result.getSourceSelection();
            String oldPath = oldSrc.getSourceFileName();
            String oldTitle = oldPath.substring(oldPath.lastIndexOf('/') + 1);
            String newPath = newSrc.getSourceFileName();
            String newTitle = newPath.substring(newPath.lastIndexOf('/') + 1);
            String tabTitle = NbBundle.getMessage(QueryResultsView.class, "LBL_DiffTitle", oldTitle);
            
            oldSource = new StringReader(oldSrc.getSource());
            newSource = new StringReader(newSrc.getSource());
            final java.awt.Component c = Diff.getDefault().createDiff(tabTitle,
                    oldTitle,
                    oldSource,
                    null,
                    newTitle,
                    newSource,
                    "text/x-java");
            
            if (c != null) {
                EventQueue.invokeLater(new Runnable() {
                    
                    public void run() {
                        if (c instanceof TopComponent) {
                            ((TopComponent)c).open();
                            ((TopComponent)c).requestActive();
                        } else {
                            c.setVisible(true);
                            c.requestFocusInWindow();
                        }
                    }
                });
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return;
        } finally {
            try {
                if (oldSource != null)
                    oldSource.close();
                if (newSource != null)
                    newSource.close();
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * Open operator result in Editor window.
     */
    
    private void displayCommandResult(QueryResult result) {
        SourceSelection src = result.getSourceSelection();
        
        try {
            FileObject file = FileUtil.toFileObject(new File(src.getSourceFileName()));
            
            if (file == null)
                return;
            DataObject dob = DataObject.find(file);
            EditorCookie ed = (EditorCookie)dob.getCookie(EditorCookie.class);
            
            if (ed != null) {
                StyledDocument doc = ed.openDocument();
                int pos = docOffsetFromFileOffset(src.getStartOffset(), src.getSource());
                int line = NbDocument.findLineNumber(doc, pos);
                int column = NbDocument.findLineColumn(doc, pos);
                int endPos = docOffsetFromFileOffset(src.getEndOffset(), src.getSource());
                int endLine = NbDocument.findLineNumber(doc, endPos);
                int endColumn = NbDocument.findLineColumn(doc, endPos);
                Line l = ed.getLineSet().getOriginal(line);
                /*
                 * multi-line annotations are not supported, so truncate selection
                 * to end of first line if necessary.
                 */
                int maxColumn = (endLine == line) ? endColumn
                        : l.getText().length();
                Line.Part part = l.createPart(column, maxColumn - column);
                
                currentSelection.setNote(result.getNote());
                currentSelection.attach(part);
                l.show(Line.SHOW_GOTO, column);
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    /**
     * Convert a file offset to a document offset.  A document offset is
     * equivalent to a file offset, except that document line endings are
     * always one character.  A source file in Unix format will therefore
     * have the same offsets for both file and documents, while DOS formatted
     * files will have a smaller document offset (except for the first line).
     */
    private static int docOffsetFromFileOffset(int pos, String src) {
        try {
            assert pos < src.length();
            if (pos == 0 || src.length() == 0)
                return 0;
            BufferedReader br = new BufferedReader(new StringReader(src.substring(0, pos)));
            int offset = 0;
            String line;
            while ((line = br.readLine()) != null)
                offset += line.length() + 1;  // include one for the line-ending "char"
            offset--;                         // except for the last, partial, line
            return offset;
        } catch (IOException e) {
            throw new AssertionError();  // should never happen with StringReader...
        }
    }
    
    protected void componentClosed() {
        currentSelection.detach();
        JackpotModule module = JackpotModule.getInstance();
        if (module.isRunning())
            // user hit the window's close button, so discard this query.
            module.abortEngine();
    }

    
    static class JackpotSelectAnnotation extends Annotation {
        private String note;
        
        public void setNote(String note) {
            this.note = note;
        }
        
        public String getAnnotationType() {
            return "jackpot-select-annotation";
        }
        
        public String getShortDescription() {
            return note;
        }
    }
}
