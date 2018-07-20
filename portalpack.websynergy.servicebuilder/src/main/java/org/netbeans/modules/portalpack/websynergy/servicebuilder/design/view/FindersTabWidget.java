/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view;

import java.awt.Image;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.FindersWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.TabWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets.TableWidget;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.helper.ServiceBuilderHelper;

/**
 *
 * @author satyaranjan
 */
public class FindersTabWidget implements TabWidget,ListSelectionListener{
    protected FindersWidget findersWidget;
    protected ObjectScene scene;
    protected TableWidget entityTable;
    protected ServiceBuilderHelper helper;
    
    
    public FindersTabWidget(ObjectScene scene,TableWidget entityTable,ServiceBuilderHelper helper) {
        this.scene = scene;
        this.entityTable = entityTable;
        this.helper = helper;
        this.entityTable.addSelectionChangeListener(this);
    }
    
    public String getTitle() {
        return "Finders";
    }

    public Image getIcon() {
        return null;
    }

    public Widget getComponentWidget() {
        if(findersWidget == null) {
            findersWidget = new FindersWidget(scene,null,helper);
        }
        return findersWidget;
    }

    public Object hashKey() {
        return hashKey();
    }

    public void valueChanged(ListSelectionEvent e) {
        
        int i = e.getFirstIndex();
        if (i == -1) {
            return;
        }
        
        Entity entity = ((Entity) entityTable.getSelectedObject());
        if(entity == null)
            return;
        
        update((Entity)entity.clone());
        
    }
    
    private void update(Entity entity){    
        
        findersWidget.reload(entity);
        //findersWidget = new OperationsWidget(scene, serviceModel);
        //findersWidget.revalidate();
    }

}
