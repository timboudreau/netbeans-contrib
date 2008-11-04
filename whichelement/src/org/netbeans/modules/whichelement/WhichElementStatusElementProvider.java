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

package org.netbeans.modules.whichelement;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.awt.StatusLineElementProvider;

/**
 * This creates a read-only text field to display the information of element under the caret.
 * The text field is added to the status bar.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
@org.openide.util.lookup.ServiceProvider(service=org.openide.awt.StatusLineElementProvider.class)
public class WhichElementStatusElementProvider implements StatusLineElementProvider {
    
    private WhichElementPanel whichElementPanel;
    public WhichElementStatusElementProvider() {
        whichElementPanel = new WhichElementPanel();
    }
    
    public Component getStatusLineElement() {
        return whichElementPanel;
    }
    
    static class WhichElementPanel extends JPanel {
        private JLabel iconLabel;
        
        private JTextField whichElementTextField;
        
        WhichElementPanel() {
            super(new FlowLayout(FlowLayout.LEADING, 0,0));
            
            iconLabel = new JLabel(){
                Point tooltipLocation;
                
                // Consider the font's size to compute the location of the
                // tooltip
                public void addNotify() {
                    super.addNotify();
                    tooltipLocation = new Point(0, -2 * getFont().getSize());
                }
                
                public Point getToolTipLocation(MouseEvent event) {
                    return tooltipLocation;
                }
            };
            
            add(iconLabel, BorderLayout.WEST);
            
            // Create the text field
            whichElementTextField = new JTextField(40) {
                Point tooltipLocation;
                
                // Consider the font's size to compute the location of the
                // tooltip
                public void addNotify() {
                    super.addNotify();
                    tooltipLocation = new Point(0, -2 * getFont().getSize());
                }
                
                public Point getToolTipLocation(MouseEvent event) {
                    return tooltipLocation;
                }
            };
            
            // Set the text field to read-only
            whichElementTextField.setEditable(false);
            
            add(whichElementTextField, BorderLayout.CENTER);
        }
        
        void setIcon(Icon icon) {
            iconLabel.setIcon(icon);
        }
        
        void setIconToolTip(String text) {
            iconLabel.setToolTipText(text);
        }
        
        void setText(String text) {
            whichElementTextField.setText(text);
        }
        
        public void setToolTipText(String text) {
            whichElementTextField.setToolTipText(text);
        }
    }
}
