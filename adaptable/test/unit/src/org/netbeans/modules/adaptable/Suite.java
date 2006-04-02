/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.adaptable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import junit.framework.TestSuite;
import org.netbeans.junit.NbTestSuite;

/** The whole suite of tests.
 *
 * @author jarda
 */
public class Suite {
    /** list of objects that shall not be counted when checking occupied
     * size of adaptable objects.
     */
    static final List<Object> excludeFromSize = new ArrayList<Object>();
    static {
        excludeFromSize.add(SingletonizerImpl.excludeFromAssertSize());
    }


    private Suite() {
    }


    /** Creates new test compatibility kit for modules that which to
     * re-implement this adaptable API. Currently used from adaptable lookup
     * framework
     *
     * @param excludeFromSize excludes this set of objects from size consideration
     *    or null if no such exclude is needed
     */
    public static TestSuite create(Set<? extends Object> excludeFromSize) {
        if (excludeFromSize != null) {
            Suite.excludeFromSize.addAll(excludeFromSize);
        }

        NbTestSuite s = new NbTestSuite();
        s.addTestSuite(SingletonizerLifeCycleTest.class);
        s.addTestSuite(SingletonizerTest.class);
        s.addTestSuite(SingletonizerTwoDifferentObjectsTest.class);
        s.addTestSuite(SingletonizerValueChangedTest.class);
        return s;
    }
}
