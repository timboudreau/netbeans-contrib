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

import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;

/** Node for group shadow data object. */
public class GroupNode extends DataNode implements PropertyChangeListener {

    /** base name for this node's icons */
    static final String GS_ICON_BASE
            = "org/netbeans/modules/group/resources/groupShadow";       //NOI18N

    /** format for display name. */
    private static MessageFormat groupFormat;


    /**
     * Creates a folder node for a given group shadow.
     *
     * @param  group  group shadow to create a node for
     * @param  children  children to use for the node
     */
    public GroupNode(final GroupShadow group, Children children) {
        super(group, children);
        setIconBase(GS_ICON_BASE);
    }

    /**
     */
    public String getDisplayName() {
        if (groupFormat == null) {
            String fmtString = NbBundle.getMessage(
                    GroupNode.class,
                    "FMT_groupShadowName");                             //NOI18N
            groupFormat = new MessageFormat(fmtString);
        }

        String name = getName();
        DataObject dataObject = getDataObject();
        FileObject fileObject = dataObject.getPrimaryFile();
        String defaultDisplayName = super.getDisplayName();
        String displayName = groupFormat.format(
                new Object[] {name != null ? name : "",                 //NOI18N
                              "",                                       //NOI18N
                              fileObject.toString(),
                              "",                                       //NOI18N
                              defaultDisplayName != null
                                    ? defaultDisplayName
                                    : ""});                             //NOI18N
        try {
            displayName = fileObject.getFileSystem().getStatus().annotateName(
                    displayName,
                    dataObject.files());

        } catch (FileStateInvalidException e) {
            /* OK, so the display name will not be annotated */
        }
        return displayName;
        //      return super.getDisplayName() + GroupShadow.getLocalizedString("PROP_group"); // " (group)"; // NOI18N
    }

    /**
     * Returns a group this node represents.
     *
     * @return  <code>GroupShadow</code> represented by this node
     */
    private GroupShadow getGroup() {
        return (GroupShadow) getCookie(DataObject.class);
    }

    /**
     * Initializes a sheet of this node's properties.
     *
     * @return  the initialized sheet
     */
    protected Sheet createSheet() {
        Sheet s = super.createSheet();
        updateSheet(s);
        return s;
    }

