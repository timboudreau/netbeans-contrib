/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
/*
 * SimpleWizard.java
 *
 * Created on February 22, 2005, 2:33 PM
 */

package org.netbeans.spi.wizard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.wizard.Wizard.WizardListener;

/**
 * A simple implementation of Wizard for use in wizards which have a
 * straightforward set of steps with no branching.  To use, implement the
 * simplified interface SimpleWizard.Info and pass that to the constructor.
 *
 * @see SimpleWizard.Info
 * @author Tim Boudreau
 */
final class SimpleWizard implements Wizard {
    final SimpleWizardInfo info;
    private final List listeners = new ArrayList(1);
    private final Map ids2panels = new HashMap();

    private String currID = null;
    
    public SimpleWizard (WizardPanelProvider prov) {
        this (new SimpleWizardInfo (prov));
    }
    
    /** Creates a new instance of SimpleWizard */
    public SimpleWizard(SimpleWizardInfo info) {
        this.info = info;
        info.setWizard (this);
    }

    public void addWizardListener(WizardListener listener) {
        listeners.add (listener);
    }
    
    public void removeWizardListener(WizardListener listener) {
        listeners.remove (listener);
    }    

    public boolean canFinish() {
        return info.canFinish() || 
            (info.isValid() && currentIndex() == info.getSteps().length - 1);
    }

    public String[] getAllSteps() {
        String[] result = new String[info.getSteps().length];
        //Defensive copy
        System.arraycopy(info.getSteps(), 0, result, 0, info.getSteps().length);
        return result;
    }

    public String getStepDescription(String id) {
        int idx = Arrays.asList(info.getSteps()).indexOf (id);
        if (idx == -1) {
            throw new IllegalArgumentException ("Undefined id: " + id);
        }
        return info.getDescriptions()[idx];
    }
    
    public JComponent navigatingTo(String id, Map settings) {
//        assert SwingUtilities.isEventDispatchThread();
        assert Arrays.asList (info.getSteps()).contains(id);
        JComponent result = (JComponent) ids2panels.get(id);
        currID = id;
        if (result == null) {
            result = info.createPanel(id, settings);
            ids2panels.put (id, result);
        } else {
            info.update();
            info.recycleExistingPanel(id, settings, result);
        }
        return result;
    }

    public String getNextStep() {
        if (!info.isValid()) {
            return null;
        }
            
        int idx = currentIndex();
        if (idx < info.getSteps().length - 1) {
            return info.getSteps() [idx + 1];
        } else {
            return null;
        }
    }

    public String getPreviousStep() {
        int idx = currentIndex();
        if (idx < info.getSteps().length && idx > 0) {
            return info.getSteps() [idx - 1];
        } else {
            return null;
        }
    }
    
    int currentIndex() {
        int idx = 0;
        if (currID != null) {
            idx = Arrays.asList(info.getSteps()).indexOf (currID);
        }
        return idx;
    }
    
    void fireNavigability() {
        for (Iterator i=listeners.iterator(); i.hasNext();) {
            WizardListener l = (WizardListener) i.next();
            l.navigabilityChanged(this);
        }
    }
    
    public Object finish(Map settings) throws WizardException {
        return info.finish(settings);
    }
    
    public String getTitle() {
        return info.getTitle();
    }
    
    public String getProblem() {
        return info.getProblem();
    }
    
    public int hashCode() {
        return info.hashCode() ^ 17;
    }
    
    public boolean equals (Object o) {
        if (o instanceof SimpleWizard) {
            return ((SimpleWizard) o).info.equals (info);
        } else {
            return false;
        }
    }
    
    public String toString() {
        return "SimpleWizard for " + info;
    }
}
