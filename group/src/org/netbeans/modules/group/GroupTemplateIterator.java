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