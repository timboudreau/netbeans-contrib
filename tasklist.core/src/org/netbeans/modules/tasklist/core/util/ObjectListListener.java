/*
 * ObjectListListener.java
 *
 * Created on 14. Januar 2007, 11:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.core.util;

import java.util.EventListener;

/**
 * A listener for an ObjectList. Objects from this list could 
 * implement this interface. Such an itemy will not be informed about
 * all changes. It will be informed about changes associated
 * with it.
 * 
 * @author tl
 */
public interface ObjectListListener extends EventListener {
    /**
     * A change in the list occured.
     *
     * @param e an event
     */
    public void listChanged(ObjectListEvent e);
}


