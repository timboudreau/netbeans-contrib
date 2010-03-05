/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.java.queries.AnnotationProcessingQuery;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.java.queries.AnnotationProcessingQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class AnnotationProcessingQueryImplTest extends NbTestCase {

    public AnnotationProcessingQueryImplTest(String n) {
        super(n);
    }

    protected @Override void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
    }

    public void testResult() throws Exception {
        File r = getWorkDir();
        File s = new File(r, "src");
        if (!s.mkdir()) {
            throw new Exception();
        }
        FileObject fo = FileUtil.toFileObject(s);
        Cache.put(s + JavaCacheConstants.SOURCE, s.getAbsolutePath());
        AnnotationProcessingQueryImplementation apq = new AnnotationProcessingQueryImpl();
        AnnotationProcessingQuery.Result apqr = apq.getAnnotationProcessingOptions(fo);
        assertNotNull(apqr);
        // Defaults:
        assertTrue(apqr.annotationProcessingEnabled());
        assertEquals(null, apqr.annotationProcessorsToRun());
        assertEquals(null, apqr.sourceOutputDirectory());
        assertEquals(Collections.emptyMap(), apqr.processorOptions());
        Cache.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, "");
        assertTrue(apqr.annotationProcessingEnabled());
        assertEquals(null, apqr.annotationProcessorsToRun());
        assertEquals(null, apqr.sourceOutputDirectory());
        Cache.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, "-proc:none");
        assertFalse(apqr.annotationProcessingEnabled());
        assertEquals(null, apqr.annotationProcessorsToRun());
        assertEquals(null, apqr.sourceOutputDirectory());
        Cache.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, "-processor proc.One,proc.Two");
        assertTrue(apqr.annotationProcessingEnabled());
        assertEquals(Arrays.asList("proc.One", "proc.Two"), apqr.annotationProcessorsToRun());
        assertEquals(null, apqr.sourceOutputDirectory());
        File gensrc = new File(r, "gensrc");
        if (!gensrc.mkdir()) {
            throw new Exception();
        }
        Cache.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, "-s " + gensrc);
        assertTrue(apqr.annotationProcessingEnabled());
        assertEquals(null, apqr.annotationProcessorsToRun());
        assertEquals(gensrc.toURI().toURL(), apqr.sourceOutputDirectory());
        Cache.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, "-Aenabled=true -Adebug");
        Map<String,String> expected = new HashMap<String,String>();
        expected.put("enabled", "true");
        expected.put("debug", null);
        assertEquals(expected, apqr.processorOptions());
    }

}
