/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.genericnavigator;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 * Root and child node classes for the nodes in the options dialog that represents
 * mime types and patterns.
 *
 * Note this class does not use DataObject/FilterNode for a reason - since we provide
 * proxy children that haven't been written to disk, it's actually easier and less
 * expensive to just work with FileObjects.
 *
 * @author Tim Boudreau
 */
public class PatternsRoot extends AbstractNode {
    private final FileObject rootFolder;
    public PatternsRoot(FileObject rootFolder) {
        super (new PatternsRootChildren(rootFolder));
        this.rootFolder = rootFolder;
    }

    Map <String, Set <PatternItemProvider>> getItemsToRemove() {
        Node[] nds = getChildren().getNodes(true);
        Map <String, Set <PatternItemProvider>> removes = new HashMap <String, Set <PatternItemProvider>> ();
        Set types = new HashSet();
        for (int i = 0; i < nds.length; i++) {
            PiFolderNode node = (PiFolderNode) nds[i];
            String mimetype = node.getName();
            types.add (mimetype);
            PiFolderChildren kids = (PiFolderChildren) node.getChildren();
            Set <PatternItemProvider> s = kids.getRemoved();
            for (PatternItemProvider ppp : s) {
                if (ppp == null) {
                    throw new NullPointerException ("Item null in " + s + " children of " + mimetype + " kids " + kids);
                }
            }
            if (!s.isEmpty()) {
                removes.put (mimetype, s);
            } else {
            }
        }
        return removes;
    }

    Map <String, Set <PatternItemProvider>>  getItemsToAdd() {
        Node[] nds = getChildren().getNodes(true);
        Map <String, Set <PatternItemProvider>> adds = new HashMap <String, Set <PatternItemProvider>> ();
        for (int i = 0; i < nds.length; i++) {
            PiFolderNode node = (PiFolderNode) nds[i];
            PiFolderChildren kids = (PiFolderChildren) node.getChildren();
            String mimetype = node.getName();
            Node[] ofType = kids.getNodes(true);
            Set <PatternItemProvider> toAdd = null;
            for (int j = 0; j < ofType.length; j++) {
                PiNode node1 = (PiNode) ofType[j];
                PatternItemProvider item = (PatternItemProvider)
                    node1.getLookup().lookup(PatternItemProvider.class);

                if (item.isVirtual()) {
                    if (toAdd == null) {
                        toAdd =new HashSet <PatternItemProvider> ();
                        adds.put (mimetype, toAdd);
                    }
                    toAdd.add (item);
                }
            }
        }
        return adds;
    }

    public void store() throws IOException {

        Map <String, Set <PatternItemProvider>> removes = getItemsToRemove();
        Map <String, Set <PatternItemProvider>> adds = getItemsToAdd();


        Set types = new HashSet();
        for (Iterator <String> i =removes.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            types.add (key);
            Set<PatternItemProvider> s = removes.get(key);
            for (Iterator <PatternItemProvider> it = s.iterator(); it.hasNext();) {
                PatternItemProvider p = it.next();
                if (!p.isVirtual()) {
                    try {
                        p.delete();
                    } catch (IOException ioe) {
                        ErrorManager.getDefault().notify (ioe);
                    }
                } else {
                    p.delete();
                }
            }
            PiFolderNode nd = (PiFolderNode) getChildren().findChild(key);
            if (nd.isEmpty()) {
                try {
                    nd.delete();
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify (ioe);
                }
            }
        }
        PatternsRootChildren prc = (PatternsRootChildren) getChildren();
        prc.store();
        types.addAll(adds.keySet());
        FileObject[] kids = rootFolder.getChildren();
        for (int i = 0; i < kids.length; i++) {
            FileObject fob = kids[i];
            FileObject[] kidKids = fob.getChildren();
            for (int j = 0; j < kidKids.length; j++) {
                FileObject realFolder = kidKids[j];
                String s = fob.getName() + '/' + realFolder.getName();
                if (!types.contains(s) && realFolder.getChildren().length == 0) {
                    realFolder.delete();
                    if (fob.getChildren().length == 0) {
                        fob.delete();
                    }
                }
            }
        }

        for (Iterator <String> i =adds.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            Set<PatternItemProvider> s = adds.get(key);
            for (Iterator<PatternItemProvider> it = s.iterator(); it.hasNext();) {
                PatternItemProvider p = it.next();
                PatternItem item = p.getPatternItem();
                try {
                    item.save (p.getMimeType(), p.getDisplayName());
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify (ioe);
                }
            }
        }
        setChildren (new PatternsRootChildren(rootFolder));
        PatternItem.changed();
    }

