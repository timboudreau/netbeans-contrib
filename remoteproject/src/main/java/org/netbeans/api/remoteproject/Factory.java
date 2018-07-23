/*
 * Factory.java
 *
 * Created on March 6, 2007, 11:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.remoteproject;

import org.netbeans.modules.remoteproject.RemoteProjectWizardIterator;
import org.openide.WizardDescriptor;

/**
 *
 * @author Tim Boudreau
 */
public final class Factory {
    private Factory() {}
    public static WizardDescriptor.ProgressInstantiatingIterator createIterator() {
        return RemoteProjectWizardIterator.createIterator();
    }
}
