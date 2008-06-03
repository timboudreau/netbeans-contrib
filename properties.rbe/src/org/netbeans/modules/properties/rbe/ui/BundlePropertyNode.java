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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.ui;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * The Bundle property node
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class BundlePropertyNode extends AbstractNode implements Comparable<BundlePropertyNode> {

    private BundleProperty property;
    private RBE rbe;

    public BundlePropertyNode(BundleProperty property, RBE rbe) {
        super((property.getChildrenProperties().size() == 0) || (rbe.getMode() == RBE.DisplayMode.FLAT)
            ? Children.LEAF : new ChildrenProperties(property, rbe), Lookups.singleton(property));
        this.property = property;
        this.rbe = rbe;
    }

    public BundleProperty getProperty() {
        return property;
    }

    public void setProperty(BundleProperty property) {
        this.property = property;
    }

    @Override
    public String getName() {
        return property.getFullname();
    }

    @Override
    public String getDisplayName() {
        return rbe.getMode() == RBE.DisplayMode.FLAT ? property.getFullname() : property.getName();
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesKey.gif");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesKey.gif");
    }

    public int compareTo(BundlePropertyNode o) {
        return property.compareTo(o.property);
    }

    private static class ChildrenProperties extends Children.Keys<BundleProperty> implements PropertyChangeListener {

        private BundleProperty bundleProperty;
        private RBE rbe;

        public ChildrenProperties(BundleProperty bundleProperty, RBE rbe) {
            this.bundleProperty = bundleProperty;
            this.rbe = rbe;
            bundleProperty.addPropertyChangeListener(this);
        }

        @Override
        protected void addNotify() {
            setKeys(bundleProperty.getChildrenProperties());
        }

        @Override
        protected Node[] createNodes(BundleProperty childProperty) {
            return new Node[]{new BundlePropertyNode(childProperty, rbe)};
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (BundleProperty.PROPERTY_CHILDREN.equals(evt.getPropertyName())) {
                addNotify();
            }
        }
    }
}
