/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.poasupport.nodes;

import java.io.IOException;
import java.awt.Dialog;
import java.awt.Component;
import java.util.*;
import java.beans.PropertyEditor;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.RequestProcessor;
import org.openide.DialogDescriptor;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.settings.*;
import org.netbeans.modules.corba.poasupport.*;
import org.netbeans.modules.corba.poasupport.tools.*;

/** A node representing POA.
 *
 * @author Dusan Balek
 */
public class POANode extends AbstractNode implements java.beans.PropertyChangeListener {
    
    public static final String ICON_BASE =
    "/org/netbeans/modules/corba/poasupport/resources/POANodeIcon"; // NOI18N
    
    private static final SystemAction[] DEFAULT_ACTIONS = new SystemAction[] {
        SystemAction.get (NewAction.class),
        null,
        SystemAction.get (DeleteAction.class),
        SystemAction.get (RenameAction.class),
        null,
//        SystemAction.get (CutAction.class),
//        SystemAction.get (CopyAction.class),
//        SystemAction.get (PasteAction.class),
        SystemAction.get (PropertiesAction.class)
    };
    
    private static final SystemAction[] ROOT_POA_DEFAULT_ACTIONS = new SystemAction[] {
        SystemAction.get (NewAction.class),
        SystemAction.get (PropertiesAction.class)
    };
    
    private static final SystemAction[] NON_WRITEABLE_DEFAULT_ACTIONS = new SystemAction[] {
        SystemAction.get (PropertiesAction.class)
    };
    
    public POANode(POAChildren _children) {
        super (_children);
        setIconBase (ICON_BASE);
        CookieSet cookie = getCookieSet ();
        cookie.add (getPOAElement().getOpenCookie());
        super.setName (getPOAElement().getPOAName());
        setActions();
        getPOAElement().addPropertyChangeListener(this);
    }
    
