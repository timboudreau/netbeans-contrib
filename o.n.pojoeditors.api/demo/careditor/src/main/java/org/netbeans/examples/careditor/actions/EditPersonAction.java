/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.examples.careditor.actions;

import java.awt.Toolkit;
import org.netbeans.api.dynactions.GenericContextSensitiveAction;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.examples.careditor.pojos.Person;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class EditPersonAction extends GenericContextSensitiveAction<Person> {
    
    public EditPersonAction() {
        super (Person.class);
        init();
    }
    
    public EditPersonAction(Lookup lkp) {
        super (lkp, Person.class);
        init();
    }

    @Override
    protected void performAction(Person t) {
        AddPassengerForm detailsEditor = new AddPassengerForm(t);
        DialogDescriptor dlg = new DialogDescriptor (detailsEditor, 
                NbBundle.getMessage(EditPersonAction.class, "TTL_Edit_Person", //NOI18N
                t.getFirstName(), t.getLastName()));
        if (DialogDisplayer.getDefault().notify(dlg).equals(DialogDescriptor.OK_OPTION)) {
            try {
                //getPerson() runs the validation code
                Person nue = detailsEditor.getPerson();
                t.setFirstName(nue.getFirstName());
                t.setLastName(nue.getLastName());
                t.setAge(nue.getAge());
                //Trigger the CarDataObject to mark itself as modified
                Car car = this.lookup.lookup(Car.class);
                if (car != null) {
                    car.fireForPersonChange();
                }
            } catch (IllegalArgumentException e) {
                StatusDisplayer.getDefault().setStatusText(
                        e.getLocalizedMessage());
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private void init() {
        setDisplayName(NbBundle.getMessage(EditPersonAction.class, 
                "ACTION_Edit_Person")); //NOI18N
    }
}
