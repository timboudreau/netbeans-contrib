/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
    public void revisionsChanged(RevisionEvent ev);
}

