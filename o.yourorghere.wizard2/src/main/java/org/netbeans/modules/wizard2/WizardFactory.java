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
package org.netbeans.modules.wizard2;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.netbeans.api.wizard.*;
import org.netbeans.modules.wizard2.WrapperIterator;
import org.netbeans.spi.wizard.*;
/**
 *
 * @author Tim Boudreau
 */
public class WizardFactory {
    private WizardFactory () {}
    private static final String KEY_WIZARD = "wizard";
    public static final String KEY_TARGET_NAME = "_#targetName";
    public static final String KEY_TARGET_FOLDER = "_#targetFolder";
    public static final String KEY_TEMPLATE = "_#template";
    public static final String KEY_TEMPLATE_FOLDER = "_#templateFolder";
    
    public static WizardDescriptor.InstantiatingIterator createWrapperWizard (FileObject f) {
        Wizard wiz = (Wizard) f.getAttribute(KEY_WIZARD);
        boolean isProgress = Boolean.TRUE.equals(f.getAttribute("progress"));
        boolean isAsynch = Boolean.TRUE.equals (f.getAttribute("asynchronous"));
        String asynchValidationPanelIds = (String) f.getAttribute("asynchValidatingPanels");
        String validatingPanelIds = (String) f.getAttribute("asynchValidatingPanels");
        Set <String> asynchVPanels = new HashSet <String> ();
        if (asynchValidationPanelIds != null) {
            asynchVPanels.addAll (Arrays.asList(asynchValidationPanelIds.split(","))); //NOI18N
        }
        Set <String> vpanels = new HashSet <String> ();
        if (validatingPanelIds != null) {
            vpanels.addAll (Arrays.asList(validatingPanelIds.split(","))); //NOI18N
        }
        if (isProgress) {
            return new WrapperIterator.ProgressWrapperIterator(wiz, vpanels, asynchVPanels);
        } else if (isAsynch) {
            return new WrapperIterator.AsynchWrapperIterator(wiz, vpanels, asynchVPanels);
        } else {
            return new WrapperIterator (wiz, vpanels, asynchVPanels);
        }
    }
    
}
