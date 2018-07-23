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

/*
 * LoggerManager.java
 *
 * Created on Sep 11, 2007, 11:58:45 AM
 */

package org.netbeans.modules.logmanagement;

import java.util.List;
import java.util.logging.LoggingMXBean;

/**
 *
 * @author Anuradha G
 */
 class LoggerManager {

    private LoggingMXBean loggingMXBean;
    private Logger root = new Logger("");// NOI18N
    private static LoggerManager instance;

    private LoggerManager() {
        this.loggingMXBean = MXConnecter.getInstance().getLoggingMXBean();
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
        root.getChildren().clear();
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
        printTree(root, "");// NOI18N
    }

    private void printTree(Logger root, String tab) {
        List<Logger> childerns = root.getChildren();
        for (Logger logger : childerns) {
            System.out.println(tab + logger.getName() + "  -+");// NOI18N
            printTree(logger, tab + "\t");// NOI18N
        }
    }
}