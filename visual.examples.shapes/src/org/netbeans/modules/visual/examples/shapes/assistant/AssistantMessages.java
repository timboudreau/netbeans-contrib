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
package org.netbeans.modules.visual.examples.shapes.assistant;

import java.util.*;

import org.openide.util.NbBundle;

/**
 * Repository of assistant messages.
 *
 * @author Jan Stola
 */
public class AssistantMessages {

    private static AssistantMessages defaultInstance;
    
    private boolean initialized = false;
    private Map<String,List<String>> contextToMessages;

    private AssistantMessages() {
    }

    public static AssistantMessages getDefault() {
        if (defaultInstance == null)
            defaultInstance = new AssistantMessages();
        return defaultInstance;
    }

    public List<String> getMessages(String context) {
        if (!initialized) {
            initialize();
        }
        return contextToMessages.get(context);
    }

    private void initialize() {
        contextToMessages = new HashMap<String,List<String>>();
        ResourceBundle bundle = NbBundle.getBundle(AssistantMessages.class);
        Enumeration enumeration = bundle.getKeys();
        while (enumeration.hasMoreElements()) {
            String bundleKey = (String)enumeration.nextElement();
            String context = getContext(bundleKey);
            List<String> messages = contextToMessages.get(context);
            if (messages == null) {
                messages = new ArrayList<String>();
                contextToMessages.put(context, messages);
            }
            messages.add(bundle.getString(bundleKey));
        }
    }

    private String getContext(String bundleKey) {
        int index = bundleKey.indexOf('_');
        if (index == -1) {
            return bundleKey;
        } else {
            return bundleKey.substring(0, index);
        }
    }

}
