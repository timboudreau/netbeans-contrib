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
import org.openide.filesystems.FileObject;

import java.util.Set;
import java.util.Collections;


/**
 * Additional metadata for files in versioning system working directory.
 * It defines a bunch of properties. All clients that
 * create instance must fill it consistently, setting values
 * or <code>null</code>-ing them if the client does
 * not have current value. Clients can also use
 * clone constructor and update just changed values
 * (inlucing <code>null</code>-ing them.)
 * <p><small>e.g. <pre>cvs up</pre><pre>U folder/file</pre>
 * command knows that the <tt>file</tt> was updated but it does
 * not know recent revision. It updates status and <code>null</code>s
 * revision.</small>
 * <p>
 * The object is treated as immutable for most of its lifetime, once
 * initialized creator must invoke {@link #freeze} to assure it.
 * <p>
 * Note: {@link org.netbeans.modules.vcscore.turbo.local.FileAttributeProvider FileAttributeProvider}s
 * must report all statuses including local/non-versioned ones. It differs
 * from {@link org.netbeans.modules.vcscore.FileReaderListener FileReaderListener} events
 * sources (<tt>LIST</tt>) that must not report local/non-versioned statuses. Mentioning
 * here because in typical setup <code>FileProperties</code>
 * provider and <tt>LIST</tt> provider is implemented by one package. It puts
 * extra:
 * <ul>
 *   <li><tt>LIST</tt> and <code>FileAttributeProvider</code> consistency for versioned files and
 *   <li><code>FileAttributeProvider</code> and framework consistency for local files
 * </ul>
 * requirement.
 *
 * @author Petr Kuzel
 */
public final class FileProperties {

    // It MUST not keep reference to associated FileObject

    /**
     * Attribute holding this object.
     */
    public static final String ID = "VCS-FileProperties"; // NOI18N

    /** Retrieved from repository on */
    private long retrieval;

    private String name;
    private String status;
    private String revision;
    private String sticky;
    private String attr;
    private String date;
    private String time;
    private long size = -1;
    private String locker;

    // frozen?
    private boolean canUpdate = true;

    /** For debuging purposes trace creation point. 3rd frame is caller. */
    private Exception origin;

    public FileProperties() {
        retrieval = System.currentTimeMillis();
        initDeep2();
    }

    /** Clones FileProperties except retrieval time. Clone is not frozen. */
    public FileProperties(FileProperties fprops) {
        if (fprops != null) {
            status = fprops.status;
            name = fprops.name;
            revision = fprops.revision;
            sticky = fprops.sticky;
            attr = fprops.attr;
            date = fprops.date;
            time = fprops.time;
            size = fprops.size;
            locker = fprops.locker;
        }
        retrieval = System.currentTimeMillis();
        initDeep2();
    }

    private void initDeep2() {
        origin = new Exception("<init> call stack");
    }
    /**
     * Creates file properties with LOCAL status for given file.
     * Callers that are not sure about local status should call
     * {@link #createLocal(FileObject)}.
     * @param name name of file (without path), trailing '/' denotes folder
     */
    public static FileProperties createLocal(String name) {
        FileProperties fprops = new FileProperties();
        fprops.initDeep2();
        fprops.setName(name);
        fprops.setStatus(Statuses.getLocalStatus());  // XXX here we set generic status while all other places use VCS specifics
        return fprops;
    }

    /**
     * Creates file properties with LOCAL or IGNORED status for given file.
     * @param fileObject target file
     */
    public static FileProperties createLocal(FileObject fileObject) {
        FileProperties fprops = new FileProperties();
        fprops.initDeep2();
        String name = fileObject.isFolder() ? fileObject.getNameExt() + '/' : fileObject.getNameExt();
        fprops.setName(name);
        FileObject folder = fileObject.getParent();
        IgnoreList ignore = IgnoreList.forFolder(folder);
        if (ignore.isIgnored(fileObject.getNameExt())) {
            fprops.setStatus(Statuses.STATUS_IGNORED);
        } else {
            fprops.setStatus(Statuses.getLocalStatus());
        }
        return fprops;
    }

    /** Constructs from StatusFormatElements. */
    public FileProperties(String[] elements) {
        status = elements[StatusFormat.ELEMENT_INDEX_STATUS];
        if (status != null) {
            status = status.intern();
        }
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
        initDeep2();
    }

    /**
     * Status as provided by VCS repository or LOCAL, IGNORED or UNKNOWN assigned by cache.
     * It's not abstract repository neutral status as defined by FileStatusInfo. 
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets new status. Please read consistency note in {@link FileProperties class} Javadoc.
     * @param status updates status or <code>null</code>
     */
    public void setStatus(String status) {
        assert canUpdate;
        if (status != null) {
            this.status = status.intern();
        } else {
            this.status = null;
        }
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

    /** Gets file name. Trailing '/' denotes folder. */
    public String getName() {
        return name;
    }

    /** Sets file name. Trailing '/' denotes folder otherwise must match name of associated file. */
    public void setName(String name) {
        assert canUpdate;
        this.name = name;
    }

    /** Returns recent VCS specifics revision string (e.g. "1.1", "-1", "r45/hotfix3") or <codE>null</code> for unknown. */
    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        assert canUpdate;
        this.revision = revision;
    }

    /** Returns recent VCS specifics sticky string (branch/date) or <codE>null</code> for unknown. */
    public String getSticky() {
        return sticky;
    }

    public void setSticky(String sticky) {
        assert canUpdate;
        this.sticky = sticky;
    }

    /** VCS specific additonal attributes (e.g CVS -kb) or <codE>null</code> for unknown. */
    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        assert canUpdate;
        this.attr = attr;
    }

    /** Returns recent VCS specifics date string or <codE>null</code> for unknown. */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        assert canUpdate;
        this.date = date;
    }

    /** Returns recent VCS specifics time string or <codE>null</code> for unknown. */
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        assert canUpdate;
        this.time = time;
    }

    /** Returns recent file size or <codE>-1</code> for unknown. */
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
        return Statuses.getLocalStatus().equals(status) || Statuses.STATUS_IGNORED.equals(status);
    }

    /**
     * Make object immutable, all setters throw exception.
     */
    public void freeze() {
        canUpdate = false;
    }

    /** What VCS specifics user holds lock on the file. <code>null</code> for unknown. */
    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        assert canUpdate;
        this.locker = locker;
    }

    /**
     * Get versioning system specifics status, "Yet-Unknown" for unknown and "Local"
     * or "Ignored" for local files (defined in {@link Statuses}).
     * @param fprops propeties or null
     * @return status or UNKNOWN for null properties or status
     */
    public static String getStatus(FileProperties fprops) {      // XXX confusing name
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

    /** For debugging purposes. */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(name).append("=>").append(status).append(" allocated:");  // NOI18N
        if (origin != null) {
            StackTraceElement[] stack = origin.getStackTrace();
            int max = stack.length > 6 ? 6 : stack.length;
            for (int i = 2; i<max; i++) {
                sb.append("\n" + stack[i].toString());  // NOI18N
            }
        } else {
            sb.append("<unknown stack trace>");  // NOI18N
        }
        sb.append("]");  // NOI18N
        return sb.toString();
    }

}
