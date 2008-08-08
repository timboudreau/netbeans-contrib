/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.portalpack.websynergy.portlets.nonjava.NonJavaPortletConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author satyaranjan
 */
public class TemplateUtil {

    private String templateFolder;
    private FileObject folder;
    private ScriptEngineManager manager;
    private Logger logger = Logger.getLogger(NonJavaPortletConstants.NON_JAVA_PORTLET_LOGGER);

    public TemplateUtil(String templateFolder) {
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

    public FileObject mergeTemplateToFile(FileObject templateFileObj, FileObject folder, String fileName, Map values)
        throws DataObjectNotFoundException, IOException {
        DataObject dbObj = DataObject.find(templateFileObj);

        DataFolder folderObj = DataFolder.findFolder(folder);
        DataObject newdobj = dbObj.createFromTemplate(folderObj, fileName, values);
        if (newdobj == null) {
            return null;
        }
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
        } catch (ScriptException ex) {
            IOException io = new IOException(ex.getMessage());
            io.initCause(ex);
            throw io;
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (is != null) {
                is.close();
            }
        }

    }

    private ScriptEngine engine(FileObject fo) {
        Object obj = fo.getAttribute("javax.script.ScriptEngine"); // NOI18N

        if (obj instanceof ScriptEngine) {
            return (ScriptEngine) obj;
        }
        if (obj instanceof String) {
            synchronized (TemplateUtil.class) {
                if (manager == null) {
                    manager = new ScriptEngineManager();
                }
            }
            return manager.getEngineByName((String) obj);
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
    public FileObject getTemplateFile(String name) throws TemplateNotFoundException {
        FileObject fo = getFolder() != null ? getFolder().getFileObject(name) : null;
        return fo;
    }

    public FileObject getFolder() throws TemplateNotFoundException {
        if (templateFolder == null || templateFolder.trim().length() == 0) {
            throw new TemplateNotFoundException("Template Folder is not set.");
        }
        if (folder == null) {
            folder = Repository.getDefault().getDefaultFileSystem().findResource(templateFolder);
        }
        return folder;
    }

    public void setFolder(FileObject folder) {
        folder = folder;
    }
}
