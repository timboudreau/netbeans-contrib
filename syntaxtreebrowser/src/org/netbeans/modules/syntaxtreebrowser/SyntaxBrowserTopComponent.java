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

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
import syntaxtreenavigator.SyntaxTreePanel;
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
