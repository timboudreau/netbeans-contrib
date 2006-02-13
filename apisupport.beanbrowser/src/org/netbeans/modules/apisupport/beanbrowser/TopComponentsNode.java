/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.beanbrowser;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 * Shows open windows.
 * @author Jesse Glick
 */
class TopComponentsNode extends AbstractNode {
    
    public TopComponentsNode(TopComponent.Registry r) {
        super(new TopComponentsChildren(r));
        setName("TopComponentsNode");
        setDisplayName("Open Windows");
        setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
    }
    
    private static final class TopComponentsChildren extends Children.Keys implements PropertyChangeListener {

        private final TopComponent.Registry r;
        
        TopComponentsChildren(TopComponent.Registry r) {
            this.r = r;
        }

        protected void addNotify() {
            super.addNotify();
            setKeys(r.getOpened());
            r.addPropertyChangeListener(this);
        }
        
        protected void removeNotify() {
            r.removePropertyChangeListener(this);
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }

        protected Node[] createNodes(Object key) {
            TopComponent c = (TopComponent) key;
            Node n;
            try {
                n = Wrapper.make(new RefinedBeanNode(c));
            } catch (IntrospectionException ex) {
                ex.printStackTrace();
                return null;
            }
            n.setName(c.getName());
            n.setDisplayName(c.getDisplayName());
            n.setShortDescription(c.toString());
            return new Node[] {n};
        }

        public void propertyChange(PropertyChangeEvent evt) {
            setKeys(r.getOpened());
        }

    }
    
}
