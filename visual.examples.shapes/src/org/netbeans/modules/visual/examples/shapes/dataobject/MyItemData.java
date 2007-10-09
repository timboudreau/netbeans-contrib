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
package org.netbeans.modules.visual.examples.shapes.dataobject;

import java.awt.Image;
import java.awt.event.MouseListener;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.openide.util.Utilities;

/**
 * A 'data' holder to be dragged from the palette to the drop panel or to be dragged (moved) within the panel
 * @author sa154850
 */
public class MyItemData {
    private Properties props;

    private Image icon16;
    private Image icon32;


    //The values here MUST match the keys in the item files:
    public static final String PROP_ID = "shape-id";
    public static final String PROP_COMMENT = "shape-comment";
    public static final String PROP_ICON16 = "shape-icon16";
    public static final String PROP_ICON32 = "shape-icon32";
    
    private static boolean dropped = false;
    private static boolean removed = false;
    
    /** Creates a new instance of MyItemData */
    MyItemData( Properties props ) {
        this.props = props;
        loadIcons();
    }
    
    public String getId() {
        return props.getProperty( PROP_ID );
    }
    
    public String getComment() {
        return props.getProperty( PROP_COMMENT );
    }
    
    public Image getSmallImage() {
        return icon16;
    }
    
    public Image getBigImage() {
        return icon32;
    }
    
    public boolean equals(Object obj) {
        if( obj instanceof MyItemData ) {
            return getId().equals( ((MyItemData)obj).getId() );
        }
        return false;
    }
    
    public void loadIcons() {
        String iconId = props.getProperty( PROP_ICON16 );
        icon16 = Utilities.loadImage( iconId );
        iconId = props.getProperty( PROP_ICON32 );
        icon32 = Utilities.loadImage( iconId );
    }
  
}
