/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
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
