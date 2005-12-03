package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.bugs.issuezilla.IZBugEngine;
import org.netbeans.modules.tasklist.bugs.bugzilla.BZBugEngine;
import org.netbeans.modules.tasklist.bugs.javanet.JavaNetEngine;
import org.netbeans.modules.tasklist.bugs.scarab.ScarabEngine;

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
    
    static String SCARAB = "Scarab";

    static String[] list() {
        return new String[] {IZ, BZ, JAVA_NET, SCARAB};
    }

    static BugEngine get(String id) {
        if (IZ.equals(id)) {
            return new IZBugEngine();
        } else if (BZ.equals(id)) {
            return new BZBugEngine();
        } else if (JAVA_NET.equals(id)) {
            return new JavaNetEngine();
        } else if (SCARAB.equals(id)) {
            return new ScarabEngine();
        } else {
            assert false : "Invalid id " + id;
            return null;
        }
    }

}
