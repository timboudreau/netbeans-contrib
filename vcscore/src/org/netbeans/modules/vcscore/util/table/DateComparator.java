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

package org.netbeans.modules.vcscore.util.table;

/**
 * a date comparator that's used in annotate command component for sorting the date column..
 * the format of the date is DD-MMM-YY
 * @author  mkleint
 */
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.DateFormatSymbols;

public class DateComparator implements TableInfoComparator {

    DateFormatSymbols symbols;
    DateFormat format;
    /** Creates new RevisionComparator */
    public DateComparator() {
        symbols = new DateFormatSymbols();
        String[] shorts = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", //NOI18N
                                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}; //NOI18N
        symbols.setShortMonths(shorts);
        format = new SimpleDateFormat("dd-MMM-yy", symbols); //NOI18N
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String str1 = obj.toString().trim();
        String str2 = obj1.toString().trim();
        ParsePosition pos1 = new ParsePosition(0);
        Date date1 = format.parse(str1, pos1);
        ParsePosition pos2 = new ParsePosition(0);
        Date date2 = format.parse(str2, pos2);
        if (date1 != null && date2 != null) {
            return date1.compareTo(date2);
        } 
        return 0;
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj != null) {
            return obj.toString();
        }
        return ""; //NOI18N
    }
}
