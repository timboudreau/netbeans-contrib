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

package org.netbeans.modules.tasklist.filter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.SystemAction;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/** Filter the tasklist such that only tasks matching a given
 * criteria (or with a subtask matching the given criteria) are
 * shown.
 *
 * @author Tor Norbye
 */
public class FilterAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    protected final boolean asynchronous() {
        return false;
    }

    public final void performAction() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        
        // Pick the right list to use
        if (!(tc instanceof FilteredTopComponent)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        final FilteredTopComponent view = (FilteredTopComponent) tc;

        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout());
        parentPanel.getAccessibleContext().setAccessibleDescription
          (NbBundle.getMessage(FilterAction.class, "ACSD_Filter"));
                                                                    

        final FiltersPanel panel = new FiltersPanel(view);
        parentPanel.add(panel, BorderLayout.CENTER);
        DialogDescriptor d = new DialogDescriptor(parentPanel,
            NbBundle.getMessage(FilterAction.class,
            "TITLE_filter")); // NOI18N
        d.setModal(isModal());

        
        final JButton ok = new JButton();
        Mnemonics.setLocalizedText(ok, NbBundle.getMessage(
            FilterAction.class, "OK")); // NOI18N
        ok.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FilterAction.class, "BTN_OK_Hint"));
        
        final JButton cancel = new JButton();
        Mnemonics.setLocalizedText(cancel, NbBundle.getMessage(
            FilterAction.class, "Cancel")); // NOI18N
        cancel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FilterAction.class, "BTN_Cancel_Hint"));
        
        final JButton apply = new JButton();
        Mnemonics.setLocalizedText(apply, NbBundle.getMessage(
            FilterAction.class, "BTN_Preview")); // NOI18N
        apply.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FilterAction.class, "BTN_Preview_Hint"));
        
        panel.addPropertyChangeListener(FilterCondition.PROP_VALUE_VALID, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ok.setEnabled(panel.isValueValid());
                apply.setEnabled(panel.isValueValid());
            }
        });


        d.setOptions(new JButton[] {ok, cancel, apply});
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setButtonListener(new ActionListener() {

	    private FilterRepository repositoryBackup = null;
	    private Filter activeFilterBackup = null;

            public void actionPerformed(ActionEvent e) {
                Object src = e.getSource();
                if (src == ok) {
                    //view.setFilter(panel.getFilter(), true);
                    panel.updateFilters();
		    view.setFilter(view.getFilters().getActive());
                } else if (src == cancel) {
		  if (repositoryBackup != null) {
		    view.getFilters().assign(repositoryBackup);
		    view.setFilter(activeFilterBackup);
		  }
                } else if (src == apply) {
		  if (repositoryBackup == null) {
		    repositoryBackup = (FilterRepository)view.getFilters().clone();
		    activeFilterBackup = view.getFilter();
		  }
		  panel.updateFilters();
		  view.setFilter(view.getFilters().getActive());
                }
            }
        });
        d.setClosingOptions(new Object[] {ok, cancel});

        
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.setVisible(true);

    }

    
    /*
    public final void performAction() {
        // Pick the right list to use
        final TaskListView view =  TaskListView.getCurrent();
        if (view == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout());
        Filter exitingFilter = view.getFilter();
        if (exitingFilter == null) exitingFilter = createFilter(view);
        final FilterPanel panel = new FilterPanel(view, exitingFilter);
        parentPanel.add(panel, BorderLayout.CENTER);
        Component south = createSubpanel();
        if (south != null) {
            parentPanel.add(south, BorderLayout.SOUTH);
            panel.initSubpanel((FilterSubpanel)south);
        }

    }
*/
    public final void performAction(SystemAction action) {
        performAction();
    }

    /**
     * Overwrite to create optional filter subpanel
     * (placed in south). Default add subpanel with
     * hiearchy options.
     * @return Component implementing FilterSubpanel
     */
    protected Component createSubpanel() {
        return null;
    }

    /**
     * Creates new filter for views wihout existing one.
     * @return default returns view's {@link TaskListView#createFilter}.
     */
    /* TODO: protected Filter createFilter(TaskListView tlv) {
        return tlv.createFilter();
    }*/

    /** Return name of the action, as shown in menus etc. */
    public String getName() {
        return NbBundle.getMessage(FilterAction.class, "Filter"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/filter/filter.png"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }

    protected boolean isModal() {
        return false;
    }

}
