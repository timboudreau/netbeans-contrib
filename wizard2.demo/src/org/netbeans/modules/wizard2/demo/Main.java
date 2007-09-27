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
package org.netbeans.modules.wizard2.demo;

import java.util.Map;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/**
 * Demo entry point.  Assembles a wizard from a set of classes and shows it.
 *
 * @author Tim Boudreau
 */
public class Main {
    public static void main(String[] ignored) {
        //All we do here is assemble the list of WizardPage subclasses we
        //want to show:
        Class[] pages = new Class[] {
            AnimalTypePage.class,
            LocomotionPage.class,
            OtherAttributesPage.class,
            FinalPage.class
        };
        
        //Use the utility method to compose a Wizard
        Wizard wizard = WizardPage.createWizard(pages, WizardResultProducer.NO_OP);
        
        //And show it onscreen
        WizardDisplayer.showWizard (wizard);
        System.exit(0);
    }
    
    public static Wizard createWizard() {
        Class[] pages = new Class[] {
            AnimalTypePage.class,
            LocomotionPage.class,
            OtherAttributesPage.class,
            FinalPage.class
        };
        
        //Use the utility method to compose a Wizard
        Wizard wizard = WizardPage.createWizard(pages, WizardResultProducer.NO_OP);
        return wizard;
    }
    
    private static WizardResultProducer producer = new WRP();
    private static final class WRP implements WizardResultProducer {
        public Object finish(Map arg0) throws WizardException {
            return new Object();
        }

        public boolean cancel(Map arg0) {
            return true;
        }
    }
    
}
