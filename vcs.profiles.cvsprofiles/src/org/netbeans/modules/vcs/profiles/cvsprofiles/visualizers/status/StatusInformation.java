/*****************************************************************************
 * Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the CVS Client Library.
 * The Initial Developer of the Original Code is Robert Greig.
 * Portions created by Robert Greig are Copyright (C) 2000.
 * All Rights Reserved.
 *
 * Contributor(s): Robert Greig.
 *****************************************************************************/
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;

import java.io.*;
import java.util.*;


/**
 * Describes status information for a file. This is the result of doing a
 * cvs status command. The fields in instances of this object are populated
 * by response handlers.
 * @author  Robert Greig
 */
public final class StatusInformation extends FileInfoContainer {
     /**
     * The Added status, i.e. the file has been added to the repository
     * but not committed yet.
     */
    public static final String ADDED = "Locally Added"; //NOI18N

    /**
     * The Removed status, i.e. the file has been removed from the repository
     * but not committed yet
     */
    public static final String REMOVED = "Locally Removed"; //NOI18N

    /**
     * The locally modified status, i.e. the file has been modified locally
     * and is out of sync with the repository
     */
    public static final String MODIFIED = "Locally Modified"; //NOI18N

    /**
     * The up-to-date status, i.e. the file is in sync with the repository
     */
    public static final String UP_TO_DATE = "Up-to-date"; //NOI18N

    /**
     * The "needs checkout" status, i.e. the file is out of sync with the
     * repository and needs to be updated
     */
    public static final String NEEDS_CHECKOUT = "Needs Checkout"; //NOI18N

    /**
     * The "needs patch" status, i.e. the file is out of sync with the
     * repository and needs to be patched
     */
    public static final String NEEDS_PATCH = "Needs Patch"; //NOI18N

    /**
     * The "needs merge" status, i.e. the file is locally modified and
     * the file in the repository has been modified too
     */
    public static final String NEEDS_MERGE = "Needs Merge"; //NOI18N

    /**
     * The "conflicts" status, i.e. the file has been merged and now
     * has conflicts that need resolved before it can be checked-in
     */
    public static final String HAS_CONFLICTS = "File had conflicts on merge"; //NOI18N

    /**
     * The unknown status, i.e. the file is not known to the CVS repository.
     */
    public static final String UNKNOWN = "Unknown"; //NOI18N
 
    private File file;
    private String status;
    private String workingRevision;
    private String repositoryRevision;
    private String repositoryFileName;
    private String stickyDate;
    private String stickyOptions;
    private String stickyTag;

    /**
     * Hold key pairs of existing tags.
     */
    private List tags;

    private StringBuffer symNamesBuffer;

    public StatusInformation() {
        setAllExistingTags(null);
    }

    /**
     * Getter for property file.
     * @return Value of property file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter for property file.
     * @param file New value of property file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter for property status.
     * @return Value of property status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter for property status.
     * @param status New value of property status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the status as a String.
     * The String returned are definitely the static-final-instances.
     */
    public String getStatusString() {
        if (status == null) {
            return null;
        }

        return status.toString();
    }

    /**
     * Sets the status by the specified string.
     
    public void setStatusString(String statusString) {
        setStatus(FileStatus.getStatusForString(statusString));
    }
*/
    /**
     * Getter for property workingRevision.
     * @return Value of property workingRevision.
     */
    public String getWorkingRevision() {
        return workingRevision;
    }

    /**
     * Setter for property workingRevision.
     * @param workingRevision New value of property workingRevision.
     */
    public void setWorkingRevision(String workingRevision) {
        this.workingRevision = workingRevision;
    }

    /**
     * Getter for property repositoryRevision.
     * @return Value of property repositoryRevision.
     */
    public String getRepositoryRevision() {
        return repositoryRevision;
    }

    /**
     * Setter for property repositoryRevision.
     * @param repositoryRevision New value of property repositoryRevision.
     */
    public void setRepositoryRevision(String repositoryRevision) {
        this.repositoryRevision = repositoryRevision;
    }

