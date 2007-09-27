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
package beans2nbm.gen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 *
 * @author Tim Boudreau
 */
public class NbmFileModel implements FileModel {
    private final String path;

    private List entries = new ArrayList();
    public NbmFileModel (String path) {
        this.path = path;
    }

    static String genClassnameDots (String classname) {
        assert classname.endsWith(".class");
        StringBuffer sb = new StringBuffer (classname.substring(0, classname.length() - ".class".length()));
        for (int i=0; i < sb.length(); i++) {
            if ('/' == sb.charAt(i)) {
                sb.setCharAt(i, '.');
            }
        }
        return sb.toString();
    }
    
    static String genSimpleClassname (String classname) {
        int start = classname.lastIndexOf('/');
        int end = classname.lastIndexOf('.');
        if (start == -1 || end == -1) {
            throw new IllegalArgumentException ("Not a valid class name" 
                    + classname);
        }
        return classname.substring(start, end);
    }

    public String getPath() {
        return path;
    }

    public void write(OutputStream stream) throws IOException {
        List l = buildEntries();
        JarOutputStream jos = new JarOutputStream (stream);
        jos.setLevel(9);
        try {
            for (Iterator i = l.iterator(); i.hasNext();) {
                FileModel file = (FileModel) i.next();
                JarEntry entry = new JarEntry (file.getPath());
                jos.putNextEntry(entry);
                file.write(jos);
                jos.closeEntry();
            }
        } finally {
            jos.close();
        }
    }
    
    public void add (FileModel mdl) {
        entries.add (mdl);
    }

    private List buildEntries() {
        List l = new ArrayList (entries);
        return l;
    }
    
}
