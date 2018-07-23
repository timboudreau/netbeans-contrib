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
import org.netbeans.api.docbook.MainFileProvider;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

public class DocBookDataNode extends DataNode {
    public DocBookDataNode(DataObject obj) {
        super(obj, Children.LEAF, obj.getLookup());
        assert obj.getLookup().lookup(OpenCookie.class) != null : obj + " has no OpenCookie; DBES=" + obj.getCookie(DocBookEditorSupport.class);
        setIconBaseWithExtension(obj instanceof DocBookDataObject ?
            "org/netbeans/modules/docbook/resources/docbook.png" : //NOI18N
            "org/netbeans/modules/docbook/resources/solbook/templates/solbook.png"); //NOI18N
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getShortDescription() {
        String mime = getDataObject().getPrimaryFile().getMIMEType();
        if (mime.equals(DocBookDataObject.MIME_DOCBOOK)) {
            return NbBundle.getMessage(DocBookDataNode.class, "HINT_file_docbook_xml"); //NOI18N
        } else if (mime.equals(DocBookDataObject.MIME_SLIDES)) {
            return NbBundle.getMessage(DocBookDataNode.class, "HINT_file_slides"); //NOI18N
        } else if (mime.equals(DocBookDataObject.MIME_SOLBOOK)) {
            return NbBundle.getMessage(DocBookDataNode.class, "HINT_file_solbook_xml"); //NOI18N
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

    @Override
    public String getHtmlDisplayName() {
        String result = super.getHtmlDisplayName();
        boolean main = isMainFile();
        if (main) {
            return result == null ? "<b>" + getDisplayName() : "<b>" + result;
        } else {
            return result;
        }
    }


}