    /** Set all actions for this node.
     * @param actions new list of actions
     */
    public void setActions() {
        if (!isWriteable())
            systemActions = NON_WRITEABLE_DEFAULT_ACTIONS;
        else if (isRootPOA())
            systemActions = ROOT_POA_DEFAULT_ACTIONS;
        else
            systemActions = DEFAULT_ACTIONS;
    }
    
    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        getPOAElement().setLinePosition();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // When you have help, change to:
        // return new HelpCtx (POANode.class);
    }
    
    public boolean isWriteable() {
        return getPOAElement().isWriteable();
    }
    
    protected POAChildren getPOAChildren () {
        return (POAChildren) getChildren ();
    }
    
    public POAElement getPOAElement () {
        return getPOAChildren().getPOAElement();
    }
    
    public boolean isRootPOA() {
        return getPOAElement().isRootPOA();
    }
    
    // Permit new subnodes to be created:
    public NewType[] getNewTypes () {
        Vector ret = new Vector();
        ret.add (new NewType () {
            public String getName () {
                return POASupport.getString("LBL_NewType_ChildPOA");
            }
            // If you have help:
            // public HelpCtx getHelpCtx () {
            //     return POANode.class.getName () + ".newType";
            // }
            public void create () throws IOException {
                POAElement parentElement = getPOAElement();
                POAElement newElement = new POAElement(parentElement, parentElement.getRootPOA(), isWriteable());
                DialogDescriptor dd;
                Dialog dialog;
                dd = new DialogDescriptor( new POACustomizer(newElement),
                    POASupport.getString("CTL_TITLE_CreatePOA"));  // Title
                dialog = TopManager.getDefault().createDialog( dd );
                dialog.show ();
                if ( dd.getValue().equals( NotifyDescriptor.OK_OPTION ) ) {
                    getPOAElement().addChildPOA(newElement);
                    ((POAChildren)getChildren ()).addNotify ();
                }
            }
        });
        if (getPOAElement().getPOAActivator() == null)
            ret.add (new NewType () {
                public String getName () {
                    return POASupport.getString("LBL_NewType_POAActivator");
                }
                // If you have help:
                // public HelpCtx getHelpCtx () {
                //     return POANode.class.getName () + ".newType";
                // }
                public void create () throws IOException {
                    POAElement parentElement = getPOAElement();
                    POAActivatorElement newElement = new POAActivatorElement(parentElement, isWriteable());
                    DialogDescriptor dd;
                    Dialog dialog;
                    dd = new DialogDescriptor( new POAMemberCustomizer(newElement),
                    POASupport.getString("CTL_TITLE_CreatePOAActivator"));  // Title
                    dialog = TopManager.getDefault().createDialog( dd );
                    dialog.show ();
                    if ( dd.getValue().equals( NotifyDescriptor.OK_OPTION ) ) {
                        getPOAElement().setPOAActivator(newElement);
                        ((POAChildren)getChildren ()).addNotify ();
                    }
                }
            });
        if (!POAChecker.checkDisabledServantActivation(getPOAElement(), getPOAElement().getPolicies()).equals(POASettings.ALL_SERVANTS))
            ret.add(new NewType() {
                public String getName () {
                    return POASupport.getString("LBL_NewType_Servant");
                }
                // If you have help:
                // public HelpCtx getHelpCtx () {
                //     return POANode.class.getName () + ".newType";
                // }
                public void create () throws IOException {
                    POAElement parentElement = getPOAElement();
                    ServantElement newElement = new ServantElement(parentElement, isWriteable());
                    DialogDescriptor dd;
                    Dialog dialog;
                    dd = new DialogDescriptor( new POAMemberCustomizer(newElement),
                    POASupport.getString("CTL_TITLE_CreateServant"));  // Title
                    dialog = TopManager.getDefault().createDialog( dd );
                    dialog.show ();
                    if ( dd.getValue().equals( NotifyDescriptor.OK_OPTION ) ) {
                        getPOAElement().addServant(newElement);
                        ((POAChildren)getChildren ()).addNotify ();
                    }
                }
            });
        if ((POAChecker.isServantManagerEnabled(getPOAElement(), getPOAElement().getPolicies()))&&(getPOAElement().getServantManager() == null))
            ret.add(new NewType() {
                public String getName () {
                    return POASupport.getString("LBL_NewType_ServantManager");
                }
                // If you have help:
                // public HelpCtx getHelpCtx () {
                //     return POANode.class.getName () + ".newType";
                // }
                public void create () throws IOException {
                    POAElement parentElement = getPOAElement();
                    ServantManagerElement newElement = new ServantManagerElement(parentElement, isWriteable());
                    DialogDescriptor dd;
                    Dialog dialog;
                    dd = new DialogDescriptor( new POAMemberCustomizer(newElement),
                    POASupport.getString("CTL_TITLE_CreateServantManager"));  // Title
                    dialog = TopManager.getDefault().createDialog( dd );
                    dialog.show ();
                    if ( dd.getValue().equals( NotifyDescriptor.OK_OPTION ) ) {
                        getPOAElement().setServantManager(newElement);
                        ((POAChildren)getChildren ()).addNotify ();
                    }
                }
            });
        if ((POAChecker.isDefaultServantEnabled(getPOAElement(), getPOAElement().getPolicies()))&&(getPOAElement().getDefaultServant() == null))
            ret.add(new NewType() {
                public String getName () {
                    return POASupport.getString("LBL_NewType_DefaultServant");
                }
                // If you have help:
                // public HelpCtx getHelpCtx () {
                //     return POANode.class.getName () + ".newType";
                // }
                public void create () throws IOException {
                    POAElement parentElement = getPOAElement();
                    DefaultServantElement newElement = new DefaultServantElement(parentElement, isWriteable());
                    DialogDescriptor dd;
                    Dialog dialog;
                    dd = new DialogDescriptor( new POAMemberCustomizer(newElement),
                    POASupport.getString("CTL_TITLE_CreateDefaultServant"));  // Title
                    dialog = TopManager.getDefault().createDialog( dd );
                    dialog.show ();
                    if ( dd.getValue().equals( NotifyDescriptor.OK_OPTION ) ) {
                        getPOAElement().setDefaultServant(newElement);
                        ((POAChildren)getChildren ()).addNotify ();
                    }
                 }
              });
        NewType[] nt = new NewType[ret.size()];
        for (int i = 0; i < ret.size(); i++)
            nt[i] = (NewType)ret.get(i);
        return nt;
    }
    
    // Handle deleting:
    
    public boolean canDestroy () {
        return (!isRootPOA()) && isWriteable();
    }
    
    public void destroy () throws IOException {
        if ((!isRootPOA())&& isWriteable()) {
            if (POAChecker.canDeletePOA(getPOAElement())) {
                ((POANode)getParentNode()).getPOAElement().removeChildPOA(getPOAElement());
                ((POAChildren)((POANode)getParentNode()).getChildren ()).addNotify ();
                super.destroy ();
            }
        }
    }
    
    // Handle renaming:
    public boolean canRename () {
        return (!isRootPOA()) && isWriteable();
    }
    
    public void setName (String nue) {
        if ((!isRootPOA()) && isWriteable()) {
            if (POAChecker.checkPOAName(nue, getPOAElement(), true))
                getPOAElement().setPOAName(nue);
        }
    }
    
    // Handle copying and cutting specially:
