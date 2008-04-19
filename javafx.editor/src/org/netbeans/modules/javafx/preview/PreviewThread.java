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

package org.netbeans.modules.javafx.preview;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.InputStream;
import java.security.CodeSource;
import java.security.PermissionCollection;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.javafx.editor.*;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;

//import sun.awt.AppContext;
//import sun.awt.SunToolkit;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.execution.NbClassPath;
import org.openide.util.Exceptions;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.util.RequestProcessor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.Utilities;
import sun.awt.image.URLImageSource;
        
public class PreviewThread extends Thread {
    
    static final String nothingToShow = "Nothing to show..."; //NOI18
    
    private class Hyperlink implements HyperlinkListener {
        private Vector<Object> foMap = new Vector<Object>();
        private Vector<Long> offsetMap = new Vector<Long>();
        
        void setMaps(Vector<Object> foMap, Vector<Long> offsetMap) {
            this.foMap = foMap;
            this.offsetMap = offsetMap;
        }
    
        private void goTo(Document doc, int offset) {
            LineCookie lc = (LineCookie) NbEditorUtilities.getDataObject(doc).getCookie(LineCookie.class);
            int line = NbDocument.findLineNumber((StyledDocument) doc,offset);
            int lineOffset = NbDocument.findLineOffset((StyledDocument) doc,line);
            int column = offset - lineOffset;

            if (line != -1) {
                Line l = lc.getLineSet().getCurrent(line);

                if (l != null) {
                    l.show(Line.SHOW_TOFRONT, column);
                }
            }
        }
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                return;
            String href = e.getDescription();
            Object fo = foMap.elementAt(Integer.parseInt(href));
            Document doc = null;
            if (fo instanceof MemoryFileObject) {
                doc = ((MemoryFileObject)fo).getDocument();
            } else {
                try {
                    DataObject od = DataObject.find((FileObject)fo);
                    EditorCookie ec = (EditorCookie) od.getCookie(EditorCookie.class);
                    doc = ec.openDocument();                
                } catch (Exception ex) {
                    return;
                }
            }
            int offset = offsetMap.elementAt(Integer.parseInt(href)).intValue();
            goTo(doc, offset);
        }
    };
    
        
    private FXDocument doc;
    private JComponent comp = null;

    class EE extends ExecutionEngine {
        
        public EE() {}

        protected NbClassPath createLibraryPath() {
            return new NbClassPath(new String[0]);
        }

        protected PermissionCollection createPermissions(CodeSource cs, InputOutput io) {
            PermissionCollection allPerms = new Permissions();
            //allPerms.add(new AllPermission());
            //allPerms.setReadOnly();
            return allPerms;
        }

        public ExecutorTask execute(String name, Runnable run, InputOutput io) {
            return new ET(run, name, io);
        }
        
        private class ET extends ExecutorTask {
            private RequestProcessor.Task task;
            private int resultValue;
            private final String name;
            private InputOutput io;
            
            public ET(Runnable run, String name, InputOutput io) {
                super(run);
                this.name = name;
                task = RequestProcessor.getDefault().post(this);
            }
            
            public void stop() {
                task.cancel();
            }
            
            public int result() {
                waitFinished();
                return resultValue;
            }
            
            public InputOutput getInputOutput() {
                return io;
            }
            
            public void run() {
                try {
                    super.run();
                } catch (RuntimeException x) {
                    x.printStackTrace();
                    resultValue = 1;
                }
            }
        }      
    }
    
    class R implements Runnable {
                
        public void run() {
            Object obj = null;
            try {
                obj = CodeManager.execute(doc);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            if (obj != null) {
                comp = CodeManager.parseObj(obj);
                if (comp == null) {
                    comp = JavaFXDocument.getNothingPane();
                }
            }
            else {
                /*List <Diagnostic> diagnostics = CodeManager.getDiagnostics();
                if (!diagnostics.isEmpty()) {
                    comp = new JPanel();
                    comp.setLayout(new GridBagLayout());
                    int i = 0;
                    GridBagConstraints gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                    gridBagConstraints.gridx = 0;
                    gridBagConstraints.weighty = 0;
                    for (Diagnostic diagnostic : diagnostics) {
                        JTextArea jta = new JTextArea(diagnostic.toString());
                        jta.setLineWrap(true);
                        jta.setEditable(false);
                        gridBagConstraints.gridy = i++;
                        gridBagConstraints.weightx = 1.0;
                        comp.add(jta, gridBagConstraints);
                        gridBagConstraints.gridy = i++;
                        comp.add(new JSeparator(), gridBagConstraints);
                    }
                    JTextArea jta = new JTextArea();
                    jta.setEditable(false);
                    gridBagConstraints.gridy = i;
                    gridBagConstraints.weighty = 1.0;
                    gridBagConstraints.fill = GridBagConstraints.BOTH;
                    comp.add(jta, gridBagConstraints);*/
                List <Diagnostic> diagnostics = CodeManager.getDiagnostics();
                if (!diagnostics.isEmpty()) {
                    JEditorPane pane = new JEditorPane();
                    pane.setEditable(false);
                    pane.setEditorKit(new HTMLEditorKit());
                    Hyperlink hl = new Hyperlink();
                    pane.addHyperlinkListener(hl);
                    //pane.setFont(new FontUIResource("Monospaced", FontUIResource.PLAIN, 20));
                    String text = "";
                    int i = 0;
                    Vector<Object> foMap = new Vector<Object>();
                    Vector<Long> offsetMap = new Vector<Long>();
                    for (Diagnostic diagnostic : diagnostics) {
                        Object source = diagnostic.getSource();
                        String name = "";
                        if (diagnostic.getSource() instanceof MemoryFileObject) {
                            MemoryFileObject mfo = (MemoryFileObject)source;
                            name = mfo.getFilePath();
                        } else {
                            JavaFileObject jFO = (JavaFileObject) source;
                            File file = new File(jFO.toUri());
                            FileObject regularFO = FileUtil.toFileObject(file);
                            name = regularFO.getPath();
                            source = regularFO;
                        }
                        foMap.add(source);
                        offsetMap.add(diagnostic.getPosition());
                        text+= "<a href=" + i + ">" + name + " : " + diagnostic.getLineNumber() + "</a>\n" + " " + "<font color=#a40000>" + diagnostic.getMessage(null) + "</font>" + "<br>";
                        i++;
                    }
                    pane.setText(text);
                    hl.setMaps(foMap, offsetMap);
                    comp = pane;
                }
                else {
                    if (comp == null) {
                        comp = JavaFXDocument.getNothingPane();
                    }
                }
            }
        }
        private JComponent getNothig() {
            JTextArea jta = new JTextArea();
            jta.append(nothingToShow);
            return jta;
        }
    }

    public PreviewThread(FXDocument doc) {
        super(new ThreadGroup("SACG"), "SACT");
        this.doc = doc;
    }

    private ExecutionEngine ee = new EE();
    private ExecutorTask task = null;
    
    synchronized public void stopTask() {
        if (task != null) {
            task.stop();
        }
    }
    
    synchronized public void joinTask() {
        if (task != null) {
            task.result();
        }
    }
            
    @Override
    synchronized public void run() {
        try {
            //SunToolkit.createNewAppContext();
            //System.out.println("Current app context " + AppContext.getAppContext());
            
            ((JavaFXDocument)doc).setCompile();
            task = ee.execute("prim", new R(), IOProvider.getDefault().getIO("JavaFX preview", false));
  
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    ((JavaFXDocument)doc).renderPreview(comp);
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
}