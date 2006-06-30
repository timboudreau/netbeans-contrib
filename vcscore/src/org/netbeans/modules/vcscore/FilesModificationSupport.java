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

package org.netbeans.modules.vcscore;

import javax.swing.event.ChangeListener;

/**
 * By implementing this interface the class declares that it can notify
 * clients about changes in files/directories.
 * It allows to attach a ChangeListener, that is called whenever a file
 * or directory content is changed.
 * The source in the provided ChangeEvent is the <code>File</code> that changed
 * (file or directory).
 *
 * @author Martin Entlicher
 */
public interface FilesModificationSupport {

    void addFilesStructureModificationListener(ChangeListener chl);
    
    void removeFilesStructureModificationListener(ChangeListener chl);
    
}
