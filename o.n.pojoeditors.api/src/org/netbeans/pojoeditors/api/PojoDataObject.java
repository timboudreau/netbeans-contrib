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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.objectloader.CacheStrategy;
import org.netbeans.api.objectloader.States;
import org.netbeans.modules.dynactions.nodes.LazyLoadDataObject;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 * Base class for data objects which represent serialized Java objects and can
 * provide editors for them.
 *
 * @author Tim Boudreau
 */
public abstract class PojoDataObject<T extends Serializable> extends LazyLoadDataObject<T> {
    
    private final Class <T> pojoType;
    private final EditorFactory<T> factory;
    private final PropertyChangeListener pcl = new PCL();
    /**
     * Create a new PojoDataObject.  The Java object
     *
     * @param ob The FileObject represented
     * @param ldr The loader that is a factory for DataObjects of this type
     * @param pojoType The type of the Java object that should be deserialized
     * @param factory A factory for editors over this serialized object
     * @throws org.openide.loaders.DataObjectExistsException
     */
    protected PojoDataObject (FileObject ob, MultiFileLoader ldr, Class<T> pojoType, CacheStrategy strategy, EditorFactory<T> factory) throws DataObjectExistsException {
        super (ob, ldr, pojoType, strategy);
        this.pojoType = pojoType;
        this.factory = factory;
        Collection <Node.Cookie> cookies = factory.createCookies(this);
        for (Node.Cookie cookie : cookies) {
            content.add (cookie);
        }
        content.add (factory);
    }

    /**
     * Get the type of the serialized java object this object represents, as
     * passed to the constructor
     * @return The type of the object
     */
    protected final Class<T> getPojoType() {
        return pojoType;
    }

    protected final void loaded (T pojo) {
        System.err.println("PojoDataObject.loaded");
        super.loaded(pojo);
        listenTo(pojo);
        System.err.println("Invoking PojoDataObject.onLoad() with " + pojo);
        onLoad (pojo);
        PojoDataNode<T> nd = nodeRef == null ? null : nodeRef.get();
        if (nd != null) {
            System.err.println("onLoad to node " + nd);
            nd.loaded(pojo);
        } else {
            System.err.println("Node is null, can't invoke loaded.");
        }
    }
    
    /**
     * Called when the object has been loaded (or restored from a modified state)
     * from disk.
     * @param pojo The object
     */
    protected void onLoad (T pojo) {
        //do nothing by default
        System.err.println("PojoDataObject.onLoad");
    }

//    final List <Action> getOpenActions () {
//        return factory.getOpenActions();
//    }
    
    private Reference <PojoDataNode<T>> nodeRef;

    /**
     * Creates the Node delegate for this DataObject.  Override
     * createNode() to provide a custom node.
     * @return
     */
    @Override
    protected final Node createNodeDelegate() {
        PojoDataNode nd = createNode();
        nodeRef = new WeakReference<PojoDataNode<T>>(nd);
        if (ldr.getState() == States.LOADED) {
            T t = ldr.getCachedInstance();
            if (t != null) {
                nd.loaded(t);
            }
        }
        return nd;
    }

    /**
     * Create a Node to represent this file in the UI.  Must be a
     * PojoDataNode.
     *
     * @return The node
     */
    protected abstract PojoDataNode createNode();

    void editorOpened(PojoEditor<T> editor) {
        factory.notifyOpened(editor);
    }

    void editorClosed(PojoEditor<T> editor) {
        factory.notifyClosed(editor);
    }

    int getOpenEditorCount() {
        return factory.getOpenEditorCount();
    }

