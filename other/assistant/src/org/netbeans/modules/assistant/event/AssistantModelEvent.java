/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
