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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * This is a helper class to merge template.
 * The default template engine supported by NetBeans is FreeMarker.
 * 
 * @author Satyaranjan
 */
public class TemplateHelper {

    private Logger logger = Logger.getLogger(CommonConstants.LOGGER);
    
    private String templateFolder;
    private FileObject folder;
    private ScriptEngineManager manager;
    
    public TemplateHelper(String templateFolder) {
        
        this.templateFolder = templateFolder;
    }
    
     public FileObject createFileFromTemplate(String templateName, FileObject destObj,
        String fileName, String extentionName) throws TemplateNotFoundException {
        FileObject templateFile = getTemplateFile(templateName);
        if (templateFile == null) {
            throw new TemplateNotFoundException("Template File " + templateName + " not found !!!");
        }

        if (destObj == null) {
            logger.severe("Destination Object is null !!!");
            return null;
        }
        try {
            return FileUtil.copyFile(templateFile, destObj, fileName, extentionName);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error creating file : " + fileName, ex);
            return null;
        }

    }
   
    public FileObject mergeTemplateToFile(FileObject templateFileObj,FileObject folder,String fileName,Map values)
            throws DataObjectNotFoundException, IOException {
         DataObject dbObj = DataObject.find(templateFileObj);
         
         DataFolder folderObj = DataFolder.findFolder(folder);
         DataObject newdobj = dbObj.createFromTemplate(folderObj, fileName, values);
         if(newdobj == null)
             return null;
         return newdobj.getPrimaryFile();              
         
    }
    
    /**
     * Merge a template into the passed Writer such as <CODE>StringWriter</CODE> .
     * @param template   Template FileObject which is returned from <CODE>TemplateHelper.getTemplateFile(String fileName)</CODE>
     * @param writer Writer to hold the result
     * @param values Map of values
     * 
     * @return void
     **/
    public void mergeTemplateToWriter(FileObject template, Writer writer,
                                            Map<String, Object> values) throws IOException {
        
        ScriptEngine eng = engine(template);
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        bind.putAll(values);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            eng.getContext().setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
        }
        
        Charset sourceEnc = FileEncodingQuery.getEncoding(template);
        
        Reader is = null;
        try {
                    
            eng.getContext().setWriter(writer);
            eng.getContext().setAttribute(FileObject.class.getName(), template, ScriptContext.ENGINE_SCOPE);
            eng.getContext().setAttribute(ScriptEngine.FILENAME, template.getNameExt(), ScriptContext.ENGINE_SCOPE);
            is = new InputStreamReader(template.getInputStream(), sourceEnc);
            eng.eval(is);
        }catch (ScriptException ex) {
            IOException io = new IOException(ex.getMessage());
            io.initCause(ex);
            throw io;
        } finally {
            if (writer != null) writer.close();
            if (is != null) is.close();
        }
        
    }
    
      private ScriptEngine engine(FileObject fo) {
        Object obj = fo.getAttribute("javax.script.ScriptEngine"); // NOI18N
        if (obj instanceof ScriptEngine) {
            return (ScriptEngine)obj;
        }
        if (obj instanceof String) {
            synchronized (TemplateHelper.class) {
                if (manager == null) {
                    manager = new ScriptEngineManager();
                }
            }
            return manager.getEngineByName((String)obj);
        }
        return null;
    } 
    
    public boolean isScriptEngineSupported(FileObject fo) {
        Object engine = engine(fo);
        if(engine == null)
            return false;
        return true;
    }  
    
    public String getTemplateData(FileObject file)
    {
        if(file == null) return null;
        try {
            StringBuffer sb = new StringBuffer();
            InputStream ins = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));

            String thisLine;
            while ((thisLine = br.readLine()) != null) {
                sb.append(thisLine);
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * It returns the template FileObject
     * 
     * @param name Template name. Template name is usually specified in the layer.xml 
     * @return Template FileObject
     **/
    public FileObject getTemplateFile(String name) throws TemplateNotFoundException {
        if(templateFolder == null)
            throw new TemplateNotFoundException("Template Folder is not set. Please set it using setTemplateFolder()");
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        if(fo == null)
            throw new TemplateNotFoundException("Template Not found : " + 
                templateFolder + "/" + name);
        return fo;
    }

    public FileObject getFolder() {
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource(templateFolder);
        }
        return folder;
    }

    private void setFolder(FileObject folder) {
        folder = folder;
    }
    
    public void setTemplateFolder(String folderPath)
    {
        templateFolder = folderPath;
    }
}