    static boolean log = false;
    public void add (PatternItem item, String mimetype) {
        PatternsRootChildren prc = (PatternsRootChildren) getChildren();
        PiFolderNode nd = (PiFolderNode) prc.findChild (mimetype);
        boolean willFireChildrenChange = nd == null;
        if (willFireChildrenChange) {
            SyntheticFolder fld = new SyntheticFolder (mimetype);
            fld.add (item);
            prc.add (fld);
        } else {
            PiFolderNode n = (PiFolderNode) prc.findChild(mimetype);
            PiFolderChildren ch = (PiFolderChildren) n.getChildren();
            n.add(item);
        }
        prc.addNotify();
    }

    public void remove (PatternItem item, String mimetype) {
        PatternsRootChildren prc = (PatternsRootChildren) getChildren();
        PiFolderNode n = (PiFolderNode) getChildren().findChild(mimetype);
        if (n != null) {
            boolean isEmpty = n.remove (item);
            if (isEmpty) {
                prc.remove(mimetype);
            }
        }
        prc.addNotify();
    }

    public void remove (PatternItemProvider item) {
        String mimetype = item.getMimeType();
        PatternsRootChildren prc = (PatternsRootChildren) getChildren();
        PiFolderNode n = (PiFolderNode) getChildren().findChild(mimetype);
        if (n != null) {
            boolean isEmpty = n.remove (item);

            if (isEmpty) {

                prc.remove(mimetype);
            }
        }
        prc.addNotify();
    }


    private static final class PatternsRootChildren extends Children.Keys implements Comparator {
        private FileObject root;
        public PatternsRootChildren (FileObject root) {
            this.root = root;
        }

        public void addNotify() {
            List <Object> l = new ArrayList <Object> ();
            FileObject[] kids = root.getChildren();
            for (int i = 0; i < kids.length; i++) {
                FileObject fob = kids[i];
                FileObject[] kidKids = fob.getChildren();
                for (int j = 0; j < kidKids.length; j++) {
                    FileObject realFolder = kidKids[j];
                    if (realFolder.getChildren().length > 0 && !removed.contains(realFolder)) {
                        l.add (realFolder);
                    }
                }
            }
            if (added != null) {
                Set <SyntheticFolder> reallyAdd = new HashSet <SyntheticFolder> (added.size());
                for (java.util.Iterator <SyntheticFolder> i = added.iterator(); i.hasNext();) {
                    SyntheticFolder fld = i.next();
                    if (!fld.isEmpty()) {
                        reallyAdd.add (fld);
                    }
                }
                l.addAll(reallyAdd);
            }
            for (Iterator<Object> it = l.iterator(); it.hasNext();) {
                Object object = it.next();
                if (object == null) {
                    throw new NullPointerException ("Null in list! " + l);
                }
            }
            l.removeAll (removed);
            Collections.sort (l, this);
            setKeys (l);
        }

