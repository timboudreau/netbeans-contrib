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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.api.common.queries;

import java.util.List;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.queries.SharabilityQueryImplementation;
import org.openide.util.Parameters;

/**
 * SharabilityQuerySupport is a support class for creating
 * a query to provide information about files sharability.
 * @since org.netbeans.modules.java.api.common/0 1.0
 * @author Tomas Mysik
 * @see SharabilityQueryImplementation
 */
public final class SharabilityQuerySupport {

    private SharabilityQuerySupport() {
    }

    /**
     * Create a new query to provide information about files sharability. The returned query listens to the changes
     * in particular source roots.
     * @param helper {@link AntProjectHelper} used for creating a query itself.
     * @param evaluator {@link PropertyEvaluator}a property evaluator to interpret paths with.
     * @param srcRoots a list of source roots to treat as sharable.
     * @param testRoots a list of test roots to treat as sharable.
     * @param additionalSourceRoots a list of additional paths to treat as sharable.
     * @return a {@link SharabilityQueryImplementation} to provide information about files sharability.
     */
    public static SharabilityQueryImplementation create(AntProjectHelper helper, PropertyEvaluator evaluator,
            SourceRoots srcRoots, SourceRoots testRoots, final List<String> additionalSourceRoots) {
        Parameters.notNull("helper", helper);
        Parameters.notNull("evaluator", evaluator);
        Parameters.notNull("srcRoots", srcRoots);
        Parameters.notNull("testRoots", testRoots);

        return new SharabilityQueryImpl(helper, evaluator, srcRoots, testRoots, additionalSourceRoots);
    }
}