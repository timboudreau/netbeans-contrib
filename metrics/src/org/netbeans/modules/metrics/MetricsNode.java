/*
 * MetricsNode.java
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

import java.util.ResourceBundle;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import java.beans.*;
import javax.swing.Action;

/**
 * The base class for specific metric nodes.
 *
 * @author  Tom Ball
 */
class MetricsNode extends AbstractNode implements Cloneable {

    /** ResourceBundle used in this class. */
    static ResourceBundle bundle = 
        NbBundle.getBundle (MetricsNode.class);

    // Associated class
    private ClassMetrics classMetrics;
    
    /** Creates new MetricsNode */
    public MetricsNode(ClassMetrics classMetrics) {
        super (Children.LEAF);
 
        setName(MetricsNode.getString( "LBL_Metrics" ) );
        setShortDescription(MetricsNode.getString( "HINT_Metrics" ) );
        setIconBaseWithExtension("org/netbeans/modules/metrics/resources/barchart.gif");
        this.classMetrics = classMetrics;
	setPropertyListener();
    }

    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }

    private void setPropertyListener() {
        classMetrics.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                resetMetrics();
            }
        });
    }

    /** Update the metrics for this node. */
    private void resetMetrics() {
        Sheet s = getSheet();
        Sheet.Set props = s.get(Sheet.PROPERTIES);

        Metric[] metrics = classMetrics.getMetrics();
        for (int i = 0, n = metrics.length; i < n; i++) {
            Metric m = metrics[i];
            if (!m.needsOtherClasses()) {
                MetricProp mp = (MetricProp)props.get(m.getName());
                mp.setValue(m.getMetricValue());
            }
        }

        firePropertySetsChange(null, null);
    }
    
    /**
     * Returns a Sheet used to change this Property.
     * @return the property sheet
     */
    protected Sheet createSheet () {
        Sheet s = super.createSheet();

        Sheet.Set props = s.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            s.put (props);
        }

        Metric[] metrics = classMetrics.getMetrics();
        for (int i = 0, n = metrics.length; i < n; i++) {
            Metric m = metrics[i];
            if (!m.needsOtherClasses()) {
                MetricProp mp = new MetricProp(m.getName(), 
                                               m.getShortDescription(),
                                               m.getDetailsViewer());
                mp.setValue(m.getMetricValue());
                props.put (mp);
            }
        }

        return s;
    }

    private static class MetricProp extends PropertySupport.ReadOnly {
        private Object value;
        private PropertyEditor viewer;

        public MetricProp (String name, String desc, PropertyEditor viewer) {
            super (name, String.class, name, desc);
            this.viewer = viewer;
        }

        public Object getValue () {
            return value;
        }

        public void setValue (Object nue) {
            value = nue;
            viewer.setValue(nue);
        }
        
        public PropertyEditor getPropertyEditor() {
            return viewer;
        }
    }
    
   /**
     * Returns an Array of Actions allowed by this Node.
     * @return a list of standard actions
     */
    public Action[] getActions (boolean context) {
	if (context)
	    return super.getActions(context);
        if (systemActions == null) {
            systemActions = new SystemAction[] {
               SystemAction.get (ToolsAction.class),
               SystemAction.get (PropertiesAction.class),
	    };
        }
        return systemActions;
    }
    private SystemAction[] systemActions = null;

    public Object clone() throws CloneNotSupportedException {
	MetricsNode node = (MetricsNode)super.clone();
	node.setPropertyListener();
        return node;
    }
  
    public boolean canCopy () {
        return true;
    }
    
    public boolean canCut () {
        return false;
    }
    
    public boolean canRename () {
        return false;
    }
    
    public boolean canDestroy () {
        return false;
    }

    static String getString(String key) {
        return bundle.getString(key);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(MetricsNode.class);
    }
}
