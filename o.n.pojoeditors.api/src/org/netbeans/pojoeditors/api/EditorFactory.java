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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.awt.EventQueue;
import java.io.Serializable;
import java.util.HashMap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.openide.actions.EditAction;
import org.openide.actions.OpenAction;
import org.openide.actions.ViewAction;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.ViewCookie;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.actions.SystemAction;

/**
 * Factory which creates TopComponents that are editors over a 
 * PojoDataObject.  Instances of EditorFactory should not be shared
 * across multiple PojoDataObjects, as each one manages the state
 * of all editors for one data object.
 *
 * @author Tim Boudreau
 */
public abstract class EditorFactory<T extends Serializable> {
    /** Editor kinds corresponding to OpenAction, ViewAction and
     * EditAction */
    public static enum Kind {
        OPEN, 
        EDIT, 
        VIEW,
    }
    
    protected EditorFactory() {
    }
    
    private final HashMap <Kind, Collection<Reference<PojoEditor<T>>>> editors = 
            new HashMap<EditorFactory.Kind, Collection<Reference<PojoEditor<T>>>>();
    /**
     * Create an editor for this action.  Will only be called with kinds
     * returned by supportedKinds().
     * 
     * @param obj A DataObject
     * @param kind the kind of action, which will determine what kind of
     *      editor should be created.
     * @return
     */
    public abstract PojoEditor<T> create (PojoDataObject<T> obj, Kind kind);
    /**
     * Get a list of supported editor kinds in the order they should appear
     * on the popup menu.
     * @return A list of EditorFactory.Kind objects, with no duplicates
     */
    public abstract List<Kind> supportedKinds();
    /**
     * Get the kind of the default action (which kind of editor should be
     * opened if the user double clicks the node).
     * 
     * @return
     */
    public abstract Kind defaultKind();
    
    public final synchronized PojoEditor<T> get (Kind kind, PojoDataObject<T> obj) {
        return get (kind, obj, true);
    }
    
    private final synchronized PojoEditor<T> get (Kind kind, PojoDataObject<T> obj, boolean create) {
        assert EventQueue.isDispatchThread();
        Collection<Reference <PojoEditor<T>>> refs = editors.get (kind);
        PojoEditor<T> result = null;
        if (refs == null) {
            refs = new ArrayList <Reference<PojoEditor<T>>> (1);
            editors.put (kind, refs);
        } else {
            for (Reference<PojoEditor<T>> ref : refs) {
                result = ref.get();
                if (result != null) {
                    break;
                }
            }
        }
        if (result == null && create) {
            result = create (obj, kind);
            if (result != null) {
                refs.add (new WeakReference<PojoEditor<T>>(result));
                result.open();
                result.requestActive();
            }
        }
        return result;
    }
    
    public final List <Action> getOpenActions() {
        List<EditorFactory.Kind> kinds = supportedKinds();
        List <Action> actions = new ArrayList <Action> (7);
        for (EditorFactory.Kind kind : kinds) {
            switch (kind) {
            case OPEN :
                actions.add (SystemAction.get(OpenAction.class));
                break;
            case EDIT :
                actions.add (SystemAction.get(EditAction.class));
                break;
            case VIEW :
                actions.add (SystemAction.get(ViewAction.class));
                break;
            default :
                throw new AssertionError();
            }
        }
        return actions;
    }
    
    public final Action getDefaultOpenAction() {
        EditorFactory.Kind kind = defaultKind();
        if (kind != null) {
            switch (kind) {
            case EDIT :
                return SystemAction.get(EditAction.class);
            case VIEW :
                return SystemAction.get(ViewAction.class);
            case OPEN :
                return SystemAction.get(OpenAction.class);
            default :
                throw new AssertionError();
            }
        }
        return null;
    }
    
    
    void notifyOpened(PojoEditor<T> ed) {
        Collection<Reference <PojoEditor<T>>> refs = editors.get (ed.getKind());
        if (refs == null) {
//            throw new IllegalStateException ("Editor instantiated without " +
//                    "using action - probably a bug: " + ed);
//            refs = new ArrayList <Reference<PojoEditor<T>>> (Collections.singleton(new WeakReference<PojoEditor<T>>>(ed)));
            refs = new ArrayList <Reference<PojoEditor<T>>>(3);
            refs.add (new WeakReference<PojoEditor<T>> (ed));
            editors.put(ed.getKind(), refs);
        }
        for (Iterator<Reference<PojoEditor<T>>> it = refs.iterator(); it.hasNext();) {
            Reference<PojoEditor<T>> ref = it.next();
            PojoEditor<T> candidate = ref.get();
            if (candidate == null) {
                //Clean up dead references
                it.remove();
            } else if (candidate == ed) {
                //It's one we created, we know about it
                return;
            }
        }
        
        //If we got here, it is a cloned editor that we did not create.  Add it
        //to the list of known editors.
        WeakReference<PojoEditor<T>> ref = new WeakReference<PojoEditor<T>>(ed);
        refs.add (ref);
    }
    
