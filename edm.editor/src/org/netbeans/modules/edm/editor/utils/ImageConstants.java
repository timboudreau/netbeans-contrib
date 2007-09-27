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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.edm.editor.utils;

import java.awt.Image;
import org.openide.util.Utilities;

/**
 *
 * @author karthikeyan s
 */
public interface ImageConstants {
    
    /* CONSTANTS USED FOR GETTING IMAGE ICONS */
    public static final String CONDITION = "CONDITION";
    
    public static final String COLUMN = "COLUMN";
    
    public static final String PROPERTIES = "PROPERTIES";
    
    public static final String FILTER = "FILTER";
    
    public static final String RUNTIMEATTR = "RUNTIMEATTR";
    
    public static final String PRIMARYKEYCOL = "PRIMARYKEYCOL";
    
    public static final String FOREIGNKEYCOL = "FOREIGNKEYCOL";
    
    public static final String FOREIGNKEY = "FOREIGNKEY";
    
    public static final String JOIN = "JOIN";
    
    public static final String TABLE = "TABLE";
    
    public static final String RUNTIMEINPUT = "RUNTIMEINPUT";
    
    public static final String RUNTIMEOUTPUT = "RUNTIMEOUTPUT";
    
    public static final String LAYOUT = "LAYOUT";
    
    public static final String COLLAPSEALL = "COLLAPSEALL";
    
    public static final String EDITCONNECTION = "EDITCONNECTION";
    
    public static final String EDITJOIN = "EDITJOIN";
    
    public static final String ADDTABLE = "ADDTABLE";
    
    public static final String JOINCONDITION = "JOINCONDITION";
    
    public static final String EXPANDALL = "EXPANDALL";
    
    public static final String FITTOHEIGHT = "FITTOHEIGHT";
    
    public static final String FITTOPAGE = "FITTOPAGE";
    
    public static final String FITTOWIDTH = "FITTOWIDTH";
    
    public static final String REMOVE = "REMOVE";
    
    public static final String OUTPUT = "OUTPUT";
    
    public static final String SHOW_SQL = "SHOW_SQL";
    
    public static final String RUN = "RUN";
    
    public static final String ZOOMIN = "ZOOMIN";
    
    public static final String ZOOMOUT = "ZOOMOUT";
    
    public static final String VALIDATE = "VALIDATE";
    
    public static final String GROUPBY = "GROUPBY";
    
    public static final String AUTOMAP = "AUTOMAP";
    
    /* Image objects */
    public static final Image JOIN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/join_view.png"); // NOI18N
    
    public static final Image TABLE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/SourceTable.png"); // NOI18N
    
    public static final Image COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/column.gif"); // NOI18N
    
    public static final Image PRIMARY_COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnPrimary.gif"); // NOI18N
    
    public static final Image FOREIGN_COLUMN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnForeign.gif"); // NOI18N
    
    public static final Image CONDITION_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/condition.png"); // NOI18N
    
    public static final Image PROPERTIES_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/properties.png"); // NOI18N
    
    public static final Image FILTER_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/filter16.gif"); // NOI18N
    
    public static final Image RUNTIME_INPUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/RuntimeInput.png"); // NOI18N
    
    public static final Image RUNTIME_OUTPUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/RuntimeOutput.png"); // NOI18N
    
    public static final Image RUNTIME_ATTR_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/columnselection.png"); // NOI18N
    
    public static final Image FOREIGN_KEY_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/foreignKey.gif"); // NOI18N
    
    public static final Image LAYOUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/layout.png"); // NOI18N
    
    public static final Image COLLAPSE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/collapse_all.png"); // NOI18N
    
    public static final Image EDIT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/DatabaseProperties.png"); // NOI18N
    
    public static final Image EDITJOIN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/edit_join.png"); // NOI18N
    
    public static final Image ADDTABLE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/join_view.png");// NOI18N
    
    public static final Image JOINCONDITION_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/system_condition.png"); // NOI18N
    
    public static final Image EXPAND_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/expand_all.png"); // NOI18N
    
    public static final Image FITTOHEIGHT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/fit_height.png"); // NOI18N
    
    public static final Image FITTOPAGE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/fit_diagram.png"); // NOI18N
    
    public static final Image FITTOWIDTH_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/fit_width.png"); // NOI18N
    
    public static final Image REMOVE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/remove.png"); // NOI18N
    
    public static final Image OUTPUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/showOutput.png"); // NOI18N
    
    public static final Image SQL_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/Show_Sql.png"); // NOI18N
    
    public static final Image RUN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/run.png"); // NOI18N
    
    public static final Image ZOOM_IN_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/zoom_in.png"); // NOI18N
    
    public static final Image ZOOM_OUT_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/zoom_out.png"); // NOI18N
    
    public static final Image VALIDATE_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/validation.png"); // NOI18N        
    
    public static final Image GROUPBY_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/groupby.gif"); // NOI18N       
    
    public static final Image AUTOMAP_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/AutoMapToTarget.png"); // NOI18N
}