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
 * TimeoutRegistry.java
 *
 * Created on September 23, 2000, 6:03 PM
 */

package org.netbeans.modules.statuspopup;

/** Allows a generic way of handling objects that &quot;time-out&quot; in a flexible
 * way, using a single timer thread.  Objects implementing the
 * <code>Timeoutable</code> interface will have their <code>doTimeout()</code>
 * method called on the interval specified by their <code>getTimeout()</code>
 * method.  On registration, and after each call to <code>doTimeout()</code>, the
 * <code>TimeoutRegistry</code> (default, singleton) instance calls <code>getTimerPollArg()</code>,
 * and stores the value retrieved.  When the timeout interval is reached,
 * <code>doTimeout()</code> is called conditionally, depending on whether the current
 * result of <code>getTimerPollArg()</code> equals the stored value from the
 * previous call.  If not, the timeout is repeated.<P> This functionality is used
 * both by the popup display of status changes (to hide the popup after a specified
 * period of time, unless the message has changed, in which case the interval is
 * repeated - in this case, the popup component that implements Timeoutable also
 * modifies its timeout interval based on a multiple of the number of characters
 * in the message being displayed, to give the user time to read it), and also
 * by the alternate tab UI provided in the workspace switcher module - but it is
 * a generally useful tool for anything needing to time-out in a flexible way.
 *
 *
 * @author Tim Boudreau
 * @version 0.2
 */
class TimeoutRegistry extends java.lang.Thread {
    private static TimeoutRegistry defaultInstance = null;
    private static java.util.ArrayList components = null;
    private static java.util.ArrayList addQueue = null;
    private static long loopMax = 100;  
    private static int MinimumTimeout = 1000;
    private boolean iterating = false;
    private boolean timerRunning = false;
    private boolean aborted = false;
    
    private class TimeoutableData extends Object {
        /**
         */        
        public long actionTime = 0;
        public java.lang.ref.WeakReference ref = null;
        public long timeout = 1000;
        public Object lastTimerPollArg = "";
        
        public Timeoutable getTarget() {
            Timeoutable result = null;
            if (ref != null) {
              result = (Timeoutable) ref.get();
            }
        return result;
        }
        
    }
    
    public class SingletonException extends Exception {}
    
    /** Creates a new TimeoutRegistry instance
     * @throws SingletonException If an attempt is made to create an instance when one already exists in the JVM.
     */    
    protected TimeoutRegistry() throws SingletonException {
        if (defaultInstance != null) {
            throw new SingletonException ();
        }
        setPriority (java.lang.Thread.MIN_PRIORITY);
        setName ("Timeout registry default instance");
        setDaemon (true);
        init ();
    }
    
    

    /** A little ugly, but this ensures that the timeout thread is running.  In order to
     * conserve resources, the actual thread that manages timeouts will be shut down if
     * it is not used for an extended period of time.
     */    
    public synchronized static void ensureActive () {
        if (!getDefault().isAlive()) {
           if (!getDefault().isActive()) getDefault().start();
        }
    }
    
    private void init () {
       if (components == null) {
           components = new java.util.ArrayList();
       }
       if (addQueue == null) {
           addQueue = new java.util.ArrayList();
       }
    }
    
    private static TimeoutRegistry getDefault() {
        if (defaultInstance == null) {
            try {
            defaultInstance = new TimeoutRegistry();
            } catch (SingletonException e) {
                System.out.println(e.toString());
            }
        }
        return defaultInstance;
    }
    
    private boolean isActive () {
        return timerRunning;
    }
    
    private final void doRegister (final Timeoutable c) {
        TimeoutableData entry = makeEntryFor (c);
        if (!iterating) {
            components.add (entry);
        } else {
            addQueue.add (entry);
        }
        if (!timerRunning) start();
    }
    
    /** Registers an object implementing Timeoutable with the default instance of
     * TimeoutRegistry.  On registration, <code>getTimeout()</code> is called on
     * the object being registered, and the result stored.
     * @param c The Timeoutable object being registered
     */    
    public synchronized static void registerComponent (final Timeoutable c) {
        getDefault().doRegister (c);
    }
    
    private TimeoutableData findInArray (final Object[] ca, final Timeoutable c) {
        //reimplement with a hashtable sometime
        TimeoutableData result = null;
        for (int i=0; i < ca.length; i++) {
            if (((TimeoutableData) ca[i]).getTarget() == c) {
                result = (TimeoutableData) ca[i];
                break;
            }
        }
        return result;
    }
    
    private TimeoutableData findEntryFor (final Timeoutable c) {
        TimeoutableData result = findInArray (components.toArray(), c);
        if (result == null) {
            result = findInArray (addQueue.toArray(), c);
        }
        return result;
    }
    
    /** Unregister a component previously registered with the TimeoutRegistry
     * @param c The Timeoutable object being unregistered.
     */    
    public static void unregisterComponent (final Timeoutable c) {
        getDefault().doUnregister (c);
    }
    
