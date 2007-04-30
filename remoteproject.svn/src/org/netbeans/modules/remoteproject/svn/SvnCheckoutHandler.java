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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
 */
/*
 * SvnCheckoutHandler.java
 *
 * Created on March 1, 2007, 9:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.remoteproject.svn;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.SvnModuleConfig;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.client.SvnProgressSupport;
import org.netbeans.modules.subversion.ui.browser.RepositoryPaths;
import org.netbeans.modules.subversion.ui.checkout.CheckoutAction;
import org.netbeans.modules.subversion.ui.checkout.CheckoutCompleted;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 *
 * @author Tim
 */
public class SvnCheckoutHandler implements CheckoutHandler {
    //XXX should be in separate module or svn module
    public boolean canCheckout(FileObject template) {
        return "svn".equals (template.getAttribute("system"));
    }

    public static final String ATTR_URL = "svnurl"; //NOI18N
    public static final String ATTR_REVISION = "revision"; //NOI18N
    public static final String ATTR_PATHS = "paths"; //NOI18N
    public static final String ATTR_USER = "username"; //NOI18N
    
    public String checkout(final FileObject template, final FileObject dest,
                           final ProgressHandle progress, final String un) {
        String url = (String) template.getAttribute (ATTR_URL);
        if (url == null) {
            throw new NullPointerException ("Attribute 'url' not " + //NOI18N
                    "specified on " + template.getPath()); //NOI18N
        }
        try {
            
        final SVNUrl repository = new SVNUrl (url);
        final File file = FileUtil.toFile (dest);
        
        String rev = (String) template.getAttribute(ATTR_REVISION);
        String paths = (String) template.getAttribute (ATTR_PATHS);
        
        String explicitUsername = (String) template.getAttribute (ATTR_USER);
        final String username = un == null ? explicitUsername : un;
        
        final RepositoryFile[] repositoryFiles = getRepositoryFiles (repository, 
                rev, paths);
        
        final ProgressHandle progress2 = ProgressHandleFactory.createHandle("SVN Checkout");        
        
        SvnProgressSupport support = new SvnProgressSupport() {
            protected ProgressHandle getProgressHandle() {
                return progress2;
            }
            
            public void perform() {
                final SvnClient client;
                try {
                    if (username == null) {
                        client = Subversion.getInstance().getClient(repository);
                    } else {
                        client = Subversion.getInstance().getClient(repository, username, "");
                    }
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
    
    public RepositoryFile[] getRepositoryFiles(SVNUrl repositoryUrl, String rev, String defaultPath) throws MalformedURLException, NumberFormatException {
        SVNRevision revision = getRevision(rev);
//        if(defaultPath == null) {
//            RepositoryFile rf = new RepositoryFile(repositoryUrl, repositoryFile.getFileUrl(), revision);
//            return new RepositoryFile[] {rf};
//        }
     
        if(defaultPath == null || !defaultPath.trim().equals("") ) {                
            return new RepositoryFile[] { new RepositoryFile(repositoryUrl, "", revision) } ;
        }
        String[] paths = defaultPath.split(","); // NOI18N
        RepositoryFile[] ret = new RepositoryFile[paths.length];
       
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i].trim();
            String repositoryUrlString = repositoryUrl.toString();
            if(path.startsWith("file://")  ||   // NOI18N
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

    public String getUserName(FileObject template) {
        return (String) template.getAttribute (ATTR_USER);
    }

    public File[] getCreatedDirs(FileObject template, File destFolder) {
       String modules = (String) template.getAttribute (ATTR_PATHS);
       File[] result;
       if (modules != null) {
           String[] s = modules.split(" ");
           List <File> l = new ArrayList<File>(s.length);
           for (int i = 0; i < s.length; i++) {
               File f = new File (destFolder, s[i]);
               if (f.exists() && f.isDirectory()) {
                   l.add (f);
               }
           }
           result = l.toArray(new File[l.size()]);           
       } else {
           result = new File[0];           
       }
       return result;
    }
}
