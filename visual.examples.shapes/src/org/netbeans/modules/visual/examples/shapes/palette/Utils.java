/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visual.examples.shapes.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.modules.visual.examples.shapes.dataobject.MyItemData;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

/**
 *
 * @author Geert Wielenga
 */
public class Utils {
    
    public static final DataFlavor MY_DATA_FLAVOR = new DataFlavor( MyItemData.class, "My Item Data" );
    private static PaletteController thePalette;
    public static String MODE;
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    public static PaletteController getPalette() {
        //create the palette
        if( null == thePalette ) {
            try {
                //DND start
                //use custom DragAndDropHandler when creating the palette so that our custom
                //dataflavor gets added when an item is being dragged from the palette
                thePalette = PaletteFactory.createPalette( "NotePalette", new MyActions(), null, new MyDnDHandler() );
                //DND end
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return thePalette;
    }
    
    private static class MyActions extends PaletteActions {
        public Action[] getImportActions() {
            return null;
        }
        
        public Action[] getCustomPaletteActions() {
            return null;
        }
        
        public Action[] getCustomCategoryActions(Lookup lookup) {
            return null;
        }
        
        public Action[] getCustomItemActions(Lookup lookup) {
            return null;
        }
        
        public Action getPreferredAction(Lookup lookup) {
            return null;
        }
        
    }
    
    //DND start
    private static class MyDnDHandler extends DragAndDropHandler {
        public void customize(ExTransferable exTransferable, Lookup lookup) {
            //check if MyItemData is availble in the lookup
            final MyItemData data = (MyItemData)lookup.lookup( MyItemData.class );
            if( null != data ) {
                //add our flavor to the list of dragged flavors because our dropPanel does not support anything else
                exTransferable.put( new ExTransferable.Single( MY_DATA_FLAVOR ) {
                    protected Object getData() throws IOException, UnsupportedFlavorException {
                        return data;
                    }
                });
            }
            
        }
    }
    //DND end
}
