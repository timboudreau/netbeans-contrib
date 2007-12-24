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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.objectloader.CacheStrategy;
import org.netbeans.modules.dynactions.nodes.LazyLoadDataObject;
import org.openide.actions.EditAction;
import org.openide.actions.OpenAction;
import org.openide.actions.ViewAction;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;

/**
 * Base class for data objects which represent serialized Java objects and can
 * provide editors for them.
 *
 * @author Tim Boudreau
 */
public abstract class PojoDataObject<T extends Serializable> extends LazyLoadDataObject implements Discardable {
    
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
    }

    /**
     * Get the type of the serialized java object this object represents, as
     * passed to the constructor
     * @return The type of the object
     */
    protected final Class<T> getPojoType() {
        return pojoType;
    }

    Action getDefaultOpenAction() {
        EditorFactory.Kind kind = factory.defaultKind();
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
    
    protected final void loaded (T pojo) {
        listenTo(pojo);
        onLoad (pojo);
    }
    
    /**
     * Called when the object has been loaded (or restored from a modified state)
     * from disk.
     * @param pojo The object
     */
    protected void onLoad (T pojo) {
        listenTo(pojo);
    }

    final List <Action> getOpenActions () {
        List<EditorFactory.Kind> kinds = factory.supportedKinds();
        List <Action> actions = new ArrayList <Action> (7);
        for (EditorFactory.Kind kind : kinds) {
            switch (kind) {
            case EDIT :
                actions.add (SystemAction.get(EditAction.class));
                break;
            case OPEN :
                actions.add (SystemAction.get(OpenAction.class));
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
    
    @Override
    protected final Node createNodeDelegate() {
        return createNode();
    }

    /**
     * Create a Node to represent this file in the UI.  Must be a
     * PojoDataNode.
     *
     * @return The node
     */
    protected abstract PojoDataNode createNode();

    /**
     * Overridden to use the Lookup.  If you need to modify the content
     * of the lookup, override getInitialLookupContents(), or add/remove
     * items from the InstanceContent held by the instance field
     * <code>content</code>
     *
     * @param clazz A type
     * @return An object or null
     */
    /*
    @Override
    public final <T extends Cookie> T getCookie (Class<T> clazz) {
        return getLookup().lookup(clazz);
    }
     */ 

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
            System.err.println("Invoking add pcl");
            m.invoke(pojo, weak);
            System.err.println("pcl added");
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
    
    public final void discardModifications() {
        ldr.reset();
        if (save != null) {
            content.remove(save);
        }
        setModified (false);
        onModificationsDiscarded();
        fire();
    }
    
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
                    content.remove(save);
                    setModified(false);
                } finally {
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
        ChangeListener[] arr = (ChangeListener[]) listeners.toArray(new ChangeListener[listeners.size()]);
        for (ChangeListener cl : arr) {
            cl.stateChanged(new ChangeEvent(this));
        }
    }
    
    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (!isModified()) {
                System.err.println("PCL got change " + e);
                if (PojoDataObject.this.propertyChange ((T) e.getSource(), e.getPropertyName(), e.getOldValue(), e.getNewValue())) {
                    setModified (true);
                    T obj = (T) e.getSource();
                    content.add(save = new Save(obj));
                }
            }
        }
    }
    
    private Save save = null;
    private class Save implements SaveCookie {
        private T pojo;
        public Save (T pojo) {
            //Hard reference the pojo for the life of the SaveCookie, so
            //a modified pojo cannot be garbage collected
            this.pojo = pojo;
        }
        
        public void save() throws IOException {
            assert isModified();
            PojoDataObject.this.doSave(pojo);
            content.remove (this);
            save = null;
        }
    }
}
