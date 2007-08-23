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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;

/**
 *
 * @author S. Aubrecht
 */
public class DelegatingLogger implements Log {

    private Logger logger;
    
    public DelegatingLogger() {
        logger = Logger.getLogger( DelegatingLogger.class.getName()  );
    }
    
    public DelegatingLogger( String name ) {
        logger = Logger.getLogger( DelegatingLogger.class.getName()  );
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable( Level.FINE );
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable( Level.SEVERE );
    }

    public boolean isFatalEnabled() {
        return logger.isLoggable( Level.SEVERE );
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable( Level.INFO );
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable( Level.FINE );
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable( Level.WARNING );
    }

    public void trace(Object msg) {
        logger.log( Level.FINE, msg.toString() );
    }

    public void trace(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void debug(Object arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void debug(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void info(Object arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void info(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void warn(Object arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void warn(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void error(Object arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void error(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fatal(Object arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fatal(Object arg0, Throwable arg1) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}
