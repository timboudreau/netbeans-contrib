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
public class Content implements ActiveEditorDrop {
    
   
    public Content() {
    }
    
    
    private String createBody() {
        
       
        String contentTag = "<%--Give content name ,contentTypeuuid and version number or contentuuid   --%> \n <cms:getContent contentName=\"\" contentTypeuuid=\"\" contentuuid=\"\" version=\"\" var=\"\" />";
               
        return contentTag;
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

