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
public class AuthContext implements ActiveEditorDrop {
    
   
    public AuthContext() {
    }
    
    
    private String createBody() {
        
       
        String authContextTag = "<%--Give a renderRequest object in portlet and request object webapp --%> \n <cms:authContext reqObj=\"\" />";
       
               
        return authContextTag;
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

