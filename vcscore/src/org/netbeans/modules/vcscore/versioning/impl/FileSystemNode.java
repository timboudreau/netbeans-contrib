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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.vcscore.versioning.impl;

import java.beans.BeanInfo;
import java.awt.Image;
import org.netbeans.modules.vcscore.util.VcsUtilities;

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
import org.netbeans.modules.vcscore.annotation.Icons;


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

    private FileObject root;

    /**
    * @param root folder to work on
    */
    public FileSystemNode(FileObject root) {
        super(getRootChildren(root));
        this.root = root;
        init();
    }
    
    private static final Children getRootChildren(FileObject root) {
        // TODO I'm hiding here a bug in FS, it wrongly works over deleted roots and shows random files!
        // visible after deserialization of old setting that used already deleted folders
        if (FileUtil.toFile(root).exists()) {
            return new FolderChildren(root);
        } else {
            return Children.LEAF;
        }
    }

    public Cookie getCookie(Class type) {
        // mimics DataNode because some actions heavily depends on DataObject cookie existence
        if (type.isAssignableFrom(DataObject.class) || type.isAssignableFrom(DataFolder.class)) {
            try {
                FileObject masterRoot = VcsUtilities.getMainFileObject(root);
                return DataObject.find(masterRoot);
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
    * @param type constants from <CODE>java.bean.BeanInfo</CODE>
    * @return icon to use to represent the bean
    */
    public Image getIcon (int type) {
        Image icon =  Icons.forFileSystem(fileSystem(), type);
        return icon==null ? super.getIcon(type) : icon;
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
        if (org.openide.filesystems.FileSystem.PROP_ROOT.equals(ev.getPropertyName())) {
            this.root = fileSystem().getRoot();
            setChildren(getRootChildren(root));
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
