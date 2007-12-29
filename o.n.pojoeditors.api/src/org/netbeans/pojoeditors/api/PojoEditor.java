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
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.objectloader.ObjectLoader;
import org.netbeans.api.objectloader.ObjectReceiver;
import org.netbeans.pojoeditors.api.EditorFactory.Kind;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.SaveAction;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
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
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.WindowManager;

/**
 * Base class for TopComponents that are editors over PojoDataObjects.  Handles
 * the bookkeeping of the set of open editors, etc.
 * <p/>
 * If writeReplace is not overridden, the default serialization code will expect
 * that this editor has a public constructor that takes an argument of
 * PojoDataObject&lt;T&gt;.
 * <p/>
 * Unless you override writeExternal() and readResolve(), any subclass should
 * have two constructors:  One that takes an instance of the exact type of 
 * DataObject it edits, and one that takes a String (for the case that a 
 * file was moved or deleted).
 * <p/>
 * Instances of PojoEditor already have their layout manager set to BorderLayout
 * and expect it to remain so.  The typical use case is to create a panel for
 * actual editing of the object in question;  it will automatically be added
 * if returned from createEditorUI().
 * 
 * @author Tim Boudreau
 */
public abstract class PojoEditor<T extends Serializable> extends CloneableTopComponent implements ExplorerManager.Provider {
    private PojoDataObject<T> dataObject;
    private final ModificationListener modListener = new ModificationListener();
    private final EditorFactory.Kind kind;
    private final NodeListener nodeListener = new NL();
    private final ExplorerManager mgr = new ExplorerManager();
    protected PojoEditor (PojoDataObject<T> dataObject, EditorFactory.Kind kind) {
        this.kind = kind;
        if (dataObject != null) { //if null, could not deserialize
            ProxyLookup lkp = new ProxyLookup (
                    dataObject.getLookup(), 
                    createLookup(), 
                    Lookups.fixed(dataObject, dataObject.getNodeDelegate()));
            associateLookup (lkp);
            setDisplayName (dataObject.getName());
        }
        mgr.setRootContext(dataObject.getNodeDelegate());
        init (dataObject);
        setLayout (new BorderLayout());
        add (new ProgressPanel(dataObject.getPrimaryFile().getPath()), 
                BorderLayout.CENTER);
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, mask), 
                "save");
        getActionMap().put("save", 
                SystemAction.get(SaveAction.class));
    }
    
    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    /**
     * Overridden to send focus to the return value of
     * getInitialFocusComponent().
     */
    @Override
    public final void requestFocus() {
        super.requestFocus();
        Component c = getInitialFocusComponent();
        if (c != null && c.isDisplayable()) {
            c.requestFocus();
        }
    }
    
    /**
     * Overridden to send focus to the return value of
     * getInitialFocusComponent().
     */
    public final boolean requestFocusInWindow () {
        boolean result = super.requestFocusInWindow();
        Component c = getInitialFocusComponent();
        if (c != null && c.isDisplayable()) {
            result = c.requestFocusInWindow();
        }
        return result;
    }
    
    /**
     * Overridden to send focus to the return value of
     * getInitialFocusComponent().
     */
    @Override
    public final boolean requestFocus (boolean tranzient) {
        boolean result = super.requestFocus(tranzient);
        Component c = getInitialFocusComponent();
        if (c != null && c.isDisplayable()) {
            if (c instanceof JComponent) {
                result = ((JComponent) c).requestFocus(tranzient);
            } else {
                c.requestFocus();
                result = true;
            }
        }
        return result;
    }
    
    Kind getKind() {
        return kind;
    }

    /**
     * Get the component that should be focused when this TopComponent
     * has requestFocus() called on it
     * 
     * @return A component or null if none
     */
    protected Component getInitialFocusComponent() {
        return null;
    }
        
    private void init(PojoDataObject<T> dataObject) {
        this.dataObject = dataObject;
        setActivatedNodes(new Node[] { dataObject.getNodeDelegate()});
        updateDisplayName();
    }
    
    /**
     * Create a custom lookup for this component.  Note this method is called
     * in the superclass constructor, so be careful about relying on instance
     * fields being initialized.
     * <p/>
     * The default implementation returns an empty lookup.
     * 
     * @return A lookup
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
        if (dataObject != null) {
            dataObject.editorOpened(this);
            dataObject.addPropertyChangeListener(modListener);
            dataObject.getNodeDelegate().addNodeListener(nodeListener);
            dataObject.addChangeListener(cl);
            dataObject.addPojoPropertyChangeListener(pojoListener);
            onOpen();
            if (pojo == null) {
                System.err.println("Invoking load");
                load();
            } else {
                System.err.println("Pojo already loaded");
            }
        }
    }
    
    @Override
    protected final void componentClosed() {
        assert EventQueue.isDispatchThread();
        if (dataObject != null) {
            dataObject.editorClosed(this);
            dataObject.removePropertyChangeListener(modListener);
            dataObject.getNodeDelegate().removeNodeListener(nodeListener);
            dataObject.removeChangeListener(cl);
            onClose();
            if (pojo != null && dataObject != null) {
                clear(pojo);
            }
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
    
    protected void onEditorAdded (Component editor) {
        //do nothing
    }
    
    private void setCenterComponent (Component c) {
        removeAll();
        add (c, BorderLayout.CENTER);
        invalidate();
        revalidate();
        repaint();
    }
    
    private T pojo;
    protected final void set (T pojo) {
        if (pojo == null) {
            throw new NullPointerException ("Pojo is null");
        }
        System.err.println("Set " + pojo);
        this.pojo = pojo;
        if (getComponents().length == 0 || getComponents()[0].getClass() == ProgressPanel.class) {
            removeAll();
            Component editor = createEditorUI(pojo);
            setCenterComponent (editor);
            if (WindowManager.getDefault().getRegistry().getActivated() == this) {
                requestFocusInWindow();
            }
        }
        onSet (pojo);
    }
    
    protected void onSet (T pojo) {
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
    }
    
    protected void onLoadFailed (Exception e) {
    }
    
    private final Object loadLock = new Object();
    private ObjectReceiver<T> receiver;
    private void load() {
        if (getDataObject() == null) {
            setCenterComponent(new JLabel(NbBundle.getMessage(PojoEditor.class,
                    "LBL_NO_DOB")));
            return;
        }
        ObjectLoader<T> ldr = getLookup().lookup(ObjectLoader.class);
        if (ldr == null) {
            setCenterComponent(new JLabel(NbBundle.getMessage(PojoEditor.class,
                    "LBL_NO_LOADER", getDataObject().getPrimaryFile().getPath())));
            return;
        }
        synchronized (loadLock) {
            if (receiver != null) {
                //Already loading
                return;
            }
            receiver = new ObjectReceiver<T>() {
                public void setSynchronous(boolean val) {
                    if (!val && !EventQueue.isDispatchThread()) {
                        if (!(getComponents()[0] instanceof ProgressPanel)) {
                            try {
                                EventQueue.invokeAndWait(new Runnable() {
                                    public void run() {
                                        ProgressPanel pnl = new ProgressPanel(getDataObject().getPrimaryFile().getPath());
                                        setCenterComponent(pnl);
                                    }
                                });
                            } catch (InterruptedException ex) {
                                Exceptions.printStackTrace(ex);
                            } catch (InvocationTargetException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }

                public void received(T t) {
                    try {
                        set (t);
                    } finally {
                        done();
                    }
                }

                public void failed(Exception e) {
                    e.printStackTrace();
                    done();
                    removeAll();
                    setCenterComponent (new JLabel (e.getLocalizedMessage()));
                    onLoadFailed (e);
                }
                
                private void done() {
                    synchronized (loadLock) {
                        receiver = null;
                    }
                }

            };
        }
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
        return (dataObject == null || !dataObject.isValid()) ? PERSISTENCE_NEVER : 
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
        PojoDataObject dob = getDataObject();
        if (dob == null || !dob.getPrimaryFile().isValid()) {
            return null;
        } else {
            FileObject ob = dob.getPrimaryFile();
            String url = URLMapper.findURL(ob, URLMapper.INTERNAL).toString();
            return new Stub (url, getClass(), dob.getPojoType());
        }
    }
    
    private static final class Stub<T extends Serializable> implements Serializable {
        private long serialVersionUID = 10395L;
        private final String url;
        private Class<T> pojoType;
        private Class editorType;
        Stub (String url, Class editorType, Class<T> pojoType) {
            this.url = url;
            this.editorType = editorType;
            this.pojoType = pojoType;
        }
        
        @SuppressWarnings("unchecked")
        private PojoDataObject<T> getDataObject() throws IOException {
            URL theUrl;
            try {
                theUrl = new URL (this.url);
            } catch (MalformedURLException dead) {
                //Editor saved over a dead object
                return null;
            }
            FileObject file = URLMapper.findFileObject(theUrl);
            if (file != null) {
                DataObject dob = DataObject.find(file);
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
                PojoDataObject<T> ob = getDataObject();
                PojoEditor ed;
                if (ob != null) {
                    return editorType.getConstructor(ob.getClass()).newInstance(ob);
                } else {
                    return null;
                }
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
    
    private static final class ProgressPanel extends JPanel {
        ProgressPanel(String path) {
            setLayout(new FlowLayout());
            JProgressBar progress = new JProgressBar();
            add (progress);
            progress.setIndeterminate(true);
            JLabel lbl = new JLabel (NbBundle.getMessage(PojoEditor.class, 
                    "Loading", path));
            add (lbl);
        }
    }
    
    final CL cl = new CL();
    private final class CL implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            //discard modifications was called - reload
            removeAll();
            PojoDataObject dob = getDataObject();
            if (dob == null || !dob.isValid()) {
                add (new JLabel(NbBundle.getMessage(PojoEditor.class, "LBL_Dead")));
                invalidate();
                revalidate();
                repaint();
                dataObject = null;
            } else {
                add (new ProgressPanel(dob.getPrimaryFile().getPath()));
                invalidate();
                revalidate();
                repaint();
                load();
            }
        }
    }
    
    private final PojoListener pojoListener = new PojoListener();
    private final class PojoListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            pojoChanged ((T) evt.getSource(), evt.getPropertyName(), 
                    evt.getOldValue(), evt.getNewValue());
        }
    }
    
    protected void pojoChanged(T source, String propertyName, Object oldValue, Object newValue) {
        
    }
}
