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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;

import java.util.EventObject;

/**
 * Event describing FileObject's FileProperties change.
 * It's fired only for <b>live</b> FileObjects. Operations that
 * set status of not-yet created FileObjects do not raise it.
 * Above comes from assumtion that nobody cares about
 * listening on status changes for non-FileObjects. Listening
 * is typically UI frontend requirement. The UI'll be most
 * propably holding (live) FileObjects.
 *
 * @author Petr Kuzel
 */
public final class TurboEvent extends EventObject {

    private final FileObject target;

    private final FileProperties fprops;

    TurboEvent(FileObject fileObject, FileProperties fprops) {
        super(Turbo.singleton());
        this.target = fileObject;
        this.fprops = fprops;
    }

    /** Gets target file object whose metadata have changed. */
    public FileObject getFileObject() {
        return target;
    }

    /** Gets actual FileProperties value.
     * It's <code>null</code> for unknown value. */
    public FileProperties getFileProperties() {
        return fprops;
    }
}
