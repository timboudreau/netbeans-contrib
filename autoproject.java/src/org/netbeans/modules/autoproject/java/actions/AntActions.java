/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.Collator;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.apache.tools.ant.module.api.AntProjectCookie;
import org.apache.tools.ant.module.api.AntTargetExecutor;
import org.apache.tools.ant.module.api.support.AntScriptUtils;
import org.apache.tools.ant.module.api.support.TargetLister;
import org.netbeans.api.project.Project;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.Presenter;

/**
 * Tries to add actions from an Ant script.
 */
public class AntActions extends AbstractAction implements ContextAwareAction {

    /** public for layer */
    public AntActions() {}

    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        Project p = actionContext.lookup(Project.class);
        assert p != null;
        return new ContextAction(p);
    }

    private static class ContextAction extends AbstractAction implements Presenter.Popup {

        private final Project p;

        public ContextAction(Project p) {
            this.p = p;
        }

        public void actionPerformed(ActionEvent e) {
            assert false;
        }

        public JMenuItem getPopupPresenter() {
            return new Menu();
        }

        private class Menu extends JMenu implements DynamicMenuContent {

            private final AntProjectCookie apc;

            Menu() {
                FileObject buildXml = p.getProjectDirectory().getFileObject("build.xml");
                apc = buildXml != null ? AntScriptUtils.antProjectCookieFor(buildXml) : null;
                Mnemonics.setLocalizedText(this, NbBundle.getMessage(AntActions.class, "AntActions.label"));
            }

            public JComponent[] getMenuPresenters() {
                removeAll();
                if (apc == null) {
                    return new JComponent[0];
                }
                // Cribbed from org.apache.tools.ant.module.nodes.RunTargetsAction:
                Set<TargetLister.Target> allTargets;
                try {
                    allTargets = TargetLister.getTargets(apc);
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                    allTargets = Collections.emptySet();
                }
                String defaultTarget = null;
                SortedSet<String> describedTargets = new TreeSet<String>(Collator.getInstance());
                for (TargetLister.Target t : allTargets) {
                    if (t.isOverridden()) {
                        // Cannot be called.
                        continue;
                    }
                    if (t.isInternal()) {
                        // Don't present in GUI.
                        continue;
                    }
                    String name = t.getName();
                    if (t.isDefault()) {
                        defaultTarget = name;
                    } else if (t.isDescribed()) {
                        describedTargets.add(name);
                    }
                }
                if (defaultTarget != null) {
                    JMenuItem menuitem = new JMenuItem(defaultTarget);
                    menuitem.addActionListener(new TargetMenuItemHandler(defaultTarget));
                    add(menuitem);
                    addSeparator();
                }
                if (!describedTargets.isEmpty()) {
                    for (String target : describedTargets) {
                        JMenuItem menuitem = new JMenuItem(target);
                        menuitem.addActionListener(new TargetMenuItemHandler(target));
                        add(menuitem);
                    }
                }
                return new JComponent[] {this};
            }

            public JComponent[] synchMenuPresenters(JComponent[] items) {
                return getMenuPresenters();
            }

            /**
             * Action handler for a menu item representing one target.
             */
            private final class TargetMenuItemHandler implements ActionListener, Runnable {

                private final String target;

                public TargetMenuItemHandler(String target) {
                    this.target = target;
                }

                public void actionPerformed(ActionEvent ev) {
                    // #16720 part 2: don't do this in the event thread...
                    RequestProcessor.getDefault().post(this);
                }

                public void run() {
                    try {
                        ActionProviderImpl.cleanGeneratedClassfiles(p);
                        AntTargetExecutor.createTargetExecutor(new AntTargetExecutor.Env()).execute(apc, new String[] {target});
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    }
                }

            }

        }

    }

}
