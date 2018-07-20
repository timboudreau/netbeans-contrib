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


package org.netbeans.modules.group;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/** Node representing one link. */
class LinkNode extends FilterNode implements PropertyChangeListener {

    /**
     * pattern for names of nodes&nbsp;- include names of linked objects
     *
     * @see  #displayFormatWOLink
     */
    private static MessageFormat displayFormatWLink;
    /**
     * pattern for names of nodes&nbsp;- names of linked objects missing
     *
     * @see  #displayFormatWLink
     */
    private static MessageFormat displayFormatWOLink;
    /** group this node's link pertains to */
    private final GroupShadow group;
    /**  */
    private String linkedFileName = null;
    /** data object (represented by a node) this link points to */
    private DataObject linkedDataObj = null;


    /**
     * Creates a new <code>LinkNode</code> pointing to a given node.
     *
     * @param  original  node to create a link to
     */
    public LinkNode(GroupShadow group, Node original) {
        super(original);
        
        this.group = group;

        linkedDataObj = (DataObject) original.getCookie(DataObject.class);
        if (linkedDataObj == null) {
            return; // should not happen
        }
        FileObject originalFO = linkedDataObj.getPrimaryFile();
        if (originalFO == null) {
            return; // should not happen
        }
        linkedFileName = GroupShadow.getLinkName(originalFO);

        linkedDataObj.addPropertyChangeListener(this);
    }


    /**
     * LinkNode can be always destroyed, when Group is also allow delete.
     *
     * @return <CODE>true</CODE> if Group allow delete.
     */
    public boolean canDestroy () {
        return group.isDeleteAllowed();
    }

    /** Destroys node. */
    public void destroy() throws IOException {
        linkedDataObj.removePropertyChangeListener(this);

        List list = group.readLinks();
        for (int i = 0; i < list.size(); i++) {
            String fileName = (String) list.get(i);
            if (fileName.equals(linkedFileName)) {
                list.remove(i);
                i--;
            }
        }
        group.writeLinks(list);
    }

    /**
     * @return true if the node can be renamed
     */
    public boolean canRename () {
        return super.canRename() && group.isRenameAllowed();
    }

    /**
     * @returns true if this object allows cutting.
     */
    public boolean canCut () {
        return super.canCut() && group.isMoveAllowed();
    }


    /**
     */
    public String getDisplayName() {
        String foldername;
        FileObject primary = linkedDataObj.getPrimaryFile();
        String fullname = primary.getPath();
        int index = fullname.lastIndexOf('/');
        if (index != -1) {
            foldername = fullname.substring(0, index + 1);
        } else {
            foldername = "";                                            //NOI18N
        }
        Object[] objs = new Object[] {linkedDataObj.getName(),
                                      fullname,
                                      foldername,
                                      linkedDataObj.getNodeDelegate()
                                            .getDisplayName()};

        if (group.showLinks) {
            if (displayFormatWLink == null) {
                String pattern = NbBundle.getMessage(
                        LinkNode.class,
                        "FMT_linkWithTarget");                          //NOI18N
                displayFormatWLink = new MessageFormat(pattern);
            }
            return displayFormatWLink.format(objs);
        } else {
            if (displayFormatWOLink == null) {
                String pattern = NbBundle.getMessage(
                        LinkNode.class,
                        "FMT_linkWithoutTarget");                       //NOI18N
                displayFormatWOLink = new MessageFormat(pattern);
            }
            return displayFormatWOLink.format(objs);
        }
    }

    /** Implements <code>PropertyChangeListener</code>. */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName == null) {
            return;
        }
        if (propertyName.equals(DataObject.PROP_PRIMARY_FILE)) {
            
            /* the linked DataObject's primary file has been renamed or moved */
            try {
                List fileNames = new ArrayList(group.readLinks());
                String newFileName = GroupShadow.getLinkName(
                        linkedDataObj.getPrimaryFile());

                for (ListIterator i = fileNames.listIterator(); i.hasNext(); ) {
                    String fileName = (String) i.next();
                    if (fileName.equals(linkedFileName)) {
                        i.set(newFileName);
                    }
                }
                linkedFileName = newFileName;
                group.writeLinks(fileNames);
                fireDisplayNameChange(null, null);
            } catch (IOException ex) {
                ErrorManager errMgr = ErrorManager.getDefault();
                String msg = NbBundle.getMessage(
                        LinkNode.class,
                        "MSG_Cannot_rename_link",                       //NOI18N
                        group.getPrimaryFile().getPath());
                errMgr.notify(ErrorManager.INFORMATIONAL,
                              errMgr.annotate(ex, msg));
            }
        } else if (propertyName.equals(DataObject.PROP_VALID)) {
            
            /* the linked DataObject might have been deleted */
            if (!linkedDataObj.isValid()) {
                linkedDataObj.removePropertyChangeListener(this);
                ((GroupNodeChildren) group.getNodeDelegate().getChildren())
                        .update();
            }
        }
    }
}
