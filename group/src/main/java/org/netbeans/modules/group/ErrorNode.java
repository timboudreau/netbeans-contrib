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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/** Node representing a broken link. */
class ErrorNode extends AbstractNode {

    /**
     * pattern for names of nodes&nbsp;- when expanded, contains the name
     * of a non-existing file (broken link)
     *
     * @see  #nodeNameSimple
     */
    private static MessageFormat nodeNameTemplate;
    /**
     * name of nodes whose target file name is unknown
     *
     * @see  #nodeNameTemplate
     */
    private static String nodeNameSimple;

    /** group this node's link pertains to */
    private final GroupShadow group;
    /** name of a non-existing file */
    private final String linkName;
    /**
     * list of possible parent folders of this node's broken link
     *
     * @see  #setFolderListening
     */
    private FileObject[] parentFolders;
    /**
     * <code>FileChangeListener</code> which listens for events
     * about new created files
     *
     * @see  #fileCreated
     */
    private FileChangeListener fileCreationListener;


    /** Creates a new <code>ErrorNode</code>. */
    public ErrorNode(GroupShadow group) {
        this(group, null);
    }

    /**
     * Creates a new <code>ErrorNode</code>.
     *
     * @param  linkName  broken link (name of a non-existing file)
     */
    public ErrorNode(GroupShadow group, String linkName) {
        super(Children.LEAF);

        this.group = group;
        this.linkName = linkName;

        systemActions = new SystemAction[] {
                            SystemAction.get(DeleteAction.class),
                            null,
                            SystemAction.get(ToolsAction.class),
                            SystemAction.get(PropertiesAction.class)
                        };

        if (linkName != null) {
            if (nodeNameTemplate == null) {
                nodeNameTemplate = new MessageFormat(NbBundle.getMessage(
                        ErrorNode.class,
                        "FMT_brokenLinkWithTarget"));                   //NOI18N
            }
            setDisplayName(nodeNameTemplate.format(new Object[] {linkName}));
            try {
                startFolderListening();
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            }
        } else {
            if (nodeNameSimple == null) {
                nodeNameSimple = NbBundle.getMessage(
                        ErrorNode.class,
                        "FMT_brokenLinkWithoutTarget");                 //NOI18N
            }
            setDisplayName(nodeNameSimple);
        }
    }

    /** Destroys node. */
    public void destroy() throws IOException {
        boolean modified = false;
        List list = new ArrayList(group.readLinks());
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            String name = (String) i.next();
            if (name.equals(this.linkName)) {
                i.remove();
                modified = true;
            }
        }
        if (modified) {
            group.writeLinks(list);
        }

        cancelFolderListening();
    }

    /**
     * @return  always <code>true</code>
     */
    public boolean canDestroy() {
        return true;
    }

    /**
     * Starts listening on all folders which could contain a file
     * matching the broken link's name, so that the broken link can
     * automatically become a regular link when such file appears.
     *
     * @see  #cancelFolderListening
     */
    private void startFolderListening() {
        // This link is invalid but its parent could be a valid folder.
        // So we could watch it creating a new file...
        int index = linkName.lastIndexOf('/');

        if (index > 0) {
            String folder = linkName.substring(0, index);
            DataObject dobj;
            try {
                dobj = GroupShadow.getDataObjectByName(folder);
            } catch (IOException ex) {
                return;
            }
            if (dobj == null) {
                return;
            }
            parentFolders = new FileObject[] {dobj.getPrimaryFile()};
        } else { // use filesystems' folders
            FileSystem[] fs = Repository.getDefault().toArray();
            parentFolders = new FileObject[fs.length];
            for (int i = 0; i < fs.length; i++) {
                parentFolders[i] = fs[i].getRoot();
            }
        }

        fileCreationListener = new FileChangeAdapter() {
                public void fileDataCreated(FileEvent fe) {
                    fileCreated(GroupShadow.getLinkName(fe.getFile()));
                }
            };
        for (int i = 0; i < parentFolders.length; i++) {
            parentFolders[i].addFileChangeListener(fileCreationListener);
        }
    }

    /**
     * Stops listening on folders which could contain a file matching
     * the broken link's name.
     *
     * @see  #startFolderListening
     */
    private void cancelFolderListening() {
        if (parentFolders != null) {
            for (int i = 0; i < parentFolders.length; i++) {
                parentFolders[i].removeFileChangeListener(fileCreationListener);
            }
            parentFolders = null;
            fileCreationListener = null;
        }
    }

    /**
     * Checks whether a given file name is equal to the name of this node's
     * broken links and updates the link if the names are equal.
     * <p>
     * This method is called when a file which could match this node's broken
     * link is created.
     *
     * @param  fileName  name of the file that was just created,
     *                   relative to the root of the filesystem
     * @see  #startFolderListening
     */
    private void fileCreated(String newFileName) {
        if (newFileName.equals(linkName)) {
            cancelFolderListening();
            GroupNodeChildren children
                    = (GroupNodeChildren) group.getNodeDelegate().getChildren();
            children.update();
        }
    }
    
}
