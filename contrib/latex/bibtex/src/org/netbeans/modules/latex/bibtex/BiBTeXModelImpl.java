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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
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
package org.netbeans.modules.latex.bibtex;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModelChangeListener;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModelChangedEvent;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/** This is going to be the central class of the BiBTeX API.
 *
 * @author Jan Lahoda
 */
public class BiBTeXModelImpl extends BiBTeXModel implements PropertyChangeListener, DocumentListener {
    
    private static final boolean debug = Boolean.getBoolean("netbeans.latex.bibtex.incremental.debug");
    private static final boolean removeDebug = Boolean.getBoolean("netbeans.latex.bibtex.incremental.remove.debug") || debug;
    
    private Object file;
    private SortedSet entries;
    private boolean upToDate;
    
    private List listeners;
    
    private RequestProcessor.Task workingTask = null;
    private int timeToWait = 2000;
    private Object prepareReparseWorkingTask = new String("prepare-reparse-working-task");

    /** Creates a new instance of BiBTeXModel */
    /*package private*/ BiBTeXModelImpl(Object file) {
        this.file = file;
        upToDate = false;
        listeners = new ArrayList();
    }
    
    private void fireEntriesRemoved(List/*<Entry>*/ entries) {
//        System.err.println("fireEntriesRemoved(" + entries + ")");
        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
            e.removePropertyChangeListener(this);
            e.setModel(null);
        }

        BiBTeXModelChangedEvent evt = null;
        List listeners = null;
        
