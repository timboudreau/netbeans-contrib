/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.archiver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.*;
import java.io.*;
import javax.swing.*;
import org.openide.*;
import org.openide.awt.Mnemonics;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;
import org.openide.util.actions.CookieAction;
import org.openide.windows.*;

public class CustomizeBeanAction extends CookieAction {
    
    protected Class[] cookieClasses() {
        return new Class[] {InstanceCookie.class};
    }
    
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }
    
    protected void performAction(Node[] nodes) {
        InstanceCookie ic = (InstanceCookie)nodes[0].getCookie(InstanceCookie.class);
        Object o;
        Node n;
        try {
            o = ic.instanceCreate();
            n = new BeanNode(o);
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.USER, e);
            return;
        }
        PropertySheet sheet = new PropertySheet();
        sheet.setNodes(new Node[] {n});
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
        Mnemonics.setLocalizedText(archiveButton, "&Archive As..."); // XXX I18N
        d.setOptions(new Object[] {archiveButton, NotifyDescriptor.CANCEL_OPTION});
        if (DialogDisplayer.getDefault().notify(d) == archiveButton) {
            serializeJavaBean(o);
        }
    }
    
    private static void serializeJavaBean(Object bean) {
        FileObject parent;
        String name;
        JPanel p = new JPanel(new BorderLayout(12, 0));
        JTextField tf = new JTextField(20);
        JLabel l = new JLabel();
        Mnemonics.setLocalizedText(l, "&Target:"); // XXX I18N
        l.setLabelFor(tf);
        p.add(tf, BorderLayout.CENTER);
        p.add(l, BorderLayout.WEST);
        try {
            // selects one folder from data systems
            DataFolder df = (DataFolder)NodeOperation.getDefault().select(
                "Archive As...", // XXX I18N
                "Save in:", // XXX I18N
                RepositoryNodeFactory.getDefault().repository(new FolderFilter()),
                new FolderAcceptor(),
                p
            )[0].getCookie(DataFolder.class);
            parent = df.getPrimaryFile();
            name = tf.getText();
        } catch (UserCancelException ex) {
            return;
        }
        store(bean, parent, name);
    }
    
    private static final class OutputWindowExceptionListener implements ExceptionListener {
        private PrintWriter err;
        public void exceptionThrown(Exception e) {
            if (err == null) {
                InputOutput io = IOProvider.getDefault().getIO("Archiver", false); // XXX I18N
                io.setFocusTaken(true);
                OutputWriter _err = io.getErr();
                try {
                    _err.reset();
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
    }
    
    private static void store(final Object bean, final FileObject parent, final String name) {
        try {
            parent.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    FileObject serFile = parent.getFileObject(name, "xml"); // NOI18N
                    if (serFile == null) {
                        serFile = parent.createData(name, "xml"); // NOI18N
                    }
                    FileLock lock = serFile.lock();
                    try {
                        ClassLoader origL = Thread.currentThread().getContextClassLoader();
                        Thread.currentThread().setContextClassLoader(bean.getClass().getClassLoader());
                        OutputStream os = serFile.getOutputStream(lock);
                        try {
                            XMLEncoder e = new XMLEncoder(os);
                            e.setExceptionListener(new OutputWindowExceptionListener());
                            e.writeObject(bean);
                            e.close();
                        } finally {
                            Thread.currentThread().setContextClassLoader(origL);
                            os.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            });
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private static final class FolderFilter implements DataFilter {
        FolderFilter() {}
        public boolean acceptDataObject(DataObject obj) {
            return obj instanceof DataFolder &&
                (obj.getPrimaryFile().canWrite() ||
                 obj.getPrimaryFile().getParent() != null);
        }
    }
    
    private static final class FolderAcceptor implements NodeAcceptor {
        FolderAcceptor() {}
        public boolean acceptNodes(Node[] nodes) {
            if (nodes == null || nodes.length == 0) {
                return false;
            }
            DataFolder cookie = (DataFolder)nodes[0].getCookie(DataFolder.class);
            return nodes.length == 1 &&
                cookie != null &&
                cookie.getPrimaryFile().canWrite();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(CustomizeBeanAction.class, "LBL_Action");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/archiver/beans.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
}
