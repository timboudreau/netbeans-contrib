/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;

import java.util.EventObject;

/**
 * Event describing FileObject's FileProperties change
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

    /** Gets actual FileProperties value. */
    public FileProperties getFileProperties() {
        return fprops;
    }
}
