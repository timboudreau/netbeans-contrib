/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.util.*;

import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.cache.CacheReference;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.virtuals.VirtualsDataLoader;

/**
 * Implementation of file attributes for version control systems. All attributes
 * read/write operations are delegated to the DefaultAttributes, whith the exception
 * of VCS-related attributes. These special attributes are not propagated to
 * the DefaultAttributes, but are interpreted as VCS commands.
 * @author  Martin Entlicher
 */
public class VcsAttributes extends DefaultAttributes {
    
    /**
     * The name of attribute, that contains the java.io.File object for
     * the given FileObject
     */
    public static final String FILE_ATTRIBUTE = "java.io.File"; //NOI18N

    /**
     * Name of the attribute that contains the instance of the vcs filesystem that the
     * fileobject belongs to.
     */
    public static final String VCS_NATIVE_FS = "VcsFileSystemAttributeIdentifier";

    /**
     * Name of the attribute that contains path of the fileobject within the versioning filesystem.
     * That can differ when the filesystem is placed within a multifilesystem.
     * In cooperation with the VCS_NATIVE_FS one can retrieve the original fileobject from
     * the vcs filesystem.
     */
    public static final String VCS_NATIVE_PACKAGE_NAME_EXT = "VcsFileSystemNativeFOPath";
    
    /**
     * Attribute name for a VCS action.
     */
    public static final String VCS_ACTION = "VCS_ACTION"; //NOI18N
    /**
     * Attribute name for the refresh action on a VCS filesystem.
     */
    public static final String VCS_REFRESH = "VCS_REFRESH"; //NOI18N
    /**
     * Attribute name for a VCS action, that schedules the VCS operation for later processing.
     * This action should be performed on important secondary file objects.
     */
    public static final String VCS_SCHEDULING_SECONDARY_FO_ACTION = "VCS_SCHEDULING_SECONDARY_FO_ACTION"; //NOI18N
    /**
     * The scheduling VCS Add action name. The file will be added to the VCS repository
     * as soon as the primary file will be committed.
     */
    public static final String VCS_SCHEDULING_ADD = "ADD"; //NOI18N
    /**
     * The scheduling VCS Remove action name. The file will be removed from the VCS repository
     * as soon as the primary file will be committed.
     */
    public static final String VCS_SCHEDULING_REMOVE = "REMOVE"; //NOI18N
    /**
     * The attribute name where scheduled files are stored.
     */
    public static final String VCS_SCHEDULED_FILES_ATTR = "VCS_SCHEDULED_FILES"; //NOI18N
    /**
     * The attribute name marking the file as scheduled for later processing.
     * The value should be {@link VCS_SCHEDULING_ADD} or {@link VCS_SCHEDULING_REMOVE}.
     */
    public static final String VCS_SCHEDULED_FILE_ATTR = "VCS_SCHEDULED_FILE"; //NOI18N
    /**
     * The attribute name containing the java.io.File name of the primary file,
     * that contains scheduled files. This will prevent the copy of scheduled
     * attributes to other files, because when the value of this attribue will
     * differ from the actual file path, all scheduled attribues will be deleted.
     */
    public static final String VCS_SCHEDULING_MASTER_FILE_NAME_ATTR = "VCS_SCHEDULING_MASTER_FILE_NAME"; //NOI18N
    /**
     * The VCS Add command name. This command adds the file to the VCS repository.
     */
    public static final String VCS_ACTION_ADD = "VCS_ADD"; //NOI18N
    /**
     * The VCS Remove command name. This command removes the file from the VCS repository.
     */
    public static final String VCS_ACTION_REMOVE = "VCS_REMOVE"; //NOI18N
    /**
     * This attribute is set when the action is done. The value is Boolean.TRUE
     * or Boolean.FALSE depending on the command exit status.
     */
    public static final String VCS_ACTION_DONE = "VCS_ACTION_DONE"; //NOI18N
    /**
     * The description to the action. Usually it is a message, that is given to
     * the VCS command (such as a change log for check in)
     */
    public static final String VCS_ACTION_DESCRIPTION = "VCS_ACTION_DESCRIPTION"; //NOI18N
    
    /**
     * Read the attribute of this name to obtain the VCS file status.
     */
    public static String VCS_STATUS = "VCS_STATUS"; //NOI18N
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are not version controlled.
     */
    public static String VCS_STATUS_LOCAL = "VCS_STATUS_LOCAL"; //NOI18N
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are present in VCS repository, but do not exist locally.
     */
    public static String VCS_STATUS_MISSING = "VCS_STATUS_MISSING"; //NOI18N
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are version controlled and are present locally.
     */
    public static String VCS_STATUS_UP_TO_DATE = "VCS_STATUS_UP_TO_DATE"; //NOI18N
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are not recognized. We can not say whether they are version controlled
     * or not.
     */
    public static String VCS_STATUS_UNKNOWN = "VCS_STATUS_UNKNOWN"; //NOI18N
    
