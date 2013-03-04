/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.licensechanger.wizard.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.*;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.modules.licensechanger.wizard.LineEndingsPanel;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Properties;
import org.openide.WizardDescriptor;
import org.openide.filesystems.*;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;

/**
 * @author Tim Boudreau
 * @author Nils Hoffmann (Refactoring)
 */
public class LicenseChangerRunnable implements Runnable {

    private final WizardDescriptor wizard;

    public LicenseChangerRunnable(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void run() {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Changing license headers");
        try {
            /*
             * If KEY_STORE_IN_USER_PROPERTIES==true, store copyright holder in 
             * User.properties under key 'user' so that NetBeans new file template
             * engine picks up the correct placeholder.
             * 
             * XXX Is there a way of doing this for an inidividual project only?
             */
            if ((Boolean) wizard.getProperty(WizardProperties.KEY_STORE_IN_USER_PROPERTIES)) {
                ProjectManager.mutex().writeAccess(new Runnable() {
                    @Override
                    public void run() {
                        String copyrightHolder = (String) wizard.getProperty(WizardProperties.KEY_COPYRIGHT_HOLDER);
                        if (copyrightHolder != null && !copyrightHolder.trim().isEmpty()) {
                            try {
                                FileObject userProperties = FileUtil.getConfigFile("Templates/Properties/User.properties");
                                InputStream istream = userProperties.getInputStream();
                                EditableProperties props = new EditableProperties(true);
                                try {
                                    props.load(istream);
                                } finally {
                                    istream.close();
                                }
                                props.setProperty("user", copyrightHolder);
                                OutputStream ostream = userProperties.getOutputStream();
                                try {
                                    props.store(ostream);
                                } finally {
                                    ostream.close();
                                }
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                });
            }
            @SuppressWarnings("unchecked")
            Set<FileChildren.FileItem> items = (Set<FileChildren.FileItem>) wizard.getProperty(WizardProperties.KEY_ITEMS);
            final String licenseText = (String) wizard.getProperty(WizardProperties.KEY_LICENSE_TEXT);
            final String licenseName = (String) wizard.getProperty(WizardProperties.KEY_LICENSE_NAME);
            int ix = 0;
            int max = items.size();
            handle.start(max);
            Charset enc;
            /*
             * Iterate and transform all selected files 
             */
            for (FileChildren.FileItem item : items) {
                handle.progress(item.getFile().getNameExt(), ix);
                try {
                    String content = FileLoader.loadFile(item.file);
                    String nue = item.handler.transform(content, licenseText, wizard.getProperties());
                    LineEndingPreference pref = LineEndingsPanel.getLineEndingPrefs();
                    nue = LineEndingPreference.convertLineEndings(pref, content, nue);

                    enc = FileEncodingQuery.getEncoding(item.file);
                    byte[] bytes;
                    try {
                        bytes = nue.getBytes(enc.name());
                    } catch (UnsupportedEncodingException e) {
                        //properties files get resource_bundle_charset
                        bytes = nue.getBytes(FileEncodingQuery.getDefaultEncoding().name());
                    }
                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    BufferedOutputStream out = new BufferedOutputStream(item.file.getOutputStream());
                    try {
                        FileUtil.copy(in, out);
                    } finally {
                        out.close();
                        in.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                ix++;
            }
            /*
             * Check whether we should update the project license. Works for 
             * ant-based and maven-based projects. 
             
             * XXX Gradle support is yet to be included :-).
             */
            Boolean updateProjectLicense = (Boolean) wizard.getProperty(WizardProperties.KEY_UPDATE_DEFAULT_PROJECT_LICENSE);
            if (updateProjectLicense) {
                Project project = (Project) wizard.getProperty(WizardProperties.KEY_PROJECT);
                Sources source = ProjectUtils.getSources(project);
                for (SourceGroup group : source.getSourceGroups(Sources.TYPE_GENERIC)) {
                    try {
                        //can this be done more generic? 
                        FileObject nbprojectDir = group.getRootFolder().getFileObject("nbproject");
                        if (nbprojectDir != null) {
                            //create if non-existent
                            final FileObject projectProps = FileUtil.createData(nbprojectDir, "project.properties");
                            boolean hasProjectProperties = group.contains(projectProps);
                            if (hasProjectProperties) {
                                ProjectManager.mutex().writeAccess(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            InputStream istream = projectProps.getInputStream();
                                            EditableProperties props = new EditableProperties(true);
                                            try {
                                                props.load(istream);
                                            } finally {
                                                istream.close();
                                            }
                                            props.setProperty("project.license", licenseName);
                                            OutputStream ostream = projectProps.getOutputStream();
                                            try {
                                                props.store(ostream);
                                            } finally {
                                                ostream.close();
                                            }
                                        } catch (IOException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                    }
                                });

                            }
                        } else {
                            //check for pom.xml
                            FileObject pom = group.getRootFolder().getFileObject("pom.xml");
                            if (pom != null) {
                                //found pom-based maven project
                                // XXX do we need to use ProjectManager.mutex().writeAccess here too?
                                Utilities.performPOMModelOperations(pom, Arrays.asList(new ModelOperation<POMModel>() {
                                    @Override
                                    public void performOperation(POMModel model) {
                                        Properties props = model.getProject().getProperties();
                                        if (props == null) {
                                            props = model.getFactory().createProperties();
                                            props.setProperty("netbeans.hint.license", licenseName);
                                            model.getProject().setProperties(props);
                                        } else {
                                            props.setProperty("netbeans.hint.license", licenseName);
                                        }
                                    }
                                }));
                            }
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } finally {
            handle.finish();
        }
    }
}
