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

import org.netbeans.modules.vcscore.caching.StatusFormat;


/**
 * Additional FileObject metadata for versioned files.
 * The object is treated as immutable. One initializing
 * the object should invoke {@link #freeze} to assure it.
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
    private long    size = 0;
    private String locker;

    // XXX it's not writen to disc layer
    private IgnoreList ignoreList;

    private boolean canUpdate = true;


    public FileProperties() {
        retrieval = System.currentTimeMillis();
    }

    /** Clones FileProperties except retrieval time. Clone is not frozen. */
    public FileProperties(FileProperties fprops) {
        status = fprops.status;
        retrieval = System.currentTimeMillis();
        name = fprops.name;
        revision = fprops.revision;
        sticky = fprops.sticky;
        attr = fprops.attr;
        date = fprops.date;
        time = fprops.time;
        size = fprops.size;
        locker = fprops.locker;
        ignoreList = fprops.ignoreList;
    }

    /**
     * Creates file properties with LOCAL status for given file.
     * @param name name of file (without path)
     */
    public FileProperties(String name) {
        this.name = name;
        status = Statuses.getLocalStatus();
        retrieval = System.currentTimeMillis();
    }

    /** Constructs from StatusFormatElements. */
    FileProperties(String[] elements) {
        status = elements[StatusFormat.ELEMENT_INDEX_STATUS];
        name = elements[StatusFormat.ELEMENT_INDEX_FILE_NAME];
        locker = elements[StatusFormat.ELEMENT_INDEX_LOCKER];
        attr = elements[StatusFormat.ELEMENT_INDEX_ATTR];
        revision = elements[StatusFormat.ELEMENT_INDEX_REVISION];
        try {
            size = Long.parseLong(elements[StatusFormat.ELEMENT_INDEX_SIZE]);
        } catch (NumberFormatException ex) {
            size = 0;
        }
        sticky = elements[StatusFormat.ELEMENT_INDEX_STICKY];
        time = elements[StatusFormat.ELEMENT_INDEX_TIME];
        date = elements[StatusFormat.ELEMENT_INDEX_DATE];
        retrieval = System.currentTimeMillis();
    }

    /** Clients must access using {@link IgnoreList#forFolder}.*/
    IgnoreList getIgnoreList() {
        return ignoreList;
    }

    void setIgnoreList(IgnoreList list) {
        ignoreList = list;
    }

    /**
     * Status as provided by VCS repository or LOCAL or UNKNOWN assidned by cache.
     * It's not abstract repository neutral status as defined by FileStatusInfo. 
     */
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

    /** VCS specific additonal attributes (e.g CVS -kb) */
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

    public long getSize() {
        return size;
    }

    public String getSizeAsString() {
        return "" + size;
    }

    public void setSize(long size) {
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

    /** What user holds lock on the file. */
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

    /** Converts to format accepted by StatusFormat */
    public String[] toElements() {
        String[] elements = new String[StatusFormat.NUM_ELEMENTS];
        elements[StatusFormat.ELEMENT_INDEX_FILE_NAME] = getName();
        elements[StatusFormat.ELEMENT_INDEX_STATUS] = getStatus();
        elements[StatusFormat.ELEMENT_INDEX_LOCKER] = getLocker();
        elements[StatusFormat.ELEMENT_INDEX_ATTR] = getAttr();
        elements[StatusFormat.ELEMENT_INDEX_REVISION] = getRevision();
        elements[StatusFormat.ELEMENT_INDEX_SIZE] = getSizeAsString();
        elements[StatusFormat.ELEMENT_INDEX_STICKY] = getSticky();
        elements[StatusFormat.ELEMENT_INDEX_TIME] = getTime();
        elements[StatusFormat.ELEMENT_INDEX_DATE] = getDate();

        return elements;
    }

    public String toString() {
        return "[" + name + "=>"  + status + "]";
    }

}
