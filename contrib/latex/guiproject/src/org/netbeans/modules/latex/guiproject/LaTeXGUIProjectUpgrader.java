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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.api.project.ProjectUtils;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**This class is used to check whether the upgrade of the project metadata should
 * be perfomed, and to perform the appropriate action to upgrade the project.
 *
 * @author Jan Lahoda
 */
public final class LaTeXGUIProjectUpgrader {
    
    public static final String UPGRADE_OPTION = "netbeans.latex.guiproject.upgrade";
    public static final String UPGRADE_IGNORE = "none";
    public static final String UPGRADE_FORCE  = "noask";
    public static final String UPGRADE_COMMON = "common";
    

    /** Creates a new instance of LaTeXGUIProjectUpgrader */
    private LaTeXGUIProjectUpgrader() {
    }
    
    private static LaTeXGUIProjectUpgrader upgrader = null;
    
    /**Get default instance of the project upgrader.
     *
     * @return default project upgrader.
     */
    public static synchronized LaTeXGUIProjectUpgrader getUpgrader() {
        if (upgrader == null)
            upgrader = new LaTeXGUIProjectUpgrader();
        
        return upgrader;
    }
    
    /**Checks whether the project should be upgraded, and performs
     * the upgrade if necessary. In the affected by the @see UPGRADE_OPTION option.
     *
     * @param project project to upgrade
     */
    public void upgrade(LaTeXGUIProject project) {
        checkUpgrade(project);
    }
    
    private static final SpecificationVersion actualVersion = new SpecificationVersion("1.1");
    
    private void checkUpgrade(LaTeXGUIProject project) {
        InputStream  ins  = null;
        OutputStream out  = null;
        FileLock     lock = null;
        
        try {
            EditableProperties properties = new EditableProperties();
            FileObject propertiesFile = project.getProjectDirectory().getFileObject("build-settings", "properties");
            
            if (propertiesFile == null) {
                //strange...
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "During checking for upgrade of project data, properties file not found!");
                return ;
            }
            
            ins = propertiesFile.getInputStream();
            properties.load(ins);
            ins.close();
            ins = null;
            
            String               versionString = (String) properties.getProperty("version-spec");
            SpecificationVersion version       = null;
            
            if (versionString != null) {//see ProjectUpgradeTest.testNoVersionProperty
                try {
                    version = new SpecificationVersion(versionString);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    version = actualVersion;
                }
            } else {
                version = actualVersion;
            }
            
            if (version.compareTo(actualVersion) < 0) {
                //Possibly do upgrade:
                String specVersionLock = (String) properties.getProperty("version-spec-lock");
                
                if (!Boolean.valueOf(specVersionLock).booleanValue()) {
                    //upgrade necessary:
                    SpecificationVersion proposedVersion = doUpgrade(project, version, properties);
                    
                    if (proposedVersion == null) {
                        properties.put("version-spec-lock", "true");
                    } else {
                        properties.put("version-spec", proposedVersion.toString());
                    }
                    
                    lock = propertiesFile.lock();
                    out  = propertiesFile.getOutputStream(lock);
                    
                    properties.store(out);
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            
            if (lock != null)
                lock.releaseLock();
        }
    }
    
    private String getItinerary(SpecificationVersion version) {
        StringBuffer result = new StringBuffer();
        boolean upgraded = true;
        
        while (version.compareTo(actualVersion) < 0 && upgraded) {
            upgraded = false;
            for (int cntr = 0; cntr < upgraders.length; cntr++) {
                Upgrader u = upgraders[cntr];
                
                if (u.getVersionFrom().equals(version)) {
                    result.append(u.getUpgradeItinerary());
                    version = u.getVersionTo();
                    upgraded = true;
                }
            }
        }
        
        return result.toString();
    }
    
    private SpecificationVersion doUpgrade(LaTeXGUIProject project, SpecificationVersion version, EditableProperties properties) {
        String upgradeOption = System.getProperty(UPGRADE_OPTION, UPGRADE_COMMON);
        
        if (UPGRADE_IGNORE.equals(upgradeOption))
            return null;
        
        if (UPGRADE_COMMON.equals(upgradeOption)) {
            String message = NbBundle.getMessage(LaTeXGUIProjectUpgrader.class, "MSG_UpgradeProjectConfirmationMessage", new Object[] {
                ProjectUtils.getInformation(project).getDisplayName(),
                String.valueOf(version),
                String.valueOf(actualVersion),
                getItinerary(version)
            });
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(message, "Upgrade Project", NotifyDescriptor.YES_NO_OPTION);
            
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION)
                return null;
        } else {
            if (!UPGRADE_FORCE.equals(upgradeOption))
                throw new IllegalStateException("Unknown " + UPGRADE_OPTION + " value: " + upgradeOption);
        }
        
        boolean upgraded = true;
        
        while (version.compareTo(actualVersion) < 0 && upgraded) {
            upgraded = false;
            for (int cntr = 0; cntr < upgraders.length; cntr++) {
                Upgrader u = upgraders[cntr];
                
                if (u.getVersionFrom().equals(version)) {
                    u.upgrade(project.getProjectDirectory(), project.getMasterFile(), properties);
                    version = u.getVersionTo();
                    upgraded = true;
                }
            }
        }
        
        return version;
    }
    
