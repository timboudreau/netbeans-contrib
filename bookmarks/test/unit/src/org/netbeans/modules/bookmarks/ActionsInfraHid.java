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
 */

package org.netbeans.modules.bookmarks;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.actions.ActionManager;
import org.openide.nodes.Node;
import java.util.Set;
import org.openide.util.actions.SystemAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.openide.windows.WindowManager;
import javax.swing.JFrame;
import java.awt.Frame;
import org.openide.windows.Workspace;
import org.openide.util.NotImplementedException;
import java.awt.Image;
import org.openide.util.Lookup;
import java.util.ArrayList;

/**
 * Copied (and changed a bit) from openide test. 
 * Utilities for actions tests.
 * @author Jesse Glick
 */
public abstract class ActionsInfraHid {
    
    private static InstanceContent ic;
    
    private ActionsInfraHid() {}
    
    public static final UsefulThings UT;
    static {
        String tm = System.getProperty("org.openide.TopManager");
        if (tm != null) throw new IllegalStateException("TopManager was initialized already: " + tm);
        String lookup = System.getProperty("org.openide.util.Lookup");
        if (lookup != null && !lookup.equals(UsefulLookup.class.getName())) throw new IllegalStateException("Already had a Lookup installed: " + lookup);
        System.setProperty("org.openide.util.Lookup", UsefulLookup.class.getName());
        UT = new UsefulThings();
        Lookup l = Lookup.getDefault();
        if (!(l instanceof UsefulLookup)) throw new IllegalStateException(Lookup.getDefault().toString());
        if (l.lookup(ActionManager.class) == null) throw new IllegalStateException("no ActionManager");
        if (l.lookup(TopComponent.Registry.class) == null) throw new IllegalStateException("no TC.R");
        //if (l.lookup(WindowManager.class) == null) throw new IllegalStateException("no WindowManager");
        //if (CallbackSystemAction.getRegistry() == null) throw new IllegalStateException("no TC.R again!");
        ic.add(new NavigationServiceImpl());
    }
    public static void main(String[] args) {
        System.err.println("ActionsInfraHid OK.");
    }
    
    /** Lookup which provides a TC.Registry and ActionManager.
     */
    public static final class UsefulLookup extends AbstractLookup {
        public UsefulLookup() {
            super(getContent());
        }
        private static AbstractLookup.Content getContent() {
            ic = new InstanceContent();
            ic.add(UT);
            ic.add(ActionsInfraHid.class.getClassLoader());
            return ic;
        }
    }
    
    /** An action manager and top component registry.
     */
    public static final class UsefulThings extends ActionManager implements TopComponent.Registry {
        
        // Registry:
        
        private TopComponent activated = null;
        
        public TopComponent getActivated() {
            return activated;
        }
        
        public void setActivated(TopComponent nue) {
            TopComponent old = activated;
            activated = nue;
            firePropertyChange(PROP_ACTIVATED, old, nue);
        }
        
        private Node[] activatedNodes = new Node[0];
        private Node[] currentNodes = null;
        
        public Node[] getActivatedNodes() {
            return activatedNodes;
        }
        
        public Node[] getCurrentNodes() {
            return currentNodes;
        }
        
        public void setCurrentNodes(Node[] nue) {
            if (nue != null) {
                Node[] old = activatedNodes;
                activatedNodes = nue;
                firePropertyChange(PROP_ACTIVATED_NODES, old, nue);
            }
            Node[] old = currentNodes;
            currentNodes = nue;
            firePropertyChange(PROP_CURRENT_NODES, old, nue);
        }
        
        private Set opened = null;
        
        public Set getOpened() {
            return opened;
        }
        
        public void setOpened(Set nue) {
            Set old = opened;
            opened = nue;
            firePropertyChange(PROP_OPENED, old, nue);
        }
        
        // ActionManager:
        
        private SystemAction[] contextActions = null;
        
        public SystemAction[] getContextActions() {
            return contextActions;
        }
        
        public void setContextActions(SystemAction[] nue) {
            SystemAction[] old = contextActions;
            contextActions = nue;
            firePropertyChange(PROP_CONTEXT_ACTIONS, old, nue);
        }
        
        public void invokeAction(Action a, ActionEvent ev) {
            a.actionPerformed(ev);
        }
        
    }
    
    /** Prop listener that will tell you if it gets a change.
     */
    public static final class WaitPCL implements PropertyChangeListener {
        /** whether a change has been received, and if so count */
        public int gotit = 0;
        /** optional property name to filter by (if null, accept any) */
        private final String prop;
        public WaitPCL(String p) {
            prop = p;
        }
        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (prop == null || prop.equals(evt.getPropertyName())) {
                gotit++;
                notifyAll();
            }
        }
        public boolean changed() {
            return changed(1500);
        }
        public synchronized boolean changed(int timeout) {
            if (gotit > 0) {
                return true;
            }
            try {
                wait(timeout);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            return gotit > 0;
        }
    }

    // Stolen from RequestProcessorTest.
    public static void doGC() {
        doGC(10);
    }
    public static void doGC(int count) {
        ArrayList l = new ArrayList(count);
        while (count-- > 0) {
            System.gc();
            System.runFinalization();
            l.add(new byte[1000]);
        }
    }

}
