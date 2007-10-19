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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URL;
import org.netbeans.pojoeditors.api.EditorFactory.Kind;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
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
public class PojoEditor<T extends Serializable> extends CloneableTopComponent {
    private PojoDataObject<T> dataObject;
    private final ModificationListener modListener = new ModificationListener();
    private final EditorFactory.Kind kind;
    protected PojoEditor (PojoDataObject<T> dataObject, EditorFactory.Kind kind) {
        this.kind = kind;
        init (dataObject);
        if (dataObject != null) { //if null, could not deserialize
            ProxyLookup lkp = new ProxyLookup (
                    dataObject.getLookup(), 
                    createLookup(), 
                    Lookups.singleton(dataObject.getNodeDelegate()));
            associateLookup (lkp);
        }
    }
    
    Kind getKind() {
        return kind;
    }
        
    private void init(PojoDataObject<T> dataObject) {
        this.dataObject = dataObject;
        dataObject.addPropertyChangeListener(WeakListeners.propertyChange(
                modListener, dataObject));
        setActivatedNodes(new Node[] { dataObject.getNodeDelegate()});
    }
    
    /**
     * Create a custom lookup for this component
     * @return
     */
    protected Lookup createLookup() {
        return Lookup.EMPTY;
    }
    
    /**
     * Convenience method for getting the pojo
     * 
     * @return the pojo
     */
    protected T getPojo() {
        return dataObject.getPojo();
    }

    @Override
    public boolean canClose() {
        assert EventQueue.isDispatchThread();
        if (dataObject.getOpenEditorCount() > 1) {
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
                dataObject.disposePojo();
            } else {
                return false;
            }
        }
        
        return true;
    }

    @Override
    protected final void componentOpened() {
        assert EventQueue.isDispatchThread();
        super.componentOpened();
        dataObject.editorOpened(this);
        dataObject.addPropertyChangeListener(modListener);
        onOpen();
    }
    
    @Override
    protected final void componentClosed() {
        assert EventQueue.isDispatchThread();
        dataObject.editorClosed(this);
        dataObject.removePropertyChangeListener(modListener);
        onClose();
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

    @Override
    public int getPersistenceType() {
        return !dataObject.isValid() ? PERSISTENCE_NEVER : 
            PERSISTENCE_ONLY_OPENED;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
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
                PojoEditor ed = (PojoEditor) editorType.getConstructor(PojoDataObject.class).newInstance(ob);
                return ed;
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
                return null;
            }
        }
    }
    
    private void updateDisplayName() {
        Mutex.EVENT.postReadRequest(new Runnable() {
            public void run() {
                String nm = dataObject.getName();
                if (dataObject.isModified()) {
                    nm += "*"; //XXX localize
                }
                setDisplayName (nm);
            }
        });
    }

    private class ModificationListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (DataObject.PROP_MODIFIED.equals(e.getPropertyName())) {
                updateDisplayName();
            } else if (DataObject.PROP_VALID.equals(e.getPropertyName())) {
                if (!dataObject.isValid()) {
                    close();
                }
            }
        }
    }
}
