/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.vcscore.grouping.AddToGroupDialog;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.grouping.VcsGroupSettings;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.SharedClassObject;

/**
 * Yet unimplemented file system utilities
 * 
 * @author Martin Entlicher
 */
public class VcsFSUtils {
    
    private static VcsFSUtils vcsFSUtils;
    
    private Map lockedFilesToBeModified;

    /** Creates a new instance of VcsFSUtils */
    private VcsFSUtils() {
        lockedFilesToBeModified = new HashMap();
    }
    
    public static synchronized VcsFSUtils getDefault() {
        if (vcsFSUtils == null) {
            vcsFSUtils = new VcsFSUtils();
        }
        return vcsFSUtils;
    }
    
    /**
     * Lock the files so that they can not be modified in the IDE.
     * This is necessary for commands, that make changes to the processed files.
     * It's crutial, that the file does not get modified twice - externally via
     * the update command and internally (e.g. through the Editor).
     * One <b>MUST</b> call {@link #unlockFilesToBeModified} after the command
     * finish.
     * @param path The path of the file to be locked or directory in which all
     *             files will be locked.
     * @param recursively Whether the files in directories should be locked recursively.
     */
    public void lockFilesToBeModified(String path, boolean recursively) {
        if (".".equals(path)) path = "";
        synchronized (lockedFilesToBeModified) {
            // Multiple locks are not considered. It's locked just once.
            lockedFilesToBeModified.put(path, recursively ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    /**
     * Unlock the files that were previously locked by {@link #lockFilesToBeModified}
     * method. It's necessary to call this method with appropriate arguments after
     * the command finish so that the user can edit the files.
     */
    public void unlockFilesToBeModified(String path, boolean recursively) {
        if (".".equals(path)) path = "";
        synchronized (lockedFilesToBeModified) {
            lockedFilesToBeModified.remove(path);
        }
    }
    
    private static final Object GROUP_LOCK = new Object();

    public void fileChanged(final FileObject fo) {
        // Fire the change asynchronously to prevent deadlocks.
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                FileProperties fprops = Turbo.getMeta(fo);
                String oldStatus = FileProperties.getStatus(fprops);
                VcsProvider provider = VcsProvider.getProvider(fo);
                if (provider == null) {
                    return ;
                }
                if (!provider.getNotModifiableStatuses().contains(oldStatus) && Statuses.getUnknownStatus().equals(oldStatus) == false) {
                    String status = FileStatusInfo.MODIFIED.getName();
                    Map tranls = provider.getGenericStatusTranslation();
                    if (tranls != null) {
                        status = (String) tranls.get(status);
                        if (status == null) {
                            // There's no mapping, use the generic status name!
                            status = FileStatusInfo.MODIFIED.getName();
                        }
                    }
                    FileProperties updated = new FileProperties(fprops);
                    updated.setName(fo.getNameExt());
                    updated.setStatus(status);
                    Turbo.setMeta(fo, updated);
                }
                provider.callFileChangeHandler(fo, oldStatus);
                VcsGroupSettings grSettings = (VcsGroupSettings) SharedClassObject.findObject(VcsGroupSettings.class, true);
                if (!grSettings.isDisableGroups()) {
                    if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_TO_DEFAULT
                    || grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {

                        if (fo != null) {
                            try {
                                int sharability = SharabilityQuery.getSharability(FileUtil.toFile(fo));
                                if (sharability != SharabilityQuery.NOT_SHARABLE) {
                                    DataObject dobj = DataObject.find(fo);
                                    synchronized (GROUP_LOCK) {
                                        DataShadow shadow = GroupUtils.findDOInGroups(dobj);
                                        if (shadow == null) {
                                            // it doesn't exist in groups, add it..
                                            if (grSettings.getAutoAddition() == VcsGroupSettings.ADDITION_ASK) {
                                                AddToGroupDialog.openChooseDialog(dobj);
                                            } else {
                                                GroupUtils.addToDefaultGroup(new Node[] {dobj.getNodeDelegate()});
                                            }
                                        }
                                    }
                                }
                            } catch (DataObjectNotFoundException exc) {
                            }
                        }
                    }
                }
            }
        });
    }
    
}
