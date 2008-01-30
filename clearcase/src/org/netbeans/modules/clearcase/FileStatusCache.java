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

import org.netbeans.modules.clearcase.client.ClearcaseClient;
import org.netbeans.modules.clearcase.client.status.FileStatus;
import org.netbeans.modules.clearcase.client.status.ListCheckouts.LSCOOutput;
import org.openide.filesystems.FileUtil;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level; 
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.modules.clearcase.client.status.ListCheckouts;
import org.netbeans.modules.clearcase.client.status.ListFiles;
import org.netbeans.modules.clearcase.util.Utils;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.spi.VersioningSupport;
import org.netbeans.modules.versioning.util.ListenersSupport;
import org.netbeans.modules.versioning.util.VersioningListener;
import org.openide.util.RequestProcessor;

/**
 * Central part of status management, deduces and caches statuses of files under version control.
 *
 * @author Maros Sandor
 */
public class FileStatusCache {
    
    /**
     * Indicates that status of a file changed and listeners SHOULD check new status
     * values if they are interested in this file.
     * The New value is a ChangedEvent object (old FileInformation object may be null)
     */
    public static final String EVENT_FILE_STATUS_CHANGED = "status.changed";        
    
    // Constant FileInformation objects that can be safely reused
    // Files that have a revision number cannot share FileInformation objects
    private static final FileInformation FILE_INFORMATION_IGNORED = new FileInformation(FileInformation.STATUS_NOTVERSIONED_IGNORED, false);
    private static final FileInformation FILE_INFORMATION_EXCLUDED_DIRECTORY = new FileInformation(FileInformation.STATUS_NOTVERSIONED_IGNORED, true);    
    private static final FileInformation FILE_INFORMATION_NOTMANAGED = new FileInformation(FileInformation.STATUS_NOTVERSIONED_NOTMANAGED, false);
    private static final FileInformation FILE_INFORMATION_NOTMANAGED_DIRECTORY = new FileInformation(FileInformation.STATUS_NOTVERSIONED_NOTMANAGED, true);
    private static final FileInformation FILE_INFORMATION_UNKNOWN = new FileInformation(FileInformation.STATUS_UNKNOWN, false);
    
    private ListenersSupport listenerSupport = new ListenersSupport(this);

    private Map<File, Map<File, FileInformation>> statusMap = new HashMap<File, Map<File, FileInformation>>();
    
    private Clearcase clearcase;
    
    private RequestProcessor rp; 
    
    private Set<File> filesToRefresh = new HashSet<File>();
    private RequestProcessor.Task filesToRefreshTask;
    
    FileStatusCache() {
        this.clearcase = Clearcase.getInstance();        
    }
    
    // --- Public interface -------------------------------------------------

