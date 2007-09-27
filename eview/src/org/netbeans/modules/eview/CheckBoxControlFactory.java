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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.eview;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * ControlFactory creating JCheckBoxes.
 * @author David Strupl
 */
public class CheckBoxControlFactory implements ControlFactory {

    /** Shared event instance. */
  private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(CheckBoxControlFactory.class, "state", null, null);
    
    /** String value that is put into the check box right after initialization */
    private boolean initValue;
    
    /**
     * Creates a new instance of CheckBoxControlFactory 
     */
    public CheckBoxControlFactory(FileObject f) {
        Object o1 = f.getAttribute("initValue");
        if (o1 instanceof Boolean) {
            initValue = ((Boolean)o1).booleanValue();
        }
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
            jcb.addActionListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JCheckBox result = new JCheckBox();
        result.setSelected(initValue);
        return result;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            return Boolean.valueOf(jcb.isSelected());
        }
        return null;
    }
    
    public String convertValueToString(JComponent c, Object value) {
        if (value instanceof Boolean) {
            if ( ((Boolean)value).booleanValue()) {
                return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_True");
            } else {
                return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_False");
            }
        }
        return NbBundle.getBundle(CheckBoxControlFactory.class).getString("LBL_Error");
    }
    
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            ControlListener controlListener = new ControlListener(l);
            jcb.removeActionListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JCheckBox) {
            JCheckBox jcb = (JCheckBox)c;
            jcb.setSelected(((Boolean)value).booleanValue());
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements ActionListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            pcl.propertyChange(pcEvent);
        }

        public boolean equals(Object anotherObject) {
            if ( ! ( anotherObject instanceof ControlListener ) ) {
                return false;
            }
            ControlListener theOtherOne = (ControlListener)anotherObject;
            return pcl.equals(theOtherOne.pcl);
        }
        public int hashCode() {
            return pcl.hashCode();
        }
    }
}
