/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import org.netbeans.modules.latex.bibtex.loaders.BiBTexDataObject;
import org.openide.ErrorManager;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.cookies.EditorCookie;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.NodeTableModel;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jan Lahoda
 */
public class OpenBiBComponent implements BiBTexModelChangeListener {
    
    private BiBTexDataObject od;
    private TopComponent tc;
    
    /** Creates a new instance of BiBTest */
    private OpenBiBComponent(BiBTexDataObject od) {
        this.od = od;
        if (od == null)
            throw new NullPointerException();
        
        BiBTeXModel.getModel(od.getPrimaryFile()).addBiBTexModelChangeListener(this);
    }
    
    private static Map/*<DataObject, OpenBiBComponent>*/ file2TC = null;
    
    public static synchronized void open(BiBTexDataObject od) {
        file2TC = new HashMap();
        
        OpenBiBComponent open = (OpenBiBComponent) file2TC.get(od);
        
        if (open == null) {
            open = new OpenBiBComponent(od);
            
            file2TC.put(od, open);
            open.tc = open.doOpen();
        }
        
        open.tc.requestActive();
    }
//    public static final void main(String[] args) {
//        new OpenBiBComponent().open();
//    }
    /**
     * @param args the command line arguments
     */
    private TopComponent doOpen() {
//        try {
        
        TreeTableView ttv = new TreeTableView();
        
        ExplorerPanel p = new ExplorerPanel();
        
        Node rootNode = getTheMainNode();
        
        p.getExplorerManager().setRootContext(rootNode);
        
        ttv.setRootVisible(false);
        Node.PropertySet[] sets = ((Node)rootNode.getChildren().getNodes()[0]).getPropertySets();
        
        Node.Property[] properties = sets[0].getProperties();
        Node.Property[] newProperties = new Node.Property[properties.length];
        
        for (int cntr = 0; cntr < properties.length; cntr++) {
            newProperties[cntr] = properties[cntr];
            newProperties[cntr].setValue("ComparableColumnTTV", Boolean.TRUE);
        }
        
        ttv.setProperties(newProperties);
        
        p.add(ttv);
        
        Mode mode = WindowManager.getDefault().findMode("output");
        
        mode.dockInto(p);
        
        p.setDisplayName("BiBTeX - " + od.getName());
        
        p.open();
        
        return p;
//        ttv.sh
//        } catch (IntrospectionException ex) {
//            ErrorManager.getDefault().notify(ex);
//            
//            return null;
//        } catch (IOException ex) {
//            ErrorManager.getDefault().notify(ex);
//        }
    }
    
    private void update() {
//        System.err.println("update:");
//        new Exception().printStackTrace();
        if (tc == null)
            return ;
        
        ExplorerPanel ep = (ExplorerPanel) tc;
        
        ep.getExplorerManager().setRootContext(getTheMainNode());
    }
    
    public void entriesAdded(Collection entries) {
        update();
    }
    
    public void entriesRemoved(Collection entries) {
        update();
    }
    
    private Node getTheMainNode() {
        List coll = BiBTeXModel.getModel(od.getPrimaryFile()).getEntries();

        Collection nodes = new ArrayList();
        
        for (Iterator i = coll.iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            
//            System.err.println("e = " + e );
            if (e instanceof PublicationEntry) {
                try {
                    nodes.add(new EntryNode((PublicationEntry) e));
                } catch (IntrospectionException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
        
        return new RootNode(nodes);
    }
    
    private static class RootNode extends AbstractNode {
        public RootNode(Collection nodes) {
            super(new RootChildren(nodes));
        }
    }
    
    private static class RootChildren extends Children.Keys {
        
        public RootChildren(Collection nodes) {
            setKeys(nodes);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] {(Node) key};
        }
        
    }
    
//    private static class MyTreeTableView extends TreeTableView {
//        public MyTreeTableView() {
//            Column
//        }
//    }
    
    private /*static*/ class EntryNode extends BeanNode {
        public EntryNode(PublicationEntry entry) throws IntrospectionException {
            super(entry, Children.LEAF);
            
            getCookieSet().add(entry);
        }
        
        public Action[] getActions(boolean context) {
            if (context) {
                return getContextActions();
            } else {
                return new Action[] {
                    new EditEntryAction(),
                    SystemAction.get(DeleteAction.class),
                    null,
                    SystemAction.get(PropertiesAction.class)
                };
            }
        }
        
        public void destroy() throws IOException {
            BiBTeXModel.getModel(od.getPrimaryFile()).removeEntry((Entry) getBean());
            super.destroy();
        }
    }
    
    private static class ROProperty extends PropertySupport.ReadOnly {
        
        private Node.Property property;
        
        public ROProperty(Node.Property property) {
            super(property.getName(), property.getValueType(), property.getDisplayName(), property.getShortDescription());
            this.property = property;
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return property.getValue();
        }
        
    }
}