    /** Listens to GroupShadow dataobject and updates Expert properties
     * list visibility (on "template" property change). */
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName == null) {
            return;
        }
        if (propertyName.equals(DataObject.PROP_TEMPLATE)) {
            Object oldValue = evt.getOldValue();
            Object newValue = evt.getNewValue();
            if (newValue != null && !newValue.equals(oldValue)) {
                updateSheet(getSheet());
            }
        } else if (propertyName.equals(GroupShadow.PROP_USE_PATTERN)) {
            updateSheet(getSheet());
        }
    }

    /** Conditionally fills the set */
    private void fillExpertSet(Sheet.Set set){
        final DataObject obj = getDataObject();
        Node.Property p;

        // put properties to set
        try {
            /*X
              p = new PropertySupport.Reflection (obj, Boolean.TYPE, "getShowLinks", "setShowLinks");
              p.setName(GroupShadow.PROP_SHOW_LINKS);
              p.setDisplayName(GroupShadow.getLocalizedString("PROP_showlinks"));
              p.setShortDescription(GroupShadow.getLocalizedString("HINT_showlinks"));
              ss.put(p);
            */
            if (obj.isTemplate()){
                
                String dispName;
                String descr;
                final boolean isReadOnly = obj.getPrimaryFile().isReadOnly();
                
                /* property Create All: */
                p = new PropertySupport.Reflection(
                        obj,
                        Boolean.TYPE, 
                        "getTemplateAll",                               //NOI18N
                        isReadOnly ? null : "setTemplateAll");          //NOI18N
                p.setName(GroupShadow.PROP_TEMPLATE_ALL);
                p.setDisplayName(NbBundle.getMessage(
                        GroupNode.class,
                        "PROP_templateall"));                           //NOI18N
                p.setShortDescription(NbBundle.getMessage(
                        GroupNode.class,
                        "HINT_templateall"));                           //NOI18N
                set.put(p);

                /* property Naming Pattern: */
                class TemplatePatternProperty
                        extends PropertySupport.Reflection {
                    public TemplatePatternProperty(DataObject obj)
                            throws NoSuchMethodException {
                        super(obj, String.class,
                              "getTemplatePattern",                     //NOI18N
                              obj.getPrimaryFile().isReadOnly()
                                      ? null
                                      : "setTemplatePattern");          //NOI18N
                    }
                    public boolean canWrite() {
                        return GroupNode.this.getGroup().isUsePattern();
                    }
                }
                p = new TemplatePatternProperty(obj);
                p.setName(GroupShadow.PROP_TEMPLATE_PATTERN);
                p.setDisplayName(NbBundle.getMessage(
                        GroupNode.class,
                        "PROP_templatePattern"));                       //NOI18N
                p.setShortDescription(NbBundle.getMessage(
                        GroupNode.class,
                        "HINT_templatePattern"));                       //NOI18N
                set.put(p);

                /* property Use Naming Pattern: */
                p = new PropertySupport.Reflection(
                        obj,
                        Boolean.TYPE,
                        "isUsePattern",                                 //NOI18N
                        "setUsePattern");                               //NOI18N
                p.setName(GroupShadow.PROP_USE_PATTERN);
                p.setDisplayName(NbBundle.getMessage(
                        GroupNode.class,
                        "PROP_UsePattern"));                            //NOI18N
                p.setShortDescription(NbBundle.getMessage(
                        GroupNode.class,
                        "HINT_UsePattern"));                            //NOI18N
                set.put(p);
            }
        } catch (Exception ex) {
            throw new InternalError();
        }
    }

    /**
     * Creates, updates or removes an expert property set of this node's
     * property sheet, according to the current template status of the group
     * shadow.  If the group shadow is not a template, its expert property set
     * is removed (if it exists). If the group shadow is a templte, its
     * expert property sheet is updated (if it already exists) or created.
     *
     * @param  sheet  property sheet whose expert property set is to be
     *                updated
     */
    private void updateSheet(Sheet sheet){
        if (getDataObject().isTemplate()) {
            Sheet.Set set = sheet.get(Sheet.EXPERT);
            if (set == null) {
                set = Sheet.createExpertSet();
                fillExpertSet(set);
                sheet.put(set);
            } else {
                fillExpertSet(set);
            }
        } else {
            sheet.remove(Sheet.EXPERT);
        }
    }

    /** Augments the default behaviour to test for {@link NodeTransfer#nodeCutFlavor} and
     * {@link NodeTransfer#nodeCopyFlavor}
     * with the {@link DataObject}. If there is such a flavor then adds
     * the cut and copy flavors. Also, if there is a copy flavor and the
     * data object is a template, adds an instantiate flavor.
     *
     * @param  t  transferable to use
     * @param  s  list of {@link PasteType}s
     */
    protected void createPasteTypes(Transferable t, List s) {
        super.createPasteTypes(t, s);
        if (getDataObject().getPrimaryFile().isReadOnly()) {
            return;
        }

        DataObject objToPaste = null;

        /* Get the DataObject being dragged/pasted: */
        objToPaste = (DataObject) NodeTransfer.cookie(
                  t,
                  NodeTransfer.CLIPBOARD_COPY | NodeTransfer.CLIPBOARD_CUT,
                  DataObject.class);

        if (objToPaste != null && objToPaste.isCopyAllowed()) {
            // copy and cut
            s.add(new Paste("PT_copy", objToPaste, false));             //NOI18N
        }
    }


    /** Paste type for data objects. */
    private class Paste extends PasteType {
        
        /** resource bundle key of this paste type's name */
        private String nameBundleKey;
        /** data object to be pasted */
        private DataObject obj;
        /**
         * indicates whether the clipboard should be cleared after the paste
         * action is finished
         */
        private boolean clearClipboard;

        
        /**
         * Creates a paste type for a given data object.
         *
         * @param  nameBundleKey  resource bundle key of the paste type's name
         * @param  obj  data object to be pasted
         * @param  clear  <code>true</code> if the clipboard should be cleared
         *                after the paste action is finished,
         *                <code>false</code> otherwise
         */
        public Paste(String nameBundleKey, DataObject obj, boolean clear) {
            this.nameBundleKey = nameBundleKey;
            this.obj = obj;
            this.clearClipboard = clear;
        }

        
        /** */
        public String getName() {
            return NbBundle.getMessage(GroupNode.class, nameBundleKey);
        }

        /** */
        public final Transferable paste() throws IOException {
            handle(obj);
            return clearClipboard ? ExTransferable.EMPTY : null;
        }

        /**
         * Actually performs the paste action.
         *
         * @param  objToPaste  data object to be pasted to this node's
         *                     <code>GroupShadow</code>
         * @exception  java.io.IOException
         *             if an error occured while reading the current
         *             or writing a modified list of contained files' names
         */
        public void handle(DataObject objToPaste) throws IOException {
            List list = getGroup().readLinks();
            String name = GroupShadow.getLinkName(objToPaste.getPrimaryFile());
            if (!list.contains(name)) {
                list.add(name);
            }
            getGroup().writeLinks(list);
        }
    }

}
