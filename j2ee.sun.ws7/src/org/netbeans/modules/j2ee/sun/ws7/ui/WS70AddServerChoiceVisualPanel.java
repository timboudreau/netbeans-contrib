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

/*
 * WS70AddServerChoiceVisualPanel.java
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.lang.reflect.*;

import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
import org.netbeans.modules.j2ee.sun.ws7.WS7LibsClassLoader;

/**
 *
 * @author  Administrator
 */
public class WS70AddServerChoiceVisualPanel extends javax.swing.JPanel {
    private static final String LOCALHOST="localhost";//NOI18N
    private static final String DEFAULT_SSL_PORT = "8989";
    private static final String DEFAULT_NON_SSL_PORT = "8800";
    private String installDirName;    
    private String hostName=LOCALHOST;
    private String portNumber= DEFAULT_SSL_PORT;
    private String userName;
    private String password;
    private final List listeners = new ArrayList();
    private List<String> ssl_ports = new ArrayList<String>();
    private List<String> non_ssl_ports = new ArrayList<String>();
    
    /**
     * Creates new form WS70AddServerChoiceVisualPanel
     */
    public WS70AddServerChoiceVisualPanel() {
        initComponents();
    }
    public String getAdminUserName(){
        return jUserNameTxt.getText().trim();
    }
    public String getAdminPassword(){
        char[] password = jPasswordTxt.getPassword();
        return String.valueOf(password);
    }
    
    public String getAdminHost(){
        return jAdminHostTxt.getText().trim();        
    }
    public String getAdminPort(){
        return jAdminPortTxt.getText().trim();        
    }

    public String getServerLocation(){
        return jLocationTxt.getText().trim();
    }
    public boolean isLocalServer(){
        return !jCBRemote.isSelected();
    }
    public boolean isAdminOnSSL(){
        return jCBSSLPort.isSelected();
    }    

    public void updatePortField() {
        String location = jLocationTxt.getText().trim();
        // To take care of JES installation also, get the instance location from wsenv file
        String instanceLocation = org.netbeans.modules.j2ee.sun.ws7.util.Util.getInstanceLocation(location);
        parseServerXml(instanceLocation);
    }

    // Use the java Reflexion to access the admin generated schema2beans classes especially Server class (which are  part of webserv-rt.jar) 
    // and then parse the server.xml to get the http-listeners 
    public void parseServerXml(String location) {
        File serverXml = new File(location+File.separator+"admin-server"+File.separator+"config"+File.separator+"server.xml");
        try {
            WS7LibsClassLoader wscl = new WS7LibsClassLoader();
            wscl.addURL(new File(location + File.separator + "lib" + File.separator + "webserv-rt.jar"));
            Class serverCls = wscl.loadClass("com.sun.webserver.config.serverbeans.Server");

            // Call create graph method (on serverXml file)  and getHttpListener method from the server class
            Method cgMth = serverCls.getMethod("createGraph", new Class[] {serverXml.getClass()});
            Method hlMth = serverCls.getMethod("getHttpListener", null);

            // Load HttpListenerType class and get the required methods
            Class hlCls = wscl.loadClass("com.sun.webserver.config.serverbeans.HttpListenerType");
            Method getPortMth = hlCls.getMethod("getPort", null);
            Method getSslMth =  hlCls.getMethod("getSsl", null);
            Method isHLEnabledMth = hlCls.getMethod("isEnabled", null);

            // Load SslType class as getSsl method returns a SslType class, which will be used to invoke the getEnabled method
            Class sslTypeCls = wscl.loadClass("com.sun.webserver.config.serverbeans.SslType");
            Method isSSLEnabledMth = sslTypeCls.getMethod("getEnabled", new Class[] {Integer.TYPE});

            // Make sure that no previous ports exist
            ssl_ports.clear();
            non_ssl_ports.clear();

            Object createGraph = cgMth.invoke(serverCls, new Object[]{serverXml});
            Object[] httpListeners = (Object[]) hlMth.invoke(createGraph, null); 

            // Iterate through all the http-listeners to separte ssl and non-ssl ports
            for(Object hl : httpListeners) {
                Object isHLEnabled = isHLEnabledMth.invoke(hl, null);
                if (!((Boolean)isHLEnabled))
                    continue;

                Object port = getPortMth.invoke(hl, null);
                Object ssl = getSslMth.invoke(hl, null);
                Object isSSLEnabled = isSSLEnabledMth.invoke(ssl, new Object[] {new Integer(0)});
                if ((Boolean)isSSLEnabled) {
                    ssl_ports.add(port.toString());
                } else {
                    non_ssl_ports.add(port.toString());
                }
            }
 
            // Set the initial display based on the http-listener ports detected
            if (ssl_ports.size() > 0 || non_ssl_ports.size() > 0)
                setInitialDisplay();
            else
                setDefaultDisplay();

        } catch(Exception e) {
            System.out.println("Warning: HttpListener's port detection is failed, so setting the defaults");
            setDefaultDisplay();
            e.printStackTrace();
        }

    }

