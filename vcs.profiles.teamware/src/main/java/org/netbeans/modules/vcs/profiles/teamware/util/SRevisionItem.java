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
 * The Original Software is the Teamware module.
 * The Initial Developer of the Original Software is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;

public class SRevisionItem extends NumDotRevisionItem {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    private int serialNumber;
    private int predecessor;
    private String time;
    private Set includedSerialNumbers = null;
    private Set excludedSerialNumbers = null;
    private Set ignoredSerialNumbers = null;
    
    public SRevisionItem(String revision) {
        super(revision);
    }
    
    public void setSerialNumber(int i) {
        this.serialNumber = i;
    }
    
    public void setPredecessor(int i) {
        this.predecessor = i;
    }
    
    public int getSerialNumber() {
        return serialNumber;
    }
    
    public int getPredecessor() {
        return predecessor;
    }
    
    public long getLongDate() {
        try {
            String dateString = getDate() + " " + getTime();
            int year = Integer.parseInt(dateString.substring(0, 2));
            if (year >= 70 && year <= 99) {
                dateString = "19" + dateString;
            } else if (year == 69) {
                // strange SCCS behavior emulated here
                dateString = "1970" + dateString.substring(2);
            } else {
                dateString = "20" + dateString;
            }
            long date = dateFormat.parse(dateString).getTime();
            if (date / 1000l > 0x7fffffffl) {
                dateString = "19" + dateString.substring(2);
                date = dateFormat.parse(dateString).getTime();
                date = ((date / 1000l) - 0x43E80000l) * 1000l;
            }
            return date;
        } catch (ParseException e) {
            return 0L;
        }
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getTime() {
        return time;
    }
    
    public void includeSerialNumber(String sn) {
        if (includedSerialNumbers == null) {
            includedSerialNumbers = new HashSet();
        }
        includedSerialNumbers.add(sn);
    }
    
    public void excludeSerialNumber(String sn) {
        if (excludedSerialNumbers == null) {
            excludedSerialNumbers = new HashSet();
        }
        excludedSerialNumbers.add(sn);
    }
    
    public void ignoreSerialNumber(String sn) {
        if (ignoredSerialNumbers == null) {
            ignoredSerialNumbers = new HashSet();
        }
        ignoredSerialNumbers.add(sn);
    }
    
    public Set includedSerialNumbers() {
        if (includedSerialNumbers == null) {
            return Collections.EMPTY_SET;
        } else {
            return includedSerialNumbers;
        }
    }

    public Set excludedSerialNumbers() {
        if (excludedSerialNumbers == null) {
            return Collections.EMPTY_SET;
        } else {
            return excludedSerialNumbers;
        }
    }

    public Set ignoredSerialNumbers() {
        if (ignoredSerialNumbers == null) {
            return Collections.EMPTY_SET;
        } else {
            return ignoredSerialNumbers;
        }
    }
    
    public String toString() {
        return getRevision();
    }
}