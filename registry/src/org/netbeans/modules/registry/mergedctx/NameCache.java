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

package org.netbeans.modules.registry.mergedctx;


import java.util.*;

abstract class NameCache {
    Map /*<String name, Integer priority>*/ content;

    protected NameCache() {
    }

    private boolean isInitialized() {
        return (content != null);
    }


    public String toString() {
        final StringBuffer sb = new StringBuffer(getClass().toString() + ": " + System.identityHashCode(this));
        if (isInitialized()) {
            for (Iterator iterator = content.entrySet().iterator(); iterator.hasNext();) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                final String s = (String) entry.getKey();
                sb.append("  " + s + "  " + entry.getValue());//NOI18N
            }
        }
        return sb.toString();
    }

    final void add(final int priority, final String name) {
        synchronized (NameCache.class) {
            if (!isInitialized()) content = new HashMap();

            final Integer foundPriority = (Integer) content.get(name);
            if (foundPriority == null || priority < foundPriority.intValue()) {
                content.put(name, new Integer(priority));
            }
        }
    }

    void clear() {
        synchronized (NameCache.class) {
            content = null;
        }
    }


    final Collection getNames() {
        return (isInitialized()) ? Collections.unmodifiableCollection(content.keySet()) : Collections.EMPTY_LIST;
    }
}
