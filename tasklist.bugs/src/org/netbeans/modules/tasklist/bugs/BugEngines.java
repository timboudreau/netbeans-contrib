package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.netbeans.modules.tasklist.bugs.bugzilla.BZBugEngine;
import org.netbeans.modules.tasklist.bugs.javanet.JavaNetEngine;

/**
 * TODO document
 * 
 * @author Petr Kuzel
 */
public final class BugEngines {

    static String IZ = "Issuezilla";

    static String BZ = "Bugzilla";

    // it has fixed URL, same Sf.net etc
    static String JAVA_NET = "Java.net";

    static String[] list() {
        return new String[] {IZ, BZ, JAVA_NET};
    }

    static BugEngine get(String id) {
        if (IZ.equals(id)) {
            return new IZBugEngine();
        } else if (BZ.equals(id)) {
            return new BZBugEngine();
        } else if (JAVA_NET.equals(id)) {
            return new JavaNetEngine();
        } else {
            assert false : "Invalid id " + id;
            return null;
        }
    }

}
