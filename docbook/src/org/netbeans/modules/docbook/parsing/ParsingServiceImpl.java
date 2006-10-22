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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ParsingServiceImpl.java
 *
 * Created on October 17, 2006, 4:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.docbook.parsing;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.docbook.Callback;
import org.netbeans.api.docbook.ParseJob;
import org.netbeans.api.docbook.ParsingService;
import org.netbeans.modules.docbook.*;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 * Implementation of the ParsingService API interface.  One of these is in the
 * lookup of the node for each docbook file.
 * 
 * @author Tim Boudreau
 */
public final class ParsingServiceImpl extends ParsingService implements FileChangeListener {
    private final FileObject obj;
    private final Object lock = new Object();
    private final List <Reference<Callback>> registered = new LinkedList <Reference <Callback>> ();

    public ParsingServiceImpl(DocBookDataObject obj) {
        this (obj.getPrimaryFile());
    }

    public ParsingServiceImpl (FileObject ob) {
        this.obj = ob;
    }

    public ParseJob enqueue(Callback callback) {
        ParseJob result = ParseJob.createJob(obj, callback);
        result.enqueue();
        return result;
    }

    private boolean contains (Callback callback) {
        boolean result = false;
        for (Reference<Callback> r : registered) {
            result |= callback.equals(r.get());
            if (result) {
                break;
            }
        }
        return result;
    }

    private List <Callback> callbacks() {
        int sz = registered.size();
        List <Callback> result = new ArrayList <Callback> (sz);
        for (Iterator <Reference<Callback>> i = registered.iterator(); i.hasNext();) {
            Reference <Callback> r = i.next();
            Callback c = r.get();
            if (c != null) {
                result.add (c);
            } else {
                i.remove();
            }
        }
        if (result.size() == 0 && sz != 0) {
            emptied();
        }
        return result;
    }

    public ParseJob register(Callback callback) {
        boolean wasEmpty;
        assert !contains (callback) : "Callback registered twice " +
                "on the same file: " + callback;

        synchronized (lock) {
            wasEmpty = registered.isEmpty();
            registered.add (new WeakReference(callback));
        }
        if (wasEmpty) {
            obj.addFileChangeListener(this);
        }
        return enqueue (callback);
    }

    public void unregister(Callback callback) {
        callback.cancel();
        boolean remove;
        synchronized (lock) {
            registered.remove(callback);
            remove = registered.isEmpty();
        }
        if (remove) {
            emptied();
        }
    }
    
    private void emptied() {
        ParseJobFactory.dequeueAll (obj);
        obj.removeFileChangeListener(this);
    }

    public void fileFolderCreated(FileEvent fe) {
    }

    public void fileDataCreated(FileEvent fe) {
    }

    public void fileChanged(FileEvent fe) {
        //XXX check if already enqueued somewhere - bool flag maybe?
        List <Callback> toDo = callbacks();
        System.err.println("Got File Change");
        Collection <ParseJob> jobs = ParseJobFactory.createJobs(obj, toDo);
        ParseJobFactory.enqueue(jobs);
    }

    public void fileDeleted(FileEvent fe) {
        synchronized (lock) {
            registered.clear();
        }
    }

    public void fileRenamed(FileRenameEvent fe) {
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }
}