        public void remove (String fld) {
            if (added != null) {
                for (Iterator<SyntheticFolder> i = added.iterator(); i.hasNext();) {
                    SyntheticFolder syntheticFolder = i.next();

                    if (syntheticFolder.toString().equals(fld)) {
                        removed.add (syntheticFolder);
                        break;
                    }
                }
            }
            FileObject[] kids = root.getChildren();
            for (int i = 0; i < kids.length; i++) {
                FileObject fob = kids[i];
                String s = fob.getName();
                FileObject[] kidKids = fob.getChildren();
                for (int j = 0; j < kidKids.length; j++) {
                    String type = s + '/' + kidKids[j].getName();
                    if (type.equals(fld)) {
                        removed.add (kidKids[j]);
                    }
                }
            }
        }

        public void store() throws IOException {
            for (Iterator i = removed.iterator(); i.hasNext();) {
                Object o = i.next();
                if (o instanceof FileObject) {
                    ((FileObject) o).delete();
                }
            }
        }

        public Set <SyntheticFolder> added;
        public boolean add (SyntheticFolder fld) {
            if (added == null) {
                added = new HashSet <SyntheticFolder> ();
            }
            removed.remove (fld);
            boolean result = added.add (fld);
            addNotify();
            return result;
        }

        private Set removed = new HashSet() {
            public boolean add (Object o) {
                if (o == null) {
                    throw new NullPointerException();
                }
                return super.add (o);
            }
        };

        protected Node[] createNodes(Object key) {
            Node result;
            if (key instanceof FileObject) {
                result = new PiFolderNode ((FileObject) key);
            } else {
                result = new PiFolderNode ((SyntheticFolder) key);
            }
            return new Node[] { result };
        }

        public int compare (Object x0, Object x1) {
            String xa, xb;
            xa = stringFor (x0);
            xb = stringFor (x1);
            return xa.compareTo (xb);
        }

        private String stringFor (Object o) {
            if (o instanceof FileObject) {
                FileObject fob = (FileObject) o;
                return fob.getParent().getName() + '/' + fob.getName();
            } else {
                return o.toString();
            }
        }
    }

    private static class SyntheticFolder {
        private final String mimetype;
        public SyntheticFolder (String mimetype) {
            this.mimetype = mimetype;
        }

        public String toString() {
            return mimetype;
        }

        private List <PatternItemProvider> items = Collections.synchronizedList (
                new ArrayList <PatternItemProvider> ());

        boolean add (PatternItem item) {
            PIP pip = new PIP (mimetype, item);
            boolean result = !items.contains(item);
            if (result) {
                items.add (pip);
            }
            return result;
        }

        void remove (PatternItem item) {
            items.remove (item);
        }

        List <PatternItemProvider> getChildren() {
            return items;
        }

        void fire() {
            if (l != null) l.stateChanged(new ChangeEvent(this));
        }

        boolean isEmpty() {
            return items.isEmpty();
        }

        private ChangeListener l;
        void setChangeListener (ChangeListener cl) {
            this.l = cl;
        }
    }

    private static class PiFolderNode extends AbstractNode {
        private final FileObject fld;
        private final SyntheticFolder sfld;
        public PiFolderNode (FileObject fld) {
            super (new PiFolderChildren (fld), Lookups.singleton (fld));
            this.fld = fld;
            sfld = ((PiFolderChildren) getChildren()).getWriteFolder();
            setName (fld.getParent().getName() + '/' + fld.getName()); //NOI18N
            setDisplayName (getName());
        }

        public PiFolderNode (SyntheticFolder sfld) {
            super (new PiFolderChildren (sfld), Lookups.singleton (sfld));
            this.sfld = sfld;
            fld = null;
            assert sfld.toString().length() > 0 : "Empty mime type";
            setDisplayName (sfld.toString());
            setName (sfld.toString());
        }

        public Image getIcon(int ignored) {
            Image fldImage;
            fldImage = (Image) UIManager.get("Nb.Explorer.Folder.icon"); //NOI18N
            return fldImage != null ? fldImage : super.getIcon(ignored);
        }

        public Image getOpenedIcon(int ignored) {
            return getIcon (ignored);
        }

        public String toString() {
            return "PiFolderNode " + getDisplayName();
        }

