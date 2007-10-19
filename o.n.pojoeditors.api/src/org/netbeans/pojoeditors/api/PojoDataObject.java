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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.actions.EditAction;
import org.openide.actions.OpenAction;
import org.openide.actions.ViewAction;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Base class for data objects which represent serialized Java objects and can
 * provide editors for them.
 *
 * @author Tim Boudreau
 */
public abstract class PojoDataObject<T extends Serializable> extends DataObject {
    protected final InstanceContent content = new InstanceContent();
    private final Lookup lkp;
    private final Class <T> pojoType;
    private PropertyChangeListener pcl;
    private int editorCount;
    private final EditorFactory<T> factory;
    /**
     * Create a new PojoDataObject.  The actual Java object represented will
     * be deserialized the first time it is needed, and can be found in the
     * Lookup of this DataObject (or use the convenience method getPojo()).
     *
     * @param ob The FileObject represented
     * @param ldr The loader that is a factory for DataObjects of this type
     * @param pojoType The type of the Java object that should be deserialized
     * @param factory A factory for editors over this serialized object
     * @throws org.openide.loaders.DataObjectExistsException
     */
    protected PojoDataObject (FileObject ob, DataLoader ldr, Class<T> pojoType, EditorFactory<T> factory) throws DataObjectExistsException {
        super (ob, ldr);
        this.pojoType = pojoType;
        this.factory = factory;
        lkp = new AbstractLookup (content);
        disposePojo();
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
    @Override
    public final <T extends Cookie> T getCookie (Class<T> clazz) {
        return getLookup().lookup(clazz);
    }

    @Override
    public final Lookup getLookup() {
        return lkp;
    }

    /**
     * Override to provide additional contents for the Lookup on initialization
     * and whenever the Pojo is explicitly disposed.
     *
     * @return
     */
    protected Set getInitialLookupContents() {
        return new HashSet<Object>(factory.createCookies(this));
    }

    void editorOpened(PojoEditor<T> editor) {
        factory.notifyOpened(editor);
    }

    void editorClosed(PojoEditor<T> editor) {
        factory.notifyClosed(editor);
    }

    int getOpenEditorCount() {
        return factory.getOpenEditorCount();
    }

    void disposePojo() {
        Set <Object> set = new HashSet<Object> (getInitialLookupContents());
        set.add (this);
        content.set(set, null);
        content.add (new LazyLoadStub(), new C());
        setModified (false);
        onDispose();
    }

    /**
     * Convenience method for doing some work when the Java object has its
     * state reset to that on disk (i.e. the user was editing and elected not
     * to save changes).
     */
    protected void onDispose() {

    }

    T doLoad() throws IOException {
        FileObject fob = getPrimaryFile();
        if (fob.isValid()) {
            ObjectInputStream in = new ObjectInputStream (
                    new BufferedInputStream (
                    fob.getInputStream()));
            T pojo = null;
            try {
                pojo = load (in);
                listenTo (pojo);
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            } finally {
                in.close();
            }
            return pojo;
        }
        return null;
    }

    private void listenTo (T pojo) {
        try {
            Method m = pojoType.getDeclaredMethod("addPropertyChangeListener",
                    PropertyChangeListener.class);
            pcl = new PCL();
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
     * Load the Java object from the passed input stream.  Note it is not
     * necessary to close the input stream.  To change how loading works,
     * or handle version issues, override.  By default, just calls in.readObject()
     * and returns the result.
     * @param in
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    protected T load(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object result = in.readObject();
        if (result != null && !pojoType.isInstance(result)) {
            throw new IOException ("Expected " + pojoType.getName() + " but " +
                    "serialized object is of type " + result.getClass());
        }
        return (T) result;
    }

    public boolean isDeleteAllowed() {
        return getPrimaryFile().canWrite() && getPrimaryFile().isValid();
    }

    public boolean isCopyAllowed() {
        return getPrimaryFile().canRead();
    }

    public boolean isMoveAllowed() {
        return getPrimaryFile().canRead() && getPrimaryFile().canWrite();
    }

    public boolean isRenameAllowed() {
        return isMoveAllowed();
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected DataObject handleCopy(final DataFolder f) throws IOException {
        FileSystem fs = f.getPrimaryFile().getFileSystem();
        final FileObject[] target = new FileObject[1];
        fs.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileObject mine = getPrimaryFile();
                target[0] = getPrimaryFile().copy(f.getPrimaryFile(), mine.getName(), mine.getExt());
            }
        });
        FileObject created = target[0];
        if (created != null) {
            return DataObject.find (created);
        }
        return null;
    }

    protected void handleDelete() throws IOException {
        FileSystem fs = getPrimaryFile().getFileSystem();
        fs.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileObject fob = getPrimaryFile();
                FileLock lock = fob.lock();
                try {
                    setValid (false);
                } catch (PropertyVetoException e) {
                    IOException ise = new IOException ("Could not invalidate " +
                            fob.getPath());
                    ErrorManager.getDefault().annotate(ise,
                            e.getLocalizedMessage());
                    throw ise;
                }
                try {
                    fob.delete(lock);
                } finally {
                    lock.releaseLock();
                }
            }
        });
    }

    protected FileObject handleRename(final String name) throws IOException {
        final FileObject fob = getPrimaryFile();
        FileSystem fs = fob.getFileSystem();
        final FileObject[] target = new FileObject [1];
        fs.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileLock lock = fob.lock();
                try {
                    fob.rename(lock, name, fob.getExt());
                    target[0] = fob;
                } finally {
                    lock.releaseLock();
                }
            }
        });
        return target[0];
    }

    protected FileObject handleMove(final DataFolder df) throws IOException {
        final FileObject fob = getPrimaryFile();
        FileSystem fs = fob.getFileSystem();
        final FileObject[] target = new FileObject [1];
        fs.runAtomicAction(new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileLock lock = fob.lock();
                try {
                    FileObject fld = df.getPrimaryFile();
                    target[0] = fob.move(lock, fld, fob.getName(), fob.getExt());
                } finally {
                    lock.releaseLock();
                }
            }
        });
        return target[0];
    }

    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        //XXX pending
        return null;
    }

    public T getPojo() {
        return getLookup().lookup (pojoType);
    }

    private class C implements InstanceContent.Convertor<LazyLoadStub, T> {
        public T convert(LazyLoadStub obj) {
            try {
                return doLoad();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
        }

        public Class<? extends T> type(LazyLoadStub obj) {
            return pojoType;
        }

        public String id(LazyLoadStub obj) {
            return "pojo"; //XXX ???
        }

        public String displayName(LazyLoadStub obj) {
            return id (obj);
        }
    }

    private static final class LazyLoadStub{}
    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent arg0) {
            setModified (true);
        }
    }
}
