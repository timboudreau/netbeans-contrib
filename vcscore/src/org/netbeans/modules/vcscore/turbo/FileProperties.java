/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;


/**
 * Additional FileObject metadata for versioned files.
 * The object is treated as immutable. One initializing
 * the object should invoke {@link #freeze} to assure it.
 *
 * TODO Copied from PersistentData no meaning of certain fields known.
 *
 *
 * @author Petr Kuzel
 */
public final class FileProperties {

    // It MUST not keep reference to associated FileObject

    private String status;

    /** Retrieved from repository on */
    private long retrieval;

    private String name = ""; // NOI18N
    private String revision = ""; // NOI18N
    private String sticky = ""; // NOI18N
    private String attr = ""; // NOI18N
    private String date = ""; // NOI18N
    private String time = ""; // NOI18N
    private int    size = 0;
    private String locker;

    // XXX it's not writen to disc layer
    private IgnoreList ignoreList;

    private boolean canUpdate = true;


    public FileProperties() {
        retrieval = System.currentTimeMillis();
    }

    /** Clients must access using {@link IgnoreList#forFolder}.*/
    IgnoreList getIgnoreList() {
        return ignoreList;
    }

    void setIgnoreList(IgnoreList list) {
        ignoreList = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        assert canUpdate;
        this.status = status;
    }

    /**
     * Keeps time when updated from repository. The longer time
     * elapsed since that point the more unknown data this hold.
     */
    public long getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(long retrieval) {
        assert canUpdate;
        this.retrieval = retrieval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        assert canUpdate;
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        assert canUpdate;
        this.revision = revision;
    }

    public String getSticky() {
        return sticky;
    }

    public void setSticky(String sticky) {
        assert canUpdate;
        this.sticky = sticky;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        assert canUpdate;
        this.attr = attr;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        assert canUpdate;
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        assert canUpdate;
        this.time = time;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        assert canUpdate;
        this.size = size;
    }

    /**
     * Determines whether reported status means that file is not versioned.
     * @return true if for sure local, otherwise false (even if unknown)
     */
    public boolean isLocal() {
        if (status == null) return false;
        return Statuses.getLocalStatus().equals(status);
    }

    /**
     * Make object immutable, all setters throw exception.
     */
    public void freeze() {
        canUpdate = false;
    }

    /** Who holds lock on the file. */
    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        assert canUpdate;
        this.locker = locker;
    }

    /**
     * Get status or UNKNOWN
     * @param fprops propeties or null
     * @return status or UNKNOWN for null properties or status
     */
    public static String getStatus(FileProperties fprops) {
        if (fprops == null) {
            return Statuses.getUnknownStatus();
        } else {
            String status = fprops.getStatus();
            if (status == null) {
                return Statuses.getUnknownStatus();
            } else {
                return status;
            }
        }
    }
}

