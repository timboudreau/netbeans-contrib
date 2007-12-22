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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import javax.swing.JLabel;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.api.objectloader.ObjectReceiver;
import org.netbeans.pojoeditors.api.EditorFactory.Kind;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;

/**
 * Base class for TopComponents that are editors over PojoDataObjects.  Handles
 * the bookkeeping of the set of open editors, etc.
 *
 * If writeReplace is not overridden, the default serialization code will expect
 * that this editor has a public constructor that takes an argument of
 * PojoDataObject&lt;T&gt;.
 * 
 * @author Tim Boudreau
 */
public abstract class PojoEditor<T extends Serializable> extends CloneableTopComponent {
    private PojoDataObject<T> dataObject;
    private final ModificationListener modListener = new ModificationListener();
    private final EditorFactory.Kind kind;
    private final NodeListener nodeListener = new NL();
    protected PojoEditor (PojoDataObject<T> dataObject, EditorFactory.Kind kind) {
        this.kind = kind;
        init (dataObject);
        if (dataObject != null) { //if null, could not deserialize
            ProxyLookup lkp = new ProxyLookup (
                    dataObject.getLookup(), 
                    createLookup(), 
                    Lookups.fixed(dataObject, dataObject.getNodeDelegate()));
            associateLookup (lkp);
            setDisplayName (dataObject.getName());
        }
        setLayout (new BorderLayout());
    }
    
    /**
     * Alternate constructor for the case where the file in fact cannot be
     * opened.
     * 
     * @param path The string path to the file
     * @param kind The kind of editor this is
     */
    protected PojoEditor (String path, EditorFactory.Kind kind) {
        this.kind = kind;
        int ix = path.lastIndexOf('/');
        if (ix != -1) {
            path = path.substring (ix);
        }
        setDisplayName(path);
        add (new JLabel (NbBundle.getMessage(PojoEditor.class, 
                "LBL_FileGone", path)), BorderLayout.CENTER);
        setLayout (new BorderLayout());
    }
    
    Kind getKind() {
        return kind;
    }
        
    private void init(PojoDataObject<T> dataObject) {
        this.dataObject = dataObject;
        dataObject.addPropertyChangeListener(WeakListeners.propertyChange(
                modListener, dataObject));
        setActivatedNodes(new Node[] { dataObject.getNodeDelegate()});
        updateDisplayName();
    }
    
    /**
     * Create a custom lookup for this component
     * @return
     */
    protected Lookup createLookup() {
        return Lookup.EMPTY;
    }
    
    @Override
    protected String preferredID() {
        return getDataObject() == null ? getClass().getName() : getDataObject().getPrimaryFile().getPath();
    }
    
    public T getPojo() {
        return pojo;
    }

