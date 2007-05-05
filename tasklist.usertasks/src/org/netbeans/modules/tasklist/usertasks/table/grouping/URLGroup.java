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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.usertasks.table.grouping;

import org.openide.util.NbBundle;

/**
 * Group for the URLs.
 *
 * @author tl
 */
public class URLGroup extends Group {
    /** No URL is associated. */
    public static final URLGroup UNDEFINED = new URLGroup(null);
    
    private String host;

    /**
     * Constructor.
     * 
     * @param host host name or null for "no URL is associated"
     */
    public URLGroup(String host) {
        this.host = host;         
    }

    public String getDisplayName() {
        if (host == null)
            return NbBundle.getMessage(DoneGroup.class, "URLUndefined"); // NOI18N
        else
            return host;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final URLGroup other = (URLGroup) obj;
        if (this.host != other.host && (this.host == null || 
                !this.host.equals(other.host))) {
            return false;
        }
        return true;
    }
}
