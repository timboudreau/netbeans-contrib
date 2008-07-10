/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.cms.palette.items;


import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.portalpack.cms.CMSPaletteUtilities;
import org.openide.text.ActiveEditorDrop;

/**
 *
 * @author Joshna
 */
public class Category implements ActiveEditorDrop {
    
   
    public Category() {
    }
    
    
    private String createBody() {
        
       
        String categoryTag = "<%--Give category name or category uuid --%> \n <cms:getCategory categoryName=\"\" categoryuuid=\"\" var=\"\" />";
       
               
        return categoryTag;
    }
    
    public boolean handleTransfer(JTextComponent targetComponent) {
       
       
       
        String body = createBody();
        try {
            CMSPaletteUtilities.insert(body, targetComponent);
        } catch (BadLocationException ble) {
            return false;
        }
       return true;
    }
 
    
}

