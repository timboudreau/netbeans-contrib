/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
 
package org.netbeans.modules.clazz;

import java.io.IOException;
import java.util.*;

import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.ErrorManager;
import org.openide.loaders.DataFolder;
import org.openide.loaders.FolderInstance;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;
import org.openide.util.Task;
import org.openide.util.Lookup;
import org.openide.util.TaskListener;

/**
 * A pool for registering FilterFactories for the object browser and the
 * explorer.
 *
 * @author  Svatopluk Dedic
 */
class NodeFactoryPool extends FolderInstance implements TaskListener {
    static final FilterFactory[] EMPTY = new FilterFactory[0];
    
    LinkedList          explicit;
    ElementNodeFactory  base;
    FilterFactory[]     factories = EMPTY;
    
    NodeFactoryPool(DataFolder storage, ElementNodeFactory base) {
        super(storage);
        this.base = base;
        addTaskListener(this);
    }
    
    final Object sync() {
        return base;
    }
    
    private FilterFactory[] getFactories() {
        try {
            return (FilterFactory[])instanceCreate();
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    void addFactory(FilterFactory f) {
        synchronized (sync()) {
            if (explicit == null) {
                explicit = new LinkedList();
                if (factories.length > 0)
                    f.attachTo(factories[factories.length]);
                else
                    f.attachTo(base);
            }
            else
                f.attachTo((FilterFactory)explicit.getLast());
            explicit.add(f);
        }
    }
    
    void removeFactory(FilterFactory f) {
        synchronized (sync()) {
            FilterFactory[] c;
            boolean first = explicit.getFirst() == f;
            
            for (ListIterator it = explicit.listIterator(); it.hasNext(); ) {
                FilterFactory f2 = (FilterFactory)it.next();
                if (f == f2) {
                    it.remove();
                    if (!first && it.hasNext()) {
                        ((FilterFactory)it.next()).attachTo((FilterFactory)it.previous());
                        return;
                    }
                }
            }
            if (first && !explicit.isEmpty()) {
                c = this.factories;
                if (c.length > 0)
                    ((FilterFactory)explicit.getFirst()).attachTo(c[c.length - 1]);
                else
                    ((FilterFactory)explicit.getFirst()).attachTo(base);
            }
        }
    }
    
    FilterFactory   getHead() {
        synchronized (sync()) {
            if (explicit != null && !explicit.isEmpty()) {
                return (FilterFactory)explicit.getLast();
            }
        }
        FilterFactory[] c;

        try {
            c = (FilterFactory[])instanceCreate();
        } catch (Exception ex) {
            logError(ex);
            return null;
        }
        if (c.length > 0)
            return c[c.length - 1];
        else
            return null;
    }
        
    protected Object createInstance(InstanceCookie[] cookies) 
    throws java.io.IOException, ClassNotFoundException {
        Collection l = new ArrayList(cookies.length);
        for (int i = 0; i < cookies.length; i++) {
            try {
                Object o = cookies[i].instanceCreate();
                if (!(o instanceof FilterFactory))
                    continue;
                l.add(o);
            } catch (IOException ex) {
                logError(ex);
            } catch (ClassNotFoundException ex) {
                logError(ex);
            }
        }
        return l.toArray(new FilterFactory[l.size()]);
    }
    
    void logError(Exception ex) {
        ((ErrorManager)Lookup.getDefault().lookup(ErrorManager.class)).notify(ex);
    }

    public void taskFinished(Task task) {
        FilterFactory[] factories;
        try {
            factories = (FilterFactory[])instanceCreate();
        } catch (IOException ex) {
            logError(ex);
            return;
        } catch (ClassNotFoundException ex) {
            logError(ex);
            return;
        }
        synchronized (sync()) {
            ElementNodeFactory previous = base;
            for (int i = 0; i < factories.length; i++ ) {
                FilterFactory f = factories[i];
                f.attachTo(previous);
                previous = f;
            }
            if (explicit != null && !explicit.isEmpty()) {
                ((FilterFactory)explicit.getFirst()).attachTo(previous);
            }
            this.factories = factories;
        }
    }    
}
