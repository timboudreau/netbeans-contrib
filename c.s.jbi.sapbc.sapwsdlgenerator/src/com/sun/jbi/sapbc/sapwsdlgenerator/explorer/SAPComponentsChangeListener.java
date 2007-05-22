package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import java.util.EventListener;

/**
 * Listener interface for receiving SAP Components node events.
 */
public interface SAPComponentsChangeListener extends EventListener {
    void added(SAPComponentsChangeEvent evt);
    void removed(SAPComponentsChangeEvent evt);
    void changed(SAPComponentsChangeEvent evt);
}
