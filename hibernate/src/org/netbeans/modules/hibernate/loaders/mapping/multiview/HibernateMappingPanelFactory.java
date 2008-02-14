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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.hibernate.loaders.mapping.multiview;

import org.netbeans.modules.hibernate.loaders.cfg.multiview.*;
import org.netbeans.modules.hibernate.loaders.cfg.*;
import org.netbeans.modules.hibernate.loaders.mapping.HibernateMappingDataObject;
import org.netbeans.modules.hibernate.mapping.model.Id;
import org.netbeans.modules.hibernate.mapping.model.MyClass;
import org.netbeans.modules.hibernate.mapping.model.Resultset;
import org.netbeans.modules.hibernate.mapping.model.Return;
import org.netbeans.modules.hibernate.mapping.model.Typedef;
import org.netbeans.modules.xml.multiview.ui.InnerPanelFactory;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.netbeans.modules.xml.multiview.ui.ToolBarDesignEditor;

/**
 * Factory for creating section panels for displaying and/or editing Hibernate configuration file
 * 
 * @author Dongmei Cao
 */
public class HibernateMappingPanelFactory implements InnerPanelFactory {

    private HibernateMappingDataObject dObj;
    private ToolBarDesignEditor editor;

    /** Creates a new instance of ServletPanelFactory */
    HibernateMappingPanelFactory(ToolBarDesignEditor editor, HibernateMappingDataObject dObj) {
        this.dObj = dObj;
        this.editor = editor;
    }

    public SectionInnerPanel createInnerPanel(Object key) {

        if (key instanceof Typedef) {
            return new TypedefPanel((SectionView) editor.getContentView(), dObj, (Typedef) key);
        } else if (key instanceof Resultset) {
            return new ReturnScalarElementsPanel((SectionView) editor.getContentView(), dObj, (Resultset)key);
        } else if (key instanceof Return) {
            return new ReturnElementPanel((SectionView) editor.getContentView(), dObj, (Return)key);
        } else if (key instanceof Id) {
            return new IdElementPanel((SectionView) editor.getContentView(), dObj, (Id)key);
        } 
        else if (key instanceof String) {
            if (((String) key).equals(HibernateMappingToolBarMVElement.META_DATA)) {
                return new MetaDataPanel((SectionView) editor.getContentView(), dObj);
            } else if (((String) key).equals(HibernateMappingToolBarMVElement.IMPORT_ELEMENT)) {
                return new ImportElementsPanel((SectionView) editor.getContentView(), dObj);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
