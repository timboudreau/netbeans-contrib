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

package org.netbeans.modules.vcscore;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.masterfs.providers.AnnotationProvider;
import org.netbeans.modules.masterfs.providers.InterceptionListener;
import org.netbeans.modules.vcscore.actions.AddToGroupAction;
import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.SharedClassObject;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;

/**
 * The VCS implementation of annotation provider
 * 
 * @author Martin Entlicher
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.masterfs.providers.AnnotationProvider.class)
public class VcsAnnotationProvider extends AnnotationProvider {
    
    private static final int BADGE_ICON_SHIFT_X = 16;
    private static final int BADGE_ICON_SHIFT_Y = 8;

    private int fileAnnotation;

    private PropertyChangeListener settingsChangeListener;
    private InterceptionListener interception;

    /**
     * Do not call directly! It's registered in
     * META-INF/services/org.netbeans.modules.masterfs.providers.AnnotationProvider
     */
    public VcsAnnotationProvider() {
        GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
        settingsChangeListener = new SettingsPropertyChangeListener();
        settings.addPropertyChangeListener(WeakListeners.propertyChange(settingsChangeListener, settings));
        fileAnnotation = settings.getFileAnnotation();
        interception = new VcsInterception();
    }

    public String annotateName(String name, Set files) {
        if (GeneralVcsSettings.FILE_ANNOTATION_NONE == fileAnnotation) { // No annotation
            return null;
        }
        if (name == null)
            return null;  // Null name, ignore it
        int len = files.size();
        if (len == 0) {// || name.indexOf(getRootDirectory().toString()) >= 0) {
            return null;
        }
        Object[] oo = files.toArray();
        oo = getSharableFiles(oo);
        len = oo.length;
        if (len == 0) {
            return null;
        }

        VcsProvider provider = VcsProvider.getProvider((FileObject) oo[0]);
        if (provider == null) {
            return null;
        }

        String result;
        if (len == 1) {
            FileObject fo = (FileObject) oo[0];
            result = StatusFormat.getStatusAnnotation(name, fo,
                    provider.getAnnotationPattern(),
                    provider.getFileAnnotation(),
                    provider.getPossibleFileStatusInfoMap());
        } else {
            String mergedStatus = mergeStatus(oo);

            if (mergedStatus != null) {
                // all files have the same status
                FileObject fo = (FileObject) oo[0];
                result = StatusFormat.getStatusAnnotation(name, fo,
                        provider.getAnnotationPattern(),
                        provider.getFileAnnotation(),
                        provider.getPossibleFileStatusInfoMap());
            } else {
                // XXX mix, do not annotate at all
                // TODO : do either original implementation or something better here
                FileObject fo = (FileObject) oo[0];
                result = StatusFormat.getStatusAnnotation(name, fo,
                        "${fileName}",
                        provider.getFileAnnotation(),
                        provider.getPossibleFileStatusInfoMap());
            }
        }
        return result;
    }

    public String annotateNameHtml(String name, Set files) {
        if (GeneralVcsSettings.FILE_ANNOTATION_NONE == fileAnnotation) { // No annotation
            return null;
        }
        if (name == null)
            return null;  // Null name, ignore it
        int len = files.size();
        if (len == 0) {// || name.indexOf(getRootDirectory().toString()) >= 0) {
            return null;
        }
        Object[] oo = files.toArray();
        oo = getSharableFiles(oo);
        len = oo.length;
        if (len == 0) {
            return null;
        }

        VcsProvider provider = VcsProvider.getProvider((FileObject) oo[0]);
        if (provider == null) {
            return null;
        }

        String result;
        if (len == 1) {
            FileObject fo = (FileObject) oo[0];
            result = StatusFormat.getHtmlStatusAnnotation(name, fo,
                    provider.getAnnotationPattern(),
                    provider.getFileAnnotation(),
                    provider.getPossibleFileStatusInfoMap());
        } else {
            String mergedStatus = mergeStatus(oo);

            if (mergedStatus != null) {
                // all files have the same status
                FileObject fo = (FileObject) oo[0];
                result = StatusFormat.getHtmlStatusAnnotation(name, fo,
                        provider.getAnnotationPattern(),
                        provider.getFileAnnotation(),
                        provider.getPossibleFileStatusInfoMap());
            } else {
                // XXX mix, do not annotate at all
                // TODO : do either original implementation or something better here
                FileObject fo = (FileObject) oo[0];
                result = StatusFormat.getHtmlStatusAnnotation(name, fo,
                        "${fileName}",
                        provider.getFileAnnotation(),
                        provider.getPossibleFileStatusInfoMap());
            }
        }
        return result;
    }

    public Image annotateIcon(Image icon, int iconType, Set files) {
        Object[] oo = files.toArray();
        oo = getSharableFiles(oo);
        int len = oo.length;
        if (len == 0) {
            return null;
        }

        VcsProvider provider = VcsProvider.getProvider((FileObject) oo[0]);
        if (provider == null) {
            return null;
        }

        if (oo.length == 1) {
            FileObject fileObject = (FileObject) oo[0];
            Turbo.prepareMeta(fileObject);
            FileProperties fprops = Turbo.getMemoryMeta(fileObject);
            String status = FileProperties.getStatus(fprops);
            return badgeByStatus(status, icon, provider);
        } else {
            String mergedStatus = mergeStatus(oo);
            return badgeByStatus(mergedStatus, icon, provider);
        }
    }

    /**
     * Badges icon for given status
     */
    private Image badgeByStatus(String status, Image dflt, VcsProvider provider) {
        Map statusInfoMap = provider.getPossibleFileStatusInfoMap();
        FileStatusInfo statusinfo = (FileStatusInfo) statusInfoMap.get(status);
        if (statusinfo != null) {
            Image badgeIcon = statusinfo.getIcon();
            if (badgeIcon != null) {
                return org.openide.util.Utilities.mergeImages(dflt, badgeIcon, BADGE_ICON_SHIFT_X, BADGE_ICON_SHIFT_Y);
            }
        }
        return null;
    }

    public Action[] actions(Set files) {
        if (files.size() == 0) {
            return null;
        }
        FileObject fo = (FileObject) files.iterator().next();
        VcsProvider provider = VcsProvider.getProvider(fo);
        boolean sharable = true;
        if (provider != null) {
            sharable = false;
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                fo = (FileObject) it.next();
                File file = FileUtil.toFile(fo);
                int sharability = SharabilityQuery.getSharability(file);
                if (sharability != SharabilityQuery.NOT_SHARABLE) {
                    sharable = true;
                    break;
                }
            }
        }
        if (sharable) {
            ArrayList actions = new ArrayList();
            actions.add(SystemAction.get(VcsFSCommandsAction.class));
            if (provider.getVersioningSystem() != null) {
                actions.add(SystemAction.get(VersioningExplorerAction.class));
            }
            actions.add(SystemAction.get(AddToGroupAction.class));
            return (SystemAction[]) actions.toArray(new SystemAction[0]);
        } else {
            return null;
        }
    }

    public InterceptionListener getInterceptionListener() {
        return interception;
    }

    private static Object[] getSharableFiles(Object[] fos) {
        java.util.List sharable = null;
        for (int i = 0; i < fos.length; i++) {
            File file = FileUtil.toFile((FileObject) fos[i]);
            int sharability = SharabilityQuery.getSharability(file);
            if (sharability == SharabilityQuery.NOT_SHARABLE) {
                if (sharable == null) {
                    sharable = new ArrayList(Arrays.asList(fos));
                }
                sharable.remove(fos[i]);
            }
        }
        if (sharable != null) {
            return sharable.toArray();
        } else {
            return fos;
        }
    }

    private static String mergeStatus(Object[] fileObjects) {
        String mergedStatus = null;
        int len = fileObjects.length;
        for (int i = 0; i < fileObjects.length; i++) {
            FileObject fo = (FileObject) fileObjects[i];
            FileProperties fprops = Turbo.getCachedMeta(fo);
            if (mergedStatus == null) {
                mergedStatus = FileProperties.getStatus(fprops);
            } else {
                if (mergedStatus.equals(FileProperties.getStatus(fprops)) == false) {
                    mergedStatus = null;
                    break;
                }
            }
        }
        return mergedStatus;
    }

    /*
    private VcsProvider findProvider(FileObject fo) {
        FSInfo[] infos = FSRegistry.getDefault().getRegistered();
        if (infos.length > 0) {
            for (int i = 0; i < infos.length; i++) {
                VcsProvider provider = infos[i].getExistingProvider();
                if (provider != null) {
                    FileObject root = provider.findResource("");
                    if (FileUtil.isParentOf(root, fo)) {
                        return provider;
                    }
                }
            }
        }
        return null;
    }
     */


    private class SettingsPropertyChangeListener implements PropertyChangeListener {
        public SettingsPropertyChangeListener () {}
        public void propertyChange(final PropertyChangeEvent event) {
            String propName = event.getPropertyName();
            if (GeneralVcsSettings.PROP_FILE_ANNOTATION.equals(propName)) {
                FSInfo[] infos = FSRegistry.getDefault().getRegistered();
                if (infos.length > 0) {
                    GeneralVcsSettings settings = (GeneralVcsSettings) SharedClassObject.findObject(GeneralVcsSettings.class, true);
                    fileAnnotation = settings.getFileAnnotation();
                    for (int i = 0; i < infos.length; i++) {
                        VcsProvider provider = infos[i].getExistingProvider();
                        if (provider != null) {
                            FileObject root =provider.findResource("");
                            try {
                                fireFileStatusChanged(new FileStatusEvent(root.getFileSystem(), false, true));
                            } catch (FileStateInvalidException ex) {
                                // Ignore
                            }
                        }
                    }
                }
                /*
                Set foSet = new HashSet();
                Enumeration e = existingFileObjects(root);
                while (e.hasMoreElements()) {
                    foSet.add(e.nextElement());
                }
                fireFileStatusChanged(new FileStatusEvent(VcsFileSystem.this, foSet, false, true));
                 */
            }
        }
    }

}
