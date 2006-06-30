/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.latex.editor.ActionsFactory;
import org.netbeans.modules.latex.editor.TexKit;
import org.netbeans.modules.latex.loaders.TexCloneableEditorCreatorJustForUI;
import org.netbeans.modules.latex.loaders.TexEditorSupport;
import org.openide.text.CloneableEditor;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.latex.ui.actions.CiteAction;
import org.netbeans.modules.latex.ui.actions.CountWordsAction;
import org.netbeans.modules.latex.ui.actions.RefAction;
import org.netbeans.modules.latex.ui.palette.IconNode;
import org.netbeans.modules.latex.ui.palette.RootNode;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jan Lahoda
 */
public class TexCloneableEditor extends CloneableEditor implements FocusListener, PropertyChangeListener {
    public TexCloneableEditor() {
        super();
        prepareActions();
    }
    
    public TexCloneableEditor(TexEditorSupport support) {
        super(support);
        prepareActions();
    }
    
    public JEditorPane getPane() {
        return pane;
    }
    
//    protected void componentActivated() {
//        System.err.println("TexCloneableEditor.componentActivated");
//	new Exception().printStackTrace(System.err);
//    }
    
    protected void componentShowing() {
//        System.err.println("TexCloneableEditor.componentShowing");
//	new Exception().printStackTrace(System.err);
        super.componentShowing();
        pane.addFocusListener(this);
    }
    
    private void bindAction(Class systemAction, String editorAction) {
        CallbackSystemAction cite = (CallbackSystemAction) SystemAction.get(systemAction);
        Class kitClazz = TexKit.class;
        Action editorCite = TexKit.getKit(kitClazz).getActionByName(editorAction);
        
        getActionMap().put(cite.getActionMapKey(), new ActionWrapper(this, editorCite));
    }
    
    private void prepareActions() {
        //There was a deadlock during startup, trying to workaround it:
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                bindAction(CiteAction.class, ActionsFactory.CITE_ACTION);
                bindAction(RefAction.class, ActionsFactory.REF_ACTION);
                bindAction(CountWordsAction.class, ActionsFactory.WORD_COUNT_ACTION);
            }
        });
    }
    
    public void focusGained(FocusEvent e) {
/*        System.err.println("TexCloneableEditor.focusGained");
	new Exception().printStackTrace(System.err);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.err.println("XXXXXXXXXXXXXXXX");
                try {
                    Method m = TexCloneableEditor.this.getClass().getMethod("requestActive", new Class[0]);
                    
                    m.invoke(TexCloneableEditor.this, new Object[0]);
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    
                    requestFocus();
                }
            }
        });*/
    }
    
    public void focusLost(FocusEvent e) {
    }

    private static Map<PaletteController, Boolean> controllers = new WeakHashMap<PaletteController, Boolean>();
    private static RootNode rootNode;

    private static synchronized RootNode getRootNode() {
        if (rootNode == null) {
            rootNode = new RootNode();
        }

        return rootNode;
    }

    private PaletteController pc;
    private MyProxyLookup lookup = null;
    
    public synchronized Lookup getLookup() {
        if (lookup == null) {
            Lookup sup = super.getLookup();
            lookup = new MyProxyLookup();
            
            Lookup palette = null;
            
            pc = PaletteFactory.createPalette(getRootNode(), new PaletteActionsImpl());

            controllers.put(pc, Boolean.TRUE);
            pc.addPropertyChangeListener(this);
            palette = Lookups.fixed(new Object[] {pc});
            
//            Lookup f = Lookups.fixed(new Object[] {
//                ((TexEditorSupport) cloneableEditorSupport()).getDataObject(),
//                ((TexEditorSupport) cloneableEditorSupport()).getDataObject().getNodeDelegate(),
//            });
            
            lookup.setLookupsInternal(new Lookup[] {sup, palette, /*f*/});
        }
        
        return lookup;
    }
    
    private static class MyProxyLookup extends ProxyLookup {
        public void setLookupsInternal(Lookup[] l) {
            super.setLookups(l);
        }
    }
    
    private static class ActionWrapper extends AbstractAction {
        
        private TexCloneableEditor tce;
        private Action             delegate;
        
        public ActionWrapper(TexCloneableEditor tce, Action delegate) {
            this.tce = tce;
            this.delegate = delegate;
        }
        
        public void actionPerformed(ActionEvent e) {
//            System.err.println("ActionWrapper.actionPerformed");
            final ActionEvent event = new ActionEvent(tce.getPane(), e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
            
            delegate.actionPerformed(event);
        }
        
    }
    
    public static class Creator implements TexCloneableEditorCreatorJustForUI {
        
        public CloneableEditor createCloneableEditor(TexEditorSupport sup) {
            return new TexCloneableEditor(sup);
        }
        
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (pc != null && PaletteController.PROP_SELECTED_ITEM.equals(evt.getPropertyName())) {
            final JTextComponent target = UIUtilities.getCurrentEditorPane();
            
            if (target != getPane())
                return ;
            
            IconNode in = (IconNode) pc.getSelectedItem().lookup(IconNode.class);
            
            if (in == null)
                return ;
            
            final String command = in.getCommand();
            
            if (command == null)
                return ;
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    requestActive();
                    target.requestFocusInWindow();
                    target.requestFocus();
                    
                    CodeTemplate template = CodeTemplateManager.get(target.getDocument()).createTemporary(command);
                    
                    template.insert(target);
                }
            });
            
            pc.clearSelection();
            
        }
    }

    public static void refresh() {
        for (PaletteController pc : controllers.keySet()) {
            pc.refresh();
        }
    }
    
    private static final class PaletteActionsImpl extends PaletteActions {
        
        public Action[] getImportActions() {
            return new Action[0];
        }
        
        public Action[] getCustomPaletteActions() {
             return new Action[0];
       }
        
        public Action[] getCustomCategoryActions(Lookup category) {
            return new Action[0];
        }
        
        public Action[] getCustomItemActions(Lookup item) {
            return new Action[0];
        }
        
        public Action getPreferredAction(Lookup item) {
            return null;
        }
        
    }
    
}
