/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views;

import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.awt.dnd.DnDConstants;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.accessibility.*;

import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.explorer.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter;
import org.openide.nodes.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.*;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.actions.PopupAction;
import org.netbeans.modules.vcscore.versioning.*;


/** 
 * Explorer view to display single item in a panel.
 * @author   Milos Kleint
 */
public class SingleNodeView extends JPanel implements Externalizable {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 0; //TODO

    /** Explorer manager to work with. Is not null only if the component is showing
    * in components hierarchy
    */
    private transient ExplorerManager manager;



    //
    // listeners
    //

    /** Listener to nearly everything */
    transient Listener managerListener;

    /** weak variation of the listener for property change on the explorer manager */
    transient PropertyChangeListener wlpc;
    /** weak variation of the listener for vetoable change on the explorer manager */
    transient VetoableChangeListener wlvc;

    /** popup */
    transient PopupAdapter popupListener;

    //
    // properties
    //

    /** if true, the icon view displays a popup on right mouse click, if false, the popup is not displayed */
    private boolean popupAllowed = true;
    /** if true, the hierarchy traversal is allowed, if false, it is disabled */
    private boolean traversalAllowed = false;

    /** action preformer */
    private ActionListener defaultProcessor;

    private transient Node filterNode;
    
    //private transient FileInfoContainer contextInfo;
    //
    // Dnd
    //

    /** true if drag support is active */
    transient boolean dragActive = false;
    /** true if drop support is active */
    transient boolean dropActive = false;

    /** True, if the selection listener is attached. */
    transient boolean listenerActive;


    // init .................................................................................

    /** Default constructor.
    */
    public SingleNodeView() {
        initialize();

/*        if (DragDropUtilities.dragAndDropEnabled) {
            setDragSource(true);
            setDropTarget(true);
        }
 */
    }

    /** Initializes the panel.
    */
    private void initialize() {
        // initilizes the JTree


/*??        {
            AbstractAction action = new GoUpAction ();
            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
            list.registerKeyboardAction(action, key, JComponent.WHEN_FOCUSED);
        }

        {
            AbstractAction action = new EnterAction ();
            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            list.registerKeyboardAction(action, key, JComponent.WHEN_FOCUSED);
        }
*/
//        System.out.println("initstarts..");
        managerListener = new Listener ();
        popupListener = new PopupAdapter ();

//        model.addListDataListener(managerListener);

        addMouseListener(managerListener);
        addMouseListener(popupListener);
/*        list.getSelectionModel().setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );
 */

        ToolTipManager.sharedInstance ().registerComponent (this);
        
        addFocusListener(new FocusAdapter());
//        System.out.println("initends..");
    }


    /*
    * Write view's state to output stream.
    */
    public void writeExternal (ObjectOutput out) throws IOException {
        out.writeObject (new Boolean (popupAllowed));
        out.writeObject (new Boolean (traversalAllowed));
    }

    /*
    * Reads view's state form output stream.
    */
    public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
        popupAllowed = ((Boolean)in.readObject ()).booleanValue ();
        traversalAllowed = ((Boolean)in.readObject ()).booleanValue ();
    }


    // properties ...........................................................................

    /** Test whether display of a popup menu is enabled.
     * @return <code>true</code> if so */
    public boolean isPopupAllowed () {
        return popupAllowed;
    }

    /** Enable/disable displaying popup menus on list view items. Default is enabled.
    * @param value <code>true</code> to enable
    */
    public void setPopupAllowed (boolean value) {
        popupAllowed = value;
    }

    /** Test whether hierarchy traversal shortcuts are permitted.
    * @return <code>true</code> if so */
    public boolean isTraversalAllowed () {
        return traversalAllowed;
    }

    /** Enable/disable hierarchy traversal using <code>CTRL+click</code> (down) and <code>Backspace</code> (up), default is enabled.
    * @param value <code>true</code> to enable
    */
    public void setTraversalAllowed (boolean value) {
        traversalAllowed = value;
    }

    /** Get the current processor for default actions.
    * If not <code>null</code>, double-clicks or pressing Enter on 
    * items in the view will not perform the default action on the selected node; rather the processor 
    * will be notified about the event.
    * @return the current default-action processor, or <code>null</code>
    */
    public ActionListener getDefaultProcessor () {
        return defaultProcessor;
    }

    /** Set a new processor for default actions.
    * @param value the new default-action processor, or <code>null</code> to restore use of the selected node's declared default action
    * @see #getDefaultProcessor
    */
    public void setDefaultProcessor (ActionListener value) {
        defaultProcessor = value;
    }



    /********** Support for the Drag & Drop operations *********/

    /** @return true if dragging from the view is enabled, false
    * otherwise.<br>
    * Drag support is disabled by default.
    */
    public boolean isDragSource () {
        return false;
    }

    /** Enables/disables dragging support.
    * @param state true enables dragging support, false disables it.
    */
