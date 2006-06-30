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
package org.netbeans.api.mdr;

import org.openide.util.Lookup;

/** Entry point to the metadata repositories.
 * Use {@link #getDefault} method to obtain the default instance.
 * @author Martin Matula
 * @version 0.1
 */
public abstract class MDRManager {
    /** Returns the default metadata repository.
     * @return default metadata repository or <code>null</code> if there is no default metadata repository.
     */
    public abstract MDRepository getDefaultRepository();

    /** Returns metadata repository of a given name.
     * @param name name of metadata repository
     * @return metadata repository of a given name or <CODE>null</CODE> if the repository of the given name does not exist.
     */
    public abstract MDRepository getRepository(String name);

    /** Returns list of names of all available metadata repositories.
     * @return list of names of all available metadata repositories
     */
    public abstract String[] getRepositoryNames();
    
    /** Should be called at the end of the MDR session. Does all the needed clean-up
     * by calling {@link MDRepository#shutdown} method on each of the repositories.
     */
    public void shutdownAll() {
        String names[] = getRepositoryNames();
        for (int i = 0; i < names.length; i++) {
            getRepository(names[i]).shutdown();
        }
    }

    /** Returns the default instance of MDRManager.
     * @return Default MDR manager.
     */    
    public static MDRManager getDefault() {
        return (MDRManager) Lookup.getDefault().lookup(MDRManager.class);
    }
}

