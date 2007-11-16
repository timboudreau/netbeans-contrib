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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.editor.hints.i18n;

import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.highlights.spi.DefaultHighlight;
import org.netbeans.modules.editor.highlights.spi.Highlighter;
import org.netbeans.modules.i18n.ResourceHolder;
import org.netbeans.modules.i18n.java.JavaI18nSupport;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.MapFormat;

/**
 * @author Jan Lahoda
 */
class AddToBundleFix implements Fix {
    
    private DataObject bundle;
    private DataObject od;
    
    private JavaI18nSupport support;
    
    private TreePathHandle handle;
    private String format;
    private List<String> argument;
    private Document doc;
    
    
    public AddToBundleFix(DataObject bundle, DataObject od, TreePathHandle handle, JavaI18nSupport support, String format, List<String> argument) {
        super();
        this.bundle = bundle;
        this.od = od;
        this.support = support;
        this.handle = handle;
        this.format = format;
        this.argument = argument;
    }
    
    public String getText() {
        return (bundle == null ? "Create new bundle and r" : "R") + "eplace with localized string";
    }
    
    private Document getDocument(JTextComponent comp) throws IOException {
        if (comp != null) {
            return comp.getDocument();
        }
        
        //only for tests:
        EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
        
        return ec.openDocument();
    }
    