    public void addVersioningListener(VersioningListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeVersioningListener(VersioningListener listener) {
        listenerSupport.removeListener(listener);
    }
    
    /**
     * Lists <b>interesting files</b> that are known to be inside given folders.     
     * <p>This method returns both folders and files.
     *
     * @param context context to examine
     * @param includeStatus limit returned files to those having one of supplied statuses
     * @return File [] array of interesting files
     */
    public File [] listFiles(VCSContext context, int includeStatus) {
        Set<File> set = new HashSet<File>();        
        
        // XXX this is crap. check for files from context
        for(Entry<File, Map<File, FileInformation>> entry : statusMap.entrySet()) {
            
            Map<File, FileInformation> map = entry.getValue();    
            for (Iterator i = map.keySet().iterator(); i.hasNext();) {                
                File file = (File) i.next();                                   
                FileInformation info = (FileInformation) map.get(file);
                if ((info.getStatus() & includeStatus) == 0) {                    
                    continue;
                }
                                                    
                for (File root : context.getRootFiles()) {
                    if (VersioningSupport.isFlat(root)) {
                        if (file.equals(root) || file.getParentFile().equals(root)) {
                            set.add(file);
                            break;
                        }
                    } else {
                        if (Utils.isParentOrEqual(root, file)) {
                            set.add(file);
                            break;
                        }   
                    }
                }
            }
        }
        if (context.getExclusions().size() > 0) {
            for (Iterator i = context.getExclusions().iterator(); i.hasNext();) {
                File excluded = (File) i.next();
                for (Iterator j = set.iterator(); j.hasNext();) {
                    File file = (File) j.next();
                    if (Utils.isParentOrEqual(excluded, file)) {
                        j.remove();
                    }
                }
            }
        }
        return set.toArray(new File[set.size()]);
    }
    
    /**
     * Returns the versionig status for a file as long it is already stored in the cache or {@link #FILE_INFORMATION_UNKNOWN}. 
     * If refreshUnknown true and no status value is available at the moment a status refresh for the given file is triggered 
     * asynchronously and subsequently status change events will be fired to notify all registered listeners.
     * 
     * @param file file to get status for
     * @param refreshUnknown if true and no status value is stored in the cache the {@link #refresh} is called asynchronouslly
     * @return FileInformation structure containing the file status or {@link #FILE_INFORMATION_UNKNOWN} if there is no staus known yet
     * @see FileInformation 
     * @see #refreshAsync
     */
    public FileInformation getCachedInfo(final File file, boolean refreshUnknown) {
        File dir = file.getParentFile();
        
        if (dir == null) {
            return FILE_INFORMATION_NOTMANAGED; // default for filesystem roots
        }                 
        
        Map<File, FileInformation> dirMap = statusMap.get(dir); // XXX synchronize this
        FileInformation info = dirMap != null ? dirMap.get(file) : null;
        if(info == null) {
            if(refreshUnknown) {
                // nothing known about the file yet, 
                // return unknown info for now and 
                // refresh with forced event firing
                refreshAsync(file); // XXX do we need a deep refresh?               
            }    
            return FILE_INFORMATION_UNKNOWN;
        } else {
            return info;
        }        
    }
    
    /**
     * Determines the versioning status information for a file. 
     * This method synchronously accesses disk and may block for a long period of time.
     *
     * @param file file to get the {@link FileInformation} for
     * @return FileInformation structure containing the file status
     * @see FileInformation
     */
    public FileInformation getInfo(File file) {      
        FileInformation fi = getCachedInfo(file, false);
        if(fi == null) {
            fi = refresh(file, false); 
        }
        if (fi == null) return FILE_INFORMATION_UNKNOWN;    // TODO: HOTFIX
        return fi;               
    }
    
    /**
     * Refreshes recursively all files in the given context.
     * This method synchronously accesses disk and may block for a long period of time.
     * Status change events will be fired to notify all registered listeners.
     * 
     * @param ctx the context to be refreshed
     */
    public void refreshRecursively(VCSContext ctx) {
        Set<File> files = ctx.getRootFiles();
        refresh(true, files.toArray(new File[files.size()]));
    }            
    
    /**
     * Asynchronously refreshes the status for the given files.
     * Status change events will be fired to notify all registered listeners.
     * 
     * @param files
     */
    public void refreshAsync(File ...files) {
        synchronized(filesToRefresh) {
            for (File file : files) {
                if(file == null) continue;
                filesToRefresh.add(file);                
            }
        }
        getFilesToRefreshTask().schedule(1000); 
    }    
    
    // --- Package private contract ------------------------------------------
    
    
    /**
     * XXX now whats the difference to listFiles ?
     * @param root
     * @return
     */
    Map<File, FileInformation> getAllModifiedValues(File root) {  // XXX add recursive flag
        Map<File, FileInformation> ret = new HashMap<File, FileInformation>();
        
        for(File modifiedDir : statusMap.keySet()) {
            if(Utils.isParentOrEqual(root, modifiedDir)) {                
                Map<File, FileInformation> map = statusMap.get(modifiedDir);
                for(File file : map.keySet()) {
                    FileInformation info = map.get(file);

                    if( (info.getStatus() & FileInformation.STATUS_LOCAL_CHANGE) != 0 ) { // XXX anything else
                        ret.put(file, info);
                    }                
                }                
            }            
        }        
        return ret;
    }
    
    /**
     * This method avoids the cached status storage and returns the {@link FileStatus.ClearcaseStatus} 
     * retrieved from a synchronous clearcase command call. 
     * 
     * This method synchronously accesses disk and may block for a longer period of time.
     * 
     * @param file the file
     * @return {@link FileStatus.ClearcaseStatus} describing the files actuall status
     */
    FileStatus.ClearcaseStatus getClearcaseStatus(File file) {
        List<FileStatus> status = getFileStatus(file, false);
        if(status == null && status.size() == 0) {
            return FileStatus.ClearcaseStatus.REPOSITORY_STATUS_UNKNOWN;
        }
        return status.get(0).getStatus();
    }

    private void refresh(boolean recursivelly, File ...files) {        
        Set<File> parents = new HashSet<File>();        
        for (File file : files) {
            File parent = file.getParentFile();         
            if(recursivelly && file.isDirectory()) {
                refreshRecursively(file);
            } else {
                if(!parents.contains(parent)) {
                    // refresh the file, all its siblings and the parent (dir)
                    refresh(file, true); 
                    // all following kids from this files parent should be be skipped
                    // as they were already refreshed
                    parents.add(parent);                
                }
            }                                        
        }                       
    }
    
    /**
     * Refreshes recursively all files in the given directory.
     * @param dir
     */
    private void refreshRecursively(File dir) {        
        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            return;
        }
        boolean kidsRefreshed = false; 
        for(File file : files) {
            if(!kidsRefreshed) {
                // refresh the file, all its siblings and the parent (dir)
                refresh(file, true); 
                // files parent directory (dir) and all it's children are refreshed
                // so skip for the next child
                kidsRefreshed = true; 
               
            } 
            if (file.isDirectory()) {
                refreshRecursively(file);                
            }            
        }
    }        
    
