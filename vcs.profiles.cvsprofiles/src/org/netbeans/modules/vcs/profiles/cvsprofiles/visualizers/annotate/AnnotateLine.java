/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.annotate;

import java.text.*;
import java.util.*;

/**
 * @author  Thomas Singer
 */
public class AnnotateLine {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", //NOI18N
                                                                       Locale.US);

    private String author;
    private String revision;
    private Date date;
    private String dateString;
    private String content;
    private int lineNum;

    public AnnotateLine() {
    }

    /**
     * Returns the author of this line.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of this line.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the revision of this line.
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets the revision of this line.
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Returns the date of this line.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the date in original String-representation of this line.
     */
    public String getDateString() {
        return dateString;
    }

    /**
     * Sets the date of this line.
     */
    public void setDateString(String dateString) {
        this.dateString = dateString;
        try {
            this.date = DATE_FORMAT.parse(dateString);
        }
        catch (ParseException ex) {
            // print stacktrace, because it's a bug
            ex.printStackTrace();
        }
    }

    /**
     * Return the line's content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the line's content.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the line's number.
     */
    public int getLineNum() {
        return lineNum;
    }

    /**
     * Returns the line's number.
     */
    public Integer getLineNumInteger() {
        return new Integer(lineNum);
    }

    /**
     * Sets the line's number.
     */
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}