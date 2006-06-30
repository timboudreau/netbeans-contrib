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