    private VcsActionSupporter supporter;
        
    private VcsFileSystem fileSystem;
    
    private static RequestProcessor vcsActionRequestProcessor;

    static final long serialVersionUID = 8084585278800267078L;
    
    /** Creates new VcsAttributes */
    public VcsAttributes(AbstractFileSystem.Info info, AbstractFileSystem.Change change,
                         AbstractFileSystem.List list, VcsFileSystem fileSystem, VcsActionSupporter supp) {
        super(info, change, list);
        this.fileSystem = fileSystem;
        supporter = supp;
    }
    
    public VcsActionSupporter getCurrentSupporter() {
        return supporter;
    }
    
    public void setCurrentSupporter(VcsActionSupporter supporter) {
        this.supporter = supporter;
    }
    
    /**
     * Get the file attribute with the specified name.
     * @param name the file name
     * @param attrName name of the attribute
     * @return appropriate (serializable) value or null if the attribute is unset
     *         (or could not be properly restored for some reason).
     *         If the attribute name is the {@link VCS_STATUS}, then the VCS status
     *         of the file is returned.
     */
    public Object readAttribute(String name, String attrName) {
        if (FILE_ATTRIBUTE.equals(attrName)) {
            return fileSystem.getFile(name);
        }
        if (VCS_STATUS.equals(attrName)) {
            if (!fileSystem.getFile(name).exists()) return VCS_STATUS_MISSING;
            FileStatusProvider statusProvider = fileSystem.getStatusProvider();
            if (statusProvider != null) {
                String status = statusProvider.getFileStatus(name);
                //CacheFile file = cacheProvider.getFile(name);
                if (statusProvider.getLocalFileStatus().equals(status)) {
                    return VCS_STATUS_LOCAL;
                }
                return VCS_STATUS_UP_TO_DATE;
            }
            return VCS_STATUS_UNKNOWN;
        } else if (GeneralCommandAction.VCS_ACTION_ATTRIBUTE.equals(attrName)) {
            return supporter;            
        } else if (VCS_NATIVE_FS.equals(attrName)) {
            return fileSystem;
        } else if (VCS_NATIVE_PACKAGE_NAME_EXT.equals(attrName)) {
            return name;
        }  else {
            if ("NetBeansAttrAssignedLoader".equals(attrName)) { /* DataObject.EA_ASSIGNED_LOADER */  //NOI18N
                CacheReference ref = fileSystem.getCacheReference(name);
                if (ref != null) {
                    if (ref.isVirtual()) {
                        //                        System.out.println("is virtual.." + name);
                        return VirtualsDataLoader.class.getName();
                    }
                }
            } else if ("NetBeansAttrAssignedLoaderModule".equals(attrName)) { /* DataObject.EA_ASSIGNED_LOADER_MODULE */  //NOI18N
                CacheReference ref = fileSystem.getCacheReference(name);
                if (ref != null) {
                    if (ref.isVirtual()) {
                        //                        System.out.println("is vitrual module..");
                        return "org.netbeans.modules.vcscore"; //NOI18N
                    }
                }
            }
            return super.readAttribute(name, attrName);
        }
    }

