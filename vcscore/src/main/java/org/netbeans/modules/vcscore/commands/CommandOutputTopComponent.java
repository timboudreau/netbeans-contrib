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

package org.netbeans.modules.vcscore.commands;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;

import org.openide.util.NbBundle;
import org.openide.util.Mutex;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

import org.netbeans.modules.vcscore.ui.ErrorOutputPanel;
import org.netbeans.modules.vcscore.ui.OutputPanel;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;

/**
 * TopComponent for vcs command output
 * @author  Richard Gregor
 */
public class CommandOutputTopComponent extends TopComponent {
    
    private static CommandOutputTopComponent outputTopComponent;
    
    private ArrayList closeListeners = new ArrayList(); 
    private java.awt.event.ActionListener killListener = null;
    private JTabbedPane tabPane;
    private String name;
    protected Object eventSource;
    private JPopupMenu menu;
    private Action discardAction;
    private ErrorOutputPanel errorOutput;
    private java.awt.GridBagConstraints gridBagConstraints;
    private JLabel emptyLabel;
    private boolean tabPaneRemoved;
    
//    private static final long serialVersionUID = -8901733341334731237L;
    
    private CommandOutputTopComponent() {        
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/vcs_output.png"));  //NOI18N        
        initComponents();
        setName(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.topName")); //NOI18N
        setToolTipText(getName());
        initPopupMenu();
        new CommandOutputTopComponent.OutputTabPopupListener();
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
        "discard"); //NOI18N

        getActionMap().put("discard", discardAction);//NOI18N
    }

    public boolean isFocusable() {
        // #54284 ctrl + BS handling
        return tabPane == null || tabPaneRemoved || tabPane.getTabCount() == 0;
    }

    protected String preferredID(){
        return "CommandOutputTopComponent";   //NOI18N        
    }
    
    // -------- TopComponent singelton & persistence stuff ----------

    /** Gets default instance. Don't use directly, it reserved for '.settings' file only,
     * i.e. deserialization routines, otherwise you can get non-deserialized instance. */
    public static synchronized CommandOutputTopComponent getDefault() {
        if (outputTopComponent == null)
            outputTopComponent = new CommandOutputTopComponent();
        return outputTopComponent;
    }

    /** Finds default instance. Use in client code instead of {@link #getDefault()}. */
    public static synchronized CommandOutputTopComponent getInstance() {
        if (outputTopComponent == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("VCSCommandOutput"); // NOI18N
            if (outputTopComponent == null) {
                org.openide.ErrorManager.getDefault().notify(
                    org.openide.ErrorManager.INFORMATIONAL,
                    new IllegalStateException("Can not find CommandOutputTopComponent component for its ID. Returned " + tc)); // NOI18N
                outputTopComponent = new CommandOutputTopComponent();
            }
        }
        return outputTopComponent;
    }

    /** Overriden to explicitely set persistence type to PERSISTENCE_ALWAYS */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    /** Replaces this in object stream. */
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    final public static class ResolvableHelper implements java.io.Serializable {
        static final long serialVersionUID = -8901733341334731237L;
        public Object readResolve() {
            return CommandOutputTopComponent.getDefault();
        }
    }

