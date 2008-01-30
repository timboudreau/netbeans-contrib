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
package org.netbeans.modules.clearcase;

import org.netbeans.modules.clearcase.util.ClearcaseUtils;
import org.netbeans.modules.versioning.spi.VCSAnnotator;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.spi.VersioningSupport;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.clearcase.ui.checkin.CheckinAction;
import org.netbeans.modules.clearcase.ui.status.RefreshAction;
import org.netbeans.modules.clearcase.ui.status.ShowPropertiesAction;
import org.netbeans.modules.clearcase.ui.add.AddAction;
import org.netbeans.modules.clearcase.ui.add.AddToRepositoryAction;
import org.netbeans.modules.clearcase.ui.checkout.CheckoutAction;
import org.netbeans.modules.clearcase.ui.checkout.UncheckoutAction;
import org.netbeans.modules.clearcase.ui.update.UpdateAction;
import org.netbeans.modules.clearcase.ui.update.MergeAction;
import org.netbeans.modules.clearcase.ui.diff.DiffAction;
import org.netbeans.modules.clearcase.ui.IgnoreAction;
import org.netbeans.modules.clearcase.ui.texthistory.TextHistoryAction;
import org.netbeans.modules.clearcase.ui.history.ViewRevisionAction;
import org.netbeans.modules.clearcase.ui.history.BrowseHistoryAction;
import org.netbeans.modules.clearcase.ui.history.BrowseVersionTreeAction;
import org.openide.util.Utilities;

import javax.swing.*;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import org.netbeans.modules.clearcase.client.status.FileStatus;
import org.netbeans.modules.clearcase.client.status.FileVersionSelector;
import org.netbeans.modules.clearcase.ui.AnnotateAction;
import org.netbeans.modules.versioning.util.SystemActionBridge;
import org.netbeans.modules.diff.PatchAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;


/**
 * Responsible for coloring file labels and file icons in the IDE and providing IDE with menu items.
 * 
 * @author Maros Sandor
 */
public class ClearcaseAnnotator extends VCSAnnotator {
    
    /*
    newLocallyFormat = <font color="#008000">{0}</font>{1}
    addedLocallyFormat = <font color="#008000">{0}</font>{1}
    modifiedLocallyFormat = <font color="#0000FF">{0}</font>{1}
    removedLocallyFormat = <font color="#999999">{0}</font>{1}
    deletedLocallyFormat = <font color="#999999">{0}</font>{1}
    newInRepositoryFormat = <font color="#000000">{0}</font>{1}
    modifiedInRepositoryFormat = <font color="#000000">{0}</font>{1}
    removedInRepositoryFormat = <font color="#000000">{0}</font>{1}
    conflictFormat = <font color="#FF0000">{0}</font>{1}
    mergeableFormat = <font color="#0000FF">{0}</font>{1}
    excludedFormat = <font color="#999999">{0}</font>{1}
     */  
    
    private static MessageFormat newLocallyFormat = new MessageFormat("<font color=\"#008000\">{0}</font>{1}");
    private static MessageFormat checkedoutFormat = new MessageFormat("<font color=\"#0000FF\">{0}</font>{1}");
    private static MessageFormat hijackedFormat = new MessageFormat("<font color=\"#FF0000\">{0}</font>{1}");
    private static MessageFormat ignoredFormat = new MessageFormat("<font color=\"#999999\">{0}</font>{1}");
    private static MessageFormat removedFormat = new MessageFormat("<font color=\"#999999\">{0}</font>{1}");
    private static MessageFormat missingFormat = new MessageFormat("<font color=\"#999999\">{0}</font>{1}");
    private static MessageFormat eclipsedFormat = new MessageFormat("<s><font color=\"#008000\">{0}</font></s>{1}");
    
    
    private static final Pattern lessThan = Pattern.compile("<");  // NOI18N

    private static final int STATUS_BADGEABLE =         
        FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY | FileInformation.STATUS_VERSIONED_UPTODATE |
        FileInformation.STATUS_VERSIONED_CHECKEDOUT_RESERVED | FileInformation.STATUS_VERSIONED_CHECKEDOUT_UNRESERVED | 
        FileInformation.STATUS_VERSIONED_HIJACKED | FileInformation.STATUS_NOTVERSIONED_ECLIPSED;;
    
