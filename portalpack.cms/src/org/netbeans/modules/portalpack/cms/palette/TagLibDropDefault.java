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
/*
 * DropDefault.java
 *
 * Created on March 8, 2007, 10:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.cms.palette;

import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.portalpack.commons.palette.jsp.AbstractActiveEditorDrop;
import org.netbeans.modules.portalpack.commons.palette.jsp.PaletteUtilities;

/**
 *
 * @author Satya
 */
public abstract class TagLibDropDefault extends AbstractActiveEditorDrop{

    @Override
    public void preHandleTransfer(JTextComponent targetComponent, Map map) {
        try {
            String wfs = "<%@ taglib uri=\"http://java.sun.com/cms\" prefix=\"cms\"%>";
            PaletteUtilities.insertLibraryDefinition(wfs, targetComponent);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
            //do nothing.
        }
    }

    @Override
    public void postHandleTransfer(JTextComponent targetComponent, Map map) {
        super.postHandleTransfer(targetComponent, map);
    }

    @Override
    public String getTemplateFolder() {
        return "cms";
    } 
    
}
