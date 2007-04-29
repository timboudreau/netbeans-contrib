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

package org.netbeans.modules.languages.ejs.editor;



import org.netbeans.modules.editor.html.HTMLKit;
import org.netbeans.modules.languages.ejs.lexer.api.EJSTokenId;


/**
 * Editor kit implementation for EJS content type
 *
 * @author Marek Fukala
 * @author Ludovic Champenois
 * @version 1.00
 */

public class EJSKit extends HTMLKit {
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx(EJSKit.class);
    }
    
    static final long serialVersionUID =-1381945567613910297L;
        
    public EJSKit(){
        super(EJSTokenId.MIME_TYPE);
    }
    
    public String getContentType() {
        return EJSTokenId.MIME_TYPE;
    }
    
    public Object clone() {
        return new EJSKit();
    }
   
    
}