/*    public void setDragSource (boolean state) {
        if (state == dragActive)
            return;
        dragActive = state;
        // create drag support if needed
        if (dragActive && (dragSupport == null))
            dragSupport = new ListViewDragSupport(this, list);
        // activate / deactivate support according to the state
        dragSupport.activate(dragActive);
    }
*/
    /** @return true if dropping to the view is enabled, false
    * otherwise<br>
    * Drop support is disabled by default.
    */
    public boolean isDropTarget () {
        return false;
    }

    /** Enables/disables dropping support.
    * @param state true means drops into view are allowed,
    * false forbids any drops into this view.
    */
/*    public void setDropTarget (boolean state) {
        if (state == dropActive)
            return;
        dropActive = state;
        // create drop support if needed
        if (dropActive && (dropSupport == null))
            dropSupport = new ListViewDropSupport(this, list);
        // activate / deactivate support according to the state
        dropSupport.activate(dropActive);
    }
*/
    /** @return Set of actions which are allowed when dragging from
    * asociated component.
    * Actions constants comes from DnDConstants.XXX constants.
    * All actions (copy, move, link) are allowed by default.
    */
    public int getAllowedDragActions () {
        // PENDING
        return DnDConstants.ACTION_MOVE | DnDConstants.ACTION_COPY |
               DnDConstants.ACTION_LINK;
    }

    /** Sets allowed actions for dragging
    * @param actions new drag actions, using DnDConstants.XXX 
    */  
    public void setAllowedDragActions (int actions) {
        // PENDING
    }

    /** @return Set of actions which are allowed when dropping
    * into the asociated component.
    * Actions constants comes from DnDConstants.XXX constants.
    * All actions are allowed by default.
    */
    public int getAllowedDropActions () {
        // PENDING
        return DnDConstants.ACTION_MOVE | DnDConstants.ACTION_COPY |
               DnDConstants.ACTION_LINK;
    }

    /** Sets allowed actions for dropping.
    * @param actions new allowed drop actions, using DnDConstants.XXX 
    */  
    public void setAllowedDropActions (int actions) {
        // PENDING
    }


    /** Called when the list changed selection and the explorer manager
    * should be updated.
    * @param nodes list of nodes that should be selected
    * @param em explorer manager
    * @exception PropertyVetoException if the manager does not allow the
    *   selection
    */
    protected void selectionChanged (Node[] nodes, ExplorerManager em)
    throws PropertyVetoException {
        em.setSelectedNodes (nodes);
    }

    /** Called when explorer manager is about to change the current selection.
    * The view can forbid the change if it is not able to display such
    * selection.
    *
    * @param nodes the nodes to select
    * @return false if the view is not able to change the selection
    */
    protected boolean selectionAccept (Node[] nodes) {
        // if the selection is just the root context, confirm the selection
/*???        if (nodes.length == 1 && manager.getRootContext().equals(nodes[0])) {
            return true;
        }

        Node cntx = manager.getExploredContext ();

        // we do not allow selection in other than the exploredContext
        for (int i = 0; i < nodes.length; i++) {
            VisualizerNode v = VisualizerNode.getVisualizer (null, nodes[i]);
            if (model.getIndex (v) == -1) {
                return false;
            }
        }
*/
        return true;
    }

    //
    // Working methods
    //

    
    public void setContextNode(Node node) {
        if (filterNode != null) {
            FileVcsInfo info = (FileVcsInfo)filterNode.getCookie(FileVcsInfo.class);
            info.removePropertyChangeListener(managerListener);
        }
        if (node != null) {
            filterNode = (FileInfoNode)node;
            FileVcsInfo info = (FileVcsInfo)filterNode.getCookie(FileVcsInfo.class);
            info.addPropertyChangeListener(managerListener);
        } 
        else {
            filterNode = null;
        }
    }
    
    public Node getContextNode() {
        return filterNode;
    }


    /* Initilizes the view.
    */
    public void addNotify () {
        super.addNotify ();
        // run under mutex

        ExplorerManager em = ExplorerManager.find (this);

        if (em != manager) {
            if (manager != null) {
                manager.removeVetoableChangeListener (wlvc);
                manager.removePropertyChangeListener (wlpc);
            }

            manager = em;

            manager.addVetoableChangeListener(wlvc = WeakListener.vetoableChange (managerListener, manager));
            manager.addPropertyChangeListener(wlpc = WeakListener.propertyChange (managerListener, manager));

            Node[] nodes = manager.getSelectedNodes();
            if (nodes != null && nodes.length == 1 && nodes[0] instanceof FileInfoNode) {
                setContextNode(nodes[0]);
            } else {
                //TODO - put a blank node here..
                setContextNode(null);
            }
//            updateSelection();
        };
        if (!listenerActive) {
            listenerActive = true;
//            list.getSelectionModel ().addListSelectionListener (managerListener);
        }
    }

    /** Removes listeners.
    */
    public void removeNotify () {
        super.removeNotify ();
        listenerActive = false;
        if (filterNode != null) {
            FileVcsInfo info = (FileVcsInfo)filterNode.getCookie(FileVcsInfo.class);
            if (info != null) {
                info.removePropertyChangeListener(managerListener);
            }
        }
//        list.getSelectionModel ().removeListSelectionListener (managerListener);
    }

    /* Requests focus for the list component. Overrides superclass method. */
    public void requestFocus () {
//        System.out.println("requesting focus..");
        this.requestFocus();
    }

    /** This method is called when user double-clicks on some object or
    * presses Enter key.
    * @param index Index of object in current explored context
    */
    final void performNode(Node node, int modifiers) {

        // if DefaultProcessor is set, the default action is notified to it overriding the default action on nodes
        if (defaultProcessor != null) {
            defaultProcessor.actionPerformed (new ActionEvent (node, 0, null, modifiers));
            return;
        }

        // on double click - invoke default action, if there is any
        // (unless user holds CTRL key what means that we should always dive into the context)
        SystemAction sa = node.getDefaultAction ();
        if (sa != null && (modifiers & java.awt.event.InputEvent.CTRL_MASK) == 0) {
            SingleNodeView.invokeAction
                (sa, new ActionEvent (node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
        }
        // otherwise dive into the context
        else if (traversalAllowed && (!node.isLeaf()))
            manager.setExploredContext (node, manager.getSelectedNodes());
    }
    
    /** 
     * Copied from TreeView.
     * Utility method for invoking actions in separate thread. Note:
     * it uses reflection because it should work without
     * the rest of the IDE classes.
     */
    static void invokeAction(SystemAction sa, ActionEvent ev) {
        Throwable t = null;
        try {
            Class c = Class.forName("org.openide.actions.ActionManager"); // NOI18N
            Object o = org.openide.util.Lookup.getDefault ().lookup(c);
            if (o != null) {
                // lookup has found the instance
                // use reflection now
                java.lang.reflect.Method m = c.getMethod("invokeAction", // NOI18N
                    new Class[] {
                        javax.swing.Action.class,
                        java.awt.event.ActionEvent.class });
                m.invoke(o, new Object[] { sa, ev } );
                // everything went ok -->
                return;
            }
        }
        // exceptions from forName:
        catch (ClassNotFoundException x) { }
        catch (ExceptionInInitializerError x) { }
        catch (LinkageError x) {  }
        // exceptions from getMethod:
        catch (SecurityException x) { t = x; } 
        catch (NoSuchMethodException x) { t = x;}
        // exceptions from invoke
        catch (IllegalAccessException x) { t = x;} 
        catch (IllegalArgumentException x) { t = x;} 
        catch (java.lang.reflect.InvocationTargetException x) {
            t = x;
        }
        
        if (t != null) {
            ErrorManager err = (ErrorManager)
                org.openide.util.Lookup.getDefault ().lookup (ErrorManager.class);

            if (err != null) {
                err.notify(ErrorManager.INFORMATIONAL, t);
            } else {
                t.printStackTrace();
            }
        }
        // something went wrong --> invoke the action directly
        sa.actionPerformed(ev);
    }
    

    /**
     * COPIED from TreeView.
     * Method created to fix #12520. It returns false in the situation
     * where the popup is invoked on non-activated top component or
     * dialog.
     * Note: Using lookup and reflection bacause we are in standalone
     *   explorer library.
     */
    static boolean shouldPopupBeDisplayed(Component comp) {
        try {
            Class c = Class.forName("org.openide.windows.TopComponent$Registry"); // NOI18N
            Object registry = org.openide.util.Lookup.getDefault().lookup(c);
            if (registry == null) {
                // in case of standalone library we always return true
                return true;
            }
            java.lang.reflect.Method m = c.getMethod("getActivated", new Class[0]);   // NOI18N
            Object activated = m.invoke(registry, new Object[0]);
            boolean fromActivated = SwingUtilities.isDescendingFrom(comp, (Component)activated);
            if (fromActivated) {
                return true;
            }
            // check for dialogs (they are not managed by the window system)
            Window w = SwingUtilities.getWindowAncestor(comp);
            if (w instanceof Dialog) {
                return true;
            }
            return false;
        } catch (Exception x) {
            ErrorManager err = (ErrorManager)
                org.openide.util.Lookup.getDefault ().lookup (ErrorManager.class);

            if (err != null) {
                err.notify(ErrorManager.INFORMATIONAL, x);
            } else {
                x.printStackTrace();
            }
        }
        // if we had any problems it is safe to just say "popup go"
        return true;
    }
    
    

    /** Called when selection has been changed.
    */
    private void updateSelection() {
        Node[] nodes = manager.getSelectedNodes();
        if (nodes != null && nodes.length == 1 && nodes[0] instanceof FileInfoNode) {
            setContextNode(nodes[0]);
//            System.out.println("selected node=" + nodes[0].getDisplayName());
        } else {
            setContextNode(null);
            //TODO put a blank node here..
        }
    }

    // innerclasses .........................................................................

    /** Enhancement of standart JList.
    */
//    final class NbList extends AutoscrollJList {
//        static final long serialVersionUID =-7571829536335024077L;

        /**
         * Overrides JComponent's getToolTipText method in order to allow 
         * renderer's tips to be used if it has text set.
         * <p>
         * NOTE: For JTree to properly display tooltips of its renderers
         *       JTree must be a registered component with the ToolTipManager.
         *       This can be done by invoking
         *       <code>ToolTipManager.sharedInstance().registerComponent(tree)</code>.
         *       This is not done automaticly!
         *
         * @param event the MouseEvent that initiated the ToolTip display
         */
/*        public String getToolTipText (MouseEvent event) {
            if (event != null) {
                Point p = event.getPoint ();
                int row = locationToIndex (p);
                if (row >= 0) {
                    VisualizerNode v = (VisualizerNode)model.getElementAt (row);
                    String tooltip = v.shortDescription;
                    String displayName = v.displayName;
                    if ((tooltip != null) && !tooltip.equals (displayName))
                        return tooltip;
                }
            }
            return null;
        }

        public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null) {
                accessibleContext = new AccessibleExplorerList();
            }
            return accessibleContext;
        }

        private class AccessibleExplorerList extends AccessibleJList {
            public String getAccessibleName() {
                return ListView.this.getAccessibleContext().getAccessibleName();
            }
            public String getAccessibleDescription() {
                return ListView.this.getAccessibleContext().getAccessibleDescription();
            }
        }
    }
*/
    /** Popup menu listener. */
    private final class PopupAdapter extends
        org.openide.awt.MouseUtils.PopupMouseAdapter {
        protected void showPopup (MouseEvent e) {
//            System.out.println("createdpopup..");
            createPopup(e.getX(), e.getY());
        }
    } // end of PopupAdapter
    
    void createPopup(int xpos, int ypos) {
        if (manager == null || getContextNode() == null) {
            return;
        }
        if (!popupAllowed) {
            return;
        }
        
        JPopupMenu popup = NodeOp.findContextMenu(new Node[] {getContextNode()});
        if ((popup != null) && (popup.getSubElements().length > 0) && (SingleNodeView.shouldPopupBeDisplayed(SingleNodeView.this))) {
            java.awt.Point p = getVisibleRect().getLocation();
            p.x = xpos - p.x;
            p.y = ypos - p.y;
            SwingUtilities.convertPointToScreen(p, SingleNodeView.this);
            Dimension popupSize = popup.getPreferredSize ();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (p.x + popupSize.width > screenSize.width) p.x = screenSize.width - popupSize.width;
            if (p.y + popupSize.height > screenSize.height) p.y = screenSize.height - popupSize.height;
            SwingUtilities.convertPointFromScreen(p, SingleNodeView.this);
            popup.show(this, p.x, p.y);
        }
    }
    
    final class PopupPerformer implements org.openide.util.actions.ActionPerformer {
        public void performAction(SystemAction act) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Point p = SingleNodeView.this.getLocation();
                    if (p == null) {
                        return;
                    }
                    createPopup(p.x, p.y);
                }
            });
        }
    }
    
    final class FocusAdapter implements java.awt.event.FocusListener {
        
        CallbackSystemAction csa;
        PopupPerformer performer;

        public void focusGained(java.awt.event.FocusEvent ev) {
            if (csa == null) {
                try {
                    Class popup = Class.forName("org.openide.actions.PopupAction"); // NOI18N
                    csa = (CallbackSystemAction) CallbackSystemAction.get(popup);
                    performer = new PopupPerformer();
                } catch (ClassNotFoundException e) {
                    Error err = new NoClassDefFoundError();
                    ErrorManager em = (ErrorManager)
                        org.openide.util.Lookup.getDefault ().lookup (ErrorManager.class);
                    if (em != null) {
                        em.annotate(err, e);
                    }
                    throw err;
                }
            }
            csa.setActionPerformer(performer);
            //ev.consume();
        }
        
        public void focusLost(java.awt.event.FocusEvent ev) {
            if (csa != null && (csa.getActionPerformer() instanceof PopupPerformer)) {
                csa.setActionPerformer(null);
            }
        }
    }

    /**
    */
    private final class Listener extends MouseAdapter
    implements 
    PropertyChangeListener, VetoableChangeListener {
        
        public void mouseClicked(MouseEvent e) {
            if (MouseUtils.isDoubleClick(e)) {
                Node nd = getContextNode();
                if (nd != null) {
                    performNode(nd, e.getModifiers());
                }
            }
        }


        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (manager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] newNodes = (Node[])evt.getNewValue();
                if (!selectionAccept (newNodes)) {
                    throw new PropertyVetoException("", evt); // NOI18N
                }
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (manager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                updateSelection();
                return;
            }
            if (evt.getSource() != null && evt.getSource() instanceof FileVcsInfo) {
                setContextNode(getContextNode());
//                updateSelection();
            }
        }

    }

    // Backspace jumps to parent folder of explored context
/*    private final class GoUpAction extends AbstractAction {
        static final long serialVersionUID =1599999335583246715L;
        public GoUpAction () {
            super ("GoUpAction"); // NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            if (traversalAllowed) {
                Node pan = manager.getExploredContext();
                pan = pan.getParentNode();
                if (pan != null)
                    manager.setExploredContext(pan, manager.getSelectedNodes());
            }
        }
        public boolean isEnabled() {
            return true;
        }
    }
 */

    //Enter key performObjectAt selected index.
    private final class EnterAction extends AbstractAction {
        static final long serialVersionUID =-239805141416294016L;
        public EnterAction () {
            super ("Enter"); // NOI18N
        }

        public void actionPerformed(ActionEvent e) {
            Node nd = getContextNode();
            if (nd != null) {
                performNode(nd, e.getModifiers());
            }
        }
        public boolean isEnabled() {
            return true;
        }
    }
}
