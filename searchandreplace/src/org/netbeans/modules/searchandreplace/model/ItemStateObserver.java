/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.searchandreplace.model;

import java.io.File;

/**
 * Listener-like interface that can detect changes in item state.  Used to
 * update the table model.
 *
 * @author Tim Boudreau
 */
public interface ItemStateObserver {
    public void becameInvalid (File file, String reason);
    public void shouldReplaceChanged (Item item, boolean shouldReplace);
    public void fileShouldReplaceChanged (File file, boolean fileShouldReplace);
    public void replaced (Item item);
}