    /**
     * Refreshes the status value for the given file, all its siblings and its parent folder. 
     * Status change events will be eventually thrown for the file, all its siblings and its parent folder. 
     * 
     * @param file the file to be refreshed
     * @param forceChangeEvent if true status change event will fired even if 
     *                         the newly retrieved status value for a file is the same as the already cached one
     * @return FileInformation
     * @see #ChangedEvent
     */
    private FileInformation refresh(File file, boolean forceChangeEvent) { 
        
        // check if it is a managed directory structure
        File dir = file.getParentFile();
        if (dir == null) {
            return FileStatusCache.FILE_INFORMATION_NOTMANAGED; // default for filesystem roots
        }
        
        if(!Clearcase.getInstance().isManaged(file)) {
            // TODO what about children if dir?
            return file.isDirectory() ? FILE_INFORMATION_NOTMANAGED_DIRECTORY : FILE_INFORMATION_NOTMANAGED;
        }     

        boolean isRoot;
        List<FileStatus> statusValues;
        
        if(!Clearcase.getInstance().isManaged(dir)) {                        
            isRoot = true;
            // file seems to be the vob root
            statusValues = getFileStatus(file, false);
        } else {
            isRoot = false;
            statusValues = getFileStatus(dir, true);
        }              
                        
        if(statusValues == null || statusValues.size() == 0) {
            return FILE_INFORMATION_UNKNOWN;
        }
                
        Map<File, FileInformation> oldDirMap = statusMap.get(dir); // XXX synchronize this!
        Map<File, FileInformation> newDirMap;        
        if(!isRoot || oldDirMap == null) {
            newDirMap = new HashMap<File, FileInformation>();            
        } else {
            // XXX what if vob gets deleted?
            newDirMap = oldDirMap;
        }
                 
        for(FileStatus fs : statusValues) {            
            FileInformation fiNew = createFileInformation(fs, null); // XXX null for repository status!                            
            try {
                newDirMap.put(fs.getFile().getCanonicalFile(), fiNew);            
            } catch (IOException ioe) {
                Clearcase.LOG.log(Level.SEVERE, null, ioe);
            }
        }       
        
        statusMap.put(dir, newDirMap);
        FileInformation fi = null;        
        try {        
            fi = newDirMap.get(file.getCanonicalFile());            
        } catch (IOException ioe) {
            Clearcase.LOG.log(Level.SEVERE, null, ioe);
        }

        if(fi == null) {                        
            // e.g. when triggered by interceptor.delete()
            fi = FILE_INFORMATION_UNKNOWN;            
        }         
            
        fireStatusEvents(newDirMap, oldDirMap, forceChangeEvent);        
        
        return fi;
        
        
        //if (files == FileStatusCache.NOT_MANAGED_MAP && repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_UNKNOWN) return FileStatusCache.FILE_INFORMATION_NOTMANAGED;
        
        // current cached file status info
        
//        // repository file status
//        FileInformation fi = null;
//        if(repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_ADDED){
//            // update file status info as up to date
//            fi = new FileInformation(FileInformation.STATUS_VERSIONED_UPTODATE, file.isDirectory());
//        } else if (repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_NEW){
//            fi = new FileInformation(FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY, file.isDirectory());
//        } else if (repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_MODIFIED){
//            fi = new FileInformation(FileInformation.STATUS_VERSIONED_UPTODATE, file.isDirectory());
//        } else if (repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_LOCAL_MODIFIED) {
//            // since local modified event could be fired by new file creation by interceptor
//            // interceptor does not know if it is from new file or real file modified event
//            // so we test here if it is from new file, do not set to local modified state
//            fi = createFileInformation(file);
//            // if there is not in cache, it is previously up to date file
//            // if it is new file, the change is not the real file modification
//            if(fi == null || fi.getStatus() != FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY)
//                fi = new FileInformation(FileInformation.STATUS_VERSIONED_MODIFIEDLOCALLY, file.isDirectory());
//        } else if(repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_REMOVED){
//            fi = new FileInformation(FileInformation.STATUS_VERSIONED_REMOVEDINREPOSITORY, file.isDirectory());
//        } else if(repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_REMOVED_LOCAL){
//            fi = new FileInformation(FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY, file.isDirectory());
//        } else if(repositoryStatus == FileStatus.RepositoryStatus.REPOSITORY_STATUS_FILE_OPEN){
//            fi = new FileInformation(FileInformation.STATUS_VERSIONED_UPTODATE, file.isDirectory());
//        } else {
//            fi = createFileInformation(file);
//        }
//        if (FileStatusCache.equivalent(fi, current)) {
//            if (forceChangeEvent) fireFileStatusChanged(file, current, fi);
//            return fi;
//        }
        
        // do not include uptodate files into cache, missing directories must be included
//        if (current == null && fi != null && !fi.isDirectory() && fi.getStatus() == FileInformation.STATUS_VERSIONED_UPTODATE) {
//            if (forceChangeEvent) fireFileStatusChanged(file, current, fi);
//            return fi;
//        }
        
//        file = FileUtil.normalizeFile(file);
//        dir = FileUtil.normalizeFile(dir);
//        Map<File, FileInformation> newFiles = new HashMap<File, FileInformation>(files);
//        if (fi != null && fi.getStatus() == FileInformation.STATUS_UNKNOWN) {
//            newFiles.remove(file);
//            turbo.writeEntry(file, FILE_STATUS_MAP, null);  // remove mapping in case of directories
//        } else if (fi != null && fi.getStatus() == FileInformation.STATUS_VERSIONED_UPTODATE && file.isFile()) {
//            newFiles.remove(file);
//        } else {
//            newFiles.put(file, fi);
//        }
//        assert newFiles.containsKey(dir) == false;
//        turbo.writeEntry(dir, FILE_STATUS_MAP, newFiles.size() == 0 ? null : newFiles);
//        
//        if (file.isDirectory() && needRecursiveRefresh(fi, current)) {
//            File [] content = listFiles(file);
//            for (int i = 0; i < content.length; i++) {
//                refresh(content[i], FileStatus.RepositoryStatus.REPOSITORY_STATUS_UNKNOWN);
//            }
//        }
//        fireFileStatusChanged(file, current, fi);
//        return fi;
    }
    
