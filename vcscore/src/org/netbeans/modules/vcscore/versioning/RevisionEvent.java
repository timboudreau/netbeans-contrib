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

import javax.swing.event.ChangeEvent;

/**
 * The event, that is fired, when the revisions are changed.
 * @author  Martin Entlicher
 */
public class RevisionEvent extends ChangeEvent {

    public static final int REVISION_NO_CHANGE = 0;
    public static final int REVISION_CHANGED = 1;
    public static final int REVISION_ADDED = 2;
    public static final int REVISION_REMOVED = 3;
    public static final int REVISION_ALL_CHANGED = 4;
    
    private String changedRevision = null;
    private int revisionChangeID = REVISION_NO_CHANGE;
    
    public RevisionEvent() {
        super(null);
    }
    
    /** Creates new RevisionEvent
     * @param fileObject the file object whose revisions has changed.
     *        It's an instance of FileObject or VcsFileObject.
     */
    public RevisionEvent(Object fileObject) {
        super(fileObject);
    }
    
    public Object getFileObject() {
        return getSource();
    }
    
    public String getFilePath() {
        Object fileObject = getSource();
        if (fileObject instanceof org.openide.filesystems.FileObject) {
            return ((org.openide.filesystems.FileObject) fileObject).getPackageNameExt('/', '.');
        }
        if (fileObject instanceof org.netbeans.modules.vcscore.versioning.VcsFileObject) {
            return ((org.netbeans.modules.vcscore.versioning.VcsFileObject) fileObject).getPackageName('/');
        }
        return "";
    }

    /** Get the changed/added/removed revision name.
     * @return the changed/added/removed revision name.
     */
    public String getChangedRevision() {
        return changedRevision;
    }
    
    /** Set the changed/added/removed revision name.
     * @param changedRevision the changed/added/removed revision name.
     */
    public void setChangedRevision(String changedRevision) {
        this.changedRevision = changedRevision;
    }
    
    /** Get the revision change id. One of REVISION_* constants is returned.
     * @return the revision change id.
     */
    public int getRevisionChangeID() {
        return revisionChangeID;
    }
    
    /** Set the revision change id. One of REVISION_* constants should be used.
     * @param revisionChangeID the revision change id.
     */
    public void setRevisionChangeID(int revisionChangeID) {
        this.revisionChangeID = revisionChangeID;
    }
    
}
