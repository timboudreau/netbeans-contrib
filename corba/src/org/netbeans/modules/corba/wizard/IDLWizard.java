/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard;

import java.awt.Dialog;
import java.util.ArrayList;
//import java.awt.event.ActionEvent;
import org.openide.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;
//import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.panels.*;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.IDLDataObject;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileSystem;
import org.openide.cookies.OpenCookie;
/**
 *
 * @author  tzezula
 * @version
 */
public class IDLWizard extends Object implements TemplateWizard.Iterator {
    
    private short index;
    private java.util.ArrayList listeners;
    private java.util.ResourceBundle bundle;
    private Dialog dialog;
    private IDLWizardData data;
    private WizardDescriptor.Panel selectWayPanel;
    private WizardDescriptor.Panel importIDLPanel;
    private CreateIDLPanel         createIDLPanel;
    private WizardDescriptor.Panel finishPanel;
    private WizardDescriptor.Panel destinationChooserPanel;
    private DataObject idl;
    
    
    public IDLWizard () {
        this.listeners = new java.util.ArrayList();
        this.data = new IDLWizardData();
    }
    
    public java.util.Set instantiate(org.openide.loaders.TemplateWizard wizard) throws java.io.IOException {
        final DataObject template = wizard.getTemplate();
        final DataFolder destination = wizard.getTargetFolder();
        final String name = wizard.getTargetName();
        final FileSystem fs = destination.getPrimaryFile().getFileSystem ();
        fs.runAtomicAction ( new FileSystem.AtomicAction () {
            public void run () {
                FileLock lock = null;
                PrintWriter out = null;
                try {
                    idl = template.createFromTemplate (destination, name);
                    String idlSource = IDLWizard.this.data.getIdlSource();
                    if (idlSource != null && idlSource.length() > 0) {                    
                        FileObject primaryFile = idl.getPrimaryFile();
                        lock = primaryFile.lock();
                        out = new PrintWriter ( new OutputStreamWriter ( primaryFile.getOutputStream (lock)));
                        java.text.DateFormat format = java.text.DateFormat.getDateTimeInstance (java.text.DateFormat.LONG, java.text.DateFormat.MEDIUM);
                        out.println ("//\n// "+idl.getName()+".idl\n//\n// Created on "+ format.format(new java.util.Date()) +"\n// by "+System.getProperty("user.name")+"\n//\n");
                        out.println (idlSource);
                    }
                }catch (java.io.IOException ioe) {}
                finally {
                    if (out  != null)
                        out.close();
                    if (lock != null)
                        lock.releaseLock();
                }
            }
        });
        OpenCookie openCookie = (OpenCookie) idl.getCookie (OpenCookie.class);
        if (openCookie != null)
            openCookie.open();
        if (this.data.continueCorbaWizard()) {
            // Add continuation of CORBA Wizard here
/*            new Thread () {
                public void run () {
                    SystemAction action = SystemAction.get (org.netbeans.modules.corba.wizard.CorbaWizardAction.class);
                    action.actionPerformed ( new ActionEvent (this,0,""));
                }
            }.start();
 */
            if (idl instanceof IDLDataObject) {
                new CorbaWizard ((IDLDataObject)idl).run();
            }
            else {
                new CorbaWizard ().run();
            }
        }
        return java.util.Collections.singleton (idl);
    }
    
    public void uninitialize (TemplateWizard wizard) {
        if (this.createIDLPanel != null) {
            this.createIDLPanel.cleanUp ();
        }
    }
    
    public void initialize(TemplateWizard wizard) {
        this.index = 0;
        this.data.setWizard (wizard);
    }
    
    public synchronized void addChangeListener (javax.swing.event.ChangeListener listener) {
        this.listeners.add (listener);
    }
    
    public synchronized void removeChangeListener (javax.swing.event.ChangeListener listener) {
        this.listeners.remove (listener);
    }
    
    public org.openide.WizardDescriptor.Panel current() {
        switch (this.index) {
            case 0:
                return getDestinationChooserPanel();
            case 1:
                return this.getSelectWayPanel();
            case 2:
                return this.getIDLCreatorPanel();
            case 3:
                return this.getFinishPanel();
                default:
                    return null;
        }
    }
    
    public boolean hasNext () {
        return (this.index < 3);
    }
    
    public boolean hasPrevious () {
        return (this.index > 0);
    }
    
    public void nextPanel () {
        this.index++;
    }
    
    public void previousPanel() {
        this.index--;
    }

    public String name () {
        return current().getComponent ().getName();
    }

    private void fireChangeEvent () {
        java.util.Iterator iterator = null;
        synchronized (this) {
            iterator = ((ArrayList)this.listeners.clone()).iterator();
        }
        javax.swing.event.ChangeEvent event = new javax.swing.event.ChangeEvent (this);
        while (iterator.hasNext()) {
            javax.swing.event.ChangeListener listener = (javax.swing.event.ChangeListener) iterator.next();
            listener.stateChanged (event);
        }
    }
    
    
    private org.openide.WizardDescriptor.Panel getSelectWayPanel () {
        if (this.selectWayPanel == null)
            this.selectWayPanel = new SelectSourcePanel (this.data);
        return this.selectWayPanel;
    }
    
    private org.openide.WizardDescriptor.Panel getFinishPanel () {
        if (this.finishPanel == null)
            this.finishPanel = new IDLFinishPanel(this.data);
        return this.finishPanel;
    }
    
    private org.openide.WizardDescriptor.Panel getIDLCreatorPanel () {
        if (this.data.importIdl()) {
            if (this.importIDLPanel == null)
                this.importIDLPanel = new ImportIDLPanel(this.data);
            return this.importIDLPanel;
        }
        else {
            if (this.createIDLPanel == null)
                this.createIDLPanel = new CreateIDLPanel(this.data);
            return this.createIDLPanel;
        }
    }
    
    
    private org.openide.WizardDescriptor.Panel getDestinationChooserPanel () {
        if (this.destinationChooserPanel == null) {
            this.destinationChooserPanel = this.data.getWizard().targetChooser();
            javax.swing.JComponent p = (javax.swing.JComponent) this.destinationChooserPanel.getComponent();
            p.putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(0));
            p.putClientProperty(CorbaWizard.PROP_CONTENT_DATA, new String[] {
                p.getName(),
                getLocalizedString("TXT_CreateImport"),
                getLocalizedString("TXT_CreateIDL"),
                getLocalizedString("TXT_Finish")
            });
        }
        return this.destinationChooserPanel;
    }
    
    private String getLocalizedString (String str) {
        if (this.bundle == null)
            this.bundle = NbBundle.getBundle (IDLWizard.class);
        return this.bundle.getString (str);
    }
    
}
