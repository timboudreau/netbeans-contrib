/*
 * The contents of this file are subject to the terms of the Common Development
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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.fold;

import org.netbeans.modules.latex.editor.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.editor.CodeFoldingSideBar;
import org.netbeans.editor.SideBarFactory;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public final class FoldMaintainerImpl implements FoldManager {
    
    private static final Logger LOG = Logger.getLogger(FoldMaintainerImpl.class.getName());
    
    private FoldOperation operation;
    /** Creates a new instance of AbstractFoldMaintainer */
    public FoldMaintainerImpl() {
    }
    
    /**
     * Notify that the fold was removed from hierarchy automatically
     * by fold hierarchy infrastructure processing
     * because it was damaged by a document modification.
     */
    public void removeDamagedNotify(Fold damagedFold) {
        position2Fold.remove(getOperation().getExtraInfo(damagedFold));
    }
    
    /**
     * Initialize this manager.
     *
     * @param operation fold hierarchy operation dedicated to the fold manager.
     */
    public void init(FoldOperation operation) {
        if (LOG.isLoggable(Level.FINE))
            LOG.log(Level.FINE, "operation = " + operation); // NOI18N
        
        this.operation = operation;
        position2Fold = new HashMap<SourcePosition, Fold>(); //Weak?
    }
    
    /**
     * Notify that the fold was removed from hierarchy automatically
     * by fold hierarchy infrastructure processing
     * because it became empty (by a document modification).
     */
    public void removeEmptyNotify(Fold emptyFold) {
        position2Fold.remove(getOperation().getExtraInfo(emptyFold));
    }
    
    /**
     * Notification that this manager will no longer be used by the hierarchy.
     * <br>
     * The folds that it maintains are still valid but after this method
     * finishes they will be removed from the hierarchy.
     *
     * <p>
     * This method is not guaranteed to be called. Therefore the manager
     * must only listen weekly on the related information providers
     * so that it does not block the hierarchy from being garbage collected.
     */
    public void release() {
        if (LOG.isLoggable(Level.FINE))
            LOG.log(Level.FINE, "getOperation()=" + getOperation()); // NOI18N
        
        getHolder(getDocument()).maintainer = null;
        
        position2Fold = null;
    }
    
    /**
     * Notify that the fold was expanded automatically
     * by fold hierarchy infrastructure processing
     * because its <code>isExpandNecessary()</code>
     * return true.
     */
    public void expandNotify(Fold expandedFold) {
        //ignore...
    }
    
    /**
     * Initialize the folds provided by this manager.
     * <br>
     * The fold manager should create initial set of folds here
     * if it does not require too much resource consumption.
     * <br>
     * As this method is by default called at the file opening time
     * then it may be better to schedule the initial fold computations
     * for later time and do nothing here.
     *
     * <p>
     * Any listeners necessary for the maintenance of the folds
     * can be attached here.
     * <br>
     * Generally there should be just weak listeners used
     * to not prevent the GC of the text component.
     *
     * @param transaction transaction in terms of which the intial
     * fold changes can be performed.
     */
    public void initFolds(FoldHierarchyTransaction transaction) {
        getHolder(getDocument()).maintainer = this;
        updateFolds();
    }
    
    /**
     * Called by hierarchy upon the insertion to the underlying document.
     * <br>
     * If there would be any fold modifications required they may be added
     * to the given transaction.
     *
     * @param evt document event describing the document modification.
     * @param transaction open transaction to which the manager can add
     * the fold changes.
     */
    public void insertUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //ignore...
    }
    
    /**
     * Called by hierarchy upon the removal in the underlying document.
     * <br>
     * If there would be any fold modifications required they may be added
     * to the given transaction.
     *
     * @param evt document event describing the document modification.
     * @param transaction open transaction to which the manager can add
     * the fold changes.
     */
    public void removeUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
        //ignore...
    }
    
    /**
     * Called by hierarchy upon the change in the underlying document.
     * <br>
     * If there would be any fold modifications required they may be added
     * to the given transaction.
     *
     * @param evt document event describing the document change.
     * @param transaction open transaction to which the manager can add
     * the fold changes.
     */
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
    
    private void updateFolds() {
        scheduleUpdate();
    }
    
    private void updateFoldsImpl() {
        if (position2Fold == null)
            return ; //either the fold maintainer was not initialized yet, or it has already been released, so the fold updating should not be done.
        
        final Document doc = getDocument();
        
        final Collection<FoldInfo>   addedFolds = new ArrayList<FoldInfo>();
        final Collection<Fold> removedFolds = new HashSet<Fold>(position2Fold.values());
        
        for (Iterator/*<FoldInfo>*/ i = getHolder(getDocument()).getFolds().iterator(); i.hasNext(); ) {
            FoldInfo bn = (FoldInfo) i.next();
            
            if (!Utilities.getDefault().compareFiles(bn.getStart().getFile(), Utilities.getDefault().getFile(doc)))
                continue ;
            
            Fold      f = position2Fold.get(bn.getStart());
            
            if (f == null) {
                addedFolds.add(bn);
                continue ;
            }
            
            if (f.getEndOffset() == bn.getEnd().getOffsetValue()) {
                removedFolds.remove(f);
            } else {
                removedFolds.remove(f);
                addedFolds.add(bn);
            }
        }
        
//        try {
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
                                position2Fold.remove(getOperation().getExtraInfo(f));
                            }
                            
                            for (Iterator add = addedFolds.iterator(); add.hasNext(); ) {
                                FoldInfo info = (FoldInfo) add.next();
                                
                                install(t, info);
                            }
                        } catch (BadLocationException e) {
                            ErrorManager.getDefault().notify(e);
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
//        } catch (InvocationTargetException e) {
//            ErrorManager.getDefault().notify(e);
//        } catch (InterruptedException e) {
//            ErrorManager.getDefault().notify(e);
//        }
    }
    
    private Map<SourcePosition, Fold> position2Fold;
    
    protected FoldOperation getOperation() {
        return operation;
    }
    
    protected Document getDocument() {
        return getOperation().getHierarchy().getComponent().getDocument();
    }

    public void install(FoldHierarchyTransaction t, FoldInfo info) throws BadLocationException {
        int startOffset = info.getStart().getOffsetValue();
        int endOffset = info.getEnd().getOffsetValue();
        int startDamage = info.getBeginDamage();
        int endDamage = info.getEndDamage();

        if (endOffset >= getDocument().getLength()) {
            endOffset = getDocument().getLength() - 1;
        }
        if (endDamage >= getDocument().getLength()) {
            endDamage = getDocument().getLength() - 1;
        }
        if (startOffset < endOffset) {
            Fold f = getOperation().addToHierarchy(info.getFoldType(), info.getBlockName(), false, startOffset, endOffset, startDamage, endDamage, info.getStart(), t);

            position2Fold.put(info.getStart(), f);
        }
        else {
            //do not create the fold.
            if (startOffset != endOffset && LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "startOffset=" + startOffset); // NOI18N
                LOG.log(Level.FINE, "endOffset=" + endOffset); // NOI18N
            }
        }
    }
        
    public static class FoldInfo {
        private SourcePosition start;
        private SourcePosition end;
        
        private String         blockName;
        
        private FoldType type;
        
        private int beginDamage;
        private int endDamage;
        
        public FoldInfo() {
        }
        
        protected SourcePosition getStart() {
            return start;
        }

        protected void setStart(SourcePosition start) {
            this.start=start;
        }

        protected SourcePosition getEnd() {
            return end;
        }

        protected void setEnd(SourcePosition end) {
            this.end=end;
        }

        protected String getBlockName() {
            return blockName;
        }

        protected void setBlockName(String blockName) {
            this.blockName=blockName;
        }

        protected int getBeginDamage() {
            return beginDamage;
        }

        protected void setBeginDamage(int beginDamage) {
            this.beginDamage=beginDamage;
        }

        protected int getEndDamage() {
            return endDamage;
        }

        protected void setEndDamage(int endDamage) {
            this.endDamage=endDamage;
        }

        public FoldType getFoldType() {
            return type;
        }

        public void setType(FoldType type) {
            this.type = type;
        }
        
    }
    
    public static class FoldInfoHolder {
        private Collection<FoldInfo> folds = Collections.<FoldInfo>emptyList();
        private FoldMaintainerImpl maintainer;
        
        synchronized Collection<? extends FoldInfo> getFolds() {
            return folds;
        }
        
        synchronized void setFolds(Collection<? extends FoldInfo> folds) {
            this.folds = new LinkedList<FoldMaintainerImpl.FoldInfo>(folds);
            
            if (maintainer != null)
                maintainer.updateFolds();
        }
    }
    
    public static FoldInfoHolder getHolder(Document doc) {
        FoldInfoHolder h = (FoldInfoHolder) doc.getProperty(FoldInfoHolder.class);
        
        if (h == null) {
            doc.putProperty(FoldInfoHolder.class, h = new FoldInfoHolder());
        }
        
        return h;
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
