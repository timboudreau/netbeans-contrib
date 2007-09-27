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

package org.netbeans.modules.docbook;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import org.netbeans.api.docbook.MainFileProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.docbook.parsing.ParsingServiceImpl;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

class DocBookDataNode extends DataNode {
    private final InstanceContent content;
    public DocBookDataNode(DocBookDataObject obj, InstanceContent content) {
        super(obj, Children.LEAF, new AbstractLookup (content));
        OpenCookie oc = obj.getCookie(OpenCookie.class);
        assert oc != null : obj + " has no OpenCookie; DBES=" + obj.getCookie(DocBookEditorSupport.class);
        this.content = content;
        content.set(Arrays.asList(
                obj, new RendererImpl (obj),
                oc,
                new Notifier (obj),
                new ParsingServiceImpl (obj)
                ), null);
        SaveCookie ck = obj.getCookie(SaveCookie.class);
        if (ck != null) {
            content.add (ck);
        }
        pcl = new PCL();
        obj.addPropertyChangeListener(WeakListeners.propertyChange(pcl, obj));
        setIconBaseWithExtension("org/netbeans/modules/docbook/resources/docbook.png");
    }

    private PropertyChangeListener pcl;

    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            boolean ck = DataObject.PROP_COOKIE.equals(evt.getPropertyName());
            if (ck) {
                SaveCookie save = getDataObject().getCookie(SaveCookie.class);
                if (save == null) {
                    SaveCookie old = getLookup().lookup(SaveCookie.class);
                    if (old != null) {
                        content.remove(old);
                    }
                } else {
                    SaveCookie old = getLookup().lookup(SaveCookie.class);
                    if (old != save && old != null) {
                        content.remove(old);
                    }
                    content.add(save);
                }
            }
        }
    }

    public String getDisplayName() {
        String result = super.getDisplayName();
        FileObject ob = getDataObject().getPrimaryFile();
        try {
            String name = ob.getFileSystem().getStatus().annotateName(
                result, Collections.singleton(ob));
            return name;
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify (e);
            return result;
        }
    }

//    public Image getIcon(int type) {
//        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
//            Image result = Utilities.loadImage(
//                    "org/netbeans/modules/docbook/docbook.png", true); //NOI18N
//            FileObject ob = getDataObject().getPrimaryFile();
//            try {
//                result = ob.getFileSystem().getStatus().annotateIcon(result, type,
//                        Collections.singleton(ob));
//            } catch (FileStateInvalidException ex) {
//                ErrorManager.getDefault().notify (ex);
//            }
//            return result;
//        } else {
//            return null;
//        }
//    }
//
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    public String getShortDescription() {
        String mime = getDataObject().getPrimaryFile().getMIMEType();
        if (mime.equals(DocBookDataLoader.MIME_DOCBOOK)) {
            return NbBundle.getMessage(DocBookDataNode.class, "HINT_file_docbook_xml"); //NOI18N
        } else if (mime.equals(DocBookDataLoader.MIME_SLIDES)) {
            return NbBundle.getMessage(DocBookDataNode.class, "HINT_file_slides"); //NOI18N
        } else {
            //Mime type can be wrong if the document is malformed
            return super.getShortDescription();
        }
    }

    private boolean isMainFile() {
        FileObject fob = getDataObject().getPrimaryFile();
        Project p = FileOwnerQuery.getOwner(fob);
        boolean result = p != null;
        if (result) {
            MainFileProvider prov = p.getLookup().lookup(MainFileProvider.class);
            result = prov != null;
            if (result) {
                result = prov.isMainFile(fob);
            }
        }
        return result;
    }

    void change() {
        fireDisplayNameChange(null, getDisplayName());
    }

    public String getHtmlDisplayName() {
        String result = super.getHtmlDisplayName();
        boolean main = isMainFile();
        if (main) {
            return result == null ? "<b>" + getDisplayName() : "<b>" + result;
        } else {
            return result;
        }
    }

    private static final class Notifier implements MainFileProvider.Notifier {
        private DocBookDataObject obj;
        Notifier (DocBookDataObject obj) {
            this.obj = obj;
        }

        public void change() {
            DocBookDataNode n =
                    (DocBookDataNode) obj.getNodeDelegate();
            n.change();
        }
    }
}
