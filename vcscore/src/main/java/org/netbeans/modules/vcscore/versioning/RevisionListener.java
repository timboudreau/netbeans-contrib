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

package org.netbeans.modules.vcscore.versioning;

import javax.swing.event.ChangeListener;

/**
 *
 * @author  Martin Entlicher
 *
 * This is a listener for changes in revisions of a file object.
 */
public interface RevisionListener extends ChangeListener {

    //public static final int NUM_REVISIONS_CHANGED = 1;
    //public static final int ONE_REVISION_CHANGED = 2;

    /**
     * The number of revisions has changed.
     * @args fo the file object whose revisions changed.
     */
    //public void numRevisionsChanged(FileObject fo);
    
    /**
     * One revision has changed. (Tags of that revision may change for instance.)
     * @args fo the file object whose revisions changed.
     * @args revision the revision which has changed.
     */
    //public void revisionChanged(FileObject fo, String revision);

    /**
     * One or more revisions has changed.
     * @args whatChanged specifies what actually changed.
     * @args fo the file object whose revisions changed.
     * @args info contains some further informations describing what has changed.
     */
    //public void stateChanged(javax.swing.event.ChangeEvent ev);
}

