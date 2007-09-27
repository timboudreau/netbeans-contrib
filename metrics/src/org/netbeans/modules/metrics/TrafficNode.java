/*
 * TrafficNode.java
 *
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
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.nodes.*;
import org.openide.util.Utilities;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * TrafficNode: a FilterNode which adds a "traffic light" to
 * the lower-right portion of a node's icon.  A traffic light
 * may add a green, yellow or red circle, or nothing as
 * specified.
 */
public class TrafficNode extends FilterNode implements Node.Cookie {
    private Light light;
    private Node.Cookie handlerCookie;

    public TrafficNode(Node original, ClassMetrics cm) {
        super(original);
	addCookie(cm);
        light = cm.getWarningLight();
        cm.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ClassMetrics.LIGHT_PROP))
                    setLight((Light)evt.getNewValue());
            }
        });
    }

    public TrafficNode(Node original, MethodMetrics mm) {
        super(original);
	addCookie(mm);
        light = mm.getWarningLight();
        mm.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ClassMetrics.LIGHT_PROP))
                    setLight((Light)evt.getNewValue());
            }
        });
    }

    public void setLight(Light light) {
        if (this.light != light) {
            this.light = light;
            fireIconChange();
            fireOpenedIconChange();
        }
    }

    public Light getLight() {
        return light;
    }

    public Image getIcon (int type) {
        return addBadge(getOriginal().getIcon(type));
    }

    public Image getOpenedIcon (int type) {
        return addBadge(getOriginal().getOpenedIcon(type));
    }

    private Image addBadge(Image image) {
        if (light != Light.NONE) {
            Image badge = light.getBadge();
            if (badge != null)
                return Utilities.mergeImages(image, badge, 16, 8);
        }
        return image;
    }

    public static class Light {
        public final static Light NONE = new Light("none");
        public final static Light GREEN = new Light("green");
        public final static Light YELLOW = new Light("yellow");
        public final static Light RED = new Light("red");

        String name;
        Image badge;

        private Light(String name) {
            this.name = name;
            badge = null;
        }

        public Image getBadge() {
            if (badge == null) {
                String gif = "org/netbeans/modules/metrics/resources/" +
                    name + ".gif";
                badge = Utilities.loadImage(gif);
            }
            return badge;
        }

        public String toString() {
            return name;
        }

    }

    /* Delegates to original, unless it is a NodeHandler cookie.
    *
    * @param type the class to look for
    * @return instance of that class or null if this class of cookie
    *    is not supported
    */
    public Node.Cookie getCookie (Class type) {
	if (type == TrafficNode.class)
	    return this;
	else if (type == NodeHandler.class)
	    return handlerCookie;
        else
	    return super.getCookie (type); // delegate to original node
    }

    private void addCookie(NodeHandler metricsObject) {
	// The Node API doesn't support filters well...
	handlerCookie = metricsObject;
    }
}
