/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.examples.careditor.file;

import org.netbeans.examples.careditor.pojos.Person;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tim Boudreau
 */
class PersonNode extends AbstractNode {
    private final Person person;
    public PersonNode(Person person) {
        super (Children.LEAF, Lookups.singleton(person));
        this.person = person;
        setDisplayName (NbBundle.getMessage(PersonNode.class, "CONCAT_NAME", 
                person.getFirstName(), person.getLastName()));
    }
}
