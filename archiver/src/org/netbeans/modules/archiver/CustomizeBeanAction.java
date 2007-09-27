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

package org.netbeans.modules.archiver;

import java.awt.Component;
import java.beans.ExceptionListener;
import java.beans.IntrospectionException;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openide.windows.WindowManager;

public class CustomizeBeanAction extends NodeAction {
    
    private static final String JAVA_MIME_TYPE = "text/x-java";
    
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return false;
        }
        if (activatedNodes[0].getCookie(InstanceCookie.class) != null) {
            return true;
        }
        DataObject d = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        if (d == null) {
            return false;
        }
        FileObject f = d.getPrimaryFile();
        return f.getMIMEType().equals(JAVA_MIME_TYPE) &&
            ClassPath.getClassPath(f, ClassPath.SOURCE) != null &&
            ClassPath.getClassPath(f, ClassPath.EXECUTE) != null;
    }
    
    protected void performAction(Node[] nodes) {
        assert nodes.length == 1;
        DataObject dob = (DataObject) nodes[0].getCookie(DataObject.class);
        InstanceCookie ic = (InstanceCookie)nodes[0].getCookie(InstanceCookie.class);
        Object o;
        try {
            if (ic != null) {
                // Archive XML file, *.class, etc.
                o = ic.instanceCreate();
            } else {
                // *.java.
                assert dob != null;
                FileObject f = dob.getPrimaryFile();
                ClassPath sourceRoot = ClassPath.getClassPath(f, ClassPath.SOURCE);
                assert sourceRoot != null;
                ClassPath execute = ClassPath.getClassPath(f, ClassPath.EXECUTE);
                ClassLoader l = execute.getClassLoader(true);
                String name = sourceRoot.getResourceName(f, '.', false);
                assert name != null;
                Class c;
                try {
                    c = l.loadClass(name);
                } catch (ClassNotFoundException e) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Class " + name + " must be compiled first; could not be found in " + execute, NotifyDescriptor.ERROR_MESSAGE));
                    return;
                }
                o = c.newInstance();
            }
        } catch (Exception e) {
            // Various possible reasons.
            ErrorManager.getDefault().notify(ErrorManager.USER, e);
            return;
        }
        PropertySheet sheet = new PropertySheet();
        try {
            sheet.setNodes(new Node[] {new BeanNode(o)});
        } catch (IntrospectionException e) {
            ErrorManager.getDefault().notify(ErrorManager.USER, e);
            return;
        }
        JComponent pane;
        if (o instanceof Component) {
            // XXX better to handle Window specially too...
            JSplitPane _pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            _pane.setTopComponent(sheet);
            _pane.setBottomComponent((Component)o);
            pane = _pane;
        } else {
            pane = sheet;
        }
        DialogDescriptor d = new DialogDescriptor(pane, "Customize Archived Bean"); // XXX I18N
        JButton archiveButton = new JButton();
        Mnemonics.setLocalizedText(archiveButton, "&Archive As XML..."); // XXX I18N
        d.setOptions(new Object[] {archiveButton, NotifyDescriptor.CANCEL_OPTION});
        if (DialogDisplayer.getDefault().notify(d) == archiveButton) {
            File suggestedDir = null;
            if (dob != null) {
                File f = FileUtil.toFile(dob.getPrimaryFile());
                if (f != null) {
                    suggestedDir = f.getParentFile();
                }
            }
            serializeJavaBean(o, suggestedDir);
        }
    }
    
    private static void serializeJavaBean(Object bean, File suggestedDir) {
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, suggestedDir);
        chooser.setDialogTitle("Archive Bean to XML");
        chooser.setFileFilter(new XMLFilter());
        if (chooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            FileObject f = store(bean, chooser.getSelectedFile());
            if (f != null) {
                try {
                    DataObject d = DataObject.find(f);
                    OpenCookie open = (OpenCookie) d.getCookie(OpenCookie.class);
                    if (open != null) {
                        open.open();
                    }
                } catch (DataObjectNotFoundException e) {
                    assert false : e;
                }
            }
        }
    }
    
    private static final class XMLFilter extends FileFilter {
        public XMLFilter() {}
        public boolean accept(File f) {
            return f.getName().toLowerCase(Locale.US).endsWith(".xml");
        }
        public String getDescription() {
            return "XML Files";
        }
    }
    
    private static final class OutputWindowExceptionListener implements ExceptionListener {
        private PrintWriter err;
        private OutputWriter out;
        public void exceptionThrown(Exception e) {
            if (err == null) {
                InputOutput io = IOProvider.getDefault().getIO("Archiver", false); // XXX I18N
                io.setFocusTaken(true);
                OutputWriter _err = io.getErr();
                try {
                    // XXX should call reset even if nothing appears (if there is an old tab)
                    _err.reset();
                    (out = io.getOut()).reset();
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                }
                // XXX #32747: deadlock calling printStackTrace(OutputWriter)
                err = new PrintWriter(_err) {
                    {
                        lock = new Object();
                    }
                };
            }
            e.printStackTrace(err);
        }
        public void close() {
            if (err != null) {
                err.close();
                out.close();
            }
        }
    }
    
    private static FileObject store(final Object bean, File target) {
        final FileObject parent = FileUtil.toFileObject(target.getParentFile());
        final String name = target.getName();
        final FileObject[] toret = new FileObject[1];
        try {
            parent.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    FileObject serFile = parent.getFileObject(name);
                    if (serFile == null) {
                        serFile = parent.createData(name);
                    }
                    FileLock lock = serFile.lock();
                    try {
                        ClassLoader origL = Thread.currentThread().getContextClassLoader();
                        ClassLoader nue = bean.getClass().getClassLoader();
                        if (nue == null) {
                            nue = ClassLoader.getSystemClassLoader();
                        }
                        Thread.currentThread().setContextClassLoader(nue);
                        OutputStream os = serFile.getOutputStream(lock);
                        OutputWindowExceptionListener listener = new OutputWindowExceptionListener();
                        try {
                            XMLEncoder e = new XMLEncoder(os);
                            e.setExceptionListener(listener);
                            e.writeObject(bean);
                            e.close();
                            toret[0] = serFile;
                        } finally {
                            Thread.currentThread().setContextClassLoader(origL);
                            os.close();
                            listener.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            });
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return toret[0];
    }
    
    public String getName() {
        return NbBundle.getMessage(CustomizeBeanAction.class, "LBL_Action");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    
}
