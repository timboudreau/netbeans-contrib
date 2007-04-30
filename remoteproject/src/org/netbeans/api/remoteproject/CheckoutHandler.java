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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
 */
/*
 * CheckoutHandler.java
 *
 * Created on March 1, 2007, 9:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.remoteproject;

import java.io.File;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;

/**
 * Interface that allows a FileObject to specify, through its
 * attributes, something to check out from a version control
 * system.
 *
 * @author Tim Boudreau
 */
public interface CheckoutHandler {
    /**
     * Determine if this CheckoutHandler knows how to do a
     * version control checkout based on the attributes of the
     * passed FileObject.
     * @param template A FileObject with attributes that
     *        identify it as describing an instance of a
     *        version control system somewhere
     * @return true if this checkout() can potentially 
     *        successfully be called for this FileObject
     */ 
    public boolean canCheckout (FileObject template);
    /**
     * Perform a checkout into the destination directory,
     * based on parameters specified as attributes of the
     * template FileObject
     * @param template a template
     * @param dest where the checked out contents should
     *        appear on the user's disk.  This parameter
     *        needs to be resolvable to a java.io.File
     * @param progress A progress handle to show progress
     * @param username Optional user id
     * @return null if the checkout is successful, a localized
     *        problem string if it has failed
     */ 
    public String checkout (FileObject template, FileObject dest, 
            ProgressHandle progress, String username);
    
    public String getUserName (FileObject template);
    
    public File[] getCreatedDirs(FileObject template, File destFolder);
}
