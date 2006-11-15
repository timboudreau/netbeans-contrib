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
import wizarddemo.panels.DogSizePanel;
import wizarddemo.panels.DogTempermentPanel;

/**
 *
 * @author Timothy Boudreau
 */
public class DogLoversSteps extends WizardPanelProvider {
    
    /** Creates a new instance of DogLoversSteps */
    public DogLoversSteps() {
        super (new String[] { "temperment", "size" },
               new String[] { "Select Temperment", "Choose size" });
    }
    
    protected JComponent createPanel(WizardController controller, String id, Map collectedData) {
        switch (indexOfStep(id)) {
            case 0 :
                return new DogTempermentPanel (controller, collectedData);
            case 1 :
                return new DogSizePanel (controller, collectedData);
            default :
                throw new IllegalArgumentException (id);
        }
    }
    
}
