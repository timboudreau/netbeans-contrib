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

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.editors.LocationPropertyEditor;
import org.netbeans.modules.tasklist.core.editors.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.core.editors.StringPropertyEditor;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.text.DataEditorSupport;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

import javax.swing.*;
import org.openide.nodes.Children;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.tasklist.core.TaskChildren;
import org.netbeans.modules.tasklist.core.SuggestionNodeProperty;
import org.netbeans.modules.tasklist.filter.FilterAction;


/**
 * A node in the Suggestions View, representing a Suggestion
 *
 * @author Tor Norbye
 */

public class SuggestionNode extends TaskNode {

    // the node is displayed in given view
    private TaskListView view;


    public SuggestionNode(SuggestionImpl item) {
      this(item, Children.LEAF);
    }

    /** 
     * @param subtasks show subtasks as children ? 
     */
    protected SuggestionNode(SuggestionImpl item, Children children) {
        super(item, children);
    }

    public Action getPreferredAction() {
        if (item.getAction() == null) {
            return SystemAction.get(ShowSuggestionAction.class);
        } else {
            return SystemAction.get(FixAction.class);
        }
    }


    public Node cloneNode () {
      SuggestionNode clon = new SuggestionNode((SuggestionImpl)this.item);
      if (!clon.isLeaf()) 
	clon.setChildren((Children)getTaskChildren().clone());
      return clon;
    }

    protected TaskChildren createChildren() {
      return new SuggestionChildren((SuggestionImpl)this.item);
    }



    protected void updateIcon() {
        setIconBase("org/netbeans/modules/tasklist/suggestions/suggTask"); // NOI18N
    }
    
    protected SystemAction[] createActions() {
        ArrayList actions = new ArrayList(20);
        if (item.getAction() != null) {
            actions.add(SystemAction.get(FixAction.class));
        }
        //actions.add(SystemAction.get(GoToTaskAction.class);
        actions.add(SystemAction.get(ShowSuggestionAction.class));
        List typeActions =
            ((SuggestionImpl)item).getSType().getActions();
        if ((typeActions != null) && (typeActions.size() > 0)) {
            actions.add(null);
            Iterator it = typeActions.iterator();
            while (it.hasNext()) {
                actions.add(it.next());
            }
        }

        // "Global" (not node specific) actions moved to toolbar
//            actions.add(null);
//            actions.add(SystemAction.get(ShowCategoryAction.class));
//            actions.add(SystemAction.get(EditTypesAction.class));
//            actions.add(SystemAction.get(DisableAction.class));
//            actions.add(null);
//            actions.add(SystemAction.get(FilterAction.class));
//            actions.add(SystemAction.get(ExpandAllAction.class));
//            actions.add(null);
//            actions.add(SystemAction.get(ExportAction.class));

        // Property: node specific, but by convention last in menu
        // #38642 do not show for TODOs, its confusing XXX
        if ("nb-tasklist-scannedtask".equals(item.getType()) == false ) { // NOI18N
            actions.add(null);
            actions.add(SystemAction.get(PropertiesAction.class));
        }

        return (SystemAction[])actions.toArray(
             new SystemAction[actions.size()]);
    }

    public Action[] getActions(boolean empty) {
        if (empty) {
            return new SystemAction[] {
                SystemAction.get(FilterAction.class),
                SystemAction.get(EditTypesAction.class)
            };
        } else {
            return super.getActions(false);
        }
    }

    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);
        

	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_SUMMARY));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_DETAILS));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_PRIORITY, PriorityPropertyEditor.class));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_FILENAME, StringPropertyEditor.class));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_LINE_NUMBER, StringPropertyEditor.class));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_CATEGORY));
	ss.put(new SuggestionNodeProperty(item, SuggestionImplProperties.PROP_LOCATION, LocationPropertyEditor.class));

        return s;
    }

    static String getCategoryLabel() {
        return NbBundle.getMessage(SuggestionNode.class, "Category"); // NOI18N
    }
    
    public boolean canRename() {
        return false;
    }

    public boolean canDestroy() {
        // No point since it gets recreated after every edit
        return false;
    }

    /** Can this node be copied?
    * @return <code>true</code>
    */
    public boolean canCopy () {
        return true;
    }

    /** Can this node be cut?
    * @return <code>false</code>
    */
    public boolean canCut () {
        // No point since it gets recreated after every edit
        return false;
    }

    /** Don't allow pastes */
    protected void createPasteTypes(Transferable t, List s) {
    }


    /** Get a cookie. Call super first, but if null, also
     * check the data object associated with the line number
     * if any.
     * @todo Should this be done in TaskNode (for all tasklist
     * tasks) or just here?
     */
    public Node.Cookie getCookie(Class cl) {
        Node.Cookie c = super.getCookie(cl);
        if (c != null) {
            return c;
        }
        if (cl.isAssignableFrom(Suggestion.class)) {
            return (SuggestionImpl)item;
        }
        Line l = item.getLine();
        if (l != null) {
            DataObject dao = DataEditorSupport.findDataObject(l);
            if (dao != null)
                return dao.getCookie(cl);
            else
                return null;
        }
        return null;
    }
}

