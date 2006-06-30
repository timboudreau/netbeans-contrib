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

package org.netbeans.a11y;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.text.DateFormat;


/**
 *  Logging class for the AccessibilityTester suite of classes.
 *  <p>
 *  This logger will try to create the file find in system property, a11ytest.log_file,
 *  and if this isn't found, will simply create "log.txt" in the current directory.
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.Com
 */
public class AccessibilityTestLogger{

    private static RandomAccessFile logFile = null;

    static{
        String filename = System.getProperty("a11ytest.log_file", "log.txt");
        
        if (filename != null){
            
            try{
                logFile = new RandomAccessFile(filename, "rw");
                logFile.seek(logFile.length());
            } catch(Exception e){
                System.out.println("Error creating log file");
                System.out.println(e);
            }
            
        }
    }
    
    /**
     *  Write a String to the end of the log file, with a time stamp.
     *
     *  @param the String to write
     */
    public static void append(String s){
        
        if (logFile != null){
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
            
            try{
                logFile.writeBytes(df.format(new java.util.Date()) + ": " + s + System.getProperty("line.separator", "\n"));
            } catch(IOException e){
                // Ignore?
            }
            
        }
    }
    
}