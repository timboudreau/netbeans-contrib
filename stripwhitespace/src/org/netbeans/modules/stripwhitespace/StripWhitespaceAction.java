/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.stripwhitespace;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.openide.util.NbBundle;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.openide.ErrorManager;
import org.openide.util.WeakListeners;

public final class StripWhitespaceAction extends AbstractAction implements ChangeListener {
    
    public StripWhitespaceAction() {
        putValue (Action.NAME, NbBundle.getMessage(StripWhitespaceAction.class, 
                "LBL_Action"));
        Registry.addChangeListener (WeakListeners.change(this, Registry.class));
    }

    public void actionPerformed(ActionEvent e) {
        BaseDocument d = getCurrentDocument();
        if (d != null) {
            d.runAtomicAsUser(new Stripper(d));
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private BaseDocument getCurrentDocument() {
        JTextComponent nue = Registry.getMostActiveComponent();
        Document d = nue.getDocument();
        if (d instanceof BaseDocument) {
            return (BaseDocument) d;
        } else {
            return null;
        }
    }

    public void stateChanged(ChangeEvent e) {
        setEnabled(getCurrentDocument() != null);
    }
    
    private static final class Stripper implements Runnable {
        private final BaseDocument d;
        public Stripper (BaseDocument d) {
            this.d = d;
        }
        
        public void run() {
            int ct = d.getDefaultRootElement().getElementCount();
            System.err.println(ct + " elements to strip length " + d.getLength() );
            try {
                for (int i=ct-1; i >=0; i--) {
                    Element curr = d.getDefaultRootElement().getElement(i);
                    String s = d.getText(curr.getStartOffset(), curr.getEndOffset() - curr.getStartOffset());
                    int toRemove = 0;
                    for (int j=s.length()-1; j >= 0; j--) {
                        if (Character.isWhitespace(s.charAt(j))) {
                            toRemove++;
                        } else {
                            break;
                        }
                    }
                    if (toRemove > 0) {
                        if (curr.getEndOffset() < d.getLength()) {
                            System.err.println("Remove from " + (curr.getEndOffset() - (toRemove)) + " " + toRemove + " chars ");
                            d.remove(curr.getEndOffset() - (toRemove), toRemove-1);
                        }
                    }
                }
            } catch (BadLocationException e) {
                //rollback somehow?
                ErrorManager.getDefault().notify(e);
            }
        }
    }
}
