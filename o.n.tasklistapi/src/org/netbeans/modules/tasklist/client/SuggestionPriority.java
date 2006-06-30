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

package org.netbeans.modules.tasklist.client;

import org.openide.util.NbBundle;

import java.util.ResourceBundle;

/**
 * This class represents an enumerated type for suggestion priorities.
 *
 * @author Tor Norbye
 */
final public class SuggestionPriority implements Comparable {

    private final int priority;

    /** Keys for the Bundle.properties */
    private static final String[] PRIORITIES_KEYS = {
        "PriorityHigh",  // NOI18N
        "PriorityMediumHigh", // NOI18N
        "PriorityMedium", // NOI18N
        "PriorityMediumLow", // NOI18N
        "PriorityLow" // NOI18N
    };

    /** Names for priorities */
    private static String[] PRIORITIES;

    static {
        PRIORITIES = new String[PRIORITIES_KEYS.length];
        ResourceBundle rb = NbBundle.getBundle(SuggestionPriority.class);
        for (int i = 0; i < PRIORITIES_KEYS.length; i++) {
            PRIORITIES[i] = rb.getString(PRIORITIES_KEYS[i]);
        }
    }

    private SuggestionPriority(final int priority) {
        this.priority = priority;
    }

    /** Highest priority */
    public static final SuggestionPriority HIGH =
        new SuggestionPriority(1); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM_HIGH =
        new SuggestionPriority(2); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM =
        new SuggestionPriority(3); // NOI18N

    /** Normal/default priority */
    public static final SuggestionPriority MEDIUM_LOW =
        new SuggestionPriority(4); // NOI18N

    /** Lowest priority */
    public static final SuggestionPriority LOW =
        new SuggestionPriority(5); // NOI18N

    /** Return a numeric value for the priority. Lower number means
     *  higher priority. Don't depend on the actual values; they may
     *  change without notice. 
     * @return Numeric value for the priority
     * XXX clients often use as index to getPriorityNames
     */
    public int intValue() {
        return priority;
    }
    
    /** Provides a useful string representation for a particular priority.
     * Not internationalized. Don't depend on this format or content. 
     * @return A string representation of the priority
     */
    public String toString() {
        switch (priority) {
        case 1: return "high priority"; // NOI18N
        case 2: return "medium-high priority"; // NOI18N
        case 3: return "normal priority"; // NOI18N
        case 4: return "medium-low priority"; // NOI18N
        case 5: return "low priority"; // NOI18N
        default: return "error"; // NOI18N
        }
    }

    public int compareTo(final Object o) {
        return ((SuggestionPriority)o).priority - priority;
    }

    /**
     * Returns localized names for priorities
     *
     * @return [0] - high, [1] - medium-high, ...
     */
    public static String[] getPriorityNames() {
        return PRIORITIES;
    }

    /**
     * Finds a priority.
     * @param n integer representation of a priority
     * @return priority
     */
    public static SuggestionPriority getPriority(final int n) {
        switch (n) {
            case 1:
                return HIGH;
            case 2:
                return MEDIUM_HIGH;
            case 3:
                return MEDIUM;
            case 4:
                return MEDIUM_LOW;
            case 5:
                return LOW;
            default:
                return MEDIUM;
        }
    }
}
