/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
