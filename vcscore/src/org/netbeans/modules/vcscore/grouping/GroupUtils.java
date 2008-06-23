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

package org.netbeans.modules.vcscore.grouping;

/**
 *
 * @author  Milos Kleint
 */

import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.cookies.InstanceCookie;
import org.openide.*;
import org.openide.util.NbBundle;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem; // override java.io.FileSystem
import java.util.*;
import java.io.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDisplayer;

public class GroupUtils {

    /** Creates new GroupUtils */
    private GroupUtils() {
    }
    
    /**
     * Returns the root node of the vcs group structure.
     */
    public static MainVcsGroupNode getMainVcsGroupNodeInstance() {
        MainVcsGroupNode root = null;
        FileSystem defFs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject fo = defFs.findResource(MainVcsGroupNode.GROUPS_PATH + "/org-netbeans-modules-vcscore-grouping-MainVcsGroupNode.instance");//NOI18N
        if (fo != null) {
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                dobj = null;
            }
            if (dobj != null && dobj instanceof InstanceDataObject) {
                InstanceDataObject ido = (InstanceDataObject)dobj;
                InstanceCookie cook = (InstanceCookie)ido.getCookie(InstanceCookie.class);
                try {
                    root = (MainVcsGroupNode)cook.instanceCreate();//ido.getNodeDelegate();
                } catch (IOException ioExc) {
                } catch (ClassNotFoundException cnfExc) {}
            }
        }
        return root;
    }

    /**
     * returns the folder in the default filesystem where the groups data is stored.
     */
    public static DataFolder getMainVcsGroupFolder() {
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        DataFolder fold = null;
        try {
            fold = (DataFolder)DataObject.find(rootFo);
        } catch (DataObjectNotFoundException exc) {
            return null;
        }
        return fold;
        
    }    

    /**
     * Returns the node of the default group.
     */
    
    public static VcsGroupNode getDefaultGroupInstance() {
        MainVcsGroupNode node = getMainVcsGroupNodeInstance();
        MainVcsGroupChildren child = (MainVcsGroupChildren)node.getChildren();
        return child.getDefaultGroupNode();
    }  
    
    /**
     * Add the array of nodes to the default group..
     */
    public static void addToDefaultGroup(Node[] nodes) {
       VcsGroupNode node = GroupUtils.getDefaultGroupInstance(); 
       DataFolder fold = (DataFolder)node.getCookie(DataObject.class);
       if (fold != null) {
           addToGroup(fold, nodes);
       }
    }
    
    /**
     * Add the array of nodes to the specified group.
     */
    public static void addToGroup(DataFolder group, Node[] nodes) {
        List okFiles = new LinkedList();
        List badGroup = new LinkedList();
        for(int i = 0; i < nodes.length; i++) {
            //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null) {
                DataShadow shadow = findDOInGroups(dd);
                if (shadow != null) {
                    if (!group.equals(shadow.getFolder())) {
                        badGroup.add(shadow);
//                        System.out.println("already in another group " + shadow.getOriginal().getName());
                    }
                } else {
                        // add to only when not in the requested group already..
                    okFiles.add(dd);
                }
                
            }
        }
        if (badGroup.size() > 0) {
            NotifyDescriptor.Confirmation conf = new NotifyDescriptor.Confirmation(
                NbBundle.getBundle(GroupUtils.class).getString("AddToGroupAction.moveToGroupQuestion"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION);
            Object retValue = DialogDisplayer.getDefault().notify(conf);
            if (retValue.equals(NotifyDescriptor.CANCEL_OPTION)) {
                return;
            }
            if (retValue.equals(NotifyDescriptor.YES_OPTION)) {
                Iterator it = badGroup.iterator();
                while (it.hasNext()) {
                    DataShadow oldShadow = (DataShadow)it.next();
                    DataObject obj = oldShadow.getOriginal();
                    try {
                        oldShadow.delete();
                        obj.createShadow(group);
                    } catch (IOException exc) {
                        ErrorManager.getDefault().annotate(exc, NbBundle.getBundle(GroupUtils.class).getString("GroupUtils.Error.CannotAddToGroup"));
                    }
                }
            }
        }
        Iterator it = okFiles.iterator();
        while (it.hasNext()) {
            try {
                DataObject obj = (DataObject)it.next();
                //System.out.println("ADDING dataobject to group: "+obj+", primaryFile = "+obj.getPrimaryFile());
                DataShadow shadow = obj.createShadow(group);
                //System.out.println("  shadow = "+shadow);
            } catch (java.io.IOException exc) {
                ErrorManager.getDefault().annotate(exc, NbBundle.getBundle(GroupUtils.class).getString("GroupUtils.Error.CannotAddToGroup"));
            }
        }
    }    
    
    /**
     * the method checks if the specified dataobject is already 
     * in any of the groups. if so, returns the shadow data object.
     * Otherwise returns null
     */
    public static DataShadow findDOInGroups(DataObject dataObj) {
        FileObject file = dataObj.getPrimaryFile();
        FileObject[] originals = new FileObject[] { file };
        //VcsUtilities.convertFileObjects(originals);
        file = originals[0];
        //System.out.println("findDOInGroups("+dataObj+")");
        //System.out.println(" primaryFile = "+dataObj.getPrimaryFile());
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject rootFo = fs.findResource(MainVcsGroupNode.GROUPS_PATH);
        Enumeration en = rootFo.getData(true);
        while (en.hasMoreElements()) {
            FileObject fo = (FileObject)en.nextElement();
            try {
                DataObject dobj = DataObject.find(fo);
                //System.out.println("  dobj = "+dobj+", instanceof DataShadow = "+(dobj instanceof DataShadow));
                if (dobj instanceof DataShadow) {
                    DataShadow shadow = (DataShadow)dobj;
                    if (!shadow.getOriginal().isValid()) {
//                        System.out.println("original not valid.. deleting..");
                        try {
                            shadow.delete();
                        } catch (java.io.IOException exc) {}
                        continue;
                    }
                    //System.out.println("  original = "+shadow.getOriginal());
                    //System.out.println("  original's primaryFile = "+shadow.getOriginal().getPrimaryFile());
                    FileObject origFile = shadow.getOriginal().getPrimaryFile();
                    originals[0] = origFile;
                    //VcsUtilities.convertFileObjects(originals);
                    origFile = originals[0];
                    //System.out.println("  original's original primary file = "+origFile+", equals = "+origFile.equals(file));
                    if (origFile.equals(file)) {
                        return shadow;
                    }
                }
            } catch (DataObjectNotFoundException exc) {
            }
        }
        return null;
    }
}
