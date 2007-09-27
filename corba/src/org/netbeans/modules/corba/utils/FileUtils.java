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

package org.netbeans.modules.corba.utils;

import java.io.File;
import java.util.Stack;
import java.util.StringTokenizer;

import org.openide.filesystems.FileObject;

import org.openide.execution.NbClassPath;
/*
 * @author Dusan Balek
 */

public class FileUtils {

    public static String getRealFileName (FileObject fo) {
        try {
	    File __fs_file = NbClassPath.toFile (fo.getFileSystem ().getRoot ());
            String __filesystem;
            if (__fs_file != null)
	        __filesystem = __fs_file.getAbsolutePath ();
            else
                __filesystem = fo.getFileSystem ().getSystemName();
	    //System.out.println ("__filesystem: " + __filesystem);
            String __file_name = fo.getPackageNameExt (File.separatorChar, '.');
	    //System.out.println ("__file_name: " + __file_name);
	    String __retval = "";
            if (__file_name != null && __file_name.length() > 0)
                __retval = __filesystem + File.separator + __file_name;
            else
                __retval = __filesystem;
	    //System.out.println ("-> " + __retval);
	    return __retval;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static String getRealPackageName (FileObject fo) {
        try {
            return getRealFileName (fo.getParent ());
        }
        catch (Exception e) {
            return null;
        }
    }        
    
    public static String convert2Canonical (String path) {
        String __canonical = "";
        try {
            Stack __stack = new Stack();
            StringTokenizer __st = new StringTokenizer(path, "/\\");
            while (__st.hasMoreTokens()) {
                String __token = __st.nextToken();
                if (__token.equals(".."))
                    __stack.pop();
                else if (!__token.equals("."))
                    __stack.push(__token);
            }
            if (!__stack.isEmpty()) {
                int __size = __stack.size()-1;
                for (int i = 0; i < __size; i++)
                    __canonical += __stack.get(i) + File.separator;
                __canonical += __stack.get(__size);
            }
        }
        catch (Exception e) {
        }
        return __canonical;
    }
}