    private List<FileStatus> getFileStatus(File file, boolean handleChildren) {
        
        final ClearcaseClient client = Clearcase.getInstance().getClient();
        List<FileStatus> statusValues = new ArrayList<FileStatus>();
        try {
            // 1. list files ...
            ListFiles listedStatusUnit = new ListFiles(file, handleChildren);    
            client.exec(listedStatusUnit);
        
            // 2. ... go throught the ct ls output ...
            List<ListFiles.ListOutput> listOutput = listedStatusUnit.getOutputList();            
            Map<File, ListFiles.ListOutput> checkedout = new HashMap<File, ListFiles.ListOutput>();
            for(ListFiles.ListOutput o : listOutput) {        
                if(o.getVersion() != null && o.getVersion().isCheckedout()) {
                    checkedout.put(o.getFile(), o);
                } else {
                    statusValues.add(new FileStatus(o.getType(), o.getFile(), o.getOriginVersion(), o.getVersion(), o.getAnnotation(), false));   
                }
            }        

            // 3. ... list checkouts if there were checkouted files in ct ls ...
            if(checkedout.size() > 0) {            
                ListCheckouts lsco = new ListCheckouts(file, handleChildren);   // TODO !!!            

                client.exec(lsco);    
                
                List<LSCOOutput> checkouts = lsco.getOutputList();
                for(LSCOOutput c : checkouts) {        
                    ListFiles.ListOutput o = checkedout.get(c.getFile());
                    // if(o != null) { XXX
                        statusValues.add(new FileStatus(o.getType(), o.getFile(), o.getOriginVersion(), o.getVersion(), o.getAnnotation(), c.isReserved()));               
                    //}
                }                
            }      
                        
        } catch (ClearcaseException ex) {
            Clearcase.LOG.log(Level.SEVERE, "Exception in status command ", ex);            
        }

        return statusValues;
    }
    
