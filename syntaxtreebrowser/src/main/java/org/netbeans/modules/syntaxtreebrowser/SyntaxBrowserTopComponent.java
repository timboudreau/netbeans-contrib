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
package org.netbeans.modules.syntaxtreebrowser;

import com.sun.source.tree.CompilationUnitTree;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.netbeans.api.java.source.JavaSource;
import syntaxtreebrowser.SyntaxTreePanel;
/**
 *
 * @author Tim Boudreau
 */
public class SyntaxBrowserTopComponent extends TopComponent {
    SyntaxTreePanel pnl = new SyntaxTreePanel();
    private DataObject dob;
    public SyntaxBrowserTopComponent(DataObject dob) {
        setLayout (new BorderLayout());
        add (pnl, BorderLayout.CENTER);
        setDisplayName (dob.getName());
        this.dob = dob;
        associateLookup (dob.getNodeDelegate().getLookup());
    }
    
    DataObject getDataObject() {
        return dob;
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected void componentOpened() {
        super.componentOpened();
        if (!initialized) {
            RequestProcessor.getDefault().post (new R(dob));
        }
    }
    
    private boolean initialized;
    
    private class R implements Runnable, CancellableTask <CompilationController> {
        Iterator <CompilationUnitTree> iterator;
        private DataObject ob;
        R (DataObject ob) {
            this.ob = ob;
        }
        
        public void run () {
            if (!EventQueue.isDispatchThread()) {
                try {
                    FileObject fob = ob.getPrimaryFile();
                    JavaSource src = JavaSource.forFileObject(fob);
                    if (src == null) {
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                JLabel lbl = new JLabel("Could not load " + ob.getName());
                                remove (pnl);
                                add (lbl, BorderLayout.CENTER);
                                initialized = true;
                            }
                        });
                        return;
                    }
                    src.runWhenScanFinished(this, true);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                if (this.tree != null) {
                    List <CompilationUnitTree> l = new ArrayList<CompilationUnitTree>(1);
                    l.add (this.tree);
                    pnl.init (l.iterator());
                }
            }
        }
        
        private CompilationUnitTree tree;
        public void run(CompilationController cc) throws Exception {
            if (cancelled) return;
            CompilationUnitTree tree = cc.getCompilationUnit();
            this.tree = tree;
            EventQueue.invokeLater (this);
        }

        volatile boolean cancelled;
        public void cancel() {
            cancelled = true;
        }
    }
}
