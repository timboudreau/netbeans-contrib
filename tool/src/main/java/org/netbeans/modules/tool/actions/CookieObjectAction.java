/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tool.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 * A replacement for the CookieAction. It allows to have cookies directly
 * in the lookup of the top components.
 * @author David Strupl
 */
public abstract class CookieObjectAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private Lookup.Result cookieResult;
    private Lookup.Result nodeResult;
    private Lookup context;
    private Class cookieClass;
    
    /** Creates a new instance of CookieObjectAction */
    public CookieObjectAction(Class cookie, Lookup context) {
        this.cookieClass = cookie;
        this.context = context;
        cookieResult = context.lookup(new Lookup.Template(cookieClass));
        cookieResult.addLookupListener(this);
        nodeResult = context.lookup(new Lookup.Template(Node.class));
        nodeResult.addLookupListener(this);
    }
    
    /**
     * Find all the cookie instances in the context lookup and
     * in the nodes lookups and perform the <code>handleCookie</code>
     * method on each of the found instances.
     */
    public final void actionPerformed(ActionEvent event) {
        // first for nodes
        Collection nodes = nodeResult.allInstances();
        for (Iterator it = nodes.iterator(); it.hasNext(); ) {
            Node node = (Node)it.next();
            Object cookie = node.getLookup().lookup(cookieClass);
            if (cookie != null) {
                handleCookie(cookie);
            }
        }
        // now for the cookies directly:
        Collection cookies = cookieResult.allInstances();
        for (Iterator it = cookies.iterator(); it.hasNext(); ) {
            Object cookie = it.next();
            if (cookie != null) {
                handleCookie(cookie);
            }
        }
    }
    
    /**
     * This method should be implemented by subclasses to perform
     * the cookie specific action. This method is called from the
     * actions's actionPerformed method.
     */
    protected abstract void handleCookie(Object cookie);
    
    /**
     * The action is enabled when the cookie is present in the activated
     * node or directly in the lookup of the activated top component.
     */
    public boolean isEnabled() {
        // first for nodes
        Collection nodes = nodeResult.allInstances();
        for (Iterator it = nodes.iterator(); it.hasNext(); ) {
            Node node = (Node)it.next();
            Object cookie = node.getLookup().lookup(cookieClass);
            if (cookie != null) {
                return true;
            }
        }
        // now for the cookies directly:
        Collection cookies = cookieResult.allInstances();
        for (Iterator it = cookies.iterator(); it.hasNext(); ) {
            Object cookie = it.next();
            if (cookie != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Re-fire the change event to test the enablement state.
     */
    public void resultChanged(LookupEvent ev) {
        firePropertyChange("enabled", null, null);
    }
}
