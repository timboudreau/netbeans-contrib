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

package org.netbeans.modules.jndi.gui;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;
import org.netbeans.modules.jndi.JndiRootNode;


/** This Class represents an Panel used for displaying warning
 *  when some providers are missing
 */
public class NotFoundPanel extends JPanel {


    /** Creates new NotFoundPanel
     *  @param String provider, the provider class
     */
    public NotFoundPanel(String provider) {
        String errMsg = JndiRootNode.getLocalizedString("EXC_ClassNotFound");
        String errTip = JndiRootNode.getLocalizedString ("TIP_Installation");
        JLabel label = new JLabel(errMsg);
        JTextArea comments = new JTextArea(errTip,2,66);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setEnabled(false);
        comments.setBackground(label.getBackground());
        javax.accessibility.AccessibleContext ac = this.getAccessibleContext();
        ac.setAccessibleName (ac.getAccessibleName()+errMsg+errTip);
        ac.setAccessibleDescription (JndiRootNode.getLocalizedString("AD_NotFoundPanel"));
        GridBagConstraints c;
        this.setLayout( new GridBagLayout());
        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets (8,8,4,8);
        c.weightx = 1.0;
        c.weighty = 0.0;
        ((GridBagLayout)this.getLayout()).setConstraints(label,c);
        this.add(label);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets (4,8,8,8);
        c.weightx = 1.0;
        c.weighty = 1.0;
        ((GridBagLayout)this.getLayout()).setConstraints(comments,c);
        this.add(comments);
    }
}