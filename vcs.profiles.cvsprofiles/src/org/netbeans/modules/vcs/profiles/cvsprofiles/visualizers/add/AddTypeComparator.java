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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.add;

import org.openide.util.NbBundle;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.modules.vcscore.util.table.*;

/**
 *
 * @author  mkleint
 * @version
 */


public class AddTypeComparator implements TableInfoComparator {

    private String IS_DIRECTORY;
    private String IS_FILE;
    private String IS_RESURRECTED;
    private String IS_RESSURECTED_AND_MODIFIED;

    /** Creates new StatusComparator */
    public AddTypeComparator() {
          IS_DIRECTORY = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.directory"); //NOI18N
          IS_FILE = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.file"); //NOI18N
          IS_RESURRECTED = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.resurrected"); //NOI18N
          IS_RESSURECTED_AND_MODIFIED = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.resurrectedAndModified"); //NOI18N
    }

    public String getDisplayValue(Object obj, Object rowObject) {
        String isDir = (String)obj;
        DefaultFileInfoContainer info = (DefaultFileInfoContainer)rowObject;
        if (info.isDirectory()) return IS_DIRECTORY;
        if (isDir.equals("A")) { //NOI18N
            return IS_FILE;
        }
        if (isDir.equals("U")) { //NOI18N
            return IS_RESURRECTED;
        }
        if (isDir.equals("M")) {
            return IS_RESSURECTED_AND_MODIFIED;
        }
        return ""; //NOI18N
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String str1 = obj.toString();
        String str2 = obj1.toString();
        return str1.compareTo(str2);
    }
    
}
