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

package org.netbeans.modules.ant.freeform.customcommands;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class NewCommandAction extends AbstractAction implements ContextAwareAction {
    
    public NewCommandAction() {
        // Label not likely displayed in GUI, but used also from ContextAction.
        super(NbBundle.getMessage(NewCommandAction.class, "LBL_action"));
    }
    
    public void actionPerformed(ActionEvent e) {
        // Cannot be invoked without any context.
        assert false;
    }
    
    public Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }
    
    private WizardDescriptor.Panel[] panels;
    
    private void run(String[] likelyCommandNames) {
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels(likelyCommandNames));
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Create Custom Project Command"); // XXX I18N
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (cancelled) {
            return;
        }
        String command = (String) wizardDescriptor.getProperty("command"); // NOI18N
        String displayName = (String) wizardDescriptor.getProperty("displayName"); // NOI18N
        String menu = (String) wizardDescriptor.getProperty("menu"); // NOI18N
        int position = ((Integer) wizardDescriptor.getProperty("position")).intValue();
        DataFolder menuFolder = DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().findResource("Menu/" + menu)); // NOI18N
        try {
            new Command(command, displayName, null).create(menuFolder, position);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private WizardDescriptor.Panel[] getPanels(String[] likelyCommandNames) {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                new NewCommandWizardPanel(likelyCommandNames),
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                steps[i] = c.getName();
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
    
    private static final Set/*<String>*/ STANDARD_COMMANDS = new HashSet();
    static {
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_COMPILE_SINGLE);
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_DEBUG_SINGLE);
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_DEBUG_STEP_INTO);
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_DEBUG_TEST_SINGLE);
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_RUN_SINGLE);
        STANDARD_COMMANDS.add(ActionProvider.COMMAND_TEST_SINGLE);
        STANDARD_COMMANDS.add("debug.fix"); // XXX no def?
    }

    private static String[] findLikelyCommandNames(Project p) throws IOException, SAXException {
        if (p == null) {
            return null; // #72266
        }
        FileObject projectXml = p.getProjectDirectory().getFileObject("nbproject/project.xml"); // NOI18N
        if (projectXml == null) {
            return null;
        }
        Document doc = XMLUtil.parse(new InputSource(projectXml.getURL().toExternalForm()), false, false, null, null);
        NodeList actions = doc.getElementsByTagName("action"); // NOI18N
        SortedSet/*<String>*/ commands = new TreeSet();
        for (int i = 0; i < actions.getLength(); i++) {
            Element action = (Element) actions.item(i);
            String command = action.getAttribute("name"); // NOI18N
            if (command == null) {
                // Might be in <context-menu> rather than <ide-actions>; ignore.
                continue;
            }
            if (STANDARD_COMMANDS.contains(command)) {
                continue;
            }
            if (action.getElementsByTagName("context").getLength() == 0) { // NOI18N
                // Not context-sensitive; ignore.
                continue;
            }
            commands.add(command);
        }
        return commands.isEmpty() ? null : (String[]) commands.toArray(new String[commands.size()]);
    }
        
    private final class ContextAction extends AbstractAction implements Presenter.Popup {
        
        private String[] likelyCommandNames;
        
        public ContextAction(Lookup context) {
            super((String) NewCommandAction.this.getValue(Action.NAME));
            Project p = (Project) context.lookup(Project.class);
            try {
                likelyCommandNames = findLikelyCommandNames(p);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                likelyCommandNames = null;
            } catch (SAXException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                likelyCommandNames = null;
            }
        }
        
        public void actionPerformed(ActionEvent e) {
            assert likelyCommandNames != null;
            run(likelyCommandNames);
        }
        
        public JMenuItem getPopupPresenter() {
            class Presenter extends JMenuItem implements DynamicMenuContent {
                public Presenter() {
                    super(ContextAction.this);
                }
                public JComponent[] getMenuPresenters() {
                    if (likelyCommandNames != null) {
                        return new JComponent[] {this};
                    } else {
                        // Disabled, so do not display at all.
                        return new JComponent[0];
                    }
                }
                public JComponent[] synchMenuPresenters(JComponent[] items) {
                    return getMenuPresenters();
                }
            }
            return new Presenter();
        }

    }
    
}
