/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.group;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.actions.ActionManager;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataLoader;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 * Template wizard iterator for handling creating group members from template.
 * This iterator is attached to each <code>GroupShadow</code> data object to
 * control group template instantiating.
 */
class GroupTemplateIterator implements TemplateWizard.Iterator {

    /** Target panel. */
    private Panel targetPanel = null;

    /** Constructor. */
    GroupTemplateIterator() {
    }

    /**
     * Instantiates the template using informations provided by the wizard.
     *
     * @param  wiz  the wizard
     * @return  set of data objects that has been created
     *          (should contain at least one) 
     * @exception  java.io.IOException  if the instantiation fails
     */
    public Set instantiate(TemplateWizard wiz) throws IOException {
        String nam = wiz.getTargetName();
        DataFolder folder = wiz.getTargetFolder();
        DataObject template = wiz.getTemplate();

        // new objects from all members of template group will be created
        // (even from nested groups)
        if (template instanceof GroupShadow) {
            GroupShadow group = (GroupShadow) template;
            List createdObjs = group.createGroupFromTemplate(folder, nam, true);
            HashSet templObjs = new HashSet(createdObjs.size());

            if (createdObjs != null) {
                Iterator it = createdObjs.iterator();
                while (it.hasNext()) {
                    DataObject obj = (DataObject) it.next();
                    if (!(obj instanceof DataFolder)
                            && !(obj instanceof GroupShadow)) {
                        templObjs.add(obj);
                        Node node = obj.getNodeDelegate();
                        SystemAction sa = node.getDefaultAction();
                        if (sa != null) {
                            ActionManager actionManager
                                    = (ActionManager) Lookup.getDefault().lookup(ActionManager.class);
                            actionManager.invokeAction(sa, new ActionEvent(node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
                        }
                    }
                }
            }
            return templObjs;
        } else {
            DataObject obj = nam == null ?
                         template.createFromTemplate(folder) :
                         template.createFromTemplate(folder, nam);

            return Collections.singleton(obj);
        }
    }

    /** Initializes this instance. */
    public void initialize(TemplateWizard wiz) {
        targetPanel = wiz.targetChooser();
    }

    /** No-op implementation. */
    public void uninitialize(TemplateWizard wiz) {
        targetPanel = null;
    }

    /** Get the current panel.
     * @return the panel */
    public Panel current() {
        return targetPanel;
    }

    /** Current name of the panel. */
    public String name() {
        return "";                                                      //NOI18N
    }

    /**
     * @return  <code>false</code> - only one panel is used
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * @return  <code>false</code> - only one panel is used
     */
    public boolean hasPrevious() {
        return false;
    }

    /**
     * Move to the next panel.
     * I.e. increment its index, need not actually change any GUI itself.
     *
     * @exception NoSuchElementException if the panel does not exist
     */
    public void nextPanel() {
        throw new NoSuchElementException();
    }

    /**
     * Move to the previous panel.
     * I.e. decrement its index, need not actually change any GUI itself.
     *
     * @exception NoSuchElementException if the panel does not exist
     */
    public void previousPanel() {
        throw new NoSuchElementException();
    }

    /** Dummy implementation of method <code>TemplateWizard.Iterator</code> interface method. */
    public void addChangeListener(ChangeListener l) {}

    /** Dummy implementation of method <code>TemplateWizard.Iterator</code> interface method. */
    public void removeChangeListener(ChangeListener l) {}

}