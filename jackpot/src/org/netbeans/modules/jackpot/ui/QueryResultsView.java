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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.jackpot.JackpotModule;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.awt.EventQueue;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.swing.*;
import javax.swing.text.Position.Bias;
import javax.swing.tree.TreeModel;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.modules.jackpot.engine.Result;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Annotation;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.Line;
import org.openide.text.PositionRef;

/**
 * Query Results window singleton.
 */
public class QueryResultsView extends TopComponent {
    private JTree tree;
    private JScrollPane scrollPane;
    private boolean hasTransformerResults;
    private ModificationResult modifications;
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
    
    public void setResults(ModificationResult mods, List<Result> results) {
        ResultNode root = new ResultNode(mods, false, null);
        for (Result result : results) {
            ResultNode child = createResultsTree(result);
            root.add(child);
            if (result.isTransformerResult())
                hasTransformerResults = true;
        }
        root.setLabel(makeResultsLabel(results.size()));
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
    
    private ResultNode createResultsTree(Result qr) {
        ResultNode node = new ResultNode(qr, qr.isTransformerResult(), null);
        String label = qr.getLabel();
        String note = qr.getNote();
        if (note != null)
            label += ": " + note;
        node.setLabel(label);
        return node;
    }
    
    private void updateTree(final ResultNode root, final boolean rootVisible) {
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
                        Object obj = node.getUserObject();
                        if (obj instanceof Result) {
                            Result result = (Result)obj;
                            if (result.isTransformerResult())
                                displayTransformResult(result, (ModificationResult)root.getUserObject());
                            else
                                displayCommandResult((Result)result);
                        }
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
    
    private void doRefactoring() {
        ResultNode root = (ResultNode)tree.getModel().getRoot();
        ModificationResult mods = (ModificationResult)root.getUserObject();
        try {
            mods.commit();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        JackpotModule.getInstance().releaseEngine();
        setRoot(new ResultNode(null, false, null), false);
        close();
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
    private void displayTransformResult(Result result, ModificationResult mods) {

        Reader oldSource = null;
        try {
            String oldTitle = NbBundle.getMessage(QueryResultsView.class, "LBL_Original");
            String newTitle = NbBundle.getMessage(QueryResultsView.class, "LBL_Changed");
            String tabTitle = NbBundle.getMessage(QueryResultsView.class, "LBL_DiffTitle", 
                                                  result.getFileObject().getNameExt());
            oldSource = new InputStreamReader(result.getFileObject().getInputStream());
            String newSrc = mods.getResultingSource(result.getFileObject());
            Reader newSource = new StringReader(newSrc);
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
            } catch (IOException e) {
            }
        }
    }

    /**
     * Open operator result in Editor window.
     */
    
    private void displayCommandResult(Result result) {
        try {
            FileObject file = result.getFileObject();
            if (file == null)
                return;
            DataObject dob = DataObject.find(file);
            CloneableEditorSupport ces = (CloneableEditorSupport)dob.getCookie(EditorCookie.class);
            if (ces != null) {
                PositionRef start = ces.createPositionRef(result.getStartPos(), Bias.Forward);
                PositionRef end = ces.createPositionRef(result.getEndPos(), Bias.Forward);
                int line = start.getLine();
                int column = start.getColumn();
                int endLine = end.getLine();
                int endColumn = end.getColumn();
                Line l = ces.getLineSet().getOriginal(line);
                
                /*
                 * multi-line annotations are not supported, so truncate selection
                 * to end of first line if necessary.
                 */
                int maxColumn = (endLine == line) ? endColumn : l.getText().length();
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
    private static class JackpotSelectAnnotation extends Annotation {
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