        synchronized (this) {
            listeners = new ArrayList(this.listeners);
        }
        
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            if (evt == null) {
                evt = new BiBTeXModelChangedEvent(this, BiBTeXModelChangedEvent.ENTRIES_REMOVED, Collections.unmodifiableList(entries));
            }
            ((BiBTeXModelChangeListener) i.next()).entriesRemoved(evt);
        }
    }
    
    private void fireEntriesAdded(List/*<Entry>*/ entries) {
//        System.err.println("fireEntriesAdded(" + entries + ")");
        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
            e.addPropertyChangeListener(this);
            e.setModel(this);
        }
            
        BiBTeXModelChangedEvent evt = null;
        List listeners = null;
        
        synchronized (this) {
            listeners = new ArrayList(this.listeners);
        }
        
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            if (evt == null) {
                evt = new BiBTeXModelChangedEvent(this, BiBTeXModelChangedEvent.ENTRIES_ADDED, Collections.unmodifiableList(entries));
            }
            ((BiBTeXModelChangeListener) i.next()).entriesAdded(evt);
        }
    }
    
    public synchronized void addBiBTexModelChangeListener(BiBTeXModelChangeListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeBiBTexModelChangeListener(BiBTeXModelChangeListener l) {
        listeners.remove(l);
    }
    
    /*private*/ SortedSet doParse() throws IOException {
        Document doc = Utilities.getDefault().openDocument(file);
        int      currentOffset = 0;
        Entry    entry;
        BiBParser parser = new BiBParser();
        SortedSet entries = new TreeSet(new EntryComparator());
        
        while (currentOffset < doc.getLength() && (entry = parser.parseEntry(doc, currentOffset)) != null) {
            //                System.err.println("entry = " + entry );
            entries.add(entry);
            currentOffset = entry.getEndPosition().getOffsetValue();// + 1;
        }
        
        return entries;
    }
    
    private synchronized void doFullReParse() {
//        System.err.println("doFullReParse");
        synchronized (prepareReparseWorkingTask) {
            if (workingTask != null) {
                workingTask.cancel(); //Maybe we are simply inside the task. Hopefully this is not forbidden.
                workingTask = null;
            }
        }
        
        if (upToDate)
            return ;
        
        try {
            if (entries == null) {
                entries = new TreeSet(new EntryComparator());
                Utilities.getDefault().openDocument(file).addDocumentListener(this);
            }
            
            SortedSet old = entries;
            entries = doParse();
            
            for (Iterator i = entries.iterator(); i.hasNext(); ) {
                Entry e = (Entry) i.next();
                
                e.addPropertyChangeListener(this);
            }
            
            upToDate = true;
            
            if (old != null && old.size() != 0) {
                fireEntriesRemoved(new ArrayList(old));
            }
            fireEntriesAdded(new ArrayList(entries));
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private Entry findAffectedEntry(SourcePosition sp) {
        //TODO: faster, binary search or similar:
        for (Iterator i = getEntries().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getStartPosition().getFile());
            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getEndPosition().getFile());
            
            int start = e.getStartPosition().getOffsetValue();
            int end   = e.getEndPosition().getOffsetValue();
            int off   = sp.getOffsetValue();
            
            if (start <= off && off <= end)
                return e;
        }
        
        return null;
    }
    
    public synchronized List/*<Entry>*/ getEntries() {
//        System.err.println("getEntries");
        if (!upToDate) {
            doFullReParse();
        }
        
        return Collections.unmodifiableList(new ArrayList(entries));
    }
    
    public synchronized void addEntry(final Entry e) {
        synchronized (PLANNED_LOCK) {
            if (plannedChange)
                return ;
            
            plannedChange = true;
        }
        
        if (!upToDate)
            getEntries();

        try {
            final Entry          last    = entries.isEmpty() ? null : (Entry) entries.last();
            final SourcePosition end     = last != null ? last.getEndPosition() : null;
            final String         toWrite = e.writeOut();
            
            try {
                final Document doc = Utilities.getDefault().openDocument(file);
                NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                    public void run() {
                        try {
                            int startOffset = end != null ? end.getOffsetValue() : 0;
                            
                            doc.insertString(startOffset, "\n", null);
                            doc.insertString(startOffset + 1, toWrite, null);
                            e.setStartPosition(new SourcePosition(doc, startOffset + 1));
                            e.setEndPosition(new SourcePosition(doc, startOffset + toWrite.length()));
                            
                            //the previous operations in fact typed the text BEFORE the end mark of the last entry.
                            //Therefore the position is moved with the text and therefore the last entry's
                            //end mark has to be updated (otherwise it is incorrect).
                            if (last != null)
                                last.setEndPosition(new SourcePosition(doc, startOffset));
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                });
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            
            entries.add(e);
            
            fireEntriesAdded(Collections.singletonList(e));
        } finally {
            synchronized (PLANNED_LOCK) {
                plannedChange = false;
            }
        }
    }
    
    private String PLANNED_LOCK = new String("planned-lock");
    private boolean plannedChange = false;
    
    public void propertyChange(PropertyChangeEvent evt) {
        //Clustering of changes (change of the content, may fire quite many PropertyChanges).
//        System.err.println("propertyChange(" + evt + ")");
        synchronized (PLANNED_LOCK) {
            if (plannedChange)
                return ;
            
            plannedChange = true;
        }
        
        try {
            Object source = evt.getSource();
            
            if (source instanceof Entry) {
	        if ("model".equals(evt.getPropertyName()))
		    return;

                final Entry e = (Entry) source;
                final String toWrite = e.writeOut();
                
                try {
                    final Document doc = Utilities.getDefault().openDocument(e.getStartPosition().getFile());
                    NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                        public void run() {
                            try {
                                int startOffset = e.getStartPosition().getOffsetValue();
                                int endOffset = e.getEndPosition().getOffsetValue();
//                                System.err.println("startOffset = " + startOffset );
//                                System.err.println("endOffset = " + endOffset );
                                int length = endOffset - startOffset + 1;
                                
                                doc.remove(startOffset, length);
//                                System.err.println("toWrite = " + toWrite );
                                doc.insertString(startOffset, toWrite, null);
                                e.setStartPosition(new SourcePosition(doc, startOffset));
                                e.setEndPosition(new SourcePosition(doc, startOffset + toWrite.length() - 1));
                            } catch (BadLocationException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    });
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        } finally {
            synchronized (PLANNED_LOCK) {
                plannedChange = false;
            }
        }
    }
    
    public void removeEntry(final Entry e) {
        if (!upToDate)
            getEntries();
        
        assert entries.contains(e);
        
        synchronized (PLANNED_LOCK) {
            if (plannedChange)
                return ;
            
            plannedChange = true;
        }
        
        try {
            entries.remove(e);
            
            try {
                final Document doc = Utilities.getDefault().openDocument(e.getStartPosition().getFile());
                NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                    public void run() {
                        try {
                            int startOffset = e.getStartPosition().getOffsetValue();
                            int endOffset = e.getEndPosition().getOffsetValue();
                            int length = endOffset - startOffset + 1;
                            
                            doc.remove(startOffset, length);
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                });
            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            
            fireEntriesRemoved(Collections.singletonList(e));
        } finally {
            synchronized (PLANNED_LOCK) {
                plannedChange = false;
            }
        }
    }
    
    /**Can be called only from insertUpdate and removeUpdate!!!!!
     */
    private void documentChange() {
        System.err.println("Full Reparse!");
        
        StackTraceElement[] elements = new Exception().getStackTrace();
        StackTraceElement   caller   = elements[1];
        
        System.err.println("Called from: " + caller.toString());
        
        synchronized (this) {
            upToDate = false;
        }
        
        synchronized (prepareReparseWorkingTask) {
            if (workingTask != null) {
                workingTask.cancel();
            }
            
            workingTask = RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    doFullReParse();
                }
            });
        }
    }
    
    public void changedUpdate(DocumentEvent e) {
        //ignored...
    }
    
    public synchronized void insertUpdate(DocumentEvent ev) {
//        System.err.println("insertUpdate:");
        
        synchronized (PLANNED_LOCK) {
            if (plannedChange)
                return ;
            
            plannedChange = true;
        }

        try {
            int changeStart = ev.getOffset();
            int changeEnd   = ev.getOffset() + ev.getLength();

            Entry affected = findAffectedEntry(new SourcePosition(ev.getDocument(), ev.getOffset()));
            
            if (affected != null) {
                //Insert inside an entry. Try to reparse it:
                try {
                    Entry nue = new BiBParser().parseEntry(ev.getDocument(), affected.getStartPosition().getOffsetValue());
                    
                    if (nue != null && affected.getStartPosition().equals(nue.getStartPosition()) && affected.getEndPosition().equals(nue.getEndPosition()) && affected.getClass().equals(nue.getClass())) {
                        //The same entry, copy important fields:
                        affected.update(nue);
                        return ;
                    } else {
                        //The entry cannot be reparsed. Reasons currently unknown.

//                        System.err.println("Will be full reparse.");
//                        System.err.println("affected.start = " + affected.getStartPosition() );
//                        System.err.println("affected.end = " + affected.getEndPosition() );
//                        System.err.println("nue.start = " + nue.getStartPosition() );
//                        System.err.println("nue.end = " + nue.getEndPosition() );
//                        System.err.println("changeStart = " + changeStart );
//                        System.err.println("changeEnd = " + changeEnd );
                        //Currently, a full reparse occurs.
                        
//                        System.err.println("Will be full reparse.");
                        documentChange();
                        return ;
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
//                    System.err.println("Will be full reparse.");
                    ex.printStackTrace();
                    documentChange();
                    return ;
                }
            } else {
                //Insert outside of entries.
                
                List toAdd = new ArrayList();
                
                //1. Find the last previous
                
                Entry last = null;
                
                Iterator i = getEntries().iterator();
                
                //TODO: faster, binary search or similar:
                for ( ; i.hasNext(); ) {
                    Entry e = (Entry) i.next();
                    
                    //            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getStart().getFile());
                    //            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getEnd().getFile());
                    
                    int end   = e.getEndPosition().getOffsetValue();
                    
                    if (end < changeStart)
                        last = e;
                }
                
                int parseStartOffset;
                int parseEndOffset;
                
                if (last == null) {
                    //currently not able to handle:
                    documentChange();
                    return ;
                } else {
                    parseStartOffset = last.getEndPosition().getOffsetValue() + 1;//TODO:verify the +1!
                    if (i.hasNext()) {
                        Entry e = (Entry) i.next();
                        
                        parseEndOffset = e.getEndPosition().getOffsetValue();
                    } else {
                        parseEndOffset = ev.getDocument().getLength();
                    }
                }

                try {
                    int currentOffset = changeStart;
                    int safetyCounter = 0;
                    
                    while (currentOffset < changeEnd) {
                        if (safetyCounter++ > 1000) {
                            System.err.println("safetyCounter > 1000!");
                            documentChange();
                            return ;
                        }
                        
                        //Several variants may occur. Try to parse a new entry:
                        Entry nue = new BiBParser().parseEntry(ev.getDocument(), parseStartOffset);
                        
                        if (nue != null/*ignore, no entry*/) {
//                            System.err.println("nue != null");
                            if (nue.getEndPosition().getOffsetValue() < parseEndOffset) {
                                //Seems acceptable:
//                                System.err.println("found new entry.");
                                toAdd.add(nue);
                            } else {
                                if (nue.getStartPosition().getOffsetValue() >= parseEndOffset) {
//                                    System.err.println("already existing..");
                                    //already existing entry...
                                } else {
                                    //Cannot handle (part of the entry was added by this event, part was an _existing_ entry (I didn't think this may happen!):
//                                    System.err.println("Cannot handle (part of the entry was added by this event, part was an _existing_ entry (I didn't think this may happen!):");
                                    documentChange();
                                    
                                    return ;
                                }
                            }
                            currentOffset = nue.getEndPosition().getOffsetValue() + 1;
                        } else {
//                            System.err.println("nue == null");
                            break;
                        }
                        
                    }
                    
                    if (toAdd.size() != 0) {
                        //TODO: listeners!
                        entries.addAll(toAdd);
                        fireEntriesAdded(toAdd);
                    }
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
//                    System.err.println("Will be full reparse.");
                    ex.printStackTrace();
                    documentChange();
                    return ;
                }
            }
        } finally {
            synchronized (PLANNED_LOCK) {
                plannedChange = false;
            }
        }
    }
    
    public synchronized /*??*/ void removeUpdate(DocumentEvent ev) {
//        System.err.println("removeUpdate");
        //TODO:  Remove of a comment may serve as an insert! (should be OK once the lexer is used).
        if (entries == null || !upToDate)
            return ;
        
        synchronized (PLANNED_LOCK) {
            if (plannedChange)
                return ;
            
            plannedChange = true;
        }
        
        try {
            int changeStart = ev.getOffset();
            int changeEnd   = ev.getOffset() + ev.getLength();
            
            if (removeDebug) {
                System.err.println("changeStart = " + changeStart );
                System.err.println("changeEnd = " + changeEnd );
            }
                
            List toRemove = new ArrayList();
            
            //TODO: faster, binary search or similar:
            for (Iterator i = getEntries().iterator(); i.hasNext(); ) {
                Entry e = (Entry) i.next();
                
                //            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getStart().getFile());
                //            assert Utilities.getDefault().compareFiles(sp.getFile(), e.getEnd().getFile());
                
                int start = e.getStartPosition().getOffsetValue();
                int end   = e.getEndPosition().getOffsetValue();
                
                if (start >= changeStart) {
                    //TODO: verify and explain:
                    start += ev.getLength();
                    end   += ev.getLength();
                }
                
                if (removeDebug) {
                    System.err.println("e: " + e.toString());
                    System.err.println("start = " + start );
                    System.err.println("end = " + end );
                }
                
                if (end < changeStart || start > changeEnd) {
                    //This entry is not directly affected by the removed text, skipping...
                    continue;
                }
                
                if (start >= changeStart && end <= changeEnd) {
                    //this entry is whole in the removed piece of text, so remove it...
                    //in fact, for a removed entry holds e.getStart().getPosition() == e.getEnd().getPosition()
                    toRemove.add(e);
                    continue;
                } else {
                    //Part of this entry has been modified. Try to reparse the entry:
                    try {
                        Entry nue = new BiBParser().parseEntry(ev.getDocument(), e.getStartPosition().getOffsetValue());
                        
                        if (nue != null) {
                            if (e.getStartPosition().equals(nue.getStartPosition()) && e.getEndPosition().equals(nue.getEndPosition()) && e.getClass().equals(nue.getClass())) {
                                //The same entry, copy important fields:
                                e.update(nue);
                                continue;
                            } else {
                                //The entry cannot be reparsed. Reasons:
                                //1. The begining or the end of the entry was removed.
                                
                                //Currently, a full reparse occurs.
                                
//                                System.err.println("Will be full reparse.");
//                                System.err.println("start = " + start );
//                                System.err.println("end = " + end );
//                                System.err.println("changeStart = " + changeStart );
//                                System.err.println("changeEnd = " + changeEnd );
                                documentChange();
                                return ;
                            }
                        } else {
                            //The entry was removed (not as a whole, but it is unparseable). How to find out WHICH entry was removed?
                            documentChange();
                            return ;
                        }
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify(ex);
//                        System.err.println("Will be full reparse.");
                        ex.printStackTrace();
                        documentChange();
                        return ;
                    }
                }
            }
            
            if (toRemove.size() > 0) {
                //It is not possible to do: entries.removeAll(toRemove), as entries
                //use a special comparator that is not consistent with Entry.equals
                //(and it is probably not possible to make it consistent).
                for (Iterator i = entries.iterator(); i.hasNext(); ) {
                    Entry entry = (Entry) i.next();
                    
                    if (toRemove.contains(entry)) {
                        i.remove();
                    }
                }
                fireEntriesRemoved(toRemove);
            }
        } finally {
            synchronized (PLANNED_LOCK) {
                plannedChange = false;
            }
        }
    }
    
    private static class EntryComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Entry e1 = (Entry) o1;
            Entry e2 = (Entry) o2;
            
            //TODO: locking?
            if (e1.getStartPosition().getOffsetValue() < e2.getStartPosition().getOffsetValue())
                return -1;
            
            if (e1.getStartPosition().getOffsetValue() > e2.getStartPosition().getOffsetValue())
                return 1;
            
            return 0;
        }
        
    }
    
    public static BiBTeXModelChangeListener createWeakListeners(BiBTeXModelChangeListener l, BiBTeXModel source) {
        return (BiBTeXModelChangeListener) WeakListeners.create(BiBTeXModelChangeListener.class, l, source);
    }
}