    // ------ End of TopComponent singelton & persistence stuff ----------
    
    
    public synchronized ErrorOutputPanel getErrorOutput() {
        if (errorOutput == null) {
            errorOutput = new ErrorOutputPanel();
            if(tabPane == null)
                initTabPane();
            tabPane.insertTab(errorOutput.getTitle(), null, errorOutput, errorOutput.getToolTipText(), 0);
        }
        tabPane.setSelectedComponent(errorOutput);
        return errorOutput;
    }
    
    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());        
        emptyLabel = new JLabel(NbBundle.getBundle(CommandOutputTopComponent.class).getString("EMPTY_OUTPUT"));
        showEmptyStatus();    
    }
    
    private void initPopupMenu() {
        this.menu = new JPopupMenu();
        JMenuItem discardTab = new JMenuItem(); //NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab"));//NOI18N        
        discardAction = new AbstractAction(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab")) { //NOI18N
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (tabPane == null || tabPane.getTabCount() < 2) {
                    discardAll();
                } else {
                    if(tabPane.getSelectedIndex() > -1)
                        discard(tabPane.getSelectedComponent());
                }
            }
        };        
        discardTab.setAction(discardAction);
        discardTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,KeyEvent.CTRL_DOWN_MASK));
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                discardAll();
            }
        });
        
        this.menu.add(discardTab);
        this.menu.add(discardAll);
       
    }    
       
    Component getSelectedComponent() {
        return tabPane.getSelectedComponent();
    }
    
    void requestVisible(final Component c) {
        Mutex.EVENT.readAccess(new Runnable() {
            public void run() {
                if (!c.equals(tabPane.getSelectedComponent())) {
                    tabPane.setSelectedComponent(c);
                }
            }
        });
    }                
      
    /** 
     * Shows given popup on given coordinations and takes care about the
     * situation when menu can exceed screen limits 
     */
    protected void showPopupMenu (Point p, Component comp) {
        SwingUtilities.convertPointToScreen (p, comp);
        Dimension popupSize = menu.getPreferredSize ();
        Rectangle screenBounds = Utilities.getUsableScreenBounds(getGraphicsConfiguration());
        
        if (p.x + popupSize.width > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - popupSize.width;
        }
        if (p.y + popupSize.height > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - popupSize.height;
        }
        
        SwingUtilities.convertPointFromScreen (p, comp);
        menu.show(comp, p.x, p.y);
    }
  
    /**
     * Open the component on the given workspace.
     */
    public void open() {        
        super.open();       
        requestActive();
    }

    public synchronized void addVisualizer(String name, final JComponent component, boolean selected) {
        if(tabPaneRemoved)
            initTabPane();
        tabPane.addTab(name,component);
        if(selected) {
            final int index = tabPane.indexOfComponent(component);
            SwingUtilities.invokeLater(new TabSelector(tabPane, index));
        }
    }
    
    /**
     * Does exactly the same work like following code, but more efficiently
     * (one class is defined only).
     * <pre>
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tabPane.setSelectedIndex(index);
                    if (index > 0) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                tabPane.setSelectedIndex(0);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        tabPane.setSelectedIndex(index);
                                    }
                                });
                            }
                        });
                    }
                }
            });
       </pre>
     * <p>
     * This is a workaround of bug <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5037709">#5037709</a>.
     */
    // TODO Remove after bug #5037709 is fixed.
    private static class TabSelector implements Runnable {
        
        JTabbedPane tabPane;
        int index;
        int count;
        
        public TabSelector(JTabbedPane tabPane, int index) {
            this.tabPane = tabPane;
            this.index = index;
            this.count = 3;
        }
        
        public void run() {
            tabPane.setSelectedIndex(index*(count % 2)); // index or zero
            count--;
            if (index > 0 && count > 0) {
                SwingUtilities.invokeLater(this);
            }
        }
        
    }
    
    private void initTabPane(){               
        try{
            remove(emptyLabel);
        }catch(NullPointerException e){
            //ignore - it's here only for case the emptyLabel isn't in container
        }
        tabPane = new JTabbedPane();  
        tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setLayout(new java.awt.GridBagLayout());        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabPane, gridBagConstraints);
        tabPaneRemoved = false;
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer"));//NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer"));//NOI18N
        tabPane.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer.tabPane"));//NOI18N
        tabPane.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer.tabPane"));//NOI18N
        
    }
    
    public synchronized void discard(Component comp) {
        Component discarded = comp;
        tabPane.remove(discarded);
        if (errorOutput == discarded) {
            errorOutput = null;
        }
        if (tabPane.getTabCount() == 0){
            showEmptyStatus();
            close();
        }
    }
    
    public synchronized void discardAll() {
        if (tabPane != null) {
            tabPane.removeAll();
        }
        errorOutput = null;
        showEmptyStatus();
        close();
    }
    
    private void showEmptyStatus() {
        assert javax.swing.SwingUtilities.isEventDispatchThread(): "Bad thread to update UI: "+Thread.currentThread();
        tabPaneRemoved = true;
        if (tabPane != null) {
            try {
                remove(tabPane);
                tabPane = null;
            } catch(NullPointerException e) {
               //ignore -  it's here only for case the tabPane isn't in container,
               // it's more convenient than search through container's components 
            }
        }
        add(emptyLabel);
    }
    
    protected void componentActivated() {
        super.componentActivated();
    }
    
    protected void componentDeactivated() {
        super.componentDeactivated();
    }
    
    public void addCloseListener(TopComponentCloseListener l) {
        synchronized (closeListeners) {
            closeListeners.add(l);
        }
    }
    
    /**
     * Override for clean up reasons.
     * Will be moved to the appropriate method when will be made.
     */
    public boolean canClose(Workspace workspace, boolean last) {
        boolean can = super.canClose(workspace, last);
        if (last && can) {
            closing();
        }
        return can;
    }
    
    /**
     * Called when the TopComponent is being to close.
     */
    private void closing() {
        synchronized (closeListeners) {
            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                TopComponentCloseListener l = (TopComponentCloseListener) it.next();
                l.closing();
            }
            closeListeners.clear();
        }
    }
    

    /**
     * Popup Listener
     * taken from org.netbeans.core.output and modified
     */
    class OutputTabPopupListener implements AWTEventListener {        
        
        private OutputTabPopupListener() {              
            Toolkit.getDefaultToolkit().addAWTEventListener(
            this, AWTEvent.MOUSE_EVENT_MASK);
        }
        
        public void eventDispatched(AWTEvent ev) {
            MouseEvent e = (MouseEvent) ev;
            
            if (e.getID() != MouseEvent.MOUSE_PRESSED
            || ! org.openide.awt.MouseUtils.isRightMouseButton(e)
            ) {
                return;
            }
            
            Component c = (Component) e.getSource();
            while (c != null && !(c instanceof JTabbedPane))
                c = c.getParent();
            if (c == null)
                return;
            final JTabbedPane tab = (JTabbedPane) c;
            
            while ((c != null) && !(c instanceof CommandOutputTopComponent)) {
                c = c.getParent();
            }
            if (c == null)
                return;
            final CommandOutputTopComponent container = (CommandOutputTopComponent) c;
            final Component prevSelected = container.getSelectedComponent();
            
            final Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), tab);
            
            final TabbedPaneUI ui = tab.getUI();
            final int clickTab = ui.tabForCoordinate(tab, p.x, p.y);
            if (clickTab < 0)
                return;
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //Component in selected tab in given JTabbedPane
                    Component selectedInTab = tab.getComponentAt(clickTab);
                    //Check if component in clicked tab is selected in container if not
                    //select it.
                    if (prevSelected != selectedInTab) {
                        container.requestVisible(selectedInTab);
                    }
                    
                    Component selected = tab.getSelectedComponent();
                    
                    container.showPopupMenu(p, tab);
                }
            });
            
        }
        
    }
}