        public void add (PatternItem item) {

            PiFolderChildren pif = (PiFolderChildren) getChildren();
            boolean had = pif.hasAdded();
            pif.add (item);
            if (!had) {
                fireDisplayNameChange(null, null);
            }
        }

        public boolean isEmpty() {
            PiFolderChildren pif = (PiFolderChildren) getChildren();
            return pif.getKeys().size() == 0;
        }

        public void delete() throws IOException {
            PiFolderChildren pif = (PiFolderChildren) getChildren();
            List keys = pif.getKeys();
            if (!isEmpty()) {
                throw new IllegalStateException ("not empty: " + keys);
            }

            fld.delete();
        }

        public boolean remove (PatternItem pi) {
            PiFolderChildren ch = (PiFolderChildren) getChildren();

            Node[] nd = ch.getNodes(true);
            //Find the exact match - the one that holds the reference to the
            //file, so it can really be deleted
            PatternItemProvider item = null;
            for (int i = 0; i < nd.length; i++) {
                PatternItemProvider pip = (PatternItemProvider) nd[i].getLookup().lookup(
                        PatternItemProvider.class);
                if (pip != null && pip.getDisplayName().equals(pi.getDisplayName())) {
                    item = pip;
                }
            }
            if (item == null) {
                item = new PIP (getName(), pi);
            }
            boolean result = ch.remove(item);
            return result;
        }

        public boolean remove (PatternItemProvider prov) {
            PiFolderChildren ch = (PiFolderChildren) getChildren();
            return ch.remove (prov);
        }

        public String getHtmlDisplayName() {
            PiFolderChildren pif = (PiFolderChildren) getChildren();
            if (pif.hasAdded()) {
                return "<font color='#0000AA'><b>" + getDisplayName(); //NOI18N
            }
            return null;
        }
    }

    private static class PiFolderChildren extends Children.Keys implements Comparator {
        private final FileObject fld;
        private final SyntheticFolder sfld;
        private final String mimetype;
         PiFolderChildren (FileObject fld) {
             this.fld = fld;
             mimetype = fld.getParent().getName() + '/' + fld.getName();
             sfld = new SyntheticFolder (mimetype);
         }

         PiFolderChildren (SyntheticFolder sfld) {
             this.sfld = sfld;
             fld = null;
             mimetype = sfld.toString();
         }

         public SyntheticFolder getWriteFolder() {
             return sfld;
         }

         public boolean hasAdded() {
             return !sfld.isEmpty();
         }

         private Set <PatternItemProvider> removed = new HashSet <PatternItemProvider> ();
         public boolean remove (PatternItemProvider item) {
             if (item == null) {
                 throw new NullPointerException();
             }
             int keysLen = getKeys().size();
             removed.add (item);

             if (getKeys().size() != keysLen - 1) {
                 throw new IllegalStateException ("Not removed: " + item);
             }
             addNotify();
             return getKeys().isEmpty();
         }

         public Set <PatternItemProvider> getRemoved() {
             return removed;
         }

         public void addNotify() {
             setKeys (getKeys());
         }

         List getKeys() {
             if (fld == null) {
                 List <PatternItemProvider> kids = sfld.getChildren();
                 kids.removeAll (removed);
                 Collections.sort (kids, this);
                 return kids;
             } else {
                 if (sfld.isEmpty()) {
                     FileObject[] obs = fld.getChildren();
                     List <PatternItemProvider> keys = new ArrayList <PatternItemProvider> (obs.length);
                     for (int i = 0; i < obs.length; i++) {
                         keys.add (new PIP(mimetype, obs[i].getName(), obs[i]));
                     }
                     keys.removeAll (removed);
                     Collections.sort (keys, this);
                     return keys;
                 } else {
                     FileObject[] obs = fld.getChildren();
                     Set <PatternItemProvider> s = new HashSet <PatternItemProvider> (obs.length);
                     for (int i = 0; i < obs.length; i++) {
                         s.add (new PIP(mimetype, obs[i].getName(), obs[i]));
                     }
                     //remove all items that we have edited versions of
                     s.removeAll (sfld.getChildren());

                     List <PatternItemProvider>  toRemove = sfld.getChildren();
                     //add the edited versions
                     s.addAll(toRemove);

                     s.removeAll(removed);

                     PatternItemProvider[] pp = (PatternItemProvider[])
                         s.toArray(new PatternItemProvider[s.size()]);

                     Arrays.sort (pp, this);
                     return Arrays.asList(pp);
                 }
             }

         }

