/*  The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.
    You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.
    When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package wizarddemo;

import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import wizarddemo.panels.SpeciesPanel;


/**
 * Defines the first two panes of the wizard.  The second one is where the
 * user decides what comes next.
 *
 * @author Timothy Boudreau
 */
 class InitialSteps extends WizardPanelProvider {
    private static final String ANIMAL_LOVER = "animalLover";
    private static final String WHICH_ANIMAL = "whichAnimal";
    private static final String STEP_0_PROBLEM = "Only animal lovers can complete this wizard";

    /**
     * Creates a new instance of InitialSteps
     */
    InitialSteps () {
        super( "New Pet Wizard", new String[] { ANIMAL_LOVER, WHICH_ANIMAL },
            new String[] { "Select basic preferences", "Choose a species" } );
    }

    protected JComponent createPanel (final WizardController controller,
        final String id, final Map data) {
        
        switch ( indexOfStep( id ) ) {
            
            case 0 :

                JPanel result = new JPanel(  );
                result.setLayout( new BorderLayout(  ) );

                final JCheckBox checkbox = new JCheckBox( "I am an animal lover" );
                
                checkbox.addActionListener( new ActionListener(  ) {
                        public void actionPerformed( ActionEvent ae ) {
                            if ( checkbox.isSelected(  ) ) {
                                controller.setProblem( null );
                            } else {
                                controller.setProblem( STEP_0_PROBLEM );
                            }
                        }
                    } );

                result.add ( checkbox );

                controller.setProblem( STEP_0_PROBLEM );
                return result;
                
            case 1 :
                return new SpeciesPanel ( controller, data );

            default :
                throw new IllegalArgumentException ( id );
        }
    }
}
