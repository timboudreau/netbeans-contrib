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

package org.netbeans.modules.rmi.registry;

import java.io.*;
import java.awt.Dialog;
import java.awt.event.*;
import java.net.*;
import java.rmi.server.RMIClassLoader;
import java.text.MessageFormat;
import java.util.*;

import org.openide.*;
import org.openide.awt.StatusDisplayer;


import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import org.openide.nodes.Node;
import org.openide.nodes.NodeOperation;
import org.openide.util.*;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author Martin Ryzl
 */
public class SaveInterfaceAction extends CookieAction {

    /** Serial version UID. */
    static final long serialVersionUID = 9058662081474395978L;

    static final String EXT_CLASS = "class"; // NOI18
    
    /** Get the cookies that this action requires.
    * @return a list of cookies
    */
    protected Class[] cookieClasses() {
        return new Class[] { InterfaceNode.class };
    }

    /** Get the mode of the action, i.e. how strict it should be about cookie support.
    * @return the mode of the action. Possible values are disjunctions of the MODE_XXX constants.
    */
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    /** Action.
    */
    protected void performAction(final Node[] nodes) {
        if (nodes.length > 0) {
            InterfaceNode in = (InterfaceNode) nodes[0].getCookie(InterfaceNode.class);
            if (in != null) {
                final Class cl = in.getInterface();
                if (cl != null) {
                    // select filesystem to save interface in
                    final FileObject fs = selectFileSystem();
                    try {
                        if (fs != null) {
                            fs.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {
                                public void run() {
                                    try {
                                        DepTool dt = new DepTool(cl, false);
                                        Iterator it = dt.getClassesSet().iterator();
                                        while (it.hasNext()) {
                                            Class clazz = (Class) it.next();
                                            saveClass(clazz, fs);
                                        }
                                        String msg = NbBundle.getMessage(SaveInterfaceAction.class, "MSG_InterfaceSuccessfullySaved"); // NOI18
                                        StatusDisplayer.getDefault().setStatusText(msg);
                                    } catch (UserCancelException ex) {
                                        return;
                                    } catch (Exception ex) {
                                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                                        String msg = NbBundle.getMessage(SaveInterfaceAction.class, "MSG_InterfaceSaveError"); // NOI18
                                        StatusDisplayer.getDefault().setStatusText(msg);
                                    }
                                }
                            });
                        }
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                        String msg = NbBundle.getMessage(SaveInterfaceAction.class, "MSG_InterfaceSaveError"); // NOI18
                        StatusDisplayer.getDefault().setStatusText(msg);
                    }
                }
            } else {
                // [TODO] Notification of failure.
            }
        }
    }

    private void saveClass(Class cl, FileObject fs) throws IOException {
        // class and package names
        String classname = cl.getName();
        int index = classname.lastIndexOf('.');
        String classfile, classpackage;
        if (index != -1) {
            classfile = classname.substring(index + 1);
            classpackage = classname.substring(0, index);
        } else {
            classfile = classname;
            classpackage = null;
        }

        // lock for writing
        FileLock lock = null;

        // streams
        InputStream is = null;
        OutputStream os = null;

        try {
            // load class as a resource
            is = cl.getResourceAsStream(classfile + '.' + EXT_CLASS); // NOI18N

            if (is != null) {
                FileObject fp = getPackage(fs, classpackage);
                FileObject fo = fp.getFileObject(classfile, EXT_CLASS);
                if (fo != null) {
                    NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(SaveInterfaceAction.class, "FMT_OverwriteClass", fo.getPackageNameExt('/', '.')), // NOI18N
                        NotifyDescriptor.YES_NO_CANCEL_OPTION
                    );
                    Object result = DialogDisplayer.getDefault().notify(nd);
                    if (result.equals(nd.CANCEL_OPTION)) throw new UserCancelException();
                    if (result.equals(nd.NO_OPTION)) return;
                } else {
                    fo = fp.createData(classfile, EXT_CLASS); // NOI18N
                }
                lock = fo.lock();
                os = fo.getOutputStream(lock);

                // load bytecode
                final byte[] buffer = new byte[4096];
                int i, n = 0;

                while ((i = is.read(buffer)) > -1) {
                    n += i;
                    os.write(buffer, 0, i);
                }
            }
        } finally {
            // release lock
            if (lock != null) lock.releaseLock();

            // close streams
            try { 
                if (is != null) is.close(); 
            } catch (IOException ex2) {}
            
            try { 
                if (os != null) os.close(); 
            } catch (IOException ex2) {}
        }
    }

    /** Get (create) package.
     * @param fs filesystem (package root)
     * @param pkg package name
     * @return file object representing the package
     */
    private static FileObject getPackage(FileObject fs, String pkg) throws IOException {
        if (pkg == null) return fs;

        StringTokenizer st = new StringTokenizer(pkg, "."); // NOI18N
        String token;
        FileObject fo = fs;

        while (st.hasMoreElements()) {
            token = st.nextToken();
            // must be a folder
            if (fs.isFolder()) {
                fo = fs.getFileObject(token);
                if (fo == null) {
                    // create folder
                    fo = fs.createFolder(token);
                }
            } else {
                break;
            }
            fs = fo;
        }

        if (!fs.isFolder()) {
            throw new IOException(NbBundle.getMessage(SaveInterfaceAction.class, "ERR_PackageCreation")); // NOI18N
        }

        return fo;
    } // getPackage


    /** Lets the user to select a file system where the interface will be saved.
     * @return FileObject for the filesystem.
     */
    private static FileObject selectFileSystem() {
        return null;
    } // select filesystem

    /** Get a human presentable name of the action. This may be presented as an item in a menu.
     * @return the name of the action
     */
    public String getName() {
        return NbBundle.getMessage(SaveInterfaceAction.class, "PROP_SaveInterfaceActionName");  // NOI18N
    }

    /** Get a help context for the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(SaveInterfaceAction.class);
    }
}