/*    public boolean canCopy () {
        return true;
    }
    
    public boolean canCut () {
        return true;
    }
*/    
    // Create a property sheet:
    protected Sheet createSheet () {
        Sheet sheet = super.createSheet ();
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        if (ps == null) {
            ps = Sheet.createPropertiesSet ();
            sheet.put (ps);
        }
        ps.put (new PropertySupport.ReadWrite ("name", String.class, POASupport.getString("LBL_POASheet_Name"), POASupport.getString("MSG_POASheet_Name")) { // NOI18N
            public Object getValue () {
                return getPOAElement().getPOAName ();
            }
            public void setValue(Object value) {
                if (POAChecker.checkPOAName((String)value, getPOAElement(), false))
                    getPOAElement().setPOAName((String)value);
            }
            public boolean canWrite() {
                return (!isRootPOA()) && isWriteable();
            }
        });
        ps.put(new PropertySupport.ReadWrite ("var", String.class, POASupport.getString("LBL_POASheet_Var"), POASupport.getString("MSG_POASheet_Var")) { // NOI18N
            public Object getValue () {
                return getPOAElement().getVarName ();
            }
            public void setValue(Object value) {
                if (POAChecker.checkPOAVarName((String)value, getPOAElement(), false))
                    getPOAElement().setVarName((String)value);
            }
            public boolean canWrite() {
                return isWriteable();
            }
        });
        ps.put(new PropertySupport.ReadWrite ("manager", String.class, POASupport.getString("LBL_POASheet_Mgr"), POASupport.getString("MSG_POASheet_Mgr")) { // NOI18N
            public Object getValue () {
                String value = getPOAElement().getManager();
                if (value == null)
                    value = POASupport.getString("FMT_DefaultPOAManagerName");
                return value;
            }
            public void setValue(Object value) {
                if (value.equals(POASupport.getString("FMT_DefaultPOAManagerName")))
                    value = null;
                getPOAElement().setManager((String)value);
            }
            public boolean canWrite() {
                return (!isRootPOA()) && isWriteable();
            }
            public PropertyEditor getPropertyEditor() {
                Vector _values = getPOAElement().getAvailablePOAManagers();
                _values.add(POASupport.getString("FMT_DefaultPOAManagerName"));
		return new ComboStringsPropertyEditor (_values);
	    }
        });
        if (isRootPOA())
            ps.put(new PropertySupport.ReadWrite ("orb", String.class, POASupport.getString("LBL_POASheet_ORBVarName"), POASupport.getString("MSG_POASheet_ORBVarName")) { // NOI18N
                public Object getValue () {
                    return ((RootPOAElement)getPOAElement()).getORBVarName ();
                }
                public void setValue(Object value) {
                    ((RootPOAElement)getPOAElement()).setORBVarName((String)value);
                }
                public boolean canWrite() {
                    return isWriteable();
                }
            });

        ps = new Sheet.Set ();
	ps.setName("POAPolicies"); // NOI18N
	ps.setDisplayName (POASupport.getString("LBL_POASheet_Policies"));
	ps.setShortDescription (POASupport.getString("MSG_POASheet_Policies"));
        final POASettings _ps;
        String _tag = getPOAElement().getRootPOA().getORBTag();
        if (_tag != null)
            _ps = POASupport.getCORBASettings().getSettingByTag(_tag).getPOASettings();
        else
            _ps = POASupport.getPOASettings();
        if (_ps != null) {
            ListIterator policyList = _ps.getPolicies().listIterator();
            while (policyList.hasNext()) {
                final String policyName = ((POAPolicyDescriptor)policyList.next()).getName();
                ps.put (new PropertySupport.ReadWrite (policyName, String.class, policyName, policyName) {
                    public Object getValue () {
                        Properties policies = getPOAElement().getPolicies();
                        Object ret = policies.get(policyName);
                        if (ret == null) {
                            List _values = _ps.getPolicyByName(policyName).getValues();
                            if (_values.size() > 0)
                                ret = ((POAPolicyValueDescriptor)_values.get(0)).getName();
                            else
                                ret = "";
                        }
                        return ret;
                    }
                    public void setValue(Object value) {
                        Properties policies = getPOAElement().getPolicies();
                        String old_value = policies.getProperty(policyName);
                        if (old_value == null) {
                            List _values = _ps.getPolicyByName(policyName).getValues();
                            if (_values.size() > 0)
                                old_value = ((POAPolicyValueDescriptor)_values.get(0)).getName();
                            else
                                old_value = "";
                        }
                        if (value.equals(old_value))
                            return;
                        if (POAChecker.checkPOAPoliciesChange(getPOAElement(), policies, policyName, (String)value, false))
                            getPOAElement().setPolicies(policies);
                    }
                    public boolean canWrite() {
                        return (!isRootPOA()) && isWriteable();
                    }
		    public PropertyEditor getPropertyEditor() {
                        ListIterator values = _ps.getPolicyByName(policyName).getValues().listIterator();
                        Vector _values = new Vector();
                        while (values.hasNext())
                            _values.add(((POAPolicyValueDescriptor)values.next()).getName());
		        return new ComboStringsPropertyEditor (_values);
		    }
                });
            }
        }
        sheet.put(ps);
        
        if (isRootPOA()) {
            ps = new Sheet.Set ();
	    ps.setName("ORB"); // NOI18N
	    ps.setDisplayName (POASupport.getString("LBL_POASheet_ORB"));
	    ps.setShortDescription (POASupport.getString("MSG_POASheet_ORB"));
            ps.put(new PropertySupport.ReadWrite ("orb", String.class, POASupport.getString("LBL_POASheet_ORB"), POASupport.getString("MSG_POASheet_ORB")) { // NOI18N
                public Object getValue () {
                    String _tag2 = ((RootPOAElement)getPOAElement()).getORBTag();
                    if (_tag2 != null)
                        return POASupport.getCORBASettings().getSettingByTag(_tag2).getName();
                    else
                        return POASupport.getString("FMT_Unknown_ORB");
                }
                public void setValue(Object value) {
                    ORBSettings _os = POASupport.getCORBASettings().getSettingByName((String)value);
                    if (_os != null)
                        ((RootPOAElement)getPOAElement()).setORBTag(_os.getORBTag());
                }
                public boolean canWrite() {
                        return (((RootPOAElement)getPOAElement()).getORBTag() == null) ? false : true;
                }
		public PropertyEditor getPropertyEditor() {
		    return new OrbPropertyEditor ();
		}
            });
            sheet.put(ps);
        }

        return sheet;
    }
    
    // Permit user to customize whole node at once (instead of per-property):
    public boolean hasCustomizer () {
        return (!isRootPOA()) && isWriteable();
    }
    
    public Component getCustomizer () {
        return new POACustomizer (getPOAElement());
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent p1) {
        if (p1.getSource() == null)
            return;
        if (p1.getSource() != getPOAElement())
            return;
        if (POAElement.PROP_POA_NAME.equals(p1.getPropertyName())) {
            super.setName((String)p1.getNewValue());
            return;
        }
        if (POAElement.PROP_POLICIES.equals(p1.getPropertyName()))
            getPOAChildren().addNotify();
        super.firePropertyChange(p1.getPropertyName(), p1.getOldValue(), p1.getNewValue());
    }
    
    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    /*
    public Node cloneNode () {
        // Try to pass in similar constructor params to what you originally got:
        return new POANode ();
    }
     */
    
    // Permit things to be pasted into this node:
    /*
    protected void createPasteTypes (final Transferable t, List l) {
        // Make sure to pick up super impl, which adds intelligent node paste type:
        super.createPasteTypes (t, l);
        if (t.isDataFlavorSupported (DataFlavor.stringFlavor)) {
            l.add (new PasteType () {
                    public String getName () {
                        return NbBundle.getMessage (POANode.class, "LBL_PasteType");
                    }
                    // If you have help:
                    // public HelpCtx getHelpCtx () {
                    //     return POANode.class.getName () + ".pasteType";
                    // }
                    public Transferable paste () throws IOException {
                        try {
                            String data = (String) t.getTransferData (DataFlavor.stringFlavor);
                            // Or, you can look for nodes and related things in the transferable, using e.g.:
                            // Node n = NodeTransfer.node (t, NodeTransfer.COPY);
                            // Node[] ns = NodeTransfer.nodes (t, NodeTransfer.MOVE);
                            // MyCookie cookie = (MyCookie) NodeTransfer.cookie (t, NodeTransfer.COPY, MyCookie.class);
                            // do something, e.g.:
                            getPOAChildren ().addKey (data);
                            // Throw an IOException if you are creating an underlying
                            // object and this fails.
                            // To leave the clipboard as is:
                            return null;
                            // To clear the clipboard:
                            // return ExTransferable.EMPTY;
                        } catch (UnsupportedFlavorException ufe) {
                            // Should not happen, since t said it supported this flavor, but:
                            throw new IOException (ufe.getMessage ());
                        }
                    }
                });
        }
    }
     */
    
