/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.examples.careditor.actions;

import org.netbeans.api.dynactions.GenericContextSensitiveAction;
import org.netbeans.examples.careditor.file.PersonRemover;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class DeletePersonAction extends GenericContextSensitiveAction<PersonRemover> {
    
    public DeletePersonAction() {
        super (PersonRemover.class);
        init();
    }
    
    public DeletePersonAction(Lookup lkp) {
        super (lkp, PersonRemover.class);
        init();
    }

    @Override
    protected void performAction(PersonRemover t) {
        t.remove();
    }

    private void init() {
        setDisplayName(NbBundle.getMessage(DeletePersonAction.class, 
                "ACTION_Delete_Person")); //NOI18N
    }
}
