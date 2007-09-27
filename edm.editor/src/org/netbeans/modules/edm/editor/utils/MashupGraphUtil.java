/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */

package org.netbeans.modules.edm.editor.utils;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.modules.sql.framework.model.SQLConstants;

/**
 *
 * @author karthikeyan s
 */
public class MashupGraphUtil {

    private static Map<String, Image> imageMap = new HashMap<String, Image>();
    
    static {
        imageMap.put(ImageConstants.COLUMN, ImageConstants.COLUMN_IMAGE);
        imageMap.put(ImageConstants.CONDITION, ImageConstants.CONDITION_IMAGE);
        imageMap.put(ImageConstants.PROPERTIES, ImageConstants.PROPERTIES_IMAGE);
        imageMap.put(ImageConstants.FILTER, ImageConstants.FILTER_IMAGE);
        imageMap.put(ImageConstants.RUNTIMEATTR, ImageConstants.RUNTIME_ATTR_IMAGE);
        imageMap.put(ImageConstants.PRIMARYKEYCOL, ImageConstants.PRIMARY_COLUMN_IMAGE);
        imageMap.put(ImageConstants.FOREIGNKEYCOL, ImageConstants.FOREIGN_COLUMN_IMAGE);
        imageMap.put(ImageConstants.FOREIGNKEY, ImageConstants.FOREIGN_KEY_IMAGE);
        imageMap.put(ImageConstants.JOIN, ImageConstants.JOIN_IMAGE);
        imageMap.put(ImageConstants.RUNTIMEINPUT, ImageConstants.RUNTIME_INPUT_IMAGE);
        imageMap.put(ImageConstants.RUNTIMEOUTPUT, ImageConstants.RUNTIME_OUTPUT_IMAGE);
        imageMap.put(ImageConstants.TABLE, ImageConstants.TABLE_IMAGE);
        imageMap.put(ImageConstants.LAYOUT, ImageConstants.LAYOUT_IMAGE);
        imageMap.put(ImageConstants.COLLAPSEALL, ImageConstants.COLLAPSE_IMAGE);
        imageMap.put(ImageConstants.EDITCONNECTION, ImageConstants.EDIT_IMAGE);
        imageMap.put(ImageConstants.EDITJOIN, ImageConstants.EDITJOIN_IMAGE);
        imageMap.put(ImageConstants.ADDTABLE,ImageConstants.ADDTABLE_IMAGE);
        imageMap.put(ImageConstants.JOINCONDITION, ImageConstants.JOINCONDITION_IMAGE);
        imageMap.put(ImageConstants.EXPANDALL, ImageConstants.EXPAND_IMAGE);
        imageMap.put(ImageConstants.FITTOHEIGHT, ImageConstants.FITTOHEIGHT_IMAGE);
        imageMap.put(ImageConstants.FITTOPAGE, ImageConstants.FITTOPAGE_IMAGE);
        imageMap.put(ImageConstants.FITTOWIDTH, ImageConstants.FITTOWIDTH_IMAGE);
        imageMap.put(ImageConstants.REMOVE, ImageConstants.REMOVE_IMAGE);
        imageMap.put(ImageConstants.OUTPUT, ImageConstants.OUTPUT_IMAGE);
        imageMap.put(ImageConstants.SHOW_SQL, ImageConstants.SQL_IMAGE);
        imageMap.put(ImageConstants.RUN, ImageConstants.RUN_IMAGE);
        imageMap.put(ImageConstants.ZOOMIN, ImageConstants.ZOOM_IN_IMAGE);
        imageMap.put(ImageConstants.ZOOMOUT, ImageConstants.ZOOM_OUT_IMAGE);
        imageMap.put(ImageConstants.VALIDATE, ImageConstants.VALIDATE_IMAGE);
        imageMap.put(ImageConstants.GROUPBY, ImageConstants.GROUPBY_IMAGE);
        imageMap.put(ImageConstants.AUTOMAP,ImageConstants.AUTOMAP_IMAGE);
    }
    
    /** Creates a new instance of MashupGraphUtil */
    private MashupGraphUtil() {
    }
    
    public static Image getImageForObject(int type) {
        switch(type) {
        case SQLConstants.JOIN:
            return imageMap.get(ImageConstants.JOIN);
        case SQLConstants.RUNTIME_INPUT:
            return imageMap.get(ImageConstants.RUNTIMEINPUT);
        case SQLConstants.SOURCE_TABLE:
        case SQLConstants.JOIN_TABLE:
            return imageMap.get(ImageConstants.TABLE);
        }
        return null;
    }
    
    public static Image getImage(String imageName) {
        return imageMap.get(imageName);
    }
}