    @Override
    public boolean canClose() {
        assert EventQueue.isDispatchThread();
        if (dataObject.getOpenEditorCount() > 1) {
            return true;
        }
        if (!dataObject.isValid()) {
            return true;
        }
        
        SaveCookie save = dataObject.getLookup().lookup(SaveCookie.class);
        if (save != null) {
            NotifyDescriptor.Confirmation dlg = new NotifyDescriptor.Confirmation(
                    dataObject.getName() + " is modified.  Save it?");  //XXX localize
            Object answer = DialogDisplayer.getDefault().notify(dlg);
            if (NotifyDescriptor.YES_OPTION.equals(answer)) {
                try {
                    save.save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else if (NotifyDescriptor.NO_OPTION.equals(answer)) {
                dataObject.discardModifications();
            } else {
                return false;
            }
        }
        
        return true;
    }

    @Override
    protected final void componentOpened() {
        System.err.println("Component opened on " + this);
        assert EventQueue.isDispatchThread();
        super.componentOpened();
        dataObject.editorOpened(this);
        dataObject.addPropertyChangeListener(modListener);
        dataObject.getNodeDelegate().addNodeListener(nodeListener);
        onOpen();
        if (pojo == null) {
            System.err.println("Invoking load");
            load();
        } else {
            System.err.println("Pojo already loaded");
        }
    }
    
    @Override
    protected final void componentClosed() {
        assert EventQueue.isDispatchThread();
        dataObject.editorClosed(this);
        dataObject.removePropertyChangeListener(modListener);
        dataObject.getNodeDelegate().removeNodeListener(nodeListener);
        onClose();
        if (pojo != null && dataObject != null) {
            clear(pojo);
        }
    }

    /**
     * Convenience method to run some code on component open.  Called from
     * componentOpened().
     */
    protected void onOpen() {
        
    }
    
    /**
     * Convenience method to run some code on component close.  Called from
     * componentClosed().
     */
    protected void onClose() {
        
    }
    
    protected abstract Component createEditorUI(T pojo);
    
    protected final T get() {
        return pojo;
    }
    
    private T pojo;
    protected final void set (T pojo) {
        if (pojo == null) {
            throw new NullPointerException ("Pojo is null");
        }
        System.err.println("Set " + pojo);
        this.pojo = pojo;
        onSet (pojo);
        if (getComponents().length == 0 || getComponents()[0].getClass() == JLabel.class) {
            removeAll();
            Component editor = createEditorUI(pojo);
            System.err.println("Adding " + editor);
            add (editor, BorderLayout.CENTER);
            invalidate();
            revalidate();
            repaint();
        }
    }
    
    protected void onSet (T pojo) {
        System.err.println("onSet " + pojo);
    }
    
    private final void clear (T oldPojo) {
        removeAll();
        synchronized (loadLock) {
            if (receiver != null) {
                ObjectLoader ldr = getLookup().lookup(ObjectLoader.class);
                if (ldr != null) {
                    ldr.cancel(receiver);
                }
            }
        }
        onClear(oldPojo);
    }
    
    protected void onClear(T oldPojo) {
        System.err.println("onClear " + oldPojo);
    }
    
    protected void onLoadFailed (Exception e) {
        System.err.println("onLoadFailed " + e);
    }
    
    private final Object loadLock = new Object();
    private ObjectReceiver<T> receiver;
    private void load() {
        if (getDataObject() == null) {
            System.err.println("Dataobject null.  Punt.");
            return;
        }
        ObjectLoader<T> ldr = getLookup().lookup(ObjectLoader.class);
        if (ldr == null) {
            System.err.println("Loader null.  Punt");
            //XXX put a label in the content area
            return;
        }
        synchronized (loadLock) {
            if (receiver != null) {
                //Already loading
                System.err.println("Already loading.  Punt.");
                return;
            }
            receiver = new ObjectReceiver<T>() {
                public void setSynchronous(boolean val) {
                    System.err.println("Set synchronous " + val);
                    if (!val && !EventQueue.isDispatchThread()) {
                        try {
                            EventQueue.invokeAndWait(new Runnable() {
                                public void run() {
                                    JLabel lbl = new JLabel("Loading...");
                                    add(lbl, BorderLayout.CENTER);
                                }
                            });
                        } catch (InterruptedException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }

                public void received(T t) {
                    System.err.println("Load completed with " + t);
                    try {
                        set (t);
                    } finally {
                        done();
                    }
                }

                public void failed(Exception e) {
                    System.err.println("Load failed");
                    e.printStackTrace();
                    done();
                    onLoadFailed (e);
                }
                
                private void done() {
                    System.err.println("load completed");
                    synchronized (loadLock) {
                        receiver = null;
                    }
                }

            };
        }
        System.err.println("off we go...");
        ldr.get(receiver);
    }
    
    protected final PojoDataObject<T> getDataObject() {
        return (PojoDataObject<T>) getLookup().lookup(DataObject.class);
    }    
    
    /**
     * By default, if the dataobject is non-null, tries to invoke a constructor
     * that takes the same exact type as the current DataObject's type.  If the
     * DataObject is null (someone is trying to clone an editor deserialized
     * against a file that no longer exists) then it looks for a constructor
     * that takes a string.  If you want other behavior or polymorphism,
     * override this method.
     * 
     * @return A clone of this editor.
     */
    @Override
    protected CloneableTopComponent createClonedObject() {
        Class type = getClass();
        DataObject dob = getDataObject();
        if (dob != null) {
            try {
                Constructor c = type.getConstructor(dob.getClass());
                return (CloneableTopComponent) c.newInstance(dob);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException (ex);
            } catch (SecurityException ex) {
                throw new IllegalStateException (ex);
            } catch (InstantiationException ex) {
                throw new IllegalStateException (ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException (ex);
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException (ex);
            } catch (InvocationTargetException ex) {
                throw new IllegalStateException (ex);
            }
        } else {
            try {
                Constructor c = type.getConstructor(String.class);
                return (CloneableTopComponent) c.newInstance(getDisplayName());
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException (ex);
            } catch (SecurityException ex) {
                throw new IllegalStateException (ex);
            } catch (InstantiationException ex) {
                throw new IllegalStateException (ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException (ex);
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException (ex);
            } catch (InvocationTargetException ex) {
                throw new IllegalStateException (ex);
            }
        }
    }
    

    @Override
    public int getPersistenceType() {
        return !dataObject.isValid() ? PERSISTENCE_NEVER : 
            PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Stub<T> stub = (Stub<T>) in.readObject();
        PojoDataObject<T> obj = stub.getDataObject();
        if (obj != null) {
            init (obj);
        }
    }

    @Override
    public UndoRedo getUndoRedo() {
        UndoRedo result = Lookup.getDefault().lookup(UndoRedo.class);
        if (result == null) {
            result = UndoRedo.NONE;
        }
        return result;
    }

    @Override
    protected Object writeReplace() throws ObjectStreamException {
        return new Stub<T> (dataObject, getClass());
    }
    
    private static final class Stub<T extends Serializable> implements Serializable {
        private long serialVersionUid = 10394L;
        private final URL url;
        private Class<T> pojoType;
        private Class editorType;
        Stub (PojoDataObject<T> dob, Class editorType) {
            FileObject ob = dob.getPrimaryFile();
            url = URLMapper.findURL(ob, URLMapper.INTERNAL);
            pojoType = dob.getPojoType();
            this.editorType = editorType;
        }
        
        @SuppressWarnings("unchecked")
        private PojoDataObject<T> getDataObject() throws IOException {
            FileObject ob = URLMapper.findFileObject(url);
            if (ob != null) {
                DataObject dob = DataObject.find(ob);
                if (dob instanceof PojoDataObject) {
                    PojoDataObject pdo = (PojoDataObject) dob;
                    if (pojoType.equals(pdo.getPojoType())) {
                        return (PojoDataObject<T>) pdo;
                    } else {
                        throw new IOException ("Incorrect Pojo" +
                                "type: " + pdo.getPojoType() + ". " +
                                "Expecting " + pojoType);
                    }
                } else {
                    throw new IOException ("Not a " +
                            "PojoDataObject: " + dob);
                }
            }
            return null;
        }
 
        public Object readResolve() {
            try {
                //XXX not sure if this is a good idea
                PojoDataObject<T> ob = getDataObject();
                PojoEditor ed = (PojoEditor) editorType.getConstructor(ob.getClass()).newInstance(ob);
                return ed;
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
                return null;
            }
        }
    }
    
    
    private void updateDisplayName () {
        DataObject dataObj = getDataObject();
        String displayName;
        if (dataObj != null && dataObj.isModified()) {
            displayName = NbBundle.getMessage (PojoEditor.class, 
                    "LBL_MODIFIED", dataObj.getName()); //NOI18N
        } else {
            displayName = dataObj == null ? "" : dataObj.getName();
        }

        final String theName = displayName;
        
        // ensure we're called always from the EDT.  This code is run from anywhere 
        // CarDataObject.setModified is called, which is potentially run from outside 
        // the EDT, as is exactly the case in the SaveCookie, which uses an AtomicAction
        Mutex.EVENT.readAccess(new Runnable() {
            @Override
            public void run() {
                setDisplayName(theName);
            }
        });
    }


    private class ModificationListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (DataObject.PROP_MODIFIED.equals(e.getPropertyName())) {
                updateDisplayName();
            } else if (DataObject.PROP_VALID.equals(e.getPropertyName())) {
                if (!dataObject.isValid()) {
                    if (pojo != null) {
                        clear(pojo);
                    }
                    close();
                }
            }
        }
    }
    
    private class NL implements NodeListener {

        public void childrenAdded(NodeMemberEvent ev) {
            
        }

        public void childrenRemoved(NodeMemberEvent ev) {
            
        }

        public void childrenReordered(NodeReorderEvent ev) {
            
        }

        public void nodeDestroyed(NodeEvent ev) {
            close();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            
        }
        
    }
}
