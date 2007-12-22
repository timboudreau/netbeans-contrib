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
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Tim Boudreau
 */
public class PojoDataObjectTest extends NbTestCase {
    
    public PojoDataObjectTest(String testName) {
        super(testName);
    }            

    FileObject oneFile;
    @Override
    protected void setUp() throws Exception {
        MockServices.setServices(Rep.class, DL.class);
        FileSystem fs = FileUtil.createMemoryFileSystem();
        Repository.getDefault().addFileSystem(fs);
        FileUtil.setMIMEType("pojo", "application/x-pojo");
        Pojo one = new Pojo("one");
        oneFile = fs.getRoot().createData("one.pojo");
        FileLock lock = oneFile.lock();
        ObjectOutputStream out = new ObjectOutputStream(oneFile.getOutputStream(lock));
        try {
            out.writeObject(one);
        } finally {
            out.close();
            lock.releaseLock();
        }
    }
    
    public void testLoadDataObject() throws Exception {
        DataObject ob = DataObject.find (oneFile);
        assertEquals (PojoDob.class, ob.getClass());
        Pojo pojo = ob.getLookup().lookup(Pojo.class);
        assertNotNull (pojo);
        Node nd = ob.getNodeDelegate();
        assertEquals (PojoNd.class, nd.getClass());
    }
    
    public void testModification() throws Exception {
        DataObject ob = DataObject.find (oneFile);
        Pojo pojo = ob.getLookup().lookup(Pojo.class);
        assertNotNull (pojo);
        assertFalse (ob.isModified());
        SaveCookie ck = ob.getLookup().lookup(SaveCookie.class);
        assertNull (ck);
        class PCL implements PropertyChangeListener, LookupListener {
            private boolean resultChanged;
            boolean fired = false;
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(DataObject.PROP_MODIFIED)) {
                    fired = true;
                }
            }
            
            public void assertFired() {
                boolean old = fired;
                fired = false;
                assertTrue (old);
            }
            
            public void assertNotFired() {
                assertFalse (fired);
            }

            public void resultChanged(LookupEvent ev) {
                resultChanged = true;
            }
            
            public void assertResultChanged() {
                boolean old = resultChanged;
                resultChanged = false;
                assertTrue (resultChanged);
            }
            
            public void assertResultNotChanged() {
                assertFalse (resultChanged);
            }
        }
        PCL pcl = new PCL();
        ob.addPropertyChangeListener(pcl);
        Lookup.Result<SaveCookie> res = ob.getLookup().lookupResult (SaveCookie.class);
        res.addLookupListener(pcl);
        res.allInstances();
        pcl.assertNotFired();
        pcl.assertResultNotChanged();
        
        pojo.setName("foo");
        
        assertTrue (ob.isModified());
        pcl.assertFired();
        ck = ob.getLookup().lookup(SaveCookie.class);
        assertNotNull (ck);
        pcl.assertResultChanged();
        
        ck.save();
        pcl.assertResultChanged();
        ck = ob.getLookup().lookup(SaveCookie.class);
        assertNull (ck);
        
    }
    
/*
    public void testGetPojoType() {
    }

    public void testGetDefaultOpenAction() {
    }

    public void testGetOpenActions() {
    }

    public void testCreateNodeDelegate() {
    }

    public void testCreateNode() {
    }

    public void testGetCookie() {
    }

    public void testGetLookup() {
    }

    public void testGetInitialLookupContents() {
    }

    public void testEditorOpened() {
    }

    public void testEditorClosed() {
    }

    public void testGetOpenEditorCount() {
    }

    public void testDisposePojo() {
    }

    public void testOnDispose() {
    }

    public void testDoLoad() throws Exception {
    }

    public void testLoad() throws Exception {
    }

    public void testIsDeleteAllowed() {
    }

    public void testIsCopyAllowed() {
    }

    public void testIsMoveAllowed() {
    }

    public void testIsRenameAllowed() {
    }

    public void testGetHelpCtx() {
    }

    public void testHandleCopy() throws Exception {
    }

    public void testHandleDelete() throws Exception {
    }

    public void testHandleRename() throws Exception {
    }

    public void testHandleMove() throws Exception {
    }

    public void testHandleCreateFromTemplate() throws Exception {
    }

    public void testGetPojo() {
    }
 */ 

    public static final class DL extends DataLoader {
        
        public DL() {
            super ("org.netbeans.pojoeditors.api.PojoDataObjectTest$PojoDob");
        }
        
        @Override
        protected DataObject handleFindDataObject(FileObject fo, RecognizedFiles recognized) throws IOException {
            if ("pojo".equals(fo.getExt())) {
                recognized.markRecognized(fo);
                return new PojoDob (fo, this);
            }
            return null;
        }
    }
    
    public static final class Pojo implements Serializable {
        private String name;
        private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
        public Pojo (String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String toString() {
            return super.toString() + "[" + getName() + "]";
        }
        
        public void setName(String nm) {
            String old = this.name;
            this.name = nm;
            System.err.println("SetName");
            if (!old.equals(nm)) {
                System.err.println("firing name change from " + old + " to " + nm);
                PropertyChangeEvent e = new PropertyChangeEvent (this, "name", 
                        old, nm);
                for (PropertyChangeListener l : listeners) {
                    l.propertyChange(e);
                }
            }
        }
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
            listeners.add (l);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
            listeners.remove (l);
        }
    }
    
    public static final class PojoDob extends PojoDataObject<Pojo> {
        public PojoDob (FileObject ob, DataLoader ldr) throws DataObjectExistsException {
            super (ob, ldr, Pojo.class, new EdFac());
        }

        @Override
        protected PojoDataNode createNode() {
            return new PojoNd ((PojoDob) getLookup().lookup(DataObject.class));
        }
        
    }
    
    public static final class PojoNd extends PojoDataNode {
        PojoNd(PojoDob dob) {
            super (dob, Children.LEAF);
            setDisplayName (dob.getPojo().getName());
        }
    }
    
    public static final class EdFac extends EditorFactory<Pojo> {

        @Override
        public PojoEditor<Pojo> create(PojoDataObject obj, Kind kind) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List supportedKinds() {
            return Arrays.<Kind>asList (Kind.OPEN);
        }

        @Override
        public Kind defaultKind() {
            return Kind.OPEN;
        }
    }
    
    public static final class Rep extends Repository {
        public Rep() {
            super (FileUtil.createMemoryFileSystem());
        }
    }
    
    /*
    private static DL LOADR = new DL();
    private static final class DLP extends DataLoaderPool {
        protected Enumeration<DataLoader> loaders() {
            return new Vector(Collections.singleton(LOADR)).elements();
        }

        Map <FileObject, PojoDob> objs = new HashMap <FileObject, PojoDob>();
        public DataObject findDataObject(FileObject fo) throws IOException {
            DataObject result;
            if ("pojo".equals (fo.getExt())) {
                result = objs.get (fo);
                if (result == null) {
                    result = new PojoDob (fo, LOADR);
                    objs.put (fo, (PojoDob)result);
                }
            } else {
                result = super.findDataObject(fo);
            }
            return result;
        }

        public DataObject findDataObject(FileObject fo, DataLoader.RecognizedFiles r) throws IOException {
            DataObject result;
            if ("pojo".equals (fo.getExt())) {
                result = objs.get (fo);
                if (result == null) {
                    result = new PojoDob (fo, LOADR);
                    objs.put (fo, (PojoDob)result);
                }
            } else {
                result = super.findDataObject(fo, r);
            }
            return result;
        }
    }
    */
}
