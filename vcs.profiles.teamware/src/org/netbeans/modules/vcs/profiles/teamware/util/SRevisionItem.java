/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
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
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;

public class SRevisionItem extends NumDotRevisionItem {
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
            DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            return df.parse(getDate() + " "  + getTime()).getTime();
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