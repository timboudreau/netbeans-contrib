/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.VariableInputDescriptor;
import org.netbeans.modules.vcscore.util.VariableInputDialog;

/**
 * The customizer of a UserCommand
 *
 * @author  Martin Entlicher
 */
public class UserCommandCustomizer extends JPanel implements ActionListener, Runnable {
    
    private CommandExecutionContext executionContext;
    private VcsDescribedCommand cmdOriginal;
    private VcsDescribedCommand cmd;
    private List actionListeners = new ArrayList();
    private VariableInputDialog dlg;
    private java.awt.GridBagConstraints gridBagConstraints;
    private String title;
    
    /** Creates a new instance of UserCommandCustomizer */
    public UserCommandCustomizer(CommandExecutionContext executionContext) {
        this.executionContext = executionContext;
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
    }
    
    public void setCommand(VcsDescribedCommand cmd, VariableInputDialog dlg,
                           String title) {
        //System.out.println("\nUserCommandCustomizer.setCommand("+cmd+", "+dlg+")");
        this.cmd = cmd;
        if (this.cmdOriginal == null) this.cmdOriginal = cmd;
        this.title = title;
        this.dlg = dlg;
        if (!isVisible()) {
            removeAll();
            add(dlg, gridBagConstraints);
        } else {
            javax.swing.SwingUtilities.invokeLater(this);
        }
        dlg.addCloseListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                //if (UserCommandCustomizer.this.dlg.isValidInput()) {
                //    setCommandVariables();
                //}
                boolean isPromptForEachFile = UserCommandCustomizer.this.dlg.getPromptForEachFile();
                //System.out.println("\n!!UserCommandCustomizer dlg.getPromptForEachFile() = "+isPromptForEachFile);
                if (!isPromptForEachFile) { // Close the dialog if I should not prompt for more files.
                    List listenersToNotify;
                    synchronized (actionListeners) {
                        listenersToNotify = new ArrayList(actionListeners);
                    }
                    for (Iterator it = listenersToNotify.iterator(); it.hasNext(); ) {
                        ((ActionListener) it.next()).actionPerformed(ev);
                    }
                    if (NotifyDescriptor.OK_OPTION.equals(ev.getSource())) {
                        
                    }
                }
            }
        });
        getAccessibleContext().setAccessibleName(dlg.getAccessibleContext().getAccessibleName());
        getAccessibleContext().setAccessibleDescription(dlg.getAccessibleContext().getAccessibleDescription());
    }
    
    public String getDisplayTitle() {
        return title;
    }
    
    public String getHelpID() {
        return (String) dlg.getClientProperty("helpID");
    }
    
    /**
     * Add action listener, which is notified when an OK/Cancel action was performed
     * on the dialog. The Dialog with this Customizer is closed when ACTION_PERFORMED
     * event is sent to this listener.
     */
    public void addActionListener(ActionListener listener) {
        synchronized (actionListeners) {
            actionListeners.add(listener);
        }
    }
    
    /**
     * Invoked when an action on the DialogDescriptor built from this customizer
     * occurs.
     */
    public void actionPerformed(ActionEvent evt) {
        dlg.getActionListener().actionPerformed(evt);
        /*
        if (evt.getID() == ActionEvent.ACTION_PERFORMED) {
            if (NotifyDescriptor.OK_OPTION.equals(evt.getSource())) {
                
            } else if (NotifyDescriptor.CANCEL_OPTION.equals(evt.getSource())) {
                
            }
        }
         */
    }
    
    /**
     * Get the component, that should have the initial focus in this dialog.
     */
    public java.awt.Component getInitialFocusedComponent() {
        return dlg.getInitialFocusedComponent();
    }

    /**
     * After the customizer is successfully finished and closed, this method is
     * called to do some post-customization work.
     */
    public void doPostCustomizationWork() {
        UserCommandSupport.commandCustomizedAndWillRun(cmdOriginal, executionContext);
    }
    
    /**
     * To update the dialog's content.
     */
    public void run() {
        removeAll();
        add(dlg, gridBagConstraints);
        revalidate();
        for (java.awt.Container parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof java.awt.Window) {
                ((java.awt.Window) parent).pack();
            }
            if (parent instanceof java.awt.Dialog) {
                ((java.awt.Dialog) parent).setTitle(title);
                break;
            } else if (parent instanceof java.awt.Frame) {
                ((java.awt.Frame) parent).setTitle(title);
                break;
            }
        }
    }
    
}