    /**
     * Examines a file or folder and computes its status. 
     * 
     * @param status entry for this file or null if the file is unknown to subversion
     * @return FileInformation file/folder status bean
     */ 
    private FileInformation createFileInformation(FileStatus status, FileStatus repositoryStatus) { // XXX get and handle repository status
        FileInformation info;
        
        // XXX why is this based on the repository status? WTH is the repository for? do we realy need it?
        if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_VIEW_PRIVATE) {
            if(isIgnored(status.getFile())) {
                info = FILE_INFORMATION_IGNORED; // XXX what if file does not exists -> isDir = false;   
            } else {
                info = new FileInformation(FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY, status, status.getFile().isDirectory()); 
            }            
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_RESERVED) {
            info = new FileInformation(FileInformation.STATUS_VERSIONED_CHECKEDOUT_RESERVED, status, status.getFile().isDirectory());
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_UNRESERVED) {
            info = new FileInformation(FileInformation.STATUS_VERSIONED_CHECKEDOUT_UNRESERVED, status, status.getFile().isDirectory());            
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_BUT_REMOVED) {
            // XXX we don't know if directory but could be retrieved from ct ls -long or ct describe 
            info = new FileInformation(FileInformation.STATUS_VERSIONED_CHECKEDOUT_BUT_REMOVED, status, status.getFile().isDirectory());  
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_LOADED_BUT_MISSING) {
            // XXX we don't know if directory but could be retrieved from ct ls -long or ct describe 
            info = new FileInformation(FileInformation.STATUS_VERSIONED_LOADED_BUT_MISSING, status, status.getFile().isDirectory());              
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_HIJACKED) {
            info = new FileInformation(FileInformation.STATUS_VERSIONED_HIJACKED, status, status.getFile().isDirectory());        
        } else if(status.getStatus() == FileStatus.ClearcaseStatus.REPOSITORY_STATUS_FILE_ECLIPSED) {
            info = new FileInformation(FileInformation.STATUS_NOTVERSIONED_ECLIPSED, status, status.getFile().isDirectory());
        } else if(status.getVersion() != null) {
            // has predecesor (is versioned) and no other status value known ...
            info = new FileInformation(FileInformation.STATUS_VERSIONED_UPTODATE, status, status.getFile().isDirectory());
        } else {
            info = new FileInformation(FileInformation.STATUS_UNKNOWN, status, status.getFile().isDirectory());   
        }        
        
        Clearcase.LOG.finer("createFileInformation " + status + " : " + info);
        return info;
        
    }
    
    /**
     * Non-recursive ignore check.
     *
     * <p>Side effect: if versioned by CC and ignored then also stores the is ignored information
     *
     * @return true if file is listed in parent's ignore list
     * or IDE thinks it should be.
     */
    private boolean isIgnored(final File file) {
        String name = file.getName();

        if(ClearcaseModuleConfig.isIgnored(file)) {
            return true;
        }

        if (SharabilityQuery.getSharability(file) == SharabilityQuery.NOT_SHARABLE) {
            // BEWARE: In NetBeans VISIBILTY == SHARABILITY                                 
            ClearcaseModuleConfig.setIgnored(file);
            return true;
        } else {
            // XXX do we still need this hack?!
            // backward compatability #68124
            if (".nbintdb".equals(name)) {  // NOI18N
                return true;
            }

            return false;
        }
    }            
            
    private RequestProcessor.Task getFilesToRefreshTask() {
        if(filesToRefreshTask == null) {
            filesToRefreshTask = getRequestProcessor().create(new Runnable() {
                public void run() {
                    File[] files;
                    synchronized(filesToRefresh) {
                        files = filesToRefresh.toArray(new File[filesToRefresh.size()]);
                        filesToRefresh.clear();
                    }                        
                    refresh(false, files);
                }
            });
        }
        return filesToRefreshTask;
    }   
           
