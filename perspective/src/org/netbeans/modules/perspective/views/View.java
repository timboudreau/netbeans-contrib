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
 * View.java
 * 
 * Created on Jul 23, 2007, 9:16:19 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.perspective.views;

import java.io.Serializable;

/**
 *
 * @author Anuradha G
 */
public class View implements Serializable{
    private static final long serialVersionUID = 1l;
    private String topcomponentID;
    private String mode;
    private boolean open;
    public View(String topcomponentID, String mode) {
        this.topcomponentID = topcomponentID;
        this.mode = mode;
    }

    public View(String topcomponentID, String mode, boolean open) {
        this.topcomponentID = topcomponentID;
        this.mode = mode;
        this.open = open;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTopcomponentID() {
        return topcomponentID;
    }

    public void setTopcomponentID(String topcomponentID) {
        this.topcomponentID = topcomponentID;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
    
    
}
