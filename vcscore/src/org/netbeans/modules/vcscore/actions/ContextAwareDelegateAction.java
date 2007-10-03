/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
