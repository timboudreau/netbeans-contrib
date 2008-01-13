/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.examples.careditor.actions;

import java.util.Collection;
import org.netbeans.api.dynactions.ObjectLoaderAction;
import org.netbeans.api.objectloader.ObjectLoader;
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
public final class AddPassengerAction extends ObjectLoaderAction<Car> {
    public AddPassengerAction () {
        super (Car.class);
        setDisplayName(NbBundle.getMessage(AddPassengerAction.class, 
                "ADD_PASSENGER")); //NOI18N
    }
    
    public AddPassengerAction(Lookup lkp) {
        super (lkp, Car.class);
    }

    @Override
    protected void performed(Car t) {
        AddPassengerForm form = new AddPassengerForm();
        DialogDescriptor dlg = new DialogDescriptor (form, 
                NbBundle.getMessage(AddPassengerAction.class, 
                "TTL_ADD_PASSENGER")); //NOI18N
        if (DialogDisplayer.getDefault().notify(dlg).equals(DialogDescriptor.OK_OPTION)) {
            try {
                Person p = form.getPerson();
                t.addPassenger(p);
            } catch (IllegalArgumentException e) {
                StatusDisplayer.getDefault().setStatusText(e.getMessage());
            }
        }
    }

    @Override
    protected boolean checkEnabled(Collection<? extends ObjectLoader> coll, Class clazz) {
        return super.checkEnabled(coll, clazz);
    }
    
    @Override
    protected String getLoadingMessage() {
        return NbBundle.getMessage(AddPassengerAction.class, "LOADING_CAR"); //NOI18N
    }
}
