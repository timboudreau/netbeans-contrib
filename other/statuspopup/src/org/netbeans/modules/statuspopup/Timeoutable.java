/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * Clearable.java
 *
 * Created on September 23, 2000, 6:00 PM
 */

package org.netbeans.modules.statuspopup;

/** Classes implementing this interface can register themselves with
 * TimeoutRegistry, and have their doTimeout() methods called
 * at an interval they specify.
 * <P>
 * There is an explicit contract between Timeoutable and TimeoutRegistry
 * that, when initiating an action the results of which will be timed out
 * (such as showing a component that you want to be hidden on a timeout),
 * that at that time you will call TimeoutRegistry.confirmActive().
 * <P>
 * The reason for this is that, for performance and memory, the timer
 * thread expires if it is unused for a period of time.  ConfirmActive
 * restarts the thread if necessary.
 * <P>
 * To register a class with TimeoutRegistry, call the static 
 * TimeoutRegistry.registerObject (myTimeoutableInstance).  There is
 * an unregister operation as well - however, TimeoutRegistry stores
 * a weak reference to the object, so it is not imperative that you
 * unregister your object for it to be garbage collected.
 *<P>
 * This is not meant to be a general purpose timer architecture, but a 
 * lightweight, low overhead one for non-critical purposes.
 *
 * @author Tim Boudreau
 * @version 0.2
 */
interface Timeoutable {
/** Returns the current timeout value (in milliseconds) for this instance.
 * Following registration with TimeoutRegistry or a the last call to getTimeout,
 * doTimeout() will be called again after <code>timeout</code> milliseconds have
 * elapsed.
 * @return The timeout in milliseconds
 */    
    public long getTimeout();
/** Returns an arbitrary object.  If that object and the stored object from
 * the last call to <code>getTimerPollArg()</code> are equal, the timeout
 * continues; if they are non-equal, the countdown is reset
 * (to the new value returned by getTimeout()).  So the countdown is reset
 * whenever the argument returned changes.
 * <P>
 * If the return value is null
 * @return the object to be compared with the previous call's result
 */    
    public Object getTimerPollArg();
    
    
/** Called when the timeout has been reached, after the return value from
 * getTimerPollArg() has changed.
 */    
    public void doTimeout();
    
}

