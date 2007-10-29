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

package javafx.netbeans.fxuserlib;

import net.java.javafx.typeImpl.*;
import java.io.*;
import javax.swing.SwingUtilities;
import java.net.*;
import java.util.*;
import net.java.javafx.type.DynamicTypeLoader;
import net.java.javafx.type.Module;
import net.java.javafx.type.Type;
import net.java.javafx.type.TypeLoader;
import net.java.javafx.type.Value;
import net.java.javafx.type.ValueList;
import net.java.javafx.type.expr.CompilationUnit;
import net.java.javafx.type.expr.ValidationError;

public class FXBuild {

    static final TypeFactoryImpl factory = new TypeFactoryImpl();
    static final Module module = factory.createModule();
    
    private static Vector<String> recurseFileSearch(File parent, String[] extArray, String subDir) {
        Vector<String> string  = new Vector<String>();
        for (File file: parent.listFiles()) {
            if (file.isDirectory())
                string.addAll(recurseFileSearch(file, extArray, file.getName()));
            else
                for (String ext: extArray)
                    if (file.getName().endsWith("." + ext)) {
                        if (subDir.contentEquals(""))
                            string.add(file.getName());
                        else
                            string.add(subDir + "/" + file.getName());
                        break;
                    }
        }
        return string;
    }

    public static void main(final String[] argv) throws Exception {
	System.setProperty("apple.laf.useScreenMenuBar", "true");
	System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        try {
            TypeLoader xsdLoader = (TypeLoader)
                Class.forName("net.java.javafx.ws.WebService").newInstance();
            xsdLoader.setModule(module);
        } catch (Exception e) {
        }
        SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {  
                    final Compilation compilation = new Compilation(module);
                    try {
                        Vector<String> vectorArgv = new Vector<String>();
                        String userDir = System.getProperty("user.dir");
                        for (int i = 0; i < argv.length; i++) {
                            if (argv[i].equals("--")) {
                                ValueList valueList = module.createValueList();
                                ++i;
                                for (; i < argv.length; i++) {
                                    Value str = module.STRING().instantiate();
                                    str.setString(argv[i]);
                                    valueList.addValue(str);
                                }
                                System.out.println("args="+valueList);
                                module.STRING().addNamedValue("ARGUMENTS", valueList);
                                break;
                            }
                        }
                        
                        for (int i = 0; i < argv.length; i++) {          
                            if (argv[i].equals("--")) {
                                vectorArgv.add(argv[i]);
                                break;
                            }
                            if (argv[i].endsWith(".fx")) {
                                vectorArgv.add(argv[i]);
                            } else {
                                File file = new File(userDir + "/" + argv[i]);
                                if (file == null || !file.exists())
                                    file = new File(argv[i]);
                                if (file != null && file.exists()) {
                                    String[] extArray = {"fx", "java"};
                                    Vector<String> list = FXBuild.recurseFileSearch(file, extArray, "");
                                    vectorArgv.addAll(list);
                                }
                            }
                        }
                        module.addTypeLoader(new DynamicTypeLoader() {
                                public void setModule(Module module) {
                                }
                                public Type loadType(String typeName) {
                                    return null;
                                }
                                public ValueList loadClass(String typeName) throws Exception {
                                    System.out.println("load class: "+ typeName);
                                    CompilationUnit unit = compilation.loadCompilationUnit(typeName);
                                    if (unit == null) {
                                        throw new ClassNotFoundException(typeName);
                                    }
                                    if (!unit.isInitialized()) {
                                        System.out.println("executing...");
                                        ValueList result = 
                                            unit.execute();
                                        System.out.println("result ="+result);
                                        return result;
                                    }
                                    return null;
                                }

                                public void link() {
                                }
                            });
                        
                        String[] newArgv = new String[vectorArgv.size()];
                        for (int i = 0; i < vectorArgv.size(); i++)
                            newArgv[i] = vectorArgv.elementAt(i);
                        ValueList result = compilation.compile(newArgv, true);
                        if (result != null) {
                            System.out.println(result);
                        }
                    } catch (ValidationError e) {
                        ValidationError err = e;
                        while (err != null) {
                            System.out.println("Error in " + err.getMessage());
                            err = err.getNextError();
                        } 
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            });
    }
}