/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium.server;

import org.junit.Test;
import org.netbeans.api.server.properties.InstanceProperties;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openqa.selenium.server.SeleniumServer;
import static org.junit.Assert.*;

/**
 *
 * @author Jindrich Sedek
 */
public class SeleniumPropertiesTest {

    @Test
    public void testCreateSheet() throws Exception {
        Sheet sheet = SeleniumProperties.createSheet();
        assertNotNull(sheet);
        assertEquals(1, sheet.toArray().length);
        Set set = sheet.get(Sheet.PROPERTIES);
        assertNotNull(set);
        assertNotNull(set.get(SeleniumProperties.START_ON_STARTUP));
        assertNotNull(set.get(SeleniumProperties.PORT));
    }

    @Test
    public void testGetInstanceProperties() {
        InstanceProperties ip = SeleniumProperties.getInstanceProperties();
        assertEquals(SeleniumServer.DEFAULT_PORT, ip.getInt(SeleniumProperties.PORT, 0));
        assertEquals(true, ip.getBoolean(SeleniumProperties.START_ON_STARTUP, false));

        ip.putBoolean(SeleniumProperties.START_ON_STARTUP, false);
        ip = SeleniumProperties.getInstanceProperties();
        assertEquals(SeleniumServer.DEFAULT_PORT, ip.getInt(SeleniumProperties.PORT, 0));
        assertEquals(false, ip.getBoolean(SeleniumProperties.START_ON_STARTUP, true));

    }
}