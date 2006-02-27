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

import java.awt.Point;

/**
 * Interface used for displaying item text in the left pane of the window.
 * The text needs to be loaded asynchronously off the event thread, so
 * pass one of these to Search.getText (Item, TextReceiever), and once the
 * text has been loaded it will be displayed.
 *
 * @author Tim Boudreau
 */
public interface TextReceiver {
    public void setText(String txt, String mimeType, Point position);
}