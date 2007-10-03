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
package org.netbeans.modules.vcscore.turbo;

import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.openide.filesystems.FileObject;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;


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

    /** Custom string interning */
    private static Set interning = new HashSet(20);

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
        sticky = intern(elements[StatusFormat.ELEMENT_INDEX_STICKY]);
        time = elements[StatusFormat.ELEMENT_INDEX_TIME];
        date = elements[StatusFormat.ELEMENT_INDEX_DATE];
        retrieval = System.currentTimeMillis();
        initDeep2();
    }

    /** Custom string interning with limited size pool. */
    private synchronized String intern(String s) {
        if (s == null) return null;
        if (interning.contains(s)) {
            Iterator it = interning.iterator();
            while (it.hasNext()) {
                String next = (String) it.next();
                if (s.equals(next)) return next;
            }
        } else {
            if (interning.size() > 19) {
                interning.remove(interning.iterator().next());
            }
            interning.add(s);
        }
        return s;
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

    /** Gets file name. Always without trailing '/' regardles folder. */
    public String getFileName() {
        if (name.endsWith("/")) {
            return name.substring(0, name.length() -1);
        } else {
            return name;
        }
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
        this.sticky = intern(sticky);
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
