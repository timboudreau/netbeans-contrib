/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;

/**
 * See enhancement #11911.
 * @author Jesse Glick
 */
public final class CookieClassList {

    // XXX does this need to be complete? Or are only Node.Cookie subtypes required?
    private static final String[] CLASS_NAMES = {
        "javax.jmi.reflect.RefObject", // NOI18N
        "org.apache.tools.ant.module.api.AntProjectCookie", // NOI18N
        "org.apache.tools.ant.module.api.ElementCookie", // NOI18N
        "org.apache.tools.ant.module.api.IntrospectionCookie", // NOI18N
        "org.netbeans.api.project.Project", // NOI18N
        "org.netbeans.api.project.ProjectInformation", // NOI18N
        "org.netbeans.api.project.Sources", // NOI18N
        "org.netbeans.modules.xml.core.cookies.CookieManagerCookie", // NOI18N
        "org.netbeans.modules.xml.tax.cookies.TreeDocumentCookie", // NOI18N
        "org.netbeans.modules.xml.tax.cookies.TreeEditorCookie", // NOI18N
        "org.netbeans.spi.java.classpath.ClassPathProvider", // NOI18N
        "org.netbeans.spi.java.queries.AccessibilityQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.MultipleRootsUnitTestForSourceQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.SourceLevelQueryImplementation", // NOI18N
        "org.netbeans.spi.java.queries.UnitTestForSourceQueryImplementation", // NOI18N
        "org.netbeans.spi.project.ActionProvider", // NOI18N
        "org.netbeans.spi.project.AuxiliaryConfiguration", // NOI18N
        "org.netbeans.spi.project.CacheDirectoryProvider", // NOI18N
        "org.netbeans.spi.project.SubprojectProvider", // NOI18N
        "org.netbeans.spi.project.ant.AntArtifactProvider", // NOI18N
        "org.netbeans.spi.project.support.ant.ProjectXmlSavedHook", // NOI18N
        "org.netbeans.spi.project.ui.CustomizerProvider", // NOI18N
        "org.netbeans.spi.project.ui.LogicalViewProvider", // NOI18N
        "org.netbeans.spi.project.ui.PrivilegedTemplates", // NOI18N
        "org.netbeans.spi.project.ui.ProjectOpenedHook", // NOI18N
        "org.netbeans.spi.project.ui.RecommendedTemplates", // NOI18N
        "org.netbeans.spi.queries.FileBuiltQueryImplementation", // NOI18N
        "org.netbeans.spi.queries.SharabilityQueryImplementation", // NOI18N
        "org.openide.cookies.CloseCookie", // NOI18N
        "org.openide.cookies.EditCookie", // NOI18N
        "org.openide.cookies.EditorCookie", // NOI18N
        "org.openide.cookies.EditorCookie$Observable", // NOI18N
        "org.openide.cookies.InstanceCookie", // NOI18N
        "org.openide.cookies.InstanceCookie$Of", // NOI18N
        "org.openide.cookies.LineCookie", // NOI18N
        "org.openide.cookies.OpenCookie", // NOI18N
        "org.openide.cookies.PrintCookie", // NOI18N
        "org.openide.cookies.SaveCookie", // NOI18N
        "org.openide.cookies.SourceCookie", // NOI18N
        "org.openide.cookies.SourceCookie$Editor", // NOI18N
        "org.openide.cookies.ViewCookie", // NOI18N
        "org.openide.loaders.DataFolder", // NOI18N
        "org.openide.loaders.DataObject", // NOI18N
        "org.openide.loaders.XMLDataObject$Processor", // NOI18N
        "org.openide.nodes.Index", // NOI18N
        "org.openide.src.ClassElement", // NOI18N
        "org.openide.src.ConstructorElement", // NOI18N
        "org.openide.src.FieldElement", // NOI18N
        "org.openide.src.InitializerElement", // NOI18N
        "org.openide.src.MethodElement", // NOI18N
        "org.openide.src.SourceElement", // NOI18N
        "org.openidex.search.SearchInfo", // NOI18N
    };
    
    private static Class[] clazzes;
    
    public static synchronized Class[] getCookieClasses() {
        if (clazzes == null) {
            List/*<Class>*/ _clazzes = new ArrayList();
            ClassLoader l = Thread.currentThread().getContextClassLoader();
            for (int i = 0; i < CLASS_NAMES.length; i++) {
                try {
                    _clazzes.add(Class.forName(CLASS_NAMES[i], true, l));
                } catch (ClassNotFoundException e) {
                    // ignore, module not available or whatever
                }
            }
            clazzes = (Class[]) _clazzes.toArray(new Class[_clazzes.size()]);
        }
        return clazzes;
    }
    
}
