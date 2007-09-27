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

import java.util.ResourceBundle;
import javax.swing.event.DocumentListener;
import javax.swing.event.ChangeListener;
import java.util.StringTokenizer;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.wizard.nodes.utils.IdlUtilities;
/**
 *
 * @author  Tomas Zezula
 * @version
 */
public class OperationPanel extends ExPanel implements DocumentListener, ChangeListener {

    ResourceBundle bundle = NbBundle.getBundle (OperationPanel.class);

    /** Creates new form OperationPanel */
    public OperationPanel() {
        initComponents ();
        postInitComponents ();
    }
  
    public String getName () {
        return this.name.getText().trim();
    }
    
    public void setName (String name) {
	this.name.setText (name);
    }
  
    public String getReturnType () {
        return this.ret.getText().trim();
    }
    
    public void setReturnType (String ret) {
	this.ret.setText (ret);
    }
  
    public String getParameters () {
        return this.params.getText().trim();
    }
    
    public void setParameters (String params) {
	this.params.setText (params);
    }
  
    public String getExceptions () {
        return this.except.getText().trim();
    }
    
    public void setExceptions (String except) {
	this.except.setText(except);
    }
  
    public String getContext () {
        return this.ctx.getText();
    }
    
    public void setContext (String context) {
	this.ctx.setText(context);
    }
  
    public boolean isOneway () {
        return this.oneway.isSelected ();
    }
    
    public void setOneway (boolean oneway) {
	this.oneway.setSelected (oneway);
    }

    private void postInitComponents () {
        this.name.getDocument().addDocumentListener (this);
        this.ret.getDocument().addDocumentListener(this);
        this.params.getDocument().addDocumentListener (this);
        this.ctx.getDocument().addDocumentListener (this);
        this.except.getDocument().addDocumentListener (this);
        this.oneway.addChangeListener (this);
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_ModuleName_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (this.bundle.getString("TXT_Return_MNE").charAt(0));
        this.jLabel3.setDisplayedMnemonic (this.bundle.getString("TXT_Params_MNE").charAt(0));
        this.jLabel4.setDisplayedMnemonic (this.bundle.getString("TXT_Except_MNE").charAt(0));
        this.jLabel5.setDisplayedMnemonic (this.bundle.getString("TXT_Ctx_MNE").charAt(0));
        this.oneway.setMnemonic (this.bundle.getString("TXT_OpMode_MNE").charAt(0));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_OperationPanel"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        ret = new javax.swing.JTextField();
        params = new javax.swing.JTextField();
        except = new javax.swing.JTextField();
        ctx = new javax.swing.JTextField();
        oneway = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(250, 160));
        jLabel1.setText(this.bundle.getString ("TXT_ModuleName"));
        jLabel1.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(this.bundle.getString ("TXT_Return"));
        jLabel2.setLabelFor(ret);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText(this.bundle.getString ("TXT_Params"));
        jLabel3.setLabelFor(params);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText(this.bundle.getString ("TXT_Except"));
        jLabel4.setLabelFor(except);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel4, gridBagConstraints);

        jLabel5.setText(this.bundle.getString("TXT_Ctx"));
        jLabel5.setLabelFor(ctx);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        add(jLabel5, gridBagConstraints);

        name.setToolTipText(this.bundle.getString("TIP_OperationName"));
        name.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(name, gridBagConstraints);

        ret.setToolTipText(this.bundle.getString("TIP_OperationRetType"));
        ret.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(ret, gridBagConstraints);

        params.setToolTipText(this.bundle.getString("TIP_OperationParams"));
        params.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(params, gridBagConstraints);

        except.setToolTipText(this.bundle.getString("TIP_OperationExceptions"));
        except.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(except, gridBagConstraints);

        ctx.setToolTipText(this.bundle.getString("TIP_OperationCtx"));
        ctx.setPreferredSize(new java.awt.Dimension(100, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        add(ctx, gridBagConstraints);

        oneway.setText(this.bundle.getString ("TXT_OpMode"));
        oneway.setToolTipText(this.bundle.getString ("TIP_OpMode"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        add(oneway, gridBagConstraints);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ctx;
    private javax.swing.JTextField ret;
    private javax.swing.JTextField except;
    private javax.swing.JTextField name;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JCheckBox oneway;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField params;
    // End of variables declaration//GEN-END:variables


    
    public void removeUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void changedUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }

    public void insertUpdate(final javax.swing.event.DocumentEvent p1) {
        checkState ();
    }
    
    private boolean acceptableArguments (String params) {
        if (params.length()==0)
            return true;
        if (params.endsWith(","))
            return false;
        StringTokenizer tk = new StringTokenizer (params,",");  // No I18N
        while (tk.hasMoreTokens()) {
            String param = tk.nextToken().trim();
            String modifier = "";
            String type = "";
            String name = "";
            int state = 0;
            int start = 0;
            for (int i=0; i< param.length();i++) {
                if (state == 7 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 8;  // Error
                }
                if (state == 8) {  // Error state
                    break;      // We found error 
                }
                if (state == 0 && (param.charAt(i)==' ' || param.charAt(i)=='\t')) {
                    modifier = param.substring(start,i);
                    state = 5;
                }
                if (state == 5 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 1;
                    start = i;
                }
                if (state == 1 && (param.charAt(i)==' ' || param.charAt(i)=='\t')) {
                    type = param.substring(start,i);
                    state = 6;
                }
                if (state == 6 && param.charAt(i)!=' ' && param.charAt(i)!='\t') {
                    state = 2;
                    start = i;
                }
                if (state == 2 && (param.charAt(i)==' ' || param.charAt(i)=='\t' || (i == param.length()-1))) {
                    name = param.substring(start,i+1);
                    state = 7;
                }
            }
            if (state != 7)
                return false;
            if (!modifier.equals ("in") && !modifier.equals("out") && !modifier.equals("inout"))
                return false;
        }
        return true;
    }

    private void checkState () {
        if (IdlUtilities.isValidIDLIdentifier(this.name.getText()) && 
	    this.ret.getText().length() >0 && 
	    acceptableArguments (this.params.getText())) {
            enableOk();
        }
        else {
            disableOk();
        }
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        checkState();
    }
    
}
