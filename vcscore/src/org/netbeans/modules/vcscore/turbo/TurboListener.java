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

import java.util.EventListener;

/**
 * Turbo.setMeta callbacks contract. Should be implemented
 * by filesystems only. It must not wait to any threads.
 *
 * @author Petr Kuzel
 */
public interface TurboListener extends EventListener {

    /**
     * Called after {@link Turbo#setMeta} invocation to notify
     * external observers. It guaranteed that status information
     * is available for fast in-memory layer retrieval. It
     * also comes with the event.
     */
    void turboChanged(TurboEvent e);

}
