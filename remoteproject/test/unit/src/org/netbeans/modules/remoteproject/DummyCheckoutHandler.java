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
 * Portions Copyright 1997-2007 Sun Microsystems, Inc. All Rights Reserved.
 */
/*
 * DummyCheckoutHandler.java
 *
 * Created on March 5, 2007, 3:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.remoteproject;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.remoteproject.CheckoutHandler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
public class DummyCheckoutHandler implements CheckoutHandler {
    
    /** Creates a new instance of DummyCheckoutHandler */
    public DummyCheckoutHandler() {
    }
    
    public boolean canCheckout(FileObject template) {
        assert template != null;
        return "vcs".equals(template.getAttribute("vcs"));
    }

    public String checkout(FileObject template, FileObject dest,
                           ProgressHandle progress) {
        tpl = template;
        return null;
    }
    
    FileObject tpl;
    public boolean wasCheckoutCalled() {
        FileObject tpl = this.tpl;
        this.tpl = null;
        return tpl != null;
    }
}
