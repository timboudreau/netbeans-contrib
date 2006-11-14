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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanInfo;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import javax.swing.JOptionPane;
import org.netbeans.modules.visual.examples.shapes.palette.Utils;
import org.openide.ErrorManager;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.datatransfer.ExTransferable;

public class ShapeDataNode extends DataNode {
    
    private MyItemData data;
    
    public ShapeDataNode( ShapeDataObject obj, MyItemData data ) {
        super( obj, Children.LEAF );
        this.data = data;
    }
    
    //Used in component palette and in Projects window:
    public Image getIcon(int i) {
        if( i == BeanInfo.ICON_COLOR_16x16 || i == BeanInfo.ICON_MONO_16x16 ) {
            return data.getSmallImage();
        }
        return data.getBigImage();
    }
    
    //Used in component palette and in Projects window:
    public String getDisplayName() {
        return data.getComment();
    }
    
    //DND start
    public Transferable drag() throws IOException {
        ExTransferable retValue = ExTransferable.create( super.drag() );
        //add the 'data' into the Transferable
        retValue.put( new ExTransferable.Single( Utils.MY_DATA_FLAVOR ) {
            protected Object getData() throws IOException, UnsupportedFlavorException {
                return data;
            }
        });
        return retValue;
    }
    //DND end
}