    private static abstract class Upgrader {
        public abstract SpecificationVersion getVersionFrom();
        public abstract SpecificationVersion getVersionTo();
        public abstract boolean upgrade(FileObject projectDir, FileObject mainFile, EditableProperties properties);
        public abstract String getUpgradeItinerary();
    }
    
//    private static class Upgrade1to11 extends Upgrader {
//        public SpecificationVersion getVersionFrom() {
//            return new SpecificationVersion("1.0");
//        }
//        
//        public SpecificationVersion getVersionTo() {
//            return new SpecificationVersion("1.1");
//        }
//        
//        public boolean upgrade(FileObject projectDir, FileObject mainFile, EditableProperties properties) {
//            //The 1.0->1.1 upgrade consists of two steps:
//            //1. A new target "gv" showing results in the gv viewer is added.
//            //2. The mainfile reference is relativized if possible.
//            FileLock     lock = null;
//            OutputStream out  = null;
//
//            try {
//                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "LaTeX project: 1.0 to 1.1 upgrade perfomer.");//NOI18N
//                
//                FileObject       buildScriptFile = projectDir.getFileObject("build", "xml");//NOI18N
//                DataObject       buildScript     = DataObject.find(buildScriptFile);
//                AntProjectCookie apc             = (AntProjectCookie) buildScript.getCookie(AntProjectCookie.class);
//                Element          mainEl          = apc.getProjectElement();
//                Document         doc             = mainEl.getOwnerDocument();
//                Set              targets         = TargetLister.getTargets(apc);
//                boolean          gvTargetExists  = false;
//                
//                for (Iterator i = targets.iterator(); i.hasNext(); ) {
//                    TargetLister.Target t = (TargetLister.Target) i.next();
//                    
//                    if ("gv".equals(t.getName())) {
//                        //gv target found, we cannot create a new one
//                        gvTargetExists = true;
//                    }
//                }
//                
//                //<target name="gv" depends="latex2ps">
//                //   <gv mainfile="${mainfile}" />
//                //</target>
//                if (!gvTargetExists) {
//                    Element target = doc.createElement("target");//NOI18N
//                    
//                    target.setAttribute("name", "gv");//NOI18N
//                    target.setAttribute("depends", "latex2ps");//NOI18N
//                    
//                    Element gv = doc.createElement("gv");
//                    
//                    gv.setAttribute("mainfile", "${mainfile}");//NOI18N
//                    
//                    target.appendChild(gv);
//                    
//                    mainEl.appendChild(target);
//                    
//                    //<taskdef name="gv" classname="org.netbeans.modules.latex.ant.tasks.GVAntTask" classpath="${libs.latextasks.classpath}" />:
//                    
//                    Element taskdef = doc.createElement("taskdef");//NOI18N
//                    
//                    taskdef.setAttribute("name", "gv");//NOI18N
//                    taskdef.setAttribute("classname", "org.netbeans.modules.latex.ant.tasks.GVAntTask");//NOI18N
//                    taskdef.setAttribute("classpath", "${libs.latextasks.classpath}");//NOI18N
//                    
//                    mainEl.appendChild(taskdef);
//                } else {
//                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "LaTeX project upgrade: cannot create \"gv\" target as target with the same name alreay exists.");//NOI18N
//                }
//                
//                lock = buildScriptFile.lock();
//                out  = buildScriptFile.getOutputStream(lock);
//                
//                XMLUtil.write(doc, out, "UTF-8");//NOI18N
//                
//                //TODO: step II: relativize the main file:
//                String mainFileName = properties.getProperty("mainfile");
//                File   mainFileFile = new File(mainFileName);
//                
//                if (mainFileFile.exists()) {
//                    properties.put("mainfile", Utilities.findShortestName(FileUtil.toFile(projectDir), mainFileFile));
//                }
//                
//                return true;
//            } catch (IOException e) {
//                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
//                return false;
//            } finally {
//                if (out != null) {
//                    try {
//                        out.close();
//                    } catch (IOException e) {
//                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
//                    }
//                }
//                
//                if (lock != null) {
//                    lock.releaseLock();
//                }
//            }
//        }
//        
//        public String getUpgradeItinerary() {
//            return "-add GhostView (gv) target to the build script\n-relativize the main file\n";
//        }
//    }
    
    private static Upgrader[] upgraders = new Upgrader[] {
//        new Upgrade1to11(),
    };
    
}
