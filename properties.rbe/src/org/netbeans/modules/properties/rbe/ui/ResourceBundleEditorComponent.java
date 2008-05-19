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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Locale;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import org.netbeans.modules.properties.Element.ItemElem;
import org.netbeans.modules.properties.PropertiesDataObject;
import org.netbeans.modules.properties.rbe.Constants;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.CloneableTopComponent;

/**
 * The Resourcebundle editor top component
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class ResourceBundleEditorComponent extends CloneableTopComponent implements ExplorerManager.Provider, PropertyChangeListener {

    public static final String PREFERRED_ID = "ResourceBundleEditorComponent";
    /** Properties data object */
    private final PropertiesDataObject dataObject;
    /** The Explorer manager for nodes */
    private ExplorerManager explorer;
    /** The UI window */
    private UIWindow uiWindow;
    /** The tree view */
    ImprovedBeanTreeView treeView;

    public ResourceBundleEditorComponent(PropertiesDataObject dataObject) {
        this.dataObject = dataObject;

        setName(dataObject.getName() + ".properties");
        setToolTipText(NbBundle.getMessage(ResourceBundleEditorComponent.class, "CTL_ResourceBundleEditorComponent"));

        treeView = new ImprovedBeanTreeView();
        treeView.setRootVisible(false);

        explorer = new ExplorerManager();
        explorer.addPropertyChangeListener(this);
        explorer.setRootContext(new AbstractNode(Children.create(new BundlePropertyNodeFactory(dataObject), true)));
        associateLookup(ExplorerUtils.createLookup(explorer, new ActionMap()));

        uiWindow = new UIWindow();
        uiWindow.getTreePanel().setLayout(new BoxLayout(uiWindow.getTreePanel(), BoxLayout.PAGE_AXIS));
        uiWindow.getRightPanel().setLayout(new BoxLayout(uiWindow.getRightPanel(), BoxLayout.PAGE_AXIS));
        uiWindow.getTreePanel().add(treeView);

        uiWindow.getCollapseAllButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                collapseAll();
            }
        });

        uiWindow.getExpandAllButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                expandAll();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(uiWindow);
    }

    public void expandAll() {
        treeView.expandAll();
    }

    public void collapseAll() {
        treeView.collapseAll();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            updateSelectedProperty();
        }
    }

    protected void updateSelectedProperty() {
        boolean reseted = false;
        for (Node selectedNode : explorer.getSelectedNodes()) {
            if (selectedNode instanceof BundlePropertyNode) {
                if (!reseted) {
                    uiWindow.getRightPanel().removeAll();
                    reseted = true;
                }
                BundlePropertyNode bundlePropertyNode = (BundlePropertyNode) selectedNode;
                for (Locale locale : bundlePropertyNode.getProperty().getBundle().getLocales()) {
                    UIPropertyPanel propertyPanel = new UIPropertyPanel();
                    ItemElem item = bundlePropertyNode.getProperty().getLocaleRepresentation().get(locale);
                    if (Constants.DEFAULT_LOCALE.equals(locale)) {
                        propertyPanel.getTitleLabel().setText(NbBundle.getMessage(ResourceBundleEditorComponent.class, "DefaultLocale"));
                    } else {
                        String title = String.format("%s (%s)%s", locale.getDisplayLanguage(),
                                locale.getLanguage(), locale.getDisplayCountry().length() > 0 ? " - " + locale.getDisplayCountry() : "");
                        propertyPanel.getTitleLabel().setText(title);
                    }
                    if (item != null) {
                        propertyPanel.getTextArea().setText(
                                "Value: " + item.getValue() + "\n" +
                                "Comment: " + item.getComment());
                    }

                    uiWindow.getRightPanel().add(propertyPanel);
                }
            }
        }
        uiWindow.getRightPanel().updateUI();
    }

    @Override
    public Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/properties/rbe/resources/propertiesObject.png"); // NOI18N

    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public ExplorerManager getExplorerManager() {
        return explorer;
    }
}
