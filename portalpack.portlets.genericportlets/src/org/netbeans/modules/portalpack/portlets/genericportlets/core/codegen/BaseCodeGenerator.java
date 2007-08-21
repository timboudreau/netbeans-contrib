/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.TemplateHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * @author Satya
 */
public class BaseCodeGenerator {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    private static String templateDir = "";
    private static String templateFileName = null;

  
    public void setTemplateFileName(String fileName)
    {
        templateFileName = fileName;
    }

  
    public String getTemplateFileName()
    {
        return templateFileName;
    }

    
     public void generateCode(File folder,String className,Map values)  throws DataObjectNotFoundException, IOException {

             FileObject templateFileObj = TemplateHelper.getTemplateFile(templateFileName);
             if(templateFileObj == null)
                 throw new IOException("Template Not Found : "+templateFileName);
             FileObject folderObj = FileUtil.toFileObject(folder);
             if(folderObj == null)
                 throw new IOException("Folder is null : "+folder);
             TemplateHelper.mergeTemplateToFile(templateFileObj, folderObj, className, values);
    }
    
    public File writeToFile(String file,StringBuffer content)
    {
 
            File f = new File(file);
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE,"error",e);
                return null;
            }
 
            try {
                fout.write(content.toString().getBytes());
                fout.flush();
            } catch (IOException e) {
                logger.log(Level.SEVERE,"error",e);
            }

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE,"error",e);
                }
            }
            return f;
    }


}
