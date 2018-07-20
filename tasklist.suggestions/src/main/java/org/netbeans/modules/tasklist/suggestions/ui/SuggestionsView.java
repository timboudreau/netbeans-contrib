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

package org.netbeans.modules.tasklist.suggestions.ui;

import javax.swing.ButtonGroup;
import org.netbeans.modules.tasklist.core.table.ChooseColumnsPanel;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.text.Line;
import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.client.*;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.netbeans.modules.tasklist.export.ExportAction;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterAction;
import org.netbeans.modules.tasklist.filter.RemoveFilterAction;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;

import org.netbeans.modules.tasklist.suggestions.*;

/** 
 * View showing the todo list items
 *
 * @author Tor Norbye
 */
public class SuggestionsView extends TaskListView implements SuggestionView {

    private static final long serialVersionUID = 1;

    private static SuggestionsView theView;
    
    /**
     * Create the live suggestion view.
     * @return
     */
    public static SuggestionsView createSuggestionsView() {
        if (theView == null) {
            theView = new SuggestionsView();
        }
        return theView;
    }

    public final static String CATEGORY = "suggestions"; // NOI18N
    
    private String originalName;
    private String originalIcon;

    /** Iff true live view otherwise snapshot. */
    private boolean scan = false;
    private SuggestionsBroker.Job broker;

    private SuggestionType showingType = null;

    /** 
     * Creates default suggestions view 
     */
    public SuggestionsView() {
        this(CATEGORY,
            NbBundle.getMessage(SuggestionsView.class, "SuggestionsView"), // NOI18N
            null,
            true,
            "org/netbeans/modules/tasklist/suggestions/suggestion.gif" // NOI18N
        );
        scan = true;
    }

    /** 
     * Construct a Scan view with the given window title, and the given
     * list to show the contents in
     *
     * @param category The category of this window
     * @param name The name of the window
     * @param list The tasklist to store the scanned tasks in
     * @param persistent should this view be reconstructed after NB restart?
     * @param icon icon for this view
     */
    public SuggestionsView(String category, String name, TaskList list,
        boolean persistent, String icon) {
    	super(
              category,
              name,
              // I made a taskView.png, but it was larger (286 bytes) than the
              // gif (186 bytes). More importantly, it had ugly display artifacts.
              Utilities.loadImage(icon),
              persistent,
              list);

        this.originalName = name;
        this.originalIcon = icon;

        // When the tab is alone in a container, don't show a tab;
        // the category nodes provide enough feedback.
        putClientProperty("TabPolicy", "HideWhenAlone"); // NOI18N
    }
    
    static final String PROP_SUGG_DETAILS = "suggDetails"; // NOI18N
    static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
    static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
    static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
    static final String PROP_SUGG_CAT = "suggCat"; // NOI18N
    static final String PROP_SUGG_LOC = "suggLoc"; // NOI18N

    protected ColumnProperty[] createColumns() {
        // No point allowing other attributes of the task since that's
        // all we support for scan items (they are not created by
        // the user - and they are not persisted.
        return new ColumnProperty[] { 
            getMainColumn(800),
            getPriorityColumn(true, 100),
            getDetailsColumn(false, 800),
            getFileColumn(true, 150),
            getLineColumn(true, 50),
            getCategoryColumn(true, 150)
        };
    };

    public ColumnProperty getMainColumn(int width) {
        // Tree column
        // NOTE: Task.getDisplayName() must also be kept in sync here
        return new ColumnProperty(
	    0, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_SUMMARY,
	    true,
            width
	    );
    }
    
    public ColumnProperty getPriorityColumn(boolean visible, int width) {
        return new ColumnProperty(
	    1, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_PRIORITY,
            true,
            visible,
            width
            );
    }

    public ColumnProperty getFileColumn(boolean visible, int width) {
        return new ColumnProperty(
	    2, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_FILENAME,
            true,
            visible,
            width
            );
    }

    public ColumnProperty getLineColumn(boolean visible, int width) {
        return new ColumnProperty(
	    3, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_LINE_NUMBER,
            true,
            visible,
            width
            );
    }

    public ColumnProperty getCategoryColumn(boolean visible, int width) {
        return new ColumnProperty(
	    4, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_CATEGORY,
            true,
            visible,
            width
            );
    }

    public ColumnProperty getDetailsColumn(boolean visible, int width) {
        return new ColumnProperty(
	    5, // UID -- never change (part of serialization
	    SuggestionImplProperties.PROP_DETAILS,
            true,
            visible,
            width
            );
    }

