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

package org.netbeans.modules.portalpack.portlets.genericportlets.storyboard.widgets;

import java.util.Properties;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.eventing.EventObject;

/**
 *
 * @author Satyaranjan
 */
public class CustomPinWidget extends VMDPinWidget {
    private String key;
    private String eventName;
    private String nodeKey;
    private EventObject event;
    public CustomPinWidget(Scene scene)
    {
        super(scene);
        
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }
    
    public String getEventName()
    {
        return eventName;
    }
    
    public EventObject getEvent()
    {
        return event;
    }
    
    public void setEvent(EventObject event)
    {
        this.event = event;
    }
    
    public String getNodeKey()
    {
        return nodeKey;
    }
    public void setNodeKey(String key)
    {
        this.nodeKey = key;
    }
    
}
