/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2008 Sun
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
package org.netbeans.modules.java.hints.scripting;

import com.sun.source.util.Trees;
import java.io.EOFException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class Utilities {
    
    public final static List<URL> computeCP() {
        List<URL> urls = new LinkedList<URL>();

        urls.add(CompilationInfo.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(FileObject.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(DataObject.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(Node.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(Trees.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(ErrorDescription.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(AbstractHint.class.getProtectionDomain().getCodeSource().getLocation());
        urls.add(Lookup.class.getProtectionDomain().getCodeSource().getLocation());

        return urls;
    }

    public static FileObject getFolder() {
        return Repository.getDefault().getDefaultFileSystem().findResource("hints");
    }
    
    public final static String copyFileToString (FileObject f) throws java.io.IOException {
        //XXX:
        int s = (int)f.getSize();
        byte[] data = new byte[s];
        InputStream in = f.getInputStream();
        int len = in.read (data);
        if (len != s)
            throw new EOFException("truncated file");
        in.close();
        return new String (data);
    }
}