/*
     public Transferable clipboardCopy () {
        // Add to, do not replace, the default node copy flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCopy ());
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return POANode.this.getDisplayName ();
                }
            });
        return et;
    }
    public Transferable clipboardCut () {
        // Add to, do not replace, the default node cut flavor:
        ExTransferable et = ExTransferable.create (super.clipboardCut ());
        // This is not so useful because this node will not be destroyed afterwards
        // (it is up to the paste type to decide whether to remove the "original",
        // and it is not safe to assume that getData will only be called once):
        et.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
                protected Object getData () {
                    return POANode.this.getDisplayName ();
                }
            });
        return et;
    }
 */
    
    
    // Permit node to be reordered (you may also want to put
    // MoveUpAction and MoveDownAction on the subnodes, if you can,
    // but ReorderAction on the parent is enough):
    /*
    private class ReorderMe extends Index.Support {
     
        public Node[] getNodes () {
            return POANode.this.getChildren ().getNodes ();
        }
     
        public int getNodesCount () {
            return getNodes ().length;
        }
     
        // This assumes that there is exactly one child node per key.
        // If you are using e.g. Children.Array, you can use shortcut implementations
        // of the Index cookie.
        public void reorder (int[] perm) {
            // Remember: {2, 0, 1} cycles three items forwards.
            List old = POANode.this.getPOAChildren ().myKeys;
            if (list.size () != perm.length) throw new IllegalArgumentException ();
            List nue = new ArrayList (perm.length);
            for (int i = 0; i < perm.length; i++)
                nue.set (i, old.get (perm[i]));
            POANode.this.getPOAChildren ().setKeys (nue);
        }
     
    }
     */
    
}