         public boolean add (PatternItem item) {
             boolean result = sfld.add(item);
             removed.remove (new PIP (mimetype, item));
             if (result) {
                 addNotify();
             }
             return result;
         }

         protected Node[] createNodes(Object key) {
             Node result = new PiNode ((PatternItemProvider) key);
             return new Node[] { result };
         }

         public int compare(Object x0, Object x1) {
             String ax = stringFor (x0);
             String bx = stringFor (x1);
             return ax.compareTo(bx);
         }

         private String stringFor (Object o) {
             return ((PatternItemProvider) o).getDisplayName();
         }
    }

    static final class PIP implements PatternItemProvider {
        private PatternItem item;
        private final String mimetype;
        public PIP (String mimetype, PatternItem item) {
            this.item = item;
            assert item != null;
            this.mimetype = mimetype;
            if (mimetype == null || item == null) {
                throw new NullPointerException();
            }
        }

        public String toString() {
            return "PIP@" + System.identityHashCode(this) + " virtual " + isVirtual() +
                    " displayname='" + getDisplayName() + "' mimetype='" + mimetype + "'" + " item='" + item + "'";
        }

        private String displayName;
        protected FileObject ob;
        public PIP (String mimetype, String displayName, FileObject ob) {
            this.displayName = displayName;
            this.ob = ob;
            this.mimetype = mimetype;
            if (ob == null || displayName == null || mimetype == null) {
                throw new NullPointerException();
            }
        }

        public void delete() throws IOException {
            if (ob == null) {
                throw new IOException ("Not backed by a file: " + getDisplayName());
            }
            ob.delete();
        }

        public String getMimeType() {
            return mimetype;
        }

        public PatternItem getPatternItem() {
            if (item == null) {
                try {
                    item = new PatternItem (DataObject.find (ob));
                } catch (DataObjectNotFoundException donfe) {
                    ErrorManager.getDefault().notify (donfe);
                }
            }
            return item;
        }

        String initialDisplayName;
        public String getDisplayName() {
            String result;
            if (item != null) {
                result = item.getDisplayName();
            } else {
                result = displayName;
            }
            if (initialDisplayName == null) {
                initialDisplayName = result;
            }
            return result;
        }

        public boolean isVirtual() {
            return ob == null;
        }

        public boolean equals (Object o) {
            getDisplayName();
            boolean result = false;
            if (o instanceof PatternItemProvider) {
                PatternItemProvider oth = (PatternItemProvider) o;
                String s = oth.getDisplayName();
                if (oth instanceof PIP) {
                    s = ((PIP) oth).initialDisplayName;
                }
                result = s.equals(initialDisplayName);
            }
            return result;
        }

        public int hashCode() {
            getDisplayName();
            return initialDisplayName.hashCode() * 31;
        }
    }

    private static class PiNode extends AbstractNode {
        public PiNode(PatternItemProvider p) {
            super (Children.LEAF, Lookups.fixed (new Object[] {p, p.getMimeType() }));
            setDisplayName (p.getDisplayName());
            setName (p.getDisplayName());
        }

        private PatternItemProvider getPIP() {
            return (PatternItemProvider) getLookup().lookup (PatternItemProvider.class);
        }

        public String getHtmlDisplayName() {
            return getPIP().isVirtual () ? "<b><font color='#0000AA'>" + getDisplayName() : null; //NOI18N
        }

        public String toString() {
            return super.toString() + ":" + getPIP();
        }
    }
}
