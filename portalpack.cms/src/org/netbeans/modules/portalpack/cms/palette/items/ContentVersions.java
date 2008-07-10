/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.cms.palette.items;


import org.netbeans.modules.portalpack.cms.palette.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.portalpack.cms.CMSPaletteUtilities;
import org.openide.text.ActiveEditorDrop;

/**
 *
 * @author Joshna
 */
public class ContentVersions implements ActiveEditorDrop {
    
   
    public ContentVersions() {
    }
    
    
    private String createBody() {
        
       
        String contentVersionsTag = " <cms:getContentVersions contentName=\"\" contentTypeuuid=\"\" var=\"\" />";
               
        return contentVersionsTag;
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

