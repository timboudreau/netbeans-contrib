/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 *//*
 * Logger.java
 *
 * Created on February 23, 2004, 8:59 PM
 */

package org.netbeans.modules.paintcatcher;

import java.awt.Component;
import java.util.EventObject;

/**
 *
 * @author  tim
 */
public interface Logger {
    public void log (String msg);
    public void log (String msg, Component c);
    public void log (String msg, EventObject eo);
    
}
