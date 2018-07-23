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

package org.netbeans.modules.java.tools.navigation;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.netbeans.api.java.source.CompilationInfo;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class JavaStructurePanel extends javax.swing.JPanel {
    private FileObject fileObject;
    private JavaStructureModel javaStructureModel;

    public JavaStructurePanel(FileObject fileObject, Element[] elements, CompilationInfo compilationInfo) {
        this.fileObject = fileObject;
        initComponents();

        ToolTipManager.sharedInstance().registerComponent(javaFileStructureTree);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        caseSensitiveFilterCheckBox.setSelected(JavaStructureOptions.isCaseSensitive());
        showFQNToggleButton.setSelected(JavaStructureOptions.isShowFQN());
        showInnerToggleButton.setSelected(JavaStructureOptions.isShowInner());
        showConstructorsToggleButton.setSelected(JavaStructureOptions.isShowConstructors());
        showMethodsToggleButton.setSelected(JavaStructureOptions.isShowMethods());
        showFieldsToggleButton.setSelected(JavaStructureOptions.isShowFields());
        showEnumConstantsToggleButton.setSelected(JavaStructureOptions.isShowEnumConstants());
        showProtectedToggleButton.setSelected(JavaStructureOptions.isShowProtected());
        showPackageToggleButton.setSelected(JavaStructureOptions.isShowPackage());
        showPrivateToggleButton.setSelected(JavaStructureOptions.isShowPrivate());
        showStaticToggleButton.setSelected(JavaStructureOptions.isShowStatic());

        javaFileStructureTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        javaFileStructureTree.setRootVisible(false);
        javaFileStructureTree.setShowsRootHandles(true);
        javaFileStructureTree.setCellRenderer(new JavaToolsTreeCellRenderer());

        javaStructureModel = new JavaStructureModel(fileObject, elements, compilationInfo);
        javaFileStructureTree.setModel(javaStructureModel);
        javaDocPane.setEditorKitForContentType("text/html", new HTMLEditorKit()); // NOI18N
        javaDocPane.setContentType("text/html"); // NOI18N

        filterTextField.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        applyFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        applyFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        applyFilter();
                    }
                });

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        firstRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0, true),
                JComponent.WHEN_FOCUSED);

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        previousRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true),
                JComponent.WHEN_FOCUSED);

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        nextRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
                JComponent.WHEN_FOCUSED);

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        lastRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, true),
                JComponent.WHEN_FOCUSED);

        signatureEditorPane.putClientProperty(
            "HighlightsLayerExcludes", // NOI18N
            "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" // NOI18N
        );

        signatureEditorPane.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        firstRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0, true),
                JComponent.WHEN_FOCUSED);

        signatureEditorPane.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        previousRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true),
                JComponent.WHEN_FOCUSED);

        signatureEditorPane.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        nextRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
                JComponent.WHEN_FOCUSED);

        signatureEditorPane.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        lastRow();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, true),
                JComponent.WHEN_FOCUSED);

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        TreePath treePath = javaFileStructureTree.getSelectionPath();
                        if (treePath != null) {
                            Object node = treePath.getLastPathComponent();
                            if (node instanceof JavaToolsJavaElement) {
                                JavaToolsJavaElement javaToolsJavaElement = (JavaToolsJavaElement) node;
                                applyFilter();
                            }
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true),
                JComponent.WHEN_FOCUSED);

        filterTextField.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        TreePath treePath = javaFileStructureTree.getSelectionPath();
                        if (treePath != null) {
                            Object node = treePath.getLastPathComponent();
                            if (node instanceof JavaToolsJavaElement) {
                                gotoElement((JavaToolsJavaElement) node);
                            }
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);

        caseSensitiveFilterCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        javaFileStructureTree.addMouseListener(
                new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                Point point = me.getPoint();
                TreePath treePath = javaFileStructureTree.getPathForLocation(point.x, point.y);
                if (treePath != null) {
                    Object node = treePath.getLastPathComponent();
                    if (node instanceof JavaToolsJavaElement) {
                        if (me.getClickCount() == 1) {
                            if (me.isControlDown()) {
                                filterTextField.setText("");
                                JavaToolsJavaElement javaToolsJavaElement = (JavaToolsJavaElement) node;
                                applyFilter();
                            }
                        } else  if (me.getClickCount() == 2) {
                            gotoElement((JavaToolsJavaElement) node);
                        }
                    }
                }

            }
        }
        );

        javaFileStructureTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                showSignature();
                showJavaDoc();
            }
        });

        javaFileStructureTree.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        TreePath treePath = javaFileStructureTree.getLeadSelectionPath();
                        if (treePath != null) {
                            Object node = treePath.getLastPathComponent();
                            if (node instanceof JavaToolsJavaElement) {
                                gotoElement((JavaToolsJavaElement) node);
                            }
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);

        javaFileStructureTree.registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        TreePath treePath = javaFileStructureTree.getLeadSelectionPath();
                        if (treePath != null) {
                            Object node = treePath.getLastPathComponent();
                            if (node instanceof JavaToolsJavaElement) {
                                filterTextField.setText("");
        //                        JavaStructurePanel.this.JavaStructureModel = new JavaStructureModel(((JavaToolsJavaElement)node).getElementHandle());
        //                        javaFileStructureTree.setModel(JavaStructurePanel.this.JavaStructureModel);
                                applyFilter();
                            }
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true),
                JComponent.WHEN_FOCUSED);

        javaDocPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    if (url != null //&& url.getProtocol().equals("http")
                            ) {
                        HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                    }
                }
            }
        });


        showFQNToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showInnerToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showConstructorsToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showMethodsToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showFieldsToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });
        
        showEnumConstantsToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });
        
        showProtectedToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showPackageToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showPrivateToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        showStaticToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                applyFilter();
            }
        });

        
        // ESCAPE
        registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        JavaStructureOptions.setShowInherited(!JavaStructureOptions.isShowInherited());
                        applyFilter();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK, false),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private boolean showingSubDialog = false;

    public void addNotify() {
        super.addNotify();
        SwingUtilities.getRootPane(this).registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        Window window = SwingUtilities.getWindowAncestor(JavaStructurePanel.this);
                        if (window != null) {
                            window.setVisible(false);
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.getWindowAncestor(JavaStructurePanel.this).addWindowListener(
                new WindowAdapter() {
            public void windowDeactivated(WindowEvent windowEvent) {
                if (!showingSubDialog) {
                    Window window = SwingUtilities.getWindowAncestor(JavaStructurePanel.this);
                    if (window != null) {
                        window.setVisible(false);
                    }
                }
            }});
        applyFilter();
    }

    private void firstRow() {
        int rowCount = javaFileStructureTree.getRowCount();
        if (rowCount > 0) {
            javaFileStructureTree.setSelectionRow(0);
            scrollTreeToSelectedRow();
        }
    }

    private void previousRow() {
        int rowCount = javaFileStructureTree.getRowCount();
        if (rowCount > 0) {
            int selectedRow = javaFileStructureTree.getSelectionModel().getMinSelectionRow();
            if (selectedRow == -1) {
                selectedRow = (rowCount -1);
            } else {
                selectedRow--;
                if (selectedRow < 0) {
                    selectedRow = (rowCount -1);
                }
            }
            javaFileStructureTree.setSelectionRow(selectedRow);
            scrollTreeToSelectedRow();
        }
    }

    private void nextRow() {
        int rowCount = javaFileStructureTree.getRowCount();
        if (rowCount > 0) {
            int selectedRow = javaFileStructureTree.getSelectionModel().getMinSelectionRow();
            if (selectedRow == -1) {
                selectedRow = 0;
                javaFileStructureTree.setSelectionRow(selectedRow);
            } else {
                selectedRow++;
            }
            javaFileStructureTree.setSelectionRow(selectedRow % rowCount);
            scrollTreeToSelectedRow();
        }
    }

    private void lastRow() {
        int rowCount = javaFileStructureTree.getRowCount();
        if (rowCount > 0) {
            javaFileStructureTree.setSelectionRow(rowCount - 1);
            scrollTreeToSelectedRow();
        }
    }

    private void scrollTreeToSelectedRow() {
        final int selectedRow = javaFileStructureTree.getLeadSelectionRow();
        if (selectedRow >=0) {
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    javaFileStructureTree.scrollRectToVisible(javaFileStructureTree.getRowBounds(selectedRow));
                }
            });
        }
    }

    private void applyFilter() {
        // show wait cursor
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                JRootPane rootPane = SwingUtilities.getRootPane(JavaStructurePanel.this);
                if (rootPane != null) {
                    rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                }
            }
        });

        // apply filters and update the tree
        SwingUtilities.invokeLater(
            new Runnable() {
            public void run() {
                javaStructureModel.setPattern(filterTextField.getText());

                JavaStructureOptions.setCaseSensitive(caseSensitiveFilterCheckBox.isSelected());
                JavaStructureOptions.setShowFQN(showFQNToggleButton.isSelected());
                JavaStructureOptions.setShowInner(showInnerToggleButton.isSelected());
                JavaStructureOptions.setShowConstructors(showConstructorsToggleButton.isSelected());
                JavaStructureOptions.setShowMethods(showMethodsToggleButton.isSelected());
                JavaStructureOptions.setShowFields(showFieldsToggleButton.isSelected());
                JavaStructureOptions.setShowEnumConstants(showEnumConstantsToggleButton.isSelected());
                JavaStructureOptions.setShowProtected(showProtectedToggleButton.isSelected());
                JavaStructureOptions.setShowPackage(showPackageToggleButton.isSelected());
                JavaStructureOptions.setShowPrivate(showPrivateToggleButton.isSelected());
                JavaStructureOptions.setShowStatic(showStaticToggleButton.isSelected());

                javaStructureModel.update();

                // expand the tree
                for (int row = 0; row < javaFileStructureTree.getRowCount(); row++) {
                    TreePath treePath = javaFileStructureTree.getPathForRow(row);
                    javaFileStructureTree.expandRow(row);
                }

                // select first matching
                for (int row = 0; row < javaFileStructureTree.getRowCount(); row++) {
                    Object o = javaFileStructureTree.getPathForRow(row).getLastPathComponent();
                    if (o instanceof JavaToolsJavaElement) {
                        if (javaStructureModel.patternMatch((JavaToolsJavaElement)o)) {
                            javaFileStructureTree.setSelectionRow(row);
                            break;
                        }
                    }
                }

                JRootPane rootPane = SwingUtilities.getRootPane(JavaStructurePanel.this);
                if (rootPane != null) {
                    rootPane.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private void gotoElement(JavaToolsJavaElement javaFileStructureElement) {
        try {
            javaFileStructureElement.gotoElement();
        } finally {
            SwingUtilities.getWindowAncestor(JavaStructurePanel.this).setVisible(false);
        }
    }

    private void showSignature() {
        signatureEditorPane.setText("");
        signatureEditorPane.setToolTipText(null);
        TreePath treePath = javaFileStructureTree.getSelectionPath();
        if (treePath != null) {
            Object node = treePath.getLastPathComponent();
            if (node instanceof JavaToolsJavaElement) {
                signatureEditorPane.setText(((JavaToolsJavaElement)node).getTooltip());
                signatureEditorPane.setCaretPosition(0);
                signatureEditorPane.setToolTipText(((JavaToolsJavaElement)node).getTooltip());                
            }
        }
    }

    private void showJavaDoc() {
        TreePath treePath = javaFileStructureTree.getSelectionPath();
        if (treePath != null) {
            Object node = treePath.getLastPathComponent();
            if (node instanceof JavaToolsJavaElement) {
                String javaDoc = ((JavaToolsJavaElement) node).getJavaDoc();
                if (javaDoc != null) {
                    javaDoc = javaDoc
                            .replaceAll("@author ",     "<b>Author:</b> ")
                            .replaceAll("@deprecated ", "<b>Deprecated:</b> ")
                            .replaceAll("@exception ",  "<b>Exception:</b> ")
                            .replaceAll("@param ",      "<b>Parameter:</b> ")
                            .replaceAll("@return ",     "<b>Return:</b> ")
                            .replaceAll("@see ",        "<b>See:</b> ")
                            .replaceAll("@since ",      "<b>Since:</b> ")
                            .replaceAll("@throws ",     "<b>Throws:</b> ")
                            .replaceAll("@version ",    "<b>Version:</b> ")
                            ;
                    javaDocPane.setText(
                            "<html>" // NOI18N
                            + "<head>" // NOI18N
                            + "</head>" // NOI18N
                            + "<body>" // NOI18N
                            + javaDoc.replaceAll("\n", "<br>") // NOI18N
                            + "</body>" // NOI18N
                            + "</html>" // NOI18N
                            );
                } else {
                    javaDocPane.setText("");
                }
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    javaDocPane.scrollRectToVisible(new Rectangle(0,0,1,1));
                }});
        }

    }
    
    private void gotoClass(TypeElement javaClass) {
//        PositionBounds bounds = null;
//        if (javaClass.getResource() != null) {
//            bounds = JavaMetamodel.getManager().getElementPosition(javaClass);
//            if (bounds == null) {
//                ClassDefinition classDefinition = ((JMManager)JavaMetamodel.getManager()).getSourceElementIfExists(javaClass);
//                if (classDefinition != null) {
//                    javaClass = (JavaClass) classDefinition;
//                }
//            }
//        }
//        Resource resource = javaClass.getResource();
//        if (resource != null) {
//            JavaStructureModel = new JavaStructureModel(resource);
//        } else {
//            JavaStructureModel = new JavaStructureModel(javaClass);
//        }
//        javaFileStructureTree.setModel(JavaStructureModel);
//        applyFilter();
    }

    /** Find classes by name.
     * @param name begining of the name of the class.
     * @return list of the matching classes
     */
    public List findClass(String name) {
//            JavaModel.setClassPath(classPathContext);
//
//            ArrayList javaClasses = new ArrayList();
//            ClassPath cp = JavaMetamodel.getManager().getClassPath();
//            FileObject[] cpRoots = cp.getRoots();
//
//            for (int i = 0; i < cpRoots.length; i++) {
//                ClassIndex ci = ClassIndex.getIndex(JavaModel.getJavaExtent(cpRoots[i]));
//                if (ci == null) continue;
//                Collection col = null;
//                col = ci.getClassesBySimpleName(name);
//                if (col == null || col.size() == 0) {
//                    col = ci.getClassesByFqn(name);
//                    if (col == null || col.size() == 0) {
//                        continue;
//                    }
//                }
//                if (col != null && col.size() > 0) {
//                    javaClasses.addAll(col);
//                }
//            }
//            return javaClasses;
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        caseSensitiveFilterCheckBox = new javax.swing.JCheckBox();
        separator = new javax.swing.JSeparator();
        splitPane = new javax.swing.JSplitPane();
        javaFileStructureTreeScrollPane = new javax.swing.JScrollPane();
        javaFileStructureTree = new javax.swing.JTree();
        javaDocScrollPane = new javax.swing.JScrollPane();
        javaDocPane = new javax.swing.JEditorPane();
        separator1 = new javax.swing.JSeparator();
        filterToolbarPanel = new javax.swing.JPanel();
        showFQNToggleButton = new javax.swing.JToggleButton();
        showInnerToggleButton = new javax.swing.JToggleButton();
        showConstructorsToggleButton = new javax.swing.JToggleButton();
        showMethodsToggleButton = new javax.swing.JToggleButton();
        showFieldsToggleButton = new javax.swing.JToggleButton();
        showEnumConstantsToggleButton = new javax.swing.JToggleButton();
        showProtectedToggleButton = new javax.swing.JToggleButton();
        showPackageToggleButton = new javax.swing.JToggleButton();
        showPrivateToggleButton = new javax.swing.JToggleButton();
        showStaticToggleButton = new javax.swing.JToggleButton();
        helpLabel = new javax.swing.JLabel();
        signatureEditorPane = new javax.swing.JEditorPane();

        setBackground(new java.awt.Color(247, 247, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.GridBagLayout());

        filterPanel.setBackground(new java.awt.Color(247, 247, 255));
        filterPanel.setLayout(new java.awt.GridBagLayout());

        filterLabel.setDisplayedMnemonic('F');
        filterLabel.setLabelFor(filterTextField);
        filterLabel.setText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("LABEL_filterLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        filterPanel.add(filterLabel, gridBagConstraints);

        filterTextField.setBackground(new java.awt.Color(247, 247, 255));
        filterTextField.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_filterTextField")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        filterPanel.add(filterTextField, gridBagConstraints);

        caseSensitiveFilterCheckBox.setBackground(new java.awt.Color(247, 247, 255));
        caseSensitiveFilterCheckBox.setMnemonic('C');
        caseSensitiveFilterCheckBox.setText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("LABEL_caseSensitiveFilterCheckBox")); // NOI18N
        caseSensitiveFilterCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        caseSensitiveFilterCheckBox.setFocusable(false);
        caseSensitiveFilterCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        filterPanel.add(caseSensitiveFilterCheckBox, gridBagConstraints);
        caseSensitiveFilterCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("ACCESSIBLE_DESCRIPTION_caseSensitiveFilterCheckBox")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(filterPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(separator, gridBagConstraints);

        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);

        javaFileStructureTreeScrollPane.setBorder(null);

        javaFileStructureTree.setBackground(new java.awt.Color(247, 247, 255));
        javaFileStructureTreeScrollPane.setViewportView(javaFileStructureTree);

        splitPane.setLeftComponent(javaFileStructureTreeScrollPane);

        javaDocPane.setEditable(false);
        javaDocScrollPane.setViewportView(javaDocPane);

        splitPane.setRightComponent(javaDocScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(splitPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(separator1, gridBagConstraints);

        filterToolbarPanel.setBackground(new java.awt.Color(247, 247, 255));
        filterToolbarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        showFQNToggleButton.setIcon(JavaStructureIcons.FQN_ICON);
        showFQNToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showFQNToggleButton")); // NOI18N
        showFQNToggleButton.setFocusPainted(false);
        showFQNToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showFQNToggleButton);

        showInnerToggleButton.setIcon(JavaStructureIcons.INNER_CLASS_ICON);
        showInnerToggleButton.setSelected(true);
        showInnerToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showInnerToggleButton")); // NOI18N
        showInnerToggleButton.setFocusPainted(false);
        showInnerToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showInnerToggleButton);

        showConstructorsToggleButton.setIcon(JavaStructureIcons.CONSTRUCTOR_ICON);
        showConstructorsToggleButton.setSelected(true);
        showConstructorsToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showConstructorsToggleButton")); // NOI18N
        showConstructorsToggleButton.setFocusPainted(false);
        showConstructorsToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showConstructorsToggleButton);

        showMethodsToggleButton.setIcon(JavaStructureIcons.METHOD_ICON);
        showMethodsToggleButton.setSelected(true);
        showMethodsToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showMethodsToggleButton")); // NOI18N
        showMethodsToggleButton.setFocusPainted(false);
        showMethodsToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showMethodsToggleButton);

        showFieldsToggleButton.setIcon(JavaStructureIcons.FIELD_ICON);
        showFieldsToggleButton.setSelected(true);
        showFieldsToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showFieldsToggleButton")); // NOI18N
        showFieldsToggleButton.setFocusPainted(false);
        showFieldsToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showFieldsToggleButton);

        showEnumConstantsToggleButton.setIcon(JavaStructureIcons.ENUM_CONSTANTS_ICON);
        showEnumConstantsToggleButton.setSelected(true);
        showEnumConstantsToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showFieldsToggleButton")); // NOI18N
        showEnumConstantsToggleButton.setFocusPainted(false);
        showEnumConstantsToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showEnumConstantsToggleButton);

        showProtectedToggleButton.setIcon(JavaStructureIcons.PROTECTED_ICON);
        showProtectedToggleButton.setSelected(true);
        showProtectedToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showProtectedToggleButton")); // NOI18N
        showProtectedToggleButton.setFocusPainted(false);
        showProtectedToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showProtectedToggleButton);

        showPackageToggleButton.setIcon(JavaStructureIcons.PACKAGE_ICON);
        showPackageToggleButton.setSelected(true);
        showPackageToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showPackageToggleButton")); // NOI18N
        showPackageToggleButton.setFocusPainted(false);
        showPackageToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showPackageToggleButton);

        showPrivateToggleButton.setIcon(JavaStructureIcons.PRIVATE_ICON);
        showPrivateToggleButton.setSelected(true);
        showPrivateToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showPrivateToggleButton")); // NOI18N
        showPrivateToggleButton.setFocusPainted(false);
        showPrivateToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showPrivateToggleButton);

        showStaticToggleButton.setIcon(JavaStructureIcons.STATIC_ICON);
        showStaticToggleButton.setSelected(true);
        showStaticToggleButton.setToolTipText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("TOOLTIP_showStaticToggleButton")); // NOI18N
        showStaticToggleButton.setFocusPainted(false);
        showStaticToggleButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        filterToolbarPanel.add(showStaticToggleButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(filterToolbarPanel, gridBagConstraints);

        helpLabel.setText(org.openide.util.NbBundle.getBundle(JavaStructurePanel.class).getString("LABEL_helpLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(helpLabel, gridBagConstraints);

        signatureEditorPane.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        signatureEditorPane.setContentType("text/x-java");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(signatureEditorPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox caseSensitiveFilterCheckBox;
    public javax.swing.JLabel filterLabel;
    public javax.swing.JPanel filterPanel;
    public javax.swing.JTextField filterTextField;
    public javax.swing.JPanel filterToolbarPanel;
    public javax.swing.JLabel helpLabel;
    public javax.swing.JEditorPane javaDocPane;
    public javax.swing.JScrollPane javaDocScrollPane;
    public javax.swing.JTree javaFileStructureTree;
    public javax.swing.JScrollPane javaFileStructureTreeScrollPane;
    public javax.swing.JSeparator separator;
    public javax.swing.JSeparator separator1;
    public javax.swing.JToggleButton showConstructorsToggleButton;
    public javax.swing.JToggleButton showEnumConstantsToggleButton;
    public javax.swing.JToggleButton showFQNToggleButton;
    public javax.swing.JToggleButton showFieldsToggleButton;
    public javax.swing.JToggleButton showInnerToggleButton;
    public javax.swing.JToggleButton showMethodsToggleButton;
    public javax.swing.JToggleButton showPackageToggleButton;
    public javax.swing.JToggleButton showPrivateToggleButton;
    public javax.swing.JToggleButton showProtectedToggleButton;
    public javax.swing.JToggleButton showStaticToggleButton;
    public javax.swing.JEditorPane signatureEditorPane;
    public javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables

}
