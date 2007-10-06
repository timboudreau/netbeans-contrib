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

// HACK - read the comment at the beginning of the Task class

// Also see core/src/org/netbeans/beaninfo/editors and
// openide/src/org/openide/explorer/propertysheet/editors

package org.netbeans.modules.tasklist.usertasks.editors;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import org.netbeans.modules.tasklist.usertasks.DateSelectionPanel;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

// bugfix# 9219 for attachEnv() method
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
 

/** 
 * A property editor for the Date class. 
 *
 * @author Tor Norbye 
 * @author Trond Norbye
 */
public class DateEditor extends PropertyEditorSupport 
implements ExPropertyEditor {
    private static SimpleDateFormat format = new SimpleDateFormat();
    
    // bugfix# 9219 added editable field and isEditable() "getter" to be used 
    // in StringCustomEditor    
    private boolean editable = true;   

    /**
     * gets information if the text in editor should be editable or not
     *
     * @return true = editable
     */
    public boolean isEditable(){
        return editable;
    }
                
    public void setAsText(String s) throws java.lang.IllegalArgumentException {
        if (s.trim().length() == 0) {
            setValue(null);
            return;
        }
        try {
            setValue(format.parse(s));
        } catch (ParseException e) {
            String msg = NbBundle.getMessage(DateEditor.class,
                "IllegalDateValue", new Object[] {s}); //NOI18N
            RuntimeException iae = new IllegalArgumentException(msg); 
            Exceptions.attachLocalizedMessage(iae, msg);
            throw iae;
        }
    }

    public String getAsText() {
        Object val = getValue();
        if (val instanceof Date) {
            return format.format((Date) val);
        } else if (val instanceof Long) {
            long v = ((Long) val).longValue();
            if (v == 0)
                return ""; // NOI18N
            else
                return format.format(new Date(v));
        } else {
            return ""; // NOI18N
        }
    }

    public boolean supportsCustomEditor () {
        return true;
    }

    public Component getCustomEditor() {
        Date d;
        if (getValue() instanceof Date) {
            d = (Date) getValue();
        } else if (getValue() instanceof Long) {
            d = new Date(((Long) getValue()).longValue());
        } else {
            d = new Date();
            setValue(d);
        }
	return new DateSelectionPanel(d);
    }

    public void attachEnv(PropertyEnv env) {        
        FeatureDescriptor desc = env.getFeatureDescriptor();
        if (desc instanceof Node.Property){
            Node.Property prop = (Node.Property)desc;
            editable = prop.canWrite();
        }
    }
}
