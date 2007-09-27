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

package org.netbeans.modules.helpbuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/** A wizard iterator (sequence of panels).
 * Used to create a wizard. Create one or more
 * panels from template as needed too.
 *
 * @author  Richard Gregor
 */
public class HelpBuilderWizardIterator implements WizardDescriptor.Iterator {

    // You should define what panels you want to use here:

    protected WizardDescriptor.Panel[] createPanels () {
        return new WizardDescriptor.Panel[] {            
            new ProjectSetup(),  
            new TocSetup(),
            new IndexSetup(),
            //new ProjectFinish(),
            new ProjectGenerator(),
            new HelpPreview(),
            new ProjectFinish()             
        };
    }

    // And the list of step names:

    protected String[] createSteps () {
        return new String[] {
            /* --> EDIT ME <--*/
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_1"),
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_2"),
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_3"),
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_4"),
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_5"),
            NbBundle.getMessage (HelpBuilderWizardIterator.class, "LBL_step_6")        
        };
    }

    // --- The rest probably does not need to be touched. ---

    // Keep track of the panels and selected panel:

    private transient int index = 0;
    // Also package-accessible to descriptor:
    protected final int getIndex () {
        return index;
    }
    private transient WizardDescriptor.Panel[] panels = null;
    protected final WizardDescriptor.Panel[] getPanels () {
        if (panels == null) {
            panels = createPanels ();
        }
        return panels;
    }

    // Also the list of steps in the left pane:

    private transient String[] steps = null;
    // Also package-accessible to descriptor:
    protected final String[] getSteps () {
        if (steps == null) {
            steps = createSteps ();
        }
        return steps;
    }

    // --- WizardDescriptor.Iterator METHODS: ---
    // Note that this is very similar to WizardDescriptor.Iterator, but with a
    // few more options for customization. If you e.g. want to make panels appear
    // or disappear dynamically, go ahead.

    public String name () {
        return NbBundle.getMessage(HelpBuilderWizardIterator.class, "TITLE_x_of_y",
            new Integer (index + 1), new Integer (getPanels ().length));
    }

    public boolean hasNext () {
        return index < getPanels ().length - 1;
    }
    public boolean hasPrevious () {
        return index > 0;
    }
    public void nextPanel () {
        if (! hasNext ()) throw new NoSuchElementException ();
        index++;
    }
    public void previousPanel () {
        if (! hasPrevious ()) throw new NoSuchElementException ();
        index--;
    }
    public WizardDescriptor.Panel current () {
        return getPanels ()[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener (ChangeListener l) {}
    public final void removeChangeListener (ChangeListener l) {}
    // If something changes dynamically (besides moving between panels),
    // e.g. the number of panels changes in response to user input, then
    // uncomment the following and call when needed:
    // fireChangeEvent ();
    /*
    private transient Set listeners = new HashSet (1); // Set<ChangeListener>
    public final void addChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.add (l);
        }
    }
    public final void removeChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.remove (l);
        }
    }
    protected final void fireChangeEvent () {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet (listeners).iterator ();
        }
        ChangeEvent ev = new ChangeEvent (this);
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }
    private Object readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject ();
        listeners = new HashSet (1);
        return this;
    }
    */

}
