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
    
    private static final long serialVersionUID = 2954900165229291293L;
    
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
            return ((org.openide.filesystems.FileObject) fileObject).getPath();
        }
        /*
        if (fileObject instanceof org.netbeans.modules.vcscore.versioning.VcsFileObject) {
            return ((org.netbeans.modules.vcscore.versioning.VcsFileObject) fileObject).getPackageName('/');
        }
         */
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