    /**
     * Getter for property repositoryFileName.
     * @return Value of property repositoryFileName.
     */
    public String getRepositoryFileName() {
        return repositoryFileName;
    }

    /**
     * Setter for property repositoryFileName.
     * @param repositoryRevision New value of property repositoryFileName.
     */
    public void setRepositoryFileName(String repositoryFileName) {
        this.repositoryFileName = repositoryFileName;
    }

    /**
     * Getter for property stickyTag.
     * @return Value of property stickyTag.
     */
    public String getStickyTag() {
        return stickyTag;
    }

    /**
     * Setter for property stickyTag.
     * @param stickyTag New value of property stickyTag.
     */
    public void setStickyTag(String stickyTag) {
        this.stickyTag = stickyTag;
    }

    /**
     * Getter for property stickyDate.
     * @return Value of property stickyDate.
     */
    public String getStickyDate() {
        return stickyDate;
    }

    /**
     * Setter for property stickyDate.
     * @param stickyDate New value of property stickyDate.
     */
    public void setStickyDate(String stickyDate) {
        this.stickyDate = stickyDate;
    }

    /**
     * Getter for property stickyOptions.
     * @return Value of property stickyOptions.
     */
    public String getStickyOptions() {
        return stickyOptions;
    }

    /**
     * Setter for property stickyOptions.
     * @param stickyOptions New value of property stickyOptions.
     */
    public void setStickyOptions(String stickyOptions) {
        this.stickyOptions = stickyOptions;
    }

    public void addExistingTag(String tagName, String revisionNumber) {
        if (tags == null) {
            tags = new ArrayList();
        }
        SymName newName = new SymName();
        newName.setTag(tagName);
        newName.setRevision(revisionNumber);
        tags.add(newName);
    }

    public List getAllExistingTags() {
        return tags;
    }

    public void setAllExistingTags(List tags) {
        this.tags = tags;
    }

    /** Search the symbolic names by number of revision. If not found, return null.
     */
    public List getSymNamesForRevision(String revNumber) {
        if (tags == null) {
            return null;
        }

        List list = new LinkedList();

        for (Iterator it = tags.iterator(); it.hasNext();) {
            StatusInformation.SymName item = (StatusInformation.SymName)it.next();
            if (item.getRevision().equals(revNumber)) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Search the symbolic names by name of tag (symbolic name).
     * If not found, return null.
     */
    public StatusInformation.SymName getSymNameForTag(String tagName) {
        if (tags == null) {
            return null;
        }

        for (Iterator it = tags.iterator(); it.hasNext();) {
            StatusInformation.SymName item = (StatusInformation.SymName)it.next();
            if (item.getTag().equals(tagName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Return a string representation of this object. Useful for debugging.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\nFile: "); //NOI18N
        buf.append((file != null) ? file.getAbsolutePath()
                   : "null"); //NOI18N
        buf.append("\nStatus is: "); //NOI18N
        buf.append(getStatusString());
        buf.append("\nWorking revision: "); //NOI18N
        buf.append(workingRevision);
        buf.append("\nRepository revision: "); //NOI18N
        buf.append("\nSticky date: "); //NOI18N
        buf.append(stickyDate);
        buf.append("\nSticky options: "); //NOI18N
        buf.append(stickyOptions);
        buf.append("\nSticky tag: "); //NOI18N
        buf.append(stickyTag);
        if (tags != null && tags.size() > 0) {
            // we are having some tags to print
            buf.append("\nExisting Tags:"); //NOI18N
            for (Iterator it = tags.iterator(); it.hasNext();) {
                buf.append("\n  "); //NOI18N
                buf.append(it.next().toString());
            }
        }
        return buf.toString();
    }

    /**
     * An inner class storing information about a symbolic name.
     * Consists of a pair of Strings. tag + revision.
     */
    public static class SymName {
        private String tag;
        private String revision;

        public SymName() {
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String symName) {
            tag = symName;
        }

        public void setRevision(String rev) {
            revision = rev;
        }

        public String getRevision() {
            return revision;
        }

        public String toString() {
            return getTag() + " : " + getRevision(); //NOI18N
        }
    }
}
