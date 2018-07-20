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
