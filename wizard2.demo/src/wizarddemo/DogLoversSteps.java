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
