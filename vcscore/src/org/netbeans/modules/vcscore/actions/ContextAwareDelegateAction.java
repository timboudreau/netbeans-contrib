/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import javax.swing.JMenuItem;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;
import org.openide.util.actions.SystemAction;

/**
 * A delegate action that is usually associated with a specific lookup and
 * extract the nodes it operates on from it. Otherwise it delegates to the
 * regular NodeAction. Copied from org.openide.util.actions.NodeAction.DelegareAction.
 *
 * @author  Martin Entlicher
 */
public class ContextAwareDelegateAction extends Object implements javax.swing.Action,
        org.openide.util.LookupListener, org.openide.util.actions.Presenter.Menu,
        org.openide.util.actions.Presenter.Popup {
    
    /** action to delegate to */
    private Delegatable delegate;
    /** lookup we are associated with (or null) */
    private Lookup.Result result;
    /** lookup to work with */
    private Lookup lookup;
    /** previous state of enabled */
    private boolean enabled = true;
    /** support for listeners */
    private java.beans.PropertyChangeSupport support = new java.beans.PropertyChangeSupport (this);
        
    /** Creates a new instance of ContextAwareDelegateAction */
    public ContextAwareDelegateAction(Delegatable delegate, Lookup actionContext) {
        this.delegate = delegate;

        this.lookup = actionContext;
        this.result = actionContext.lookup (new org.openide.util.Lookup.Template (
            Node.class
        ));
        this.result.addLookupListener ((LookupListener)org.openide.util.WeakListeners.create (
            LookupListener.class, this, this.result
        ));
        resultChanged (null);
    }
    
    /** Overrides superclass method, adds delegate description. */
    public String toString() {
        return super.toString() + "[delegate=" + delegate + "]"; // NOI18N
    }

    private static final Node[] EMPTY_NODE_ARRAY = new Node[0];

    /** Nodes are taken from the lookup if any.
     */
    public final synchronized Node[] nodes () {
        if (result != null) {
            return (Node[])result.allInstances().toArray(EMPTY_NODE_ARRAY);
        } else {
            return EMPTY_NODE_ARRAY;
        }
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        support.addPropertyChangeListener (listener);
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        support.removePropertyChangeListener (listener);
    }

    public void putValue(String key, Object o) {}

    public Object getValue(String key) {
        return delegate.getValue(key);
    }

    public boolean isEnabled() {
        return delegate.enable(nodes ());
    }

    public void setEnabled(boolean b) {
    }

    public void resultChanged(org.openide.util.LookupEvent ev) {
        boolean newEnabled = delegate.enable(nodes ());
        if (newEnabled != enabled) {
            support.firePropertyChange (SystemAction.PROP_ENABLED, enabled, newEnabled);
            enabled = newEnabled;
        }
    }

    public javax.swing.JMenuItem getMenuPresenter() {
        return delegate.getPresenter (true, lookup);
    }

    public javax.swing.JMenuItem getPopupPresenter() {
        return delegate.getPresenter (false, lookup);
    }
    
    /**
     * This interface should implement the action that needs to be delegated.
     */
    public static interface Delegatable extends javax.swing.Action {
        
        boolean enable(Node[] nodes);
        
        JMenuItem getPresenter(boolean inMenu, Lookup lookup);
    }
    
}
