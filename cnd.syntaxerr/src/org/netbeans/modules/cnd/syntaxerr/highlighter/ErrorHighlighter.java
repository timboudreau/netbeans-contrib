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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.windows.TopComponent;

/**
 * 
 * @author Vladimir Kvashin
 */
public class ErrorHighlighter implements PropertyChangeListener {
    
    private static final ErrorHighlighter instance = new ErrorHighlighter();
    
    public static final ErrorHighlighter instance() {
        return instance;
    }
    
    public void startup() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
    
    public void shutdown() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        if (TopComponent.Registry.PROP_CURRENT_NODES.equals(evt.getPropertyName())) {
//            
//        }
        System.err.printf("ErrorHighlighter.propertyChange %s\n", evt.getPropertyName());
    }
    
    

}
