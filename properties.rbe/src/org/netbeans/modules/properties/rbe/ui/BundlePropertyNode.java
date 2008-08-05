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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.ui;

import java.awt.Dialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.netbeans.modules.properties.rbe.model.TreeItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

/**
 * The BundlePropertyNode
 * @author @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public abstract class BundlePropertyNode extends AbstractNode {

    protected final static Image defaultIcon;
    protected final static Image defaultIconWithWarning;


    static {
        defaultIcon = Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesKey.gif");
        defaultIconWithWarning = ImageUtilities.mergeImages(defaultIcon,
                Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/warning2.gif"), 14, 10);
    }

    public BundlePropertyNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public BundlePropertyNode(Children children) {
        super(children);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    SystemAction.get(CutAction.class),
                    SystemAction.get(CopyAction.class),
                    SystemAction.get(PasteAction.class),
                    //                    new MoveAction(),
                    new DuplicateAction()
                };
    }

    @Override
    public Image getIcon(int type) {
        if (getProperty().isContainsEmptyLocaleProperty()) {
            return defaultIconWithWarning;
        }
        return defaultIcon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    public void move(String key) {
    }

    public void duplicate(String key) {
        
    }

    public abstract BundleProperty getProperty();

    public abstract TreeItem<BundleProperty> getTreeItem();

    class MoveAction extends AbstractAction {

        public MoveAction() {
            super("Move");
        }

        public void actionPerformed(ActionEvent ev) {
            UIMoveActionPanel actionPanel = new UIMoveActionPanel();
            actionPanel.getKeyTextField().setText(getProperty().getKey());
            DialogDescriptor dialogDescriptor = new DialogDescriptor(
                    actionPanel,
                    NbBundle.getBundle(UIMoveActionPanel.class).getString("UIMoveActionPanel.title"),
                    true,
                    DialogDescriptor.OK_CANCEL_OPTION,
                    DialogDescriptor.OK_OPTION,
                    new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getSource() == DialogDescriptor.OK_OPTION) {
                                move("");
                            }
                        }
                    });

            Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
            dialog.setVisible(true);
        }
    }

    class DuplicateAction extends AbstractAction implements ActionListener {

        public DuplicateAction() {
            super("Duplicate");
        }

        public void actionPerformed(ActionEvent ev) {
            final UIDuplicateActionPanel actionPanel = new UIDuplicateActionPanel();
            actionPanel.getKeyTextField().setText(getProperty().getKey());
            DialogDescriptor dialogDescriptor = new DialogDescriptor(
                    actionPanel,
                    NbBundle.getBundle(UIMoveActionPanel.class).getString("UIDuplicateActionPanel.title"),
                    true,
                    DialogDescriptor.OK_CANCEL_OPTION,
                    DialogDescriptor.OK_OPTION,
                    new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getSource() == DialogDescriptor.OK_OPTION) {
                                String key = actionPanel.getKeyTextField().getText();
                                duplicate(key);
                            }
                        }
                    });

            Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
            dialog.setVisible(true);
        }
    }
}
