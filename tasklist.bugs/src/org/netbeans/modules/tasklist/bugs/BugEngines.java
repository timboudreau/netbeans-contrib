package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.netbeans.modules.tasklist.bugs.bugzilla.BZBugEngine;

/**
 * TODO document
 * 
 * @author Petr Kuzel
 */
public final class BugEngines {

    String IZ = "Issuezilla";

    String BZ = "Bugzilla";

    String[] list() {
        return new String[] {IZ, BZ};
    }

    BugEngine get(String id) {
        if (IZ.equals(id)) {
            return new IZBugEngine();
        } else if (BZ.equals(id)) {
            return new BZBugEngine();
        } else {
            assert false : "Invalid id " + id;
            return null;
        }
    }
}
