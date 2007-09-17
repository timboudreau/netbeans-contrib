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
 * LoggerManager.java
 *
 * Created on Sep 11, 2007, 11:58:45 AM
 */

package org.netbeans.modules.logmanagement;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.LoggingMXBean;

/**
 *
 * @author Anuradha G
 */
 class LoggerManager {

    private LoggingMXBean loggingMXBean;
    private Logger root = new Logger("");
    private static LoggerManager instance;

    private LoggerManager() {
        this.loggingMXBean = LogManager.getLoggingMXBean();
    }

    public static synchronized LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public Logger findLoggerByName(String name) {
        return root.findLogger(name);
    }

    public void refresh() {
        root.getChilderns().clear();
        List<String> loggerNames = loggingMXBean.getLoggerNames();
        for (String name : loggerNames) {
            addLogger(name);
        }
    }

    public Logger getRoot() {
        return root;
    }

    private Logger addLogger(String name) {
        if (name.trim().length() == 0) {
            return root;
        }
        Logger logger = findLoggerByName(name);
        if (logger != null) {
            //already added
            return logger;
        }
        String parent = loggingMXBean.getParentLoggerName(name);
        if (parent == null || parent.trim().length() == 0) {
            //add to root
            logger = new Logger(name);
            root.addLogger(logger);
        } else {
            Logger parentLogger = findLoggerByName(parent);
            if (parentLogger == null) {
                parentLogger = addLogger(parent);
            }
            logger = new Logger(name);
            parentLogger.addLogger(logger);
        }
        return logger;
    }

    public void printTree() {
        printTree(root, "");
    }

    private void printTree(Logger root, String tab) {
        List<Logger> childerns = root.getChilderns();
        for (Logger logger : childerns) {
            System.out.println(tab + logger.getName() + "  -+");
            printTree(logger, tab + "\t");
        }
    }
}