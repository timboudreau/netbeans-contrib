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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
import org.openide.*;
import org.netbeans.modules.corba.wizard.panels.*;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CorbaWizard extends Object implements WizardDescriptor.Iterator, PropertyChangeListener {

    private static final boolean DEBUG = true;
    //  private static final boolean DEBUG = flase;

    private WizardDescriptor.Panel[] panels;
    private CorbaWizardData data;
    private int index;
    private Dialog dialog;

    /** Creates new CorbaWizard */
    public CorbaWizard() {
        this (new CorbaWizardData());
    }

    public CorbaWizard (CorbaWizardData data) {
        this (data, new WizardDescriptor.Panel[]{new StartPanel(), new ORBPanel()});
    }

    public CorbaWizard (CorbaWizardData data, WizardDescriptor.Panel[] panels) {
        this.data = data;
        this.panels = panels;
        this.index = 0;
    }

    public CorbaWizardData getData() {
        return this.data;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    public boolean hasNext() {
        return (index < this.panels.length-1);
    }

    public boolean hasPrevious () {
        return index > 0;
    }

    private int totalCount () {
        return this.panels.length;
    }

    private int currentIndex () {
        return index;
    }

    public String name () {
        return CorbaWizardAction.getLocalizedString("TITLE_CorbaWizard");
    }

    public synchronized void nextPanel () {
        if (index < this.panels.length-1)
            this.index++;
    }

    public synchronized void  previousPanel () {
        if (index > 0)
            this.index--;
    }

    public void run () {
        if (DEBUG)
            System.out.println("Starting CORBA Wizard...");

        WizardDescriptor descriptor = new WizardDescriptor(this.panels, data);
        descriptor.setTitleFormat(new java.text.MessageFormat("CORBA Wizard[{1}]"));
        descriptor.addPropertyChangeListener(this);

        dialog = TopManager.getDefault().createDialog(descriptor);
        dialog.show();
        if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION){
            //Generate code here
            if (DEBUG)
                System.out.println("CORBA Wizard: generating...");
        }
    }

    public void addChangeListener (ChangeListener listener){
    }

    public void removeChangeListener (ChangeListener listener){
    }

    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getPropertyName().equals(DialogDescriptor.PROP_VALUE)){
            Object option = event.getNewValue();
            if (option == WizardDescriptor.FINISH_OPTION || option == WizardDescriptor.CANCEL_OPTION){
                dialog.setVisible(false);
                dialog.dispose();
            }
        }
    }

}