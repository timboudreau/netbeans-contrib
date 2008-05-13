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
package org.netbeans.modules.portalpack.saw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Vihang
 */
public class SAWFrameworkProvider extends WebFrameworkProvider {

    private SAWFrameworkWizardPanel1 sawFrameworkWizardPanel1;

    /** Creates a new instance of WorkflowFrameworkProvider */
    public SAWFrameworkProvider() {
        super(NbBundle.getBundle(SAWFrameworkProvider.class).getString("SAW_FRAMEWORK"), NbBundle.getBundle(SAWFrameworkProvider.class).getString("SAW_FRAMEWORK_DESC"));
    }

    public WebModuleExtender createWebModuleExtender(WebModule wm, ExtenderController controller) {

      //  boolean customizer = (wm != null && isInWebModule(wm));
        sawFrameworkWizardPanel1 = new SAWFrameworkWizardPanel1(this, wm, controller);

        return sawFrameworkWizardPanel1;
    }

    public Set extendImpl(WebModule wm) {
        final FileObject documentBase = wm.getDocumentBase();
        Project project = FileOwnerQuery.getOwner(documentBase);
        try {

            String selectedValue = sawFrameworkWizardPanel1.getSelectedValueFromVisualPanel();
            if (selectedValue.equals("JCAPS")) {
                createPropertyFiles(wm, selectedValue);
                Library bpLibrary = LibraryManager.getDefault().getLibrary("saw"); //NOI18N
                if (bpLibrary != null) {

                    Sources sources = (Sources) project.getLookup().lookup(Sources.class);
                    SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

                    for (int i = 0; i < groups.length; i++) {
                        ProjectClassPathModifier.addLibraries(new Library[]{bpLibrary}, groups[i].getRootFolder(), ClassPath.COMPILE);
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInWebModule(WebModule webModule) {

        try {
             
              ClassPath cp = ClassPath.getClassPath(webModule.getDocumentBase(), ClassPath.COMPILE);
              if(cp == null || cp.findResource("com/sun/saw/Workflow.class") == null) { //NOI18N)
                  return false;
                } 
        } catch (Exception e) {
            return false;

        }
        return true;
    }

    public File[] getConfigurationFiles(WebModule arg0) {
        return null;
    }

    private void createPropertyFiles(WebModule wm, String selectedValue) {
        try {

            final FileObject documentBase = wm.getDocumentBase();
            Project project = FileOwnerQuery.getOwner(documentBase);
            Sources sources = ProjectUtils.getSources(project);
            SourceGroup[] sourceGroup = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            
                    if(sourceGroup.length > 0) {
                            FileObject fileObject = sourceGroup[0].getRootFolder();
                            org.openide.filesystems.FileObject fObject10 = fileObject.createData("ImplementationType", "properties");
                            org.openide.filesystems.FileLock fLock1 = fObject10.lock();
                            java.io.OutputStream ostream = fObject10.getOutputStream(fLock1);
                            ostream.write(("ImplementationType=" + selectedValue).getBytes());
                            ostream.flush();
                            ostream.close();
                            fLock1.releaseLock();
                           /* org.openide.filesystems.FileObject fObject11 = fileObjectArray[i].createData("WorkflowConfig", "properties");
                            org.openide.filesystems.FileLock fLock2 = fObject11.lock();
                            java.io.OutputStream ostream1 = fObject11.getOutputStream(fLock2);
                            ostream1.write(("businessProcess=" + "com.sun.saw.impls.jcaps.JCAPSWorkflow\n").getBytes());
                            ostream1.write(("logFileLocation=" + "Specify location of log file").getBytes());
                            ostream1.flush();
                            ostream1.close();
                            fLock2.releaseLock(); */
                            org.openide.filesystems.FileObject fObject12 = fileObject.createData("WorkflowConfig", "properties");

                            org.openide.filesystems.FileLock fLock3 = fObject12.lock();
                            java.io.OutputStream ostream2 = fObject12.getOutputStream(fLock3);
                            ostream2.write(("#The Workflow Engine to use.\n").getBytes());
                            ostream2.write(("sawworkflowimplclass=" + "com.sun.saw.impls.jcaps.JCAPSWorkflow\n").getBytes());
                            ostream2.write(("# Properties that are needed by the JCAPS Implementation of SAW.\n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.appserverhost=" + "host where jcaps is installed e.g. test.domain.com\n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.iiopport=" + "port where jcaps workflow service is available e.g. 8080\n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.appserverusername=" + "admin user name of jcaps app server e.g. admin\n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.appserverpassword=" + "password of jcaps app server e.g. abc \n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.contextfactory=" + "context factory of workflow service e.g. com.sun.jndi.cosnaming.CNCtxFactory \n").getBytes());
                            ostream2.write(("com.sun.saw.impls.jcaps.JCAPSWorkflow.serviceJndi=" + "jndi context of workflow service provided by jcaps e.g. WorkflowService\n").getBytes());                            
                            ostream2.write(("#Properties that are needed by the JBOSS Implementation of SAW.\n").getBytes());
                            ostream2.write(("jboss.server =" + "Specific JBOSS Server on Which jBPM is running\n").getBytes());
                            ostream2.write(("jboss.password =" + "Value for the server password\n").getBytes());                            
                            ostream2.flush();
                            ostream2.close();
                            fLock3.releaseLock();
                        }
            
            FileObject sawTldFileObject = Repository.getDefault().getDefaultFileSystem().findResource("velocity/templates/SAW");
            FileUtil.copyFile(sawTldFileObject, wm.getWebInf(), "SAW", "tld");

        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    private static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuffer sb = new StringBuffer();
        String lineSep = System.getProperty("line.separator");//NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

    private void createFile(FileObject target, String content, String encoding) throws IOException {
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();

        } finally {
            lock.releaseLock();
        }
    }
}
