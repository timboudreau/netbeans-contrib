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

import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.netbeans.modules.perspective.views.PerspectiveMode;
import org.netbeans.modules.perspective.views.View;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Anuradha G
 */
public class PerspectiveReader {

     PerspectiveImpl readPerspective(FileObject fo) {
        PerspectiveImpl perspective = new PerspectiveImpl(fo.getName(),
                fo.getAttribute("alias").toString());
        perspective.setDescription((String) fo.getAttribute("description"));
        perspective.setImagePath((String) fo.getAttribute("image"));
        perspective.setIndex((Integer) fo.getAttribute("position"));

        FileObject[] children = fo.getChildren();
        for (FileObject fileObject : children) {
            if ("modes".equals(fileObject.getName())) {
                FileObject[] fos = fileObject.getChildren();
                for (FileObject object : fos) {
                    perspective.addPerspectiveMode(readMode(object));
                }

                
            }
        }

        return perspective;
    }

    private PerspectiveMode readMode(FileObject fo) {
        PerspectiveMode mode = new PerspectiveMode(fo.getName());
        String active = (String) fo.getAttribute("active");
        FileObject[] fileObjects = fo.getChildren();
        for (FileObject fileObject : fileObjects) {
          View view=readView(fileObject);
          if(view.getTopcomponentID().equals(active)){
            mode.setActiveView(view);
          }
          mode.addView(view);
        }

        return mode;
    }

    private View readView(FileObject fileObject) {
        int index = (Integer) fileObject.getAttribute("index");
        boolean opened = (Boolean) fileObject.getAttribute("opened");
        return new View(fileObject.getName(), index, opened);
       
    }
}
