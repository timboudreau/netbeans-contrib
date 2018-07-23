/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.websynergy.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.netbeans.modules.portalpack.servers.websynergy.util.TemplateUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author satyaranjan
 */
public class PortalEXTModifier {

   public static void setDeveloperMode(String portalDepDir) {
        CustomProperties props = new CustomProperties();
        FileOutputStream fout = null;
        FileInputStream fin = null;
        //try retrieve data from file
        try {
            File portalDepClassesDir = new File(portalDepDir + File.separator
                                            + "WEB-INF" + File.separator
                                            + "classes");
            File portalExt = new File(portalDepClassesDir + File.separator
                                        + "portal-ext.properties");

            if(portalExt.exists()) {
                fin = new FileInputStream(portalExt);
                props.loadProperties(fin);
                if(fin != null) {
                    try {
                        fin.close();
                    }catch(Exception e){}
                }
            }

            String overrideProperties = props.getProperty("include-and-override");
            if(overrideProperties == null || overrideProperties.trim().length() == 0) {

                //createNewExtFileWithoutComments(portalDepClassesDir, portalExt);
                props.setProperty("include-and-override", "portal-developer.properties");
                if(portalExt.exists()) {
                    FileUtil.copyFile(FileUtil.toFileObject(portalExt),
                            FileUtil.toFileObject(portalDepClassesDir),
                            getAvailableFileName(portalDepClassesDir, "portal-ext-original",".properties"));
                }
                fout = new FileOutputStream(portalExt);
                props.store(fout, "");
            }

           

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fout != null) {
                try {
                    fout.close();
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                }

            }
        }
    }

   public static boolean createDeveloperProperties(String portalDepDir) {

        try {
            File portalDepClassesDir = new File(portalDepDir + File.separator
                                            + "WEB-INF" + File.separator
                                            + "classes");
            File portalDevProperties = new File(portalDepClassesDir + File.separator
                                        + "portal-developer.properties");
			if(portalDevProperties.exists())
				return true;

			FileObject portalClassesDirFO = FileUtil.toFileObject(portalDepClassesDir);
			TemplateUtil templateUtil = new TemplateUtil("liferay/templates");
			FileObject pdFO = templateUtil.createFileFromTemplate("portal-developer.properties",
					portalClassesDirFO, "portal-developer", "properties");
		    
			if(pdFO != null)
				return true;
			else
				return false;
		}catch(Exception e) {
			return false;
		}

   }
   
    private static String getAvailableFileName(File folder,String name, String ext) {

        int counter = 0;
        File file = new File(folder,name+ext);
        String newName = name;
        while(file.exists()) {
            counter++;
            newName = name + "_" + counter;
            file = new File(folder,newName + ext);
        }

        return newName;
    }
}
