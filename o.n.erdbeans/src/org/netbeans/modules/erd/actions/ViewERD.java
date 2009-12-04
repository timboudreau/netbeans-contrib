/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erd.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.erd.io.DocumentSave;
import org.netbeans.modules.erd.io.ERDContext;
import org.netbeans.modules.erd.io.ERDDataObject;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class ViewERD implements ActionListener {

    private final DatabaseConnection connection;

    public ViewERD(DatabaseConnection context) {
        this.connection = context;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            
            // 1. create document based on given connection
            ERDContext context = createERDContext();
            // 3. open saved file
            saveERDContext(context);
            // 3. open saved file
            openDiagram(context);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private ERDContext createERDContext() throws IOException {
        File f = File.createTempFile("diagram", ".erd"); // NOI18N
        FileObject fo = FileUtil.toFileObject(f);
        String url = connection.getDatabaseURL();
        return new ERDContext(fo, url, ERDContext.DATASOURCETYPE.CONNECTION);
    }

    private void saveERDContext(ERDContext context) {
         DocumentSave.save(context);
    }

    private void openDiagram(ERDContext context) throws DataObjectExistsException, IOException {
        FileObject fo = context.getFileObject();
        File f = FileUtil.toFile(fo);
        f.setReadOnly(); // XXX: ERD is read only so far
        DataObject erdDO = ERDDataObject.find(fo);
        assert erdDO != null : "ERDDataObject exists for " + fo;
        erdDO.getNodeDelegate().setDisplayName(NbBundle.getMessage(ViewERD.class, "ViewERD_DisplayNameInEditor", connection.getDisplayName())); // NOI18N
        OpenCookie open = erdDO.getLookup().lookup(OpenCookie.class);
        open.open();
    }
}
