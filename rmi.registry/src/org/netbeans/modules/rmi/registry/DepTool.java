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

package org.netbeans.modules.rmi.registry;

import java.io.*;
import java.util.*;

/**
 *
 * @author  mryzl
 * @version
 */
public class DepTool extends Object {

    private boolean nullAnnotation;
    private Class clazz;

    /** Creates new DepTool.
     * @param clazz - a class
     * @param nullAnnotation - if true all classes which have null classloader are being looked for as well
     */
    public DepTool(Class clazz, boolean nullAnnotation) {
        this.clazz = clazz;
        this.nullAnnotation = nullAnnotation;
    }

    /** Get list of all class that the given class depends on.
     * @return list of Class
     * @exception ClassNotFound - when a class cannot be loaded by the class loader.
     * @exception IOException - if the bytecode of the class cannot be loaded.
     */
    public List getClassesSet() throws ClassNotFoundException, IOException {
        ClassLoader loader = clazz.getClassLoader();
        HashSet set = new HashSet();
        set.add(clazz.getName().replace('.', '/'));
        ArrayList list = new ArrayList();
        list.add(clazz);
        int index = 0;
        Class tempClass;
        while (index < list.size()) {
            tempClass = (Class) list.get(index++);
            String resourceName = '/' + tempClass.getName().replace('.', '/') + ".class"; // NOI18N
            String[] names = getClassesFromBytecode(clazz.getResourceAsStream(resourceName));
            for (int i = 0; i < names.length; i++) {
                if (set.add(names[i])) {
                    Class clazz2 = loader.loadClass(names[i].replace('/', '.'));
                    if (nullAnnotation || (clazz2.getClassLoader() != null)) {
                        list.add(clazz2);
                    }
                }
            }
        }
        return list;
    }
    
    private String[] getClassesFromBytecode(InputStream is) throws IOException {
        
        if (is == null) return new String[0];
        
        HashSet set = new HashSet();
        
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        try {
            dis.readInt(); // magic
            dis.readInt(); // version
            int len = dis.readUnsignedShort() - 1; // length of constant pool
            String[] classes = new String[len];
            int[] indexes = new int[len];
            for(int i = 0; i < len; i++) { 
                indexes[i] = -1;
                int tag = dis.readUnsignedByte();
                switch (tag) {
                    case 7: 
                        int index = dis.readUnsignedShort();
                        indexes[i] = index - 1;
                    break;
                    
                    case 3:
                    case 4:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        dis.readUnsignedShort();
                        dis.readUnsignedShort();
                    break;
                    
                    case 8:
                        dis.readUnsignedShort();
                    break;
                    
                    case 5:
                    case 6:
                        dis.readUnsignedShort();
                        dis.readUnsignedShort();
                        dis.readUnsignedShort();
                        dis.readUnsignedShort();
                        indexes[++i] = -1;
                    break;
                    
                    case 1:
                        String utf = dis.readUTF();
                        classes[i] = utf;
                    break;
                }
            }
            // post process
            for(int i = 0; i < len; i++) {
                int index2 = indexes[i];
                if (index2 != -1) {
                    String className = classes[index2];
                    if ((className != null) && (className.length() > 0)) {
                        if (className.charAt(0) == '[') {
                            int k;
                            for(k = 0; k < className.length(); k++) {
                                if (className.charAt(k) != '[') break;
                            }
                            if (k >= (className.length() - 1)) continue;
                            else className = className.substring(k + 1); 
                            int iSemiColon = className.indexOf(';');
                            if (iSemiColon != -1) className = className.substring(0, iSemiColon);
                        }
                        set.add(className);
                    }
                }
            }
        } catch (ClassCastException ex) {
            throw new IOException("wrong format of class file");
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IOException("wrong format of class file");
        } finally {
            if (dis != null) dis.close();
        }
        return (String[]) set.toArray(new String[set.size()]);
    }

    public static void main(String[] args) throws Exception {
        DepTool dt = new DepTool(DepTool.class, true);
        Iterator it = dt.getClassesSet().iterator();
        while (it.hasNext()) {
            Class clazz = (Class) it.next();
            System.err.println(clazz.getName());
        }
    }
}
