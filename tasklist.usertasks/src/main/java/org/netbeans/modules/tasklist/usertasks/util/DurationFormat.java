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

package org.netbeans.modules.tasklist.usertasks.util;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public enum Type {
        /**
         * Short format. For example "1w 2d 06:06"
         */
        SHORT, 
        
        /**
         * Long format. For example "1 week 2 days 5 hours 5 minutes"
         */
        LONG
    };

    private MessageFormat format;
    private Pattern parsePattern;
    private Type type;
    private StringBuilder sb = new StringBuilder(25);

    /**
     * Creates a new instance of EnDurationFormatter.
     *
     * @param type format type
     */
    public DurationFormat(Type type) {
        this.type = type;
        String s;
        if (type == Type.LONG) {
            s = NbBundle.getMessage(DurationFormat.class, 
                "DurationFormat"); // NOI18N
            parsePattern = Pattern.compile(
                    NbBundle.getMessage(DurationFormat.class, 
                    "DurationParseFormat")); // NOI18N;
        } else {
            s = NbBundle.getMessage(DurationFormat.class, 
                "DurationShortFormat"); // NOI18N
            String pp = NbBundle.getMessage(DurationFormat.class, 
                    "DurationShortParseFormat"); // NOI18N
            parsePattern = Pattern.compile(pp); // NOI18N;
        }

        if (s.trim().length() != 0)
            format = new MessageFormat(s);
    }
    
    /**
     * Parses duration.
     * The method may not use the entire text of the given string.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return parsed duration
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Duration parse(String source) throws ParseException {
        Matcher matcher = parsePattern.matcher(source);
        if (!matcher.matches())
            throw new ParseException(source, 0);
        if (matcher.groupCount() != 4) {
            // System.out.println("" + matcher.groupCount());
            throw new ParseException(source, 0);
        }
        try {
            //System.out.println(matcher.group(1) + " " + 
            //        matcher.group(2) + " " + 
            //        matcher.group(3) + " " + 
            //        matcher.group(4));
            String ws = matcher.group(1);
            String ds = matcher.group(2);
            String hs = matcher.group(3);
            String ms = matcher.group(4);
            int w = ws == null ? 0 : Integer.parseInt(ws);
            int d = ds == null ? 0 : Integer.parseInt(ds);
            int h = hs == null ? 0 : Integer.parseInt(hs);
            int m = ms == null ? 0 : Integer.parseInt(ms);
            // System.out.println(w + " " + d + " " + h + " " + m);
            return new Duration(w, d, h, m);
        } catch (NumberFormatException e) {
            throw new ParseException(source, 0); // NOI18N
        }
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
        
        sb.setLength(0);
        if (type == Type.LONG) {
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
            
            if (d.hours != 0 || d.minutes != 0) {
                sb.append(' ');
                if (d.hours < 10)
                    sb.append('0');
                sb.append(d.hours);
                sb.append(':');
                if (d.minutes < 10)
                    sb.append('0');
                sb.append(d.minutes);
            }
            
            if (sb.length() > 0 && sb.charAt(0) == ' ') 
                sb.delete(0, 1);
            return sb.toString();
        }
    }
}
