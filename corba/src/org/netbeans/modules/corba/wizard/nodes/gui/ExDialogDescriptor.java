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

package org.netbeans.modules.corba.wizard.nodes.gui;

import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
/**
 *
 * @author  tzezula
 * @version
 */
public class ExDialogDescriptor extends DialogDescriptor implements PropertyChangeListener {

    private JButton okButton;
    private JButton cancelButton;

    public final static String OK = "OK"; // No I18N
    public final static String CANCEL = "CANCEL"; // No I18N
    
    private ResourceBundle bundle;

    /** Creates new ExDialogDescriptor */
    public ExDialogDescriptor(ExPanel panel, String title, boolean isModal, ActionListener listener) {
        super (panel,title,isModal,listener);
        this.bundle = NbBundle.getBundle (ExDialogDescriptor.class);
        this.okButton = new JButton (this.bundle.getString("CTL_Ok")); 
        this.okButton.setActionCommand (OK);  
        this.okButton.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_Ok"));
        this.cancelButton = new JButton (this.bundle.getString("CTL_Cancel"));
        this.cancelButton.setActionCommand (CANCEL); 
        this.cancelButton.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_Cancel"));
        this.setOptions ( new Object[] {okButton, cancelButton});
        this.setOptionsAlign (DialogDescriptor.BOTTOM_ALIGN);
        panel.addPropertyChangeListener (this);
    }
    
    public void enableOk () {
        okButton.setEnabled (true);
    }
    
    public void enableCancel () {
        cancelButton.setEnabled (true);
    }
    
    public void disableOk () {
        okButton.setEnabled (false);
    }
    
    public void disableCancel () {
        cancelButton.setEnabled (false);
    }
    
    public boolean isOkEnabled () {
        return okButton.isEnabled();
    }
    
    public boolean isCancelEnabled () {
        return cancelButton.isEnabled ();
    }

    public void propertyChange(final java.beans.PropertyChangeEvent event) {
        if ("Ok".equals(event.getPropertyName())){ // No I18N
            okButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
        else if ("Cancel".equals(event.getPropertyName())){  //No I18N
            cancelButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
    }
}