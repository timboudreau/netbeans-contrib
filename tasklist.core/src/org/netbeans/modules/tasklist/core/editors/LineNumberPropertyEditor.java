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

package org.netbeans.modules.tasklist.core.editors;

import java.beans.PropertyEditorSupport;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for line numbers.
 */
public final class LineNumberPropertyEditor extends PropertyEditorSupport {
    private static final JLabel LABEL = new JLabel();

    static {
        LABEL.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        if (box.width <= 3)
            return;
        
        gfx.translate(box.x, box.y);
        LABEL.setText(getAsText());
        LABEL.setSize(box.width - 3, box.height);
        LABEL.setForeground(gfx.getColor());
        LABEL.paint(gfx);
        gfx.translate(-box.x, -box.y);
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        try {
            if (text.trim().length() == 0)
                setValue(new Integer(0));
            else
                setValue(new Integer(Integer.parseInt(text)));
        } catch (NumberFormatException nfe) {
            String msg = NbBundle.getMessage(LineNumberPropertyEditor.class, 
                "IllegalLineNumber", new Object[] {text}); //NOI18N
            RuntimeException iae = new IllegalArgumentException(msg); //NOI18N
            ErrorManager.getDefault().annotate(iae, ErrorManager.USER, msg,
                msg, nfe, new java.util.Date());
            throw iae;
        }
    }
    
    public String getAsText() {
        Object v = getValue();
        String s;
        if (v instanceof Line) {
            s = String.valueOf(((Line) v).getLineNumber() + 1);
        } else if (v instanceof Integer) {
            int n = ((Integer) v).intValue();
            if (n <= 0)
                s = "";
            else
                s = String.valueOf(n);
        } else {
            s = "";
        }
        return s;
    }
}