    /**
     * Set the file attribute with the specified name. If the name is {@link VCS_ACTION},
     * and the value is an instance of FeatureDescriptor, then it's not set as file attribute,
     * but is interpreted as a VCS command. The name of the command is taken from
     * value.getName() and commands options from attributes of that feature descriptor.
     * @param name the file name
     * @param attrName name of the attribute
     * @param value new value or null to clear the attribute. Must be serializable,
     *        with the exception of VCS command attribute.
     * @throws IOException if the attribute cannot be set. If serialization is
     *                     used to store it, this may in fact be a subclass such
     *                     as NotSerializableException.
     * @throws UnknownServiceException if the requested VCS action is not provided.
     *                                 A subclass of IOException was chosen, since
     *                                 FileObject.setAttribute throws IOException.
     */
    public void writeAttribute(final String name, final String attrName, final Object value) throws IOException, java.net.UnknownServiceException {
        if (VCS_ACTION.equals(attrName) && value instanceof FeatureDescriptor) {
            performVcsAction(name, (FeatureDescriptor) value);
        } else if (VCS_REFRESH.equals(attrName)) {
            performRefresh(name, value);
        } else if (VCS_SCHEDULING_SECONDARY_FO_ACTION.equals(attrName) && value instanceof String) {
            // Set the scheduling action for a secondary file. value is the action name,
            // currently "ADD" and "REMOVE" are the only supported values
            final FileObject fo = fileSystem.findFileObject(name);
            //System.out.println("scheduleSecondaryFOVcsAction("+name+", "+actionName+") = "+fo);
            if (fo == null) return ;
            FileObject primary;
            try {
                org.openide.loaders.DataObject dobj = org.openide.loaders.DataObject.find(fo);
                primary = dobj.getPrimaryFile();
                //System.out.println("  primary("+primary+").equals("+fo+") = "+primary.equals(fo));
                if (primary.equals(fo)) return ;
            } catch (org.openide.loaders.DataObjectNotFoundException exc) {
                exc.printStackTrace();
                return ;
            }
            // The scheduling is started. When all scheduling actions are done, a refresh is introduced.
            startFileScheduling(name);
            final FileObject primaryFO = primary;
            RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    scheduleSecondaryFOVcsAction(name, (String) value, fo, primaryFO);
                }
            });
            super.writeAttribute(name, VCS_SCHEDULED_FILE_ATTR, value);
        } else {
            if ("NetBeansAttrAssignedLoader".equals(attrName)) { /* DataObject.EA_ASSIGNED_LOADER */  //NOI18N
                if (value != null && "org.netbeans.modules.vcscore.util.virtuals.VirtualsDataLoader".equals(value.toString())) {
                    //System.out.println("dont write loader attribute,.," + name);
                    //don't write to .nbattrs file..
                    CacheReference ref = fileSystem.getCacheReference(name);
                    if (ref != null) {
                        //System.out.println("writed is virtual");
                        ref.setVirtual(true);
                    }
                    return;
                }
                if (value == null) {
                    CacheReference ref = fileSystem.getCacheReference(name);
                    if (ref != null) {
                        ref.setVirtual(false);
                    }
                }
                //System.out.println("write assigned loader for=" + name);
            }    
            if ("NetBeansAttrAssignedLoaderModule".equals(attrName)) { /* DataObject.EA_ASSIGNED_LOADER_MODULE */  //NOI18N
                if (value != null && "org.netbeans.modules.vcscore".equals(value.toString())  //NOI18N
                    && fileSystem.checkVirtual(name)) {
                   //don't write to .nbattrs file..
                   return;  
                }
                //System.out.println("write assigned module=" + value);
            }                
            super.writeAttribute(name, attrName, value);
        }
    }
    
    private static synchronized RequestProcessor getVcsActionRequestProcessor() {
        if (vcsActionRequestProcessor == null) {
            vcsActionRequestProcessor = new RequestProcessor("Vcs Attribute Action Request Processor");
        }
        return vcsActionRequestProcessor;
    }
    
    /**
     * Perform a VCS command on a specific file.
     * @param name the file the command should run on
     * @param descriptor the descriptor of the command. descriptor.getName() should
     *        return the command name, attributes can contain variable values
     *        which are given to the command. VCS_ACTION_DONE attribute is set
     *        when the action is done with the value being Boolean.TRUE or Boolean.FALSE
     *        depending on the command exit status.
     */
    private void performVcsAction(final String name, final FeatureDescriptor descriptor) throws java.net.UnknownServiceException {
        //System.out.println("performVcsAction("+name+")");
        String cmdName = descriptor.getName();
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) throw new java.net.UnknownServiceException(cmdName);
        final Table files = new Table();
        files.put(name, fileSystem.findResource(name));
        final Hashtable additionalVars = new Hashtable();
        for (Enumeration varNames = descriptor.attributeNames(); varNames.hasMoreElements(); ) {
            String varName = (String) varNames.nextElement();
            additionalVars.put(varName, descriptor.getValue(varName));
        }
        getVcsActionRequestProcessor().postRequest(new Runnable() {
            public void run() {
                VcsCommandExecutor[] executors = VcsAction.doCommand(files, cmd, additionalVars, fileSystem);
                boolean status = true;
                for (int i = 0; i < executors.length; i++) {
                    try {
                        fileSystem.getCommandsPool().waitToFinish(executors[i]);
                    } catch (InterruptedException iexc) {
                        return ;
                    }
                    status &= executors[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED;
                }
                descriptor.setValue(VCS_ACTION_DONE, new Boolean(status));
            }
        });
    }
    
    /**
     * Do a refresh of the folder.
     * @param name the folder name
     * @param recursive if it's value is Boolean.TRUE, a recursive refresh
     * of this folder is performed.
     */
    private void performRefresh(final String name, final Object recursive) {
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
            boolean rec = Boolean.TRUE.equals(recursive);
            FileCacheProvider cache = fileSystem.getCacheProvider();
            if (cache != null) {
                if (rec) {
                    cache.refreshCacheDirRecursive(name);
                } else {
                    cache.refreshCacheDir(name);
                }
            }
            }
        });
    }
    
    /**
     * Schedule a secondary file for "ADD" or "REMOVE". Files scheduled for remove are filtered from children().
     * Files scheduled are added as a special file attribute to the primary file.
     * @param name the name of a secondary file to be scheduled.
     * @param actionName "ADD" or "REMOVE"
     * @param fo the file object associated to the file
     * @param primary the associated primary file
     */
    private boolean scheduleSecondaryFOVcsAction(final String name, final String actionName, FileObject fo, FileObject primary) {
        if (VCS_STATUS_LOCAL.equals(primary.getAttribute(VCS_STATUS))) {
            // do not schedule local files
            endFileScheduling(name);
            return false;
        }
        int id;
        // create the descriptor of a scheduling action
        FeatureDescriptor descriptor = new FeatureDescriptor() {
            public void setValue(String attrName, Object value) {
                if (VCS_ACTION_DONE.equals(attrName)) {
                    // the scheduling action is done, inform the file system
                    fileSystem.removeScheduledFileToBeProcessed(name);
                    endFileScheduling(name);
                }
                super.setValue(attrName, value);
            }
        };
        boolean endOfScheduling = false;
        if (VCS_SCHEDULING_ADD.equals(actionName)) {
            //fileSystem.addScheduledSecondaryFO(name, VcsFileSystem.SCHEDULING_ACTION_ADD_ID);
            //FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(VcsCommand.NAME_SCHEDULE_ADD);
            try {
                fileSystem.addScheduledFileToBeProcessed(name);
                performVcsAction(name, descriptor);
            } catch (java.net.UnknownServiceException unsExc) {
                fileSystem.removeScheduledFileToBeProcessed(name);
                endOfScheduling = true;
            }
            id = 1;
        } else if (VCS_SCHEDULING_REMOVE.equals(actionName)) {
            //fileSystem.addScheduledSecondaryFO(name, VcsFileSystem.SCHEDULING_ACTION_REMOVE_ID);
            //FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(VcsCommand.NAME_SCHEDULE_REMOVE);
            try {
                fileSystem.addScheduledFileToBeProcessed(name);
                performVcsAction(name, descriptor);
            } catch (java.net.UnknownServiceException unsExc) {
                fileSystem.removeScheduledFileToBeProcessed(name);
                endOfScheduling = true;
            }
            id = 0;
        } else {
            endFileScheduling(name);
            return false;
        }
        // the file is being scheduled, add it to the primary file attribute.
        Set[] scheduled = (Set[]) primary.getAttribute(VCS_SCHEDULED_FILES_ATTR);
        if (scheduled == null) scheduled = new HashSet[2];
        if (scheduled[id] == null) scheduled[id] = new HashSet();
        scheduled[id].add(name);
        try {
            primary.setAttribute(VCS_SCHEDULED_FILES_ATTR, scheduled);
            java.io.File file = org.openide.execution.NbClassPath.toFile(primary);
            if (file != null) {
                primary.setAttribute(VCS_SCHEDULING_MASTER_FILE_NAME_ATTR, file.getAbsolutePath());
            }
        } catch (IOException ioExc) {
            if (endOfScheduling) endFileScheduling(name);
            return false;
        }
        if (endOfScheduling) endFileScheduling(name);
        return true;
    }
    
    private transient Map schedulingFilesByFolders;
    
    /**
     * Remember the file as being scheduled for it's folder.
     */
    private void startFileScheduling(String name) {
        synchronized (this) {
            if (schedulingFilesByFolders == null) {
                schedulingFilesByFolders = new HashMap();
            }
            int index = name.lastIndexOf('/');
            String dir = (index < 0) ? "" : name.substring(0, index);
            String file = (index < 0) ? name : index < (name.length() - 1) ? name.substring(index + 1) : "";
            Set files = (Set) schedulingFilesByFolders.get(dir);
            if (files == null) {
                files = new HashSet();
            }
            files.add(file);
            schedulingFilesByFolders.put(dir, files);
        }
    }
    
    /**
     * The scheduling action for this file was done, if no more actions are being
     * processed in the file's folder, do a refresh of that folder.
     */
    private void endFileScheduling(String name) {
        Set files;
        String dir;
        synchronized (this) {
            if (schedulingFilesByFolders == null) {
                schedulingFilesByFolders = new HashMap();
            }
            int index = name.lastIndexOf('/');
            dir = (index < 0) ? "" : name.substring(0, index);
            String file = (index < 0) ? name : index < (name.length() - 1) ? name.substring(index + 1) : "";
            files = (Set) schedulingFilesByFolders.get(dir);
            if (files != null) {
                files.remove(file);
                if (files.size() == 0) files = null;
            }
            if (files == null) {
                schedulingFilesByFolders.remove(dir);
            }
        }
        if (files == null) {
            performRefresh(dir, Boolean.FALSE);
        }
    }
    
    private void readObject (java.io.ObjectInputStream ois)
        throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        if (supporter != null && supporter instanceof VcsActionSupporter) {
            ((VcsActionSupporter)supporter).setFileSystem(fileSystem);
        }
    }


}
