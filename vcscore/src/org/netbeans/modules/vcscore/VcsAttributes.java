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
import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.Table;

/**
 * Implementation of file attributes for version control systems. All attributes
 * read/write operations are delegated to the DefaultAttributes, whith the exception
 * of VCS-related attributes. These special attributes are not propagated to
 * the DefaultAttributes, but are interpreted as VCS commands.
 * @author  Martin Entlicher
 */
public class VcsAttributes extends DefaultAttributes {
    
    /**
     * Attribute name for a VCS action.
     */
    public static final String VCS_ACTION = "VCS_ACTION";
    /**
     * Attribute name for a VCS action, that schedules the VCS operation for later processing.
     * This action should be performed on important secondary file objects.
     */
    public static final String VCS_SCHEDULING_SECONDARY_FO_ACTION = "VCS_SCHEDULING_SECONDARY_FO_ACTION";
    /**
     * The scheduling VCS Add action name. The file will be added to the VCS repository
     * as soon as the primary file will be committed.
     */
    public static final String VCS_SCHEDULING_ADD = "ADD";
    /**
     * The scheduling VCS Remove action name. The file will be removed from the VCS repository
     * as soon as the primary file will be committed.
     */
    public static final String VCS_SCHEDULING_REMOVE = "REMOVE";
    /**
     * The attribute name where scheduled files are stored.
     */
    public static final String VCS_SCHEDULED_FILES_ATTR = "VCS_SCHEDULED_FILES";
    /**
     * The attribute name marking the file as scheduled for later processing.
     * The value should be {@link VCS_SCHEDULING_ADD} or {@link VCS_SCHEDULING_REMOVE}.
     */
    public static final String VCS_SCHEDULED_FILE_ATTR = "VCS_SCHEDULED_FILE";
    /**
     * The VCS Add command name. This command adds the file to the VCS repository.
     */
    public static final String VCS_ACTION_ADD = "VCS_ADD";
    /**
     * The VCS Remove command name. This command removes the file from the VCS repository.
     */
    public static final String VCS_ACTION_REMOVE = "VCS_REMOVE";
    /**
     * This attribute is set when the action is done. The value is Boolean.TRUE
     * or Boolean.FALSE depending on the command exit status.
     */
    public static final String VCS_ACTION_DONE = "VCS_ACTION_DONE";
    /**
     * The description to the action. Usually it is a message, that is given to
     * the VCS command (such as a change log for check in)
     */
    public static final String VCS_ACTION_DESCRIPTION = "VCS_ACTION_DESCRIPTION";
    
    /**
     * Read the attribute of this name to obtain the VCS file status.
     */
    public static String VCS_STATUS = "VCS_STATUS";
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are not version controlled.
     */
    public static String VCS_STATUS_LOCAL = "VCS_STATUS_LOCAL";
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are present in VCS repository, but do not exist locally.
     */
    public static String VCS_STATUS_MISSING = "VCS_STATUS_MISSING";
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are version controlled and are present locally.
     */
    public static String VCS_STATUS_UP_TO_DATE = "VCS_STATUS_UP_TO_DATE";
    /**
     * The status, that is returned from {@link readAttribute} for files,
     * that are not recognized. We can not say whether they are version controlled
     * or not.
     */
    public static String VCS_STATUS_UNKNOWN = "VCS_STATUS_UNKNOWN";
    
    private VcsActionSupporter supporter;
        
    private VcsFileSystem fileSystem;

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
        } else {
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
    public void writeAttribute(String name, String attrName, Object value) throws IOException, java.net.UnknownServiceException {
        if (VCS_ACTION.equals(attrName) && value instanceof FeatureDescriptor) {
            performVcsAction(name, (FeatureDescriptor) value);
        } else if (VCS_SCHEDULING_SECONDARY_FO_ACTION.equals(attrName) && value instanceof String) {
            if (scheduleSecondaryFOVcsAction(name, (String) value)) {
                super.writeAttribute(name, VCS_SCHEDULED_FILE_ATTR, value);
            }
        } else {
            super.writeAttribute(name, attrName, value);
        }
    }
    
    private void performVcsAction(final String name, final FeatureDescriptor descriptor) throws java.net.UnknownServiceException {
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
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                VcsCommandExecutor[] executors = VcsAction.doCommand(files, cmd, additionalVars, fileSystem);
                boolean status = true;
                for (int i = 0; i < executors.length; i++) {
                    fileSystem.getCommandsPool().waitToFinish(executors[i]);
                    status &= executors[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED;
                }
                descriptor.setValue(VCS_ACTION_DONE, new Boolean(status));
            }
        });
    }
    
    private boolean scheduleSecondaryFOVcsAction(final String name, final String actionName) {
        org.openide.filesystems.FileObject fo = fileSystem.findFileObject(name);
        if (fo == null) return false;
        org.openide.filesystems.FileObject primary;
        try {
            org.openide.loaders.DataObject dobj = org.openide.loaders.DataObject.find(fo);
            primary = dobj.getPrimaryFile();
            if (primary.equals(fo)) return false;
        } catch (org.openide.loaders.DataObjectNotFoundException exc) {
            return false;
        }
        if (VCS_STATUS_LOCAL.equals(primary.getAttribute(VCS_STATUS))) return false;
        int id;
        if (VCS_SCHEDULING_ADD.equals(actionName)) {
            //fileSystem.addScheduledSecondaryFO(name, VcsFileSystem.SCHEDULING_ACTION_ADD_ID);
            FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(VcsCommand.NAME_SCHEDULE_ADD);
            try {
                performVcsAction(name, descriptor);
            } catch (java.net.UnknownServiceException unsExc) {}
            id = 1;
        } else if (VCS_SCHEDULING_REMOVE.equals(actionName)) {
            //fileSystem.addScheduledSecondaryFO(name, VcsFileSystem.SCHEDULING_ACTION_REMOVE_ID);
            FeatureDescriptor descriptor = new FeatureDescriptor();
            descriptor.setName(VcsCommand.NAME_SCHEDULE_REMOVE);
            try {
                performVcsAction(name, descriptor);
            } catch (java.net.UnknownServiceException unsExc) {}
            id = 0;
        } else return false;
        Set[] scheduled = (Set[]) primary.getAttribute(VCS_SCHEDULED_FILES_ATTR);
        if (scheduled == null) scheduled = new HashSet[2];
        if (scheduled[id] == null) scheduled[id] = new HashSet();
        scheduled[id].add(name);
        try {
            primary.setAttribute(VCS_SCHEDULED_FILES_ATTR, scheduled);
        } catch (IOException ioExc) {
            return false;
        }
        return true;
    }
    
    private void readObject (java.io.ObjectInputStream ois)
        throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        if (supporter != null && supporter instanceof VcsActionSupporter) {
            ((VcsActionSupporter)supporter).setFileSystem(fileSystem);
        }
    }


}
