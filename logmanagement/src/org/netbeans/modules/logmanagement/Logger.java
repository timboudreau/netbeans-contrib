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
/*
 * Logger.java
 *
 * Created on Sep 11, 2007, 11:50:55 AM
 *
 */

package org.netbeans.modules.logmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.LoggingMXBean;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Anuradha G
 */
public class Logger implements Comparable<Logger> {

    private final String name;
    private List<Logger> childerns = new ArrayList<Logger>();
    private LoggingMXBean loggingMXBean = LogManager.getLoggingMXBean();

    public Logger(String name) {
        this.name = name;
    }

    public List<Logger> getChilderns() {
        return childerns;
    }

    public String getLevel() {
        return extractLevel(name);
    }
    //get parent level
    private String extractLevel(String loggerName){
        String level=loggingMXBean.getLoggerLevel(loggerName);
        if(level.trim().length()==0){
          String  parent  =loggingMXBean.getParentLoggerName(loggerName);
           if(parent.trim().length()!=0){
               level=extractLevel(parent);
           }else{
               level=loggingMXBean.getLoggerLevel(parent);
           }
        }
        return level;
    }
    public void setLevel(String level) {
        try {
            loggingMXBean.setLoggerLevel(name, level);
        } catch (java.lang.IllegalArgumentException exception) {
            NotifyDescriptor d = new NotifyDescriptor.Message(exception.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    public String getName() {
        return name;
    }

    public Logger findLogger(String name) {
        Logger logger = null;
        for (Logger l : childerns) {
            if (l.getName().equals(name)) {
                return l;
            }
        }
        for (Logger l : childerns) {
            logger = l.findLogger(name);
            if (logger != null) {
                break;
            }
        }
        return logger;
    }

    public void addLogger(Logger logger) {
        childerns.add(logger);
    }

    public void removeLogger(Logger logger) {
        childerns.remove(logger);
    }

    @Override
    public String toString() {
        return name;
    }

    public int compareTo(Logger o) {
        return name.compareToIgnoreCase(o.name);
    }
}