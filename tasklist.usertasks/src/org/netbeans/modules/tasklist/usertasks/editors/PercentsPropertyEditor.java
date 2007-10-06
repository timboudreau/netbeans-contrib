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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.beans.PropertyEditorSupport;

import javax.swing.JProgressBar;
import javax.swing.UIManager;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for percents field.
 */
public class PercentsPropertyEditor extends PropertyEditorSupport {
    private static String[] TAGS = {
        "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", // NOI18N
        "55", "60", "65", "70", "75", "80", "85", "90", "95", "100" // NOI18N
    };

    private static JProgressBar progressBar;
    
    static {
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBackground(UIManager.getColor("Table.background")); // NOI18N
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        int n = ((Integer) getValue()).intValue();
        progressBar.setValue(n);
        progressBar.setString(n + "%"); // NOI18N
        int height = box.height > 15 ? 15 : box.height;
        int width = box.width > 100 ? 100 : box.width;
        int y = (box.height - height) / 2;
        progressBar.setSize(width, height);
        
        gfx.translate(box.x, box.y + y);
        progressBar.paint(gfx);
        gfx.translate(-box.x, -box.y - y);
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        try {
            setValue(new Integer(text));
        } catch (NumberFormatException e) {
            IllegalArgumentException iae = 
                new java.lang.IllegalArgumentException(
                    NbBundle.getMessage(PercentsPropertyEditor.class, 
                    "NotANumber")); // NOI18N
            Exceptions.attachLocalizedMessage(iae, iae.getMessage());
            throw iae;
        }
    }
    
    public String[] getTags() {
        return TAGS;
    }    
}
