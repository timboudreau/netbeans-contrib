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
        UTUtils.LOGGER.fine(c + " " + property + " " + name + " " + value); // NOI18N
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
