/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        s.addTestSuite(GCListenerTest.class);
        return s;
    }
}
