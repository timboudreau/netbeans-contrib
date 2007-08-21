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
package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

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
import org.netbeans.api.queries.FileEncodingQuery;
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

    private static String templateFolder = "genericportlets/templates";
    private static FileObject folder;
    private static ScriptEngineManager manager;
    
    public TemplateHelper() {
    }
   
    public static FileObject mergeTemplateToFile(FileObject templateFileObj,FileObject folder,String fileName,Map values)
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
    public static void mergeTemplateToWriter(FileObject template, Writer writer,
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
    
      private static ScriptEngine engine(FileObject fo) {
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
    

    public InputStream getResourceStream(String str) throws TemplateNotFoundException {

        FileObject fo = getTemplateFile(str);
        InputStream is = null;
        try {
            if (fo == null) {
                is = getClass().getResourceAsStream((new StringBuilder()).append("templates/").append(str).toString());
                if (is == null) {
                    throw new TemplateNotFoundException(str);
                }
            } else {
                is = fo.getInputStream();
            }
        } catch (IOException e) {
            throw new TemplateNotFoundException((new StringBuilder()).append(e.getMessage()).append("(").append(str).append(")").toString());
        }
        return is;
    }

    /**
     * It returns the template FileObject
     * 
     * @param name Template name. Template name is usually specified in the layer.xml 
     * @return Template FileObject
     **/
    public static FileObject getTemplateFile(String name) {
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        return fo;
    }

    public static FileObject getFolder() {
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource(templateFolder);
        }
        return folder;
    }

    public static void setFolder(FileObject folder) {
        folder = folder;
    }

    
}
