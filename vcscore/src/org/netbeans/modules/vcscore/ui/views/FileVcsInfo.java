/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;


import org.openide.filesystems.*;
import org.openide.cookies.*;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import java.beans.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;


/**
 * vcs output info generic container class. implements No.Cookie and is returned 
 * by all FileInfoNodes, that appear in the views..
 * @author  Milos Kleint
 */
public class FileVcsInfo extends Object implements Node.Cookie {

    
    /**
     * info type.
     */
    public static final String BLANK = "BLANK"; //NOI18N
    
    /**
     * Attribute name, used internally to create tree structure links..
     */
    public static final String PROPERTY_NODE_PATH = "NODE_PATH"; //NOI18N

    /**
     * fired property change when the filter changes..
     */
    public static final String PROPERTY_FILTER = "FILTER_UPDATED"; //NOI18N
    
    private static final String PROPERTY_CHILDREN = "Children.Keys Instance"; //NOI18N
    
    private File file;
    private transient WeakReference fileObject = null;
    private transient WeakReference versFileObject = null;
    private transient FileVcsInfoFactory.GeneralTypeInfo info;
    
    private String type;
    private java.util.Map attrMap;
    
    private boolean dumped;
    private File dumpFile;
    
    /** Utility field used by event firing mechanism. */
    private transient javax.swing.event.EventListenerList listenerList =  null;        
    
    /** Creates a new instance of FileVcsInfo - blank..*/
    public FileVcsInfo() {
        this(new File(""), BLANK, new FileVcsInfoChildren());
    }
    
    private FileVcsInfo(File file, Children children) {
        this.file = file;
        attrMap = new HashMap(6, (float)0.9);
        setAttribute(PROPERTY_CHILDREN, children);
        dumped = false;
    }
    
    
    public FileVcsInfo(File file, String type, Children children) {
        this(file, children);
        this.type = type;
    }

    public File getFile() {
        return file;
    }
    
    public Children getChildren() {
        return (Children)getAttribute(PROPERTY_CHILDREN);
    }

    protected void reviveDumpedIfNeccesary(String attrName) {
        if (dumped && info.isDumpAbleAttribute(attrName)) {
            //TODO
        }
    }

