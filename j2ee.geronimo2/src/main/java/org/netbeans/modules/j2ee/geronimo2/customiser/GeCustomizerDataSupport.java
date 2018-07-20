/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.geronimo2.customiser;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.geronimo2.GeDeploymentManager;
import org.netbeans.modules.j2ee.geronimo2.GePluginProperties;
import org.openide.util.Exceptions;

/**
 * Customizer data support keeps models for all the customizer components,
 * initializes them, tracks model changes and performs save.
 *
 * @author maxa
 */
public class GeCustomizerDataSupport {
    
    //models
    private GeCustomizerSupport.PathModel classModel;
    private GeCustomizerSupport.PathModel javadocModel;
    private Document geHomeModel;
    private Document usernameModel;
    private Document passwordModel;
    private SpinnerNumberModel serverPortModel;
    private SpinnerNumberModel adminPortModel;
    
    //dirty flags
    private boolean javadocModelFlag;
    private boolean usernameModelFlag;
    private boolean passwordModelFlag;
    private boolean serverPortModelFlag;
    private boolean adminPortModelFlag;
    
    private GePluginProperties properties;
    private GeDeploymentManager dm;
    
    /**
     * Creates a new instance of CustomizerDataSupport
     */
    public GeCustomizerDataSupport(GeDeploymentManager dm) {
        this.dm = dm;
        this.properties = dm.getProperties();
        init();
    }
    
    /**
     * Initialize the customizer models
     */
    private void init() {
        // classModel
        classModel = new GeCustomizerSupport.PathModel(properties.getClasses());
        
        // javadocModel
        javadocModel = new GeCustomizerSupport.PathModel(properties.getJavadocs());
        javadocModel.addListDataListener(new ModelChangeAdapter() {
            public void modelChanged() {
                javadocModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // geronimoHomeModel
        geHomeModel = createDocument(properties.getGeHomeLocation());
        
        // usernameModel
        usernameModel = createDocument(properties.getUsername());
        usernameModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                usernameModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // passwordModel
        passwordModel = createDocument(properties.getPassword());
        passwordModel.addDocumentListener(new ModelChangeAdapter() {
            public void modelChanged() {
                passwordModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // serverPortModel
        serverPortModel = new SpinnerNumberModel(properties.getServerPort(), 0, 65535, 1);
        serverPortModel.addChangeListener(new ModelChangeAdapter() {
            public void modelChanged() {
                serverPortModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
        
        // shutdownPortModel
        adminPortModel = new SpinnerNumberModel(properties.getAdminPort(), 0, 65535, 1);
        adminPortModel.addChangeListener(new ModelChangeAdapter() {
            public void modelChanged() {
                adminPortModelFlag = true;
                store(); // This is just temporary until the server manager has OK and Cancel buttons
            }
        });
    }
    
    public InstanceProperties getInstanceProperties() {
        return dm.getInstanceProperties();
    }
    
    
    //model getters
    
    public GeCustomizerSupport.PathModel getClassModel() {
        return classModel;
    }
    
    public GeCustomizerSupport.PathModel getJavadocsModel() {
        return javadocModel;
    }
    
    public Document getGeHomeModel() {
        return geHomeModel;
    }
    
    public Document getUsenameModel() {
        return usernameModel;
    }
    
    public Document getPaswordModel() {
        return passwordModel;
    }
    
    public SpinnerNumberModel getServerPortModel() {
        return serverPortModel;
    }
    
    public SpinnerNumberModel getAdminPortModel() {
        return adminPortModel;
    }
    
    /**
     * Save all changes
     */
    private void store() {
        if (javadocModelFlag) {
            properties.setJavadocs(javadocModel.getData());
            javadocModelFlag = false;
        }
        
        if (usernameModelFlag) {
            properties.setUsername(getText(usernameModel));
            usernameModelFlag = false;
        }
        
        if (passwordModelFlag) {
            properties.setPassword(getText(passwordModel));
            passwordModelFlag = false;
        }
        
        if (serverPortModelFlag) {
            properties.setServerPort(((Integer)serverPortModel.getValue()).intValue());
            serverPortModelFlag = false;
        }
        
        if (adminPortModelFlag) {
            properties.setAdminPort(((Integer)adminPortModel.getValue()).intValue());
            adminPortModelFlag = false;
        }
    }
    
    /** Create a Document initialized by the specified text parameter, which may be null */
    private Document createDocument(String text) {
        PlainDocument doc = new PlainDocument();
        if (text != null) {
            try {
                doc.insertString(0, text, null);
            } catch(BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return doc;
    }
    
    /** Get the text value from the document */
    private String getText(Document doc) {
        try {
            return doc.getText(0, doc.getLength());
        } catch(BadLocationException e) {
            Exceptions.printStackTrace(e);
            return null;
        }
    }
    
    /**
     * Adapter that implements several listeners, which is useful for dirty model
     * monitoring.
     */
    private abstract class ModelChangeAdapter implements ListDataListener,
            DocumentListener, ItemListener, ChangeListener {
        
        public abstract void modelChanged();
        
        public void contentsChanged(ListDataEvent e) {
            modelChanged();
        }
        
        public void intervalAdded(ListDataEvent e) {
            modelChanged();
        }
        
        public void intervalRemoved(ListDataEvent e) {
            modelChanged();
        }
        
        public void changedUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void removeUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void insertUpdate(DocumentEvent e) {
            modelChanged();
        }
        
        public void itemStateChanged(ItemEvent e) {
            modelChanged();
        }
        
        public void stateChanged(javax.swing.event.ChangeEvent e) {
            modelChanged();
        }
    }
}