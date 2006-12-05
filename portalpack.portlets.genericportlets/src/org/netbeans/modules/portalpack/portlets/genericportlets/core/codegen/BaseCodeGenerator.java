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

import org.apache.velocity.Template;
import org.apache.velocity.VTResourceLoader;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.app.Velocity;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public StringBuffer generateCode(Map values) throws Exception, ParseErrorException {

        StringWriter writer = new StringWriter();
        try {
             VelocityContext context = VTResourceLoader.getContext(values);
             
             Template template = Velocity.getTemplate(templateFileName);
            if (template == null) {
                throw new IllegalStateException(" no target defined ");
            }
        
            template.merge(context, writer);
            
        } catch (Exception e) {
        
            logger.log(Level.SEVERE,"error",e);
        }

        return writer.getBuffer();
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
