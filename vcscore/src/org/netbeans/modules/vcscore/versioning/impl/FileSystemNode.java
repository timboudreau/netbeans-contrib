/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import java.beans.BeanInfo;
import java.awt.Image;

import org.openide.nodes.Sheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

import org.openide.DialogDisplayer;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;


/**
 * Special node for filesystem (root folder). Takes icon from the
 * filsystem. This class was copied from the core.
 * It adds actions, cookies, icon and display name based on background file.
 * Property sheet and customizer is based on background filesystem.
 * It does not support any clipboard operations.
 *
 * @author Jaroslav Tulach, Martin Entlicher
 */
final class FileSystemNode extends AbstractNode implements java.beans.PropertyChangeListener {

    /** generated Serialized Version UID */
    static final long serialVersionUID = 1742510847721720990L;

    /** message that gives name to the root */
    private static java.text.MessageFormat formatRoot;

    private final FileObject root;

    /**
    * @param root folder to work on
    */
    public FileSystemNode(FileObject root) {
        // TODO I'm hiding here a bug in FS, it wrongly works over deleted roots and shows random files!
        // visible after deserialization of old setting that used already deleted folders
        super(FileUtil.toFile(root).exists() ? new FolderChildren(root) : Children.LEAF);
        this.root = root;
        init();
    }

