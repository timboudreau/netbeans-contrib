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

package org.netbeans.modules.assistant.event;

import java.net.URL;
import org.netbeans.modules.assistant.*;

/**
 * Notifies interested parties that a change in a
 * Assistant Model source has occurred.
 *
 * @author Richard Gregor
 */

public class AssistantModelEvent extends java.util.EventObject {
    private AssistantID id;
    private URL url;

    /**
     * Represents a change in the Assistant in the current ID or URL
     * @see org.netbeans.modules.Assistant
     *
     * @param source The source for this event.
     * @param id The ID that has changed. Should be null if URL is specified.
     * @param url The URL that has changed. Should be null if ID is specified.
     * @throws IllegalArgumentException if source is null.
     * @throws IllegalArgumentException of both ID and URL are null.
     */
    public AssistantModelEvent(Object source, AssistantID id, URL url) {
	super(source);
        this.id = id;
        this.url = url;
    }
    
    /**
     * Returns the current ID in the HelpModel.
     * @return The current ID.
     */
    public AssistantID getID() {
	return id;
    }

    /**
     * Returns the current URL in the HelpModel.
     * @return The current URL.
     */
    public URL getURL() {
	return url;
    }    
    
}
