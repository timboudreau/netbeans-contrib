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
package org.netbeans.modules.codetemplatetools.ui.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CodeTemplateUtils {
    
    @SuppressWarnings("unchecked")
    public static void saveTemplate(JEditorPane editorPane, String templateName, String templateText, boolean modifying) {
        if (templateName.length() == 0) {
            return;
        }
        initReflection();
        try {
            Object codeTemplateSettingsImpl = getMethod.invoke(null, MimePath.get(editorPane.getContentType())); // NOI18N
            if (codeTemplateSettingsImpl != null) {
                Map templatesMap = (Map) getCodeTemplatesMethod.invoke(codeTemplateSettingsImpl);                
                if (templatesMap == null) {
                    templatesMap = new HashMap();
                } else {
                    if (!modifying) {
                        Object existingTemplate = templatesMap.get(templateName);
                        if (existingTemplate != null) {
                            if (JOptionPane.showConfirmDialog(WindowManager
                                    .getDefault().getMainWindow(),
                                    "Code Template " + templateName
                                            + " already exists. Overwrite?",
                                    "Overwrite exiting Code Template",
                                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                                return;
                            }
                            // fall through
                        }
                    }
                }
                
                Object codeTemplateDescription = 
                    codeTemplateDescriptionConstructor.newInstance(
                        templateName,
                        "",
                        templateText);
                
                Map modifiedTemplatesMap = new HashMap(templatesMap);
                modifiedTemplatesMap.put(templateName, codeTemplateDescription);
                
                setCodeTemplatesMethod.invoke(codeTemplateSettingsImpl, modifiedTemplatesMap);
            }
        } catch (SecurityException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalArgumentException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalAccessException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InvocationTargetException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InstantiationException e) {
            ErrorManager.getDefault().notify(e);
        }
    }   

    // Batch save all the given abbreviations into the map for the given editor's mimetype
    public static void saveTemplates(JEditorPane editorPane, Map<String,String> abbrevs) {
        if (abbrevs.size() == 0) {
            return;
        }
        initReflection();
        try {
            Object codeTemplateSettingsImpl = getMethod.invoke(null, MimePath.get(editorPane.getContentType())); // NOI18N
            Map templatesMap = (Map) getCodeTemplatesMethod.invoke(codeTemplateSettingsImpl);
            
            Map modifiedTemplatesMap = (templatesMap == null ? new HashMap(): new HashMap(templatesMap));

            for (String templateName : abbrevs.keySet()) {
                Object codeTemplateDescription = codeTemplateDescriptionConstructor.newInstance(new Object[]{
                        templateName,
                        "",
                        abbrevs.get(templateName),
                });                    
                
                modifiedTemplatesMap.put(templateName, codeTemplateDescription);
            }
            
            setCodeTemplatesMethod.invoke(codeTemplateSettingsImpl, modifiedTemplatesMap);
        } catch (SecurityException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalArgumentException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalAccessException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InvocationTargetException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InstantiationException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public static void deleteTemplates(JEditorPane editorPane, CodeTemplate[] templates) {
        if (templates == null) {
            return;
        }
        initReflection();
        try {
            Object codeTemplateSettingsImpl = getMethod.invoke(null, MimePath.get(editorPane.getContentType())); // NOI18N
            Map templatesMap = (Map) getCodeTemplatesMethod.invoke(codeTemplateSettingsImpl);
            
            Map modifiedTemplatesMap = (templatesMap == null ? new HashMap(): new HashMap(templatesMap));

            for (CodeTemplate template : templates) {
                modifiedTemplatesMap.remove(template.getAbbreviation());
            }
            
            setCodeTemplatesMethod.invoke(codeTemplateSettingsImpl, modifiedTemplatesMap);
        } catch (SecurityException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalArgumentException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IllegalAccessException e) {
            ErrorManager.getDefault().notify(e);
        } catch (InvocationTargetException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private static ClassLoader systemClassLoader;
    private static Class codeTemplateSettingsImplClass;
    private static Method getMethod;
    private static Method getCodeTemplatesMethod;
    private static Method setCodeTemplatesMethod;
    private static Class codeTemplateDescriptionClass;
    private static Constructor codeTemplateDescriptionConstructor;    
    
    private static void initReflection() {        
        if (systemClassLoader == null) {
            systemClassLoader = Lookup.getDefault().lookup(ClassLoader.class);
            try {
                codeTemplateSettingsImplClass = Class.forName("org.netbeans.lib.editor.codetemplates.storage.CodeTemplateSettingsImpl", true, systemClassLoader);
                getMethod = codeTemplateSettingsImplClass.getDeclaredMethod("get", MimePath.class);
                getCodeTemplatesMethod = codeTemplateSettingsImplClass.getDeclaredMethod("getCodeTemplates");                
                setCodeTemplatesMethod = codeTemplateSettingsImplClass.getDeclaredMethod("setCodeTemplates", Map.class);
                codeTemplateDescriptionClass = Class.forName("org.netbeans.api.editor.settings.CodeTemplateDescription", true, systemClassLoader);
                codeTemplateDescriptionConstructor = codeTemplateDescriptionClass.getConstructor(new Class[] {
                   String.class,
                   String.class,
                   String.class,
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }        
    }

}
