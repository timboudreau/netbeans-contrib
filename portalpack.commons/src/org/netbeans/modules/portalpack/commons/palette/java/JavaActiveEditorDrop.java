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
package org.netbeans.modules.portalpack.commons.palette.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.portalpack.commons.TemplateHelper;
import org.netbeans.modules.portalpack.commons.TemplateNotFoundException;
import org.netbeans.modules.portalpack.commons.palette.java.JavaMethod;
import org.netbeans.modules.portalpack.commons.palette.java.util.JavaPaletteXMLUtil;
import org.netbeans.modules.portalpack.commons.palette.java.util.JavaUtil;
import org.netbeans.modules.portalpack.commons.palette.jsp.PaletteUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public abstract class JavaActiveEditorDrop implements ActiveEditorDrop {

    private final String TEMPLATE_PATH_PREFIX = "portalpack/palette";

    protected String createBody(Map map) {

        TemplateHelper templateHelper = new TemplateHelper();
        templateHelper.setTemplateFolder(TEMPLATE_PATH_PREFIX + "/" + getTemplateFolder());

        try {
            FileObject template = templateHelper.getTemplateFile(getTemplateName());
            if (!templateHelper.isScriptEngineSupported(template)) {
                return templateHelper.getTemplateData(template);
            }

            StringWriter writer = new StringWriter();

            templateHelper.mergeTemplateToWriter(template, writer, map);


            return writer.toString();

        } catch (TemplateNotFoundException e) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(JavaActiveEditorDrop.class, "TEMPLATE_NOT_FOUND", getTemplateName()), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    protected JavaMethod createMethodDeclaration(Map map) {

        TemplateHelper templateHelper = new TemplateHelper();
        templateHelper.setTemplateFolder(TEMPLATE_PATH_PREFIX + "/" + getTemplateFolder());

        String templateName = getTemplateName();
        int index = templateName.indexOf(".");
        if (index == -1) {
            return null;
        }
        String templatePrefix = templateName.substring(0, index);
        String methodDeclarationTemplate = templatePrefix + "-method.xml";

        try {
            FileObject template = templateHelper.getTemplateFile(methodDeclarationTemplate);
            /*if (!templateHelper.isScriptEngineSupported(template)) {
            return templateHelper.getTemplateData(template);
            }*/

            InputStream ins = template.getInputStream();
            JavaMethod method = JavaPaletteXMLUtil.getMethodDefination(ins);
            return method;

        } catch (TemplateNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public boolean handleTransfer(JTextComponent targetComponent) {
        Map dataMap = new HashMap();
        preHandleTransfer(targetComponent, dataMap);

        JavaMethod method = createMethodDeclaration(dataMap);
        String body = createBody(dataMap);

        if (method == null) {
            if (body == null) {
                return false;
            }
            try {

                PaletteUtilities.insert(body, targetComponent);
                return true;
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
                return false;
            }
        }
        
        method.setMethodBody(body);
        
        
        
        BaseDocument document = (BaseDocument) targetComponent.getDocument();
        FileObject fObject = NbEditorUtilities.getFileObject(document);    
        //ClassPath cp = ClassPath.getClassPath(fObject, ClassPath.COMPILE);
        List<JavaMethod> methods = JavaUtil.getMethods(fObject, null);
        
        if(JavaUtil.isMethodPresent(method, 
                        (JavaMethod [])methods.toArray(new JavaMethod[0]))) {
        
            
        } else {
            JavaUtil.addMethod(fObject, method);
        }
        
        postHandleTransfer(targetComponent, dataMap);

        return true;
    }

    public void preHandleTransfer(JTextComponent targetComponent, Map map) {
    }

    public void postHandleTransfer(JTextComponent targetComponent, Map map) {
    }

    /**
     * 
     * @return template path for this snippet
     */
    public abstract String getTemplateName();

    /**
     * 
     * @return template folder like saw,cms etc.
     */
    public abstract String getTemplateFolder();
}