//    /**
//     * Two FileInformation objects are equivalent if their status contants are equal AND they both reperesent a file (or
//     * both represent a directory) AND Entries they cache, if they can be compared, are equal.
//     *
//     * @param other object to compare to
//     * @return true if status constants of both object are equal, false otherwise
//     */
//    private static boolean equivalent(FileInformation main, FileInformation other) {
//        if (other == null || main.getStatus() != other.getStatus() || main.isDirectory() != other.isDirectory()) return false;
//        
//        FileStatus e1 = main.getStatus(null);
//        FileStatus e2 = other.getStatus(null);
//        return e1 == e2 || e1 == null || e2 == null || e1.equals(e2);
//    }
    
//    private boolean needRecursiveRefresh(FileInformation fi, FileInformation current) {
//        if (fi != null && fi.getStatus() == FileInformation.STATUS_NOTVERSIONED_EXCLUDED ||
//                current != null && current.getStatus() == FileInformation.STATUS_NOTVERSIONED_EXCLUDED) return true;
//        if (fi != null && fi.getStatus() == FileInformation.STATUS_NOTVERSIONED_NOTMANAGED ||
//                current != null && current.getStatus() == FileInformation.STATUS_NOTVERSIONED_NOTMANAGED) return true;
//        return false;
//    }
              
    private boolean exists(File file) {
        if (!file.exists()) return false;
        return file.getAbsolutePath().equals(FileUtil.normalizeFile(file).getAbsolutePath());
    }
    
    public static class ChangedEvent {
        
        private File file;
        private FileInformation oldInfo;
        private FileInformation newInfo;
        
        public ChangedEvent(File file, FileInformation oldInfo, FileInformation newInfo) {
            this.file = file;
            this.oldInfo = oldInfo;
            this.newInfo = newInfo;
        }
        
        public File getFile() {
            return file;
        }
        
        public FileInformation getOldInfo() {
            return oldInfo;
        }
        
        public FileInformation getNewInfo() {
            return newInfo;
        }
    }    

    private void fireStatusEvents(Map<File, FileInformation> newDirMap, Map<File, FileInformation> oldDirMap, boolean force) {
        for (File file : newDirMap.keySet()) { 
            FileInformation newInfo;
            FileInformation oldInfo;
            try {
                newInfo = newDirMap.get(file.getCanonicalFile());
                oldInfo = oldDirMap != null ? oldDirMap.get(file.getCanonicalFile()) : null;                
                fireFileStatusChanged(file, oldInfo, newInfo, force);
            } catch (IOException ex) {
                Clearcase.LOG.log(Level.SEVERE, null, ex);
            }            
        }
        if(oldDirMap == null) {
            return;
        }
        for (File file : oldDirMap.keySet()) { 
            FileInformation newInfo = newDirMap.get(file);
            if(newInfo == null) {
                FileInformation oldInfo;
                try {
                    oldInfo = oldDirMap.get(file.getCanonicalFile());
                    fireFileStatusChanged(file, oldInfo, newInfo, force);    
                } catch (IOException ex) {
                    Clearcase.LOG.log(Level.SEVERE, null, ex);
                }
                                        
            }
        }                                        
    }
    
    private void fireFileStatusChanged(File file, FileInformation oldInfo, FileInformation newInfo, boolean force) {        
        if(!force) {            
           if (oldInfo == null && newInfo == null) {
               return;
           }
           if(oldInfo != null && newInfo != null && oldInfo.getStatus() == newInfo.getStatus()) {
                return;
           } 
        }                              
        listenerSupport.fireVersioningEvent(EVENT_FILE_STATUS_CHANGED, new Object [] { file, oldInfo, newInfo });                
    }    
    
    private RequestProcessor getRequestProcessor() {        
        if(rp == null) {
           rp = new RequestProcessor("ClearCase - status cache");    
        }        
        return rp;
    }
    
}
