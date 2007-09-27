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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
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
/*
 * SvnCheckoutHandler.java
 *
 * Created on March 1, 2007, 9:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.doap;

import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.SvnModuleConfig;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.client.SvnProgressSupport;
import org.netbeans.modules.subversion.ui.browser.RepositoryPaths;
import org.netbeans.modules.subversion.ui.checkout.CheckoutAction;
import org.netbeans.modules.subversion.ui.checkout.CheckoutCompleted;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 *
 * @author Tim Boudreau
 */
final class SvnCheckoutHandler implements CheckoutHandler {
    private final String url;
    private final String tag;
    private final File dest;
    private final ArrayList<String> paths;
    private final String rev;
    
    public SvnCheckoutHandler(String url, String tag, String rev, ArrayList<String> paths, File dest) {
        this.url = url;
        this.tag = tag;
        this.dest = dest;
        this.paths = paths;
        this.rev = rev;
    }
    
    public String checkout(final ProgressHandle progress) {
        try {
            assert !EventQueue.isDispatchThread();
            final SVNUrl repository = new SVNUrl(url);
            final File file = dest;
            final RepositoryFile[] repositoryFiles = getRepositoryFiles(repository,
                    rev, paths);
            //        progress.start();
            
            //FIXME - passed in progress handle is not used by checkout for
            //some reason, but system one will be.
            final ProgressHandle progress2 = ProgressHandleFactory.createHandle("SVN Checkout");
            
            SvnProgressSupport support = new SvnProgressSupport() {
                protected ProgressHandle getProgressHandle() {
                    return progress2;
                }
                
                public void perform() {
                    final SvnClient client;
                    try {
                        client = Subversion.getInstance().getClient(repository);
                    } catch (SVNClientException ex) {
                        Exceptions.printStackTrace(ex); // should not happen
                        return;
                    }
                    try {
                        setDisplayName(java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("LBL_Checkout_Progress"));
                        CheckoutAction.checkout(client, repository, repositoryFiles, file, true, this);
                    } catch (SVNClientException ex) {
                        annotate(ex);
                        return;
                    }
                    if(isCanceled()) {
                        return;
                    }
                    boolean atWorkingDirLevel = true; //XXX ?
                    
                    setDisplayName(java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("LBL_ScanFolders_Progress"));
                    if (SvnModuleConfig.getDefault().getShowCheckoutCompleted()) {
                        String[] folders;
                        if(atWorkingDirLevel) {
                            folders = new String[1];
                            folders[0] = "."; // NOI18N
                        } else {
                            folders = new String[repositoryFiles.length];
                            for (int i = 0; i < repositoryFiles.length; i++) {
                                if(isCanceled()) {
                                    return;
                                }
                                if(repositoryFiles[i].isRepositoryRoot()) {
                                    folders[i] = "."; // NOI18N
                                } else {
                                    folders[i] = repositoryFiles[i].getFileUrl().getLastPathSegment();
                                }
                            }
                        }
                        CheckoutCompleted cc = new CheckoutCompleted(file, folders, true);
                        if(isCanceled()) {
                            return;
                        }
                        cc.scanForProjects(this);
                    }
                }
            };
            support.start(Subversion.getInstance().getRequestProcessor(repository), repository, java.util.ResourceBundle.getBundle("org/netbeans/modules/subversion/ui/checkout/Bundle").getString("LBL_Checkout_Progress"));
        } catch (MalformedURLException mre) {
            Exceptions.printStackTrace(mre);
        }
        return null;
    }
    
    public RepositoryFile[] getRepositoryFiles(SVNUrl repositoryUrl, String rev, ArrayList<String> paths) throws MalformedURLException, NumberFormatException {
        SVNRevision revision = getRevision(rev);
        //        if(defaultPath == null) {
        //            RepositoryFile rf = new RepositoryFile(repositoryUrl, repositoryFile.getFileUrl(), revision);
        //            return new RepositoryFile[] {rf};
        //        }
        
        if(paths == null ) {
            return new RepositoryFile[] { new RepositoryFile(repositoryUrl, "", revision) } ;
        }
        RepositoryFile[] ret = new RepositoryFile[paths.size()];
        
        for (int i=0; i < paths.size(); i++) {            
            String path = paths.get(i).trim();
            String repositoryUrlString = repositoryUrl.toString();            
            if (path.startsWith("file://")  ||   // NOI18N
                    path.startsWith("http://")  ||   // NOI18N
                    path.startsWith("https://") ||   // NOI18N
                    path.startsWith("svn://")   ||   // NOI18N
                    path.startsWith("svn+ssh://")) { // NOI18N   // XXX check only
                //for svn+ and concat the remaining part from
                //the protocol
                // must be a complete URL
                // so check if it matches with the given repository URL
                if(path.startsWith(repositoryUrlString)) {
                    // lets take only the part without the repository base URL
                    ret[i] = new RepositoryFile(repositoryUrl, path.substring(repositoryUrlString.length()), revision);
                } else {
                    throw new MalformedURLException(NbBundle.getMessage(RepositoryPaths.class, "MSG_RepositoryPath_WrongStart", path, repositoryUrlString)); // NOI18N
                }
            } else {
                ret[i] = new RepositoryFile(repositoryUrl, path, revision);
            }
        }
        return ret;
    }
    
    public SVNRevision getRevision(String rev) {
        if (rev == null || ".".equals(rev)) {
            return SVNRevision.HEAD;
        }
        return SvnUtils.getSVNRevision(rev);
    }
}
