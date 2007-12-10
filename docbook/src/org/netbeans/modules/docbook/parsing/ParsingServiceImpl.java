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
import org.netbeans.modules.docbook.resources.solbook.*;
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

    public ParsingServiceImpl(SolBookDataObject obj) {
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
