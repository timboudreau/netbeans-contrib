/*
 * ThemeEditorAction.java
 *
 * Created on Jul 2, 2007, 3:10:36 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class ThemeEditorAction extends AbstractAction {

    public ThemeEditorAction() {
        putValue (Action.NAME, NbBundle.getMessage(ThemeEditorAction.class,
                "LBL_EDIT_ACTION")); //NOI18N
    }

    public void actionPerformed(ActionEvent e) {
        TC editor = null;
        for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
            if (tc instanceof TC) {
                editor = (TC) tc;
                break;
            }
        }
        if (editor == null) {
            editor = new TC();
        }
        editor.open();
        editor.requestActive();
    }

    private static final class TC extends TopComponent {
        TC () {
            setLayout (new BorderLayout());
            add (customizer, BorderLayout.CENTER);
            setDisplayName(NbBundle.getMessage(ThemeEditorAction.class,
                "LBL_EDITOR"));
        }

        @Override
        protected void componentActivated() {
            getComponents()[0].requestFocus();
        }

        @Override
        public int getPersistenceType() {
            return PERSISTENCE_NEVER;
        }

        private final ColorsCustomizer customizer = new
                ColorsCustomizer();
    }

}
