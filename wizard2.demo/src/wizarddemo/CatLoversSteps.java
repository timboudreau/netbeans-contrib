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

import java.util.Map;
import javax.swing.JComponent;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPanelProvider;
import wizarddemo.panels.CatBreedPanel;
import wizarddemo.panels.CatHairLengthPanel;

/**
 *
 * @author Timothy Boudreau
 */
public class CatLoversSteps extends WizardPanelProvider {
    
    /** Creates a new instance of CatLoversSteps */
    public CatLoversSteps() {
        super (
            new String[] { "hairLength", "breed" }, 
            new String[] { "Select hair length", "Choose breed" });
    }
    
    protected JComponent createPanel(WizardController controller, String id, Map settings) {
        switch (indexOfStep(id)) {
            case 0 :
                return new CatHairLengthPanel (controller, settings);
            case 1 :
                return new CatBreedPanel (controller, settings);
            default :
                throw new IllegalArgumentException (id);
        }
    }
    
}
