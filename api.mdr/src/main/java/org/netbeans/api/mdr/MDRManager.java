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