    protected Component createNorthComponent() {
        SystemAction[] actions = new SystemAction[] {
            SystemAction.get(FixAction.class),
            SystemAction.get(ShowSuggestionAction.class),
            SystemAction.get(FilterAction.class),
            null,
            SystemAction.get(RemoveFilterAction.class),
            SystemAction.get(ShowCategoryAction.class),
            SystemAction.get(EditTypesAction.class),
            SystemAction.get(DisableAction.class),
            null,
            SystemAction.get(ExportAction.class)
        };

        JToolBar toolbar = SystemAction.createToolbarPresenter(actions);
        toolbar.setFloatable(false);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
            
        toolbar.setFloatable(false);
        // TODO: toolbar.setLayout(new ToolbarLayout());

        ButtonGroup bg = new ButtonGroup();
        JToggleButton cur = new JToggleButton("Current File", true); // TODO: NOI18N
        bg.add(cur);
        JToggleButton openedFiles = 
                new JToggleButton("Opened Projects", false); // TODO: NOI18N
        bg.add(openedFiles);
        JToggleButton openedProjects = 
                new JToggleButton("Opened Files", false); // TODO: NOI18N
        bg.add(openedProjects);
        
        toolbar.add(cur, 0);
        toolbar.add(openedFiles, 1); // TODO: NOI18N
        toolbar.add(openedProjects, 2); // TODO: NOI18N

        return toolbar;
    }
   
