/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2009.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.editor.fold.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.editor.CodeFoldingSideBar;
import org.netbeans.editor.SideBarFactory;
import org.netbeans.modules.editor.fold.spi.support.FoldInfo;
import org.netbeans.modules.editor.fold.spi.support.FoldInfoHolder;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public final class FoldMaintainerImpl implements FoldManager {
    
    private static final Logger LOG = Logger.getLogger(FoldMaintainerImpl.class.getName());
    
    private FoldOperation operation;
    
    public FoldMaintainerImpl() {}
    
    public void removeDamagedNotify(Fold damagedFold) {
        installedFolds.remove(getOperation().getExtraInfo(damagedFold));
    }
    
    public void init(FoldOperation operation) {
        if (LOG.isLoggable(Level.FINE))
            LOG.log(Level.FINE, "operation = " + operation); // NOI18N

        System.err.println("operation");
        
        this.operation = operation;
        installedFolds = new HashMap<FoldInfo, Fold>(); //Weak?
    }
    
    public void removeEmptyNotify(Fold emptyFold) {
        installedFolds.remove(getOperation().getExtraInfo(emptyFold));
    }
    
    public void release() {
        if (LOG.isLoggable(Level.FINE))
            LOG.log(Level.FINE, "getOperation()=" + getOperation()); // NOI18N
        
        FoldInfoHolder.getHolder(getDocument()).maintainer.set(null);
        
        installedFolds = null;
    }
    
    public void expandNotify(Fold expandedFold) {
        //ignore...
    }
    
    public void initFolds(FoldHierarchyTransaction transaction) {
        FoldInfoHolder.getHolder(getDocument()).maintainer.set(this);
        updateFolds();
    }
    
    public void insertUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //ignore...
    }
    
    public void removeUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //ignore...
    }
    
    public void changedUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //ignore...
    }
    
    private RequestProcessor.Task updatingTask = null;
    
    private static final RequestProcessor updating;
    
    static {
        updating = new RequestProcessor("Fold Updater"); // NOI18N
    }
    
    private synchronized void scheduleUpdate() {
        if (updatingTask != null) {
            updatingTask.cancel();
            updatingTask = null;
        }
        
        updatingTask = updating.post(new Runnable() {
            public void run() {
                updateFoldsImpl();
            }
        }, 500);
    }
    
    public void updateFolds() {
        scheduleUpdate();
    }
    
    private void updateFoldsImpl() {
        if (installedFolds == null)
            return ; //either the fold maintainer was not initialized yet, or it has already been released, so the fold updating should not be done.
        
        final Document doc = getDocument();
        
        final Collection<FoldInfo>   addedFolds = new ArrayList<FoldInfo>();
        final Collection<Fold> removedFolds = new HashSet<Fold>(installedFolds.values());
        
        for (FoldInfo fi : FoldInfoHolder.getHolder(getDocument()).getFolds()) {
            Fold      f = installedFolds.get(fi);
            
            if (f == null) {
                addedFolds.add(fi);
                continue ;
            }
            
            removedFolds.remove(f);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (doc != getDocument()) {
                    //If the document was changed, there is not point in trying to create the folds
                    //and it is in fact dangerous (BadLocationException, etc.):
                    return ;
                }
                
                if (!(doc instanceof AbstractDocument) || doc.getLength() == 0) {
                    return; // can happen (e.g. after component close)
                }
                
                AbstractDocument adoc = (AbstractDocument)doc;
                adoc.readLock();
                try {
                    FoldHierarchy hierarchy = getOperation().getHierarchy();
                    hierarchy.lock();
                    try {
                        FoldHierarchyTransaction t = getOperation().openTransaction();
                        try {
                            for (Iterator remove = removedFolds.iterator(); remove.hasNext(); ) {
                                Fold f = (Fold) remove.next();
                                
                                getOperation().removeFromHierarchy(f, t);
                                installedFolds.remove(getOperation().getExtraInfo(f));
                            }
                            
                            for (Iterator add = addedFolds.iterator(); add.hasNext(); ) {
                                FoldInfo info = (FoldInfo) add.next();
                                
                                install(t, info);
                            }
                        } catch (BadLocationException e) {
                            Exceptions.printStackTrace(e);
                        } finally {
                            t.commit();
                        }
                    } finally {
                        hierarchy.unlock();
                    }
                } finally {
                    adoc.readUnlock();
                }
            }
        });
    }
    
    private Map<FoldInfo, Fold> installedFolds;
    
    protected FoldOperation getOperation() {
        return operation;
    }
    
    protected Document getDocument() {
        return getOperation().getHierarchy().getComponent().getDocument();
    }

    public void install(FoldHierarchyTransaction t, FoldInfo info) throws BadLocationException {
        int startOffset = info.getStart().getOffset();
        int endOffset = info.getEnd().getOffset();
        int startDamage = /*XXX*/0;
        int endDamage = /*XXX*/0;

        if (endOffset >= getDocument().getLength()) {
            endOffset = getDocument().getLength() - 1;
        }
        if (endDamage >= getDocument().getLength()) {
            endDamage = getDocument().getLength() - 1;
        }
        if (startOffset < endOffset) {
            Fold f = getOperation().addToHierarchy(info.getType(), info.getDescription(), info.isCollapseByDefault(), startOffset, endOffset, startDamage, endDamage, info, t);

            installedFolds.put(info, f);
        }
        else {
            //do not create the fold.
            if (startOffset != endOffset && LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "startOffset=" + startOffset); // NOI18N
                LOG.log(Level.FINE, "endOffset=" + endOffset); // NOI18N
            }
        }
    }
        
    
    public static class SideBarFactoryImpl implements SideBarFactory {
        public JComponent createSideBar(JTextComponent jTextComponent) {
            return new CodeFoldingSideBar(jTextComponent);
        }
        
    }
    
    public static class MaintainerFactory implements FoldManagerFactory {
        public FoldManager createFoldManager() {
            return new FoldMaintainerImpl();
        }
        
    }
    
}