    private void doUnregister (final Timeoutable c) {
        //System.out.println("Component unregistered");
        TimeoutableData entry = findEntryFor (c);
        if (!iterating) {
            components.remove (entry);
            if (components.size() == 0) aborted = true;
        } else {
            entry.ref = null;  //if target is null, will be removed on next processComponents call
            if ((components.size() == 1) && (addQueue.size() == 0)) aborted = true;
        }
    }
    
    private synchronized void registerQueuedComponents () {
        components.addAll (addQueue);
        addQueue.clear();
        //System.out.println("Queued components registered");
    }
    
    private TimeoutableData makeEntryFor (final Timeoutable c) {
        //System.out.println("Creating entry for component");
        TimeoutableData result = new TimeoutableData ();
        result.actionTime = System.currentTimeMillis() + c.getTimeout();
        result.timeout = c.getTimeout();
        result.ref = new java.lang.ref.WeakReference (c);
        return result;
    }
    
    private final void resetTimeout (final TimeoutableData entry) {
        //System.out.println("Resetting timeout due to text change");
        Timeoutable target = entry.getTarget();
        if (target != null) {
        entry.lastTimerPollArg = target.getTimerPollArg();
        entry.actionTime = System.currentTimeMillis() + target.getTimeout();
//        System.out.println("New timeout is " + (new Long (target.getTimeout())).toString());
        }
    }
    
    private final void doTimeout  (final TimeoutableData entry) {
        //System.out.println("Timing out and clearing");
        Timeoutable target = entry.getTarget();
        if (target != null) {
          target.doTimeout();
          entry.lastTimerPollArg = "";  //should be unnecessary
        }
    }
    
    
    private boolean processComponent (final TimeoutableData entry) {
        //System.out.println("Processing one component");
        boolean result = false;
        Timeoutable target = entry.getTarget();
        boolean alreadyCleared = ((target == null) || (target.getTimerPollArg() == null));
        
        //if (alreadyCleared) System.out.println("  - Already cleared, nothing to do");
        if (!alreadyCleared) {
            boolean argsUpdated = !entry.lastTimerPollArg.equals (target.getTimerPollArg());
            if (argsUpdated) {
                resetTimeout (entry);
                result = true;
            } else {
                boolean isTimedOut = ((entry.timeout > 0) && (entry.actionTime < System.currentTimeMillis()));
                if (isTimedOut) {
                    doTimeout (entry);
                    result = true;
                }
            }
        }
        return result;
        //if (!isTimedOut) System.out.println("Not timed out yet.");
        
    }
    
    private boolean processComponents() {
        //System.out.println("******** MinimumTimeout:");
        boolean result = false;
        TimeoutableData entry;
        iterating = true;
        java.util.Iterator i = components.iterator();
        while (i.hasNext()) {
            entry = (TimeoutableData) i.next();
            if (entry.getTarget() != null) {
                result = result || processComponent (entry);
            } else {
                i.remove();  //anything null has had Remove called on it during last iteration
                //System.out.println("Removed component inside iteration loop because target was null");
            }
        }
        iterating = false;
        if (!addQueue.isEmpty()) registerQueuedComponents();
        return result;
    }
    
    /** Run the timeout thread
     */    
    public void run () {
        boolean anyProcessed;
        long loopCount = 0;
        //System.out.println("Starting timer thread");
        if (MinimumTimeout > 0) {
            try {
                timerRunning = true;
                while (!aborted) {
                    sleep (MinimumTimeout);
                    if (!components.isEmpty()) {
                        anyProcessed = processComponents();
                        if (anyProcessed) {
                            loopCount = 0;  //reset inactivity measure
                        } else {
                            loopCount++; //increment inactivity if none processed
//                            System.out.println((new Long(loopCount)).toString());
                        }
                    } else {
                        loopCount++;  //increment inactivity if nothing to process
//                        System.out.println((new Long(loopCount)).toString());
                    }
                    if ((loopCount > loopMax) && (loopMax != 0)) {
                        aborted = true;
//                        System.out.println("Aborting repaint thread due to inactivity");
                        timerRunning = false;
                    }
                }
                //System.out.println("Repaint timer aborted.");
                timerRunning = false;
                
            } catch (java.lang.InterruptedException e) {
                //System.out.println("Interrupted exception from timer");
            } finally {
                aborted = false;
                defaultInstance = null;
//                System.out.println("Cleared default instance");
                //System.out.println("Ending timer thread");
                timerRunning = false;
            }
        }
    }
    
    /** Get the minimum allowable timeout (i.e. how often the timeout thread should
     * wake up and see if it has any objects to time-out)
     * @return The minimum timeout, in milliseconds
     */    
    public static int getMinimumTimeout () {
        return MinimumTimeout;
    }
    
    /** Set the minimum allowable timeout (i.e. how often the timeout thread should
     * wake up and see if it has any objects to time-out)
     * @param value The minimum timeout, in milliseconds
     */    
    public static void setMinimumTimeout (int value) {
        MinimumTimeout = value;
    }
}



