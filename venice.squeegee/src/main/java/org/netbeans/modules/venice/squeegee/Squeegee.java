/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

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

package org.netbeans.modules.venice.squeegee;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.modules.venice.model.Model;
import org.netbeans.modules.venice.model.ModelProvider;
import org.netbeans.modules.venice.sourcemodel.api.SrcModelProvider;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 * Squeegee is a temporary pseudo-model provider so we can write some 
 * visualization code before we really know what we're doing :-)
 *
 * @author Tim Boudreau
 */
public class Squeegee implements ModelProvider, PropertyChangeListener {
    SrcModelProvider prov = new SrcModelProvider();
    
    public Squeegee() {
    }
    
    PropertyChangeListener l = null;
    public void setListener (PropertyChangeListener l) {
	if (this.l != null && l != this.l) {
	    throw new IllegalStateException ("Already listening with " + this.l);
	}
	this.l = l;
	if (l == null) {
	    stopListening();
	} else {
	    startListening();
	    //Prime it with the current nodes
	    PropertyChangeEvent evt = new PropertyChangeEvent (
		    TopComponent.getRegistry(), 
		    TopComponent.Registry.PROP_ACTIVATED_NODES, null, null);
	    propertyChange(evt);
	}
    }

    public boolean accept(Object o) {
	Node n = (Node) o;
	Element el = (Element) n.getLookup().lookup(Element.class);
	return el != null && prov.accept(el);
    }

    public Model createModel(Object o) {
	Node n = (Node) o;
	Element el = (Element) n.getLookup().lookup(Element.class);
	return prov.createModel(el);
    }

    private Reference lastNode = null;
    
    private void change() {
	Node[] n = TopComponent.getRegistry().getActivatedNodes();
	if (n.length >= 1) {
	    setNode (n[0]);
	} else {
	    setNode (null);
	}
    }
    
    private void setNode (Node n) {
	Node old = lastNode != null ? (Node) lastNode.get() : null;
	if (old != null) {
	    if (old == n) {
		return;
	    }
	}
	if (n == null) {
	    lastNode = null;
	} else {
	    lastNode = new WeakReference (n);
	    if (l != null) {
		PropertyChangeEvent pce = new PropertyChangeEvent (this, "foo", old, n);
		l.propertyChange(pce);
	    }
	}
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
	if (TopComponent.Registry.PROP_ACTIVATED_NODES.equals(evt.getPropertyName())) {
	    change();
	}
    }

    private void stopListening() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    private void startListening() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
}