    public Cookie getCookie(Class type) {
        // mimics DataNode because some actions heavily depends on DataObject cookie existence
        if (type.isAssignableFrom(DataObject.class) || type.isAssignableFrom(DataFolder.class)) {
            try {
                return DataObject.find(root);
            } catch (DataObjectNotFoundException e) {
                // ignore, call super later on
            }
        }
        return super.getCookie(type);
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    /** Adds properties from customize sheet and for sorting.
    * @return the property sheet
    */        
    protected Sheet createSheet () {
        Sheet s = super.createSheet ();

        s.remove(Sheet.PROPERTIES);

        try {
            BeanInfo bi = java.beans.Introspector.getBeanInfo (fileSystem().getClass ());
            org.openide.nodes.BeanNode.Descriptor d = org.openide.nodes.BeanNode.computeProperties (fileSystem(), bi);

            Sheet.Set set = Sheet.createPropertiesSet ();
            set.put (d.property);
            s.put (set);

            if (d.expert != null) {
                set = Sheet.createExpertSet ();
                set.put (d.expert);
                s.put (set);
            }
        } catch (java.beans.IntrospectionException ex) {
            org.openide.ErrorManager.getDefault ().notify (
                org.openide.ErrorManager.INFORMATIONAL, ex
            );
        }

        return s;
    }
    
    /** Name of the node.
    */
    public String getName () {
        return fileSystem() == null ? "" : fileSystem().getSystemName (); // NOI18N
    }
    
    public String getShortDescription() {
        return fileSystem() == null ? "" : fileSystem().getDisplayName(); // NOI18N
    }

    /** initiates node */
    void init() {
        fileSystem().addPropertyChangeListener(org.openide.util.WeakListeners.propertyChange(this, fileSystem()));

        setIconBase ("org/netbeans/modules/vcscore/versioning/impl/defaultFS"); // NOI18N
        formatRoot = new java.text.MessageFormat ("{0}"); // NOI18N
        initDisplayName ();
    }
    
    /** Initializes display name.
    */
    void initDisplayName () {
        String s = formatRoot.format (
                       new Object[] {fileSystem().getDisplayName (), fileSystem().getSystemName ()}
                   );
        // TODO distinquish invalid FSs after external deletion
        setDisplayName (s);
    }

    /** Finds an icon for this node. The filesystem's icon is returned.
    * @see java.bean.BeanInfo
    * @see org.openide.filesystems.FileSystem#getIcon
    * @param type constants from <CODE>java.bean.BeanInfo</CODE>
    * @return icon to use to represent the bean
    */
    public Image getIcon (int type) {
        BeanInfo bi;
        try {
            bi = org.openide.util.Utilities.getBeanInfo(fileSystem().getClass());
        } catch (java.beans.IntrospectionException e) {
            return super.getIcon(type);
        }
        Image icon =  bi.getIcon(type);
        if (icon == null) {
            icon = (Image) getFSMethodValue("getFSIcon", type);
        }
        return icon==null ? super.getIcon(type) : icon;
    }

    private Object getFSMethodValue(String name, int i) {
        Object value = null;
        try {
            java.lang.reflect.Method getFsMethod = fileSystem().getClass().getMethod(name, new Class[] { Integer.TYPE });
            value = getFsMethod.invoke(fileSystem(), new Object[] { new Integer(i) });
        } catch (NoSuchMethodException nsmex) {
        } catch (SecurityException sex) {
        } catch (IllegalAccessException iaex) {
        } catch (IllegalArgumentException iarex) {
        } catch (java.lang.reflect.InvocationTargetException itex) {
        }
        return value;
    }

    /** The DataFolderRoot's opened icon is the same as the closed one.
    * @return icon to use to represent the bean when opened
    */
    public Image getOpenedIcon (int type) {
        return getIcon(type);
    }

    /** @return the system actions for the root folder */
    public SystemAction[] createActions() {
        return FolderNode.getFolderActions();
    }

    public boolean canCopy() {
        return false;
    }

    public boolean canCut() {
        return false;
    }

    public boolean canDestroy() {
        return false;
    }

    public boolean canRename() {
        return false;
    }    

    /** Customizer to customize file system.
    */
    public boolean hasCustomizer () {
        try {
            BeanInfo info = java.beans.Introspector.getBeanInfo (fileSystem().getClass ());
            if (info.getBeanDescriptor ().getCustomizerClass () != null) return true;
        } catch (java.beans.IntrospectionException ex) {
            //return false;
        }
        return false;
        //return getFSMethodValue("getFSCustomizer") != null;
    }

    /** Property sheet with file system.
    */
    public java.awt.Component getCustomizer () {
        Class c = null;
        try {
            BeanInfo info = java.beans.Introspector.getBeanInfo (fileSystem().getClass ());
            c = info.getBeanDescriptor ().getCustomizerClass ();
        } catch (java.beans.IntrospectionException ex) {}
        if (c == null) return null;
        Object ret;
        try {
            ret = c.newInstance ();
            if (ret instanceof java.beans.Customizer) {
                java.beans.Customizer cust = (java.beans.Customizer)ret;
                cust.setObject (fileSystem());
            }

            if (ret instanceof java.awt.Component) {
                return (java.awt.Component)ret;
            }
            
            if (ret instanceof org.openide.DialogDescriptor) {
                org.openide.DialogDescriptor dd = (org.openide.DialogDescriptor)ret;
                return DialogDisplayer.getDefault ().createDialog (dd);
            }
        } catch (InstantiationException iex) {
            org.openide.ErrorManager.getDefault ().notify (iex);
        } catch (IllegalAccessException iaex) {
            org.openide.ErrorManager.getDefault ().notify (iaex);
        }

        return null;
    }


    /** deserializes object */
    private void readObject(java.io.ObjectInputStream is)
    throws java.io.IOException, ClassNotFoundException {
        is.defaultReadObject(); // df
        init();
    }

    /** Checks whether the name of FS is changed and if so it updates
    * the display name.
    */
    public void propertyChange(java.beans.PropertyChangeEvent ev) {
        if (org.openide.filesystems.FileSystem.PROP_SYSTEM_NAME.equals(ev.getPropertyName()) ||
            org.openide.filesystems.FileSystem.PROP_DISPLAY_NAME.equals(ev.getPropertyName())) {
            initDisplayName ();
        }
    }

    /** @return wrapped filesystem */
    private FileSystem fileSystem() {
        try {
            return root.getFileSystem();
        } catch (org.openide.filesystems.FileStateInvalidException ex) {
            return null;
        }
    }
}
