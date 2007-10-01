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

import java.applet.Applet;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.Iterator;
import javax.swing.JApplet;
import javax.swing.JButton;

import org.openide.*;
import org.openide.util.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.CookieSet;
import org.openide.src.SourceElement;
import org.openide.src.nodes.SourceChildren;
import org.openide.src.nodes.SourceElementFilter;
import org.openide.src.nodes.FilterFactory;
import org.openide.src.nodes.ElementNodeFactory;
import org.netbeans.modules.classfile.ClassFile;

/* TODO:
  - check the showDeclaredOnly flag - it works different for
    variables/constructors than for methods (i.e. for variables/constructors
    the declaredOnly are not subset of notDecalredOnly
*/

/**
* DataObject which represents .class files.
*
* @author Jan Jancura, Ian Formanek, Petr Hamernik, Dafe Simonek
*/
public class CompiledDataObject extends ClassDataObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -7355104884002106137L;

    /** Name of arguments property. */
    private final static String  PROP_ARGV = "Arguments"; // NOI18N
    /** Name of execution property. */
    private final static String  PROP_EXECUTION = "Execution"; // NOI18N

    // variables ...................................................................................

    // constructors ...................................................................................

    /** Constructs a new ClassDataObject */
    public CompiledDataObject(final FileObject fo,final ClassDataLoader loader) throws org.openide.loaders.DataObjectExistsException {
        super (fo, loader);
        initCookies();
    }
    
    /** Performs cookie initialization. */
    protected void initCookies () {
        super.initCookies();

        CookieSet cs = getCookieSet();
        // only JavaBeans should offer `Customize Bean' action
        cs.add(InstanceCookie.class, this);
    }
    
    protected Node.Cookie createBeanInstanceSupport() {
	if (isJavaBean()) {
	    return createInstanceSupport();
	} else {
	    return null;
	}
    }
    
    public Node.Cookie createCookie(Class c) {
        if (InstanceCookie.class.isAssignableFrom(c)) {
	    return createBeanInstanceSupport();
	}
        return super.createCookie(c);
    }


    /**
    * @return class data node
    */
    protected Node createNodeDelegate () {
        return new CompiledDataNode (this);
    }

    // Properties implementation .....................................................................

    boolean isExecutable () {
        return createInstanceSupport().isExecutable ();
    }

    // other methods ..............................................................................

    /** Check if in specific folder exists .ser fileobject with the same name.
    * If it exists user is asked for confirmation to rewrite, rename or
    * cancel operation. Throws UserCancelException if user pressed cancel
    * button.
    * @param f destination folder
    * @return new Name of file in destination
    */
    protected String existInFolder(DataFolder f) throws UserCancelException {
        FileObject fo = getPrimaryFile();
        String name = fo.getName();
        String ext = "ser"; // NOI18N
        String destName = fo.getName();
        if (f.getPrimaryFile().getFileObject(name, ext) != null) {
            // file with the same name exists - ask user what to do
            ResourceBundle bundle = NbBundle.getBundle(ClassDataObject.class);
            String rewriteStr = bundle.getString("CTL_Rewrite");
            String renameStr = bundle.getString("CTL_Rename");
            String cancelStr = bundle.getString("CTL_Cancel");
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                                      new MessageFormat(bundle.getString("MSG_SerExists")).
                                      format(new Object[] { name, f.getName() }));
            nd.setOptions(new Object[] { rewriteStr, renameStr, cancelStr });
            String retStr = (String)DialogDisplayer.getDefault().notify(nd);
            if (cancelStr.equals(retStr)) // user cancelled the dialog
                throw new UserCancelException();
            if (renameStr.equals(retStr))
                destName = FileUtil.findFreeFileName (
                               f.getPrimaryFile(), destName, ext);
            if (rewriteStr.equals(retStr)) {
                try {
                    FileObject dest = f.getPrimaryFile().getFileObject(name, ext);
                    FileLock lock = dest.lock();
                    dest.delete(lock);
                    lock.releaseLock();
                }
                catch (IOException e) {
                    return null;
                }
            }
        }
        return destName;
    }
}

