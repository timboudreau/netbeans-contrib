package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A property change notifier used to inform listeners of changes that occur
 * under the SAP Components node.
 */
class SAPComponentsNotifier {
    
    private SAPComponentsNotifier() {
    }

    static synchronized void addChangeListener(SAPComponentsChangeListener listener) {
        listeners.add(listener);
    }
    
    static synchronized void removeChangeListener(SAPComponentsChangeListener listener) {
        listeners.remove(listener);
    }
    
    static void notifyLibraryAdded(File libraryPath) {
        synchronized (listeners) {
            SAPComponentsChangeEvent evt =
                new SAPComponentsChangeEvent(
                    SAPComponentsNotifier.class,
                    libraryPath,
                    SAPComponentsChangeEvent.EventType.ADD_LIBRARY_EVENT);
            
            for (Iterator<SAPComponentsChangeListener> iter = listeners.iterator()
                ; iter.hasNext(); ) {
                SAPComponentsChangeListener listener = iter.next();
                listener.added(evt);
            }
        }
    }
    
    static void notifyLibraryRemoved(File libraryPath) {
        synchronized (listeners) {
            SAPComponentsChangeEvent evt =
                new SAPComponentsChangeEvent(
                    SAPComponentsNotifier.class,
                    libraryPath,
                    SAPComponentsChangeEvent.EventType.REMOVE_LIBRARY_EVENT);
            
            for (Iterator<SAPComponentsChangeListener> iter = listeners.iterator()
                ; iter.hasNext(); ) {
                SAPComponentsChangeListener listener = iter.next();
                listener.removed(evt);
            }
        }
    }
    
    static void notifyLibraryRenamed(File oldFile, File newFile) {
        synchronized (listeners) {
            SAPComponentsChangeEvent evt =
                new SAPComponentsChangeEvent(
                    SAPComponentsNotifier.class,
                    new File[] { oldFile, newFile },
                    SAPComponentsChangeEvent.EventType.RENAME_LIBRARY_EVENT);
            
            for (Iterator<SAPComponentsChangeListener> iter = listeners.iterator()
                ; iter.hasNext(); ) {
                SAPComponentsChangeListener listener = iter.next();
                listener.changed(evt);
            }
        }
    }

    private static final Set<SAPComponentsChangeListener> listeners =
        Collections.synchronizedSet(new HashSet<SAPComponentsChangeListener>());
}