    /**
     * dumps some unnessesary attributes to file short-term, to be used 
     * when large amounts of data are to be displayed. NOT FINISHED.
     */
    public void dumpAttributesToDisk() {
        if (dumped) {
            return;
        }
        String[] attrs = info.getAttributesAllowedToDump();
        if (attrs != null) {
            ObjectOutputStream stream = null;
            try {
                dumpFile = File.createTempFile(getType(), "dmp");  //NOI18N
                dumpFile.deleteOnExit();
                stream = new ObjectOutputStream(new FileOutputStream(dumpFile));
                for (int i = 0; i < attrs.length; i++) {
                    Object value = getAttribute(attrs[i]);
                    if (value instanceof Composite) {
                        Composite comp = (Composite)value;
                        comp.dumpAllRows(this, stream);
                    } else {
                        stream.writeUTF(attrs[i]);
                        stream.writeObject(value);
                    }
                }
                for (int i = 0; i < attrs.length; i++) {
                    attrMap.remove(attrs[i]);
                }
                dumped = true;
            } catch (IOException exc) {
                dumped = false;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException exc) {
                        dumped = false;
                    }
                }
            }
        }
        
    }
    

    /**
     * Sets the filter on the children. recursively transmitted to all children objects.
     *
     */
    public void setChildrenFilter(ChildrenInfoFilter filter) {
        setChildrenFilter(filter, true);
    }
    
    private void setChildrenFilter(ChildrenInfoFilter filter, boolean recursive) {
        Children childs = getChildren();
        if (childs instanceof FileVcsInfoChildren) {
            FileVcsInfoChildren myChilds = (FileVcsInfoChildren)childs;
            Iterator it = myChilds.getAllKeys();
            while (it.hasNext()) {
                FileVcsInfo info = (FileVcsInfo)it.next();
                info.setChildrenFilter(filter);
            }
            myChilds.setChildrenNodesFilter(filter);
            firePropertyChangeListener(new PropertyChangeEvent(this, PROPERTY_FILTER, null, filter));
        }
    }
    
    /**
     * Replaces the children attribute with new instance. Is kind of hack since a
     * Children object can belong to just one node and if the node is removed/recreated during
     * filtering, it causes problems..
     */
    void replaceChildrenWithClone() {
        if (getChildren() instanceof FileVcsInfoChildren) {
            Children newChildren = (Children)((FileVcsInfoChildren)getChildren()).clone();
            setAttribute(PROPERTY_CHILDREN, newChildren);
        }
    }
    
    
    void setGeneralTypeInfo(FileVcsInfoFactory.GeneralTypeInfo info) {
        this.info = info;
    }

    /**
     * Will overwrite all attributes.. eg. also CHILDREN which effectively swaps all
     * Children nodes..
     * Not dump-safe..
     */
    void overwriteAttributesFrom(FileVcsInfo source) {
        //TODO - needs a mechanism to replace the attributes and copy children
        // items from source to here..
        Children childs = getChildren();
        attrMap.clear();
        attrMap = source.getMap();
        
    }

    /**
     * Not dump-safe.
     */
    Map getMap() {
        return attrMap;
    }
    
    public SystemAction[] getAdditionalActions() {
        if (info != null) {
            return info.getAdditionalActions();
        }
        return null;
    }
    
    /**
     * Sets attribute value.
     * Is dump-safe.
     */
    
    public void setAttribute(String attrName, Object value) {
        reviveDumpedIfNeccesary(attrName);
        attrMap.put(attrName, value);
        firePropertyChangeListener(new PropertyChangeEvent(this, attrName, null, value));
    }

    /**
     * Gets attribute value.
     * Is dump-safe.
     */
    
    public Object getAttribute(String attributeName) {
        reviveDumpedIfNeccesary(attributeName);
        return attrMap.get(attributeName);
    }

    /**
     * Gets attribute value converted to String.
     * Is dump-safe.
     */
    public String getAttributeNonNull(String attributeName) {
        reviveDumpedIfNeccesary(attributeName);
        Object obj = attrMap.get(attributeName);
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }
    
    public String getType() {
        return type;
    }


    /** Registers PropertyChangeListener to receive events.
     * @param listener The listener to register.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (listenerList == null ) {
            listenerList = new javax.swing.event.EventListenerList();
        }
        listenerList.add(java.beans.PropertyChangeListener.class, listener);
    }
    
    /** Removes PropertyChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        listenerList.remove(java.beans.PropertyChangeListener.class, listener);
    }
    
    /** Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void firePropertyChangeListener(java.beans.PropertyChangeEvent event) {
        if (listenerList == null) return;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==java.beans.PropertyChangeListener.class) {
                ((java.beans.PropertyChangeListener)listeners[i+1]).propertyChange(event);
            }
        }
    }
    
    /**
     * attribute value that includes a table with values cosisting of COmpositeItems.
     */
    
    public static class Composite {
        
        public static final String PROPERTY_SELECTED_ITEMS = "SelectedItems"; //NOI18N
        public static final String PROPERTY_ADDED_ITEM = "AddedItem"; //NOI18N
        
        static final String COMPOSITE_BLOCK_START = "COMPOSITE_DUMP_BLOCK_START"; //NOI18N
        static final String COMPOSITE_BLOCK_END = "COMPOSITE_DUMP_BLOCK_END"; //NOI18N
        
        private String type;
        
        private List list;
        private FileVcsInfo info;
        
        private CompositeItem[] selectedItems;

        /** Utility field used by event firing mechanism. */
        private javax.swing.event.EventListenerList listenerList =  null;        
        
        public Composite(String type) {
            list = new ArrayList();
            this.type = type;
            info = null;
        }
        
        public Composite(List list, String type) {
            this.type = type;
            this.list = new ArrayList(list);
        }

        void dumpAllRows(FileVcsInfo info, ObjectOutputStream stream) throws java.io.IOException {
            this.info = info;
            Iterator it = list.iterator();
            stream.writeUTF(getType());
            stream.writeObject(COMPOSITE_BLOCK_START);
            while (it.hasNext()) {
                stream.writeObject(it.next());
                it.remove();
            }
            stream.writeObject(COMPOSITE_BLOCK_END);
        }
        
        void reviveDumpedRows(ObjectOutputStream stream) throws java.io.IOException {
            //TODO
        }
    
        protected void reviveDumpedIfNeccesary() {
            if (info != null) {
                info.reviveDumpedIfNeccesary(getType());
            }
        }
        
        public void appendRow(CompositeItem item) {
            reviveDumpedIfNeccesary();
            list.add(item);
            firePropertyChangeListener(new PropertyChangeEvent(this, PROPERTY_ADDED_ITEM, null, item));
        }
        
        public String getType() {
            return type;
        }
        
        public CompositeItem getRow(int row) {
            reviveDumpedIfNeccesary();
            if (row >=0 && row < list.size()) {
                return (CompositeItem)list.get(row);
            }
            return null; 
        }
        
        public Object getItem(int row, String attributeName) {
            reviveDumpedIfNeccesary();
            if (row >=0 && row < list.size()) {
                CompositeItem item = (CompositeItem)list.get(row);
                return item.getAttribute(attributeName);
            }
            return null;
            
        }
        
        public int getCount() {
            reviveDumpedIfNeccesary();
            return list.size();
        }
        
        public void setSelectedItems(FileVcsInfo.CompositeItem[] items) {
            FileVcsInfo.CompositeItem[] oldItems = selectedItems;
            selectedItems = items;
            firePropertyChangeListener(new PropertyChangeEvent(this, PROPERTY_SELECTED_ITEMS, oldItems, selectedItems));
        }
        
        public CompositeItem[] getSelectedItems() {
            return selectedItems;
        }
        
        /** Registers PropertyChangeListener to receive events.
         * @param listener The listener to register.
         */
        public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
            if (listenerList == null ) {
                listenerList = new javax.swing.event.EventListenerList();
            }
            listenerList.add(java.beans.PropertyChangeListener.class, listener);
        }        
        
        /** Removes PropertyChangeListener from the list of listeners.
         * @param listener The listener to remove.
         */
        public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
            listenerList.remove(java.beans.PropertyChangeListener.class, listener);
        }
        
        /** Notifies all registered listeners about the event.
         *
         * @param event The event to be fired
         */
        private void firePropertyChangeListener(java.beans.PropertyChangeEvent event) {
            if (listenerList == null) return;
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==java.beans.PropertyChangeListener.class) {
                    ((java.beans.PropertyChangeListener)listeners[i+1]).propertyChange(event);
                }
            }
        }
    }
    

    /**
     * Items that the Composite consists of.
     * 
     */
    
    public static class CompositeItem  implements Serializable {
        static final long serialVersionUID = 0;
        
        private java.util.Map attrMap;
        
        public CompositeItem() {
            attrMap = new HashMap(5, (float)0.9);
        }
        public void setAttribute(String attrName, Object value) {
            attrMap.put(attrName, value);
        }
    
        public Object getAttribute(String attributeName) {
            return attrMap.get(attributeName);
        }
        public String getAttributeNonNull(String attributeName) {
            Object obj = attrMap.get(attributeName);
            if (obj == null) {
                return "";
            }
            return obj.toString();
        }
        
    }
    
    
}
