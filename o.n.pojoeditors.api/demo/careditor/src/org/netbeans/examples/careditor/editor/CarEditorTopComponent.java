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
 * 
 * Contributor(s): Tom Wheeler, Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.examples.careditor.editor;

import java.awt.Component;
import org.netbeans.examples.careditor.file.CarDataObject;
import org.netbeans.examples.careditor.pojos.Car;
import org.netbeans.pojoeditors.api.EditorFactory.Kind;
import org.netbeans.pojoeditors.api.PojoEditor;
import org.openide.util.Utilities;

public class CarEditorTopComponent extends PojoEditor<Car> {
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/examples/careditor/editor/car.gif";
    CarEditorForm form;
    public CarEditorTopComponent(CarDataObject obj) {
        super (obj, Kind.OPEN);
        init();
    }

    @Override
    protected void pojoChanged(Car source, String propertyName, Object oldValue, Object newValue) {
        //The DataObject is listening to our Car, and calls us back if the
        //car has changed (for example, a cloned editor has changed a property)
        form.externalChanged(source, propertyName, oldValue, newValue);
    }

    @Override
    protected void onClose() {
        if (form != null) {
            removeAll();
        }
    }

    @Override
    protected void onSet(Car pojo) {
        form.set(pojo);
    }
    
    @Override
    protected Component createEditorUI(Car pojo) {
        form = new CarEditorForm ();
        return form;
    }

    private void init() {
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    @Override
    protected Component getInitialFocusComponent() {
        //Will be null if invoked before createEditorUI is called
        return form == null ? null : form.isDisplayable() ?
            form.getInitialFocusComponent() : null;
    }
}
