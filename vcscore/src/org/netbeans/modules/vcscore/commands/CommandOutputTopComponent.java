/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    
//    private static final long serialVersionUID = -8901733341334731237L;
    
    private CommandOutputTopComponent() {        
        setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/commands/commandOutputWindow.gif"));  //NOI18N        
        initComponents();
        setName(NbBundle.getBundle(CommandOutputVisualizer.class).getString("CommandOutputVisualizer.topName")); //NOI18N
        initPopupMenu();
        new CommandOutputTopComponent.OutputTabPopupListener();  
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(       
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK),
        "discard"); //NOI18N
        
        getActionMap().put("discard", discardAction);//NOI18N
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
            tabPane.insertTab(errorOutput.getTitle(), null, errorOutput, errorOutput.getToolTipText(), 0);
        }
        tabPane.setSelectedComponent(errorOutput);
        return errorOutput;
    }
    
    private void initComponents() {
        tabPane = new JTabbedPane();  
        tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabPane, gridBagConstraints);
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSN_CommandOutputVisualizer"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandOutputVisualizer.class, "ACSD_CommandOutputVisualizer"));
    }
    
    private void initPopupMenu() {
        this.menu = new JPopupMenu();
        JMenuItem discardTab = new JMenuItem(); //NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab"));//NOI18N        
        discardAction = new AbstractAction(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab")) { //NOI18N
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if(tabPane.getSelectedIndex() > -1)
                    discard(tabPane.getSelectedComponent());
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
        requestVisible();
    }

    public void addVisualizer(String name, JComponent component, boolean selected){
        tabPane.addTab(name,component);
        if(selected)
            tabPane.setSelectedComponent(component);
    }
    
    public synchronized void discard(Component comp) {
        Component discarded = comp;
        tabPane.remove(discarded);
        if (errorOutput == discarded) {
            errorOutput = null;
        }
        if (tabPane.getTabCount() == 0)
            close();
    }
    
    public synchronized void discardAll(){
        tabPane.removeAll();
        errorOutput = null;
        close();
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