    void notifyClosed(PojoEditor<T> ed) {
        Collection<Reference <PojoEditor<T>>> refs = editors.get (ed.getKind());
        for (Iterator<Reference<PojoEditor<T>>> it = refs.iterator(); it.hasNext();) {
            Reference<PojoEditor<T>> ref = it.next();
            PojoEditor<T> candidate = ref.get();
            if (candidate == null || candidate == ed) {
                //Clean up dead references
                it.remove();
                return;
            }
        }
    }
    
    int getOpenEditorCount() {
        int result = 0;
        //Iterate all kinds
        for (Kind k : Kind.values()) {
            Collection<Reference <PojoEditor<T>>> refs = editors.get (k);
            if (refs == null) {
                continue;
            }
            //Iterate all known editors of each kind
            for (Iterator<Reference<PojoEditor<T>>> it = refs.iterator(); it.hasNext();) {
                Reference<PojoEditor<T>> ref = it.next();
                PojoEditor<T> candidate = ref.get();
                //Count the live ones, remove the dead ones
                if (candidate == null) {
                    //Clean up dead references
                    it.remove();
                } else {
                    result++;
                }
            }
        }
        return result;
    }
    
    Collection <Node.Cookie> createCookies(PojoDataObject<T> dob) {
        if (!dob.isValid()) {
            return Collections.<Node.Cookie>emptyList();
        }
        List<Kind> kinds = supportedKinds();
        Set<Node.Cookie> result = new HashSet<Node.Cookie>();
        for (Kind kind : kinds) {
            switch (kind) {
            case EDIT :
                result.add (new Edit(dob));
                break;
            case OPEN :
                result.add (new Open(dob));
                break;
            case VIEW :
                result.add (new View(dob));
                break;
            default :
                throw new AssertionError();
            }
        }
        return result;
    }
    
    private class Base {
        private PojoDataObject<T> dob;
        Base (PojoDataObject<T> dob) {
            this.dob = dob;
        }

        public void view() {
            Mutex.EVENT.postReadRequest(new Runnable() {
                public void run() {
                    PojoEditor<T> ed = get (Kind.VIEW, dob);
                    ed.open();
                    ed.requestActive();
                }
            });
        }

        public void open() {
            Mutex.EVENT.postReadRequest(new Runnable() {
                public void run() {
                    PojoEditor<T> ed = get (Kind.OPEN, dob);
                    ed.open();
                    ed.requestActive();
                }
            });
        }

        public void edit() {
            Mutex.EVENT.postReadRequest(new Runnable() {
                public void run() {
                    PojoEditor<T> ed = get(Kind.EDIT, dob);
                    ed.open();
                    ed.requestActive();
                }
            });
        }
    }
    
    private class Edit extends Base implements EditCookie {
        Edit (PojoDataObject<T> dob) {
            super(dob);
        }
    }
    
    private class View extends Base implements ViewCookie {
        View (PojoDataObject<T> dob) {
            super(dob);
        }
    }
    
    private class Open extends Base implements OpenCookie {
        Open (PojoDataObject<T> dob) {
            super(dob);
        }
    }
    
    PojoEditorFinder createFinder (PojoDataObject<T> ob) {
        return new PojoEditorFinderImpl(ob);
    }
    
    private class PojoEditorFinderImpl implements PojoEditorFinder<T> {
        PojoDataObject<T> ob;
        PojoEditorFinderImpl (PojoDataObject<T> ob) {
            this.ob = ob;
        }
        
        public PojoEditor find(Kind kind, boolean openIfNecessary) {
            if (supportedKinds().contains(kind)) {
                return get (kind, ob, openIfNecessary);
            }
            return null;
        }
        
    }
}
