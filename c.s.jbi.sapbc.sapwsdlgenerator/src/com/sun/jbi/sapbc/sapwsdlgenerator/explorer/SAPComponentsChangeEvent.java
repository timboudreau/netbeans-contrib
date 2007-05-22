package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.util.EventObject;

/**
 * An event that indicates a change has occured under the SAP Components node.
 */
public class SAPComponentsChangeEvent extends EventObject {
    
    public SAPComponentsChangeEvent(Object source, Object subject, EventType type) {
        super(source);
        this.subject = subject;
        this.type = type;
    }
    
    public Object getSubject() {
        return subject;
    }
    
    public EventType getType() {
        return type;
    }
    
    public static enum EventType {
        ADD_LIBRARY_EVENT, REMOVE_LIBRARY_EVENT, RENAME_LIBRARY_EVENT
    }
    
    private EventType type;
    private Object subject;
}