    private static final int STATUS_TEXT_ANNOTABLE = FileInformation.STATUS_NOTVERSIONED_IGNORED | 
            FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY | FileInformation.STATUS_VERSIONED_UPTODATE |
            FileInformation.STATUS_VERSIONED_CHECKEDOUT_RESERVED | FileInformation.STATUS_VERSIONED_CHECKEDOUT_UNRESERVED | 
            FileInformation.STATUS_VERSIONED_HIJACKED | FileInformation.STATUS_NOTVERSIONED_ECLIPSED;

    private MessageFormat format;     
    private String emptyFormat;
    
    private FileStatusCache cache;    

    public ClearcaseAnnotator() {
        cache = Clearcase.getInstance().getFileStatusCache();
        format = new MessageFormat("[{0}; {1}]"); // TODO {0} - label, {1} - version
        emptyFormat = format.format(new String[] {"", ""} , new StringBuffer(), null).toString().trim();
    }

    public String annotateName(String name, VCSContext context) {        
        int includeStatus = 
                FileInformation.STATUS_VERSIONED_UPTODATE | 
                FileInformation.STATUS_LOCAL_CHANGE | 
                FileInformation.STATUS_NOTVERSIONED_IGNORED | 
                FileInformation.STATUS_NOTVERSIONED_ECLIPSED |                 
                FileInformation.STATUS_VERSIONED_CHECKEDOUT_RESERVED | 
                FileInformation.STATUS_VERSIONED_CHECKEDOUT_UNRESERVED;
                
        FileInformation mostImportantInfo = null;
        File mostImportantFile = null;
        boolean folderAnnotation = false;
        
        for (File file : context.getRootFiles()) {
            FileInformation info = getCachedInfo(file);
            if(info == null) {
                return name;
            }
            int status = info.getStatus();
            if ((status & includeStatus) == 0) continue;
            
            if (isMoreImportant(info, mostImportantInfo)) {
                mostImportantInfo = info;
                mostImportantFile = file;
                folderAnnotation = file.isDirectory();
            }
        }

        if (folderAnnotation == false && context.getRootFiles().size() > 1) {
            folderAnnotation = !Utils.shareCommonDataObject(context.getRootFiles().toArray(new File[context.getRootFiles().size()]));
        }

        if (mostImportantInfo == null) return null;
        return folderAnnotation ? 
                annotateNameHtml(name, mostImportantInfo, mostImportantFile) : 
                annotateNameHtml(name, mostImportantInfo, mostImportantFile);
    }

    public Image annotateIcon(Image icon, VCSContext context) {
        boolean folderAnnotation = false;       
        for (File file : context.getRootFiles()) {
            if (file.isDirectory()) {
                folderAnnotation = true;
                break;
            }
        }
        
        if (folderAnnotation == false && context.getRootFiles().size() > 1) {
            folderAnnotation = !Utils.shareCommonDataObject(context.getRootFiles().toArray(new File[context.getRootFiles().size()]));
        }

        if (folderAnnotation == false) {
            return null;
        }

        boolean isVersioned = false;
        for (Iterator i = context.getRootFiles().iterator(); i.hasNext();) {
            File file = (File) i.next();
            FileInformation info = getCachedInfo(file);
            if(info == null) {
                return null;
            }
            if ((info.getStatus() & STATUS_BADGEABLE) != 0) {  
                isVersioned = true;
                break;
            }
        }
        if (!isVersioned) return null;
        
        boolean allExcluded = true;
        boolean modified = false;
        
//        Map modifiedFiles = new HashMap();
//        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
//            File file = (File) i.next();
//            FileInformation info = (FileInformation) map.get(file);
//            if ((info.getCachedStatus() & FileInformation.STATUS_LOCAL_CHANGE) != 0) modifiedFiles.put(file, info);
//        }

        for (File root : context.getRootFiles()) {
            Map<File, FileInformation> modifiedFiles = cache.getAllModifiedValues(root); // XXX should go only after files from the context
            
            if (VersioningSupport.isFlat(root)) {
                
                for (File file : modifiedFiles.keySet()) {
                    
                    if (file.getParentFile().equals(root)) {
                        FileInformation info = (FileInformation) modifiedFiles.get(file);
                        if (info.isDirectory()) continue;
                        modified = true;
                        allExcluded &= isExcludedFromCommit(file.getAbsolutePath());
                    }
                }
                
            } else {
                // TODO should go recursive!
                for (File file : modifiedFiles.keySet()) {
                    
                    if (Utils.isAncestorOrEqual(root, file)) {
                        FileInformation info = (FileInformation) modifiedFiles.get(file);
                        // XXX
//                        if ((status == FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY || status == FileInformation.STATUS_VERSIONED_ADDEDLOCALLY) && file.equals(mf)) {
//                            continue;
//                        }
//                        if (status == FileInformation.STATUS_VERSIONED_CONFLICT) {
//                            Image badge = Utilities.loadImage("org/netbeans/modules/clearcase/resources/icons/conflicts-badge.png", true); // NOI18N
//                            return Utilities.mergeImages(icon, badge, 16, 9);
//                        }
                        modified = true;
                        allExcluded &= isExcludedFromCommit(file.getAbsolutePath());
                    }
                    
                }
            }
        }

        if (modified && !allExcluded) {
            Image badge = Utilities.loadImage("org/netbeans/modules/clearcase/resources/icons/modified-badge.png", true); // NOI18N
            return Utilities.mergeImages(icon, badge, 16, 9);
        } else {
            return null;
        }
    }
    
