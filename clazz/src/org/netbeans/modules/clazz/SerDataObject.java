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

package org.netbeans.modules.clazz;

import org.openide.util.HelpCtx;
import org.netbeans.modules.classfile.ClassFile;
import org.openide.loaders.InstanceSupport;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;
import org.openide.nodes.CookieSet;
import java.io.InputStream;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import java.io.IOException;
import org.openide.util.NbBundle;


/** DataObject which represents JavaBeans (".ser" files).
* This class is final only for performance reasons,
* can be happily unfinaled if desired.
*
* @author Jan Jancura, Ian Formanek, Dafe Simonek
*/
public final class SerDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 8229229209013849842L;

    /** Constructs a new BeanDataObject */
    public SerDataObject(FileObject fo, ClassDataLoader loader) throws DataObjectExistsException {
        super (fo, loader);
        initCookies();
    }
    
    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        cs.add(InstanceCookie.class, this);
    }
    
    /**
     * All serialized objects are treated as JavaBeans since the instance of
     * the object can be obtained (using deserialization).
     * @returns true
     */
    public boolean isJavaBean() {
        return true;
    }

    /** 
     * Creates NodeDelegate for this DataObject
     * @returns node that represent the obejct
    */
    protected Node createNodeDelegate () {
        return new SerDataNode (this);
    }


    protected ClassFile loadClassFile() throws IOException,ClassNotFoundException {
        Class clazz = createInstanceSupport().instanceClass();
        String resourceName='/'+clazz.getName().replace('.','/')+".class"; // NOI18N
        InputStream stream=clazz.getResourceAsStream(resourceName);

        if (stream==null)
            return null;
        try {
            return new ClassFile(stream,false);
        } finally {
            stream.close();
        }
    }

    /**
     * Provides special processing for the serialized objects:
     * if the help can be found for the class that is serialized inside
     * the ser file, returns that help. The default is the help for SerDataObject.
     * @returns appropriate HelpCtx
     */
    public HelpCtx getHelpCtx () {
        HelpCtx test = InstanceSupport.findHelp (createInstanceSupport());
        if (test != null)
            return test;
        else
            return super.getHelpCtx();
    }

    protected FileObject handleRename (String name) throws IOException {
        if (name.indexOf(".")!=-1) { // NOI18N
            throw (IOException)ErrorManager.getDefault().annotate(
            new IOException("Dot in name"), // NOI18N
            ErrorManager.USER,
            null, NbBundle.getMessage(SerDataObject.class, "MSG_INVName"),
            null, null
	    );
        }
        return super.handleRename(name);
    }
}