    // If only 1 port type is detected then SSL checkbox is disabled, while appropriately checked
    // If only 1 SSL, 1 non SSL, or 1 of both ssl and non SSL ports are detected then port field is disabled
    // If both port types (SSL and non SSL) are detected then SSL checkbox is enabled
    // If more than 1 port of the selected type (SSL or non SSL) is found then port field is enabled for that type
    private void setInitialDisplay() {
        List<String> ports = (ssl_ports.size() != 0) ? ssl_ports : non_ssl_ports;
        jAdminPortTxt.setText(ports.get(0));
        jAdminPortTxt.setEditable((ports.size() > 1));
        jCBSSLPort.setSelected((ssl_ports.size() != 0));
        jCBSSLPort.setEnabled((ssl_ports.size() > 0 && non_ssl_ports.size() > 0));
    }

   // In case of any failures just use this as a default display
   private void setDefaultDisplay() {
       jAdminPortTxt.setText(DEFAULT_SSL_PORT);
       jAdminPortTxt.setEditable(false);
       jCBSSLPort.setEnabled(false);
       jCBSSLPort.setSelected(true);
   }

    public boolean isValid(WizardDescriptor wizard){        
        if (!validateDirectory()){
            jLocationTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            return false;
        }        
        if(!validateAdminHost()){
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_HOSTNAME"));
            jAdminHostTxt.setFocusable(true);
            return false;
        }
        if(!validateAdminPort()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_VALID_PORT"));
            return false;
        }
        if(!validateUserName()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_USERNAME"));
            return false;
        }
        // Remote host validation needs to be done only after validating admin host and admin port
        // Just show a warning message and proceed further even if validation fails
        if (!validateRemoteHost()) {
           wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE 
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ADMIN_CONNECT_ERROR")); 
           return true;
        }
        wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE, null);
        return true;
    }
    private boolean validateAdminHost(){
        if(hostName==null || hostName.length()<=0){            
            return false;
        }
        return true;
    }    
    private boolean validateAdminPort(){
        if(portNumber==null || portNumber.length()<=0){
            return false;
        }
        int port;
        try{
            port = Integer.parseInt(portNumber);
        }catch(NumberFormatException e){
            port = -1;
        }
        if(port<=0 || port>65535){            
            return false;
        }

        if (!jCBRemote.isSelected()) 
            return (jCBSSLPort.isSelected()) ? ssl_ports.contains(portNumber) 
                                             : non_ssl_ports.contains(portNumber);
        return true;
    }
    private boolean validateUserName(){
        if(userName==null || userName.length()<=0){
            return false;
        }
        return true;
    }        
    private boolean validateRemoteHost() {
        if (jCBRemote.isSelected()) {
            String host = jAdminHostTxt.getText().trim();
            String port = jAdminPortTxt.getText().trim();
            return org.netbeans.modules.j2ee.sun.ws7.util.Util.isRunning(host, Integer.parseInt(port));
        }
        return true;
    }
    private boolean validateDirectory() {
        if(installDirName==null){
            return false;
        }
        if(installDirName.equals("")){
            return false;
        }
        
        if(!isValidServer(new File(installDirName))){
            
            // Bug# 84607 Just show the error at the bottom of the panel. 
            // Message box is only needed when user has selected a wrong 
            // directory name using the FileChooser
            
            //Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            jLocationTxt.requestFocusInWindow();
            jLocationTxt.selectAll();
            return false;
        }
        return true;
    }
    
    private boolean isValidServer(File dir){        
        File libDir = new File(dir + File.separator + "lib");
        File binDir = new File(dir + File.separator + "bin");
        File libFile = new File(libDir + File.separator + "webserv-rt.jar");
        if(!libDir.exists() || !binDir.exists() || !libFile.exists() ) {
            return false;
        }

        File binFile = new File(binDir + File.separator + "wadm");
        File binWinFile = new File(binDir + File.separator + "wadm.bat");
        if(!binFile.exists() && !binWinFile.exists()) {
            return false;
        }

        return true;
    }
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public String getName(){
        return NbBundle.getMessage(WS70AddServerChoiceVisualPanel.class, "LBL_AddServerWizardTitle");
    }
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }    
    private void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        ArrayList tempList;

        synchronized(listeners) {
            tempList = new ArrayList(listeners);
        }

        Iterator iter = tempList.iterator();
        while (iter.hasNext())
            ((ChangeListener)iter.next()).stateChanged(event);
    }    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jServerInstructionsLbl = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDirectoryLbl = new javax.swing.JLabel();
        jLocationTxt = new javax.swing.JTextField();
        jBrowseBtn = new javax.swing.JButton();
        jAdminHostLbl = new javax.swing.JLabel();
        jAdminHostTxt = new javax.swing.JTextField();
        jAdminPortLbl = new javax.swing.JLabel();
        jAdminPortTxt = new javax.swing.JTextField();
        jUserNameLbl = new javax.swing.JLabel();
        jUserNameTxt = new javax.swing.JTextField();
        jPasswordLbl = new javax.swing.JLabel();
        jPasswordTxt = new javax.swing.JPasswordField();
        jAdminInstructionsLbl = new javax.swing.JLabel();
        jCBRemote = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jCBSSLPort = new javax.swing.JCheckBox();

        jServerInstructionsLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanelTitle")); // NOI18N

        jDirectoryLbl.setDisplayedMnemonic(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_Inst_Dir_Mnem").charAt(0));
        jDirectoryLbl.setLabelFor(jLocationTxt);
        jDirectoryLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanel_Directory")); // NOI18N

        jLocationTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusLost(evt);
            }
        });

        jBrowseBtn.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Browse_Mnem").charAt(0));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle"); // NOI18N
        jBrowseBtn.setText(bundle.getString("LBL_AddServerVisualPanel_Browse")); // NOI18N
        jBrowseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseBtnActionPerformed(evt);
            }
        });

        jAdminHostLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Host_Mnem").charAt(0));
        jAdminHostLbl.setLabelFor(jAdminHostTxt);
        jAdminHostLbl.setText(bundle.getString("LBL_AddServerVisualPanelHost")); // NOI18N

        jAdminHostTxt.setEditable(false);
        jAdminHostTxt.setText(LOCALHOST);

        jAdminHostTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminHostTxtCaretUpdate(evt);
            }
        });

        jAdminPortLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Port_Mnem").charAt(0));
        jAdminPortLbl.setLabelFor(jAdminPortTxt);
        jAdminPortLbl.setText(bundle.getString("LBL_AddServerVisualPanelPort")); // NOI18N

        jAdminPortTxt.setEditable(false);
        jAdminPortTxt.setText(DEFAULT_SSL_PORT);

        jAdminPortTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminPortTxtCaretUpdate(evt);
            }
        });

        jUserNameLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_UserName_Mnem").charAt(0));
        jUserNameLbl.setLabelFor(jUserNameTxt);
        jUserNameLbl.setText(bundle.getString("LBL_AddServerVisualPanelUserName")); // NOI18N

        jUserNameTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jUserNameTxtCaretUpdate(evt);
            }
        });

        jPasswordLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Password_Mnem").charAt(0));
        jPasswordLbl.setLabelFor(jPasswordTxt);
        jPasswordLbl.setText(bundle.getString("LBL_AddServerVisualPanelPassword")); // NOI18N

        jPasswordTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusLost(evt);
            }
        });

        jAdminInstructionsLbl.setText(bundle.getString("LBL_AddServerVisualPanelAdmin")); // NOI18N

        jCBRemote.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Remote_Mnem").charAt(0));
        jCBRemote.setText(bundle.getString("LBL_RegisterRemoteServer")); // NOI18N
        jCBRemote.setToolTipText(bundle.getString("ToolTip_RegisterRemoteServer")); // NOI18N
        jCBRemote.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCBRemote.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCBRemote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBRemoteActionPerformed(evt);
            }
        });
        jCBRemote.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCBRemoteStateChanged(evt);
            }
        });

        jCBSSLPort.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_SSL_Mnem").charAt(0));
        jCBSSLPort.setSelected(true);
        jCBSSLPort.setEnabled(false);
        jCBSSLPort.setText(bundle.getString("LBL_AddServerVisualPanelSSLPort")); // NOI18N
        jCBSSLPort.setToolTipText(bundle.getString("Tooltip_AddServerVisualPanelSSLPort")); // NOI18N
        jCBSSLPort.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCBSSLPort.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCBSSLPort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBSSLPortItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(36, 36, 36)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jAdminInstructionsLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 299, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPasswordLbl)
                            .add(jAdminHostLbl)
                            .add(jAdminPortLbl)
                            .add(jUserNameLbl))
                        .add(31, 31, 31)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .add(jAdminHostTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(25, 25, 25)
                                .add(jCBSSLPort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                .add(84, 84, 84))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jCBRemote)
                            .add(layout.createSequentialGroup()
                                .add(jDirectoryLbl)
                                .add(16, 16, 16)
                                .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBrowseBtn))
                            .add(jServerInstructionsLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 350, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(105, 105, 105)
                        .add(jLabel2))
                    .add(layout.createSequentialGroup()
                        .add(jServerInstructionsLbl)
                        .add(16, 16, 16)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jDirectoryLbl)
                            .add(jBrowseBtn))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCBRemote)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 12, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jAdminInstructionsLbl)
                .add(16, 16, 16)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminHostTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jAdminHostLbl))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminPortLbl)
                    .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jCBSSLPort))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jUserNameLbl))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jPasswordLbl))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jServerInstructionsLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanelTitle")); // NOI18N
        jServerInstructionsLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanelTitle")); // NOI18N
        jDirectoryLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanel_Directory")); // NOI18N
        jDirectoryLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanel_Directory")); // NOI18N
        jDirectoryLbl.getAccessibleContext().setAccessibleParent(this);
        jBrowseBtn.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanel_Browse")); // NOI18N
        jBrowseBtn.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanel_Browse")); // NOI18N
        jBrowseBtn.getAccessibleContext().setAccessibleParent(this);
        jAdminHostLbl.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelHost")); // NOI18N
        jAdminHostLbl.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelHost")); // NOI18N
        jAdminHostLbl.getAccessibleContext().setAccessibleParent(this);
        jAdminPortLbl.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelPort")); // NOI18N
        jAdminPortLbl.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelPort")); // NOI18N
        jAdminPortLbl.getAccessibleContext().setAccessibleParent(this);
        jUserNameLbl.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelUserName")); // NOI18N
        jUserNameLbl.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelUserName")); // NOI18N
        jUserNameLbl.getAccessibleContext().setAccessibleParent(this);
        jPasswordLbl.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelPassword")); // NOI18N
        jPasswordLbl.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelPassword")); // NOI18N
        jPasswordLbl.getAccessibleContext().setAccessibleParent(this);
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelAdmin")); // NOI18N
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelAdmin")); // NOI18N
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleParent(this);
        jCBSSLPort.getAccessibleContext().setAccessibleName(bundle.getString("A11Y_NAME_AddServerVisualPanelSSLPort")); // NOI18N
        jCBSSLPort.getAccessibleContext().setAccessibleDescription(bundle.getString("A11Y_DESC_AddServerVisualPanelSSLPort")); // NOI18N
        jCBSSLPort.getAccessibleContext().setAccessibleParent(this);
    }// </editor-fold>//GEN-END:initComponents

    private void jCBSSLPortItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBSSLPortItemStateChanged
        if (jCBRemote.isSelected()) {
            if (jCBSSLPort.isSelected()) 
                jAdminPortTxt.setText(DEFAULT_SSL_PORT);
            else
                jAdminPortTxt.setText(DEFAULT_NON_SSL_PORT);
        } else {
            List<String> ports = (jCBSSLPort.isSelected()) ? ssl_ports : non_ssl_ports;
            jAdminPortTxt.setEditable((ports.size() > 1));
            jAdminPortTxt.setText(ports.get(0));
        }
    }//GEN-LAST:event_jCBSSLPortItemStateChanged

    private void jCBRemoteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCBRemoteStateChanged
        if (!jCBRemote.isSelected()) {
            jAdminHostTxt.setText(LOCALHOST);
            jAdminHostTxt.setEditable(false);
            if (validateDirectory())
                setInitialDisplay();
            else
                setDefaultDisplay();
        } else {
            jAdminHostTxt.setEditable(true);                
            jAdminPortTxt.setText(DEFAULT_SSL_PORT);
            jAdminPortTxt.setEditable(true);
            jCBSSLPort.setEnabled(true);
            jCBSSLPort.setSelected(true);
        }
    }//GEN-LAST:event_jCBRemoteStateChanged

    private void jCBRemoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRemoteActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jCBRemoteActionPerformed

    private void jLocationTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusLost
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        

        // If a valid webserver installation is detected then call the updatePortField method to parse the server.xml file
        // and then to update the admin port value, else fall back to the default display
        if (validateDirectory())
            updatePortField();
        else
            setDefaultDisplay();
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusLost

    private void jLocationTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusGained
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusGained

    private void jAdminHostTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminHostTxtCaretUpdate
        String host = jAdminHostTxt.getText().trim();
        if (host.equals(this.hostName) ) {
            return;
        }
        hostName = host;
        fireChange();          
    }//GEN-LAST:event_jAdminHostTxtCaretUpdate

    private void jAdminPortTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminPortTxtCaretUpdate
       String port = jAdminPortTxt.getText().trim();
        if (port.equals(this.portNumber)) {
            return;
        }
        portNumber = port;
        fireChange();            
    }//GEN-LAST:event_jAdminPortTxtCaretUpdate

    private void jUserNameTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jUserNameTxtCaretUpdate
        String uname = jUserNameTxt.getText().trim();
        if (uname.equals(this.userName) ) {
            return;
        }
        userName = uname;
        fireChange();
    }//GEN-LAST:event_jUserNameTxtCaretUpdate

    private void jPasswordTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusLost
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusLost

    private void jPasswordTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusGained
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusGained

    private void jBrowseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseBtnActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File installDir = null;
        if (installDir != null )
            chooser.setSelectedFile(installDir);
        boolean repeatchooser = true;
        while ( repeatchooser ) {
            repeatchooser = false;
            if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
                installDir = chooser.getSelectedFile();
                if (isValidServer(installDir)){
                    jLocationTxt.setText(installDir.getAbsolutePath());
                    installDirName = installDir.getAbsolutePath();
                } else {
                    Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
                    jLocationTxt.setText( "" );  // NOI18N
                    installDir = null;
                    repeatchooser = true;                    
                }
            }
        }

        // If a valid webserver installation is detected then call the updatePortField method to parse the server.xml file
        // and then to update the admin port value, else fall back to the default display
        if (validateDirectory())
            updatePortField();
        else
            setDefaultDisplay();
    }//GEN-LAST:event_jBrowseBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jAdminHostLbl;
    private javax.swing.JTextField jAdminHostTxt;
    private javax.swing.JLabel jAdminInstructionsLbl;
    private javax.swing.JLabel jAdminPortLbl;
    private javax.swing.JTextField jAdminPortTxt;
    private javax.swing.JButton jBrowseBtn;
    private javax.swing.JCheckBox jCBRemote;
    private javax.swing.JCheckBox jCBSSLPort;
    private javax.swing.JLabel jDirectoryLbl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jLocationTxt;
    private javax.swing.JLabel jPasswordLbl;
    private javax.swing.JPasswordField jPasswordTxt;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel jServerInstructionsLbl;
    private javax.swing.JLabel jUserNameLbl;
    private javax.swing.JTextField jUserNameTxt;
    // End of variables declaration//GEN-END:variables
    
}
