/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the DocSup module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.latex.editor.ActionsFactory;
import org.netbeans.modules.latex.editor.TexKit;
import org.netbeans.modules.latex.loaders.TexCloneableEditorCreatorJustForUI;
import org.netbeans.modules.latex.loaders.TexEditorSupport;
import org.openide.text.CloneableEditor;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.SystemAction;

import org.netbeans.modules.latex.ui.actions.CiteAction;
import org.netbeans.modules.latex.ui.actions.CountWordsAction;
import org.netbeans.modules.latex.ui.actions.RefAction;

/**
 *
 * @author Jan Lahoda
 */
public class TexCloneableEditor extends CloneableEditor implements FocusListener {
    public TexCloneableEditor() {
        super();
        prepareActions();
//        TexGroupActivator.install();
    }
    
    public TexCloneableEditor(TexEditorSupport support) {
        super(support);
        prepareActions();
//        TexGroupActivator.install();
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
        TexGroupActivator.install();
        pane.addFocusListener(this);
    }
    
    private void bindAction(Class systemAction, String editorAction) {
        CallbackSystemAction cite = (CallbackSystemAction) SystemAction.get(systemAction);
        Class kitClazz = TexKit.class;
        Action editorCite = TexKit.getKit(kitClazz).getActionByName(editorAction);
        
        getActionMap().put(cite.getActionMapKey(), new ActionWrapper(this, editorCite));
    }
    
    private void prepareActions() {
        bindAction(CiteAction.class, ActionsFactory.CITE_ACTION);
        bindAction(RefAction.class, ActionsFactory.REF_ACTION);
        bindAction(CountWordsAction.class, ActionsFactory.WORD_COUNT_ACTION);
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
    
}
