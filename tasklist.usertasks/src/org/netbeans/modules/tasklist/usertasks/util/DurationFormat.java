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

package org.netbeans.modules.tasklist.usertasks.util;

import java.text.MessageFormat;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.openide.util.NbBundle;

/**
 * Formats durations.
 *
 * @author tl
 */
public class DurationFormat {
    /**
     * format type
     * long: 1 week 2 days 5 hours 5 minutes
     * short: 1w 2d 05:05
     */
    public enum Type {SHORT, LONG};

    private MessageFormat format;
    private Type type;

    /**
     * Creates a new instance of EnDurationFormatter.
     *
     * @param type format type
     */
    public DurationFormat(Type type) {
        this.type = type;
        String s;
        if (type == Type.LONG)
            s = NbBundle.getMessage(DurationFormat.class, 
                "DurationFormat"); // NOI18N
        else
            s = NbBundle.getMessage(DurationFormat.class, 
                "DurationShortFormat"); // NOI18N
        if (s.length() != 0)
            format = new MessageFormat(s);
    }
    
    /**
     * Formats a duration.
     *
     * @param d the duration value
     * @return string representation
     */
    public String format(Duration d) {
        if (format != null)
            return format.format(new Object[] {
                    new Integer(d.weeks),
                    new Integer(d.days), 
                    new Integer(d.hours), new Integer(d.minutes)
                    }).trim();
        
        if (type == Type.LONG) {
            StringBuilder sb = new StringBuilder(25);
            switch (d.weeks) {
                case 0:
                    break;
                case 1: 
                    sb.append("1 week");
                    break;
                default: 
                    sb.append(d.weeks).append(" weeks");
                    break;
            } 
            switch (d.days) {
                case 0:
                    break;
                case 1:
                    sb.append(" 1 day");
                    break;
                default:
                    sb.append(' ').append(d.days).append(" days");
                    break;
            }
            switch (d.hours) {
                case 0:
                    break;
                case 1:
                    sb.append(" 1 hour");
                    break;
                default:
                    sb.append(' ').append(d.hours).append(" hours");
                    break;
            }
            switch (d.minutes) {
                case 0:
                    break;
                case 1:
                    sb.append(" 1 minute");
                    break;
                default:
                    sb.append(' ').append(d.minutes).append(" minutes");
                    break;
            }
            if (sb.length() > 0 && sb.charAt(0) == ' ') 
                sb.delete(0, 1);
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            switch (d.weeks) {
                case 0:
                    break;
                default: 
                    sb.append(d.weeks).append("w");
                    break;
            } 
            switch (d.days) {
                case 0:
                    break;
                default:
                    sb.append(' ').append(d.days).append("d");
                    break;
            }
            sb.append(' ');
            if (d.hours < 10)
                sb.append('0');
            sb.append(d.hours);
            sb.append(':');
            if (d.minutes < 10)
                sb.append('0');
            sb.append(d.minutes);
            if (sb.length() > 0 && sb.charAt(0) == ' ') 
                sb.delete(0, 1);
            return sb.toString();
        }
    }
}