    public Action[] getActions(VCSContext ctx, VCSAnnotator.ActionDestination destination) {
        Lookup context = ctx.getElements();
        List<Action> actions = new ArrayList<Action>(20);
        if (destination == VCSAnnotator.ActionDestination.MainMenu) {
            actions.add(new CheckoutAction("Checkout...", ctx));
            actions.add(new UncheckoutAction("Undo Checkout", ctx));
            actions.add(new AddAction("Add To Source Control...", ctx));
            actions.add(null);
            //actions.add(SystemAction.get(RefreshAction.class));
            actions.add(new DiffAction("Diff", ctx));
            actions.add(new UpdateAction("Update", ctx));
            actions.add(new MergeAction("Merge", ctx));
            actions.add(new CheckinAction("Checkin...", ctx));
            actions.add(null);
//            actions.add(SystemAction.get(ExportDiffAction.class));
            actions.add(SystemAction.get(PatchAction.class));
            actions.add(null);
            actions.add(new AnnotateAction(ctx, Clearcase.getInstance().getAnnotationsProvider(ctx)));
            actions.add(new ViewRevisionAction("View Revision...", ctx));
            actions.add(new TextHistoryAction("List History", ctx));
            actions.add(new BrowseHistoryAction("Browse History", ctx));
            actions.add(new BrowseVersionTreeAction("Browse Version Tree", ctx));
            actions.add(null);
            actions.add(new IgnoreAction(ctx));
            actions.add(new ShowPropertiesAction("Show Properties", ctx));
//            actions.add(new RemoveAction("Remove Name from Directory...", ctx));
        } else {
            boolean noneVersioned = isNothingVersioned(ctx);
            if (noneVersioned) {
                actions.add(new AddToRepositoryAction("Import into Clea&rcase Repository...", ctx));
            } else {
                actions.add(new CheckoutAction("Checkout...", ctx));
                actions.add(new UncheckoutAction("Undo Checkout", ctx));
                actions.add(new AddAction("Add To Source Control...", ctx));
                actions.add(null);
                actions.add(SystemActionBridge.createAction(SystemAction.get(RefreshAction.class), "Show Changes", context));
                actions.add(new DiffAction("Diff", ctx));
                actions.add(new UpdateAction("Update", ctx));
                actions.add(new MergeAction("Merge", ctx));
                actions.add(new CheckinAction("Checkin...", ctx));
                actions.add(null);
                actions.add(new AnnotateAction(ctx, Clearcase.getInstance().getAnnotationsProvider(ctx)));
                actions.add(new ViewRevisionAction("View Revision...", ctx));
                actions.add(new TextHistoryAction("List History", ctx));
                actions.add(new BrowseHistoryAction("Browse History", ctx));
                actions.add(new BrowseVersionTreeAction("Browse Version Tree", ctx));
                actions.add(null);
                actions.add(new IgnoreAction(ctx));
                actions.add(new ShowPropertiesAction("Show Properties", ctx));
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }    
    
    private static boolean isNothingVersioned(VCSContext ctx) {        
        for (File file : ctx.getFiles()) {
            FileInformation info = getCachedInfo(file);
            if (info == null || (info.getStatus() & FileInformation.STATUS_MANAGED) != 0) return false;
        }
        return true;
    }
    
    public String annotateNameHtml(File file, FileInformation info) {
        return annotateNameHtml(file.getName(), info, file);
    }
    
    public String annotateNameHtml(String name, FileInformation info, File file) {        
        name = htmlEncode(name);

        int status = info.getStatus();
        String textAnnotation;
        boolean annotationsVisible = VersioningSupport.getPreferences().getBoolean(VersioningSupport.PREF_BOOLEAN_TEXT_ANNOTATIONS_VISIBLE, false);
        if (annotationsVisible && file != null && (status & STATUS_TEXT_ANNOTABLE) != 0) {
            if (format != null) {
                textAnnotation = formatAnnotation(info, file);
            } else {                                
                String statusText = info.getShortStatusText();
                if(!statusText.equals("")) {
                    textAnnotation = " [" + info.getShortStatusText() + "]"; // NOI18N
                } else {
                    textAnnotation = "";
                }                
            }
        } else {
            textAnnotation = ""; // NOI18N
        }
        if (textAnnotation.length() > 0) {
            textAnnotation = NbBundle.getMessage(ClearcaseAnnotator.class, "textAnnotation", textAnnotation); 
        }
        
        if (info.getStatus() == FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY) {
            return newLocallyFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_VERSIONED_CHECKEDOUT_RESERVED | 
                   info.getStatus() == FileInformation.STATUS_VERSIONED_CHECKEDOUT_UNRESERVED) 
        {
            return checkedoutFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_VERSIONED_HIJACKED) 
        {
            return hijackedFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_NOTVERSIONED_IGNORED ) {
            return ignoredFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_NOTVERSIONED_ECLIPSED ) {
            return eclipsedFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_VERSIONED_CHECKEDOUT_BUT_REMOVED ) {
            return removedFormat.format(new Object [] { name, textAnnotation });
        } else if (info.getStatus() == FileInformation.STATUS_VERSIONED_LOADED_BUT_MISSING ) {
            return missingFormat.format(new Object [] { name, textAnnotation });
        }         
                
        return name;
    }
    
    /**
     * Applies custom format.
     */
    private String formatAnnotation(FileInformation info, File file) {
        String statusString = "";  // NOI18N
        int status = info.getStatus();
        if (status != FileInformation.STATUS_VERSIONED_UPTODATE) {
            statusString = info.getShortStatusText();
        }

        String revisionString = "";     // NOI18N
        FileStatus fileStatus = info.getStatus(file);
        if (fileStatus != null) {
            FileVersionSelector version = fileStatus.getOriginVersion();
            if(version != null) {
                revisionString = fileStatus.getOriginVersion().getVersionSelector();                
            }            
        }
        
        Object[] arguments = new Object[] {
            statusString,
            revisionString
        };
                
        String annotation = format.format(arguments, new StringBuffer(), null).toString().trim();    
        if(annotation.equals(emptyFormat)) {
            return "";            
        } else {
            return " " + annotation;
        }
    }
    
    private String htmlEncode(String name) {
        if (name.indexOf('<') == -1) return name;
        return lessThan.matcher(name).replaceAll("&lt;"); // NOI18N
    }    

    private boolean isMoreImportant(FileInformation a, FileInformation b) {
        if (b == null) return true;
        if (a == null) return false;
        return ClearcaseUtils.getComparableStatus(a.getStatus()) < ClearcaseUtils.getComparableStatus(b.getStatus());
    }        
    
    private boolean isExcludedFromCommit(String absolutePath) {
        // TODO
        return false;
    }        

    private static FileInformation getCachedInfo(File file) {        
        FileStatusCache cache = Clearcase.getInstance().getFileStatusCache();
        FileInformation info = cache.getCachedInfo(file);
        if(info == null) {
            cache.refreshAsync(file);
        }
        return info;
    }

}
