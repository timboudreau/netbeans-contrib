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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.perspective.persistence;

import java.io.IOException;
import java.util.Set;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.netbeans.modules.perspective.views.PerspectiveMode;
import org.netbeans.modules.perspective.views.View;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Anuradha G
 */
public class PerspectiveWriter {

     void writePerspective(FileObject base, PerspectiveImpl pi) throws IOException {
        FileObject fileObject = base.createFolder(pi.getName());
        fileObject.setAttribute("alias", pi.getAlias());
        fileObject.setAttribute("description", pi.getDescription()!=null? pi.getDescription():"");
        fileObject.setAttribute("image", pi.getImagePath());
        fileObject.setAttribute("position", pi.getIndex());
        FileObject modesFileObject = fileObject.createFolder("modes");
        Set<PerspectiveMode> modes = pi.getPerspectiveModes();
        
        for (PerspectiveMode mode : modes) {
            FileObject modeFileObject = modesFileObject.createFolder(mode.getId());
            View activeView = mode.getActiveView();
            if(activeView!=null)
              modeFileObject.setAttribute("active", activeView.getTopcomponentID());
            Set<View> views = mode.getViews();
            for (View view : views) {
                FileObject viewFileObject = modeFileObject.createFolder(view.
                        getTopcomponentID());
                viewFileObject.setAttribute("index", view.getIndex());
                viewFileObject.setAttribute("opened", view.isOpen());
            }

        }

    }
}