    public ChangeInfo implement() throws IOException {
        final JTextComponent comp = Registry.getMostActiveComponent();
        
        if (!TESTS && !od.equals(comp.getDocument().getProperty(Document.StreamDescriptionProperty)))
            return null;
        
        //first make sure the bundle to exists (or can be created):
        if (bundle == null && !TESTS) {
            try {
                FileObject bundleFO = od.getPrimaryFile().getParent().createData("Bundle.properties");
                assert bundleFO != null;
                bundle = DataObject.find(bundleFO);
            }  catch (IOException ex) {
                Logger.global.log(Level.INFO, ex.getLocalizedMessage(), ex);
            }
        }
        
        JavaSource js = JavaSource.forFileObject(od.getPrimaryFile());
        String bundleNameDots;
        String bundleNameSlashes;
        
        if (bundle == null) {
            if (TESTS) {
                bundleNameSlashes = "Bundle.properties";
                bundleNameDots = "Bundle.properties";
            } else {
                return null;
            }
        } else {
            bundleNameSlashes = js.getClasspathInfo().getClassPath(PathKind.SOURCE).getResourceName( bundle.getPrimaryFile(), '/', false );
            bundleNameDots = js.getClasspathInfo().getClassPath(PathKind.SOURCE).getResourceName( bundle.getPrimaryFile(), '.', false );
        }
        
        String format = findFormat(od.getPrimaryFile());
        
        Map<String, String> table = new HashMap<String, String>();
        
        String key = this.format.replace('{', '_').replace('}', '_');
        
        I18NChecker.LOG.log(Level.FINE, "key = {0}", key); // NOI18N
        
        table.put("key", key); // NOI18N
        table.put("bundleNameSlashes", bundleNameSlashes); // NOI18N
        table.put("bundleNameDots", bundleNameDots); // NOI18N
        table.put("sourceFileName", js.getClasspathInfo().getClassPath(PathKind.SOURCE).getResourceName( od.getPrimaryFile(), '.', false )); // NOI18N
        
        StringBuffer arguments = new StringBuffer();
        
        arguments.append("new Object[] {"); // NOI18N
        
        for (String arg : argument) {
            arguments.append(arg);
            arguments.append(", "); // NOI18N
        }
        
        arguments.append("}"); // NOI18N
        
        table.put("arguments", arguments.toString()); // NOI18N

        I18NChecker.LOG.log(Level.FINE, "table = {0}", table); // NOI18N
        
        MapFormat formatEngine = new MapFormat(table);
        
        final String text = formatEngine.format(format);
        
        doc = getDocument(comp);
        final Position[] toSearch= new Position[2];
        try {
            
            js.runModificationTask(new CancellableTask<WorkingCopy>() {
                public void cancel() {
                }
                public void run(WorkingCopy cont) throws Exception {
                    cont.toPhase(Phase.PARSED);
                    TreePath path = handle.resolve(cont);
                    Scope context = cont.getTrees().getScope(path);
                    SourcePositions[] pos = new SourcePositions[1];
                    
                    Tree t = cont.getTreeUtilities().parseExpression(text, pos);
                    
                    cont.getTreeUtilities().attributeTree(t, context);
                    
//                    t = GeneratorUtilities.get(cont).importFQNs(t);
                    new ImportFQNsHack(cont).scan(new TreePath(path.getParentPath(), t), null);
                    
                    cont.rewrite(path.getLeaf(), t);
                    
                    toSearch[0] = NbDocument.createPosition(doc, (int) cont.getTrees().getSourcePositions().getStartPosition(cont.getCompilationUnit(), path.getParentPath().getLeaf()), Bias.Forward);
                    toSearch[1] = NbDocument.createPosition(doc, (int) cont.getTrees().getSourcePositions().getEndPosition(cont.getCompilationUnit(), path.getParentPath().getLeaf()), Bias.Backward);
                }
            }).commit();
            
            String textToSearch = doc.getText(toSearch[0].getOffset(), toSearch[1].getOffset() - toSearch[0].getOffset());
            
            int keyStart = textToSearch.indexOf('"' + key + '"');
            
            if (keyStart == (-1)) {
                I18NChecker.LOG.log(Level.SEVERE, "Cannot find the key in the generated source code. Key: {0}, textToSearch: {1}.", new Object[] {key, textToSearch});
                finish();
                return null;
            }
            
            if (!TESTS) {
                new TopComponentListener(comp, start = NbDocument.createPosition(doc, toSearch[0].getOffset() + keyStart + 1, Bias.Backward), end = NbDocument.createPosition(doc, toSearch[0].getOffset() + keyStart + key.length() + 1, Bias.Forward));
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        
        return null;
    }
    
    private String findFormat(FileObject file) {
        ClassPath cp = ClassPath.getClassPath(file, ClassPath.COMPILE);
        //XXX: will not work for openide/util itself:
        if (cp.findResource("org/openide/util/NbBundle.class") != null) {
            if (argument.isEmpty()) {
                return "org.openide.util.NbBundle.getMessage({sourceFileName}.class, \"{key}\")"; // NOI18N
            } else {
                return "org.openide.util.NbBundle.getMessage({sourceFileName}.class, \"{key}\", {arguments})"; // NOI18N
            }
        } else {
            if (argument.isEmpty()) {
                return "java.util.ResourceBundle.getBundle(\"{bundleNameSlashes}\").getString(\"{key}\")"; // NOI18N
            } else {
                return "java.text.MessageFormat.format(java.util.ResourceBundle.getBundle(\"{bundleNameSlashes}\").getString(\"{key}\"), {arguments})"; // NOI18N
            }
        }
    }
    
    private Position start;
    private Position end;
    
    private void finish() {
        final StringBuffer sb = new StringBuffer();
        doc.render(new Runnable() {
            public void run() {
                try {
                    sb.append(doc.getText(start.getOffset(), end.getOffset() - start.getOffset()));
                } catch (BadLocationException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        });
        
        ResourceHolder holder = support.getResourceHolder();
        
        holder.setResource(bundle);
        holder.addProperty(sb.toString(), format, "");
        
        SaveCookie sc = bundle.getCookie(SaveCookie.class);
        if (sc != null) {
            try {
                sc.save();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
    }
    
    private static final Coloring COLORING = new Coloring(null, null, new Color(138, 191, 236));
    
    private class TopComponentListener implements KeyListener, CaretListener {
        
        private Document doc;
        private JTextComponent target;
        private Position start;
        private Position end;
        
        private TopComponentListener(JTextComponent target, Position start, Position end) {
            this.target = target;
            this.doc = target.getDocument();
            this.start = start;
            this.end = end;
            
            target.addKeyListener(this);
            target.addCaretListener(this);
            
            target.putClientProperty(TopComponentListener.class, this);
            
            Highlighter.getDefault().setHighlights(getFileObject(), "i18nrt", Collections.singletonList(new DefaultHighlight(COLORING, start, end))); // NOI18N
            
            target.setSelectionStart(start.getOffset());
            target.setSelectionEnd(end.getOffset());
        }
        
        private FileObject getFileObject() {
            DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            
            if (od == null)
                return null;
            
            return od.getPrimaryFile();
        }
        
        public void caretUpdate(CaretEvent e) {
            int caret = e.getDot();
            
            if (caret < start.getOffset() || end.getOffset() < caret) {
                release();
            }
        }
        
        public void keyTyped(KeyEvent e) {
        }
        
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiers() == 0) {
                release();
                e.consume();
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == 0) {
                release();
                e.consume();
            }
        }
        
        public void keyReleased(KeyEvent e) {
        }
        
        private void release() {
            target.putClientProperty(TopComponentListener.class, null);
            target.removeKeyListener(this);
            target.removeCaretListener(this);
            Highlighter.getDefault().setHighlights(getFileObject(), "i18nrt", Collections.emptyList()); // NOI18N
            
            doc = null;
            target = null;
            
            finish();
        }
        
    }
    
    private static final class ImportFQNsHack extends TreePathScanner<Void, Void> {
        
        private WorkingCopy wc;

        public ImportFQNsHack(WorkingCopy wc) {
            this.wc = wc;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree node, Void p) {
            Element e = wc.getTrees().getElement(getCurrentPath());
            
            if (e != null && (e.getKind().isClass() || e.getKind().isInterface())) {
                wc.rewrite(node, wc.getTreeMaker().QualIdent(e));
                return null;
            } else {
                return super.visitMemberSelect(node, p);
            }
        }
        
    }
    
    static boolean TESTS;
    
}
