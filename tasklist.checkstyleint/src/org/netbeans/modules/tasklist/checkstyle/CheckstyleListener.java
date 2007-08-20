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

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

/**
 *
 * @author S.Aubrecht
 */
class CheckstyleListener implements AuditListener {

    private static final String GROUP_NAME = "nb-tasklist-checkstyle"; //NOI18N
    
    private List<Task> tasks;
    private FileObject currentResource;
    
    public CheckstyleListener() {
    }
    
    public void setCurrentResource( FileObject fo ) {
        this.currentResource = fo;
    }
    
    public List<Task> getTasks() {
        return tasks;
    }

    public void auditStarted(AuditEvent e) {
        tasks = null;
    }

    public void auditFinished(AuditEvent e) {
    }

    public void fileStarted(AuditEvent e) {
    }

    public void fileFinished(AuditEvent e) {
    }

    public void addError(AuditEvent e) {
        if( null == tasks ) {
            tasks = new LinkedList<Task>();
        }
        tasks.add( Task.create(currentResource, GROUP_NAME, e.getMessage(), e.getLine() ) );
    }

    public void addException(AuditEvent ae, Throwable ex) {
        Logger.getLogger( CheckstyleListener.class.getName() ).log(Level.INFO, ae.getMessage(), ex );
    }

}
