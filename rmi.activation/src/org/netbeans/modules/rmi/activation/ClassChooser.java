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

package org.netbeans.modules.rmi.activation;

import java.io.*;
import java.lang.ClassLoader;

import org.openide.*;
import org.openide.cookies.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.src.ClassElement;
import org.openide.src.SourceElement;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.NbBundle;

/** Allow user to choose appropriate class
 *
 * @author  Jan Pokorsky
 * @version
 */
public class ClassChooser implements DataFilter, NodeAcceptor {

    private static ClassChooser chooser = null;
    private Node node;
    private static Class clazzSerDataObject = null;
    
    /** Creates new ClassChooser */
    private ClassChooser() {
    }
    
    public static ClassChooser getInstance() {
        if (chooser == null) {
            chooser = new ClassChooser();
        }
        return chooser;
    }
    
    private Class getClazzSerDataObject() {
        if (clazzSerDataObject == null) {
            // add SerDataObject without dependency on ClassModule
            try {
                clazzSerDataObject = Class.forName("org.netbeans.modules.clazz.SerDataObject", false, (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class)); // NOI18N
            } catch (ClassNotFoundException ex) {
            }
        }
        return clazzSerDataObject;
    }

    /** Should the data object be displayed or not?
     * @param obj the data object
     * @return <CODE>true</CODE> if the object should be displayed,
     *   <CODE>false</CODE> otherwise
 */
    public boolean acceptDataObject(DataObject obj) {
        if (obj.getCookie(DataFolder.class) != null) return true;
        if (obj.getCookie(SourceCookie.class) != null) {
            Class clazz = getClazzSerDataObject();
            if (clazz != null && obj.getCookie(clazz) == null) return true;
        }
        return false;
    }
    
    /** Is the set of nodes acceptable?
     * @param nodes the nodes to consider
     * @return <CODE>true</CODE> if so
 */
    public boolean acceptNodes(Node[] nodes) {
        if (nodes == null || nodes.length != 1) return false;
        if (nodes[0].getCookie(DataFolder.class) != null) return false;
        
        ClassElement element = (ClassElement) nodes[0].getCookie(ClassElement.class);
        if (element != null) return !element.isInterface();
        
        DataObject data = (DataObject) nodes[0].getCookie(DataObject.class);
        if (data != null && data.getCookie(SourceCookie.class) != null) return true;
        return false;
    }
    
    /** Show dialog to choose class. */
    public void show() throws UserCancelException {
        node = null;

        Node[] nodes = NodeOperation.getDefault().select(
                        NbBundle.getMessage(ClassChooser.class, "LAB_ClassChooser.SelectClass"),  // NOI18N
                        NbBundle.getMessage(ClassChooser.class, "LAB_ClassChooser.LookIn"),  // NOI18N
                        RepositoryNodeFactory.getDefault().repository(this),
                        this);

        node = nodes[0];
    }
    
    /** Get full specified class name (with package). */
    public String getFullClassName() {
        if (node == null) return null;
        ClassElement element = (ClassElement) node.getCookie(ClassElement.class);
        
        if (element == null) {
            // find top level class with same name as primary file
            DataObject data = (DataObject) node.getCookie(DataObject.class);
            if (data == null) return null;
            SourceElement source = ((SourceCookie) data.getCookie(SourceCookie.class)).getSource();
            if (source == null) return null;
            ClassElement[] elements = source.getClasses();
            String filename = data.getPrimaryFile().getName();
            for (int i = 0; elements != null && i < elements.length; i++){
                if (elements[i].getName().getName().equals(filename)) {
                    element = elements[i];
                    break;
                }
            }
            if (element == null) return null;
        }
        
//        return element.getName().getFullName();
        
        // Due to instantiation inner classes. It returns names like pckg.Clazz$InnerClazz
        // instead of pckg.Clazz.InnerClazz
        return element.getVMName();
    }
    
    /** Get class URL. */
    public String getPathToClass() {
        if (node == null) return null;
            Node parent = node;
            DataObject data;
            
            while (true) {
                data = (DataObject) parent.getCookie(DataObject.class);
                if (data != null) break;
                parent = parent.getParentNode();
            }
            
            FileObject fileObj = data.getPrimaryFile();
            
            final StringBuffer path = new StringBuffer(64);

            try {
                fileObj.getFileSystem().prepareEnvironment(new FileSystem.Environment() {
                                                          public void addClassPath(String element) {
                                                              path.append(element);
                                                          }
                                                      }
                );
                return new File(path.toString()).toURL().toExternalForm();
            } catch (java.net.MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (EnvironmentNotSupportedException ex) {
                ErrorManager.getDefault().notify(ex);
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            
            return ""; // NOI18N
    }
    
    /** Create URL with file protocol and if path is directory then append File.separatorChar. */
    public static String getURLPath(String path) throws java.net.MalformedURLException {
        File f = new File(path);
        if (f.isDirectory()) path += File.separatorChar;

        return new java.net.URL("file", null, path).toExternalForm(); // NOI18N
    }

/*
    public static void main(String[] args) {
        ClassChooser chooser = ClassChooser.getInstance();
        try {
        chooser.show();
        System.out.println("getFullClassName: " + chooser.getFullClassName());
        System.out.println("getPathToClass: " + chooser.getPathToClass());
        } catch (UserCancelException ex) {
            System.out.println("user canceled selection.");
        }
    }
 */
}