    private void listenTo (T pojo) {
        try {
            Method m = pojoType.getDeclaredMethod("addPropertyChangeListener",
                    PropertyChangeListener.class);
            PropertyChangeListener weak = WeakListeners.propertyChange(pcl,
                    pojo);
            m.invoke(pojo, weak);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Override to change the way the pojo is written to disk.  By default
     * it is simply written to an ObjectOutputStream.
     * <p/>
     * This method is called in an atomic action on the filesystem, with the
     * primary file's lock held, so no additional locking should be needed.
     * The stream will be closed on this methods exit.  So only I/O code is
     * needed in this method.
     * @param pojo
     * @throws java.io.IOException
     */
    protected void save (final T pojo, OutputStream stream) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream (stream);
        try {
            oout.writeObject (pojo);
        } finally {
            oout.close();
        }
    }

    @Override
    public final void setModified(boolean modif) {
        boolean old = isModified();
        if (old != modif) {
            super.setModified(modif);
            T tRef = ldr.getCachedInstance();
            if (modif && tRef == null) {
                throw new IllegalStateException ("SetModified called with no " +
                        "instance of " + type() + " loaded");
            }
            if (modif) {
                synchronized (PojoDataObject.this) {
                    if (save == null) {
                        save = new Save (tRef);
                    }
                    content.add(save);
                }
            } else {
                synchronized (PojoDataObject.this) {
                    if (save != null) {
                        content.remove (save);
                    }
                    save = null;
                }
            }
        }
    }
    
    /**
     * Set this data object to an unloaded state.  Override 
     * onModificationsDiscarded() to remove all references to this
     * DataObject's pojo from any existing UI and set it to an uninitialized
     * state.
     */
    protected final void discardModifications() {
        if (!isModified()) {
            return;
        }
        ldr.reset();
        setModified (false);
        try {
            onModificationsDiscarded();
            PojoDataNode<T> nd = nodeRef == null ? null : nodeRef.get();
            if (nd != null) {
                nd.modificationsDiscarded();
            }
        } finally {
            hintNodeChildrenChanged();
            fire();
        }
    }
    
    /**
     * Call this method to trigger notifying the node that it may need to
     * referesh its children, without instantiating the node if it does not
     * already exist.
     */
    protected final void hintNodeChildrenChanged() {
        PojoDataNode<T> nd = nodeRef == null ? null : nodeRef.get();
        if (nd != null) {
            nd.hintChildrenChanged();
        }
    }
    
    /**
     * Called when something has invoked discardModifications.
     */
    protected void onModificationsDiscarded() {
        
    }
    
    private void doSave (final T pojo) throws IOException {
        FileSystem fs = getPrimaryFile().getFileSystem();
        fs.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileLock lock = getPrimaryFile().lock();
                OutputStream out = new BufferedOutputStream(getPrimaryFile().getOutputStream(lock));
                try {
                    save (pojo, out);
                    setModified(false);
                } finally {
//                    content.remove(save);
//                    synchronized (PojoDataObject.this) {
//                        save = null;
//                    }
                    out.close();
                    lock.releaseLock();
                }
            }
        });
    }
    
    /**
     * Called when a property change is received from the pojo.  Return true
     * if it means this DataObject should mark itself as modified.  The default
     * implementation always returns true.  If the dataobject is already marked
     * as modified, that state is retained.
     * 
     * @param src The pojo
     * @param property The property name
     * @param old The old value
     * @param nue The new value
     * @return true if the dataobject should mark itself as modified
     */
    protected boolean propertyChange (T src, String property, Object old, Object nue) {
        return true;
    }
    
    private final Set<ChangeListener> listeners = 
            Collections.synchronizedSet(new HashSet<ChangeListener>());
    /**
     * Add a change listener.  The listener will be notified if 
     * discardModifications() is called, instructing any UI displaying this
     * object to reload it.
     * 
     * @param l the listener
     */
    public final void addChangeListener (ChangeListener l) {
        listeners.add (l);
    }
    
    /**
     * Remove a change listener
     * @param l The listener
     */
    public final void removeChangeListener (ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fire() {
        ChangeListener[] arr = listeners.toArray(new ChangeListener[listeners.size()]);
        for (ChangeListener cl : arr) {
            cl.stateChanged(new ChangeEvent(this));
        }
    }
    
    private final Set<Reference<PropertyChangeListener>> pojoListeners = 
            new HashSet<Reference<PropertyChangeListener>>();
    
    void addPojoPropertyChangeListener (PropertyChangeListener pce) {
        pojoListeners.add (new WeakReference<PropertyChangeListener>(pce));
    }

    void removePojoPropertyChangeListener (PropertyChangeListener pce) {
        for (Iterator <Reference<PropertyChangeListener>> i=pojoListeners.iterator(); i.hasNext();) {
            Reference<PropertyChangeListener> r = i.next();
            PropertyChangeListener listener = r.get();
            if (listener == null || listener == pce) {
                i.remove();
            }
        }
    }
    
    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            assert type().isInstance(e.getSource()) : "Got property change " +
                    "from wrong source: " + e;
            @SuppressWarnings("Unchecked")
            T obj = (T) e.getSource();
            if (PojoDataObject.this.propertyChange (obj, 
                    e.getPropertyName(), e.getOldValue(), e.getNewValue())) {
                setModified (true);
            }
            for (Iterator <Reference<PropertyChangeListener>> i = pojoListeners.iterator(); i.hasNext();) {
                Reference<PropertyChangeListener> r = i.next();
                PropertyChangeListener pcl = r.get();
                if (pcl == null) {
                    i.remove();
                } else {
                    pcl.propertyChange(e);
                }
            }
        }
    }
    
    private Save save;
    private class Save implements SaveCookie, Discardable {
        private final T pojo;
        public Save (T pojo) {
            //Hard reference the pojo for the life of the SaveCookie, so
            //a modified pojo cannot be garbage collected
            this.pojo = pojo;
        }
        
        public void save() throws IOException {
            assert isModified() : "Save called on unmodified DataObject";
            PojoDataObject.this.doSave(pojo);
        }

        public void discardModifications() {
            assert isModified() : "Discard modifications called when not modified";
            PojoDataObject.this.discardModifications();
        }
    }
}