    protected String preferredID() {
        return getClass().getName();
    }
    
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        int version = objectInput.readInt();  // IOExceptions means even older unversioned serialization
        if (version == 1) {
            scan = objectInput.readBoolean();
            theView = this;
        }
        super.readExternal(objectInput);
        category = CATEGORY;
    }

    public void writeExternal(java.io.ObjectOutput objectOutput) throws java.io.IOException {
        objectOutput.writeInt(1); // version
        objectOutput.writeBoolean(this == theView);  // theView
        super.writeExternal(objectOutput);
        ObservableList list = getModel();
        if (list != null) {
            flushExpansion();
            ManagerSettings.getDefault().store();
        }
    }

    protected void componentHidden() {
        super.componentHidden();
        // Update expansion state before we remove the nodes
        ObservableList list = getModel();
        if (list != null) {
            flushExpansion();
        }
        getManager().dispatchStop();

        if (scan) {
            broker.stopBroker();  // created in componentShowing
            setModel(null);
        }
    }

    protected void componentShowing() {
        super.componentShowing();
        getManager().dispatchRun();
        if (scan) {
            broker = SuggestionsBroker.getDefault().startBroker(ProviderAcceptor.ALL);
            setModel(broker.getSuggestionsList());
        }
    }

    protected void componentOpened() {
        super.componentOpened();
        getManager().dispatchPrepare();
    }
    
    protected void componentClosed() {
        super.componentClosed();
        getManager().notifyViewClosed();
        theView = null;
    }
    
    protected Node createRootNode() {
        return new TaskListNode(getModel());
    }

    // Ensure that we clear the filter icon and label if a user-entered
    // filter (not by the ShowCategoryAction) is added
    public void setFilter(Filter f) {
        super.setFilter(f);
        notifyFiltered(null);
    }

    /**
     * Notify view that a new filter for the given type is in effect 
     * This method is thread safe.
     */
    void notifyFiltered(SuggestionType type) {
        showingType = type;
        final Image icon;
        final String title;
        if (type != null) {
            icon = type.getIconImage();
            title = type.getLocalizedName();
        } else {
            title = originalName;
            icon = Utilities.loadImage(originalIcon);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setName(title);
                setIcon(icon);
            }
        });
    }

    /**
     * Returns the live view managed by manager.
     * <p>
     * If you need to locate scanned view
     * you must use retrieve it from context (e.g. SuggestionNode).
     *
     * @return the view or null
     */
    public static SuggestionsView getCurrentView() {
        return theView;
    }

    protected TaskAnnotation getAnnotation(Task task) {
        return new SuggestionAnno(task, this);
    }

    /** 
     * Set the scanning status of the window. While the view is scanning,
     * show that it's busy. 
     * This method is thread safe.
     */
    void setScanning(boolean scanning) {
        final Image icon;
        if (scanning) {
            icon = Utilities.loadImage(
                "org/netbeans/modules/tasklist/suggestions/scanning.gif");
        } else {
            if (showingType != null) {
                icon = showingType.getIconImage();
            } else {
                icon = Utilities.loadImage(originalIcon);
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setIcon(icon);
            }
        });
    }

    public String toString() {
        return "SuggestionsView@" + hashCode();
    }

    private SuggestionList getSuggestionList() {
        return (SuggestionList) getList();
    }

    // SuggestionView interface ~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean isObserved(String category) {
        // XXX filters
        return isShowing();
    }

    public SuggestionList getSuggestionsModel() {
        return getSuggestionList();
    }

    /** For the category tasks, update the expansion state setting. */
    private void flushExpansion() {
        Collection categoryTasks = getSuggestionList().getCategoryTasks();
        if (categoryTasks == null) {
            return;
        }
        SuggestionManagerImpl manager =
            (SuggestionManagerImpl)SuggestionManager.getDefault();
        Node root = getEffectiveRoot();
        Iterator it = categoryTasks.iterator();
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl)it.next();
            Node n = TaskNode.find(root, s);
            if (n == null) {
                continue;
            }
            SuggestionType type = s.getSType();
            boolean expanded = isExpanded(n);
            if (expanded) {
                manager.setExpandedType(type, true);
            } else if (manager.isExpandedType(type)) {
                // Only set it to false if it's already recorded to be true
                manager.setExpandedType(type, false);
            }
        }
    }

    // XXX unused was attached to caret listener

    Line prevLine = null;
    List erase = null;
    List origIcon = null;

    /**
     * Set the current cursor line to the given line position.
     * Suggestions on the given line will be highlighted.
     *
     * @param line The current line of the cursor.
     */
    private void setCursorLine(Line line) {
        if (line == prevLine) {
            return;
        }
        prevLine = line;

        // Clear out previously highlighted items
        if (erase != null) {
            Iterator it = erase.iterator();
            Iterator itorig = origIcon.iterator();
            while (it.hasNext()) {
                SuggestionImpl s = (SuggestionImpl) it.next();
                Image icon = (Image) itorig.next();
                s.setIcon(icon);
                //s.setHighlighted(false);
            }
        }
        erase = null;
        origIcon = null;


        if (line == null) {
            // Prevent line==null from highlighting all suggestions
            // without an associated line position...
            return;
        }

        SuggestionsView view = SuggestionsView.getCurrentView();
        if (view != null) {
            Node node = view.getEffectiveRoot();
            highlightNode(node, line);
        }
    }

    /** Set a series of suggestions as highlighted. Or, clear the current
     * selection of highlighted nodes.
     * <p>
     * @param suggestions List of suggestions that should be highlighted.
     *      If null, the selection is cleared.
     * @param type The type for which this selection applies. Each type
     *      has its own, independent set of highlighted suggestions. If
     *      null, it applies to all types.
     *
     */
    private void highlightNode(Node node, Line line) {
        SuggestionImpl s = (SuggestionImpl) TaskNode.getTask(node);
        if (s.getLine() == line) {
            if (erase == null) {
                origIcon = new ArrayList(20);
                erase = new ArrayList(20);
            }
            origIcon.add(s.getIcon());
            //s.setHighlighted(true);
            Image badge = Utilities.loadImage(
                "org/netbeans/modules/tasklist/suggestions/badge.gif"); // NOI18N
            Image image = Utilities.mergeImages(s.getIcon(), badge,
                    0, 0);
            s.setIcon(image);
            erase.add(s);
        }

        // Recurse?
        if (s.hasSubtasks() && (isExpanded(node))) {
            Node[] nodes = node.getChildren().getNodes();
            int n = (nodes != null) ? nodes.length : 0;
            for (int i = 0; i < n; i++) {
                highlightNode(nodes[i], line);
            }
        }
    }

    private SuggestionManagerImpl getManager() {
        return (SuggestionManagerImpl)SuggestionManager.getDefault();
    }
    
    public Filter createFilter() {
        return new SuggestionFilter("Simple"); // NOI18N
    }
    
    protected Component createCenterComponent() {
        Component cmp = super.createCenterComponent();
        JTabbedPane tp = new JTabbedPane();
        tp.addTab("Current File", cmp); // TODO: i18n
        JTable t = new SuggestionsTable();
        JScrollPane sp = new JScrollPane(t);
        ChooseColumnsPanel.installChooseColumnsButton(sp);
        tp.addTab("Open Projects", sp);// TODO: i18n
        return tp;
    }

}
