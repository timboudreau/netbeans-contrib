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

package org.netbeans.modules.tasklist.usertasks.translators;

import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.data.CalendarBuilder;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * Workaround for the X-NETBEANS-WORK-PERIOD;START=20050524T083759Z:76 and
 * X-NETBEANS-DEPENDENCY.
 *
 * @author tl
 */
public class MyCalendarBuilder extends CalendarBuilder {
    private List cmps = new ArrayList();
    private String property;

    /**
     * Creates a new instance of MyCalendarParser
     */
    public MyCalendarBuilder() {
    }

    public void parameter(String name, String value) throws java.net.URISyntaxException {
        String c = (String) cmps.get(cmps.size() - 1);
        if (c.equals("VTODO") && "X-NETBEANS-WORK-PERIOD".equals(property) &&  // NOI18N
            name.equals("START")) // NOI18N
            name = "X-NETBEANS-START"; // NOI18N
        if (c.equals("VTODO") && "X-NETBEANS-DEPENDENCY".equals(property) &&  // NOI18N
            name.equals("TYPE")) // NOI18N
            name = "X-NETBEANS-TYPE"; // NOI18N
        super.parameter(name, value);
    }

    @SuppressWarnings("unchecked")
    public void startComponent(String name) {
        if (name.equals("X"))
            name = "X-UNKNOWN";
        
        cmps.add(name);
        
        super.startComponent(name);
    }

    public void endComponent(String name) {
        super.endComponent(name);
        cmps.remove(cmps.size() - 1);
    }

    public void startProperty(String name) {
        if (name.indexOf(' ') >= 0) {
            name = "X-" + name.replace(' ', '-');
        }
        property = name;
        super.startProperty(name);
    }

    public void endProperty(String name) {
        super.endProperty(name);
        property = null;
    }
}
