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

package util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import junit.framework.AssertionFailedError;
import org.openide.TopManager;

public class StatusBarTracer implements PropertyChangeListener {
    
    ArrayList array;
    
    public StatusBarTracer() {
        array = new ArrayList ();
    }
    
    public void start () {
        TopManager.getDefault().addPropertyChangeListener(this);
    }
    
    public void stop () {
        TopManager.getDefault().removePropertyChangeListener(this);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ("statusText".equals (evt.getPropertyName())) {
            synchronized (this) {
                array.add ((evt.getNewValue() != null) ? evt.getNewValue().toString () : "<NULL>");
            }
        }
    }
    
    public void clear () {
        synchronized (this) {
            array.clear();
        }
    }
    
    public String waitText (String text) {
        return waitText (60, text, true);
    }
    
    public String waitText (int iter, String text, boolean exact) {
        for (; iter > 0; iter --) {
            synchronized (this) {
                for (int a = 0; a < array.size (); a ++) {
                    String str = (String) array.get (a);
                    if ((exact  &&  str.equals (text))  ||  (!exact  &&  str.indexOf(text) >= 0))
                        return str;
                }
            }
            Helper.sleep (1000);
        }
        throw new AssertionFailedError ("TIMEOUT: Waiting for status bar text: Exact: " + exact + " Text: " + text);
    }

    public String removeText (String text) {
        return removeText (text, true);
    }
    
    public String removeText (String text, boolean exact) {
        return removeText (60, text, exact);
    }
    
    public String removeText (int iter, String text, boolean exact) {
        for (; iter > 0; iter --) {
            synchronized (this) {
                while (array.size () > 0) {
                    String str = (String) array.remove (0);
                    if ((exact  &&  str.equals (text))  ||  (!exact  &&  str.indexOf(text) >= 0))
                        return str;
                }
            }
            Helper.sleep (1000);
        }
        throw new AssertionFailedError ("TIMEOUT: Waiting for status bar text: Exact: " + exact + " Text: " + text);
    }
    
    public void finalize () {
        stop ();
    }
    
}
